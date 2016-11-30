package zhaos.spaceagegame.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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
import zhaos.spaceagegame.game.SpaceGameLocal;
import zhaos.spaceagegame.game.SpaceGameHexTile;
import zhaos.spaceagegame.util.MyBundle;
import zhaos.spaceagegame.util.Request;
import zhaos.spaceagegame.util.RequestConstants;
import zhaos.spaceagegame.util.FloatPoint;
import zhaos.spaceagegame.util.HHexDirection;

/**
 * Created by kodomazer on 9/27/2016.
 *
 *
 */
class GameUIManager implements Runnable {
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

    SpaceGameLocal game;
    private Map<Point,HexGUI> GUIGrid;

    private RelativeLayout mainView;
    private TextView infoText[];
    private FrameLayout infoFrame;
    private SubsectionInfoWrapper subsectionInfo;
    private LinearLayout hexInfo;

    private int selectingSubsection;



    //Density Pixel here is the number of pixels for an inch on the screen
    private FloatPoint densityPixel;

    GameUIManager(SpaceGameActivity parent){
        (this).parent=parent;
        mainHandler = new Handler(parent.getMainLooper());
        densityPixel = parent.densityPixel;
        mainView =(RelativeLayout) parent.findViewById(R.id.MainView);

        //initialize infoText array
        infoText = new TextView[7];
        infoText[0]=(TextView) parent.findViewById(R.id.Center);
        infoText[1]=(TextView) parent.findViewById(R.id.Top);
        infoText[2]=(TextView) parent.findViewById(R.id.TopRight);
        infoText[3]=(TextView) parent.findViewById(R.id.BottomRight);
        infoText[4]=(TextView) parent.findViewById(R.id.Bottom);
        infoText[5]=(TextView) parent.findViewById(R.id.BottomLeft);
        infoText[6]=(TextView) parent.findViewById(R.id.TopLeft);

        //initialize text info
        infoFrame = (FrameLayout)parent.findViewById(R.id.InfoView);
        subsectionInfo = new SubsectionInfoWrapper(parent);
        hexInfo = (LinearLayout) parent.findViewById(R.id.hexInfoBase);

        selectingSubsection = 0;

        GUIGrid = new HashMap<>();


        relativeAngleWidth = ANGLEWIDTH * densityPixel.x;
        relativeCenterWidth = CENTERWIDTH * densityPixel.x;
        relativeHalfHeight = HALFHEIGTH * densityPixel.y;
    }



    @Override
    public void run() {
        game = SpaceGameLocal.getInstance();
        game.setHandler(mainHandler);
        Bundle extras = parent.getIntent().getExtras();

        //Set Game options
        game.setRadius(extras.getInt("EXTRA_BOARD_SIZE",5));
        game.setTeamCount(extras.getInt("EXTRA_TEAM_COUNT",3));
        game.execute();

        buildGameView();
    }

