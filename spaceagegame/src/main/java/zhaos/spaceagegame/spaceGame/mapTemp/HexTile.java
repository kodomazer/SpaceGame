package zhaos.spaceagegame.spaceGame.map;

import android.graphics.Point;

import zhaos.spaceagegame.spaceGame.LocalGame;
import zhaos.spaceagegame.spaceGame.entity.SpaceStation;
import zhaos.spaceagegame.util.HHexDirection;

/**
 * Created by kodomazer on 9/26/2016.
 */
public class HexTile {
    Point position;
    final HexTile[] neighbors = new HexTile[6];

    protected int energyCount;
    //-1 if contested
    //0 if unclaimed
    //any other number if it belongs to a team
    protected int affiliation;

    zhaos.spaceagegame.spaceGame.map.Subsection[] subsections;

    public HexTile(Point position){
        //deep copy
        this.position = new Point(position);
        //Hexes start off as neutral
        affiliation = 0;
        //Initialize subsections
        subsections = new zhaos.spaceagegame.spaceGame.map.Subsection[7];
        for(int i = 0;i<7;i++) {
                subsections[i] = zhaos.spaceagegame.spaceGame.map.MapHandler.makeSubsection(HHexDirection.getDirection(i),this);
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

}
