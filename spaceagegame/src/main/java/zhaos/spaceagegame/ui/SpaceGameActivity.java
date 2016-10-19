package zhaos.spaceagegame.ui;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.ZoomControls;

import zhaos.spaceagegame.R;
import zhaos.spaceagegame.game.SpaceGameHexTile;
import zhaos.spaceagegame.util.FloatPoint;

/**
 * Created by kodomazer on 9/20/2016.
 */
public class SpaceGameActivity extends Activity{
    private String TAG = "GameActivity";
    //false if testing new code
    private boolean old = true;

    FloatPoint densityPixel; //each unit is worth one inch on the screen
    GameHandler gameHandler;
    ZoomControls zoomButtons;

    protected float scale;


    protected SpaceGameMapView mapView;


    private static SpaceGameActivity instance;

    public static SpaceGameActivity getInstance() {
        return instance;
    }


    public SpaceGameActivity(){
        instance = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        densityPixel = new FloatPoint();
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        densityPixel.x = metrics.xdpi;
        densityPixel.y = metrics.ydpi;
        scale = 1;

        if(old) {
            setContentView(R.layout.empty);


            gameHandler = new GameHandler(this);
            gameHandler.run();

            zoomButtons = (ZoomControls) findViewById(R.id.zoomControls);
            zoomButtons.setOnZoomInClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    scale *= 1.25;
                    updateScale();
                }
            });
            zoomButtons.setOnZoomOutClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    scale *= 0.8;
                    updateScale();
                }
            });
        }
        else {
            if(gameHandler.game==null){
                Log.d(TAG, "onCreate: NULL GAME");
            }
            setContentView(R.layout.space_game_game);
            FrameLayout mainView =(FrameLayout) findViewById(R.id.mainGameView);
            mainView.setClipChildren(false);
            mapView = new SpaceGameMapView(this,
                    gameHandler.game,
                    new Point(metrics.widthPixels,
                            metrics.heightPixels));
            mainView.addView(mapView);
        }
    }

    public void setInfoText(SpaceGameHexTile t){
        TextView view = (TextView) findViewById(R.id.InfoPanel);
        String s = "";
        s+=t.getPosition().toString();
        view.setText(s);
    }

    private void updateScale(){
        gameHandler.updateScale(scale);
    }


}