    private void buildGameView(){
        Point position;
        FloatPoint pixelPosition;
        HexGUI gui;
        for(SpaceGameHexTile t:game.getTiles()) {
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
        HHexDirection direction = null;
        final Point CENTER = new Point(
                (int)(relativeAngleWidth+relativeCenterWidth/2),
                (int)relativeHalfHeight);
        Point relativePosition = new Point((int)(xPosition
                -clickPoint.x*(relativeAngleWidth+relativeCenterWidth)),
                (int)(yPosition - (clickPoint.y*2-(xPosition%2==1?0:1))
                        *relativeHalfHeight));
        boolean top = true;
        boolean left = true;
        boolean topSide = false;
        if(relativePosition.y>CENTER.y){
            top = false;
            relativePosition.y-=CENTER.y;
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
            case 0b011:
                direction = HHexDirection.Down;
                break;
            case 0b001:
                direction = HHexDirection.DownRight;
                break;
            case 0b010:
                direction = HHexDirection.DownLeft;
                break;
            case 0b100:
            case 0b111:
                direction = HHexDirection.Up;
                break;
            case 0b101:
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

        if(selectingSubsection!=0){
            MyBundle request = new MyBundle();
            request.putInt(RequestConstants.INSTRUCTION,selectingSubsection);

            request.putPoint(RequestConstants.DESTINATION_HEX,clickPoint);
            request.putSubsection(RequestConstants.DESTINATION_SUBSECTION,subsection);

            selectingSubsection = 0;
            infoFrame.setVisibility(View.VISIBLE);
        }
        else {
            //debug string
            infoText[0].setText(clickPoint.toString());

            HexGUI clicked = GUIGrid.get(clickPoint);
            if (clicked == null) return;
            clicked.performClick();

            //Build Request
            MyBundle request = new MyBundle();
            request.putInt(
                    RequestConstants.INSTRUCTION,
                    RequestConstants.HEX_INFO);
            request.putPoint(
                    RequestConstants.ORIGIN_HEX,
                    clickPoint);
            //Function to build UI once the info is returned
            game.sendRequest(new Request(request, new Request.RequestCallback() {
                @Override
                public void onComplete(MyBundle info) {
                    handleTextHexInfo(info);
                }
            }));
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

    private void subsectionClicked(Point hex, HHexDirection subsection) {
        MyBundle request = new MyBundle();
        request.putInt(RequestConstants.INSTRUCTION,RequestConstants.SUBSECTION_INFO);
        request.putPoint(RequestConstants.ORIGIN_HEX,hex);
        request.putSubsection(RequestConstants.ORIGIN_SUBSECTION,subsection);
        game.sendRequest(new Request(request, new Request.RequestCallback() {
            @Override
            public void onComplete(MyBundle info) {
                subsectionInfoCallback(info);
            }
        }));
    }

    private void subsectionInfoCallback(MyBundle info) {
        if(info==null)return;
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
    public class SubsectionInfoWrapper extends LinearLayout {
        private static final String TAG = "Subsection Text Info";
        //City Section
        TextView cityHeader;
        TextView cityInfo;
        CityInfoWrapper cityInfoWrapper;

        //Unit Section
        TextView unitHeader;
        LinearLayout unitList;
        UnitInfoWrapper unitInfoWrapper;


        public SubsectionInfoWrapper(Context context) {
            super(context);
            setOrientation(VERTICAL);
            cityHeader = new TextView(context);
            cityHeader.setText("Space Station:");
            addView(cityHeader);
            cityInfo = new TextView(context);
            addView(cityInfo);

            //Units
            unitHeader = new TextView(context);
            unitHeader.setText("Units:");
            addView(unitHeader);

            unitList = new LinearLayout(context);
            unitList.setOrientation(VERTICAL);
            addView(unitList);

            //Unit details
            unitInfoWrapper = new UnitInfoWrapper(context);
        }

        public void setInfo(MyBundle subsectionInfo){
            MyBundle spaceStation =
                    subsectionInfo.getBundle(RequestConstants.SPACE_STATION_INFO);

            if(subsectionInfo
                    .getSubsection(RequestConstants.SUBSECTION)== HHexDirection.CENTER) {
                cityHeader.setVisibility(VISIBLE);
                cityInfo.setVisibility(VISIBLE);
                if (spaceStation == null) {
                    cityInfo.setText("No City");
                } else {
                    final int id = spaceStation.getInt(RequestConstants.SPACE_STATION_ID);
                    int level = spaceStation.getInt(RequestConstants.LEVEL);
                    cityInfo.setText("City level: " + level);
                    cityInfo.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            extraCityInfo(id);
                        }
                    });
                }
            }
            else{
                cityHeader.setVisibility(GONE);
                cityInfo.setVisibility(GONE);
            }

            ArrayList<MyBundle> units =
                    subsectionInfo.getArrayList(RequestConstants.UNIT_LIST);

            unitList.removeAllViews();
            if(units!=null&&units.size()!=0){
                Log.i(TAG, "setInfo: "+units.size());
                int index = 0;
                for(MyBundle unit: units){
                    final int id = unit.getInt(RequestConstants.UNIT_ID);
                    final int in = index;
                    TextView unitText = new TextView(getContext());
                    unitText.setText("Unit level: " + unit.getInt(RequestConstants.LEVEL));
                    unitText.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            extraUnitInfo(id,in,v);
                        }
                    });
                    unitList.addView(unitText);
                }
            }
            else {
                TextView unitText = new TextView(getContext());
                unitText.setText("No Units");
                unitList.addView(unitText);
            }


        }

        private void extraUnitInfo(int id,final int index,final View view) {
            MyBundle request = new MyBundle();
            request.putInt(RequestConstants.INSTRUCTION,RequestConstants.UNIT_INFO);
            request.putInt(RequestConstants.UNIT_ID,id);
            game.sendRequest(new Request(request, new Request.RequestCallback() {
                @Override
                public void onComplete(MyBundle info) {
                    unitList.removeView(unitInfoWrapper);
                    unitInfoWrapper.setView(view);
                    unitInfoWrapper.updateInfo(info);
                    unitList.addView(unitInfoWrapper);
                }
            }));
        }

        private void extraCityInfo(int id) {
            //TODO
        }

        private class UnitInfoWrapper extends LinearLayout{
            Button select;
            Button move;
            Button attack;
            private View view;

            public UnitInfoWrapper(Context context) {
                super(context);
                select = new Button(context);
                select.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //TODO send request to select this unit
                    }
                });
                select.setText("Select");
                move = new Button(context);
                move.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        infoFrame.setVisibility(GONE);
                        selectingSubsection = RequestConstants.UNIT_MOVE;

                    }
                });
                move.setText("Move");
                attack = new Button(context);
                attack.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        infoFrame.setVisibility(GONE);
                        selectingSubsection = RequestConstants.UNIT_ATTACK;

                    }
                });
                attack.setText("Attack");
                addView(select);
                addView(move);
                addView(attack);
            }


            public void updateInfo(MyBundle info) {
                int status = info.getInt(RequestConstants.UNIT_STATUS_FLAGS);
                if((status & RequestConstants.MOVEABLE) != 0){
                    move.setVisibility(VISIBLE);
                }
                else{
                    move.setVisibility(GONE);
                }
                if((status & RequestConstants.CAN_ATTACK) != 0){
                    attack.setVisibility(VISIBLE);
                    select.setVisibility(VISIBLE);
                    select.setText("Select");
                }
                else{
                    attack.setVisibility(GONE);
                    select.setVisibility(GONE);
                }
                if((status & RequestConstants.SELECTED) != 0){
                    view.setBackgroundColor(Color.argb(100,100,100,100));
                    select.setVisibility(VISIBLE);
                    select.setText("Deselect");

                }
                else{
                    view.setBackgroundColor(Color.argb(0,255,255,255));
                }
            }

            void setView(View view){
                this.view = view;
            }
        }

        private class CityInfoWrapper extends LinearLayout {
            public CityInfoWrapper(Context context) {
                super(context);
            }
        }
    }


}
