package zhaos.spaceagegame.spaceGame.entity;

import android.support.annotation.NonNull;

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
        energy = 3;
        setActionPoints(2);
    }

    @Override
    public void getInfo(MyBundle bundle){
        super.getInfo(bundle);
        bundle.putInt(RequestConstants.SPACE_STATION_ID, getID());

        int status = 0;
        if(energy>1&&remainingActions()>0)
            status |= RequestConstants.CAN_PRODUCE_UNIT;
        bundle.putInt(RequestConstants.CITY_STATUS_FLAGS,status);

    }

    boolean createUnit(Request action, MyBundle bundle){
        if(energy<2)
            return false;
        if(remainingActions()<1)
            return false;
        getParent().newUnit(this);
        energy-=2;
        useAction();
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
    public void getDice(@NonNull int[] dice) {
        dice[0] = 1*getLevel();
        dice[1] = 12;
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
