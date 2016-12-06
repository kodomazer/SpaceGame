package zhaos.spaceagegame.spaceGame.entity;

import zhaos.spaceagegame.spaceGame.map.HexTile;
import zhaos.spaceagegame.spaceGame.map.Subsection;
import zhaos.spaceagegame.spaceGame.map.SubsectionCenter;
import zhaos.spaceagegame.util.HHexDirection;

/**
 * Created by kodomazer on 9/27/2016.
 */
public class ConstructionPod {
    private String TAG = "Cons_Pod";

    private HexTile hexTile;
    private Subsection subsection;

    private int ID;

    public int actionCounter;

    ConstructionPod(SpaceStation station,int ID){
        this.ID = ID;
        hexTile = station.getHexTile();
        subsection = hexTile.getSubsection(HHexDirection.CENTER);
    }


    public void setPosition(HexTile parent, Subsection subsection) {
        hexTile = parent;
        this.subsection = subsection;
    }

    void addAction() {
        if (subsection.getPosition() != HHexDirection.CENTER) return;

        actionCounter += 1;
        if (actionCounter == 3) {
            ((SubsectionCenter)subsection).getCity();
            //TODO: add in City Building/Upgrade code
        }
    }

}
