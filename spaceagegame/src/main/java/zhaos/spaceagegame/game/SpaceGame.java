package zhaos.spaceagegame.game;


import android.graphics.Point;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import zhaos.spaceagegame.ui.SpaceGameActivity;
import zhaos.spaceagegame.util.HHexDirection;
import zhaos.spaceagegame.util.IntPoint;

/**
 * Created by kodomazer on 9/19/2016.
 */
public class SpaceGame{
    //Singleton
    protected static SpaceGame instance;

    public static SpaceGame getInstance(){
        if(instance==null)instance = new SpaceGame();
        return instance;
    }


    protected Map<IntPoint,SpaceGameHexTile> grid;
    protected TeamController[] teams;

    private int radius;
    private int teamCount;

    protected Unit activeUnit;

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
    protected int gamePhase;



    private SpaceGame() {
        //Initialize
        gamePhase = -1;
        initializeDirections();

        radius = -1;
        teamCount = -1;

        grid = new HashMap<>();
        teams=null;
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


    void initializeTeams(int teamCount){
        teams = new TeamController[teamCount];
        for(int i = 0;i<teamCount;i++)
            teams[i] = new TeamController();

    }

    public SpaceGameHexTile getTile(IntPoint position) {
        return grid.get(position);
    }

    public SpaceGameHexTile getTile(Point position) {
        return grid.get(new IntPoint(position.x,position.y));
    }

    public Collection<SpaceGameHexTile> getTiles(){
        return grid.values();
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






    //Phases
    protected void resetPhase(){
        for(SpaceGameHexTile h:grid.values()){
            for(SpaceGameHexSubsection s : h.getSubsections()){
                s.resetInfo();
            }
        }
        calculateAreaOfInfluence();
    }


    //Auxillary methods for internal use
    private void calculateAreaOfInfluence() {
        for (TeamController t : teams) {
            for (Unit u : t.getUnits()) {
                grid.get(u.getHexTile())
                        .getSubsection(u.getSubsection())
                        .updateInfluence(6, u.getAffiliation());
            }
        }
    }


    //Initialize HHexDirection enum translation methods
    private void initializeDirections() {
        HHexDirection.Up.setTranslate(new IntPoint.translateInterface() {
            @Override
            public void translatePoint(IntPoint translated) {
                translated.y-=1;
            }
        });
        HHexDirection.UpRight.setTranslate(new IntPoint.translateInterface() {
            @Override
            public void translatePoint(IntPoint translated) {
                if(translated.x%2==1){
                    translated.y-=1;
                }
                translated.x+=1;
            }
        });
        HHexDirection.DownRight.setTranslate(new IntPoint.translateInterface() {
            @Override
            public void translatePoint(IntPoint translated) {
                if(translated.x%2==0){
                    translated.y+=1;
                }
                translated.x+=1;
            }
        });

        HHexDirection.Down.setTranslate(new IntPoint.translateInterface() {
            @Override
            public void translatePoint(IntPoint translated) {
                translated.y+=1;
            }
        });

        HHexDirection.DownLeft.setTranslate(new IntPoint.translateInterface() {
            @Override
            public void translatePoint(IntPoint translated) {
                if(translated.x%2==0){
                    translated.y+=1;
                }
                translated.x-=1;
            }
        });

        HHexDirection.UpLeft.setTranslate(new IntPoint.translateInterface() {
            @Override
            public void translatePoint(IntPoint translated) {
                if(translated.x%2==1){
                    translated.y-=1;
                }
                translated.x-=1;
            }
        });
    }

    //Initialize map
    private void initializeMap(int radius) {
        grid = new HashMap<>();
        //center is at (radius*2,radius)
        IntPoint current = new IntPoint(radius,radius);
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
                    //Add a new hex tile to the grid
                    //Deep copy of the Point because the current point will change
                    grid.put(new IntPoint(current),
                            new SpaceGameHexTile(this,current));
        }
    }


}
