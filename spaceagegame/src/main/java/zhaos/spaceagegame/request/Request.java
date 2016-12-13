package zhaos.spaceagegame.request;

import android.support.annotation.NonNull;

/**
 * Created by kodomazer on 10/22/2016.
 */

public class Request {

    private final Request.RequestCallback emptyCallback = new Request.RequestCallback() {
        @Override
        public void onComplete(MyBundle info) {
            //do Nothing
        }
    };

    protected MyBundle thisRequest;
    private RequestCallback callback;

    public Request(MyBundle request, RequestCallback callback){
        thisRequest = request;
        this.callback = callback;
    }

    public interface RequestCallback {
        void onComplete(MyBundle info);
    }

    public int getInstructioin(){
        return thisRequest.getInt(RequestConstants.INSTRUCTION);
    }

    public MyBundle getThisRequest() {
        if(thisRequest==null){
            generateRequest();
        }
        return thisRequest;
    }


    protected void generateRequest(){
        thisRequest = new MyBundle();

    }

    public @NonNull RequestCallback getCallback() {
        if(callback==null)
            return emptyCallback;
        return callback;
    }
}
