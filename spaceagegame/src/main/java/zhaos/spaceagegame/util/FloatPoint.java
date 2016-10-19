package zhaos.spaceagegame.util;

import android.graphics.Point;

import java.nio.FloatBuffer;

/**
 * Created by kodomazer on 9/25/2016.
 */
public class FloatPoint{
    public float x;
    public float y;

    public FloatPoint(){
        this(0,0);
    }

    public FloatPoint(float x,float y){
        this.x = x;
        this.y = y;
    }

    public FloatPoint(FloatPoint deepCopy){
        this.x = deepCopy.x;
        this.y = deepCopy.y;
    }
    public FloatPoint(IntPoint deepCopy){
        this.x = deepCopy.x;
        this.y = deepCopy.y;
    }
    public FloatPoint(IntPoint deepCopy,float scale){
        this.x = deepCopy.x*scale;
        this.y = deepCopy.y*scale;
    }
    public FloatPoint PiecewiseMultiply(FloatPoint scale){
        FloatPoint copy= new FloatPoint();
        copy.x = x*scale.x;
        copy.y = y*scale.y;
        return copy;
    }

    public IntPoint toIntPoint(){
        IntPoint i = new IntPoint();
        i.x = (int) this.x;
        i.y = (int) y;
        return i;
    }

}
