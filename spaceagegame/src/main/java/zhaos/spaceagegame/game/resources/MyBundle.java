package zhaos.spaceagegame.game.resources;

import android.graphics.Point;
import android.os.Bundle;
import android.util.ArrayMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import zhaos.spaceagegame.util.HHexDirection;

/**
 * Created by kodomazer on 10/24/2016.
 * Needed to implement my own data type getter and setters
 */

public class MyBundle {
    ArrayMap<String,Object> map;

    public MyBundle(){
        map = new ArrayMap<>();
    }

    //Getters for decoding
    public ArrayList getArrayList(String name){
        return (ArrayList)map.get(name);
    }

    public int getInt(String name,int defaultValue){
        return (Integer)map.get(name);
    }

    public int getInt(String name){
        return getInt(name,-1);
    }

    public Point getPoint(String name){
        return (Point) map.get(name);
    }

    //Setters for encoding
    public void putArrayList(String name, ArrayList list){
        map.put(name,list);

    }

    public void putPoint(String name,Point value){
        map.put(name,value);
    }

    public void putSubsection(String name,HHexDirection subsection){
        putInt(name,subsection.i());
    }

    public void putInt(String name, int value){
        map.put(name,value);
    }
}
