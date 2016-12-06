package zhaos.spaceagegame.spaceGame;


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

import zhaos.spaceagegame.spaceGame.entity.EntityHandler;
import zhaos.spaceagegame.spaceGame.entity.SpaceStation;
import zhaos.spaceagegame.spaceGame.entity.Unit;
import zhaos.spaceagegame.spaceGame.map.HexTile;
import zhaos.spaceagegame.spaceGame.map.MapHandler;
import zhaos.spaceagegame.spaceGame.map.Subsection;
import zhaos.spaceagegame.spaceGame.map.SubsectionCenter;
import zhaos.spaceagegame.util.MyBundle;
import zhaos.spaceagegame.util.Request;
import zhaos.spaceagegame.util.RequestConstants;
import zhaos.spaceagegame.ui.SpaceGameActivity;
import zhaos.spaceagegame.util.HHexDirection;

/**
 * Created by kodomazer on 9/19/2016.
 * Local hosting of Space Game for single player/hotseat games
 */
public class LocalGame extends AsyncTask<Void,Void,Void> {
    private static final String TAG = "Local Space Game";
    //Singleton
    private static LocalGame instance;
    private boolean running;
    private SynchronousQueue<Request> actionQueue;
    private final Request.RequestCallback emptyCallback = new Request.RequestCallback() {
        @Override
        public void onComplete(MyBundle info) {
            //do Nothing
        }
    };
    private Unit[] selected = new Unit[3];

    public static LocalGame getInstance() {
        if (instance == null) instance = new LocalGame();
        return instance;
    }

    private Handler mainThread;

    private EntityHandler entityHandler;
    private MapHandler mapHandler;


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


    private LocalGame() {
        //Initialize
        gamePhase = GamePhase.uninitialized;
        initializeDirections();

        entityHandler = new EntityHandler();
        mapHandler = new MapHandler();

        radius = -1;
        teamCount = -1;

        actionQueue = new SynchronousQueue<>(true);

        teams = null;
    }

