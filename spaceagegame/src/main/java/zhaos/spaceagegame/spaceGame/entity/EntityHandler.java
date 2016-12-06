package zhaos.spaceagegame.spaceGame.entity;

import android.util.ArrayMap;
import android.widget.Space;

import java.lang.reflect.AccessibleObject;
import java.util.Map;

import zhaos.spaceagegame.spaceGame.map.HexTile;
import zhaos.spaceagegame.util.Request;
import zhaos.spaceagegame.util.RequestConstants;

/**
 * Created by kodomazer on 12/4/2016.
 */

public class EntityHandler {

    private int lastUnitID;
    private Map<Integer, Unit> units;

    private int lastPodID;
    private Map<Integer, ConstructionPod> pods;

    private int lastSpaceStationID;
    private Map<Integer, SpaceStation> spaceStations;

    public EntityHandler(){

        units = new ArrayMap<>();
        lastSpaceStationID = 1;
        spaceStations = new ArrayMap<>();
        lastPodID = 1;
        pods = new ArrayMap<>();
    }
    
    //Getters
    public Unit getUnit(int id){
        Unit unit = units.get(id);
        //When everything gets turned into an Entity 
        //this will have to be a class check before casting
        if(unit == null)return null;
        return unit;
    }
    
    //Factory Creations
    public Unit newUnit(SpaceStation station){
        Unit unit = new Unit(station,lastUnitID);
        units.put(lastUnitID,unit);
        lastUnitID++;
        return unit;
    }

    public SpaceStation newSpaceStation(int faction, HexTile hexTile) {
        SpaceStation spaceStation = new SpaceStation(faction,hexTile,lastSpaceStationID);
        spaceStations.put(lastSpaceStationID,spaceStation);
        lastSpaceStationID++;
        return spaceStation;
    }

    public ConstructionPod newConstructionPod(SpaceStation station){
        ConstructionPod pod = new ConstructionPod(station,lastPodID);
        pods.put(lastPodID,pod);
        lastPodID++;
        return pod;
    }

    public void handleAction(Request action) {

        switch (action.getThisRequest().getInt(RequestConstants.INSTRUCTION)
                & RequestConstants.HANDLER_MASK) {
            case RequestConstants.UNIT_HANDLER:
                delegateToUnit(action);
                break;
            case RequestConstants.CITY_HANDLER:
                delegateToCity(action);
                break;
            case RequestConstants.POD_HANDLER:
                delegateToPod(action);
                break;
            default:
                switch (action.getThisRequest().getInt(RequestConstants.INSTRUCTION)) {
                    default:
                        //nothing so far
                }
        }
    }

    private void delegateToUnit(Request action) {
        //TODO: Find Unit and pass on the action
    }

    private void delegateToCity(Request action) {
        //TODO: Find the City and pass on the action
    }

    private void delegateToPod(Request action) {
        //TODO: Find the Pod and pass on the action
    }
}
