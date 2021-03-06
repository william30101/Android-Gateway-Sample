package gatewaysample.kii.com.gateway_agent_androidsample;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.kii.cloud.storage.KiiThing;
import com.kii.cloud.storage.KiiUser;
import com.kii.thingif.Owner;
import com.kii.thingif.PushBackend;
import com.kii.thingif.Target;
import com.kii.thingif.ThingIFAPI;
import com.kii.thingif.TypedID;
import com.kii.thingif.exception.ThingIFException;

import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;
import org.json.JSONObject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import gatewaysample.kii.com.gateway_agent_androidsample.promise_api_wrapper.IoTCloudPromiseAPIWrapper;
import gatewaysample.kii.com.gateway_agent_androidsample.promise_api_wrapper.KiiCloudPromiseAPIWrapper;
import gatewaysample.kii.com.gateway_agent_androidsample.smart_light_demo.ApiBuilder;
import gatewaysample.kii.com.gateway_agent_androidsample.utils.Config;


public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActivity";


    private ListView listView;
//    private String[] list = {"OnBoardGateway", "Show GateWay Info", "OnBoardThing", "Show Thing Info",
//            "List Things", "Delete Gateway", "Delete EndNode", "Replace Gateway", "Replace EndNode", "Start Gateway Service",
//            "OnBoarding EndNode", "List endNode", "Replace EndNode", "Read mapping file",  "Del EndNode", "updateConnectionsStatus",
//            "update EndNode States", "Get EndNode States", "Send Cmd To EndNode", "Update Cmd Result"};

    private String[] list = {"GatewayService", "Controller"};
    private ArrayAdapter<String> listAdapter;

    private KiiUser user;
    private TextView accountView;
    private GoogleCloudMessaging gcm;


    public ThingIFAPI gatewayApi, thingApi;
    private Toolbar toolbar;

    GatewayService gatewayService;


    private BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        accountView = (TextView) findViewById(R.id.accountView);



        KiiUser user = KiiUser.getCurrentUser();
        String userID = user.getID();
        String accessToken = user.getAccessToken();
        TypedID typedUserID = new TypedID(TypedID.Types.USER, userID);
        Owner owner = new Owner(typedUserID, accessToken);

        initToolBar();
//
//        Intent intent = new Intent(MainActivity.this, RegistrationIntentService.class);
//        this.startService(intent);

        if (savedInstanceState != null) {
            this.gatewayApi = savedInstanceState.getParcelable("ThingIFGatewayAPI");
        } else {
            this.gatewayApi = ApiBuilder.buildApi(getApplicationContext(), owner);
        }

        thingApi = ApiBuilder.buildApi(getApplicationContext(), owner);


        //gcm = GoogleCloudMessaging.getInstance(this.getApplicationContext());

        // if the id is saved in the preference, it skip the registration and just install push.
//        String regId = GCMPreference.getRegistrationId(this.getApplicationContext());
//        if (regId.isEmpty()) {
//            new GCMRegisterTask(this.gatewayApi).execute();
//        }

        getUser();

