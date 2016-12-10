package zhaos.spaceagegame.spaceGame.map;

import android.graphics.Point;

import java.util.ArrayList;

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

    private void setNeighbor(HexTile neighbor,HHexDirection direction){
        neighbor.reciprocateNeighbor(this,direction.inverse());
        neighbors[direction.i()] = neighbor;
        HexTile temp;
        if(neighbors[direction.counterClockwise().i()]==null) {
            temp = neighbor.getNeighbor(direction.inverse().clockwise());
            if (temp != null)
                setNeighbor(temp, direction.counterClockwise());
        }
        if(neighbors[direction.clockwise().i()]==null)
        {
            temp = neighbor.getNeighbor(direction.inverse().counterClockwise());
            if (temp != null)
                setNeighbor(temp, direction.clockwise());
        }
    }

    private void reciprocateNeighbor(HexTile neighbor,HHexDirection direction){
        neighbors[direction.i()] = neighbor;
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

    public void placeCity(SpaceStation station) {
        ((SubsectionCenter)subsections[6]).placeCity(station);
    }

    void handleAction(Request action) {

    }

    public void getInfo(MyBundle bundle){
        ArrayList<MyBundle> subsectionList = new ArrayList<>(7);
        //Build Bundles for each subsection and then adds it to a list
        for (Subsection subsection : getSubsections()) {
            MyBundle subInfo = new MyBundle();
            subInfo.putPoint(RequestConstants.ORIGIN_HEX,
                    position);
            subsection.getSubsectionShallowInfo(subInfo);
            subsectionList.add(subInfo);
        }
        bundle.putArrayList(RequestConstants.SUBSECTION_LIST, subsectionList);

    }
}
