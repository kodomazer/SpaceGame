package zhaos.spaceagegame.game;


import android.graphics.Point;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.SynchronousQueue;

import zhaos.spaceagegame.game.resources.InfoBundle;
import zhaos.spaceagegame.game.resources.MyBundle;
import zhaos.spaceagegame.game.resources.Request;
import zhaos.spaceagegame.game.resources.RequestConstants;
import zhaos.spaceagegame.ui.SpaceGameActivity;
import zhaos.spaceagegame.util.HHexDirection;
import zhaos.spaceagegame.util.IntPoint;

/**
 * Created by kodomazer on 9/19/2016.
 */
public class SpaceGameLocal extends AsyncTask<Void,Void,Void>{
    private static final String TAG = "Local Space Game";
    //Singleton
    private static SpaceGameLocal instance;
    private boolean running;
    private final Object action= new Object();
    private SynchronousQueue<Request> actionQueue;

    public static SpaceGameLocal getInstance(){
        if(instance==null)instance = new SpaceGameLocal();
        return instance;
    }


    private int lastUnitID;
    private Map<Integer,Unit> unitList;

    private int lastPodID;
    private Map<Integer,SpaceGameConstructionPod> podList;

    private int lastCityID;
    private Map<Integer,SpaceStation> cityList;

    private Map<Point,SpaceGameHexTile> tileMap;
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

    private enum GamePhase{
        uninitialized;

    }


    private SpaceGameLocal() {
        //Initialize
        gamePhase = GamePhase.uninitialized;
        initializeDirections();

        radius = -1;
        teamCount = -1;

        actionQueue = new SynchronousQueue<>(true);

        tileMap = new HashMap<>();
        teams=null;
    }

    @Override
    protected Void doInBackground(Void... params) {
        running = true;
        while(true) {
            if (!running) break;
            Log.i(TAG, "doInBackground: running");
            Request action;
            try {
                 action = actionQueue.take();
            }
            catch (InterruptedException i){
                continue;
            }
            MyBundle requestBundle = action.getThisRequest();
            switch (requestBundle.getInt(RequestConstants.INSTRUCTION)) {
                case RequestConstants.UNIT_INFO:
                    getUnitInfo(action);
                    break;
                case RequestConstants.HEX_INFO:
                    getHexInfo(action);
                    break;
                default:
                    //do nothing

            }
        }

        return null;
    }

    private void getUnitInfo(Request action) {
        InfoBundle info = new InfoBundle();
        Request.RequestCallback callback = action.getCallback();
        if (callback == null) return;

        MyBundle bundle = action.getThisRequest();
        int unitID = bundle.getInt(RequestConstants.UNIT_ID);
        Unit selectedUnit = unitList.get(unitID);
        if (selectedUnit == null) {
            callback.onComplete(null);
            return;
        }

        info.putPoint(RequestConstants.ORIGIN_HEX,selectedUnit.getHexTile());
        info.putInt(RequestConstants.LEVEL,selectedUnit.getLevel());
        info.putInt(RequestConstants.FACTION_ID,selectedUnit.getAffiliation());
        callback.onComplete(info);
    }

    private void getHexInfo(Request action){
        InfoBundle info = new InfoBundle();
        Request.RequestCallback callback = action.getCallback();
        if (callback == null) return;

        MyBundle bundle = action.getThisRequest();
        Point position = bundle.getPoint(RequestConstants.ORIGIN_HEX);

        SpaceGameHexTile hex = getHex(position);
        if(hex==null){
            callback.onComplete(null);
            return;}

        ArrayList<MyBundle> subsectionList = new ArrayList<>();
        for(SpaceGameHexSubsection subsection:hex.getSubsections()){
            MyBundle subInfo = new MyBundle();
            subInfo.putSubsection(RequestConstants.ORIGIN_SUBSECTION,subsection.getPosition());
            subInfo.putInt(RequestConstants.FACTION_ID,subsection.getAffiliation());
            subsectionList.add(subInfo);
        }
        info.putArrayList(RequestConstants.SUBSECTION_LIST,subsectionList);

        callback.onComplete(info);
    }

    private SpaceGameHexTile getHex(Point position) {
        return tileMap.get(position);
    }

