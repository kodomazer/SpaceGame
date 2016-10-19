package zhaos.spaceagegame.ui;

import android.graphics.Color;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.RelativeLayout;

import zhaos.spaceagegame.game.SpaceGameLocal;
import android.content.Context;

import zhaos.spaceagegame.game.SpaceGameHexTile;

/**
 * Created by kodomazer on 10/11/2016.
 */
public class SpaceGameMapView extends RelativeLayout {
    private static String TAG = "MapView";
    protected SpaceGameLocal gameInstance;
    protected Point canvasSize;
    protected Point topLeftCorner;

    protected Point pixelsPerInch;

    protected float scale;

    public final Point hexSize = new Point(200, 173);
    public final Point hexOffset = new Point(150, 87);


    public SpaceGameMapView(Context context){
        super(context);
    }

    public SpaceGameMapView(SpaceGameActivity context, SpaceGameLocal game, Point size) {
        super(context);
        setClipChildren(false);
        gameInstance = game;
        canvasSize = new Point(size);
        int radius = game.getRadius();
        topLeftCorner = new Point(radius * hexSize.x - size.x / 2,
                radius * hexSize.y - size.y / 2);
        scale = 1;
        Log.d(TAG, "SpaceGameMapView: " + canvasSize.toString());

        DisplayMetrics metrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(metrics);

        pixelsPerInch = new Point((int) metrics.xdpi, (int) metrics.ydpi);
//        canvasSize.set(metrics.widthPixels,
//                metrics.heightPixels);
        updatePosition();


    }


    void updatePosition() {
        //If game instance hasn't been set there is nothing to render
        if (gameInstance != null) {
            //Find the top left grid location to draw
            Point topLeftGridLocation = new Point(
                    (int) (topLeftCorner.x / (hexOffset.x) / scale) - 1,
                    (int) (topLeftCorner.y / (hexOffset.y) / scale) - 1);
            Point bottomRightGridLocation = new Point(
                    (int) (topLeftGridLocation.x + canvasSize.x / (hexOffset.x) / scale) + 3,
                    (int) (topLeftGridLocation.y + canvasSize.y / (hexOffset.y) / scale) + 3);
            Log.d(TAG, "updatePosition: topleft,bottom right" + topLeftGridLocation + bottomRightGridLocation.toString());
            SpaceGameHexTile hex;
            Point current = new Point(topLeftGridLocation);
            Point relativeSize = new Point((int) (hexSize.x * scale),
                    (int) (hexSize.y * scale));
            Point relativeOffset = new Point((int) (hexOffset.x * scale),
                    (int) (hexOffset.y * scale));
            for (; current.x < bottomRightGridLocation.x; current.offset(1, 0)) {
                for (; current.y < bottomRightGridLocation.y; current.offset(0, 1)) {
                    Log.d(TAG, "updatePosition: current " + current.toString());
                    hex = gameInstance.getTile(current);
                    HexGUI gui = new HexGUI(this,
                            hex,
                            new Point((relativeOffset.x) * current.x
                                            -topLeftCorner.x,
                                    (relativeOffset.y) * current.y*2
                                            -topLeftCorner.y
                                            - (current.x % 2 == 0 ? 0 : relativeOffset.y))
                            , relativeSize);
                    this.addView(gui,gui.getParams());
                    Log.d(TAG, "updatePosition: added   " + current.toString());
                    Log.d(TAG, "updatePosition: params   " + gui.getParams().height);
                }
                current.set(current.x, topLeftGridLocation.y);
                Log.d(TAG, "updatePosition: radius " + gameInstance.getRadius());
            }
            setBackgroundColor(Color.rgb(200, 2, 2));
//            hex = gameInstance.getTile(current);
//            HexGUI gui = new HexGUI(hex,
//                    new Point((relativeSize.x - relativeOffset.x) * current.x,
//                            (relativeSize.y - relativeOffset.y) * current.y
//                                    - (current.x % 2 == 0 ? 0 : relativeOffset.y))
//                    , relativeSize);
        }
    }


}
