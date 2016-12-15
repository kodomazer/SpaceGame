package zhaos.spaceagegame.spaceGame.map;

import android.graphics.Point;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;

import zhaos.spaceagegame.request.MyBundle;
import zhaos.spaceagegame.request.RequestConstants;
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
    private HHexDirection position;

    protected Collection<Unit> occupants;
    protected Collection<ConstructionPod> pods;
    protected int affiliation;
    private int[] influenceLevels;
    protected Subsection[] neighbors = null;

    Subsection(HexTile parent, HHexDirection spot){
        occupants = new ArrayList<>();
        pods = new ArrayList<>();
        this.parent = parent;
        position = spot;
        affiliation = -1;
        influenceLevels = new int[10];
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

        if(!neighbor)return false;
        if(affiliation==-1)
            affiliation = e.getTeam();

        if(e.getTeam()==affiliation){
            occupants.add(e);
            origin.moveOut(e);
            ConstructionPod pod = e.constructionPod();
            if(pod!=null){
                e.getSubsection().moveOut(pod);
                pod.setPosition(this);
            }
            return true;
        }
        return false;
    }

    public boolean moveOut(Unit e){
        boolean success = occupants.remove(e);
        if(occupants.size()==0){
            affiliation = -1;
        }
        return success;
    }

    private boolean moveOut(ConstructionPod c){
        return pods.remove(c);
    }

    //returns neighboring subsections
    public Subsection[] getNeighbors(){
        if(neighbors == null) {
            //Four neighbors for a subsection in the outer ring of a Hex
            Subsection[] a = new Subsection[4];
            //Center subsection of same hexTile
            a[0] = parent.getSubsection(HHexDirection.CENTER);
            //Adjacent in the clockwise direction
            a[1] = parent.getSubsection(position.clockwise());
            //Adjacent on the other side in the counterclockwise direction
            a[2] = parent.getSubsection(position.counterClockwise());
            //subsection in neighboring Hex
            HexTile neighbor = parent.getNeighbor(position);
            if (neighbor != null)
                a[3] = neighbor.getSubsection(HHexDirection.flip(position));
            neighbors = a;
        }
        return neighbors;
    }

    public HHexDirection getPosition() {
        return position;
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

    protected void getSubsectionInfo(@NonNull MyBundle bundle) {
        //Handle Units
        Unit[] units = getUnits();
        ArrayList<MyBundle> unitList = new ArrayList<>(units.length);
        for(Unit unit: units){
            MyBundle unitInfo = new MyBundle();
            unitInfo.putInt(RequestConstants.LEVEL,unit.getLevel());
            unitInfo.putInt(RequestConstants.UNIT_ID,unit.getID());
            unitList.add(unitInfo);
        }
        bundle.putArrayList(RequestConstants.UNIT_LIST,unitList);
        getSubsectionOverview(bundle);
    }

    void getSubsectionOverview(@NonNull MyBundle bundle) {
        bundle.putSubsection(RequestConstants.ORIGIN_SUBSECTION,
                position);
        bundle.putInt(RequestConstants.FACTION_ID,
                affiliation);
    }

    public boolean canMove(){
        for (Subsection sub: getNeighbors()){
            if(sub.getAffiliation()==getAffiliation() || sub.getAffiliation()==-1)
                return true;
        }
        return false;
    }

    public boolean canAttack(){
        for(Subsection sub: getNeighbors()){
            if(sub.getAffiliation()!=getAffiliation() && sub.getAffiliation()!= -1)
                return true;
        }
        return false;
    }

    public Subsection[] getMoves() {
        Subsection[] move = new Subsection[getNeighbors().length];
        int team = getAffiliation();
        int i=0;
        int tile;
        for (Subsection s : getNeighbors()) {
            if (s == null) continue;
            tile = s.getAffiliation();
            if (tile == team || tile == -1) {
                move[i] = s;
                i++;
            }
        }
        return move;
    }

    public Subsection[] getAttacks() {
        Subsection[] attack = new Subsection[getNeighbors().length];
        int team = getAffiliation();
        int i=0;
        int tile;
        for (Subsection s : getNeighbors()) {
            if (s == null) continue;
            tile = s.getAffiliation();
            if (tile != team && tile != -1) {
                attack[i] = s;
                i++;
            }
        }
        return attack;
    }

    public boolean equals(Point hex, HHexDirection subsection) {
        return hex.equals( getParentPosition()) && subsection.equals(getPosition());
    }

    public void resetInfo(){
        for(int i = 0;i<influenceLevels.length;i++){
            influenceLevels[i]=0;
        }
        if(getAffiliation()==-1){
            if(getUnits().length!=0){
                affiliation = getUnits()[0].getTeam();
            }
        }
    }

    public void updateInfluence(int influence, int team){
        if(influence<=influenceLevels[team])
            return;
        influenceLevels[team]=influence;
        for (Subsection s: getNeighbors()) {
            if(s==null)continue;
            s.updateInfluence(influence-1,team);
        }
    }
}
