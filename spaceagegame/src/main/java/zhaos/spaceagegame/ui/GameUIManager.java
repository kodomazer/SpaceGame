package zhaos.spaceagegame.ui;

import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import zhaos.spaceagegame.R;
import zhaos.spaceagegame.request.MyBundle;
import zhaos.spaceagegame.request.Request;
import zhaos.spaceagegame.request.RequestConstants;
import zhaos.spaceagegame.request.helperRequest.EndTurnRequest;
import zhaos.spaceagegame.request.helperRequest.HexInfoRequest;
import zhaos.spaceagegame.request.helperRequest.SubsectionInfoBase;
import zhaos.spaceagegame.request.helperRequest.SubsectionInfoRequest;
import zhaos.spaceagegame.spaceGame.LocalGame;
import zhaos.spaceagegame.spaceGame.map.HexTile;
import zhaos.spaceagegame.ui.textInfoView.SubsectionInfoWrapper;
import zhaos.spaceagegame.util.FloatPoint;
import zhaos.spaceagegame.util.HHexDirection;

/**
 * Created by kodomazer on 9/27/2016.
 *
 *
 */
public class GameUIManager implements Runnable {
    private String TAG = "UI Manager";

    Handler mainHandler;
    private SpaceGameActivity parent;

    private final float ANGLEWIDTH = 0.5f;
    private final float CENTERWIDTH = 1f;
    private final float HALFHEIGTH = 0.866f;

    private float relativeAngleWidth;
    private float relativeCenterWidth;
    private float relativeHalfHeight;

    private HexGUI lastClick;
    private SubsectionGroup subsectionGroup;

    private int xPosition;
    private int yPosition;

    public LocalGame game;
    private Map<Point,HexGUI> GUIGrid;

    private RelativeLayout mainView;
    private TextView infoText[];
    public FrameLayout infoFrame;
    private SubsectionInfoWrapper subsectionInfo;
    private LinearLayout hexInfo;

    public SubsectionInfoBase selectingSubsection;



    //Density Pixel here is the number of pixels for an inch on the screen
    private FloatPoint densityPixel;

