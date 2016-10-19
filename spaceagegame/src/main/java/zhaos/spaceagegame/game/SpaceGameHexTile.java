package zhaos.spaceagegame.game;

import zhaos.spaceagegame.util.HHexDirection;
import zhaos.spaceagegame.util.IntPoint;

/**
 * Created by kodomazer on 9/26/2016.
 */
public class SpaceGameHexTile {
    IntPoint position;
    SpaceGameLocal parentGame;

    protected int energyCount;
    //-1 if contested
    //0 if unclaimed
    //any other number if it belongs to a team
    protected int affiliation;

    SpaceGameHexSubsection[] subsections;

    public SpaceGameHexTile(SpaceGameLocal parent, IntPoint position){
        //Have to have a reference to parent
        parentGame = parent;
        //deep copy
        this.position = new IntPoint(position);
        //Hexes start off as neutral
        affiliation = 0;
        //Initialize subsections
        subsections = new SpaceGameHexSubsection[7];
        for(int i = 0;i<7;i++){
            subsections[i] = new SpaceGameHexSubsection(this,HHexDirection.getDirection(i));
        }
    }


    public SpaceGameHexSubsection getSubsection(HHexDirection position){
        if(position==null)return null;
        return subsections[position.i()];
    }


    public IntPoint getPosition() {
        return position;
    }

    public void setPosition(IntPoint newPosition) {
        position = newPosition;
    }

    public SpaceGameHexTile getNeighbor(HHexDirection dir){
        IntPoint pos = new IntPoint(position);
        dir.translatePoint(pos);
        return parentGame.getTile(pos);
    }

    public SpaceGameHexTile[] getNeighbors(){
        //6 neighboring Hexes
        SpaceGameHexTile[] a = new SpaceGameHexTile[6];
        IntPoint current = new IntPoint(getPosition());
        HHexDirection dir = HHexDirection.DownRight;

        do{
            //get neighbor
            a[dir.i()] = parentGame.getTile(current);
            //move current point to the next point around the edge
            dir.translatePoint(current);
            //move direction Clockwise to be able to move the point in the right direction
            dir = HHexDirection.rotateClockwise(dir);
        }while(dir!=HHexDirection.DownRight);

        return a;
    }

    public void setParentGame(SpaceGameLocal parent) {
        parentGame = parent;
    }

    public SpaceGameHexSubsection[] getSubsections() {
        return subsections;
    }
}
