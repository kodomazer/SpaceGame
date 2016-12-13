package zhaos.spaceagegame.spaceGame.map;

import android.graphics.Point;

import java.util.ArrayList;

import zhaos.spaceagegame.request.helperRequest.SubsectionInfoBase;
import zhaos.spaceagegame.spaceGame.entity.SpaceStation;
import zhaos.spaceagegame.util.HHexDirection;
import zhaos.spaceagegame.request.MyBundle;
import zhaos.spaceagegame.request.Request;
import zhaos.spaceagegame.request.RequestConstants;

/**
 * Hex Tiles make up the base of a Hex based game board
 */
public class HexTile {
    private Point position;
    private final HexTile[] neighbors = new HexTile[6];

    protected int energyCount;
    //-1 if contested
    //0 if unclaimed
    //any other number if it belongs to a team
    protected int affiliation;

    Subsection[] subsections;

    public HexTile(Point position){
        //deep copy
        this.position = new Point(position);
        //Hexes start off as neutral
        affiliation = 0;
        //Initialize subsections
        subsections = new zhaos.spaceagegame.spaceGame.map.Subsection[7];
        for(int i = 0;i<7;i++) {
                subsections[i] = MapHandler.makeSubsection(HHexDirection.getDirection(i),this);
        }
    }


    public Subsection getSubsection(HHexDirection position){
        if(position==null)return null;
        return subsections[position.i()];
    }


    public Point getPosition() {
        return position;
    }

    public void setPosition(Point newPosition) {
        position = newPosition;
    }

    boolean setNeighbor(HexTile neighbor,HHexDirection direction){
        if(neighbors[direction.i()]!=null)
            return false;
        neighbors[direction.i()] = neighbor;
        return true;
    }


    void linkMapRecursion(HexTile neighbor,final HHexDirection direction) {
        if(!setNeighbor(neighbor,direction))
            return;
        neighbor.linkMapRecursion(this,direction.inverse());
        HHexDirection facing = direction;

        //Propagate clockwise
        facing = direction;
        for (int i = 0; i < 6; i++) {
            //We know our neighbor in the direction that we're facing
            //If the next neighbor is unknown
            if (neighbors[facing.clockwise().i()] == null) {
                //ask the neighbor we know
                HexTile nextNeighbor = neighbors[facing.i()]
                        //if they know the neighbor in that direction
                        .getNeighbor(facing.inverse().counterClockwise());
                //If they don't know stop the entire check
                //otherwise we won't know the neighbor in the facing direction
                if (nextNeighbor == null) break;
                //Set the neighbor first
                setNeighbor(nextNeighbor,facing.clockwise());
                //so it doesn't propagate back
                nextNeighbor.linkMapRecursion(this,
                        //and give it the opposite direction that we took
                        facing.clockwise().inverse());
            }
            facing = facing.clockwise();
        }

        //Propagate counter clockwise
        //in case a full round isn't made
        facing = direction;
        for (int i = 0; i < 6; i++) {
            //We know our neighbor in the direction that we're facing
            //If the next neighbor is unknown
            if (neighbors[facing.counterClockwise().i()] == null) {
                //ask the neighbor we know
                HexTile nextNeighbor = neighbors[facing.i()]
                        //if they know the neighbor in that direction
                        .getNeighbor(facing.inverse().clockwise());
                //If they don't know stop the entire check
                //otherwise we won't know the neighbor in the facing direction
                if (nextNeighbor == null) break;
                //Set the neighbor first
                setNeighbor(nextNeighbor,facing.counterClockwise());
                //so it doesn't propagate back
                nextNeighbor.linkMapRecursion(this,
                        //and give it the opposite direction that we took
                        facing.counterClockwise().inverse());
            }
            facing = facing.counterClockwise();
        }

    }

    HexTile getNeighbor(HHexDirection dir){
        if(dir == HHexDirection.CENTER)return this;
        return neighbors[dir.i()];
    }

    public HexTile[] getNeighbors(){
        //6 neighboring Hexes
        HexTile[] a = new HexTile[6];
        System.arraycopy(neighbors, 0, a, 0, 6);

        return a;
    }

    public Subsection[] getSubsections() {
        return subsections;
    }

    public SubsectionCenter getCenter(){
        return (SubsectionCenter) subsections[6];
    }

    public void placeCity(SpaceStation station) {
        getCenter().placeCity(station);
    }

    public void getInfo(MyBundle bundle){
        ArrayList<MyBundle> subsectionList = new ArrayList<>(7);
        //Build Bundles for each subsection and then adds it to a list
        for (Subsection subsection : getSubsections()) {
            MyBundle subInfo = new MyBundle();
            subInfo.putPoint(RequestConstants.ORIGIN_HEX,
                    position);
            subsection.getSubsectionOverview(subInfo);
            subsectionList.add(subInfo);
        }
        bundle.putArrayList(RequestConstants.SUBSECTION_LIST, subsectionList);

    }

    public void getSubsectionInfo(SubsectionInfoBase action, MyBundle subsectionInfo) {
        getSubsection(action.getSubsection()).getSubsectionInfo(subsectionInfo);
    }
}
