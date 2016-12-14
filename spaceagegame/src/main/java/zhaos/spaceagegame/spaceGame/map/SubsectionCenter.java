package zhaos.spaceagegame.spaceGame.map;

import android.support.annotation.NonNull;

import zhaos.spaceagegame.spaceGame.entity.SpaceStation;
import zhaos.spaceagegame.spaceGame.entity.Unit;
import zhaos.spaceagegame.util.HHexDirection;
import zhaos.spaceagegame.request.MyBundle;
import zhaos.spaceagegame.request.RequestConstants;

/**
 * Created by kodomazer on 9/27/2016.
 */
public class SubsectionCenter extends Subsection {

    //Center subsection can hold a SpaceStation, other subsections cannot
    private SpaceStation station;
    private int energyCount;

    SubsectionCenter(HexTile parent) {
        super(parent, HHexDirection.CENTER);
        station = null;
    }

    @Override
    public Subsection[] getNeighbors() {
        //lazy initialize
        if(neighbors == null) {
            //6 subsections adjacent to the center of the hexTile, all of the outer subsections
            Subsection[] a = new Subsection[6];
            HHexDirection current = HHexDirection.Up;
            do{
                a[current.i()] = parent.getSubsection(current); //get neighbor
                current = current.clockwise(); //move direction clockwise
            }while(current!=HHexDirection.Up);
            neighbors = a;
        }
        return neighbors;
    }

    public SpaceStation getCity() {
        return station;
    }


    void placeCity(SpaceStation spaceStation) {
        station = spaceStation;
    }

    //Only used when creating units via space station
    public void addUnit(Unit unit) {
        if(occupants.contains(unit))return;
        occupants.add(unit);
    }

    void buildCity(SpaceStation city) {
        station = city;
    }

    @Override
    public boolean moveOut(Unit u){
        boolean success = super.moveOut(u);
        if(getCity()!=null){
            affiliation = getCity().getTeam();
        }
        return success;
    }

    @Override
    protected void getSubsectionInfo(@NonNull MyBundle bundle) {
        super.getSubsectionInfo(bundle);
            MyBundle cityInfo = new MyBundle();
            SpaceStation city = getCity();
            //
            if(city!=null){
                city.getInfo(cityInfo);
                bundle.putBundle(RequestConstants.SPACE_STATION_INFO,cityInfo);
            }
    }
}
