package zhaos.spaceagegame.spaceGame.entity;

import android.graphics.Point;
import android.util.ArrayMap;
import android.util.Log;

import java.util.Map;

import zhaos.spaceagegame.request.MyBundle;
import zhaos.spaceagegame.request.Request;
import zhaos.spaceagegame.request.RequestConstants;
import zhaos.spaceagegame.request.helperRequest.EntityProductionRequest;
import zhaos.spaceagegame.request.helperRequest.SpaceStationInfoRequest;
import zhaos.spaceagegame.request.helperRequest.UnitAttackRequest;
import zhaos.spaceagegame.request.helperRequest.UnitInfoRequest;
import zhaos.spaceagegame.request.helperRequest.UnitMoveRequest;
import zhaos.spaceagegame.spaceGame.map.Subsection;
import zhaos.spaceagegame.spaceGame.map.SubsectionCenter;
import zhaos.spaceagegame.util.HHexDirection;

/**
 * Created by kodomazer on 12/4/2016.
 */

public class EntityHandler {
    private static String TAG = "Entity Handle";

    private int lastUnitID;
    private Map<Integer, Unit> units;

    private int lastPodID;
    private Map<Integer, ConstructionPod> pods;

    private int lastSpaceStationID;
    private Map<Integer, SpaceStation> spaceStations;
    private final Unit[] selected = new Unit[3];

    public EntityHandler() {

        units = new ArrayMap<>();
        lastSpaceStationID = 1;
        spaceStations = new ArrayMap<>();
        lastPodID = 1;
        pods = new ArrayMap<>();
    }

    //Getters
    private SpaceStation getCity(int id) {
        SpaceStation spaceStation = spaceStations.get(id);
        if (spaceStation == null) return null;
        return spaceStation;
    }

    public Unit getUnit(int id) {
        Unit unit = units.get(id);
        //When everything gets turned into an Entity 
        //this will have to be a class check before casting
        if (unit == null) return null;
        return unit;
    }

    public ConstructionPod getPod(int id) {
        //eventually Collapse all collections into one.
        Entity entity = pods.get(id);
        if (entity.getType() == ConstructionPod.TYPE) {
            return (ConstructionPod) entity;
        }
        return null;
    }

    //Factory Creations
    public Unit newUnit(SpaceStation station) {
        Unit unit = new Unit(station, lastUnitID);
        units.put(lastUnitID, unit);
        lastUnitID++;
        return unit;
    }

    public SpaceStation newSpaceStation(int faction, SubsectionCenter subsection) {
        SpaceStation spaceStation = new SpaceStation(lastSpaceStationID,
                faction,
                this,
                subsection);
        spaceStations.put(lastSpaceStationID, spaceStation);
        lastSpaceStationID++;
        return spaceStation;
    }

    public ConstructionPod newConstructionPod(SpaceStation station) {
        ConstructionPod pod = new ConstructionPod(station, lastPodID);
        pods.put(lastPodID, pod);
        lastPodID++;
        return pod;
    }

    //Actions across all Entities
    public void reset() {
        for (Unit u : units.values()) {
            u.getSubsection().updateInfluence(6, u.getTeam());
        }
    }

    //Request handling
    public boolean handleRequest(Request action, MyBundle bundle) {
        Log.i(TAG, "handleRequest: Entity Handler");
        switch (action.getInstruction()) {
            case RequestConstants.UNIT_ATTACK:
                return unitAttack(action, bundle);
            case RequestConstants.UNIT_INFO:
            case RequestConstants.CITY_INFO:
                return entityInfo(action, bundle);
            case RequestConstants.UNIT_MOVE:
                return unitMove(action, bundle);
            case RequestConstants.CITY_PROD_UNIT:
                return produceUnit(action,bundle);
            case RequestConstants.CITY_PROD_POD:
                return producePod(action,bundle);
        }
        //if none of the instruction signatures match
        return false;
    }

    //unused,
    private boolean producePod(Request action, MyBundle bundle) {

        return false;
    }

    private boolean produceUnit(Request action, MyBundle bundle) {
        EntityProductionRequest request = (EntityProductionRequest) action;
        SpaceStation city =  getCity(request.getID());
        if(city==null){
            Log.i(TAG, "produceUnit: NOT A CITY YOU Doofus");
            return false;}
        //if city isn't null, produce the unit
        return city.createUnit(action,bundle);
    }


    private boolean unitAttack(Request action, MyBundle bundle) {
        UnitAttackRequest attackRequest = (UnitAttackRequest) action;

        Point hex = attackRequest.getHex();
        HHexDirection subsection = attackRequest.getSubsection();

        Unit unit = getUnit(bundle.getInt(RequestConstants.UNIT_ID));

        for(Subsection sub: unit.getSubsection().getNeighbors()){
            if(sub.equals(hex,subsection)){
                sub.attack(new Unit[]{unit});
                return true;
            }
        }
        return false;
    }

    private boolean entityInfo(Request action, MyBundle bundle) {
        switch (action.getInstruction()){
            case RequestConstants.UNIT_INFO:
                UnitInfoRequest infoRequest = (UnitInfoRequest) action;
                Unit unit = getUnit(infoRequest.getUnitID());
                if (unit == null) {
                    return false;
                }
                unit.getInfo(bundle);
                return true;
            case RequestConstants.CITY_INFO:
                SpaceStationInfoRequest spaceStationInfoRequest =
                        (SpaceStationInfoRequest) action;
                SpaceStation spaceStation = getCity(spaceStationInfoRequest.getID());
                if(spaceStation==null) {
                    return false;
                }
                spaceStation.getInfo(bundle);
                return true;
        }
        return true;
    }

    private boolean unitMove(Request action, MyBundle bundle) {
        UnitMoveRequest request = (UnitMoveRequest) action;

        Log.i(TAG, "unitMove: Process Request");

        int unitID = request.getId();
        Unit unit = getUnit(unitID);
        if(unit==null){
            Log.i(TAG, "unitMove: NOT A UNIT");
            return false;}
        //If not null return
        return unit.moveToSubsection(request, bundle);
    }
}
