package zhaos.spaceagegame.spaceGame.entity;

import android.support.annotation.NonNull;

import zhaos.spaceagegame.spaceGame.map.Subsection;
import zhaos.spaceagegame.spaceGame.map.SubsectionCenter;
import zhaos.spaceagegame.util.HHexDirection;

/**
 * Construction Pod
 * Held off until later
 */
public class ConstructionPod extends Entity{
    private String TAG = "Cons_Pod";
    public final static int TYPE = 3;

    ConstructionPod(SpaceStation station,int ID){
        super(ID,0,station.getSubsection());
    }

    @Override
    public int getType() {
        return TYPE;
    }

    @Override
    public boolean resetPhase() {
        return false;
    }

    @Override
    public int getTeam(){
        return -1;
    }

    @Override
    public void getDice(@NonNull int[] dice) {
        dice[0] = 0;
        dice[1] = 0;
    }

    public void setPosition(Subsection subsection) {
        absoluteDamage(1);
        this.subsection = subsection;
    }

    void addAction() {
        if (subsection.getPosition() != HHexDirection.CENTER) return;

        upgrade();
        if (getLevel() == 4) {
            ((SubsectionCenter)subsection).getCity();
            //TODO: add in City Building/Upgrade code
        }
    }
}
