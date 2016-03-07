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

    ThreadCall(Context context){
        mContext = context;
    }

    ThreadCall(){

    }

    public Context getmContext() {
        return mContext;
    }



    public void mappingInit(){
        //listThingsUnderGateway(gatewayApi);
        //getEndNodeList();
        mappingAllRestore();
    }

    private void mappingAllRestore(){

        if (fileExists(mContext, Config.MAPPING_FILE_NAME)){
            // TODO
            // restore endNode list
            FileInputStream fin = null;
            BufferedReader reader;
//
//            String gatewayID = ReadSharedPreferences("gatewayThingID");
//            Log.i(TAG, " gateway ID : " + gatewayID);

            try {
                fin = mContext.openFileInput(Config.MAPPING_FILE_NAME);

                reader = new BufferedReader(new InputStreamReader(fin));
                String line;
                while ((line = reader.readLine()) != null) {
                    //arr[0] should be name , arr[1] be thingID
                    if (line.length() > 0){
                        String arr[] = line.split(" ",-1);
                        MappingObject obj = new MappingObject(arr[0], arr[1], arr[2], arr[3], arr[4]);
                        mappingTable.add(obj);
                    }
                }

                for(int i=0; i< mappingTable.size(); i++){
                    Log.i(TAG, "position : " + mappingTable.get(i).getPosition() +
                            " ThingID: " + mappingTable.get(i).getThingID() +
                            " VendorThingID: " + mappingTable.get(i).getVendorThingID() +
                            " AccessToken: " + mappingTable.get(i).getAccessToken() +
                            " ownerID: " + mappingTable.get(i).getOwnerID());
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }


    public void onBoardGateWay(){
        // finalGatewayA.login("VENDOR_THING_ID:gatewaytest2", "123456");
            if (gatewayA != null && owner != null) {
                try {
                    //getTID("onBoardThread ");
                    Log.i(TAG, "onBoardThread running");
                    String gatewayThingID = gatewayA.onboardGateway(GatewayVendorThingID, GatewayPassword, "led", null, owner.getTypedID().toString());

                    if (gatewayThingID != null) {
                        mappingTable.clear();
                        mappingTable.add(new MappingObject("Gateway", gatewayThingID, GatewayVendorThingID,
                                owner.getAccessToken(), owner.getTypedID().toString()));
                        //WriteSharedPreferences("gatewayThingID", gatewayThingID);

                        List<EndNode> endNodes = getEndNodeList();
                        if (endNodes != null) {
                            for (int i = 0; i < endNodes.size(); i++) {
                                mappingTable.add(i, new MappingObject("EndNode", endNodes.get(i).getThingID(),
                                        endNodes.get(i).getVendorThingID(), owner.getAccessToken(), owner.getTypedID().toString()));
                            }
                        }

                        newMappingFile(mappingTable);

                        //MQTT receive connections established.
                        mqttClient = new MqttClient(mContext, gatewayA.getmGateway().getMqttEndpoint());
                        mqttClient.connect();
                    }

                } catch (ThingIFException e) {
                    e.printStackTrace();
                }

            }
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
        KiiApp app = new KiiApp(Config.APP_ID, Config.APP_KEY, Site.JP);
        //KiiApp app = new KiiApp(Config.APP_ID, Config.APP_KEY, "127.0.0.1");
        GatewayAPIBuilder gatewayBuilder = GatewayAPIBuilder.newBuilder(mContext, app, "127.0.0.1", userName, passWord);
        try {
            gatewayA = gatewayBuilder.build4Gateway();
            gatewayEnd = gatewayBuilder.build4EndNode();

        } catch (ThingIFException e) {
            e.printStackTrace();
        }

        // sign in user
        final GatewayAPI4Gateway finalGatewayA = gatewayA;


        try {
            KiiUser user = KiiUser.logIn(userName, passWord);
            String accessToken = user.getAccessToken();

            // Get the access token by getAccessTokenBundle
            Bundle b = user.getAccessTokenBundle();
            accessToken = b.getString("access_token");

            TypedID typedUserID = new TypedID(TypedID.Types.USER, user.getID());
            owner = new Owner(typedUserID, accessToken);

            // Securely store the access token in persistent storage
            // (assuming that your application implements this function)
            if (finalGatewayA != null) {
                finalGatewayA.setAccessToken(accessToken);
                gatewayEnd.setAccessToken(accessToken);
            }


        } catch (IOException e) {
            // Sign-in failed for some reasons
            // Please check IOExecption to see what went wrong...
        } catch (AppException e) {
            // Sign-in failed for some reasons
            // Please check AppException to see what went wrong...
        }

    }

    class MappingObject {
        String position;
        String thingID;
        String vendorThingID;
        String accessToken;
        String ownerID;

        /***
         *  use this class for mapping file .
         *
         * @param position Gateway or EndNode
         * @param thingID  return ID from REST API
         * @param vendorThingID vendorThingID from onBoarding.
         * @param accessToken  owner AccessToken
         * @param ownerID  ownerID
         */
        public MappingObject(String position, String thingID, String vendorThingID, String accessToken, String ownerID) {
            this.position = position;
            this.thingID = thingID;
            this.vendorThingID = vendorThingID;
            this.accessToken = accessToken;
            this.ownerID = ownerID;
        }

        public String getPosition() {
            return position;
        }

        public String getThingID() {
            return thingID;
        }

        public String getVendorThingID() {
            return vendorThingID;
        }

        public String getAccessToken() {
            return accessToken;
        }

        public String getOwnerID() {
            return ownerID;
        }

        public void setPosition(String position) {
            this.position = position;
        }

        public void setThingID(String thingID) {
            this.thingID = thingID;
        }

        public void setVendorThingID(String vendorThingID) {
            this.vendorThingID = vendorThingID;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }

        public void setOwnerID(String ownerID) {
            this.ownerID = ownerID;
        }
    }
}
