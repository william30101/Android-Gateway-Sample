package gatewaysample.kii.com.gateway_agent_androidsample.rest_service;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.kii.cloud.storage.KiiUser;
import com.kii.cloud.storage.exception.app.AppException;
import com.kii.thingif.KiiApp;
import com.kii.thingif.Owner;
import com.kii.thingif.Site;
import com.kii.thingif.TypedID;
import com.kii.thingif.exception.ThingIFException;
import com.kii.thingif.gateway.EndNode;
import com.kii.thingif.gateway.GatewayAPI4EndNode;
import com.kii.thingif.gateway.GatewayAPI4Gateway;
import com.kii.thingif.gateway.GatewayAPIBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import gatewaysample.kii.com.gateway_agent_androidsample.mqtt.MqttClient;
import gatewaysample.kii.com.gateway_agent_androidsample.utils.Config;
import gatewaysample.kii.com.gateway_agent_androidsample.utils.MappingObject;


public class ThreadCall extends Thread{

    final String TAG = "ThreadCall";

    static MqttClient mqttClient;
    final String GatewayVendorThingID =  "gateway-android";
    final String GatewayPassword = "123456";
    static List<MappingObject> mappingTable = new ArrayList<>();

    private final String EndNodeVendorThingID = "endnode-android";
    private final String EndNodePassword = "123456";

    private final String replaceEndNodeVendorThingID = "endnode-replace-android";
    private final String replaceEndNodePassword = "123456";

    String syncObject = "sync";
    static GatewayAPI4Gateway gatewayA;
    static GatewayAPI4EndNode gatewayEnd;
    static Owner owner;

    static Context mContext;
    static boolean mStop;
    static String accessToken;

    ThreadCall(Context context){
        mContext = context;
    }

    ThreadCall(){

    }

    public Context getmContext() {
        return mContext;
    }


    public boolean fileExists(Context context, String filename) {
        File file = context.getFileStreamPath(filename);
        if(file == null || !file.exists()) {
            return false;
        }
        return true;
    }

    public boolean fileDel(Context context, String filename) {
        File file = context.getFileStreamPath(filename);
        if(file == null || !file.exists()) {
            return true;
        }
        return file.delete();
    }

    void newMappingFile(List<MappingObject> newMappingTable){

        if (fileExists(mContext,Config.MAPPING_FILE_NAME)){
            fileDel(mContext, Config.MAPPING_FILE_NAME);
        }

        for (int i=0; i < newMappingTable.size(); i++) {
            writeToMappingFile(newMappingTable.get(i).getPosition() + " " + newMappingTable.get(i).getThingID() + " " +
                    newMappingTable.get(i).getVendorThingID() + " " + newMappingTable.get(i).getAccessToken() + " " +
                    newMappingTable.get(i).getOwnerID());
        }

    }

    void replaceMappingTable(MappingObject obj){
        if (mappingTable.size() > 0) {
            boolean finded = false;
            for (int i = 0; i < mappingTable.size(); i++) {

                if (mappingTable.get(i).getVendorThingID().equals(obj.getVendorThingID())) {
                    finded = true;
                    mappingTable.get(i).setThingID(obj.getThingID());
                }
            }


            if (!finded){
                //Log.i(TAG," Not find match object :"  + obj.getVendorThingID() +  ", write new one");
                Log.i(TAG, " Not find match object :" + obj.getVendorThingID() + ", write new one");
                mappingTable.add(obj);
            }

        }
        //If table list not finded match object , write new one.
//

        if (fileExists(mContext,Config.MAPPING_FILE_NAME)){
            fileDel(mContext, Config.MAPPING_FILE_NAME);
        }

        for (int i=0; i < mappingTable.size(); i++) {
            writeToMappingFile(mappingTable.get(i).getPosition() + " " + mappingTable.get(i).getThingID() + " " +
                    mappingTable.get(i).getVendorThingID() + " " + mappingTable.get(i).getAccessToken() + " " +
                    mappingTable.get(i).getOwnerID());
        }


    }

    private void writeToMappingFile(String data){
        try {
            FileOutputStream fOut = mContext.openFileOutput(Config.MAPPING_FILE_NAME, mContext.MODE_PRIVATE | mContext.MODE_APPEND);
            fOut.write(System.getProperty("line.separator").getBytes());
            fOut.write(data.getBytes());
            fOut.close();
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    List<EndNode> getEndNodeList(){
        try {
            List<EndNode> endNodes = gatewayA.listAllEndNode(0, null);
            for (int i=0; i< endNodes.size(); i++){
                Log.i(TAG, "vendor thingID :" + endNodes.get(i).getVendorThingID() +
                        "thingID : " + endNodes.get(i).getThingID());
            }
            return endNodes;

        } catch (ThingIFException e) {
            e.printStackTrace();
        }

        return null;
    }


    public void login(String userName, String passWord){


        try {
            KiiUser user = KiiUser.logIn(userName, passWord);
            String accessToken = user.getAccessToken();
            this.accessToken = accessToken;

            // Get the access token by getAccessTokenBundle
            Bundle b = user.getAccessTokenBundle();
            accessToken = b.getString("access_token");

            TypedID typedUserID = new TypedID(TypedID.Types.USER, user.getID());
            owner = new Owner(typedUserID, accessToken);



        } catch (IOException e) {
            // Sign-in failed for some reasons
            // Please check IOExecption to see what went wrong...
        } catch (AppException e) {
            // Sign-in failed for some reasons
            // Please check AppException to see what went wrong...
        }

    }
}
