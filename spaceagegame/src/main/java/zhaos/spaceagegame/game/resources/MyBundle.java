package zhaos.spaceagegame.game.resources;

import android.graphics.Point;
import android.util.ArrayMap;

import java.util.ArrayList;

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

    public MyBundle getBundle(String name){
        return (MyBundle)map.get(name);
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

    public HHexDirection getSubsection(String name) {
        return HHexDirection.getDirection(getInt(name));
    }

    public boolean getBoolean(String name){
        return (boolean)map.get(name);
    }

    //Setters for encoding
    public void putArrayList(String name, ArrayList list){
        map.put(name,list);
    }

    public void putBundle(String name, MyBundle bundle){
        map.put(name,bundle);
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

    public void putBoolean(String name, boolean b) {
        map.put(name,b);
    }
}
