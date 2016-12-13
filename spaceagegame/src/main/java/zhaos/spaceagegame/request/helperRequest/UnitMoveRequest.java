package zhaos.spaceagegame.request.helperRequest;

import zhaos.spaceagegame.request.RequestConstants;

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

    public int getId(){
        if(thisRequest!=null){
            return thisRequest.getInt(RequestConstants.UNIT_ID);
        }
        return id;
    }

    @Override
    protected void generateRequest() {
        super.generateRequest();
        thisRequest.putInt(RequestConstants.UNIT_ID,
                id);
        thisRequest.putInt(RequestConstants.INSTRUCTION, RequestConstants.UNIT_MOVE);
    }
}
