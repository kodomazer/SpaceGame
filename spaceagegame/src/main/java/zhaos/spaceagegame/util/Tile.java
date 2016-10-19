package zhaos.spaceagegame.util;

import android.widget.RelativeLayout;

/**
 * Created by kodomazer on 9/19/2016.
 */
public interface Tile {


    //Position is coordinates on the grid
    IntPoint getPosition();
    void setPosition(IntPoint newPosition);

    //Size is render size
    //Scale is in inches
    FloatPoint getSize();
    void setSize(FloatPoint newSize);

    //returns the resource ID to help the GameGUIActivity program generate the gui
    int getResourceID();


    void setParentGame(Game parent);



}
