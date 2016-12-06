package zhaos.spaceagegame.spaceGame.map;

import android.graphics.Point;

import java.util.ArrayList;
import java.util.Collection;

import zhaos.spaceagegame.spaceGame.entity.ConstructionPod;
import zhaos.spaceagegame.spaceGame.entity.Unit;
import zhaos.spaceagegame.util.HHexDirection;

/**
 * Created by kodomazer on 9/26/2016.
 */
public class Subsection {
    private static final String TAG = "Hex Subsection";
    //Hex Tile that the subsection belongs to.
    protected HexTile parent;


    //The Part of the tile the subsection is part of
    protected HHexDirection position;

    protected Collection<Unit> occupants;
    protected Collection<ConstructionPod> pods;
    protected int affiliation;
    private int[] influenceLevels;

    Subsection(HexTile parent, HHexDirection spot){
        occupants = new ArrayList<>();
        pods = new ArrayList<>();
        this.parent = parent;
        position = spot;
        affiliation = -1;
    }

    public int getAffiliation(){
        return affiliation;
    }

    public boolean moveIn(Unit e){
        boolean neighbor=false;
        Subsection origin = e.getSubsection();
        if(origin!=null)
            for(Subsection subsections:getNeighbors()){
                if(origin==subsections)
                    neighbor = true;
            }
        else
            neighbor = true;

        if(!neighbor)return false;
        if(affiliation==-1)
            affiliation = e.getAffiliation();

        if(e.getAffiliation()==affiliation){
            occupants.add(e);
            if(origin!=null)
                origin.moveOut(e);
            ConstructionPod pod = e.constructionPod();
            if(pod!=null){
                e.getSubsection().moveOut(pod);
                pod.setPosition(parent,this);
            }
            e.setSubsection(parent,this);
            return true;
        }
        return false;
    }

    public boolean moveOut(Unit e){
        boolean success = occupants.remove(e);

        if(occupants.size()==0){
            affiliation = 0;
        }

        return success;
    }

    public boolean moveOut(ConstructionPod c){
        return pods.remove(c);
    }


    //Takes in an array of Entity
    //returns true if the attack killed the last unit in the subsection
    //returns false if there are still units in the subsection
    public boolean attack(Unit[] entities){
        //TODO Implement battle mechanics
        //TODO figure out how to pick defenders, automated? or choice
        return false;
    }


    //returns neighboring subsections
    public Subsection[] getNeighbors(){
        //Four neighbors for a subsection in the outer ring of a Hex
        Subsection[] a = new Subsection[4];
        //Center subsection of same hexTile
        a[0]=parent.getSubsection(HHexDirection.CENTER);
        //Adjacent in the clockwise direction
        a[1]=parent.getSubsection(HHexDirection.rotateClockwise(position));
        //Adjacent on the other side in the counterclockwise direction
        a[2]=parent.getSubsection(HHexDirection.rotateCounterClockwise(position));
        //subsection in neighboring Hex
        a[3]=parent.getNeighbor(position).getSubsection(HHexDirection.flip(position));
        return a;
    }

    public HHexDirection getPosition() {
        return position;
    }

    public void resetInfo(){
        for(int i = 0;i<influenceLevels.length;i++){
            influenceLevels[i]=0;
        }
    }

    public void updateInfluence(int influence, int team){
        if(influence<=influenceLevels[team])
            return;
        influenceLevels[team]=influence;
        for (Subsection s: getNeighbors()) {
            s.updateInfluence(influence-1,team);
        }
    }

    public Point getParentPosition() {
        return parent.getPosition();
    }
    HexTile getParent() {
        return parent;
    }

    public Unit[] getUnits() {
        Unit[] units = new Unit[occupants.size()];
        occupants.toArray(units);
        return units;
    }

}
