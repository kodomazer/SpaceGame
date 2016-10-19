package zhaos.spaceagegame.game;

import android.widget.RelativeLayout;

import zhaos.spaceagegame.util.HHexDirection;
import zhaos.spaceagegame.util.FloatPoint;
import zhaos.spaceagegame.util.Game;
import zhaos.spaceagegame.util.HexTile;
import zhaos.spaceagegame.util.IntPoint;

/**
 * Created by kodomazer on 9/26/2016.
 */
public class GameHexTile {
    IntPoint position;
    SpaceGame parentGame;

    protected int energyCount;
    //-1 if contested
    //0 if unclaimed
    //any other number if it belongs to a team
    protected int affiliation;

    HexSubsection[] subsections;

    public GameHexTile(SpaceGame parent, IntPoint position){
        //Have to have a reference to parent
        parentGame = parent;
        //deep copy
        this.position = new IntPoint(position);
        //Hexes start off as neutral
        affiliation = 0;
        //Initialize subsections
        subsections = new HexSubsection[7];
        for(int i = 0;i<7;i++){
            subsections[i] = new HexSubsection(this,HHexDirection.getDirection(i));
        }
    }


    public HexSubsection getSubsection(HHexDirection position){
        if(position==null)return null;
        return subsections[position.i()];
    }


    public IntPoint getPosition() {
        return position;
    }

    public void setPosition(IntPoint newPosition) {
        position = newPosition;
    }

    public GameHexTile getNeighbor(HHexDirection dir){
        IntPoint pos = new IntPoint(position);
        dir.translatePoint(pos);
        return parentGame.getTile(pos);
    }

    public GameHexTile[] getNeighbors(){
        //6 neighboring Hexes
        GameHexTile[] a = new GameHexTile[6];
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

    public void setParentGame(SpaceGame parent) {
        parentGame = parent;
    }

    public HexSubsection[] getSubsections() {
        return subsections;
    }
}
