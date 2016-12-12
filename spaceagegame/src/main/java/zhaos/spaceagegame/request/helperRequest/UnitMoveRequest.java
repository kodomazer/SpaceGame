package zhaos.spaceagegame.request.helperRequest;

import android.graphics.Point;

import zhaos.spaceagegame.request.MyBundle;
import zhaos.spaceagegame.request.Request;
import zhaos.spaceagegame.request.RequestConstants;
import zhaos.spaceagegame.util.HHexDirection;

/**
 * Created by russel on 12/10/2016.
 */

public class UnitMoveRequest extends SubsectionInfoBase{
    private int id;


    public UnitMoveRequest(RequestCallback callback) {
        super(callback);
    }

    public void setId(int id){
        this.id = id;
    }

    @Override
    protected void generateRequest() {
        super.generateRequest();
        thisRequest.putInt(RequestConstants.UNIT_ID,
                id);
        thisRequest.putInt(RequestConstants.INSTRUCTION, RequestConstants.UNIT_MOVE);
    }
}