    @Override
    protected Void doInBackground(Void... params) {
        //testing init
        initializeFactionStart(1,new Point(2,1));
        //end testing

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
                case RequestConstants.HEX_INFO:
                    getHexInfo(action);
                    break;
                case RequestConstants.SUBSECTION_INFO:
                    getSubsectionInfo(action);
                    break;
                case RequestConstants.UNIT_INFO:
                    getUnitInfo(action);
                    break;
                case RequestConstants.UNIT_MOVE:
                    moveUnit(action);
                    break;
                case RequestConstants.UNIT_SELECT:
                    unitSelect(action);
                    break;
                case RequestConstants.UNIT_ATTACK:
                    unitAttack(action);
                    break;

                default:
                    //do nothing

            }
        }

        return null;
    }



    private void unitAttack(Request action) {
        MyBundle bundle = action.getThisRequest();
        Request.RequestCallback callback = action.getCallback();
        if (callback == null) callback = emptyCallback;

        Point hexPos = bundle.getPoint(RequestConstants.DESTINATION_HEX);
        HHexDirection subsectionDir = bundle
                .getSubsection(RequestConstants.DESTINATION_SUBSECTION);
        HexTile hex = getHex(hexPos);
        Subsection subsection = hex.getSubsection(subsectionDir);

        Unit unit = getUnit(bundle.getInt(RequestConstants.UNIT_ID));
        selected[2] = unit;
        subsection.attack(selected);


        actionCompleted(callback, bundle, true);

    }

    private void unitSelect(Request action) {
        MyBundle bundle = action.getThisRequest();
        Request.RequestCallback callback = action.getCallback();
        if (callback == null) callback = emptyCallback;

        Unit currentUnit = getUnit(bundle.getInt(RequestConstants.UNIT_ID));
        if(currentUnit==null){
            actionCompleted(callback, bundle, false);
            return;
        }
        if(selected[0]!=null) {
            if (selected[0].getHexTile() != currentUnit.getHexTile()) {
                for (int i = 0; i < 2; i++)
                    selected[i] = null;
            }
            if (selected[0].getSubsection() != currentUnit.getSubsection()) {
                for (int i = 0; i < 2; i++)
                    selected[i] = null;
            }
        }


        for(int i = 0; i<3;i++) {
            if (selected[i] == null) {
                selected[i] = currentUnit;
                break;
            }
        }

        actionCompleted(callback, bundle, true);
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
        Subsection[] valid = moves();
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


        for (Subsection section : valid) {
            if (section.getParentPosition() == destinationHex) {
                if (section.getPosition() == destinationSubsection) {
                    if (section.getAffiliation() == faction || section.getAffiliation() == 0) {
                        getHex(originHex).getSubsection(originSubsection).moveOut(unit);
                        section.moveIn(unit);
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
        Unit selectedUnit = getUnit(unitID);
        if (selectedUnit == null) {
            callback.onComplete(null);
            return;
        }

        boolean isSelected = false;
        for (Unit aSelected : selected) {
            if (aSelected == selectedUnit) {
                isSelected = true;
                break;
            }
        }
        info.putInt(RequestConstants.UNIT_STATUS_FLAGS,
                (isSelected?RequestConstants.SELECTED:0)|
                        //TODO actual checks for movement and battle
                        (selectedUnit.canMove()?RequestConstants.MOVEABLE|
                        RequestConstants.CAN_ATTACK:0));

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

        HexTile hex = getHex(position);
        if (hex == null) {
            callback.onComplete(null);
            return;
        }

        ArrayList<MyBundle> subsectionList = new ArrayList<>();

        //Build Bundles for each subsection and then adds it to a list
        for (Subsection subsection : hex.getSubsections()) {
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
        Subsection subsection = getHex(position).getSubsection(direction);

        if(direction == HHexDirection.CENTER){
            MyBundle cityInfo = new MyBundle();
            SpaceStation city = ((SubsectionCenter)subsection).getCity();
            if(city!=null){
                cityInfo.putInt(RequestConstants.SPACE_STATION_ID,city.getID());
                cityInfo.putInt(RequestConstants.LEVEL,city.getLevel());
                bundle.putBundle(RequestConstants.SPACE_STATION_INFO,cityInfo);
            }
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


    private Unit getUnit(int unitID) {
        return entityHandler.getUnit(unitID);
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
                mapHandler.initializeMap(radius);
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

    public HexTile getHex(Point position) {
        return mapHandler.getHex(position);
    }

    public Collection<HexTile> getTiles() {
        return mapHandler.getMap();
    }

    private void selectUnit(Unit e) {
        activeUnit = e;
    }

    private Subsection[] moves() {
        ArrayList<Subsection> a = new ArrayList<>();
        int team = activeUnit.getAffiliation();
        int tile;
        for (Subsection s : activeUnit.getSubsection().getNeighbors()) {
            tile = s.getAffiliation();
            if (tile == team || tile == 0)
                a.add(s);
        }
        return (Subsection[]) a.toArray();
    }

    public Subsection[] attacks() {
        ArrayList<Subsection> a = new ArrayList<>();
        int team = activeUnit.getAffiliation();
        int tile;
        for (Subsection s : activeUnit.getSubsection().getNeighbors()) {
            tile = s.getAffiliation();
            if (tile == team || tile == 0)
                continue;
            a.add(s);
        }
        return (Subsection[]) a.toArray();
    }

    public Unit newUnit(SpaceStation station){
        return entityHandler.newUnit(station);
    }

    public SpaceStation registerSpaceStation(int faction,HexTile hexTile){
        return entityHandler.newSpaceStation(faction,hexTile);
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
        for (HexTile h : getTiles()) {
            for (Subsection s : h.getSubsections()) {
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



    private void initializeFactionStart(int faction,Point startingHex){
        HexTile start = getHex(startingHex);
        if(start == null) return;
        start.placeCity(registerSpaceStation(faction,start));
        SpaceStation city =
        ((SubsectionCenter)start.getSubsection(HHexDirection.CENTER))
                .getCity();
        city.createUnit();
        city.createUnit();
        city.createUnit();
        city.upgrade();
        city.upgrade();

    }

}
