package zhaos.spaceagegame.request.helperRequest;

import android.graphics.Point;

import zhaos.spaceagegame.request.MyBundle;
import zhaos.spaceagegame.request.Request;
import zhaos.spaceagegame.request.RequestConstants;
import zhaos.spaceagegame.util.HHexDirection;

/**
 * Created by russel on 12/10/2016.
 */

public class UnitMoveRequest extends Request {
    private int id;
    private HHexDirection destinationSubsection;
    private Point destinationHex;


    public UnitMoveRequest(RequestCallback callback) {
        super(null, callback);

    }

    public void setUnitID(int id){
        this.id = id;
    }
    public void setDestination(Point hex, HHexDirection subsection){
        this.destinationHex = hex;
        this.destinationSubsection = subsection;
    }


    @Override
    protected void generateRequest() {
        thisRequest = new MyBundle();
        thisRequest.putInt(RequestConstants.INSTRUCTION,
                RequestConstants.UNIT_MOVE);
        thisRequest.putInt(RequestConstants.UNIT_ID,
                id);
        thisRequest.putPoint(RequestConstants.DESTINATION_HEX,
                destinationHex);
        thisRequest.putSubsection(RequestConstants.DESTINATION_SUBSECTION,
                destinationSubsection);
    }
}
