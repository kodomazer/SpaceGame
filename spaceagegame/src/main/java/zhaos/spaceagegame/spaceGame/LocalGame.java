package zhaos.spaceagegame.spaceGame;


import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.SynchronousQueue;

import zhaos.spaceagegame.request.helperRequest.HexInfoRequest;
import zhaos.spaceagegame.request.helperRequest.SubsectionInfoBase;
import zhaos.spaceagegame.request.helperRequest.UnitInfoRequest;
import zhaos.spaceagegame.spaceGame.entity.EntityHandler;
import zhaos.spaceagegame.spaceGame.entity.SpaceStation;
import zhaos.spaceagegame.spaceGame.entity.Unit;
import zhaos.spaceagegame.spaceGame.map.HexTile;
import zhaos.spaceagegame.spaceGame.map.MapHandler;
import zhaos.spaceagegame.spaceGame.map.Subsection;
import zhaos.spaceagegame.spaceGame.map.SubsectionCenter;
import zhaos.spaceagegame.request.MyBundle;
import zhaos.spaceagegame.request.Request;
import zhaos.spaceagegame.request.RequestConstants;
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
    private Unit[] selected = new Unit[3];

    public static LocalGame getInstance() {
        if (instance == null) instance = new LocalGame();
        return instance;
    }

    public static EntityHandler getEntityHandler() {
        if (instance == null) instance = new LocalGame();
        return instance.entityHandler;
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
        initializeFactionStart(1, new Point(2, 1));
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
            action.getThisRequest();
            switch (action.getInstructioin()
                    ) {
//                &    RequestConstants.ACTION_MASK) {
//                case RequestConstants.GAME_ACTION:
//                    handleAction(action);
//                case RequestConstants.MAP_ACTION:
//                    mapHandler.handleAction(action);
//                    break;
//                case RequestConstants.ENTITY_ACTION:
//                    entityHandler.handleAction(action);
//                    break;
//                Deprecating code for now.  Refactoring for the facade design pattern
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
            resetPhase();
        }

        return null;
    }

    //move this into the Entity Handler
    //Or move most of the code calling
    private void getUnitInfo(Request action) {
        if (action.getInstructioin() != RequestConstants.UNIT_INFO) {
            actionCompleted(action.getCallback(), null, false);
            return;
        }
        MyBundle infoBundle = new MyBundle();
        UnitInfoRequest infoRequest = (UnitInfoRequest) action;
        Unit unit = entityHandler.getUnit(infoRequest.getUnitID());

        if (unit == null) {
            actionCompleted(action.getCallback(), null, false);
            return;
        }
        unit.getInfo(infoBundle);
        actionCompleted(action.getCallback(), infoBundle, true);
    }

    private void getSubsectionInfo(Request action) {
        if (action.getInstructioin() != RequestConstants.SUBSECTION_INFO) {
            actionCompleted(action.getCallback(), null, false);
            return;
        }
        SubsectionInfoBase infoRequest = (SubsectionInfoBase) action;
        MyBundle subsectionInfo = new MyBundle();
        HexTile tile = mapHandler.getHex(infoRequest.getHex());
        if (tile == null) {
            actionCompleted(action.getCallback(), subsectionInfo, false);
            return;
        }
        tile.getSubsectionInfo(infoRequest, subsectionInfo);

        actionCompleted(action.getCallback(),
                subsectionInfo,
                true);
    }

    private void getHexInfo(Request action) {
        if (action.getInstructioin() != RequestConstants.HEX_INFO) {
            actionCompleted(action.getCallback(), null, false);
            return;
        }
        HexInfoRequest infoRequest = (HexInfoRequest) action;
        MyBundle hexInfo = new MyBundle();
        HexTile tile = mapHandler.getHex(infoRequest.getHex());
        if (tile == null) {
            actionCompleted(action.getCallback(), hexInfo, false);
            return;
        }
        tile.getInfo(hexInfo);

        actionCompleted(action.getCallback(),
                hexInfo,
                true);

    }

    private void unitAttack(Request action) {
        MyBundle bundle = action.getThisRequest();
        Request.RequestCallback callback = action.getCallback();

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

        Unit currentUnit = getUnit(bundle.getInt(RequestConstants.UNIT_ID));
        if (currentUnit == null) {
            actionCompleted(callback, bundle, false);
            return;
        }
        if (selected[0] != null) {
            if (!selected[0].getSubsection().equals(currentUnit.getSubsection())) {
                for (int i = 0; i < 2; i++)
                    selected[i] = null;
            }
        }
        for (int i = 0; i < 3; i++) {
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

//        int faction = bundle.getInt(RequestConstants.FACTION_ID);
        int unitID = bundle.getInt(RequestConstants.UNIT_ID);
        Unit unit = getUnit(unitID);
//        if (unit.getTeam() != faction) return;

        selectUnit(unit);
        Subsection[] valid = moves();
        Point destinationHex = bundle.getPoint(RequestConstants.DESTINATION_HEX);
        HHexDirection destinationSubsection
                = bundle.getSubsection(RequestConstants.DESTINATION_SUBSECTION);

//        Point originHex = bundle.getPoint(RequestConstants.ORIGIN_HEX);
//        HHexDirection originSubsection = bundle.getSubsection(RequestConstants.ORIGIN_SUBSECTION);
//
//        if (unit.getHexTile().getPosition() != originHex) {
//            actionCompleted(callback, bundle, false);
//            return;
//        }
//        if (unit.getSubsection().getPosition() != originSubsection) {
//            actionCompleted(callback, bundle, false);
//            return;
//        }

        for (Subsection section : valid) {
            if (section == null) continue;
            Log.i(TAG, "moveUnit: " + section.getParentPosition() + " " + destinationHex);
            if (section.getParentPosition().equals(destinationHex)) {
                Log.i(TAG, "moveUnit: " + section.getPosition() + " " + destinationSubsection);
                if (section.getPosition().i() == destinationSubsection.i()) {
                    //            if (section.getTeam() == faction || section.getTeam() == 0)
                    section.moveIn(unit);
                    break;
                    //          }
                }
            }
        }
        actionCompleted(callback, bundle, true);
    }


    private void getGameInfo(Request action) {

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
        ArrayList<Subsection> a = new ArrayList<Subsection>();
        int team = activeUnit.getTeam();
        int tile;
        for (Subsection s : activeUnit.getSubsection().getNeighbors()) {
            if (s == null) continue;
            tile = s.getAffiliation();
            if (tile == team || tile == -1) {
                a.add(s);
                Log.i(TAG, "moves: " + team + " " + tile);
            }
        }
        return a.toArray(new Subsection[6]);
    }

    private SpaceStation registerSpaceStation(int faction, SubsectionCenter subsection) {

        return entityHandler.newSpaceStation(faction, subsection);
    }

    //methods for Team Controller to give actions
    public void sendRequest(final Request gameAction) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //Not sure if this necessarily is thread safe or not
                actionQueue.offer(gameAction);
            }
        }).run();
    }

    public void stopGame() {
        running = false;
    }

    public void actionCompleted(final Request.RequestCallback callback,
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
    private void resetPhase() {
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
                u.getSubsection().updateInfluence(6, u.getTeam());
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

    private void initializeFactionStart(int faction, Point startingHex) {
        HexTile start = getHex(startingHex);
        if (start == null) return;
        start.placeCity(
                registerSpaceStation(
                        faction,
                        (SubsectionCenter)start.getSubsection(HHexDirection.CENTER)));
        SpaceStation city =
                ((SubsectionCenter) start.getSubsection(HHexDirection.CENTER))
                        .getCity();
        entityHandler.newUnit(city);
        entityHandler.newUnit(city);

    }
}