    GameUIManager(SpaceGameActivity parent){
        (this).parent=parent;
        mainHandler = new Handler(parent.getMainLooper());
        densityPixel = parent.densityPixel;
        mainView =(RelativeLayout) parent.findViewById(R.id.MainView);

        //initialize infoText array
        infoText = new TextView[7];
        infoText[6]=(TextView) parent.findViewById(R.id.Center);
        infoText[0]=(TextView) parent.findViewById(R.id.Top);
        infoText[1]=(TextView) parent.findViewById(R.id.TopRight);
        infoText[2]=(TextView) parent.findViewById(R.id.BottomRight);
        infoText[3]=(TextView) parent.findViewById(R.id.Bottom);
        infoText[4]=(TextView) parent.findViewById(R.id.BottomLeft);
        infoText[5]=(TextView) parent.findViewById(R.id.TopLeft);

        //initialize text info
        infoFrame = (FrameLayout)parent.findViewById(R.id.InfoView);
        subsectionInfo = new SubsectionInfoWrapper(parent);
        hexInfo = (LinearLayout) parent.findViewById(R.id.hexInfoBase);

        subsectionInfo.setParent(this);

        Button endTurn = (Button) parent.findViewById(R.id.end_turn);
        endTurn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EndTurnRequest request = new EndTurnRequest(new Request.RequestCallback(){
                    @Override
                    public void onComplete(MyBundle info) {
                        //possibly put in a request to refresh view in here
                    }
                });
                game.sendRequest(request);
            }
        });

        selectingSubsection = null;

        GUIGrid = new HashMap<>();


        relativeAngleWidth = ANGLEWIDTH * densityPixel.x;
        relativeCenterWidth = CENTERWIDTH * densityPixel.x;
        relativeHalfHeight = HALFHEIGTH * densityPixel.y;
    }



    @Override
    public void run() {
        game = LocalGame.getInstance();
        game.setHandler(mainHandler);
        Bundle extras = parent.getIntent().getExtras();

        if(!game.running()) {
            //Set Game options
            game.setRadius(extras.getInt("EXTRA_BOARD_SIZE", 5));
            game.setTeamCount(extras.getInt("EXTRA_TEAM_COUNT", 3));
            game.execute();
        }
        buildGameView();
    }

    private void buildGameView(){
        Point position;
        FloatPoint pixelPosition;
        HexGUI gui;
        for(HexTile t:game.getTiles()) {
            position = t.getPosition();
            pixelPosition = topLeftCornerPosition(position);
            gui = new HexGUI(
                    mainView,
                    t,
                    new Point(
                            (int) pixelPosition.x,
                            (int) pixelPosition.y),
                    new Point(
                            (int) (2 * relativeAngleWidth + relativeCenterWidth),
                            (int) (2 * relativeHalfHeight)));
            GUIGrid.put(position, gui);

            mainView.addView(gui,gui.getParams());
            gui.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    infoFrame.removeAllViews();
                    infoFrame.addView(hexInfo);
                    if(lastClick!=null)
                    lastClick.resetActive();
                    lastClick = ((HexGUI) v);
                    lastClick.setActive();
                    parent.setInfoText(lastClick.hexTile);
                    String info;

                    for(int i = 0;i<7;i++){
                        info = HHexDirection.toString(i);
                        infoText[i].setText(info);
                    }
                }
            });
            gui.setClickable(false);
        }

        //Remember where was last touched
        mainView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                xPosition = (int) event.getX();
                yPosition = (int) event.getY();

                return false;
            }
        });
        //Gets notified if there was actually a click
        mainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMapHexClicked();

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

    private Point findClickedHex(int x, int y){
        Point point=new Point();
        boolean top=false;
        point.set((int)(x/(relativeCenterWidth+relativeAngleWidth)),
                (int)(y/(relativeHalfHeight)));
        if(point.y%2==0)
            top = true;
        Point adjustedPosition= new Point(
                x-(int)(point.x*(relativeAngleWidth+relativeCenterWidth)),
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
                if(adjustedPosition.y<(relativeHalfHeight*
                        (1-(adjustedPosition.x)/relativeAngleWidth)))
                    point.offset(-1,point.x%2==0?0:-1);
            }
            else{
                if(adjustedPosition.y>(relativeHalfHeight*
                        ((adjustedPosition.x)/relativeAngleWidth)))
                    point.offset(-1,point.x%2==0?1:0);

            }
        }

        point.y = point.y/2;
        return point;
    }

    private HHexDirection findClickedSubsection
            (int xPosition, int yPosition, Point clickPoint) {
        HHexDirection direction;
        final Point CENTER = new Point(
                (int)(relativeAngleWidth+relativeCenterWidth/2),
                (int)relativeHalfHeight);
        Point relativePosition = new Point((int)(xPosition
                -clickPoint.x*(relativeAngleWidth+relativeCenterWidth)),
                (int)(yPosition - (clickPoint.y*2+(clickPoint.x%2==0?1:0))
                        *relativeHalfHeight));
        boolean top = true;
        boolean left = true;
        boolean topSide;
        if(relativePosition.y>CENTER.y) {
            top = false;
            relativePosition.y -= CENTER.y;
        }
        //Left or Right
        if(relativePosition.x>CENTER.x){
            left = false;
            relativePosition.x-=CENTER.x;
        }
        else{
            relativePosition.x-=relativeAngleWidth;
        }
        if(left^top){
            topSide = relativePosition.y<
                    ((1-((float)relativePosition.x/relativeCenterWidth*2))
                    *relativeHalfHeight);
        }
        else{
            topSide = relativePosition.y<
                    (relativePosition.x/relativeCenterWidth*2
                    *relativeHalfHeight);
        }
        switch((top?1:0)*0b100+(left?1:0)*0b010+(topSide?1:0)){
            case 0b000:
            case 0b010:
                direction = HHexDirection.Down;
                break;
            case 0b001:
                direction = HHexDirection.DownRight;
                break;
            case 0b011:
                direction = HHexDirection.DownLeft;
                break;
            case 0b101:
            case 0b111:
                direction = HHexDirection.Up;
                break;
            case 0b100:
                direction = HHexDirection.UpRight;
                break;
            case 0b110:
                direction = HHexDirection.UpLeft;
                break;
            default:
                direction = HHexDirection.CENTER;
        }
        return direction;
    }

    private void onMapHexClicked() {
        Point clickPoint = findClickedHex(xPosition, yPosition);
        HHexDirection subsection = findClickedSubsection(xPosition,yPosition,clickPoint);

//        if(clickPoint.equals(subsectionGroup.getHexPosition()))

        if(selectingSubsection!=null){
            selectingSubsection.setHex(clickPoint);
            selectingSubsection.setSubsection(subsection);

            game.sendRequest(selectingSubsection);

            selectingSubsection = null;
            infoFrame.setVisibility(View.VISIBLE);
        }
        else {
            //debug string
            infoText[0].setText(clickPoint.toString());

            HexGUI clicked = GUIGrid.get(clickPoint);
            if (clicked == null) return;
            clicked.performClick();

            HexInfoRequest request= new HexInfoRequest(new Request.RequestCallback() {
                @Override
                public void onComplete(MyBundle info) {
                    handleTextHexInfo(info);
                }
            });
            request.setHex(clickPoint);
            request.getThisRequest();
            //Function to build UI once the info is returned
            game.sendRequest(request);
        }
    }


    private void handleTextHexInfo(MyBundle info){
        if (info == null) return;
        infoFrame.removeAllViews();
        infoFrame.addView(hexInfo);
        ArrayList<MyBundle> subsectionList =
                        info.getArrayList(RequestConstants.SUBSECTION_LIST);
        for (MyBundle subsectionBundle : subsectionList) {
            //TODO Make Subsection groups to GUI view
            final Point hex = subsectionBundle
                    .getPoint(RequestConstants.ORIGIN_HEX);
            final HHexDirection subsection = subsectionBundle
                    .getSubsection(RequestConstants.ORIGIN_SUBSECTION);
            String text = subsection.toString() + ": \nFaction: " +
                    subsectionBundle.getInt(RequestConstants.FACTION_ID);
            infoText[subsection.i()]
                    .setText(text);
            infoText[subsection.i()]
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            subsectionClicked(hex,subsection);

                        }
                    });
        }
    }

    public void subsectionClicked(Point hex, HHexDirection subsection) {
        SubsectionInfoBase request = new SubsectionInfoRequest(new Request.RequestCallback() {
            @Override
            public void onComplete(MyBundle info) {
                subsectionInfoCallback(info);
            }
        });
        request.setHex(hex);
        request.setSubsection(subsection);
        game.sendRequest(request);
    }

    public void subsectionInfoCallback(MyBundle info) {
        if(info==null)return;
        if(!info.getBoolean(RequestConstants.SUCCESS))return;
        subsectionInfo.setInfo(info);
        infoFrame.removeAllViews();
        infoFrame.addView(subsectionInfo);

    }

    private FloatPoint topLeftCornerPosition(Point coordinate){
        FloatPoint visiblePosition = new FloatPoint();
        visiblePosition.x = coordinate.x*(relativeAngleWidth+relativeCenterWidth);
        visiblePosition.y = coordinate.y*2*relativeHalfHeight;
        if(coordinate.x%2==0){
            visiblePosition.y+=relativeHalfHeight;
        }
        return visiblePosition;
    }


}
