package zhaos.spaceagegame.spaceGame.entity;

import zhaos.spaceagegame.spaceGame.map.HexTile;
import zhaos.spaceagegame.util.MyBundle;
import zhaos.spaceagegame.util.Request;
import zhaos.spaceagegame.util.RequestConstants;

/**
 * Created by kodomazer on 9/27/2016.
 */
public class SpaceStation{

    private int _ID;
    private int level;
    private int actions;
    private int affiliation;

    private HexTile hexTile;
    private EntityHandler parent;

    SpaceStation(EntityHandler entityHandler,
                 int faction,
                 HexTile hexTile,
                 int ID){
        level = 1;
        this.affiliation = faction;
        this.hexTile = hexTile;
        this._ID = ID;

        parent = entityHandler;
    }

    public void getSpaceStationInfo(MyBundle bundle){
        bundle.putInt(RequestConstants.SPACE_STATION_ID, getID());
        bundle.putInt(RequestConstants.LEVEL,getLevel());
    }

    int getAffiliation(){
        return affiliation;
    }

    HexTile getHexTile(){
        return hexTile;
    }

    private void createUnit(){
        Unit unit = parent.newUnit(this);
    }

    private void createPod(){
        ConstructionPod pod = parent.newConstructionPod(this);
    }

    int getID() {
        return _ID;
    }

    int getLevel() {
        return level;
    }

    void upgrade() {
        level++;
    }

    void handleAction(Request action) {
        MyBundle bundle = action.getThisRequest();
        switch (bundle.getInt(RequestConstants.INSTRUCTION)){
            case RequestConstants.CITY_PROD_UNIT:
                createUnit();
                break;
            case RequestConstants.CITY_PROD_POD:
                createPod();
                break;

        }
    }
}
