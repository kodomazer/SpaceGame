package zhaos.spaceagegame.spaceGame.entity;

import zhaos.spaceagegame.spaceGame.map.Subsection;

/**
 * Created by kodomazer on 9/26/2016.
 */

//Base class for anything that can take an action during the game

public abstract class Entity {
    //Teams start from ID 1
    protected int teamID;

    //Level 0 means the unit is dead
    protected int level;

    //Generally means actions remaining
    protected int actionPoints;

    //The subsection the Entity resides in
    protected Subsection parent;


    public int getAffiliation() {
        return teamID;
    }

    public int getLevel(){
        return level;
    }

    public int remainingActions(){
        return actionPoints;
    }

    protected boolean useAction(){
        if(actionPoints>0) {
            actionPoints--;
            return true;
        }
        return false;
    }






}
