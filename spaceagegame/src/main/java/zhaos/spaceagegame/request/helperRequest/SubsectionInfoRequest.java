package zhaos.spaceagegame.request.helperRequest;

import zhaos.spaceagegame.request.RequestConstants;

/**
 * Created by russel on 12/10/2016.
 */

public class SubsectionInfoRequest extends SubsectionInfoBase {
    public SubsectionInfoRequest(RequestCallback callback) {
        super(callback);
    }

    @Override
    protected void generateRequest() {
        super.generateRequest();
        thisRequest.putInt(RequestConstants.INSTRUCTION, RequestConstants.SUBSECTION_INFO);
    }
}
