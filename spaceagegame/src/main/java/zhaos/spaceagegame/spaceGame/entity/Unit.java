package zhaos.spaceagegame.spaceGame.entity;

import zhaos.spaceagegame.spaceGame.map.Subsection;
import zhaos.spaceagegame.request.MyBundle;
import zhaos.spaceagegame.request.RequestConstants;

/**
 * Created by kodomazer on 9/26/2016.
 */

//Base class for anything that can take an action during the game

public class Unit extends Entity{
    private static final int TYPE = 1;

    //remember what is held
    private int heldPodID;

    //Generally means actions remaining
    private int actionPoints;
    private boolean skippedMainPhase;

    //Might hold a construction Pod
    private ConstructionPod heldConstructionPod;

    Unit(SpaceStation s,int ID){
        super(ID,s.getTeam(),s.getSubsection());
        actionPoints=3;
    }

    @Override
    public int getType() {
        return TYPE;
    }

    @Override
    public boolean resetPhase() {
        actionPoints = 3;
        return false;
    }

    @Override
    public void getInfo(MyBundle bundle) {
        super.getInfo(bundle);
        bundle.putInt(RequestConstants.UNIT_STATUS_FLAGS,
                ((getSubsection().canMove() ? 1 : 0) * RequestConstants.MOVABLE) |
                        ((getSubsection().canAttack() ? 1 : 0) * RequestConstants.CAN_ATTACK));
    }

    void combatResetPhase(){
        actionPoints = 1;
    }

    public Subsection getSubsection(){
        return subsection;
    }


    public void setSubsection(Subsection subsection){
        this.subsection = subsection;
    }


    public ConstructionPod constructionPod() {
        return heldConstructionPod;
    }

    public void pickUpConstructionPod(ConstructionPod constructionPod) {
        heldConstructionPod = constructionPod;
    }

}
