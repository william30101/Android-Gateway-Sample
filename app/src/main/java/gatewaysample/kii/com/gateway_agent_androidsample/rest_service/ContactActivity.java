package gatewaysample.kii.com.gateway_agent_androidsample.rest_service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.kii.thingif.Owner;
import com.kii.thingif.gateway.GatewayAPI4EndNode;
import com.kii.thingif.gateway.GatewayAPI4Gateway;

import org.restlet.Component;
import org.restlet.data.Protocol;

import java.util.ArrayList;
import java.util.List;


import gatewaysample.kii.com.gateway_agent_androidsample.GatewayService;
import gatewaysample.kii.com.gateway_agent_androidsample.R;
import gatewaysample.kii.com.gateway_agent_androidsample.converter.MainConverter;
import gatewaysample.kii.com.gateway_agent_androidsample.mqtt.MqttClient;
import gatewaysample.kii.com.gateway_agent_androidsample.utils.Config;


public class ContactActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private final String TAG= "ContactActivity";
    ListView listView;
    private ArrayAdapter<String> listAdapter;
    private String[] list = {Config.REGISTER_CMD,Config.ENDNODE_ONBOARD, Config.GET_GATEWAY_ID,
            Config.ONBOARD_SUCCESS, Config.SEARCH_ENDNODE};
    private Toolbar toolbar;
    private String accessToken = "jaiwefjia";

    GatewayService gatewayService;

    private MqttClient mqttClient;
    private final String GatewayVendorThingID =  "gateway-android";
    private final String GatewayPassword = "123456";
    List<MappingObject> mappingTable = new ArrayList<>();

    private final String EndNodeVendorThingID = "endnode-android";
    private final String EndNodePassword = "123456";

    private final String replaceEndNodeVendorThingID = "endnode-replace-android";
    private final String replaceEndNodePassword = "123456";

    private String syncObject = "sync";
    GatewayAPI4Gateway gatewayA;
    GatewayAPI4EndNode gatewayEnd;
    Owner owner;
    ThreadCall threadCall;
    RestThread restThread;
    public static final int resultNum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact);

        initToolBar();
        initUI();
        GatewayServiceStart();


       //runningGatewayThread.start();
    }


    private void restServiceStart(){
        try {
            Component component = new Component();
            component.getServers().add(Protocol.HTTP, 8080);
            component.getDefaultHost().attach("/",
                    new BookServiceRestletApplication(gatewayService));

            component.start();
        } catch (Exception e) {
            Log.i(TAG,"!!! Could not start restlet based server");
            e.printStackTrace();
        }
    }

    private void initUI(){
        listView = (ListView) findViewById(R.id.list_contact);
        listAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(this);

        threadCall = new ThreadCall(this);


    }

    private void initToolBar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Gateway_App");

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        String name = (String) parent.getItemAtPosition(position);

        RestThread.mStop = false;

        switch(name){
            case Config.REGISTER_CMD:
                if (restThread != null){
                    if (restThread.getState() == Thread.State.TERMINATED){
                        restThread = new RestThread("token", "william.wu","1qaz@WSX");
                        restThread.start();
                    }if (!restThread.isAlive()) {
                        restThread.start();
                    }
                }else{
                    restThread = new RestThread("token", "william.wu", "1qaz@WSX");
                    restThread.start();
                }
                break;
            case Config.ENDNODE_ONBOARD:
                if (restThread != null){
                    if (restThread.getState() == Thread.State.TERMINATED){
                        restThread = new RestThread("apps/"+ Config.APP_ID +"/gateway/onboarding");
                        restThread.start();
                    }if (!restThread.isAlive()) {
                        restThread.start();
                    }
                }else{
                    restThread = new RestThread("apps/"+ Config.APP_ID +"/gateway/onboarding");
                    restThread.start();
                }
                break;
            case Config.GET_GATEWAY_ID:
                if (restThread != null){
                    if (restThread.getState() == Thread.State.TERMINATED){
                        restThread = new RestThread("apps/"+ Config.APP_ID +"/gateway/id");
                        restThread.start();
                    }if (!restThread.isAlive()) {
                        restThread.start();
                    }
                }else{
                    restThread = new RestThread("apps/"+ Config.APP_ID +"/gateway/id");
                    restThread.start();
                }
                break;
            case Config.GET_PENDING_ENDNODE:
                if (restThread != null){
                    if (restThread.getState() == Thread.State.TERMINATED){
                        restThread = new RestThread("apps/"+ Config.APP_ID +"/gateway/end-nodes/pending");
                        restThread.start();
                    }if (!restThread.isAlive()) {
                        restThread.start();
                    }
                }else{
                    restThread = new RestThread("apps/"+ Config.APP_ID +"/gateway/end-nodes/pending");
                    restThread.start();
                }
                break;
            case Config.ONBOARD_SUCCESS:
                if (restThread != null){
                    if (restThread.getState() == Thread.State.TERMINATED){
                        restThread = new RestThread("apps/"+ Config.APP_ID +"/gateway/end-nodes/VENDOR_THING_ID:"+EndNodeVendorThingID);
                        restThread.start();
                    }if (!restThread.isAlive()) {
                        restThread.start();
                    }
                }else{
                    restThread = new RestThread("apps/"+ Config.APP_ID +"/gateway/end-nodes/VENDOR_THING_ID:"+EndNodeVendorThingID);
                    restThread.start();
                }
                break;
            case Config.SEARCH_ENDNODE:
                Intent intent = new Intent();
                intent.setClass(ContactActivity.this, MainConverter.class);
                startActivityForResult(intent, resultNum);
                break;
            default:
                break;
        }





    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == resultNum){
                Log.i(TAG,"return from converter");
            }
        }
    }


    private void GatewayServiceStart(){
        Intent serviceIntent = new Intent(ContactActivity.this, GatewayService.class);
        startService(serviceIntent); //Starting the service
        bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE); //Binding to the service!

    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            Toast.makeText(ContactActivity.this, "onServiceConnected called", Toast.LENGTH_SHORT).show();
            // We've binded to LocalService, cast the IBinder and get LocalService instance
            GatewayService.LocalBinder binder = (GatewayService.LocalBinder) service;
            gatewayService = binder.getServiceInstance(); //Get instance of your service!
            //gatewayService.registerClient(ContactActivity.this); //Activity register in the service as client for callabcks!
            restServiceStart();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Toast.makeText(ContactActivity.this, "onServiceDisconnected called", Toast.LENGTH_SHORT).show();

        }
    };

    public GatewayService getGatewayService() {
        return gatewayService;
    }

    public Handler mShowHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message message) {
            switch (message.what){
                case 0:
                    Toast.makeText(ContactActivity.this, (String)message.obj, Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent serviceIntent = new Intent(ContactActivity.this, GatewayService.class);
//        stopService(serviceIntent);

        unbindService(mConnection);
        stopService(serviceIntent);
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
