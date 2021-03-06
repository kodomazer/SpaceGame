package zhaos.spaceagegame.spaceGame.entity;

import android.support.annotation.NonNull;

import zhaos.spaceagegame.request.MyBundle;
import zhaos.spaceagegame.request.RequestConstants;
import zhaos.spaceagegame.spaceGame.map.Subsection;

/**
 * Created by kodomazer on 9/26/2016.
 */

//Base class for anything that can take an action during the game

public abstract class Entity {
    //Teams start from ID 1
    private int teamID;

    private int ID;

    //Level 0 means the unit is dead
    private int level;

    //Generally means actions remaining
    private int actionPoints;

    //The subsection the Entity resides in
    protected Subsection subsection;

    Entity(int ID,int team,Subsection subsection){
        this.ID = ID;
        teamID = team;
        this.subsection = subsection;

        level = 1;
    }

    //Entity Type identifier for casting in the Entity Manager
    public abstract int getType();

    //called on reset
    //Returns false if reset properly
    //returns true if extra actions needed (e.g. Construction Pods used)
    public abstract boolean resetPhase();

    public Subsection getSubsection(){
        return subsection;
    }

    public int getTeam() {
        return teamID;
    }

    public abstract void getDice(@NonNull int[] dice);

    public int getID(){
        return ID;
    }

    public int getLevel(){
        return level;
    }

    protected void upgrade(){
        level++;
    }

    public void damage(){
        damage(1);
    }

    protected void damage(int damage){
        level -= damage;
    }

    protected void absoluteDamage(int level){
        this.level = level;
    }

    public int remainingActions(){
        return actionPoints;
    }

    protected void setActionPoints(int points){
        actionPoints = points;
    }

    public boolean useAction(){
        if(actionPoints>0) {
            actionPoints--;
            return true;
        }
        return false;
    }

    public void getInfo(MyBundle bundle){
        //location
        bundle.putPoint(RequestConstants.ORIGIN_HEX, getSubsection().getParentPosition());
        bundle.putSubsection(RequestConstants.SUBSECTION,getSubsection().getPosition());
        //Basic Info
        bundle.putInt(RequestConstants.LEVEL, getLevel());
        bundle.putInt(RequestConstants.FACTION_ID, getTeam());
        bundle.putInt(RequestConstants.UNIT_ID, getID());
    }
}
