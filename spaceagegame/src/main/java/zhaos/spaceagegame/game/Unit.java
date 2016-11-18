package zhaos.spaceagegame.game;

import zhaos.spaceagegame.util.HHexDirection;

/**
 * Created by kodomazer on 9/26/2016.
 */

//Base class for anything that can take an action during the game

class Unit {
    private static int lastID = 1;
    //0 for neutral anything else is on a team
    private int factionNumber;

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
    private SpaceGameConstructionPod heldConstructionPod;
    private int ID;

    public Unit(SpaceStation s){
        factionNumber = s.getAffiliation();
        ID = lastID;
        lastID++;
        level = 1;
        hexTile = s.getHexTile();
        subsection = hexTile.getSubsection(HHexDirection.CENTER);
        ((SpaceGameCenterSubsection)subsection).addUnit(this);
        actionPoints=3;
    }

    public void mainResetPhase(){

        actionPoints = 3;
    }

    void combatResetPhase(){
        actionPoints = 1;
    }

    public boolean canMove(){
        return actionPoints>0;
    }

    public int remainingActions(){
        return actionPoints;
    }

    int getAffiliation() {
        return factionNumber;
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


    void setSubsection(SpaceGameHexTile tile,SpaceGameHexSubsection subsection){
        hexTile = tile;
        this.subsection = subsection;
    }


    public SpaceGameConstructionPod constructionPod() {
        return heldConstructionPod;
    }

    public void pickUpConstructionPod(SpaceGameConstructionPod constructionPod) {
        heldConstructionPod = constructionPod;
    }

    public int getID() {
        return ID;
    }
}
