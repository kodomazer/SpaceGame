package zhaos.spaceagegame.spaceGame.entity;

import zhaos.spaceagegame.request.MyBundle;
import zhaos.spaceagegame.request.Request;
import zhaos.spaceagegame.request.RequestConstants;
import zhaos.spaceagegame.spaceGame.map.SubsectionCenter;

/**
 * Created by kodomazer on 9/27/2016.
 */
public class SpaceStation extends Entity{
    private static final int TYPE = 2;

    //Needs to know to make more entities
    private EntityHandler parent;
    private int energy;

    SpaceStation(int ID, int faction, EntityHandler entityHandler,
                 SubsectionCenter subsection){
        super(ID,faction,subsection);
        parent = entityHandler;
    }

    public void getSpaceStationInfo(MyBundle bundle){
        bundle.putInt(RequestConstants.SPACE_STATION_ID, getID());
        bundle.putInt(RequestConstants.LEVEL,getLevel());
    }

    boolean createUnit(){
        //TODO: Check for supplies
        Unit unit = getParent().newUnit(this);
        return true;
    }

    boolean createPod() {
        //TODO: Check for Supplies
        ConstructionPod pod = getParent().newConstructionPod(this);
        return true;
    }

    void upgradeUnit(Unit unit){
        //TODO check for supplies and then upgrade
    }

    @Override
    public SubsectionCenter getSubsection(){
        return (SubsectionCenter) super.getSubsection();
    }

    @Override
    public int getType() {
        return TYPE;
    }

    @Override
    public boolean resetPhase() {
        //Energy per turn based on level in lieu of proper collection
        energy += getLevel();
        setActionPoints(getLevel());
        return false;
    }

    public EntityHandler getParent() {
        return parent;
    }
}
