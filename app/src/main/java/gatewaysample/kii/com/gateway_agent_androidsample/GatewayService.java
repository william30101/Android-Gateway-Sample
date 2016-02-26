package gatewaysample.kii.com.gateway_agent_androidsample;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import com.kii.cloud.storage.KiiUser;
import com.kii.cloud.storage.exception.app.AppException;
import com.kii.thingif.KiiApp;
import com.kii.thingif.Owner;
import com.kii.thingif.Site;
import com.kii.thingif.ThingIFAPI;
import com.kii.thingif.TypedID;
import com.kii.thingif.command.Action;
import com.kii.thingif.command.Command;
import com.kii.thingif.exception.ThingIFException;
import com.kii.thingif.gateway.*;
import com.kii.thingif.gateway.EndNode;
import com.kii.thingif.internal.GsonRepository;
import com.kii.thingif.internal.utils.JsonUtils;
import com.kii.thingif.internal.utils.Log;
import com.kii.thingif.schema.Schema;
import com.kii.thingif.schema.SchemaBuilder;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import gatewaysample.kii.com.gateway_agent_androidsample.mqtt.MqttClient;
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



public class GatewayService extends Service {

    private final String TAG = "GatewayService";
    private Handler handler = new Handler();
    private Handler toasthandler;
    private ThingIFAPI gatewayApi, thingApi;
    private String filename = "myfile";
    public static final String ENCODING = "UTF-8";
    GatewayAPI4Gateway gatewayA;
    GatewayAPI4EndNode gatewayEnd;
    Owner owner;
    private String syncObject = "test";
    private List<Map<String, String>> restoreThings = new ArrayList<>() ;
    //static ServerSocket variable
    private static ServerSocket server;
    //socket server port on which it will listen
    private static int port = 9876;
    private volatile boolean cancelled;
    private MqttClient mqttClient;
    private final String GatewayVendorThingID =  "gateway-android";
    private final String GatewayPassword = "123456";
    List<MappingObject> mappingTable = new ArrayList<>();

    private final String EndNodeVendorThingID = "endnode-android";
    private final String EndNodePassword = "123456";

    private final String replaceEndNodeVendorThingID = "endnode-replace-android";
    private final String replaceEndNodePassword = "123456";

    private String mCmdID;

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class LocalBinder extends Binder {
        GatewayService getService() {
            return GatewayService.this;
        }
    }

    private final IBinder mBinder = new LocalBinder();

    @Override
    public void onCreate() {
        super.onCreate();
        toasthandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

//
//        KiiUser user = KiiUser.getCurrentUser();
//        String userID = user.getID();
//        String accessToken = user.getAccessToken();
//        TypedID typedUserID = new TypedID(TypedID.Types.USER, userID);
//        Owner owner = new Owner(typedUserID, accessToken);
//        this.gatewayApi = ApiBuilder.buildApi(getApplicationContext(), owner);
       // handler.postDelayed(onBoarding, 1000);


        runningGatewayThread.start();

        return START_NOT_STICKY;
    }

    Thread runningGatewayThread = new Thread(new Runnable() {
        @Override
        public void run() {

            mappingInit();
            loginThread.start();

            synchronized(syncObject){
                try{
                   Log.i(TAG,"Waiting for loginThread to complete...");
                    syncObject.wait();
                }catch(InterruptedException e){
                    e.printStackTrace();
                }

                Log.i(TAG," onboard start");
                onBoardThread.start();

                synchronized(syncObject){
                    try{
                        Log.i(TAG,"Waiting for onBoardThread to complete...");
                        syncObject.wait();
                    }catch(InterruptedException e){
                        e.printStackTrace();
                    }

                    socketThread.start();

                }
            }
        }
    });

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void getTID(String msg){
        Thread t = Thread.currentThread();
        long l = t.getId();
        String name = t.getName();
        Log.i(TAG,msg + " ID : " + l + " Name : " + name);
    }

