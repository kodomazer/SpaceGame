package zhaos.spaceagegame.spaceGame.map;

import android.graphics.Point;

import java.util.ArrayList;

import zhaos.spaceagegame.spaceGame.LocalGame;
import zhaos.spaceagegame.spaceGame.entity.SpaceStation;
import zhaos.spaceagegame.spaceGame.entity.Unit;
import zhaos.spaceagegame.util.HHexDirection;
import zhaos.spaceagegame.util.MyBundle;
import zhaos.spaceagegame.util.Request;
import zhaos.spaceagegame.util.RequestConstants;

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
        //6 subsections adjacent to the center of the hexTile, all of the outer subsections
        Subsection[] a = new Subsection[6];
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
    protected void getSubsectionInfo(Request action) {
        Request.RequestCallback callback = action.getCallback();
        if (callback == null) return;

        MyBundle bundle = action.getThisRequest();
        Point position = bundle.getPoint(RequestConstants.ORIGIN_HEX);
        HHexDirection direction = bundle.getSubsection(RequestConstants.ORIGIN_SUBSECTION);

        if(direction == HHexDirection.CENTER){
            MyBundle cityInfo = new MyBundle();
            SpaceStation city = getCity();
            //
            if(city!=null){
                city.getSpaceStationInfo(cityInfo);
                bundle.putBundle(RequestConstants.SPACE_STATION_INFO,cityInfo);
            }
        }

        //Handle Units
        Unit[] units = getUnits();
        ArrayList<MyBundle> unitList = new ArrayList<>(units.length);
        for(Unit unit: units){
            MyBundle unitInfo = new MyBundle();
            unitInfo.putInt(RequestConstants.LEVEL,unit.getLevel());
            unitInfo.putInt(RequestConstants.UNIT_ID,unit.getID());
            unitList.add(unitInfo);
        }
        bundle.putArrayList(RequestConstants.UNIT_LIST,unitList);

        //callback
        LocalGame.getInstance().actionCompleted(callback, bundle, true);
    }
}
