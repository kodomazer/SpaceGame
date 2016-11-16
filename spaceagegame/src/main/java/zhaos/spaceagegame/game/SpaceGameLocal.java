package zhaos.spaceagegame.game;


import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.ArrayMap;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.SynchronousQueue;

import zhaos.spaceagegame.util.MyBundle;
import zhaos.spaceagegame.util.Request;
import zhaos.spaceagegame.util.RequestConstants;
import zhaos.spaceagegame.ui.SpaceGameActivity;
import zhaos.spaceagegame.util.HHexDirection;

/**
 * Created by kodomazer on 9/19/2016.
 * Local hosting of Space Game for single player/hotseat games
 */
public class SpaceGameLocal extends AsyncTask<Void,Void,Void> {
    private static final String TAG = "Local Space Game";
    //Singleton
    private static SpaceGameLocal instance;
    private boolean running;
    private SynchronousQueue<Request> actionQueue;
    private final Request.RequestCallback emptyCallback = new Request.RequestCallback() {
        @Override
        public void onComplete(MyBundle info) {
            //do Nothing
        }
    };

    public static SpaceGameLocal getInstance() {
        if (instance == null) instance = new SpaceGameLocal();
        return instance;
    }

    private Handler mainThread;

    private Map<Integer, Unit> unitList;

    private int lastPodID;
    private Map<Integer, SpaceGameConstructionPod> podList;

    private int lastCityID;
    private Map<Integer, SpaceStation> cityList;

    private Map<Point, SpaceGameHexTile> tileMap;
    private TeamController[] teams;

    private int radius;
    private int teamCount;

    private Unit activeUnit;

    protected SpaceGameActivity view;


    /*Game Phases
    -1: Initializing, no actions for anyone
    0: Reset, automated
    1: Production:
        Move Energy to Space Stations
        Use Energy and Space Station Actions to
            make Units and Construction Pods
    2: Move:
        Units have 3 actions
        Units within 6 subsections of another unit cannot move
        1 action per move (subsection to subsection)
            can bring (1) construction pod during move
            can bring space station of up to twice the Unit's level
                Space Station must be deconstructed
        6 actions to build a Space Station from a construction pod
        3 actions and 1 construction pod per current level to
            upgrade a Space Station
    3-5: Combat moves - Taken if a unit was in another faction's zone of influence
        Units get 1 action per turn
        Movement into a subsection with enemies executes combat
     */
    private GamePhase gamePhase;

    private enum GamePhase {
        uninitialized,
        start,
        reset,
        production,
        move,
        combat1,
        combat2,
        combat3;
    }


    private SpaceGameLocal() {
        //Initialize
        gamePhase = GamePhase.uninitialized;
        initializeDirections();


        radius = -1;
        teamCount = -1;

        actionQueue = new SynchronousQueue<>(true);

        unitList = new ArrayMap<>();
        lastCityID = 1;
        cityList = new ArrayMap<>();
        lastPodID = 1;
        podList = new ArrayMap<>();

        tileMap = new HashMap<>();
        teams = null;
    }

    @Override
    protected Void doInBackground(Void... params) {
        //testing init
        SpaceGameHexTile hex = tileMap.get(new Point(2,1));
        SpaceGameCenterSubsection subsection =
                (SpaceGameCenterSubsection)hex.getSubsection(HHexDirection.CENTER);
        SpaceStation city = new SpaceStation(1,hex,lastCityID);
        subsection.buildCity(city);
        cityList.put(lastCityID,city);
        lastCityID+=1;
        Unit unit = new Unit(subsection.getCity());
        unitList.put(unit.getID(), unit);

        running = true;
        while (true) {
            if (!running) break;
            Log.i(TAG, "doInBackground: running");
            Request action;
            try {
                action = actionQueue.take();
            } catch (InterruptedException i) {
                continue;
            }
            MyBundle requestBundle = action.getThisRequest();
            switch (requestBundle.getInt(RequestConstants.INSTRUCTION)) {
                case RequestConstants.GAME_INFO:
                    getGameInfo(action);
                    break;
                case RequestConstants.UNIT_INFO:
                    getUnitInfo(action);
                    break;
                case RequestConstants.HEX_INFO:
                    getHexInfo(action);
                    break;
                case RequestConstants.SUBSECTION_INFO:
                    getSubsectionInfo(action);
                    break;
                case RequestConstants.UNIT_MOVE:
                    moveUnit(action);
                    break;
                default:
                    //do nothing

            }
        }

        return null;
    }



