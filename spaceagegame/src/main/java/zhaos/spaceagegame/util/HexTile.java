package zhaos.spaceagegame.util;

import android.support.annotation.NonNull;
import android.widget.RelativeLayout;

import zhaos.spaceagegame.R;

/**
 * Created by kodomazer on 9/19/2016.
 */
public class HexTile implements Tile {

    protected IntPoint position;
    protected HexTile[] neighbor;

    public enum Direction{
        Up(0),
        UpRight(1),
        DownRight(2),
        Down(3),
        DownLeft(4),
        UpLeft(5);
        private int i;
        Direction(int n){
            i=n;
        }
        public int i(){
            return i;
        }
    }

    public HexTile(){
        position = new IntPoint();
        neighbor = new HexTile[6];
    }
    public HexTile(@NonNull IntPoint position) {
        this();
        this.position = position;
    }

    public HexTile getNeighbor(@NonNull Direction dir){
        return neighbor[dir.i()];
    }


    @Override
    public IntPoint getPosition() {
        return null;
    }

    @Override
    public void setPosition(IntPoint newPosition) {

    }


    @Override
    public FloatPoint getSize() {
        return null;
    }

    @Override
    public void setSize(FloatPoint newSize) {

    }

    @Override
    public int getResourceID() {
        return 0;
    }


    @Override
    public void setParentGame(Game parent) {

    }
}
