package zhaos.spaceagegame.spaceGame.entity;

import zhaos.spaceagegame.spaceGame.LocalGame;
import zhaos.spaceagegame.spaceGame.map.HexTile;
import zhaos.spaceagegame.spaceGame.map.Subsection;
import zhaos.spaceagegame.spaceGame.map.SubsectionCenter;
import zhaos.spaceagegame.util.HHexDirection;
import zhaos.spaceagegame.util.MyBundle;
import zhaos.spaceagegame.util.Request;
import zhaos.spaceagegame.util.RequestConstants;

/**
 * Created by kodomazer on 9/26/2016.
 */

//Base class for anything that can take an action during the game

public class Unit {
    //0 for neutral anything else is on a team
    private int factionNumber;

    //Level 0 means the unit is dead
    private int level;
    private int heldPodID;

    //Generally means actions remaining
    private int actionPoints;
    private boolean skippedMainPhase;

    //remember position
    private HexTile hexTile;
    private Subsection subsection;
    private LocalGame game;
    private ConstructionPod heldConstructionPod;
    private int ID;

    Unit(SpaceStation s,int ID){
        factionNumber = s.getAffiliation();
        this.ID = ID;
        level = 1;
        hexTile = s.getHexTile();
        subsection = hexTile.getSubsection(HHexDirection.CENTER);
        ((SubsectionCenter)subsection).addUnit(this);
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

    public int getAffiliation() {
        return factionNumber;
    }

    public HexTile getHexTile(){
        return hexTile;
    }

    public int getLevel(){
        return level;
    }

    public Subsection getSubsection(){
        return subsection;
    }


    public void setSubsection(HexTile tile, Subsection subsection){
        hexTile = tile;
        this.subsection = subsection;
    }


    public ConstructionPod constructionPod() {
        return heldConstructionPod;
    }

    public void pickUpConstructionPod(ConstructionPod constructionPod) {
        heldConstructionPod = constructionPod;
    }

    public int getID() {
        return ID;
    }

    void bundleInfo(MyBundle bundle){

    }
}
