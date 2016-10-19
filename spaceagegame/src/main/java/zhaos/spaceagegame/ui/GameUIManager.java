package zhaos.spaceagegame.ui;

import android.graphics.Point;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import zhaos.spaceagegame.R;
import zhaos.spaceagegame.game.SpaceGameLocal;
import zhaos.spaceagegame.game.SpaceGameHexTile;
import zhaos.spaceagegame.util.FloatPoint;
import zhaos.spaceagegame.util.IntPoint;

/**
 * Created by kodomazer on 9/27/2016.
 *
 *
 */
class GameUIManager extends Thread {
    private String TAG = "UI Manager";


    private SpaceGameActivity parent;

    private final float ANGLEWIDTH = 0.5f;
    private final float CENTERWIDTH = 1f;
    private final float HALFHEIGTH = 0.866f;

    private float relativeAngleWidth;
    private float relativeCenterWidth;
    private float relativeHalfHeight;

    private HexGUI lastClick;

    private int xPosition;
    private int yPosition;

    SpaceGameLocal game;
    private Map<Point,HexGUI> GUIGrid;

    private RelativeLayout mainView;
    private TextView infoText[];

    //Density Pixel here is the number of pixels for an inch on the screen
    private FloatPoint densityPixel;

    GameUIManager(SpaceGameActivity parent){
        (this).parent=parent;
        densityPixel = parent.densityPixel;
        mainView =(RelativeLayout) parent.findViewById(R.id.MainView);
        infoText = new TextView[7];
        infoText[0]=(TextView) parent.findViewById(R.id.Center);
        infoText[1]=(TextView) parent.findViewById(R.id.Top);
        infoText[2]=(TextView) parent.findViewById(R.id.TopRight);
        infoText[3]=(TextView) parent.findViewById(R.id.BottomRight);
        infoText[4]=(TextView) parent.findViewById(R.id.Bottom);
        infoText[5]=(TextView) parent.findViewById(R.id.BottomLeft);
        infoText[6]=(TextView) parent.findViewById(R.id.TopLeft);

        GUIGrid = new HashMap<>();

        relativeAngleWidth = ANGLEWIDTH * densityPixel.x;
        relativeCenterWidth = CENTERWIDTH * densityPixel.x;
        relativeHalfHeight = HALFHEIGTH * densityPixel.y;
    }



    @Override
    public void run() {
        game = SpaceGameLocal.getInstance();
        Bundle extras = parent.getIntent().getExtras();

        //Set Game options
        game.setRadius(extras.getInt("EXTRA_BOARD_SIZE",5));
        game.setTeamCount(extras.getInt("EXTRA_TEAM_COUNT",3));


        buildGameView();
    }

    private void buildGameView(){
        IntPoint intPosition;
        Point position;
        FloatPoint pixelPosition;
        HexGUI gui;
        for(SpaceGameHexTile t:game.getTiles()) {
            intPosition = t.getPosition();
            position = new Point(intPosition.x,intPosition.y);
            pixelPosition = position(position);
            gui = new HexGUI(mainView,
                    t,
                    new Point((int) pixelPosition.x, (int) pixelPosition.y),
                    new Point((int) (2 * relativeAngleWidth + relativeCenterWidth),
                            (int) (2 * relativeHalfHeight)));
            GUIGrid.put(position, gui);

            mainView.addView(gui,gui.getParams());
            gui.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    parent.setInfoText(((HexGUI)v).hexTile);
                    if(lastClick!=null)
                    lastClick.resetActive();
                    lastClick = ((HexGUI) v);
                    lastClick.setActive();
                }
            });
            gui.setClickable(false);
        }

        //take care of touch inputs
        mainView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                xPosition = (int) event.getX();
                yPosition = (int) event.getY();

                return false;
            }
        });
        mainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Point clickPoint = getHex(xPosition,yPosition);
                infoText[0].setText(clickPoint.toString());
                HexGUI clicked = GUIGrid.get(new Point(clickPoint.x,clickPoint.y));
                if(clicked !=null){
                    clicked.performClick();}
            }
        });
    }

    void updateScale(float newScale) {
        relativeAngleWidth = ANGLEWIDTH * newScale * densityPixel.x;
        relativeCenterWidth = CENTERWIDTH * newScale * densityPixel.x;
        relativeHalfHeight = HALFHEIGTH * newScale * densityPixel.y;
        String infoString1 = "Angle Width: " + relativeAngleWidth;
        String infoString2 = "Center Width: " + relativeCenterWidth;
        String infoString3 = "Half Height" + relativeHalfHeight;
        infoText[4].setText(infoString1);
        infoText[5].setText(infoString2);
        infoText[6].setText(infoString3);
        mainView.removeAllViews();
        for (HexGUI g :
                GUIGrid.values()) {
            g.updateScale(newScale);
            mainView.addView(g, g.getParams());
        }
    }

    private Point getHex(int x,int y){
        Point point=new Point();
        boolean top=false;
        point.set((int)(x/(relativeCenterWidth+relativeAngleWidth)),
                (int)(y/(relativeHalfHeight)));
        if(point.y%2==0)
            top = true;
        Point adjustedPosition=new Point(x-(int)(point.x*(relativeAngleWidth+relativeCenterWidth)),
                y-(int)(point.y*(relativeHalfHeight)));
        if(point.x%2==0) {
            point.y -= 1;
            top=!top;
        }
        String rawString="Raw Coordinates: Point("+x+","+y+")" + "\tTop: " + top;
        infoText[1].setText(rawString);
        String coordString = "Rectangle: Point("+point.x+","+(point.y/2.)+")";
        infoText[2].setText(coordString);
        String adjustedString="Adjusted Coordinates: "+adjustedPosition.toString();
        infoText[3].setText(adjustedString);

        if(adjustedPosition.x<relativeAngleWidth){
            if(top){
                if(adjustedPosition.y<(relativeHalfHeight*(1-(adjustedPosition.x)/relativeAngleWidth)))
                    point.offset(-1,point.x%2==0?0:-1);
            }
            else{
                if(adjustedPosition.y>(relativeHalfHeight*((adjustedPosition.x)/relativeAngleWidth)))
                    point.offset(-1,point.x%2==0?1:0);

            }
        }

        point.y = point.y/2;
        return point;
    }

    private FloatPoint position(Point coordinate){
        FloatPoint visiblePosition = new FloatPoint();
        visiblePosition.x = coordinate.x*(relativeAngleWidth+relativeCenterWidth);
        visiblePosition.y = coordinate.y*2*relativeHalfHeight;
        if(coordinate.x%2==0){
            visiblePosition.y+=relativeHalfHeight;
        }
        return visiblePosition;
    }
}
