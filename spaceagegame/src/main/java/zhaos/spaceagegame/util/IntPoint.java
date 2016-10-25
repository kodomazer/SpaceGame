package zhaos.spaceagegame.util;

import android.graphics.Point;

/**
 * Created by kodomazer on 9/19/2016.
 */
public class IntPoint {
    public int x;
    public int y;

    public IntPoint(){
        x=0;
        y=0;
    }
    public IntPoint(int a, int b){
        x=a;
        y=b;
    }
    public IntPoint(IntPoint deepCopy){
        x = deepCopy.x;
        y= deepCopy.y;
    }

    public void translate(IntPoint by){
        translate(by.x,by.y);
    }
    public void translate(int dx,int dy){
        x+=dx;
        y+=dy;
    }

    public interface translateInterface{
        void translatePoint(Point translated);
    }

    public String toString(){
        return "(" + x + ", " + y + ")";
    }

}