    private void moveUnit(Request action) {
        MyBundle bundle = action.getThisRequest();
        Request.RequestCallback callback = action.getCallback();
        if (callback == null) callback = emptyCallback;

        int faction = bundle.getInt(RequestConstants.FACTION_ID);
        int unitID = bundle.getInt(RequestConstants.UNIT_ID);
        Unit unit = getUnit(unitID);
        if (unit.getAffiliation() != faction) return;

        selectUnit(unit);
        SpaceGameHexSubsection[] valid = moves();
        Point destinationHex = bundle.getPoint(RequestConstants.DESTINATION_HEX);
        HHexDirection destinationSubsection
                = bundle.getSubsection(RequestConstants.DESTINATION_SUBSECTION);

        Point originHex = bundle.getPoint(RequestConstants.ORIGIN_HEX);
        HHexDirection originSubsection = bundle.getSubsection(RequestConstants.ORIGIN_SUBSECTION);

        if (unit.getHexTile().getPosition() != originHex) {
            actionCompleted(callback, bundle, false);
            return;
        }
        if (unit.getSubsection().getPosition() != originSubsection) {
            actionCompleted(callback, bundle, false);
            return;
        }


        for (SpaceGameHexSubsection section : valid) {
            if (section.getParentPosition() == destinationHex) {
                if (section.getPosition() == destinationSubsection) {
                    if (section.getAffiliation() == faction || section.getAffiliation() == 0) {
                        getHex(originHex).getSubsection(originSubsection).moveOut(unit);
                        section.moveIn(unit);
                        unit.combatResetPhase();
                        break;
                    }
                }
            }
        }

        actionCompleted(callback, bundle, true);
    }

    private void getUnitInfo(Request action) {
        MyBundle info = new MyBundle();
        Request.RequestCallback callback = action.getCallback();
        if (callback == null) return;

        MyBundle bundle = action.getThisRequest();
        int unitID = bundle.getInt(RequestConstants.UNIT_ID);
        Unit selectedUnit = unitList.get(unitID);
        if (selectedUnit == null) {
            callback.onComplete(null);
            return;
        }

        info.putPoint(RequestConstants.ORIGIN_HEX, selectedUnit.getHexTile().getPosition());
        info.putInt(RequestConstants.LEVEL, selectedUnit.getLevel());
        info.putInt(RequestConstants.FACTION_ID, selectedUnit.getAffiliation());
        info.putInt(RequestConstants.UNIT_ID, unitID);
        actionCompleted(callback, info, true);
    }

    private void getGameInfo(Request action) {

    }

    private void getHexInfo(Request action) {
        MyBundle info = new MyBundle();
        Request.RequestCallback callback = action.getCallback();
        if (callback == null) return;

        MyBundle bundle = action.getThisRequest();
        Point position = bundle.getPoint(RequestConstants.ORIGIN_HEX);

        SpaceGameHexTile hex = getHex(position);
        if (hex == null) {
            callback.onComplete(null);
            return;
        }

        ArrayList<MyBundle> subsectionList = new ArrayList<>();

        //Build Bundles for each subsection and then adds it to a list
        for (SpaceGameHexSubsection subsection : hex.getSubsections()) {
            MyBundle subInfo = new MyBundle();
            subInfo.putPoint(RequestConstants.ORIGIN_HEX, subsection.getParentPosition());
            subInfo.putSubsection(RequestConstants.ORIGIN_SUBSECTION, subsection.getPosition());
            subInfo.putInt(RequestConstants.FACTION_ID, subsection.getAffiliation());
            subsectionList.add(subInfo);
        }
        info.putArrayList(RequestConstants.SUBSECTION_LIST, subsectionList);

        actionCompleted(callback, info, true);
    }


    private void getSubsectionInfo(Request action) {
        Request.RequestCallback callback = action.getCallback();
        if (callback == null) return;

        MyBundle bundle = action.getThisRequest();
        Point position = bundle.getPoint(RequestConstants.ORIGIN_HEX);
        HHexDirection direction = bundle.getSubsection(RequestConstants.ORIGIN_SUBSECTION);
        SpaceGameHexSubsection subsection = getHex(position).getSubsection(direction);

        if(direction == HHexDirection.CENTER){
            MyBundle cityInfo = new MyBundle();
            SpaceStation city = ((SpaceGameCenterSubsection)subsection).getCity();
            cityInfo.putInt(RequestConstants.LEVEL,city.getLevel());
            bundle.putBundle(RequestConstants.SPACE_STATION_INFO,cityInfo);
        }

        //Handle Units
        Unit[] units = subsection.getUnits();
        ArrayList<MyBundle> unitList = new ArrayList<>(units.length);
        for(Unit unit: units){
            MyBundle unitInfo = new MyBundle();
            unitInfo.putInt(RequestConstants.LEVEL,unit.getLevel());
            unitInfo.putInt(RequestConstants.UNIT_ID,unit.getID());
            unitList.add(unitInfo);
        }
        bundle.putArrayList(RequestConstants.UNIT_LIST,unitList);

        //callback
        actionCompleted(callback, bundle, true);
    }

    private SpaceGameHexTile getHex(Point position) {
        return tileMap.get(position);
    }

    private Unit getUnit(int unitID) {
        return unitList.get(unitID);
    }

    //returns the radius of the game
    public int getRadius() {
        return radius;
    }

    //returns true if radius is set, returns false if radius has already been set
    public boolean setRadius(int radius) {
        if (radius > 0)
            if (this.radius == -1) {
                this.radius = radius;
                initializeMap(radius);
                return true;
            }
        return false;
    }

    public void setHandler(Handler mainHandler) {
        mainThread = mainHandler;
    }