    Thread socketThread = new Thread(new Runnable(){
                @Override
                public void run() {

                    try {
                        server = new ServerSocket(port);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    while (!cancelled) {

                        Log.i(TAG,"Waiting for client request");
                        //creating socket and waiting for client connection
                        Socket socket = null;
                        //read from socket to ObjectInputStream object
                        ObjectInputStream ois = null;
                        //convert ObjectInputStream object to String
                        String message = null;
                        try {
                            socket = server.accept();
                            ois = new ObjectInputStream(socket.getInputStream());
                            message = (String) ois.readObject();

                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }

                        Log.i(TAG,"Message Received: " + message);
                        String updateThingID = null;
                        if (gatewayA != null && owner != null){
                            switch(message){
                                case Config.THING_ONBOARDING:

                                    try {

                                        Log.i(TAG, "onBoardThread running");


                                        updateThingID = gatewayEnd.onboardEndNode(EndNodeVendorThingID, EndNodePassword, GatewayVendorThingID, owner.getTypedID().toString(), null, "endNodeType");
                                        //Save to mapping file
                                        if (updateThingID != null){
//                                        writeToMappingFile("EndNode " + endNodeThingID + " " + EndNodeVendorThingID + " "
//                                                + owner.getAccessToken() + " " + owner.getTypedID().toString());

                                            replaceMappingTable(new MappingObject("EndNode", updateThingID, EndNodeVendorThingID,
                                                    owner.getAccessToken(), owner.getTypedID().toString()));

                                        }


                                    } catch (ThingIFException e) {
                                        e.printStackTrace();
                                    }

                                    break;
                                case Config.LIST_ENDNODE:

                                    Log.i(TAG, "list endNode running");


                                    List<EndNode> allEndNode = null;
                                    try {
                                        allEndNode = gatewayA.listAllEndNode(0, null);
                                    } catch (ThingIFException e) {
                                        e.printStackTrace();
                                    }
                                    //Save to mapping file
                                    if (allEndNode.size() > 0){
                                        for (int i=0; i < allEndNode.size(); i++){
                                            Log.i(TAG, "endNode vendor thing ID is : " + allEndNode.get(i).getVendorThingID());
                                        }


                                    }

                                    break;
                                case Config.REPLACE_ENDNODE:

                                    Log.i(TAG, "replace endNode running");

                                    for (int i=0; i < mappingTable.size(); i++){
                                        Log.i(TAG, "endNode vendor thing ID is : " + mappingTable.get(i).getVendorThingID());
                                        if (mappingTable.get(i).getVendorThingID().equals(EndNodeVendorThingID)){
                                            updateThingID = mappingTable.get(i).getThingID();
                                        }
                                    }

                                    if (updateThingID != null){
                                        try {
                                            String retEndNodeID = gatewayA.replaceEndNode(updateThingID, replaceEndNodeVendorThingID, replaceEndNodePassword);


                                            replaceMappingTableWithOriginalVendorThingID(new MappingObject("EndNode", retEndNodeID, replaceEndNodeVendorThingID,
                                                    owner.getAccessToken(), owner.getTypedID().toString()), EndNodeVendorThingID);
                                        } catch (ThingIFException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    break;
                                case Config.READ_MAPPING_FILE:
                                    readMappingFile();
                                    break;
                                case Config.DEL_ENDNODE:

                                    if (mappingTable.size() > 0) {
                                        for (int i=0; i< mappingTable.size(); i++){
                                            if (mappingTable.get(i).getVendorThingID().equals(EndNodeVendorThingID)){
                                                updateThingID = mappingTable.get(i).getThingID();
                                                try {
                                                    gatewayA.delEndNode(updateThingID);
                                                } catch (ThingIFException e) {
                                                    e.printStackTrace();
                                                }

                                                delteMappingTableWithVendorThingID(EndNodeVendorThingID);
                                            }
                                        }
                                    }

                                    break;
                                case Config.UPDATE_ENDNODE_CONNECTION_STATUS:

                                    if (mappingTable.size() > 0) {
                                        for (int i=0; i< mappingTable.size(); i++){
                                            if (mappingTable.get(i).getVendorThingID().equals(EndNodeVendorThingID)){
                                                updateThingID = mappingTable.get(i).getThingID();
                                                try {
                                                    gatewayA.updateEndNodeConnectionStatus(updateThingID,true);
                                                } catch (ThingIFException e) {
                                                    e.printStackTrace();
                                                }


                                            }
                                        }
                                    }
                                    break;

                                case Config.UPDATE_ENDNODE_STATES:

                                    if (mappingTable.size() > 0) {
                                        for (int i=0; i< mappingTable.size(); i++){
                                            if (mappingTable.get(i).getVendorThingID().equals(EndNodeVendorThingID)){
                                                updateThingID = mappingTable.get(i).getThingID();
                                                try {
                                                    gatewayA.updateEndNodeStates(updateThingID);
                                                } catch (ThingIFException e) {
                                                    e.printStackTrace();
                                                }


                                            }
                                        }
                                    }
                                    break;

                                case Config.GET_ENDNODE_STATES:

                                    if (mappingTable.size() > 0) {
                                        for (int i=0; i< mappingTable.size(); i++){
                                            if (mappingTable.get(i).getVendorThingID().equals(EndNodeVendorThingID)){
                                                updateThingID = mappingTable.get(i).getThingID();
                                                try {
                                                    gatewayA.getEndNodeStates(updateThingID);
                                                } catch (ThingIFException e) {
                                                    e.printStackTrace();
                                                }


                                            }
                                        }
                                    }
                                    break;
                                case Config.SEND_CMD_TO_ENDNODE:

                                    if (mappingTable.size() > 0) {
                                        for (int i=0; i< mappingTable.size(); i++){
                                            if (mappingTable.get(i).getVendorThingID().equals(EndNodeVendorThingID)){
                                                updateThingID = mappingTable.get(i).getThingID();

                                                Schema schema = buildSchema();

                                                List<Action> actions = new ArrayList<Action>();

                                                TurnPower action1 = new TurnPower();
                                                action1.power = true;

                                                SetColor action2 = new SetColor();
                                                action2.color = new int[]{20,50,200};

                                                SetBrightness action3 = new SetBrightness();
                                                action3.brightness = 120;

                                                SetColorTemperature action4 = new SetColorTemperature();
                                                action4.colorTemperature = 35;

                                                actions.add(action1);
                                                actions.add(action2);
                                                actions.add(action3);
                                                actions.add(action4);
                                                String cmdID = null;
                                                try {
                                                    cmdID = gatewayA.sendCmdToEndNode(updateThingID, Config.SCHEMA_NAME, Config.SCHEMA_VERSION, actions, owner, schema);
                                                } catch (ThingIFException e) {
                                                    e.printStackTrace();
                                                }

                                                if (cmdID != null){
                                                    mCmdID = cmdID;
                                                    message = cmdID;
                                                }

                                            }
                                        }
                                    }
                                    break;

                                case Config.UPDATE_CMD_RESULT:

                                    if (mappingTable.size() > 0) {
                                        for (int i=0; i< mappingTable.size(); i++){
                                            if (mappingTable.get(i).getVendorThingID().equals(EndNodeVendorThingID)){
                                                updateThingID = mappingTable.get(i).getThingID();

                                                Schema schema = buildSchema();

                                                List<Action> actions = new ArrayList<>();

                                                TurnPower action1 = new TurnPower();


                                                SetColor action2 = new SetColor();


                                                SetBrightness action3 = new SetBrightness();


                                                SetColorTemperature action4 = new SetColorTemperature();


                                                actions.add(action1);
                                                actions.add(action2);
                                                actions.add(action3);
                                                actions.add(action4);
                                                try {
                                                    if (mCmdID != null){
                                                        gatewayA.updateCmdResult(updateThingID, mCmdID, actions);

                                                    }
                                                } catch (ThingIFException e) {
                                                    e.printStackTrace();
                                                }


                                            }
                                        }
                                    }
                                    break;

                                default:
                                    Log.i(TAG,"No Support event here");
                                    break;
                            }
                        }else{
                            Log.i(TAG," login first");
                        }

                        //create ObjectOutputStream object
                        ObjectOutputStream oos = null;
                        try {
                            oos = new ObjectOutputStream(socket.getOutputStream());
                            //write object to Socket
                            oos.writeObject("Hi Client " + message);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        //close resources
                        try {
                            ois.close();
                            oos.close();
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        //terminate the server if client sends exit request
                        if (message.equalsIgnoreCase("exit")){
                            cancelled = true;
                        }
                    }


                }
            });

    Thread loginThread = new Thread(new Runnable(){
        @Override
        public void run() {
            //TODO
            // use JP , gateway API
            synchronized(syncObject) {
                getTID("login thread ");
                KiiApp app = new KiiApp(Config.APP_ID, Config.APP_KEY, Site.JP);
                //KiiApp app = new KiiApp(Config.APP_ID, Config.APP_KEY, "127.0.0.1");
                GatewayAPIBuilder gatewayBuilder = GatewayAPIBuilder.newBuilder(GatewayService.this, app, "127.0.0.1", "william.wu", "1qaz@WSX");
                try {
                    gatewayA = gatewayBuilder.build4Gateway();
                    gatewayEnd = gatewayBuilder.build4EndNode();

                } catch (ThingIFException e) {
                    e.printStackTrace();
                }

                // sign in user
                final GatewayAPI4Gateway finalGatewayA = gatewayA;


                try {
                    KiiUser user = KiiUser.logIn("william.wu", "1qaz@WSX");
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
                        syncObject.notify();
                    }


                } catch (IOException e) {
                    // Sign-in failed for some reasons
                    // Please check IOExecption to see what went wrong...
                } catch (AppException e) {
                    // Sign-in failed for some reasons
                    // Please check AppException to see what went wrong...
                }

            }
        }
    });

    Thread onBoardThread = new Thread(new Runnable() {
        @Override
        public void run() {
            // finalGatewayA.login("VENDOR_THING_ID:gatewaytest2", "123456");
            synchronized(syncObject) {
                if (gatewayA != null && owner != null){
                    try {
                        //getTID("onBoardThread ");
                        Log.i(TAG, "onBoardThread running");
                        String gatewayThingID = gatewayA.onboardGateway(GatewayVendorThingID, GatewayPassword, "led", null, owner.getTypedID().toString());

                        if (gatewayThingID != null){
                            mappingTable.clear();
                            mappingTable.add(new MappingObject("Gateway", gatewayThingID, GatewayVendorThingID,
                                    owner.getAccessToken(), owner.getTypedID().toString()));
                            //WriteSharedPreferences("gatewayThingID", gatewayThingID);

                            List<EndNode> endNodes = getEndNodeList();
                            if (endNodes != null){
                                for (int i=0; i< endNodes.size(); i++){
                                    mappingTable.add(i, new MappingObject("EndNode", endNodes.get(i).getThingID(),
                                            endNodes.get(i).getVendorThingID(), owner.getAccessToken(), owner.getTypedID().toString()));
                                }
                            }

                            newMappingFile(mappingTable);

                            //MQTT receive connections established.
                            mqttClient = new MqttClient(GatewayService.this,gatewayA.getmGateway().getMqttEndpoint());
                            mqttClient.connect();
                        }

                    } catch (ThingIFException e) {
                        e.printStackTrace();
                    }

                    syncObject.notify();
                }
            }

        }
    });

    private void readMappingFile(){
        if (fileExists(this,Config.MAPPING_FILE_NAME)){
            FileInputStream fin = null;
            BufferedReader reader;

            try {
                fin = openFileInput(Config.MAPPING_FILE_NAME);

                reader = new BufferedReader(new InputStreamReader(fin));
                String line;
                while ((line = reader.readLine()) != null) {
                    //arr[0] should be name , arr[1] be thingID
                    if (line.length() > 0){
                        String arr[] = line.split(" ",-1);
                        MappingObject obj = new MappingObject(arr[0], arr[1], arr[2], arr[3], arr[4]);
                        Log.i(TAG, "position : " + obj.getPosition() +
                                " ThingID: " + obj.getThingID() +
                                " VendorThingID: " + obj.getVendorThingID() +
                                " AccessToken: " +obj.getAccessToken() +
                                " ownerID: " + obj.getOwnerID());
                    }
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void mappingInit(){
        //listThingsUnderGateway(gatewayApi);
        //getEndNodeList();
        mappingAllRestore();
    }

    private List<EndNode> getEndNodeList(){
        try {
            List<EndNode> endNodes = gatewayA.listAllEndNode(0, null);
            for (int i=0; i< endNodes.size(); i++){
                Log.i(TAG,"vendor thingID :" + endNodes.get(i).getVendorThingID() +
                        "thingID : " + endNodes.get(i).getThingID());
            }
            return endNodes;

        } catch (ThingIFException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void delteMappingTableWithVendorThingID(String vendorThingID){
        boolean finded = false;
        for (int i = 0; i < mappingTable.size(); i++) {
            if (mappingTable.get(i).getVendorThingID().equals(vendorThingID)) {
                finded = true;
                mappingTable.remove(i);
            }
        }

        if (finded){
            if (fileExists(this,Config.MAPPING_FILE_NAME)){
                fileDel(this,Config.MAPPING_FILE_NAME);
            }

            for (int i=0; i < mappingTable.size(); i++) {
                writeToMappingFile(mappingTable.get(i).getPosition() + " " + mappingTable.get(i).getThingID() + " " +
                        mappingTable.get(i).getVendorThingID() + " " + mappingTable.get(i).getAccessToken() + " " +
                        mappingTable.get(i).getOwnerID());
            }
        }
    }

    /***
     * use for replace function , got original vendorthingID , replace it on table.
     * @param obj
     * @param originalVendorThingID
     */
    private void replaceMappingTableWithOriginalVendorThingID(MappingObject obj , String originalVendorThingID){
        if (mappingTable.size() > 0) {
            boolean finded = false;
            for (int i = 0; i < mappingTable.size(); i++) {

                if (mappingTable.get(i).getVendorThingID().equals(originalVendorThingID)) {
                    finded = true;
                    mappingTable.get(i).setThingID(obj.getThingID());
                    mappingTable.get(i).setVendorThingID(obj.getVendorThingID());
                }
            }

            if (!finded){
                mappingTable.add(obj);
            }
        }

            //If table list not finded match object , write new one.
           // if (!finded){

        if (fileExists(this,Config.MAPPING_FILE_NAME)){
            fileDel(this,Config.MAPPING_FILE_NAME);
        }

        for (int i=0; i < mappingTable.size(); i++) {
            writeToMappingFile(mappingTable.get(i).getPosition() + " " + mappingTable.get(i).getThingID() + " " +
                    mappingTable.get(i).getVendorThingID() + " " + mappingTable.get(i).getAccessToken() + " " +
                    mappingTable.get(i).getOwnerID());
        }

           // }

    }

    private void newMappingFile(List<MappingObject> newMappingTable){

        if (fileExists(this,Config.MAPPING_FILE_NAME)){
            fileDel(this, Config.MAPPING_FILE_NAME);
        }

        for (int i=0; i < newMappingTable.size(); i++) {
            writeToMappingFile(newMappingTable.get(i).getPosition() + " " + newMappingTable.get(i).getThingID() + " " +
                    newMappingTable.get(i).getVendorThingID() + " " + newMappingTable.get(i).getAccessToken() + " " +
                    newMappingTable.get(i).getOwnerID());
        }

    }

    private void replaceMappingTable(MappingObject obj){
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
                Log.i(TAG," Not find match object :"  + obj.getVendorThingID() +  ", write new one");
                mappingTable.add(obj);
            }

        }
            //If table list not finded match object , write new one.
//

        if (fileExists(this,Config.MAPPING_FILE_NAME)){
            fileDel(this, Config.MAPPING_FILE_NAME);
        }

        for (int i=0; i < mappingTable.size(); i++) {
            writeToMappingFile(mappingTable.get(i).getPosition() + " " + mappingTable.get(i).getThingID() + " " +
                    mappingTable.get(i).getVendorThingID() + " " + mappingTable.get(i).getAccessToken() + " " +
                    mappingTable.get(i).getOwnerID());
        }


    }

    private void writeToMappingFile(String data){
        try {
            FileOutputStream fOut = openFileOutput(Config.MAPPING_FILE_NAME,MODE_PRIVATE | MODE_APPEND);
            fOut.write(System.getProperty("line.separator").getBytes());
            fOut.write(data.getBytes());
            fOut.close();
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void mappingAllRestore(){

        if (fileExists(this,Config.MAPPING_FILE_NAME)){
            // TODO
            // restore endNode list
            FileInputStream fin = null;
            BufferedReader reader;
//
//            String gatewayID = ReadSharedPreferences("gatewayThingID");
//            Log.i(TAG, " gateway ID : " + gatewayID);

            try {
                fin = openFileInput(Config.MAPPING_FILE_NAME);

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
                    Log.i(TAG,"position : " + mappingTable.get(i).getPosition() +
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

    public String readFileData(String fileName){
        String result="";
        try {
            FileInputStream fin = openFileInput(fileName);

            int length = fin.available();
            byte[] buffer = new byte[length];
            fin.read(buffer);

//            result = EncodingUtils.getString(buffer, ENCODING);
//
//            String str = new String(bytes, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }




    public Schema buildSchema(){
        SchemaBuilder schemaBuilder = SchemaBuilder.newSchemaBuilder(Config.THING_TYPE,
                Config.SCHEMA_NAME, Config.SCHEMA_VERSION, LightState.class);
        schemaBuilder.addActionClass(TurnPower.class, TurnPowerResult.class);
        schemaBuilder.addActionClass(SetBrightness.class, SetBrightnessResult.class);
        schemaBuilder.addActionClass(SetColor.class, SetColorResult.class);
        schemaBuilder.addActionClass(SetColorTemperature.class, SetColorTemperatureResult.class);

        return schemaBuilder.build();
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
