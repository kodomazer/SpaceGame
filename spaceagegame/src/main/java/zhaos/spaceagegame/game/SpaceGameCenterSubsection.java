package zhaos.spaceagegame.game;

import zhaos.spaceagegame.util.HHexDirection;

/**
 * Created by kodomazer on 9/27/2016.
 */
public class SpaceGameCenterSubsection extends SpaceGameHexSubsection {

    //Center subsection can hold a SpaceStation, other subsections cannot
    protected SpaceStation station;
    private int energyCount;

    public SpaceGameCenterSubsection(SpaceGameHexTile parent) {
        super(parent, HHexDirection.CENTER);
        station = null;
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

    public SpaceStation getCity() {
        return station;
    }

    public void buildCity(SpaceStation spaceStation) {
        station = spaceStation;
    }

    public void addUnit(Unit unit) {
        occupants.add(unit);

    }
}
