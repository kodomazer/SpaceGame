package zhaos.spaceagegame.util;

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


    int directionNumber;
    IntPoint.translateInterface translate;
    HHexDirection(int i){
        directionNumber = i;
    }
    public int i(){
        return directionNumber;
    }
    public void translatePoint(IntPoint i){
        translate.translatePoint(i);
    }
    public void setTranslate(IntPoint.translateInterface i){
        translate = i;
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
    public static HHexDirection rotateClockwise(HHexDirection cur){
        return getDirection((cur.i()+1)%6);
    }
    public static HHexDirection rotateCounterClockwise(HHexDirection cur){
        return getDirection((cur.i()+5)%6);
    }
    public static HHexDirection flip(HHexDirection cur){
        return getDirection((cur.i()+3)%6);
    }
}
