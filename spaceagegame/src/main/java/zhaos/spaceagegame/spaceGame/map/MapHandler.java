package zhaos.spaceagegame.spaceGame.map;

import android.graphics.Point;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import zhaos.spaceagegame.util.HHexDirection;
import zhaos.spaceagegame.request.Request;
import zhaos.spaceagegame.request.RequestConstants;

/**
 * Created by kodomazer on 12/4/2016.
 */

public final class MapHandler {

    private Map<Point, HexTile> map;

    public MapHandler(){
        map = new HashMap<>();
    };


    static Subsection makeSubsection(HHexDirection direction,
                                     HexTile parent){
        if(HHexDirection.CENTER == direction){
            return new SubsectionCenter(parent);
        }
        else{
            return new Subsection(parent,direction);
        }
    }

    public HexTile getHex(Point position){
        return map.get(position);
    }

    public Collection<HexTile> getMap(){
        return map.values();
    }

    private void generateTile(Point position){
        if(getHex(position)!=null)return;
        HexTile hex = new HexTile(position);
        map.put(new Point(position),hex);
    }

    //Initialize map
    public void initializeMap(int radius) {
        map = new HashMap<>();
        //center is at (radius*2,radius)
        Point current = new Point(radius, radius);
        HHexDirection facing = HHexDirection.DownLeft;

        //Generate map ring by ring, going outward
        for (int i = 1; i <= radius; i++) {
            //Move up once to move out one ring
            HHexDirection.Up.translatePoint(current);
            //Go each direction once, switch the direction of travel each loop
            for (int j = 0; j < 6; j++, facing = HHexDirection.rotateClockwise(facing))
                //Translate in the direction a number of times equal to the radius
                //Radius is equal to side length
                for (int k = 0; k < i; k++, facing.translatePoint(current))
                    //Add a new hex tile to the map
                    //Deep copy of the Point because the current point will change
                    generateTile(current);
        }
    }

    public void handleAction(Request action) {
        switch (action.getThisRequest().getInt(RequestConstants.INSTRUCTION)
                & RequestConstants.HANDLER_MASK){
            case RequestConstants.HEX_HANDLER:
                delegateToHex(action);
                break;
            case RequestConstants.SUB_HANDLER:
                delegateToSubsection(action);
                break;
            default:
                switch (action.getThisRequest().getInt(RequestConstants.INSTRUCTION)){
                    default:
                        //nothing so far
                }
        }

    }

    private void delegateToSubsection(Request action) {
        //TODO: Need to find the proper Subsection and then pass on the action
        //Possibly just pass it to to the hex and the hex will pass it to the subsection...
    }

    private void delegateToHex(Request action) {
        //TODO: Need to find the proper hex and then pass on the action
        HexTile hex = getHex(action.getThisRequest().getPoint(RequestConstants.HEX));
        if(hex!=null){
            hex.handleAction(action);
        }
    }

}
