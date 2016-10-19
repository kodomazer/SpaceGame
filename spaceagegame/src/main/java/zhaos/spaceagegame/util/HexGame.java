package zhaos.spaceagegame.util;

import android.support.v4.util.ArrayMap;

import java.util.Map;

/**
 * Created by kodomazer on 9/19/2016.
 */
public abstract class HexGame<T extends HexTile> implements Game {
    protected IntPoint size;
    protected Map<IntPoint,T> Grid;

    public HexGame(IntPoint size){
        this.size =size;
        Grid = new ArrayMap<>();
        for(int i = 0;i<size.x;i++){
            for(int j = 0;j<size.y;j++){
                if(i%2==1)
                    if(j==size.y-1)
                        break;
                IntPoint spot = new IntPoint(i,j);
                Grid.put(spot,null);
            }
        }
    }
    public HexGame(int radius){
        this.size.x =radius*2;
        this.size.y = radius*4;
        Grid = new ArrayMap<>();
        //start from the innermost ring
        for(int i = 1;i<radius;i++){
            //Start from the right corner and move clockwise
            for(int j = 0;j<i*6;j++){
                IntPoint spot = new IntPoint(i,j);
                Grid.put(spot,null);
            }
        }
    }





    @Override
    public Tile getTile(IntPoint position) {
        return Grid.get(position);
    }

    @Override
    public IntPoint getSize() {
        return size;
    }
}
