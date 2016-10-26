package zhaos.spaceagegame.game.resources;

import android.os.Bundle;

/**
 * Created by kodomazer on 10/22/2016.
 */

public class Request {
    private MyBundle thisRequest;
    private RequestCallback callback;

    public Request(MyBundle request, RequestCallback callback){
        thisRequest = request;
        this.callback = callback;

    }



    public interface RequestCallback {
        void onComplete(MyBundle info);
    }

    public MyBundle getThisRequest() {
        return thisRequest;
    }

    public RequestCallback getCallback() {
        return callback;
    }
}
