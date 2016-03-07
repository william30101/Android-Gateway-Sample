package gatewaysample.kii.com.gateway_agent_androidsample.rest_service;


import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.Form;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import gatewaysample.kii.com.gateway_agent_androidsample.utils.Config;

public class GatewayInfoRest extends ServerResource implements IBookService {

    private final String TAG = "Bookservice";
    private ThreadCall mThreadCall;
    private android.content.Context mContext;
    RegisterGatewayThread mThread;

    public void getTID(String msg){
        Thread t = Thread.currentThread();
        long l = t.getId();
        String name = t.getName();
        com.kii.thingif.internal.utils.Log.i(TAG, msg + " ID : " + l + " Name : " + name);
    }


    @Override
    @Post
    @Get
    public String present(Representation entity) {
        getTID("rest ");
        //mThreadCall = (ThreadCall) getContext().getAttributes().get("threadCall");
        mContext = (android.content.Context) getContext().getAttributes().get("mContext");
        Form form = new Form(entity);
        Set<String> names = form.getNames();
        List listOfNames = new ArrayList(names);
        //String bodyStr = form.get(0).toString();
        //String bodyStr = names.
//        String methodPost = form.getFirstValue("username");
//        Log.i(TAG,"method : " + methodPost);



        String methodGet = (String) getRequestAttributes().get("method");
        JSONObject responseBody =  new JSONObject();
        Log.i(TAG, "method : " + methodGet);

        List<String> segments= getReference().getSegments();
        for(int i=0; i< segments.size(); i++){
            switch(segments.get(0)){
                case "token":

                    if (listOfNames.size() > 0 && i == 0){
                        JSONObject body = null;
                        try {


                            body = new JSONObject(listOfNames.get(0).toString());
                            String userName = body.optString("username");
                            String passWord = body.optString("password");

                            Log.i(TAG,"username : " + userName + " pass : " + passWord);



//
//                            ThreadCall.mStop = false;
//                            if (mThread != null){
//                                if (mThread.getState() == Thread.State.TERMINATED){
//                                    mThread = new RegisterGatewayThread(userName, passWord);
//                                    Log.i(TAG,"run");
//                                    mThread.start();
//                                }if (!mThread.isAlive()) {
//                                    mThread.start();
//                                }
//                            }else{
//                                mThread = new RegisterGatewayThread(userName, passWord);
//                                Log.i(TAG,"run");
//                                mThread.start();
//                            }



//                            Runnable r = new RegisterGatewayThread(userName, passWord);
//                            new Thread(r).start();

                            String accessTokenStr = Config.APP_ID + ":" + Config.APP_KEY;

                            try {
                                byte[] data = accessTokenStr.getBytes("UTF-8");
                                responseBody.put("accessToken",Base64.encodeToString(data, Base64.DEFAULT));
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case "gateway-app":
                    break;
                case "gateway-info":
                    break;
                case "apps":
                    break;
                default:
                    break;
            }
        }


//        return "Resource URI  : " + getReference() + '\n' + "Root URI      : "
//                + getRootRef() + '\n' + "Routed part   : "
//                + getReference().getBaseRef() + '\n' + "Remaining part: "
//                + "method \"" + methodPost + '\n'
//                + getReference().getRemainingPart();

        return responseBody.toString();

    }


}