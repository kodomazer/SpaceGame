package zhaos.spaceagegame.game;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import zhaos.spaceagegame.ui.GameGUIActivity;
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


    protected Map<IntPoint,GameHexTile> grid;
    protected TeamController[] teams;

    private int radius;
    private int teamCount;

    protected Unit activeUnit;

    protected GameGUIActivity view;


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

    //returns true if radius is set, returns false if radius has already been set
    public boolean setSize(int size) {
        if (size > 0)
            if (this.radius == -1) {
                this.radius = size;
                initializeMap(size);
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

    public GameHexTile getTile(IntPoint position) {
        return grid.get(position);
    }

    public Collection<GameHexTile> getTiles(){
        return grid.values();
    }

    public void selectUnit(Unit e){
        activeUnit = e;
    }

    public HexSubsection[] moves() {
        ArrayList<HexSubsection> a = new ArrayList<>();
        int team = activeUnit.getAffiliation();
        int tile;
        for (HexSubsection s : getTile(activeUnit.getHexTile())//get Tile
                .getSubsection(activeUnit.getSubsection())//get subsection
                .getNeighbors()){
            tile = s.getAffiliation();
            if( tile == team || tile == 0)
                a.add(s);
        }
        return (HexSubsection[]) a.toArray();
    }

    public HexSubsection[] attacks() {
        ArrayList<HexSubsection> a = new ArrayList<>();
        int team = activeUnit.getAffiliation();
        int tile;
        for (HexSubsection s : getTile(activeUnit.getHexTile())//get Tile
                .getSubsection(activeUnit.getSubsection())//get subsection
                .getNeighbors()) {
            tile = s.getAffiliation();
            if (tile == team || tile == 0)
                continue;
            a.add(s);
        }
        return (HexSubsection[]) a.toArray();
    }

    protected void resetPhase(){
        for(GameHexTile h:grid.values()){
            for(HexSubsection s : h.getSubsections()){
                s.resetInfo();
            }
        }
        calculateAreaOfInfluence();
    }

    private void calculateAreaOfInfluence() {
        for (TeamController t : teams) {
            for (Unit u : t.getUnits()) {
                grid.get(u.getHexTile())
                        .getSubsection(u.getSubsection())
                        .updateInfluence(6, u.getAffiliation());
            }
        }
    }

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

    private void initializeMap(int radius) {
        grid = new HashMap<>();
        //center is at (radius*2,radius)
        IntPoint current = new IntPoint(radius,radius);
        HHexDirection facing = HHexDirection.DownLeft;

        for(int i = 1;i<=radius;i++) {
            HHexDirection.Up.translatePoint(current);
            for(int j = 0;j<6;j++,facing = HHexDirection.rotateClockwise(facing))
                for(int k = 0;k<i;k++,facing.translatePoint(current))
                    grid.put(new IntPoint(current),
                            new GameHexTile(this,current));
        }
    }


}
