package zhaos.spaceagegame.game;

import android.graphics.Point;
import android.widget.Space;

import zhaos.spaceagegame.util.HHexDirection;
import zhaos.spaceagegame.util.IntPoint;

/**
 * Created by kodomazer on 9/26/2016.
 */

//Base class for anything that can take an action during the game

class Unit {
    //0 for neutral anything else is on a team
    private int teamID;

    //Level 0 means the unit is dead
    private int level;
    private int heldPodID;

    //Generally means actions remaining
    private int actionPoints;
    private boolean skippedMainPhase;

    //remember position
    private SpaceGameHexTile hexTile;
    private SpaceGameHexSubsection subsection;
    private SpaceGameLocal game;

    public Unit(SpaceStation s){
        teamID = s.getAffiliation();
        level = 1;
        hexTile = s.getHexTile();

        subsection = hexTile.getSubsection(HHexDirection.CENTER);
    }

    public void mainResetPhase(){

        actionPoints = 3;
    }

    void combatResetPhase(){
        actionPoints = 1;
    }

    public int remainingActions(){
        return actionPoints;
    }

    int getAffiliation() {
        return teamID;
    }

    SpaceGameHexTile getHexTile(){
        return hexTile;
    }

    int getLevel(){
        return level;
    }

    SpaceGameHexSubsection getSubsection(){
        return subsection;
    }


    void setSubsection(SpaceGameHexTile tile, SpaceGameHexSubsection subsection){

    }



}
