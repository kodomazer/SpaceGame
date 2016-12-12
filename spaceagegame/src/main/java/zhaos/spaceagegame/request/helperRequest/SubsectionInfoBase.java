package zhaos.spaceagegame.request.helperRequest;

import zhaos.spaceagegame.request.RequestConstants;
import zhaos.spaceagegame.spaceGame.map.Subsection;
import zhaos.spaceagegame.util.HHexDirection;

/**
 * Created by russel on 12/10/2016.
 */
public class SubsectionInfoBase extends HexInfoRequest {
    private HHexDirection subsection;

    public SubsectionInfoBase(RequestCallback callback) {
        super(callback);
    }

    public void setSubsection(HHexDirection subsection) {
        this.subsection = subsection;
    }
    public HHexDirection getSubsection(){
        return subsection;
    }

    @Override
    protected void generateRequest() {
        super.generateRequest();
        thisRequest.putSubsection(RequestConstants.SUBSECTION,subsection);
        thisRequest.putSubsection(RequestConstants.DESTINATION_SUBSECTION,subsection);
    }
}
