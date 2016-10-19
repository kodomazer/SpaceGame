package zhaos.spaceagegame.ui;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.HashMap;
import java.util.Map;

import zhaos.spaceagegame.R;
import zhaos.spaceagegame.game.GameHexTile;
import zhaos.spaceagegame.game.SpaceGame;
import zhaos.spaceagegame.util.FloatPoint;
import zhaos.spaceagegame.util.IntPoint;

/**
 * Created by kodomazer on 9/27/2016.
 */
public class GameHandler extends Thread {
    GameGUIActivity parent;

    protected final float ANGLEWIDTH = 0.15f;
    protected final float CENTERWIDTH = 0.3f;
    protected final float HALFHEIGTH = 0.25f;

    SpaceGame game;
    Map<IntPoint,HexGUI> GUIGrid;

    RelativeLayout mainView;

    //Density Pixel here is the number of pixels for an inch on the screen
    FloatPoint densityPixel;

    public GameHandler(GameGUIActivity parent){
        (this).parent=parent;
        densityPixel = parent.densityPixel;
        mainView =(RelativeLayout) parent.findViewById(R.id.MainView);
        GUIGrid = new HashMap<>();
    }



    @Override
    public void run() {
        game = SpaceGame.getInstance();
        Bundle extras = parent.getIntent().getExtras();

        //Set Game options
        game.setSize(extras.getInt("EXTRA_BOARD_SIZE",5));
        game.setTeamCount(extras.getInt("EXTRA_TEAM_COUNT",3));


        buildGameView();
    }

    private void buildGameView(){
        IntPoint position;
        for(GameHexTile t:game.getTiles()){
            position = t.getPosition();
            GUIGrid.put(position,
            new HexGUI(mainView,
                    t,
                    position(position).PiecewiseMultiply(densityPixel).toIntPoint(),
                    new FloatPoint(2*ANGLEWIDTH+CENTERWIDTH,
                            2*HALFHEIGTH).PiecewiseMultiply(densityPixel).toIntPoint())
            );
        }
    }

    public void updateScale(float newScale){
        for (HexGUI g:
             GUIGrid.values()) {
            g.updateScale(newScale);
        }
    }


    private FloatPoint position(IntPoint coordinate){
        FloatPoint visiblePosition = new FloatPoint();
        visiblePosition.x = coordinate.x*(ANGLEWIDTH+CENTERWIDTH);
        visiblePosition.y = coordinate.y*2*HALFHEIGTH;
        if(coordinate.x%2==0){
            visiblePosition.y+=HALFHEIGTH;
        }
        return visiblePosition;
    }
}
