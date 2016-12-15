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
import zhaos.spaceagegame.request.helperRequest.UnitAttackRequest;
import zhaos.spaceagegame.request.helperRequest.UnitMoveRequest;
import zhaos.spaceagegame.ui.GameUIManager;
import zhaos.spaceagegame.util.HHexDirection;

/**
 * Created by russel on 12/11/2016.
 */
class UnitInfoWrapper extends LinearLayout {
    private static String TAG = "Unit Info Wrapper";

    Point hex;
    HHexDirection subsection;

    GameUIManager parent;
    Button select;
    Button move;
    Button attack;
    private View view;
    private int unitID;

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
                parent.infoFrame.setVisibility(GONE);
                UnitMoveRequest request = new UnitMoveRequest(new Request.RequestCallback() {
                    @Override
                    public void onComplete(MyBundle info) {
                        refreshView();
                    }
                });
                request.setId(unitID);
                parent.selectingSubsection = request;
            }
        });
        move.setText("Move");
        attack = new Button(context);
        attack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                parent.infoFrame.setVisibility(GONE);
                UnitAttackRequest request = new UnitAttackRequest(new Request.RequestCallback(){
                    @Override
                    public void onComplete(MyBundle info) {
                        refreshView();
                    }
                });
                request.setId(unitID);
                parent.selectingSubsection = request;
            }
        });
        attack.setText("Attack");
        addView(select);
        addView(move);
        addView(attack);
    }

    public void setParent(final GameUIManager parent) {
        this.parent = parent;
    }

    public void updateInfo(MyBundle info) {
        int status = info.getInt(RequestConstants.UNIT_STATUS_FLAGS);
        if((status & RequestConstants.MOVABLE) != 0){
            move.setVisibility(VISIBLE);
        }
        else{
            move.setVisibility(GONE);
        }
        if((status & RequestConstants.CAN_ATTACK) != 0){
            attack.setVisibility(VISIBLE);
            //select.setVisibility(VISIBLE);
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
        unitID = info.getInt(RequestConstants.UNIT_ID);

        hex = info.getPoint(RequestConstants.HEX);
        subsection = info.getSubsection(RequestConstants.SUBSECTION);
    }

    private void refreshView(){
        //Update Subsection after move
        parent.subsectionClicked(hex,subsection);
    }

    void setView(View view){
        this.view = view;
    }
}