//        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                String errorMessage = intent.getStringExtra("ErrorMessage");
//                final String token = intent.getStringExtra("token");
//                Log.e("GCMTest", "Registration completed:" + errorMessage + " token: " + token);
//                if (errorMessage != null) {
//                    Toast.makeText(MainActivity.this, "Error push registration:" + errorMessage, Toast.LENGTH_LONG).show();
//                } else {
//                    Toast.makeText(MainActivity.this, "Succeeded push registration", Toast.LENGTH_LONG).show();
//                        new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//
//                                try {
//                                    thingApi.installPush(token, PushBackend.GCM);
//                                } catch (ThingIFException e) {
//                                    e.printStackTrace();
//                                }
//
////                                    boolean development = true;
////                                    try {
////                                        KiiUser.pushInstallation(development).install(token);
////                                    } catch (IOException e) {
////                                        e.printStackTrace();
////                                    } catch (BadRequestException e) {
////                                        e.printStackTrace();
////                                    } catch (UnauthorizedException e) {
////                                        e.printStackTrace();
////                                    } catch (ForbiddenException e) {
////                                        e.printStackTrace();
////                                    } catch (ConflictException e) {
////                                        e.printStackTrace();
////                                    } catch (NotFoundException e) {
////                                        e.printStackTrace();
////                                    } catch (UndefinedException e) {
////                                        e.printStackTrace();
////                                    }
//
//
//                            }
//                        });
//
//                }
//            }
//        };


        listView = (ListView) findViewById(R.id.action_list_view);
        listAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String name = (String) parent.getItemAtPosition(position);
                //Toast.makeText(getApplicationContext(), "你選擇的是" + list[position], Toast.LENGTH_SHORT).show();
                switch (name) {
                    case "GatewayService":
                        Intent intent = new Intent(MainActivity.this, GatewayNoUI.class);
                        startActivity(intent);
                        break;
                    case "Controller":
                        Intent intent2 = new Intent(MainActivity.this, GatewayMainActivity.class);

                        startActivity(intent2);
                        break;
//                    case 0:
//                        if (gatewayApi.onboarded()) {
//                            Log.i(TAG, "already onBoard ");
//                        } else {
//                            ThingConstant gateway = new ThingConstant("gateway-android", "123456", "gateway");
//                            onBoardGatewayVendorId(gateway, gatewayApi);
//                        }
//                        break;
//
//                    //Show Gateway Info
//                    case 1:
//                        if (gatewayApi.onboarded()) {
//                            showGatewayInfo(gatewayApi);
//                        }
//                        break;
//
//                    //onBoarding
//                    case 2:
//                        if (thingApi.onboarded()) {
//                            Toast.makeText(MainActivity.this, " Thing already onBoard", Toast.LENGTH_SHORT);
//                        } else {
//
//                            KiiCloudPromiseAPIWrapper wp = new KiiCloudPromiseAPIWrapper(gatewayApi);
//                            wp.loadWithThingID(gatewayApi.getTarget().getTypedID().getID()).then(new DoneCallback<KiiThing>() {
//                                @Override
//                                public void onDone(KiiThing thing) {
//                                    if (thing.getVendorThingID() != null) {
//                                        ThingConstant endNode = new ThingConstant("HC-05", "123456", thing.getVendorThingID(), gatewayApi.getOwner().getTypedID().toString(), "endNodeType", null);
//                                        onBoardEndNodeVendorId(endNode, thingApi);
//
//                                    } else {
//                                        Toast.makeText(MainActivity.this, "Unable to get target VendorThingID!: ", Toast.LENGTH_LONG).show();
//
//                                    }
//                                }
//                            }, new FailCallback<Throwable>() {
//                                @Override
//                                public void onFail(Throwable result) {
//                                    Toast.makeText(MainActivity.this, "Unable to get target thing!: " + result.getMessage(), Toast.LENGTH_LONG).show();
//                                }
//                            });
//                        }
//                        break;
//
//                    //Show EndNode Info
//                    case 3:
//                        if (thingApi.onboarded()) {
//                            showEndNodeInfo(thingApi);
//                        }
//                        break;
//
//                    //List EndNode
//                    case 4:
//                        if (gatewayApi.onboarded()) {
//                            DialogListEndNodeFragment dialogListEndNode = new DialogListEndNodeFragment(gatewayApi);
//                            dialogListEndNode.show(getFragmentManager(), "DialogGateway");
//                        }
//                        break;
//                    //Delete Gateway
//                    case 5:
//                        if (gatewayApi.onboarded()) {
//                            deleteGateway(gatewayApi);
//                        }
//                        break;
//
//                    //Delete Thing
//                    case 6:
//                        if (thingApi.onboarded()) {
//                            deleteEndNode(gatewayApi, thingApi);
//                        }
//                        break;
//
//                    //Replace Gateway
//                    case 7:
//                        ThingConstant gateway = new ThingConstant("8000", "1234", "gatewaynew");
//                        replaceGateway(gateway, gatewayApi);
//                        break;
//
//                    //Replace EndNode
//                    case 8:
//                        ThingConstant endNodeCon = new ThingConstant("8100", "1234", "EndNodeNew");
//                        replaceEndNode(endNodeCon, gatewayApi, thingApi );
//                        break;
//
//                    //Start Gateway Service
//                    case 9:
////                        Intent intent = new Intent(MainActivity.this, GatewayService.class);
////                        startService(intent);
//                        Intent serviceIntent = new Intent(MainActivity.this, GatewayService.class);
//                        startService(serviceIntent); //Starting the service
//                        bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE); //Binding to the service!
//
//                        break;
//
//                    //Send Cmd to gateway
//                    // Mad socket client , send to server
//                    case 10:
//                        //sendToGatewayThread.start();
//
//                        Runnable run_boarding = new SendToGatewayThread(Config.ENDNODE_ONBOARDING);
//                        new Thread(run_boarding).start();
//
//                        break;
//                    case 11:
//
//                        Runnable list_endnode = new SendToGatewayThread(Config.LIST_ENDNODE);
//                        new Thread(list_endnode).start();
//                        break;
//                    case 12:
//
//                        Runnable replace_endnode = new SendToGatewayThread(Config.REPLACE_ENDNODE);
//                        new Thread(replace_endnode).start();
//                        break;
//
//                    //read mapping file
//                    case 13:
////
////                        Runnable readMappingFile = new SendToGatewayThread(Config.READ_MAPPING_FILE);
////                        new Thread(readMappingFile).start();
//
//                        sendMessage();
//                        break;
//
//                    // Delete EndNode
//                    case 14:
//                        Runnable delEndNodeRun = new SendToGatewayThread(Config.DEL_ENDNODE);
//                        new Thread(delEndNodeRun).start();
//                        break;
//
//                    // Update EndNode Connection Status
//                    case 15:
//                        Runnable conStatus = new SendToGatewayThread(Config.UPDATE_ENDNODE_CONNECTION_STATUS);
//                        new Thread(conStatus).start();
//                        break;
//
//                    // Update EndNode States
//                    case 16:
//                        Runnable statesRun = new SendToGatewayThread(Config.UPDATE_ENDNODE_STATES);
//                        new Thread(statesRun).start();
//                        break;
//
//                    // Update EndNode States
//                    case 17:
//                        Runnable getStatesRun = new SendToGatewayThread(Config.GET_ENDNODE_STATES);
//                        new Thread(getStatesRun).start();
//                        break;
//
//                    // Send Cmd To EndNode
//                    case 18:
//                        Runnable sendCmdRun = new SendToGatewayThread(Config.SEND_CMD_TO_ENDNODE);
//                        new Thread(sendCmdRun).start();
//                        break;
//
//                    // Update Cmd Result
//                    case 19:
//                        Runnable cmdResultRun = new SendToGatewayThread(Config.UPDATE_CMD_RESULT);
//                        new Thread(cmdResultRun).start();
//                        break;
                    default:
                        break;

                }
            }
        });