    //returns the radius of the game
    public int getRadius(){
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

    public boolean setTeamCount(int teamCount) {
        if (teamCount > 1)
            if (this.teamCount == -1) {
                this.teamCount = teamCount;
                initializeTeams(teamCount);
                return true;
            }
        return false;
    }


    private void initializeTeams(int teamCount){
        teams = new TeamController[teamCount];
        for(int i = 0;i<teamCount;i++)
            teams[i] = new TeamController();

    }

    public SpaceGameHexTile getTile(IntPoint position) {
        return tileMap.get(new Point(position.x,position.y));
    }

    public SpaceGameHexTile getTile(Point position) {
        return tileMap.get(position);
    }

    public Collection<SpaceGameHexTile> getTiles(){
        return tileMap.values();
    }

    public void selectUnit(Unit e){
        activeUnit = e;
    }

    public SpaceGameHexSubsection[] moves() {
        ArrayList<SpaceGameHexSubsection> a = new ArrayList<>();
        int team = activeUnit.getAffiliation();
        int tile;
        for (SpaceGameHexSubsection s : getTile(activeUnit.getHexTile())//get Tile
                .getSubsection(activeUnit.getSubsection())//get subsection
                .getNeighbors()){
            tile = s.getAffiliation();
            if( tile == team || tile == 0)
                a.add(s);
        }
        return (SpaceGameHexSubsection[]) a.toArray();
    }

    public SpaceGameHexSubsection[] attacks() {
        ArrayList<SpaceGameHexSubsection> a = new ArrayList<>();
        int team = activeUnit.getAffiliation();
        int tile;
        for (SpaceGameHexSubsection s : getTile(activeUnit.getHexTile())//get Tile
                .getSubsection(activeUnit.getSubsection())//get subsection
                .getNeighbors()) {
            tile = s.getAffiliation();
            if (tile == team || tile == 0)
                continue;
            a.add(s);
        }
        return (SpaceGameHexSubsection[]) a.toArray();
    }



    //methods for Team Controller to give actions
    public void doAction(Request gameAction){
        //Not sure if this necessarily is thread safe or not
        actionQueue.offer(gameAction);
    }


    //Phases
    protected void resetPhase(){
        for(SpaceGameHexTile h: tileMap.values()){
            for(SpaceGameHexSubsection s : h.getSubsections()){
                s.resetInfo();
            }
        }
        calculateAreaOfInfluence();
    }


    //Auxiliary methods for internal use
    private void calculateAreaOfInfluence() {
        for (TeamController t : teams) {
            for (Unit u : t.getUnits()) {
                tileMap.get(u.getHexTile())
                        .getSubsection(u.getSubsection())
                        .updateInfluence(6, u.getAffiliation());
            }
        }
    }


    //Initialize HHexDirection enum translation methods
    private void initializeDirections() {
        HHexDirection.Up.setTranslate(new IntPoint.translateInterface() {
            @Override
            public void translatePoint(Point translated) {
                translated.y-=1;
            }
        });
        HHexDirection.UpRight.setTranslate(new IntPoint.translateInterface() {
            @Override
            public void translatePoint(Point translated) {
                if(translated.x%2==1){
                    translated.y-=1;
                }
                translated.x+=1;
            }
        });
        HHexDirection.DownRight.setTranslate(new IntPoint.translateInterface() {
            @Override
            public void translatePoint(Point translated) {
                if(translated.x%2==0){
                    translated.y+=1;
                }
                translated.x+=1;
            }
        });

        HHexDirection.Down.setTranslate(new IntPoint.translateInterface() {
            @Override
            public void translatePoint(Point translated) {
                translated.y+=1;
            }
        });

        HHexDirection.DownLeft.setTranslate(new IntPoint.translateInterface() {
            @Override
            public void translatePoint(Point translated) {
                if(translated.x%2==0){
                    translated.y+=1;
                }
                translated.x-=1;
            }
        });

        HHexDirection.UpLeft.setTranslate(new IntPoint.translateInterface() {
            @Override
            public void translatePoint(Point translated) {
                if(translated.x%2==1){
                    translated.y-=1;
                }
                translated.x-=1;
            }
        });
    }

    //Initialize map
    private void initializeMap(int radius) {
        tileMap = new HashMap<>();
        //center is at (radius*2,radius)
        Point current = new Point(radius,radius);
        HHexDirection facing = HHexDirection.DownLeft;

        //Generate map ring by ring, going outward
        for(int i = 1;i<=radius;i++) {
            //Move up once to move out one ring
            HHexDirection.Up.translatePoint(current);
            //Go each direction once, switch the direction of travel each loop
            for(int j = 0;j<6;j++,facing = HHexDirection.rotateClockwise(facing))
                //Translate in the direction a number of times equal to the radius
                //Radius is equal to side length
                for(int k = 0;k<i;k++,facing.translatePoint(current))
                    //Add a new hex tile to the tileMap
                    //Deep copy of the Point because the current point will change
                    tileMap.put(new Point(current),
                            new SpaceGameHexTile(this,current));
        }
    }


}
