package zhaos.spaceagegame.request.helperRequest;

import zhaos.spaceagegame.request.RequestConstants;

/**
 * Created by russel on 12/10/2016.
 */

public class UnitAttackRequest extends UnitMoveRequest {
    public UnitAttackRequest(RequestCallback callback) {
        super(callback);
    }

    @Override
    protected void generateRequest() {
        super.generateRequest();

        thisRequest.putInt(RequestConstants.INSTRUCTION,RequestConstants.UNIT_ATTACK);
    }
}
