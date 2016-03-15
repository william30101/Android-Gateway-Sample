package gatewaysample.kii.com.gateway_agent_androidsample.rest_service;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.kii.cloud.storage.KiiUser;
import com.kii.thingif.MediaTypes;
import com.kii.thingif.Owner;
import com.kii.thingif.PushBackend;
import com.kii.thingif.ThingIFAPI;
import com.kii.thingif.TypedID;
import com.kii.thingif.command.Action;
import com.kii.thingif.command.Command;
import com.kii.thingif.exception.ThingIFException;
import com.kii.thingif.gateway.GatewayAPI4EndNode;
import com.kii.thingif.gateway.GatewayAPI4Gateway;
import com.kii.thingif.internal.GsonRepository;
import com.kii.thingif.internal.http.IoTRestRequest;
import com.kii.thingif.internal.utils.JsonUtils;
import com.kii.thingif.internal.utils.Path;
import com.kii.thingif.schema.Schema;
import com.kii.thingif.schema.SchemaBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.Component;
import org.restlet.data.Protocol;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import de.greenrobot.event.EventBus;
import gatewaysample.kii.com.gateway_agent_androidsample.GatewayService;
import gatewaysample.kii.com.gateway_agent_androidsample.R;
import gatewaysample.kii.com.gateway_agent_androidsample.RegistrationIntentService;
import gatewaysample.kii.com.gateway_agent_androidsample.mqtt.MqttClient;
import gatewaysample.kii.com.gateway_agent_androidsample.smart_light_demo.ApiBuilder;
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
import gatewaysample.kii.com.gateway_agent_androidsample.utils.MappingObject;
import gatewaysample.kii.com.gateway_agent_androidsample.utils.MyControllerEvent;
import gatewaysample.kii.com.gateway_agent_androidsample.utils.MyEvent;


