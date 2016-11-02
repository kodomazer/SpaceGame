package zhaos.spaceagegame.ui;

import android.content.Context;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import zhaos.spaceagegame.util.HHexDirection;
import zhaos.spaceagegame.util.MyBundle;
import zhaos.spaceagegame.util.RequestConstants;

/**
 * Created by kodomazer on 10/27/2016.
 */

class SubsectionInfoWrapper extends LinearLayout {
    private static final String TAG = "Subsection Text Info";
    //City Section
    TextView cityHeader;
    TextView cityInfo;

    //Unit Section
    TextView unitHeader;
    LinearLayout unitList;



    public SubsectionInfoWrapper(Context context) {
        super(context);
        setOrientation(VERTICAL);
        cityHeader = new TextView(context);
        cityHeader.setText("Space Station:");
        addView(cityHeader);
        cityInfo = new TextView(context);
        addView(cityInfo);
        unitHeader = new TextView(context);
        unitHeader.setText("Units:");
        addView(unitHeader);
        unitList = new LinearLayout(context);
        addView(unitList);

    }

    public void setInfo(MyBundle subsectionInfo){
        MyBundle spaceStation = subsectionInfo.getBundle(RequestConstants.SPACE_STATION_INFO);
        if(subsectionInfo.getSubsection(RequestConstants.SUBSECTION)== HHexDirection.CENTER) {
            cityHeader.setVisibility(VISIBLE);
            cityInfo.setVisibility(VISIBLE);
            if (spaceStation == null) {
                cityInfo.setText("No City");
            } else {
                int level = spaceStation.getInt(RequestConstants.LEVEL);
                cityInfo.setText("City level: " + level);
            }
        }
        else{
            cityHeader.setVisibility(INVISIBLE);
            cityInfo.setVisibility(INVISIBLE);
        }

        ArrayList<MyBundle> units =
                subsectionInfo.getArrayList(RequestConstants.UNIT_LIST);

        unitList.removeAllViews();
        if(units!=null){
            Log.i(TAG, "setInfo: "+units.size());
            for(MyBundle unit: units){
                TextView unitText = new TextView(getContext());
                unitText.setText("Unit level: " + unit.getInt(RequestConstants.LEVEL));
                unitList.addView(unitText);
            }
        }
        else {
            TextView unitText = new TextView(getContext());
            unitText.setText("No Units");
            unitList.addView(unitText);
        }


    }



}
