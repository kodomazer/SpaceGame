package zhaos.spaceagegame.ui;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.util.Log;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import zhaos.spaceagegame.R;
import zhaos.spaceagegame.game.SpaceGameHexTile;
import zhaos.spaceagegame.util.IntPoint;

/**
 * Created by bzhao on 10/4/2016.
 */
public class HexGUI extends ImageView {
    private final String TAG = "Hex GUI";
public class HexGUI extends ImageView {

    protected SpaceGameHexTile hexTile;
    protected Point position;
    protected Point size;
    protected RelativeLayout.LayoutParams params;

    protected RelativeLayout hexLayout;
    protected ImageView imageView;
    protected SubsectionGUI[] subsections;

    Bitmap hex;

    public HexGUI(RelativeLayout parent,
                  SpaceGameHexTile content,
                  Point position,
                  Point size) {
        super(parent.getContext());
        //copy pertinent information over
        hexTile = content;
        this.position = new Point(position);
        this.size = new Point(size);
        //increase the size a little to remove gaps
        this.size.offset(1, 1);
//        setBackgroundColor(Color.argb(32,0,255,0));
        setImageResource(R.mipmap.empty_hex_white);
        setColorFilter(Color.rgb(0,0,0));

        params = new RelativeLayout.LayoutParams(this.size.x,this.size.y);

        params.leftMargin = position.x;
        params.topMargin = position.y;
        Log.i(TAG, "HexGUI: HexMade");


    }

    public void setPosition(Point newPosition) {
        position = newPosition;
        params.leftMargin = position.x;
        params.topMargin = position.y;
    }

    public void updateScale(float newScale) {
        //update our copy of the parameters
        params.leftMargin = (int)(position.x*newScale);
        params.topMargin = (int)(position.y*newScale);
        params.width = (int)(size.x*newScale)+1;
        params.height = (int)(size.y*newScale)+1;
    }

    public void setActive(){
        setColorFilter(Color.rgb(255,255,0));
    }

    public void resetActive(){
        setColorFilter(Color.rgb(0,0,0));
    }

    public RelativeLayout.LayoutParams getParams() {
        return params;
    }
}