public class ContactActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private final static String TAG = "ContactActivity";
    ListView listView;
    private ArrayAdapter<String> listAdapter;
    private String[] list = {Config.REGISTER_CMD, Config.GET_GATEWAY_ID,
            Config.SEARCH_ENDNODE, Config.GET_PENDING_ENDNODE, Config.SEND_CMD_TO_ENDNODE};
    private Toolbar toolbar;

    GatewayService gatewayService;

    private MqttClient mqttClient;
    private final String GatewayVendorThingID = "gateway-android";
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
    private static RestThread restThread;
    private static Dialog dialog;

    private EventBus mEventBus;
    List<Action> actions = new ArrayList<>();

    private GoogleCloudMessaging gcm;
    public ThingIFAPI thingApi;

    private BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact);

        mEventBus = EventBus.getDefault();
        mEventBus.register(this);

        initToolBar();
        initUI();
        initGCM();
        //GatewayServiceStart();

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String errorMessage = intent.getStringExtra("ErrorMessage");
                final String token = intent.getStringExtra("token");

                Log.e("GCMTest", "Registration completed:" + errorMessage + " token: " + token);
                if (errorMessage != null) {
                    Toast.makeText(ContactActivity.this, "Error push registration:" + errorMessage, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ContactActivity.this, "Succeeded push registration", Toast.LENGTH_LONG).show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            try {
                                thingApi.installPush(token, PushBackend.GCM);
                                //thingApi.uninstallPush(token);
                            } catch (ThingIFException e) {
                                e.printStackTrace();
                            }

//                                    boolean development = true;
//                                    try {
//                                        KiiUser.pushInstallation(development).install(token);
//                                    } catch (IOException e) {
//                                        e.printStackTrace();
//                                    } catch (BadRequestException e) {
//                                        e.printStackTrace();
//                                    } catch (UnauthorizedException e) {
//                                        e.printStackTrace();
//                                    } catch (ForbiddenException e) {
//                                        e.printStackTrace();
//                                    } catch (ConflictException e) {
//                                        e.printStackTrace();
//                                    } catch (NotFoundException e) {
//                                        e.printStackTrace();
//                                    } catch (UndefinedException e) {
//                                        e.printStackTrace();
//                                    }


                        }
                    }).start();

                }
            }
        };

        //runningGatewayThread.start();
    }

    private void initGCM(){
        KiiUser user = KiiUser.getCurrentUser();
        String userID = user.getID();
        String accessToken = user.getAccessToken();
        TypedID typedUserID = new TypedID(TypedID.Types.USER, userID);
        owner = new Owner(typedUserID, accessToken);

        gcmRegister();

    }

    private void gcmRegister(){
        gcm = GoogleCloudMessaging.getInstance(this.getApplicationContext());
        thingApi = ApiBuilder.buildApi(getApplicationContext(), owner);

        Intent intent = new Intent(ContactActivity.this, RegistrationIntentService.class);
        this.startService(intent);



    }

    private void restServiceStart() {
        try {
            Component component = new Component();
            component.getServers().add(Protocol.HTTP, 8080);
            component.getDefaultHost().attach("/",
                    new BookServiceRestletApplication(gatewayService));

            component.start();
        } catch (Exception e) {
            Log.i(TAG, "!!! Could not start restlet based server");
            e.printStackTrace();
        }
    }

    private void initUI() {
        listView = (ListView) findViewById(R.id.list_contact);
        listAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(this);
        threadCall = new ThreadCall(this);


    }

    private void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Gateway_App");

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        String name = (String) parent.getItemAtPosition(position);

        RestThread.mStop = false;

        switch (name) {
            case Config.REGISTER_CMD:



                if (restThread != null) {
                    if (restThread.getState() == Thread.State.TERMINATED) {
                        restThread = new RestThread("token", "william.wu", "1qaz@WSX");
                        restThread.start();
                    }
                    if (!restThread.isAlive()) {
                        restThread.start();
                    }
                } else {
                    restThread = new RestThread("token", "william.wu", "1qaz@WSX");
                    restThread.start();
                }
                break;
//            case Config.ENDNODE_ONBOARD:
//                if (restThread != null) {
//                    if (restThread.getState() == Thread.State.TERMINATED) {
//                        restThread = new RestThread("apps/" + Config.APP_ID + "/gateway/onboarding");
//                        restThread.start();
//                    }
//                    if (!restThread.isAlive()) {
//                        restThread.start();
//                    }
//                } else {
//                    restThread = new RestThread("apps/" + Config.APP_ID + "/gateway/onboarding");
//                    restThread.start();
//                }
//                break;
            case Config.GET_GATEWAY_ID:
                if (restThread != null) {
                    if (restThread.getState() == Thread.State.TERMINATED) {
                        restThread = new RestThread("apps/" + Config.APP_ID + "/gateway/id");
                        restThread.start();
                    }
                    if (!restThread.isAlive()) {
                        restThread.start();
                    }
                } else {
                    restThread = new RestThread("apps/" + Config.APP_ID + "/gateway/id");
                    restThread.start();
                }
                break;
            case Config.GET_PENDING_ENDNODE:
                if (restThread != null) {
                    if (restThread.getState() == Thread.State.TERMINATED) {
                        restThread = new RestThread("apps/" + Config.APP_ID + "/gateway/end-nodes/pending");
                        restThread.start();
                    }
                    if (!restThread.isAlive()) {
                        restThread.start();
                    }
                } else {
                    restThread = new RestThread("apps/" + Config.APP_ID + "/gateway/end-nodes/pending");
                    restThread.start();
                }
                break;
            case Config.ONBOARD_SUCCESS:
                if (restThread != null) {
                    if (restThread.getState() == Thread.State.TERMINATED) {
                        restThread = new RestThread("apps/" + Config.APP_ID + "/gateway/end-nodes/VENDOR_THING_ID:" + EndNodeVendorThingID);
                        restThread.start();
                    }
                    if (!restThread.isAlive()) {
                        restThread.start();
                    }
                } else {
                    restThread = new RestThread("apps/" + Config.APP_ID + "/gateway/end-nodes/VENDOR_THING_ID:" + EndNodeVendorThingID);
                    restThread.start();
                }
                break;
            case Config.SEARCH_ENDNODE:
//                Intent intent = new Intent();
//                intent.setClass(ContactActivity.this, MainConverter.class);
//                startActivityForResult(intent, resultNum);

//                ClientSocketActivity client = new ClientSocketActivity(this);
//                client.onStart();
                dialog = ProgressDialog.show(ContactActivity.this,
                        "Searching", "wait...", true);
                dialog.show();
                if (restThread != null) {
                    if (restThread.getState() == Thread.State.TERMINATED) {
                        restThread = new RestThread("apps/" + Config.APP_ID + "/gateway/end-nodes/discovery");
                        restThread.start();
                    }
                    if (!restThread.isAlive()) {
                        restThread.start();
                    }
                } else {
                    restThread = new RestThread("apps/" + Config.APP_ID + "/gateway/end-nodes/discovery");
                    restThread.start();
                }


                break;
            case Config.SEND_CMD_TO_ENDNODE:
                // first : get device list from gateway.
                if (restThread != null) {
                    if (restThread.getState() == Thread.State.TERMINATED) {
                        restThread = new RestThread("apps/" + Config.APP_ID + "/gateway/end-nodes/listOnBoardDevice");
                        restThread.start();
                    }
                    if (!restThread.isAlive()) {
                        restThread.start();
                    }
                } else {
                    restThread = new RestThread("apps/" + Config.APP_ID + "/gateway/end-nodes/listOnBoardDevice");
                    restThread.start();
                }


                // Second : send cmd to target endNode on eventbus.

                break;
            default:
                break;
        }


    }

    private void createDialog(final String thingID){

        // custom dialog
        final Dialog dialog = new Dialog(ContactActivity.this);
        dialog.setContentView(R.layout.cmd_dialog);
        dialog.setTitle("CMD Dialog");


//        LinearLayout ll = (LinearLayout)dialog.findViewById(R.id.cmd_dialog_linear);
//        ll.getLayoutParams().width=360;

//        // set the custom dialog components - text, image and button
//        TextView text = (TextView) dialog.findViewById(R.id.text);
//        text.setText("CMD controll");

    Switch powerSwitch = (Switch) dialog.findViewById(R.id.power_switch);
        final TurnPower action1 = new TurnPower();
        powerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Log.d(TAG,"power on");
                    action1.power = true;
                    //sendCmdFromDialog(thingID);

                } else {
                    Log.d(TAG,"power off");
                    action1.power = false;
                   // sendCmdFromDialog(thingID);
                }
            }
        });

        actions.add(action1);

        //Use simulate data.

        SetColor action2 = new SetColor();
        action2.color = new int[]{20, 50, 200};

        SetBrightness action3 = new SetBrightness();
        action3.brightness = 120;

        SetColorTemperature action4 = new SetColorTemperature();
        action4.colorTemperature = 35;

        actions.add(action2);
        actions.add(action3);
        actions.add(action4);

        // end simulate data


        Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dialog.dismiss();
                sendCmdFromDialog(thingID);
            }
        });
        Window dialogWindow = dialog.getWindow();

        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        p.height = (int) (d.getHeight() * 0.6); // 高度设置为屏幕的0.6
        p.width = (int) (d.getWidth() * 0.65); // 宽度设置为屏幕的0.65
        dialogWindow.setAttributes(p);


        dialog.show();

    }

    private void sendCmdFromDialog(String thingID){

        //Add each action here.


        RestThread.mStop = false;
        if (restThread != null) {
            if (restThread.getState() == Thread.State.TERMINATED) {
                restThread = new RestThread("apps/" + Config.APP_ID + "/gateway/end-nodes/sendCmd/" + thingID, actions);
                restThread.start();
            }
            if (!restThread.isAlive()) {
                restThread.start();
            }
        } else {
            restThread = new RestThread("apps/" + Config.APP_ID + "/gateway/end-nodes/sendCmd" + thingID, actions);
            restThread.start();
        }

    }

    private void GatewayServiceStart() {
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


    //Get from RestThread , receive information in here.
    public void onEventMainThread(MyControllerEvent event) {

        EventType eventType = event.getMyEventString();
        switch (eventType.getWho()) {
            case Config.SEND_FROM_GET_TOKEN:

                String token = (String) eventType.getBody();
                Toast.makeText(this, "token : " + token, Toast.LENGTH_LONG).show();
                break;
            case Config.SEND_FROM_GET_GATEWAY_ID:
                String gatewayID = (String) eventType.getBody();
                Toast.makeText(this, "gatewayID : " + gatewayID, Toast.LENGTH_LONG).show();
                break;
            case Config.SEND_FROM_ENDNODE_ONBOARD:
                String endNodeThingID = (String) eventType.getBody();
                Toast.makeText(this, "endNodeThingID : " + endNodeThingID, Toast.LENGTH_LONG).show();
                break;
            case Config.SEND_FROM_GET_PENDING_DEVICE:

                List<String> pendingDevice = new ArrayList<>();
                JSONObject pendingdeviceJson = (JSONObject) eventType.getBody();

                try {
                    JSONArray arr = new JSONArray(pendingdeviceJson.getString("pendingEndNodes"));
                    for (int i = 0; i < arr.length(); i++) {
                        pendingDevice.add(arr.getJSONObject(i).optString("vendorThingID"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.d(TAG, "pending device size : " + pendingDevice.size());
                if (dialog != null) {
                    dialog.dismiss();
                }
                if (pendingDevice.size() > 0) {
                    final List<String> deviceStrFinal = pendingDevice;
                    new AlertDialog.Builder(this)
                            .setItems(deviceStrFinal.toArray(new String[deviceStrFinal.size()]), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String name = deviceStrFinal.get(which);
                                    Toast.makeText(ContactActivity.this, name, Toast.LENGTH_SHORT).show();

                                    RestThread.mStop = false;
                                    if (restThread != null) {
                                        if (restThread.getState() == Thread.State.TERMINATED) {
                                            //TODO create a API for onboarding endnode.
                                            restThread = new RestThread("apps/" + Config.APP_ID + "/gateway/onboarding/" + name);
                                            restThread.start();
                                        }
                                        if (!restThread.isAlive()) {
                                            restThread.start();
                                        }
                                    } else {
                                        //TODO create a API for onboarding endnode.
                                        restThread = new RestThread("apps/" + Config.APP_ID + "/gateway/onboarding/" + name);
                                        restThread.start();
                                    }


                                }
                            })
                            .show();
                }


                break;
            case Config.SEND_FROM_DISCOVERY:

                List<String> deviceStr = new ArrayList<>();
                JSONObject deviceJson = (JSONObject) eventType.getBody();

                try {
                    JSONArray arr = new JSONArray(deviceJson.getString("devices"));
                    for (int i = 0; i < arr.length(); i++) {
                        deviceStr.add(arr.getJSONObject(i).optString("name"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.d(TAG, "bt device size : " + deviceStr.size());
                if (dialog != null) {
                    dialog.dismiss();
                }
                if (deviceStr.size() > 0) {
                    final List<String> deviceStrFinal = deviceStr;
                    new AlertDialog.Builder(this)
                            .setItems(deviceStrFinal.toArray(new String[deviceStrFinal.size()]), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String name = deviceStrFinal.get(which);
                                    Toast.makeText(ContactActivity.this, name, Toast.LENGTH_SHORT).show();

                                    RestThread.mStop = false;
                                    if (restThread != null) {
                                        if (restThread.getState() == Thread.State.TERMINATED) {
                                            restThread = new RestThread("apps/" + Config.APP_ID + "/gateway/end-nodes/connect:" + name);
                                            restThread.start();
                                        }
                                        if (!restThread.isAlive()) {
                                            restThread.start();
                                        }
                                    } else {
                                        restThread = new RestThread("apps/" + Config.APP_ID + "/gateway/end-nodes/connect:" + name);
                                        restThread.start();
                                    }


                                }
                            })
                            .show();
                }

                break;
            case Config.SEND_FROM_CONNECT_DEVICE:
                break;
            //When thing onboard done. send cmd to EndNode.
            case Config.SEND_FROM_GET_ONBOARD_LIST:


                List<String> devicesNameStr = new ArrayList<>();
                List<String> devicesStr = new ArrayList<>();
                JSONObject devicesJson = (JSONObject) eventType.getBody();

                try {
                    JSONArray arr = new JSONArray(devicesJson.getString("devices"));
                    for (int i = 0; i < arr.length(); i++) {
                        if (arr.getJSONObject(i).optString("position") != "Gateway"){
                            devicesNameStr.add(arr.getJSONObject(i).optString("vendorThingID"));
                            devicesStr.add(arr.getJSONObject(i).optString("thingID"));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (devicesStr.size() > 0) {
                    final List<String> deviceNameStrFinal = devicesNameStr;
                    final List<String> deviceStrFinal = devicesStr;
                    new AlertDialog.Builder(this)
                            .setItems(deviceNameStrFinal.toArray(new String[deviceNameStrFinal.size()]), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String thingID = deviceStrFinal.get(which);
                                    Toast.makeText(ContactActivity.this, thingID, Toast.LENGTH_SHORT).show();

                                    createDialog(thingID);

//                                    RestThread.mStop = false;
//                                    if (restThread != null) {
//                                        if (restThread.getState() == Thread.State.TERMINATED) {
//                                            restThread = new RestThread("apps/" + Config.APP_ID + "/gateway/end-nodes/sendCmd/" + thingID, getActions());
//                                            restThread.start();
//                                        }
//                                        if (!restThread.isAlive()) {
//                                            restThread.start();
//                                        }
//                                    } else {
//                                        restThread = new RestThread("apps/" + Config.APP_ID + "/gateway/end-nodes/sendCmd" + thingID, getActions());
//                                        restThread.start();
//                                    }


                                }
                            })
                            .show();
                }

                break;
            default:
                break;
        }

    }


    @Override
    protected void onPostResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter("gatewaysample.kii.com.gateway_agent_androidsample.rest_service.COMPLETED"));
        super.onPostResume();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
      //  Intent serviceIntent = new Intent(ContactActivity.this, GatewayService.class);
//        stopService(serviceIntent);

//        unbindService(mConnection);
//        stopService(serviceIntent);

    }
}
