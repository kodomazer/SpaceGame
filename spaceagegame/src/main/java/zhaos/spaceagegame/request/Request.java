package zhaos.spaceagegame.request;

/**
 * Created by kodomazer on 10/22/2016.
 */

public class Request {
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

    public RequestCallback getCallback() {
        return callback;
    }
}
