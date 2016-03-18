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

import gatewaysample.kii.com.gateway_agent_androidsample.GatewayService;
import gatewaysample.kii.com.gateway_agent_androidsample.utils.Config;

public class GatewayAppRest extends ServerResource implements IBookService {

    private final String TAG = "GatewayAppRest";
    GatewayService mGatewayService;

//    public void getTID(String msg){
//        Thread t = Thread.currentThread();
//        long l = t.getId();
//        String name = t.getName();
//        com.kii.thingif.internal.utils.Log.i(TAG, msg + " ID : " + l + " Name : " + name);
//    }


    @Override
    @Post
    @Get
    public String present(Representation entity) {
        //getTID("rest ");
        //mThreadCall = (ThreadCall) getContext().getAttributes().get("threadCall");
        mGatewayService = (GatewayService) getContext().getAttributes().get("mGatewayService");
        Form form = new Form(entity);
        Set<String> names = form.getNames();
        List listOfNames = new ArrayList(names);
        //String bodyStr = form.get(0).toString();
        //String bodyStr = names.
//        String methodPost = form.getFirstValue("username");
//        Log.i(TAG,"method : " + methodPost);



        String method = (String) getRequestAttributes().get("method");
        JSONObject responseBody =  new JSONObject();
        Log.i(TAG, "method : " + method);


            switch(method){
                case "onboarding":



        //                            body = new JSONObject(listOfNames.get(0).toString());
        //                            String authorization = body.optString("authorization");
                    String thingID = "";
                    if (mGatewayService != null){
                        thingID = mGatewayService.onBoardingGateway();
                    }

                    Log.i(TAG," thing ID : " + thingID);
                    try {
                        responseBody.put("thingID",thingID);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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

//                            String accessTokenStr = Config.APP_ID + ":" + Config.APP_KEY;
//
//                            try {
//                                byte[] data = accessTokenStr.getBytes("UTF-8");
//                                responseBody.put("mAccessToken",Base64.encodeToString(data, Base64.DEFAULT));
//                            } catch (UnsupportedEncodingException e) {
//                                e.printStackTrace();
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }


                    break;
                case "restore":
                    break;

                default:
                    break;
            }



//        return "Resource URI  : " + getReference() + '\n' + "Root URI      : "
//                + getRootRef() + '\n' + "Routed part   : "
//                + getReference().getBaseRef() + '\n' + "Remaining part: "
//                + "method \"" + methodPost + '\n'
//                + getReference().getRemainingPart();

        return responseBody.toString();

    }

}