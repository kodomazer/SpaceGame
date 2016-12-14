package zhaos.spaceagegame.ui.textInfoView;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import zhaos.spaceagegame.request.MyBundle;
import zhaos.spaceagegame.request.Request;
import zhaos.spaceagegame.request.RequestConstants;
import zhaos.spaceagegame.request.helperRequest.EntityProductionRequest;
import zhaos.spaceagegame.request.helperRequest.UnitMoveRequest;
import zhaos.spaceagegame.ui.GameUIManager;
import zhaos.spaceagegame.util.HHexDirection;

/**
 * City Info Wrapper
 *
 * Used in Subsection Info Wrapper to give extra
 */

class CityInfoWrapper extends LinearLayout {
    GameUIManager parent;

    Point hex;
    HHexDirection subsection;

    private int cityID;

    Button produceUnit;

    public CityInfoWrapper(Context context) {
        super(context);

        produceUnit = new Button(context);
        produceUnit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                EntityProductionRequest request = new EntityProductionRequest(new Request.RequestCallback() {
                    @Override
                    public void onComplete(MyBundle info) {
                        //Update Subsection after move
                        parent.subsectionClicked(hex,subsection);
                    }
                });
                request.setID(cityID);
                parent.game.sendRequest(request);
            }
        });
        produceUnit.setText("Produce Unit");
        addView(produceUnit);
    }

    public void setParent(final GameUIManager parent) {
        this.parent = parent;
    }

    public void updateInfo(MyBundle info) {
        int status = info.getInt(RequestConstants.CITY_STATUS_FLAGS);

        if((status&RequestConstants.CAN_PRODUCE_UNIT)!=0){
            produceUnit.setVisibility(VISIBLE);
        }
        else{
            produceUnit.setVisibility(GONE);
        }
        cityID = info.getInt(RequestConstants.SPACE_STATION_ID);
        hex = info.getPoint(RequestConstants.HEX);
        subsection = info.getSubsection(RequestConstants.SUBSECTION);
    }
}