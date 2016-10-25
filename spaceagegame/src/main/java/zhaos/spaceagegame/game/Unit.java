package zhaos.spaceagegame.game;

import android.graphics.Point;

import zhaos.spaceagegame.util.HHexDirection;
import zhaos.spaceagegame.util.IntPoint;

/**
 * Created by kodomazer on 9/26/2016.
 */

//Base class for anything that can take an action during the game

public class Unit {
    //0 for neutral anything else is on a team
    private int teamID;

    //Level 0 means the unit is dead
    private int level;

    //Generally means actions remaining
    private int actionPoints;

    //remember position
    protected Point hexTile;
    protected HHexDirection subsection;

    public Unit(SpaceStation s){
        teamID = s.getAffiliation();
        level = 1;
        hexTile = s.getHexTile();
        subsection = HHexDirection.CENTER;
    }

    public void mainResetPhase(){
        actionPoints = 3;
    }

    public void combatResetPhase(){
        actionPoints = 1;
    }

    public int getAffiliation() {
        return teamID;
    }

    public int getLevel(){
        return level;
    }

    public int remainingActions(){
        return actionPoints;
    }

    public Point getHexTile(){
        return hexTile;
    }


    public HHexDirection getSubsection(){
        return subsection;
    }





}
