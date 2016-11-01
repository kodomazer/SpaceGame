package zhaos.spaceagegame.ui;

import android.content.Context;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import zhaos.spaceagegame.game.resources.MyBundle;
import zhaos.spaceagegame.game.resources.RequestConstants;

/**
 * Created by kodomazer on 10/27/2016.
 */

class SubsectionInfoWrapper extends LinearLayout {
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
        if(spaceStation == null){
            cityInfo.setText("No City");
        }
        else {
            int level = spaceStation.getInt(RequestConstants.LEVEL);
            cityInfo.setText("City level: " + level);
        }

        ArrayList<MyBundle> units =
                subsectionInfo.getArrayList(RequestConstants.UNIT_LIST);

        unitList.removeAllViews();
        if(units!=null)
            for(MyBundle unit: units){
                TextView unitText = new TextView(getContext());
                unitText.setText("Unit level: " + unit.getInt(RequestConstants.LEVEL));
                unitList.addView(unitText);
            }


    }



}
