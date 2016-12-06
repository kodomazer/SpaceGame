package zhaos.spaceagegame.spaceGame.entity;

import zhaos.spaceagegame.spaceGame.map.HexTile;
import zhaos.spaceagegame.spaceGame.map.Subsection;
import zhaos.spaceagegame.spaceGame.map.SubsectionCenter;
import zhaos.spaceagegame.util.HHexDirection;

/**
 * Created by kodomazer on 9/27/2016.
 */
public class ConstructionPod {
    public HexTile hexTile;
    public Subsection subsection;

    public int actionCounter;


    public void setPosition(HexTile parent, Subsection subsection) {
        hexTile = parent;
        this.subsection = subsection;
    }

    void addAction() {
        if (subsection.getPosition() != HHexDirection.CENTER) return;

        actionCounter += 1;
        if (actionCounter == 3) {
            ((SubsectionCenter)subsection).getCity();
        }
    }

}
