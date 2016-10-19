package zhaos.spaceagegame.ui;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.ZoomControls;

import zhaos.spaceagegame.R;
import zhaos.spaceagegame.game.GameHexTile;
import zhaos.spaceagegame.util.FloatPoint;

/**
 * Created by kodomazer on 9/20/2016.
 */
public class GameGUIActivity extends Activity{
    FloatPoint densityPixel; //each unit is worth one inch on the screen
    GameHandler gameHandler;
    ZoomControls zoomButtons;

    protected float scale;

    private static GameGUIActivity instance;

    public static GameGUIActivity getInstance() {
        return instance;
    }


    public GameGUIActivity(){
        instance = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.empty);

        densityPixel = new FloatPoint();
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        densityPixel.x= metrics.xdpi;
        densityPixel.y= metrics.ydpi;
        scale = 1;

        gameHandler = new GameHandler(this);
        gameHandler.run();

        zoomButtons = (ZoomControls) findViewById(R.id.zoomControls);
        zoomButtons.setOnZoomInClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Zoom Control", "onClick: IN"+scale);
                scale*=1.25;
                updateScale();
            }
        });
        zoomButtons.setOnZoomOutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scale*=0.8;
                updateScale();
            }
        });
    }

    public void setInfoText(GameHexTile t){
        TextView view = (TextView) findViewById(R.id.InfoPanel);
        String s = "";
        s+=t.getPosition().toString();
        view.setText(s);
    }

    private void updateScale(){
        gameHandler.updateScale(scale);
    }


}
