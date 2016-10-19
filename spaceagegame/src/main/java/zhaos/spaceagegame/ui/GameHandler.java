package zhaos.spaceagegame.ui;

import android.graphics.Color;
import android.graphics.Point;
import android.media.DeniedByServerException;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import zhaos.spaceagegame.R;
import zhaos.spaceagegame.game.SpaceGame;
import zhaos.spaceagegame.game.SpaceGameHexTile;
import zhaos.spaceagegame.util.FloatPoint;
import zhaos.spaceagegame.util.IntPoint;

/**
 * Created by kodomazer on 9/27/2016.
 */
public class GameHandler extends Thread {
    private String TAG = "GameHandler";
    SpaceGameActivity parent;

    protected final float ANGLEWIDTH = 0.5f;
    protected final float CENTERWIDTH = 1f;
    protected final float HALFHEIGTH = 0.866f;

    protected float relativeAngleWidth;
    protected float relativeCenterWidth;
    protected float relativeHalfHeight;


    protected SubsectionGUI[] subsections;

    protected int xPointer;
    protected int yPointer;

    SpaceGame game;
    Map<Point,HexGUI> GUIGrid;

    RelativeLayout mainView;

    HorizontalScrollView horizontalScroll;
    ScrollView verticalScroll;

    //Density Pixel here is the number of pixels for an inch on the screen
    FloatPoint densityPixel;

    public GameHandler(SpaceGameActivity parent){
        (this).parent=parent;
        densityPixel = parent.densityPixel;
        mainView =(RelativeLayout) parent.findViewById(R.id.MainView);
        GUIGrid = new HashMap<>();
        updateScale(1);

        xPointer=-1;
        yPointer=-1;
        subsections = new SubsectionGUI[7];
        for(int i = 0;i<7;i++){
            subsections[i] = new SubsectionGUI(mainView);
            subsections[i].setSubsection(i);
        }
    }



    @Override
    public void run() {
        game = SpaceGame.getInstance();
        Bundle extras = parent.getIntent().getExtras();

        //Set Game options
        game.setRadius(extras.getInt("EXTRA_BOARD_SIZE",5));
        game.setTeamCount(extras.getInt("EXTRA_TEAM_COUNT",3));


        buildGameView();
    }

    private void buildGameView() {
        IntPoint intPosition;
        FloatPoint floatPosition;
        Point position = new Point();
        HexGUI hexGUI;
        for (SpaceGameHexTile t : game.getTiles()) {
            intPosition = t.getPosition();
            floatPosition = position(intPosition);
            position.set((int) floatPosition.x, (int) floatPosition.y);
            hexGUI = new HexGUI(mainView,
                    t,
                    new Point(position),
                    new Point((int) ((2 * ANGLEWIDTH + CENTERWIDTH) * densityPixel.y),
                            (int) (2 * HALFHEIGTH * densityPixel.y)));
            GUIGrid.put(new Point(intPosition.x,intPosition.y),hexGUI);
            mainView.addView(hexGUI,hexGUI.getParams());
            hexGUI.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    parent.setInfoText(((HexGUI)v).hexTile);
                    ((HexGUI)v).setColorFilter(Color.rgb(222,222,222));
                }
            });
            hexGUI.setClickable(false);
        }
        mainView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                xPointer = (int) event.getX();
                yPointer = (int) event.getY();
                return false;

            }

        });
        mainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: "+xPointer+","+yPointer);
                HexGUI h = GUIGrid.get(getPosition(xPointer,yPointer));
                if(h!=null) {
                    h.callOnClick();
                    for(int i = 0;i<7;i++){
                        mainView.removeView(subsections[i]);
                        mainView.addView(subsections[i],h.getParams());
                    }
                }
            }
        });
    }

    public void updateScale(float newScale){

        relativeAngleWidth = ANGLEWIDTH * newScale * densityPixel.x;
        relativeCenterWidth = CENTERWIDTH * newScale *densityPixel.x;
        relativeHalfHeight = HALFHEIGTH * newScale * densityPixel.y;
        String text = relativeAngleWidth+","+relativeCenterWidth+","
                +relativeHalfHeight;
        ((TextView)parent.findViewById(R.id.Center))
                .setText(text);
        for (HexGUI g:
             GUIGrid.values()) {
            g.updateScale(newScale);
            mainView.removeView(g);
            mainView.addView(g,g.getParams());
        }
    }


    private Point getPosition(int x,int y) {

        Point point = new Point();
        int xHalf = (int) (x / (relativeAngleWidth + relativeCenterWidth));
        int yHalf = (int) (y / (relativeHalfHeight))-1;
        boolean top = yHalf % 2 == 0;
        if (xHalf % 2 == 1) {
            yHalf += 1;
            top = !top;
        }
        point.set(xHalf, yHalf / 2);
        Log.i(TAG, "Position" + point);
        int xDiff = (int) (x - point.x * (relativeAngleWidth + relativeCenterWidth));
        int yDiff = (int) (y - (point.y *2+(point.x%2==1?0:1))* (relativeHalfHeight));
        Log.i(TAG, "xDiff: " + xDiff + "\nyDiff: " + yDiff);
        if (xDiff < relativeAngleWidth) {
            if (top) {
                //If the y offset from the local corner is higher than the edge
                if (yDiff < relativeHalfHeight * ((relativeAngleWidth) - xDiff) / relativeAngleWidth)
                    //push it up and left hex
                    point.offset(-1,point.x % 2 == 0 ? 0 :  -1);
            } else
                //if the y offset from the local corner is lower than the edge
                if (yDiff-relativeHalfHeight > xDiff * relativeHalfHeight / relativeAngleWidth)
                    //then push it down and left a hex
                    point.offset(-1, point.x % 2 == 0 ? 1 : 0);
        }
        Log.i(TAG, "Position" + point);
        return point;
    }

    private FloatPoint position(IntPoint coordinate){
        FloatPoint visiblePosition = new FloatPoint();
        visiblePosition.x = coordinate.x*(relativeCenterWidth+relativeAngleWidth);
        visiblePosition.y = (coordinate.y*2+1)*(relativeHalfHeight);
        if(coordinate.x%2==1){
            visiblePosition.y-=relativeHalfHeight;
        }
        return visiblePosition;
    }
}
