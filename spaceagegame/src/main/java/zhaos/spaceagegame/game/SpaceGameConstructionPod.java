package zhaos.spaceagegame.game;

import zhaos.spaceagegame.util.HHexDirection;

/**
 * Created by kodomazer on 9/27/2016.
 */
public class SpaceGameConstructionPod {
    public SpaceGameHexTile hexTile;
    public SpaceGameHexSubsection subsection;

    public int actionCounter;


    public void setPosition(SpaceGameHexTile parent, SpaceGameHexSubsection subsection) {
        hexTile = parent;
        this.subsection = subsection;
    }

    void addAction() {
        if (subsection.getPosition() != HHexDirection.CENTER) return;

        actionCounter += 1;
        if (actionCounter == 3) {
            ((SpaceGameCenterSubsection)subsection).getCity();
        }
    }

}
