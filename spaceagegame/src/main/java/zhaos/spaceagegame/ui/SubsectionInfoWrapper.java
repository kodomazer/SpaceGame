package zhaos.spaceagegame.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import zhaos.spaceagegame.request.MyBundle;
import zhaos.spaceagegame.request.Request;
import zhaos.spaceagegame.request.RequestConstants;
import zhaos.spaceagegame.request.helperRequest.SubsectionInfoBase;
import zhaos.spaceagegame.request.helperRequest.SubsectionInfoRequest;
import zhaos.spaceagegame.request.helperRequest.UnitAttackRequest;
import zhaos.spaceagegame.request.helperRequest.UnitInfoRequest;
import zhaos.spaceagegame.request.helperRequest.UnitMoveRequest;
import zhaos.spaceagegame.util.HHexDirection;

/**
 * Created by russel on 12/11/2016.
 */
class SubsectionInfoWrapper extends LinearLayout {
    private static final String TAG = "Subsection Text Info";

    private GameUIManager parent;

    Point hex;
    HHexDirection subsection;
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

    public void setParent(GameUIManager parent) {
        this.parent = parent;
        unitInfoWrapper.setParent(parent);
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
        UnitInfoRequest request = new UnitInfoRequest(new Request.RequestCallback() {
            @Override
            public void onComplete(MyBundle info) {
                unitList.removeView(unitInfoWrapper);
                unitInfoWrapper.setView(view);
                unitInfoWrapper.updateInfo(info);
                unitList.addView(unitInfoWrapper,index);
            }
        });
        request.setUnitID(id);
        parent.game.sendRequest(request);
    }

    private void extraCityInfo(int id) {
        //TODO
    }



    private class CityInfoWrapper extends LinearLayout {
        public CityInfoWrapper(Context context) {
            super(context);
        }
    }
}
