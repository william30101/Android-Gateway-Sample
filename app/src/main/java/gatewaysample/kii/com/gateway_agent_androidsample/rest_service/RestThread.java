package gatewaysample.kii.com.gateway_agent_androidsample.rest_service;


import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
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
import com.kii.thingif.command.Action;
import com.kii.thingif.command.Command;
import com.kii.thingif.exception.ThingIFException;
import com.kii.thingif.gateway.GatewayAPI4Gateway;
import com.kii.thingif.gateway.GatewayAPIBuilder;
import com.kii.thingif.internal.GsonRepository;
import com.kii.thingif.internal.http.IoTRestClient;
import com.kii.thingif.internal.http.IoTRestRequest;
import com.kii.thingif.internal.utils.JsonUtils;
import com.kii.thingif.internal.utils.Path;
import com.kii.thingif.schema.Schema;
import com.kii.thingif.schema.SchemaBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;
import gatewaysample.kii.com.gateway_agent_androidsample.smart_light_demo.LightState;
import gatewaysample.kii.com.gateway_agent_androidsample.smart_light_demo.SetBrightness;
import gatewaysample.kii.com.gateway_agent_androidsample.smart_light_demo.SetBrightnessResult;
import gatewaysample.kii.com.gateway_agent_androidsample.smart_light_demo.SetColor;
import gatewaysample.kii.com.gateway_agent_androidsample.smart_light_demo.SetColorResult;
import gatewaysample.kii.com.gateway_agent_androidsample.smart_light_demo.SetColorTemperature;
import gatewaysample.kii.com.gateway_agent_androidsample.smart_light_demo.SetColorTemperatureResult;
import gatewaysample.kii.com.gateway_agent_androidsample.smart_light_demo.TurnPower;
import gatewaysample.kii.com.gateway_agent_androidsample.smart_light_demo.TurnPowerResult;
import gatewaysample.kii.com.gateway_agent_androidsample.utils.Config;
import gatewaysample.kii.com.gateway_agent_androidsample.utils.ControllCmd;
import gatewaysample.kii.com.gateway_agent_androidsample.utils.EventType;
import gatewaysample.kii.com.gateway_agent_androidsample.utils.MyControllerEvent;
import gatewaysample.kii.com.gateway_agent_androidsample.utils.MyEvent;

public class RestThread extends ThreadCall implements Runnable {

    static boolean mStop = false;
    protected final IoTRestClient restClient = new IoTRestClient();
    private String gatewayCrediental;

    private String mUserName;
    private String mPassWord;
    private String mUrl;
    private String baseSite = "http://10.0.0.9:8080/";
    private String onBoardGatewayUrl = baseSite + "gateway-app/gateway/onboarding";
    private ContactActivity myActivity;
    //private ContactActivity.mShowHandler mHandler;
    private static EventBus mEventBus;
    private List<Action> mActions;

    public RestThread( String url, String userName, String passWord) {
        this.mUrl = url;
        this.mUserName = userName;
        this.mPassWord = passWord;

        mEventBus = EventBus.getDefault();

    }

    public RestThread(String url) {
        this.mUrl = url;
    }

    public RestThread(String url, List<Action> actions) {
        this.mUrl = url;
        this.mActions = actions;
    }

