package zhaos.spaceagegame.game;

import android.graphics.Point;

import zhaos.spaceagegame.util.IntPoint;

/**
 * Created by kodomazer on 9/27/2016.
 */
public class SpaceStation{

    private int level;
    private int actions;
    private int affiliation;

    private Point hexTile;


    public int getAffiliation(){
        return affiliation;
    }

    public Point getHexTile(){
        return hexTile;
    }



}
