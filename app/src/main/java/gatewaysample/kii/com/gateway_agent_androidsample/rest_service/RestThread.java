package gatewaysample.kii.com.gateway_agent_androidsample.rest_service;


import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.kii.cloud.storage.KiiUser;
import com.kii.cloud.storage.exception.app.AppException;
import com.kii.thingif.KiiApp;
import com.kii.thingif.MediaTypes;
import com.kii.thingif.Owner;
import com.kii.thingif.Site;
import com.kii.thingif.TypedID;
import com.kii.thingif.exception.ThingIFException;
import com.kii.thingif.gateway.GatewayAPI4Gateway;
import com.kii.thingif.gateway.GatewayAPIBuilder;
import com.kii.thingif.internal.http.IoTRestClient;
import com.kii.thingif.internal.http.IoTRestRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import gatewaysample.kii.com.gateway_agent_androidsample.utils.Config;

public class RestThread extends ThreadCall implements Runnable {

    static boolean mStop = false;
    protected final IoTRestClient restClient = new IoTRestClient();
    private String gatewayCrediental;

    private String mUserName;
    private String mPassWord;
    private String mUrl;
    private String baseSite = "http://10.0.0.5:8080/";
    private String onBoardGatewayUrl = baseSite + "gateway-app/gateway/onboarding";
    private ContactActivity myActivity;

    public RestThread( String url, String userName, String passWord) {
        this.mUrl = url;
        this.mUserName = userName;
        this.mPassWord = passWord;



    }

    public RestThread(String url) {
        this.mUrl = url;
    }

    public void run() {
        if (mContext instanceof ContactActivity){
            myActivity = (ContactActivity) mContext;
        }

        while(!mStop){
            initCredential();
            //String path = "http://10.0.0.5:8080/token";
            //String path = "http://10.0.0.5:8080/gateway-app/gateway/onboarding";

           // String url = path;
            String url = baseSite + mUrl;
            Map<String, String> headers = newHeader();

            if (mUrl.contains("onboarding")){ //onboarding endnode.
                IoTRestRequest request = new IoTRestRequest(url, IoTRestRequest.Method.POST, headers);

                JSONObject responseBody =  new JSONObject();
                try {
                    responseBody = restClient.sendRequest(request);
                } catch (ThingIFException e) {
                    e.printStackTrace();
                }

                String thingID = responseBody.optString("thingID");
                Log.i(TAG, "thingID : " + thingID);

            }else if (mUrl.contains("token")){ // include gateway onboarding , and get token.
                //JsonUtils.newJson(GsonRepository.gson().toJson((actions.get(0).getActionName())));
                JSONObject requestBody = new JSONObject();

                try {
                    requestBody.put("username", mUserName);
                    requestBody.put("password", mPassWord);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                IoTRestRequest request = new IoTRestRequest(url, IoTRestRequest.Method.POST, headers, MediaTypes.MEDIA_TYPE_JSON, requestBody);

                JSONObject responseBody =  new JSONObject();
                try {
                    responseBody = restClient.sendRequest(request);
                } catch (ThingIFException e) {
                    e.printStackTrace();
                }

                String token = responseBody.optString("accessToken");
                Log.i(TAG, "token : " + token);

                //Toast.makeText(mContext, "token : " + token, Toast.LENGTH_LONG).show();
                Message message = myActivity.mShowHandler.obtainMessage(0,  "token : " + token);
                message.sendToTarget();
                String urlOnBoard = onBoardGatewayUrl;



                request = new IoTRestRequest(urlOnBoard, IoTRestRequest.Method.POST, headers);

                responseBody =  new JSONObject();
                try {
                    responseBody = restClient.sendRequest(request);
                } catch (ThingIFException e) {
                    e.printStackTrace();
                }

                String thingID = responseBody.optString("thingID");


                message = myActivity.mShowHandler.obtainMessage(0, "gateway ID : " + thingID);
                message.sendToTarget();

                //Toast.makeText(mContext, "gateway ID : " + thingID, Toast.LENGTH_LONG).show();
                Log.i(TAG, "thingID : " + thingID);
            }else if (mUrl.contains("id")){
                IoTRestRequest request = new IoTRestRequest(url, IoTRestRequest.Method.GET, headers);

                JSONObject responseBody =  new JSONObject();
                try {
                    responseBody = restClient.sendRequest(request);
                } catch (ThingIFException e) {
                    e.printStackTrace();
                }

                String thingID = responseBody.optString("thingID");
                //Toast.makeText(mContext, "gateway ID : " + thingID, Toast.LENGTH_LONG).show();
                Message message = myActivity.mShowHandler.obtainMessage(0, "gateway ID : " + thingID);
                message.sendToTarget();
                Log.i(TAG, "thingID : " + thingID);
            }else if (mUrl.contains("pending")){
                IoTRestRequest request = new IoTRestRequest(url, IoTRestRequest.Method.GET, headers);

                JSONObject responseBody =  new JSONObject();
                try {
                    responseBody = restClient.sendRequest(request);
                } catch (ThingIFException e) {
                    e.printStackTrace();
                }

                Message message = myActivity.mShowHandler.obtainMessage(0, "pending device: " + responseBody);
                message.sendToTarget();

//                String thingID = responseBody.optString("thingID");
//                Log.i(TAG, "thingID : " + thingID);
            }




            mStop = true;
        }

        if (mStop){
            Log.i(TAG,"stop");
            this.interrupt();

        }


    }

    protected Map<String, String> newHeader() {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "Bearer " + gatewayCrediental);
        return headers;
    }

    private void initCredential(){
        String accessTokenStr = Config.APP_ID + ":" + Config.APP_KEY;
        try {
            byte[] data = accessTokenStr.getBytes("UTF-8");
            gatewayCrediental = Base64.encodeToString(data, Base64.DEFAULT);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}