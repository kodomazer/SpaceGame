package zhaos.spaceagegame.request.helperRequest;

import zhaos.spaceagegame.request.Request;
import zhaos.spaceagegame.request.RequestConstants;

/**
 * Created by russel on 12/13/2016.
 */

public class SpaceStationInfoRequest extends Request {
    private int id;


    public SpaceStationInfoRequest(RequestCallback callback) {
        super(null, callback);
    }

    public void setID(int id) {
        this.id = id;
    }

    public int getID(){
        return id;
    }

    @Override
    protected void generateRequest() {
        super.generateRequest();
        thisRequest.putInt(RequestConstants.SPACE_STATION_ID,id);
        thisRequest.putInt(RequestConstants.INSTRUCTION,RequestConstants.CITY_INFO);
    }
}
