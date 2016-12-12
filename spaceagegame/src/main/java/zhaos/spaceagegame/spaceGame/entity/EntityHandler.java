package zhaos.spaceagegame.spaceGame.entity;

import android.util.ArrayMap;

import java.util.Map;

import zhaos.spaceagegame.spaceGame.map.SubsectionCenter;

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
    private SpaceStation getCity(int id) {
        SpaceStation spaceStation = spaceStations.get(id);
        if(spaceStation==null)return null;
        return spaceStation;
    }
    public Unit getUnit(int id){
        Unit unit = units.get(id);
        //When everything gets turned into an Entity 
        //this will have to be a class check before casting
        if(unit == null)return null;
        return unit;
    }

    public ConstructionPod getPod(int id){
        //eventually Collapse all collectioins into one.
        Entity entity = pods.get(id);
        if(entity.getType() == ConstructionPod.TYPE){
            return (ConstructionPod) entity;
        }
        return null;
    }
    
    //Factory Creations
    public Unit newUnit(SpaceStation station){
        Unit unit = new Unit(station,lastUnitID);
        units.put(lastUnitID,unit);
        lastUnitID++;
        station.getSubsection().addUnit(unit);
        return unit;
    }

    public SpaceStation newSpaceStation(int faction, SubsectionCenter subsection) {
        SpaceStation spaceStation = new SpaceStation(lastSpaceStationID, faction, this, subsection);
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

}