    public boolean setTeamCount(int teamCount) {
        if (teamCount > 1)
            if (this.teamCount == -1) {
                this.teamCount = teamCount;
                initializeTeams(teamCount);
                return true;
            }
        return false;
    }


    private void initializeTeams(int teamCount) {
        teams = new TeamController[teamCount];
        for (int i = 0; i < teamCount; i++)
            teams[i] = new TeamController();

    }


    public SpaceGameHexTile getTile(Point position) {
        return tileMap.get(position);
    }

    public Collection<SpaceGameHexTile> getTiles() {
        return tileMap.values();
    }

    private void selectUnit(Unit e) {
        activeUnit = e;
    }

    private SpaceGameHexSubsection[] moves() {
        ArrayList<SpaceGameHexSubsection> a = new ArrayList<>();
        int team = activeUnit.getAffiliation();
        int tile;
        for (SpaceGameHexSubsection s : activeUnit.getSubsection().getNeighbors()) {
            tile = s.getAffiliation();
            if (tile == team || tile == 0)
                a.add(s);
        }
        return (SpaceGameHexSubsection[]) a.toArray();
    }

    public SpaceGameHexSubsection[] attacks() {
        ArrayList<SpaceGameHexSubsection> a = new ArrayList<>();
        int team = activeUnit.getAffiliation();
        int tile;
        for (SpaceGameHexSubsection s : activeUnit.getSubsection().getNeighbors()) {
            tile = s.getAffiliation();
            if (tile == team || tile == 0)
                continue;
            a.add(s);
        }
        return (SpaceGameHexSubsection[]) a.toArray();
    }


    //methods for Team Controller to give actions
    public void sendRequest(Request gameAction) {
        //Not sure if this necessarily is thread safe or not
        actionQueue.offer(gameAction);
    }

    public void stopGame(){
        running = false;
    }

    private void actionCompleted(final Request.RequestCallback callback,
                                 final MyBundle info, boolean success) {
        info.putBoolean(RequestConstants.SUCCESS, success);
        mainThread.post(new Runnable() {
            @Override
            public void run() {
                callback.onComplete(info);
            }
        });
    }


    //Phases
    protected void resetPhase() {
        for (SpaceGameHexTile h : tileMap.values()) {
            for (SpaceGameHexSubsection s : h.getSubsections()) {
                s.resetInfo();
            }
        }
        calculateAreaOfInfluence();
    }


    //Auxiliary methods for internal use
    private void calculateAreaOfInfluence() {
        for (TeamController t : teams) {
            for (Unit u : t.getUnits()) {
                u.getSubsection().updateInfluence(6, u.getAffiliation());
            }
        }
    }


    //Initialize HHexDirection enum translation methods
    private void initializeDirections() {
        HHexDirection.Up.setTranslate(new HHexDirection.TranslateInterface() {
            @Override
            public void translatePoint(Point translated) {
                translated.y -= 1;
            }
        });
        HHexDirection.UpRight.setTranslate(new HHexDirection.TranslateInterface() {
            @Override
            public void translatePoint(Point translated) {
                if (translated.x % 2 == 1) {
                    translated.y -= 1;
                }
                translated.x += 1;
            }
        });
        HHexDirection.DownRight.setTranslate(new HHexDirection.TranslateInterface() {
            @Override
            public void translatePoint(Point translated) {
                if (translated.x % 2 == 0) {
                    translated.y += 1;
                }
                translated.x += 1;
            }
        });

        HHexDirection.Down.setTranslate(new HHexDirection.TranslateInterface() {
            @Override
            public void translatePoint(Point translated) {
                translated.y += 1;
            }
        });

        HHexDirection.DownLeft.setTranslate(new HHexDirection.TranslateInterface() {
            @Override
            public void translatePoint(Point translated) {
                if (translated.x % 2 == 0) {
                    translated.y += 1;
                }
                translated.x -= 1;
            }
        });

        HHexDirection.UpLeft.setTranslate(new HHexDirection.TranslateInterface() {
            @Override
            public void translatePoint(Point translated) {
                if (translated.x % 2 == 1) {
                    translated.y -= 1;
                }
                translated.x -= 1;
            }
        });
    }

    //Initialize map
    private void initializeMap(int radius) {
        tileMap = new HashMap<>();
        //center is at (radius*2,radius)
        Point current = new Point(radius, radius);
        HHexDirection facing = HHexDirection.DownLeft;

        //Generate map ring by ring, going outward
        for (int i = 1; i <= radius; i++) {
            //Move up once to move out one ring
            HHexDirection.Up.translatePoint(current);
            //Go each direction once, switch the direction of travel each loop
            for (int j = 0; j < 6; j++, facing = HHexDirection.rotateClockwise(facing))
                //Translate in the direction a number of times equal to the radius
                //Radius is equal to side length
                for (int k = 0; k < i; k++, facing.translatePoint(current))
                    //Add a new hex tile to the tileMap
                    //Deep copy of the Point because the current point will change
                    tileMap.put(new Point(current),
                            new SpaceGameHexTile(this, current));
        }
    }

    private void initializeFactionStart(int faction,Point startingHex){
        tileMap.get(startingHex).placeCity(faction,3);

    }

}
