package zhaos.spaceagegame.game;

import zhaos.spaceagegame.util.HHexDirection;

/**
 * Created by kodomazer on 9/27/2016.
 */
public class SpaceGameCenterSubsection extends SpaceGameHexSubsection {

    //Center subsection can hold a SpaceStation, other subsections cannot
    protected SpaceStation station;

    public SpaceGameCenterSubsection(SpaceGameHexTile parent) {
        super(parent, HHexDirection.CENTER);
        station = new SpaceStation();
    }

    @Override
    public SpaceGameHexSubsection[] getNeighbors() {
        //6 subsections adjacent to the center of the hexTile, all of the outer subsections
        SpaceGameHexSubsection[] a = new SpaceGameHexSubsection[6];
        HHexDirection current = HHexDirection.Up;
        do{
            a[current.i()] = parent.getSubsection(current); //get neighbor
            current = HHexDirection.rotateClockwise(current); //move direction clockwise
        }while(current!=HHexDirection.Up);

        return a;
    }
}
