package zhaos.spaceagegame.request.helperRequest;

import zhaos.spaceagegame.request.MyBundle;
import zhaos.spaceagegame.request.Request;
import zhaos.spaceagegame.request.RequestConstants;

/**
 * Created by russel on 12/10/2016.
 */

public class UnitInfoRequest extends Request {
    private int id;

    public UnitInfoRequest(RequestCallback callback) {
        super(null, callback);
    }


    public void setUnitID(int id){
        this.id = id;
    }

    public int getUnitID(){
        return id;
    }

    @Override
    protected void generateRequest() {
        super.generateRequest();

        thisRequest.putInt(RequestConstants.UNIT_ID, id);
        thisRequest.putInt(RequestConstants.INSTRUCTION,RequestConstants.UNIT_INFO);
    }
}