    public void run() {
        if (mContext instanceof ContactActivity){
            myActivity = (ContactActivity) mContext;
            //mHandler = new ContactActivity.mShowHandler(myActivity);
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

                String endNodeThingID = responseBody.optString("thingID");
                Log.i(TAG, "thingID : " + endNodeThingID);

                sendEventToController(new EventType(Config.SEND_FROM_ENDNODE_ONBOARD, endNodeThingID));


            }else if (mUrl.contains("token")){ // include gateway onboarding , and get token.
                //JsonUtils.newJson(GsonRepository.gson().toJson((actions.get(0).getActionName())));
                JSONObject requestBody = new JSONObject();

                //register on app side for sendCmd Start.
                login(mUserName, mPassWord);
                //register on app side for sendCmd End.

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
//                Message message = mHandler.obtainMessage(0,  "token : " + token);
//                message.sendToTarget();
                sendEventToController(new EventType(Config.SEND_FROM_GET_TOKEN, token));

                String urlOnBoard = onBoardGatewayUrl;



                request = new IoTRestRequest(urlOnBoard, IoTRestRequest.Method.POST, headers);

                responseBody =  new JSONObject();
                try {
                    responseBody = restClient.sendRequest(request);
                } catch (ThingIFException e) {
                    e.printStackTrace();
                }

                String thingID = responseBody.optString("thingID");


//                message = mHandler.obtainMessage(0, "gateway ID : " + thingID);
//                message.sendToTarget();

                sendEventToController(new EventType(Config.SEND_FROM_GET_GATEWAY_ID, thingID));

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
//                Message message = mHandler.obtainMessage(0, "gateway ID : " + thingID);
//                message.sendToTarget();
                sendEventToController(new EventType(Config.SEND_FROM_GET_GATEWAY_ID, thingID));

                Log.i(TAG, "thingID : " + thingID);
            }else if (mUrl.contains("pending")){
                IoTRestRequest request = new IoTRestRequest(url, IoTRestRequest.Method.GET, headers);

                JSONObject responseBody =  new JSONObject();
                try {
                    responseBody = restClient.sendRequest(request);
                } catch (ThingIFException e) {
                    e.printStackTrace();
                }

//                Message message = mHandler.obtainMessage(2, "pending device: " + responseBody);
//                message.sendToTarget();

                sendEventToController(new EventType(Config.SEND_FROM_GET_PENDING_DEVICE, responseBody));

//                String thingID = responseBody.optString("thingID");
//                Log.i(TAG, "thingID : " + thingID);
            }else if (mUrl.contains("discovery")){
                IoTRestRequest request = new IoTRestRequest(url, IoTRestRequest.Method.GET, headers);

                JSONObject responseBody =  new JSONObject();
                try {
                    responseBody = restClient.sendRequest(request);
                } catch (ThingIFException e) {
                    e.printStackTrace();
                }

//                Message message = mHandler.obtainMessage(0, "finding device: " + responseBody);
//                message.sendToTarget();
//
//                //Show dialog
//                message = mHandler.obtainMessage(1, responseBody);
//                message.sendToTarget();

                sendEventToController(new EventType(Config.SEND_FROM_DISCOVERY, responseBody));

//                String thingID = responseBody.optString("thingID");
//                Log.i(TAG, "thingID : " + thingID);
            }else if (mUrl.contains("connect")){
                IoTRestRequest request = new IoTRestRequest(url, IoTRestRequest.Method.GET, headers);

                JSONObject responseBody =  new JSONObject();
                try {
                    responseBody = restClient.sendRequest(request);
                } catch (ThingIFException e) {
                    e.printStackTrace();
                }

//                Message message = mHandler.obtainMessage(0, "connect device: " + responseBody);
//                message.sendToTarget();

                sendEventToController(new EventType(Config.SEND_FROM_CONNECT_DEVICE, responseBody));

//                String thingID = responseBody.optString("thingID");
//                Log.i(TAG, "thingID : " + thingID);
            }else if (mUrl.contains("listOnBoardDevice")){
                IoTRestRequest request = new IoTRestRequest(url, IoTRestRequest.Method.GET, headers);

                JSONObject responseBody =  new JSONObject();
                try {
                    responseBody = restClient.sendRequest(request);
                } catch (ThingIFException e) {
                    e.printStackTrace();
                }

//                Message message = mHandler.obtainMessage(0, "connect device: " + responseBody);
//                message.sendToTarget();

                sendEventToController(new EventType(Config.SEND_FROM_GET_ONBOARD_LIST, responseBody));
            } else if (mUrl.contains("sendCmd")){
                String thingID = mUrl.substring(mUrl.lastIndexOf('/') + 1);
                ControllCmd cmd = new ControllCmd(thingID, Config.SCHEMA_NAME, Config.SCHEMA_VERSION, mActions, owner, buildSchema());
                try {
                    sendCmdToEndNode(cmd);
                } catch (ThingIFException e) {
                    e.printStackTrace();
                }

//                Message message = mHandler.obtainMessage(0, "send cmd ret : " + responseBody);
//                message.sendToTarget();

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

    protected Map<String, String> newHeaderRemote() {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "Bearer " + accessToken);
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

    public void sendEventToController(EventType obj){
        MyControllerEvent event = new MyControllerEvent();
        event.setMyEventString(obj);
        mEventBus.post(event);
    }

    //We send remote controll cmd .
    public String sendCmdToEndNode( ControllCmd cmd) throws ThingIFException {

        String path = MessageFormat.format("/thing-if/apps/{0}/targets/thing:{1}/commands", Config.APP_ID, cmd.getThingID());
        String url = Path.combine(Config.IOTAPPBASEURL, path);
        Map<String, String> headers = this.newHeaderRemote();


        Command command = new Command(cmd.getSchemaName(), cmd.getSchemaVersion(), owner.getTypedID(), cmd.getActions());
        JSONObject requestBody = JsonUtils.newJson(GsonRepository.gson(cmd.getSchema()).toJson(command));


        IoTRestRequest request = new IoTRestRequest(url, IoTRestRequest.Method.POST, headers, MediaTypes.MEDIA_TYPE_JSON, requestBody);

        JSONObject responseBody = this.restClient.sendRequest(request);

        String cmdID = responseBody.optString("commandID");

        Log.i(TAG, "cmd ID : " + cmdID);

        return cmdID;

    }

    public Schema buildSchema() {
        SchemaBuilder schemaBuilder = SchemaBuilder.newSchemaBuilder(Config.THING_TYPE,
                Config.SCHEMA_NAME, Config.SCHEMA_VERSION, LightState.class);
        schemaBuilder.addActionClass(TurnPower.class, TurnPowerResult.class);
        schemaBuilder.addActionClass(SetBrightness.class, SetBrightnessResult.class);
        schemaBuilder.addActionClass(SetColor.class, SetColorResult.class);
        schemaBuilder.addActionClass(SetColorTemperature.class, SetColorTemperatureResult.class);

        return schemaBuilder.build();
    }
}
