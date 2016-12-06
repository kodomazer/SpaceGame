package zhaos.spaceagegame.spaceGame.entity;

import android.util.ArrayMap;

import java.util.Map;

import zhaos.spaceagegame.spaceGame.map.HexTile;

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
}
