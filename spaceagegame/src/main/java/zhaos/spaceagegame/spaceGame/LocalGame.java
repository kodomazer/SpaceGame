package zhaos.spaceagegame.spaceGame;


import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Collection;
import java.util.concurrent.SynchronousQueue;

import zhaos.spaceagegame.request.MyBundle;
import zhaos.spaceagegame.request.Request;
import zhaos.spaceagegame.request.RequestConstants;
import zhaos.spaceagegame.spaceGame.entity.EntityHandler;
import zhaos.spaceagegame.spaceGame.entity.SpaceStation;
import zhaos.spaceagegame.spaceGame.map.HexTile;
import zhaos.spaceagegame.spaceGame.map.MapHandler;
import zhaos.spaceagegame.spaceGame.map.SubsectionCenter;
import zhaos.spaceagegame.util.HHexDirection;

/**
 * Created by kodomazer on 9/19/2016.
 * Local hosting of Space Game for single player/hotseat games
 */
public class LocalGame extends AsyncTask<Void,Void,Void> {
    private static final String TAG = "Local Space Game";

    //Singleton
    private static LocalGame instance;

    public static LocalGame getInstance() {
        if (instance == null) instance = new LocalGame();
        return instance;
    }

    //Ongoing task stuff
    private SynchronousQueue<Request> actionQueue;
    private boolean running;
    private Handler mainThread;

    //Facade
    private EntityHandler entityHandler;
    private MapHandler mapHandler;

    private TeamController[] teams;
    private int activeTeam;

    //init variables
    private int radius;
    private int teamCount;

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
        initializeFactionStart(2, new Point(1, 2));
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
            MyBundle infoBundle = new MyBundle();
            infoBundle.putInt(RequestConstants.INSTRUCTION,action.getInstructioin());
            boolean success = false;
            switch (action.getInstructioin()
                &    RequestConstants.ACTION_MASK) {
                case RequestConstants.GAME_ACTION:
                    success = localGameAction(action, infoBundle);
                    break;
                case RequestConstants.MAP_ACTION:
                    success = mapHandlerAction(action,infoBundle);
                    break;
                case RequestConstants.ENTITY_ACTION:
                    success = entityHandlerAction(action, infoBundle);
                    break;
            }
            actionCompleted(action.getCallback(),infoBundle,success);
        }

        return null;
    }

    private boolean localGameAction(Request action, MyBundle bundle){
        switch (action.getInstructioin()){
            case RequestConstants.GAME_INFO:
                return getGameInfo(action,bundle);
            case RequestConstants.GAME_END:
                return stopGame(action,bundle);
            case RequestConstants.END_TURN:
                return endTurn(action,bundle);
        }
        return false;
    }

    private boolean endTurn(Request action, MyBundle bundle) {
        resetPhase();
        return getGameInfo(action,bundle);
    }

    private boolean entityHandlerAction(Request action, MyBundle bundle) {
        return entityHandler.handleRequest(action,bundle);
    }

    private boolean mapHandlerAction(Request action, MyBundle bundle) {
        return mapHandler.handleRequest(action,bundle);
    }

    private boolean getGameInfo(Request action,MyBundle bundle) {
        //TODO make sure all "meta" data is represented
        bundle.putInt(RequestConstants.ACTIVE_FACTION,activeTeam);
        return true;
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

    //methods for Team Controller to give actions
    public void sendRequest(final Request gameAction) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                actionQueue.offer(gameAction);
            }
        }).run();
    }

    public boolean stopGame(Request action, MyBundle bundle) {
        running = false;
        return true;
    }

    public void actionCompleted(@NonNull final Request.RequestCallback callback,
                                final MyBundle info, boolean success) {
        info.putBoolean(RequestConstants.SUCCESS, success);
        Log.i(TAG, "actionCompleted: Instruction: "
                + Integer.toHexString(info.getInt(RequestConstants.INSTRUCTION))
                +"\tSucess:"
                + success);
        mainThread.post(new Runnable() {
            @Override
            public void run() {
                callback.onComplete(info);
            }
        });
    }

    //Phases
    private void resetPhase() {
        mapHandler.reset();
        entityHandler.reset();
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
                entityHandler.newSpaceStation(
                        faction,
                        (SubsectionCenter)start.getSubsection(HHexDirection.CENTER)));
        SpaceStation city =
                ((SubsectionCenter) start.getSubsection(HHexDirection.CENTER))
                        .getCity();
        entityHandler.newUnit(city);
        entityHandler.newUnit(city);

    }
}
