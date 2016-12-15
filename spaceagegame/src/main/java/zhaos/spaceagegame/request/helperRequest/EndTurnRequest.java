package zhaos.spaceagegame.request.helperRequest;

import zhaos.spaceagegame.request.Request;
import zhaos.spaceagegame.request.RequestConstants;

/**
 * Created by russel on 12/14/2016.
 */
public class EndTurnRequest extends Request{
    public EndTurnRequest(Request.RequestCallback requestCallback) {
        super(null,requestCallback);
    }

    @Override
    protected void generateRequest() {
        super.generateRequest();
        thisRequest.putInt(RequestConstants.INSTRUCTION,RequestConstants.END_TURN);
    }
}
