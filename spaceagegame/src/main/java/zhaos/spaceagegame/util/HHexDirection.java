package zhaos.spaceagegame.util;

import android.graphics.Point;

/**
 * Created by kodomazer on 9/26/2016.
 */
public enum HHexDirection {
    Up(1),
    UpRight(0),
    DownRight(5),
    Down(4),
    DownLeft(3),
    UpLeft(2),
    CENTER(6);


    final int directionNumber;
    TranslateInterface translate;
    HHexDirection(int i){
        directionNumber = i;
    }
    public int i(){
        return directionNumber;
    }
    public void translatePoint(Point i){
        translate.translatePoint(i);
    }
    public void setTranslate(TranslateInterface i){
        translate = i;
    }

    public HHexDirection inverse(){
        switch (directionNumber){
            case 0:
                return DownLeft;
            case 1:
                return Down;
            case 2:
                return DownRight;
            case 3:
                return UpRight;
            case 4:
                return Up;
            case 5:
                return UpLeft;
            default:
                return CENTER;
        }
    }

    public HHexDirection clockwise(){
        switch (directionNumber){
            case 0:
                return DownRight;
            case 1:
                return UpRight;
            case 2:
                return Up;
            case 3:
                return UpLeft;
            case 4:
                return DownLeft;
            case 5:
                return Down;
            default:
                return CENTER;
        }
    }

    public HHexDirection counterClockwise(){
        switch (directionNumber){
            case 0:
                return Up;
            case 1:
                return UpLeft;
            case 2:
                return DownLeft;
            case 3:
                return Down;
            case 4:
                return DownRight;
            case 5:
                return UpRight;
            default:
                return CENTER;
        }
    }

    public static HHexDirection getDirection(int i){
        switch(i){
            case 0:
                return UpRight;
            case 5:
                return DownRight;
            case 4:
                return Down;
            case 3:
                return DownLeft;
            case 2:
                return UpLeft;
            case 1:
                return Up;
            default:
                return CENTER;
        }
    }

    public static String toString(HHexDirection direction){
        switch (direction){
            case Up:
                return "Up";
            case Down:
                return "Down";
            case UpLeft:
                return "Up Left";
            case UpRight:
                return "Up Right";
            case DownLeft:
                return "Down Left";
            case DownRight:
                return "Down Right";
            case CENTER:
                return "Center";
            default:
                return "None";
        }
    }

    public static String toString(int direction){
        return toString(getDirection(direction));
    }

    public static HHexDirection flip(HHexDirection cur){
        return getDirection((cur.i()+3)%6);
    }

    public interface TranslateInterface {
        void translatePoint(Point point);
    }
}
