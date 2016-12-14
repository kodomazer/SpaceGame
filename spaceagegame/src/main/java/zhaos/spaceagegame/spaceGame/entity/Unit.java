package zhaos.spaceagegame.spaceGame.entity;

import android.graphics.Point;
import android.support.annotation.NonNull;
import android.util.Log;

import zhaos.spaceagegame.request.helperRequest.UnitMoveRequest;
import zhaos.spaceagegame.spaceGame.map.Subsection;
import zhaos.spaceagegame.request.MyBundle;
import zhaos.spaceagegame.request.RequestConstants;
import zhaos.spaceagegame.util.HHexDirection;

/**
 * Created by kodomazer on 9/26/2016.
 */

//Base class for anything that can take an action during the game

public class Unit extends Entity{
    private static final String TAG = "Mobile Entity";
    private static final int TYPE = 1;

    //remember what is held
    private int heldPodID;

    private boolean skippedMainPhase;

    //Might hold a construction Pod
    private ConstructionPod heldConstructionPod;

    Unit(SpaceStation s,int ID){
        super(ID,s.getTeam(),s.getSubsection());

        s.getSubsection().addUnit(this);
        setActionPoints(3);
    }

    @Override
    public int getType() {
        return TYPE;
    }

    @Override
    public boolean resetPhase() {
        setActionPoints(3);
        return false;
    }

    @Override
    public void getInfo(MyBundle bundle) {
        super.getInfo(bundle);

        int status = 0;
        if(getSubsection().canMove()&&remainingActions()>1)
            status |= RequestConstants.MOVABLE;
        if(getSubsection().canAttack()&&remainingActions()>1)
            status|= RequestConstants.CAN_ATTACK;

        bundle.putInt(RequestConstants.UNIT_STATUS_FLAGS,status);
    }

    void combatResetPhase(){
        setActionPoints(1);
    }

    public Subsection getSubsection(){
        return subsection;
    }

    @Override
    public void getDice(@NonNull int[] dice) {
        dice[1] = 6;
        dice[0] = 2*getLevel();
    }


    public boolean moveToSubsection(UnitMoveRequest moveRequest, MyBundle infoBundle){
        Log.i(TAG, "moveToSubsection: Begin Processing");
        if(remainingActions()<1)
            return false;

        Subsection[] valid = getSubsection().getMoves();

        //get destination Info
        Point destinationHex = moveRequest.getHex();
        HHexDirection destinationSubsection
                = moveRequest.getSubsection();

        //If one of the subsections is the correct subsection
        for (Subsection section : valid) {
            if (section == null) continue;
            if (section.equals(destinationHex, destinationSubsection)) {
                Log.i(TAG, "unitMove: moved in");
                useAction();
                boolean success =section.moveIn(this);
                this.subsection = section;
                return success;
            }
        }
        Log.i(TAG, "moveToSubsection: No Suitable destination");
        return false;
    }


    //Construction pod will be held off until later
    public ConstructionPod constructionPod() {
        return heldConstructionPod;
    }

    public void pickUpConstructionPod(ConstructionPod constructionPod) {
        heldConstructionPod = constructionPod;
    }

}