//        if (Config.DIRECT_TO_GATEWAY){
//            Intent intent = new Intent(this, ContactActivity.class);
//            startActivity(intent);
//        }
    }

    private void sendMessage() {
        com.kii.thingif.internal.utils.Log.d(TAG, "Broadcasting message");
        Intent intent = new Intent("toService");
        // You can also include some extra data.
        intent.putExtra("message", "SendToService msg");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra("message");
            Log.d("receiver", "Got message: " + message);
        }
    };

    private void initToolBar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("GatewaySample");
//        toolbar.inflateMenu(R.menu.toolbar_menu);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //Log.i(TAG,"menu ID : "+ id);
        if (id == R.id.gateway_service){
            Intent intent = new Intent(this, GatewayNoUI.class);
//            EditText editText = (EditText) findViewById(R.id.edit_message);
//            String message = editText.getText().toString();
//            intent.putExtra(EXTRA_MESSAGE, message);
            startActivity(intent);
        }else if (id == R.id.controller){
            // Test REST Service
            Intent intent = new Intent(this, GatewayMainActivity.class);
//            EditText editText = (EditText) findViewById(R.id.edit_message);
//            String message = editText.getText().toString();
//            intent.putExtra(EXTRA_MESSAGE, message);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }


//    public class SendToGatewayThread implements Runnable {
//
//        private String mWhichEvent;
//
//        public SendToGatewayThread(String parameter) {
//            // store parameter for later user
//            mWhichEvent = parameter;
//        }
//
//        public void run() {
//
//
//            List<String> socketObject = new ArrayList<>();
//            InetAddress host = null;
//            try {
//                host = InetAddress.getLocalHost(); // 127.0.0.1
//            } catch (UnknownHostException e) {
//                e.printStackTrace();
//            }
//            Socket socket = null;
//            ObjectOutputStream oos = null;
//            ObjectInputStream ois = null;
//
//            //establish socket connection to server
//            try {
//                socket = new Socket(host.getHostName(), 9876);
//                //write to socket using ObjectOutputStream
//                oos = new ObjectOutputStream(socket.getOutputStream());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            Log.i(TAG, "Sending request " + mWhichEvent +" to Socket Server");
//
//            try {
//                oos.writeObject(mWhichEvent);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
////            switch(mWhichEvent){
////
////                case Config.ENDNODE_ONBOARDING :
////
////                    try {
////                        oos.writeObject(mWhichEvent);
////                    } catch (IOException e) {
////                        e.printStackTrace();
////                    }
////
////                    break;
////                case Config.LIST_ENDNODE:
////                    try {
////                        oos.writeObject(mWhichEvent);
////                    } catch (IOException e) {
////                        e.printStackTrace();
////                    }
////                    break;
////
////                case Config.REPLACE_ENDNODE:
////                    try {
////                        oos.writeObject(mWhichEvent);
////                    } catch (IOException e) {
////                        e.printStackTrace();
////                    }
////                    break;
////
////                case Config.READ_MAPPING_FILE:
////                    try {
////                        oos.writeObject(mWhichEvent);
////                    } catch (IOException e) {
////                        e.printStackTrace();
////                    }
////                    break;
////
////                case Config.DEL_ENDNODE:
////                    try {
////                        oos.writeObject(mWhichEvent);
////                    } catch (IOException e) {
////                        e.printStackTrace();
////                    }
////                    break;
////                default:
////                    Log.i(TAG, "exit");
////                    try {
////                        oos.writeObject("exit");
////                    } catch (IOException e) {
////                        e.printStackTrace();
////                    }
////                    break;
////            }
//
//            //read the server response message
//            try {
//                ois = new ObjectInputStream(socket.getInputStream());
//                String message = (String) ois.readObject();
//                Log.i(TAG, "Message: " + message);
//            } catch (SocketTimeoutException e) {
//                Log.i(TAG,"timeout");
//            } catch (ClassNotFoundException e) {
//                e.printStackTrace();
//            } catch (IOException e){
//                e.printStackTrace();
//            }
//
//            //close resources
//            try {
//                ois.close();
//                oos.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
////            if(mWhichEvent == "thihg_onboarding") {
////                //establish socket connection to server
////                try {
////                    socket = new Socket(host.getHostName(), 9876);
////                    //write to socket using ObjectOutputStream
////                    oos = new ObjectOutputStream(socket.getOutputStream());
////                } catch (IOException e) {
////                    e.printStackTrace();
////                }
////
////                Log.i(TAG, "Sending request to Socket Server");
////                try {
////                    if (i == 1) {
////                        oos.writeObject("exit");
////                    }else{
////                        oos.writeObject("" + i);
////                    }
////                } catch (IOException e) {
////                    e.printStackTrace();
////                }
////
////                //read the server response message
////                try {
////                    ois = new ObjectInputStream(socket.getInputStream());
////                    String message = (String) ois.readObject();
////                    Log.i(TAG, "Message: " + message);
////                } catch (IOException e) {
////                    e.printStackTrace();
////                } catch (ClassNotFoundException e) {
////                    e.printStackTrace();
////                }
////
////                //close resources
////                try {
////                    ois.close();
////                    oos.close();
////                } catch (IOException e) {
////                    e.printStackTrace();
////                }
////            }
//        }
//    }

//    Thread sendToGatewayThread = new Thread(new Runnable() {
//
//
//
//        @Override
//        public void run() {
//
//
//
//            InetAddress host = null;
//            try {
//                host = InetAddress.getLocalHost(); // 127.0.0.1
//            } catch (UnknownHostException e) {
//                e.printStackTrace();
//            }
//            Socket socket = null;
//            ObjectOutputStream oos = null;
//            ObjectInputStream ois = null;
//            for(int i=0; i<5;i++) {
//                //establish socket connection to server
//                try {
//                    socket = new Socket(host.getHostName(), 9876);
//                    //write to socket using ObjectOutputStream
//                    oos = new ObjectOutputStream(socket.getOutputStream());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//                Log.i(TAG, "Sending request to Socket Server");
//                try {
//                    if (i == 1) {
//                        oos.writeObject("exit");
//                    }else{
//                        oos.writeObject("" + i);
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//                //read the server response message
//                try {
//                    ois = new ObjectInputStream(socket.getInputStream());
//                    String message = (String) ois.readObject();
//                    Log.i(TAG, "Message: " + message);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } catch (ClassNotFoundException e) {
//                    e.printStackTrace();
//                }
//
//                //close resources
//                try {
//                    ois.close();
//                    oos.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    });

    private void getUser() {
        // Get the currently logged in user.
        KiiUser user = KiiUser.getCurrentUser();
        accountView.setText(user.getUsername());

    }

//    private void deleteEndNode(final ThingIFAPI gatewayApi, final ThingIFAPI thingApi) {
//
//        KiiCloudPromiseAPIWrapper wp = new KiiCloudPromiseAPIWrapper(gatewayApi);
//        wp.loadWithThingID(gatewayApi.getTarget().getTypedID().getID()).then(new DoneCallback<KiiThing>() {
//            @Override
//            public void onDone(KiiThing thing) {
//
//                if (thing.getID() != null) {
//                    final String gatewayThingID = thing.getID();
//
//
//                    KiiCloudPromiseAPIWrapper wp = new KiiCloudPromiseAPIWrapper(thingApi);
//                    wp.loadWithThingID(thingApi.getTarget().getTypedID().getID()).then(new DoneCallback<KiiThing>() {
//                        @Override
//                        public void onDone(KiiThing thing) {
//
//                            if (thing.getID() != null) {
//                                String endNodeThingID = thing.getID();
//
//                                IoTCloudPromiseAPIWrapper wp = new IoTCloudPromiseAPIWrapper(thingApi);
//
//                                wp.deleteEndNode(gatewayThingID, endNodeThingID).then(new DoneCallback<String>() {
//                                    @Override
//                                    public void onDone(String result) {
//                                        Toast.makeText(MainActivity.this, "Delete EndNode " + result + "Success!", Toast.LENGTH_LONG).show();
//                                    }
//                                }, new FailCallback<Throwable>() {
//                                    @Override
//                                    public void onFail(Throwable result) {
//                                        Toast.makeText(MainActivity.this, "Delete EndNode failed!: " + result.getMessage(), Toast.LENGTH_LONG).show();
//                                    }
//                                });
//
//                            }
//                        }
//                    }, new FailCallback<Throwable>() {
//                        @Override
//                        public void onFail(Throwable result) {
//                            Toast.makeText(getApplicationContext(), "Unable to get gateway thing!: " + result.getMessage(), Toast.LENGTH_LONG).show();
//                        }
//                    });
//
//
//                }
//            }
//        }, new FailCallback<Throwable>() {
//            @Override
//            public void onFail(Throwable result) {
//                Toast.makeText(getApplicationContext(), "Unable to get gateway thing!: " + result.getMessage(), Toast.LENGTH_LONG).show();
//            }
//        });
//
//
//    }

//    private void deleteGateway(final ThingIFAPI api) {
//
//        KiiCloudPromiseAPIWrapper wp = new KiiCloudPromiseAPIWrapper(api);
//        wp.loadWithThingID(api.getTarget().getTypedID().getID()).then(new DoneCallback<KiiThing>() {
//            @Override
//            public void onDone(KiiThing thing) {
//
//                if (thing.getID() != null) {
//                    IoTCloudPromiseAPIWrapper wp = new IoTCloudPromiseAPIWrapper(api);
//
//                    wp.deleteGateway(thing.getID()).then(new DoneCallback<String>() {
//                        @Override
//                        public void onDone(String result) {
//                            Toast.makeText(MainActivity.this, "Delete Gateway " + result + "Success!", Toast.LENGTH_LONG).show();
//                        }
//                    }, new FailCallback<Throwable>() {
//                        @Override
//                        public void onFail(Throwable result) {
//                            Toast.makeText(MainActivity.this, "Delete Gateway failed!: " + result.getMessage(), Toast.LENGTH_LONG).show();
//                        }
//                    });
//
//                }
//            }
//        }, new FailCallback<Throwable>() {
//            @Override
//            public void onFail(Throwable result) {
//                Toast.makeText(getApplicationContext(), "Unable to get gateway thing!: " + result.getMessage(), Toast.LENGTH_LONG).show();
//            }
//        });
//    }

//    private void onBoardEndNodeVendorId(ThingConstant thingCon, final ThingIFAPI api) {
//
//        final String endNodeVendorThingID = thingCon.getEndNodeVendorThingID();
//        final String endNodeThingPassword = thingCon.getEndNodeThingPassword();
//        final String gatewayVendorThingID = thingCon.getGatewayVendorThingID();
//        final String owner = thingCon.getOwner();
//        final String endNodeThingType = thingCon.getEndNodeThingType();
//        final JSONObject endNodeThingProperties = thingCon.getEndNodeThingProperties();
//
//        IoTCloudPromiseAPIWrapper wp = new IoTCloudPromiseAPIWrapper(api);
//
//        wp.onboardEndNode(endNodeVendorThingID, endNodeThingPassword, gatewayVendorThingID,
//                endNodeThingType, owner, endNodeThingProperties).then(new DoneCallback<Target>() {
//            @Override
//            public void onDone(Target result) {
//                Toast.makeText(MainActivity.this, "On board succeeded!", Toast.LENGTH_LONG).show();
//                Log.i(TAG, "onBoard : " + api.onboarded());
//            }
//        }, new FailCallback<Throwable>() {
//            @Override
//            public void onFail(Throwable result) {
//                Toast.makeText(MainActivity.this, "On board failed!: " + result.getMessage(), Toast.LENGTH_LONG).show();
//            }
//        });
//    }

//    private void onBoardGatewayVendorId(ThingConstant thingCon, final ThingIFAPI api) {
//
//        final String venderThingID = thingCon.getVenderThingID();
//        final String thingPassword = thingCon.getThingPassword();
//        final String thingType = thingCon.getThingType();
//        IoTCloudPromiseAPIWrapper wp = new IoTCloudPromiseAPIWrapper(api);
//
//        wp.onboardGateWay(venderThingID, thingPassword, thingType).then(new DoneCallback<Target>() {
//            @Override
//            public void onDone(Target result) {
//                Toast.makeText(MainActivity.this, "On board succeeded!", Toast.LENGTH_LONG).show();
//                Log.i(TAG, "onBoard : " + api.onboarded());
//            }
//        }, new FailCallback<Throwable>() {
//            @Override
//            public void onFail(Throwable result) {
//                Toast.makeText(MainActivity.this, "On board failed!: " + result.getMessage(), Toast.LENGTH_LONG).show();
//            }
//        });
//    }

//    private void showGatewayInfo(ThingIFAPI gatewayApi) {
//
//        DialogGatewayFragment dialogGateway = new DialogGatewayFragment(gatewayApi);
//        dialogGateway.show(getFragmentManager(), "DialogGateway");
//    }

//    private void replaceGateway(ThingConstant thingCon, final ThingIFAPI api) {
//        //TODO
//        // REST API Seems problem.
//
////        final String venderThingID = thingCon.getVenderThingID();
////        final String thingPassword = thingCon.getThingPassword();
////
////        IoTCloudPromiseAPIWrapper wp = new IoTCloudPromiseAPIWrapper(api);
////
////        wp.onboardGateWay(venderThingID, thingPassword, thingType).then(new DoneCallback<Target>() {
////            @Override
////            public void onDone(Target result) {
////                Toast.makeText(MainActivity.this, "On board succeeded!", Toast.LENGTH_LONG).show();
////                Log.i(TAG, "onBoard : " + api.onboarded());
////            }
////        }, new FailCallback<Throwable>() {
////            @Override
////            public void onFail(Throwable result) {
////                Toast.makeText(MainActivity.this, "On board failed!: " + result.getMessage(), Toast.LENGTH_LONG).show();
////            }
////        });
//    }

//    private void replaceEndNode(ThingConstant thingCon, final ThingIFAPI gatewayApi, final ThingIFAPI endNodeApi) {
//
//        final String venderThingID = thingCon.getVenderThingID();
//        final String thingPassword = thingCon.getThingPassword();
//
//
//
//        KiiCloudPromiseAPIWrapper wp = new KiiCloudPromiseAPIWrapper(gatewayApi);
//        wp.loadWithThingID(gatewayApi.getTarget().getTypedID().getID()).then(new DoneCallback<KiiThing>() {
//            @Override
//            public void onDone(KiiThing thing) {
//
//                if (thing.getID() != null) {
//                    final String gatewayThingID = thing.getID();
//
//
//                    KiiCloudPromiseAPIWrapper wp = new KiiCloudPromiseAPIWrapper(endNodeApi);
//                    wp.loadWithThingID(endNodeApi.getTarget().getTypedID().getID()).then(new DoneCallback<KiiThing>() {
//                        @Override
//                        public void onDone(KiiThing thing) {
//
//                            if (thing.getID() != null) {
//                                String endNodeThingID = thing.getID();
//
//                                IoTCloudPromiseAPIWrapper wp = new IoTCloudPromiseAPIWrapper(endNodeApi);
//
//                                wp.replaceEndNode(gatewayThingID, venderThingID, thingPassword, endNodeApi.getTarget()).then(new DoneCallback<Target>() {
//                                    @Override
//                                    public void onDone(Target result) {
//                                        Toast.makeText(MainActivity.this, "Replace EndNode " + result.getTypedID() + "Success!", Toast.LENGTH_LONG).show();
//                                    }
//                                }, new FailCallback<Throwable>() {
//                                    @Override
//                                    public void onFail(Throwable result) {
//                                        Toast.makeText(MainActivity.this, "Replace EndNode failed!: " + result.getMessage(), Toast.LENGTH_LONG).show();
//                                    }
//                                });
//
//                            }
//                        }
//                    }, new FailCallback<Throwable>() {
//                        @Override
//                        public void onFail(Throwable result) {
//                            Toast.makeText(getApplicationContext(), "Unable to get gateway thing!: " + result.getMessage(), Toast.LENGTH_LONG).show();
//                        }
//                    });
//
//
//                }
//            }
//        }, new FailCallback<Throwable>() {
//            @Override
//            public void onFail(Throwable result) {
//                Toast.makeText(getApplicationContext(), "Unable to get gateway thing!: " + result.getMessage(), Toast.LENGTH_LONG).show();
//            }
//        });
//
//
//    }

//    private void showEndNodeInfo(ThingIFAPI endNodeApi) {
//
//        DialogEndNodeFragment dialogGateway = new DialogEndNodeFragment(endNodeApi);
//        dialogGateway.show(getFragmentManager(), "DialogEndNode");
//    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("ThingIFGatewayAPI", this.gatewayApi);
    }

