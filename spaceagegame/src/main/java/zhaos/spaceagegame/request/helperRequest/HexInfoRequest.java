package zhaos.spaceagegame.request.helperRequest;

import android.graphics.Point;

import zhaos.spaceagegame.request.Request;
import zhaos.spaceagegame.request.RequestConstants;

/**
 * Created by russel on 12/10/2016.
 */

public class HexInfoRequest extends Request {

    private Point hex;


    public HexInfoRequest(RequestCallback callback) {
        super(null, callback);
    }

    public void setHex(Point hex) {
        this.hex = hex;
    }

    public Point getHex(){
        return hex;
    }

    @Override
    protected void generateRequest() {
        super.generateRequest();
        thisRequest.putInt(RequestConstants.INSTRUCTION,
                RequestConstants.HEX_INFO);
        thisRequest.putPoint(RequestConstants.DESTINATION_HEX,
                hex);
    }
}
