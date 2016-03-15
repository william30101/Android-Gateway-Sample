package gatewaysample.kii.com.gateway_agent_androidsample.rest_service;


import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Base64;
import android.util.Log;

import com.kii.thingif.gateway.EndNode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.Form;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import org.restlet.routing.Template;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import gatewaysample.kii.com.gateway_agent_androidsample.GatewayService;
import gatewaysample.kii.com.gateway_agent_androidsample.utils.Config;
import gatewaysample.kii.com.gateway_agent_androidsample.utils.MappingObject;

public class AppsRest extends ServerResource implements IBookService {

    private final String TAG = "AppsRest";
    GatewayService mGatewayService;

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



        String appId = (String) getRequestAttributes().get("appId");
        String method1 = (String) getRequestAttributes().get("method1");
        String method2 = (String) getRequestAttributes().get("method2");
        String method3 = (String) getRequestAttributes().get("method3");
        JSONObject responseBody =  new JSONObject();
        Log.i(TAG, "method : " + method1);

            switch(method1){
                case "onboarding":

                    String endNodeThingID = "";
                    if (mGatewayService != null){
                        endNodeThingID = mGatewayService.onBoardEndNode(method2);
                    }

                    Log.i(TAG," thing ID : " + endNodeThingID);
                    try {
                        responseBody.put("thingID",endNodeThingID);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;
                case "id":

                    String gatewayID = "";
                    if (mGatewayService != null){
                        gatewayID = mGatewayService.getGatewayID();
                    }

                    Log.i(TAG," thing ID : " + gatewayID);
                    try {
                        responseBody.put("thingID", gatewayID);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case "end-nodes":
                    if (method2.equals("pending")){
                        if (mGatewayService != null){
                            ArrayList<EndNode> endNodes = mGatewayService.getPendingEndNodes();
                            if (endNodes.size() > 0){
                                JSONArray endNodesArr = new JSONArray();
                                for (int i=0; i < endNodes.size(); i++){
                                    JSONObject endNodeObj = new JSONObject();
                                    try {
                                        endNodeObj.put("thingID", endNodes.get(i).getThingID());
                                        endNodeObj.put("vendorThingID", endNodes.get(i).getVendorThingID());
                                        endNodesArr.put(endNodeObj);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                                try {
                                    responseBody.put("pendingEndNodes",endNodesArr);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }
                    }else if (method2.contains("VENDOR_THING_ID")){
                        String ret= "";
                        if (mGatewayService != null){
                            String arr[] = method2.split(":",-1);
                            if (arr.length >= 2){
                                ret = mGatewayService.onBoardSuccess(arr[1]);
                            }


                        }

                        try {
                            responseBody.put("pendingEndNodes",ret);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else if (method2.contains("discovery")){
                        String ret= "";
                        if (mGatewayService != null){
                            List<BluetoothDevice> devices = mGatewayService.discoveryEndNodes();

                            JSONArray endNodesArr = new JSONArray();
                            for (int i=0; i < devices.size(); i++){
                                JSONObject endNodeObj = new JSONObject();
                                try {
                                    endNodeObj.put("name", devices.get(i).getName());
                                    endNodeObj.put("address", devices.get(i).getAddress());
                                    endNodesArr.put(endNodeObj);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }


                            try {
                                responseBody.put("devices",endNodesArr);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                    }else if (method2.contains("connect")){
                        String ret= "";
                        if (mGatewayService != null){
                            String arr[] = method2.split(":",-1);
                            if (arr.length >= 2){
                                mGatewayService.connectEndNode(arr[1]);
                            }


                        }

                        try {
                            responseBody.put("pendingEndNodes",ret);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else if (method2.contains("sendCmd")){
                        String thingID = method3;

                    }else if (method2.contains("listOnBoardDevice")){
                        if (mGatewayService != null){
                            List<MappingObject> mappingEndNodes = mGatewayService.getMappingTable();

                            JSONArray endNodesArr = new JSONArray();
                            for (int i=0; i < mappingEndNodes.size(); i++){


                                JSONObject endNodeObj = new JSONObject();
                                try {
                                    endNodeObj.put("thingID", mappingEndNodes.get(i).getThingID());
                                    endNodeObj.put("vendorThingID", mappingEndNodes.get(i).getVendorThingID());
                                    endNodeObj.put("position", mappingEndNodes.get(i).getPosition());
                                    endNodesArr.put(endNodeObj);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }

                            try {
                                responseBody.put("devices",endNodesArr);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }

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