//    public class GCMRegisterTask extends AsyncTask<Void, Void, Exception> {
//
//        private final ThingIFAPI api;
//
//        GCMRegisterTask(ThingIFAPI api) {
//            this.api = api;
//        }
//
//        @Override
//        protected Exception doInBackground(Void... params) {
//            if (TextUtils.isEmpty(Config.SENDERID)) {
//                return null;
//            }
//            String registrationId = null;
//            int retry = 0;
//            Exception lastException = null;
//            while (retry < 3) {
//                try {
//                    GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(MainActivity.this);
//                    registrationId = gcm.register(Config.SENDERID);
//
//                    break;
//                } catch (IOException ignore) {
//                    lastException = ignore;
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        lastException = e;
//                    }
//                    retry++;
//                }
//            }
//            if (TextUtils.isEmpty(registrationId)) {
//                return lastException;
//            }
//            try {
//
//                this.api.installPush(registrationId, PushBackend.GCM);
//            } catch (ThingIFException e) {
//                return e;
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(final Exception e) {
//            if (e != null) {
//                Toast.makeText(MainActivity.this, "Unable to register GCM!: " + e.getMessage(), Toast.LENGTH_LONG).show();
//            }
//        }
//    }




//    private ServiceConnection mConnection = new ServiceConnection() {
//
//        @Override
//        public void onServiceConnected(ComponentName className,
//                                       IBinder service) {
//            Toast.makeText(MainActivity.this, "onServiceConnected called", Toast.LENGTH_SHORT).show();
//            // We've binded to LocalService, cast the IBinder and get LocalService instance
//            GatewayService.LocalBinder binder = (GatewayService.LocalBinder) service;
//            gatewayService = binder.getServiceInstance(); //Get instance of your service!
//            //gatewayService.registerClient(MainActivity.this); //Activity register in the service as client for callabcks!
//
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName arg0) {
//            Toast.makeText(MainActivity.this, "onServiceDisconnected called", Toast.LENGTH_SHORT).show();
//
//        }
//    };

    @Override
    protected void onResume() {
//        LocalBroadcastManager.getInstance(this).registerReceiver(
//                mReceiver, new IntentFilter("action"));


        super.onResume();

    }

    @Override
    protected void onPause() {
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(
//                mReceiver);
      //  LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Intent serviceIntent = new Intent(MainActivity.this, GatewayService.class);
//        stopService(serviceIntent);

        //unbindService(mConnection);
        //stopService(serviceIntent);

    }
}
