package zhaos.spaceagegame.spaceGame.map;

import android.graphics.Point;
import android.support.annotation.NonNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import zhaos.spaceagegame.request.MyBundle;
import zhaos.spaceagegame.request.helperRequest.HexInfoRequest;
import zhaos.spaceagegame.request.helperRequest.SubsectionInfoBase;
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

    private @NonNull HexTile generateTile(Point position){
        if(getHex(position)!=null)return getHex(position);
        HexTile hex = new HexTile(position);
        map.put(new Point(position),hex);
        return hex;
    }

    //Initialize map
    public void initializeMap(int radius) {
        map = new HashMap<>();
        //center is at (radius*2,radius)
        Point current = new Point(radius, radius);
        HHexDirection facing = HHexDirection.UpRight;
        HexTile last;
        HexTile first = null;
        HexTile cur = null;
        //Generate map ring by ring, going outward
        for (int i = 1; i <= radius; i++) {
            //Move up once to move out one ring
            HHexDirection.Up.translatePoint(current);
            //Go each direction once, switch the direction of travel each loop
            for (int j = 0; j < 6; j++) {
                facing = facing.clockwise();
                //Translate in the direction a number of times equal to the radius
                //Radius is equal to side length
                for (int k = 0; k < i; k++) {
                    //Add a new hex tile to the map
                    //Deep copy of the Point because the current point will change
                    last = cur;
                    cur = generateTile(current);
                    if(j+k==0){
                        if(first!=null)
                            first.linkMapRecursion(cur,HHexDirection.Up);
                        first = cur;
                    }
                    else if (last != null) {
                        last.linkMapRecursion(cur,k<1?facing.counterClockwise():facing);
                    }
                    facing.translatePoint(current);
                }
            }
            if(cur!=null)
                cur.linkMapRecursion(first,facing);
            cur = first;
        }
    }

    public void reset() {
        for (HexTile h : getMap()) {
            for (Subsection s : h.getSubsections()) {
                s.resetInfo();
            }
        }
    }

    public boolean handleRequest(Request action, MyBundle bundle) {
        switch (action.getThisRequest().getInt(RequestConstants.INSTRUCTION)){
            case RequestConstants.HEX_INFO:
                return hexInfo(action,bundle);
            case RequestConstants.SUBSECTION_INFO:
                return subsectionInfo(action,bundle);
        }
        return false;
    }

    private boolean subsectionInfo(Request action, MyBundle infoBundle) {
        if (action.getInstructioin() != RequestConstants.SUBSECTION_INFO) {
            return false;
        }
        SubsectionInfoBase infoRequest = (SubsectionInfoBase) action;
        HexTile tile = getHex(infoRequest.getHex());
        if (tile == null) {
            return false;
        }
        tile.getSubsectionInfo(infoRequest, infoBundle);
        return true;
    }

    private boolean hexInfo(Request action, MyBundle infoBundle) {
        if (action.getInstructioin() != RequestConstants.HEX_INFO) {
            return false;
        }
        HexInfoRequest infoRequest = (HexInfoRequest) action;
        HexTile tile = getHex(infoRequest.getHex());
        if (tile == null) {
            return false;
        }
        tile.getInfo(infoBundle);
        return true;
    }
}
