package gatewaysample.kii.com.gateway_agent_androidsample;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.widget.Toast;

import com.kii.cloud.storage.KiiThing;
import com.kii.cloud.storage.KiiUser;
import com.kii.thingif.Owner;
import com.kii.thingif.Target;
import com.kii.thingif.ThingIFAPI;
import com.kii.thingif.TypedID;
import com.kii.thingif.gateway.*;
import com.kii.thingif.gateway.EndNode;
import com.kii.thingif.internal.utils.Log;

import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;

import gatewaysample.kii.com.gateway_agent_androidsample.promise_api_wrapper.IoTCloudPromiseAPIWrapper;
import gatewaysample.kii.com.gateway_agent_androidsample.promise_api_wrapper.KiiCloudPromiseAPIWrapper;
import gatewaysample.kii.com.gateway_agent_androidsample.smart_light_demo.ApiBuilder;
import gatewaysample.kii.com.gateway_agent_androidsample.utils.Config;


/**
 * Created by william.wu on 2/15/16.
 */
public class GatewayService extends Service {

    private final String TAG = "GatewayService";
    private Handler handler = new Handler();
    private Handler toasthandler;
    private ThingIFAPI gatewayApi, thingApi;
    private String filename = "myfile";
    public static final String ENCODING = "UTF-8";

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


        KiiUser user = KiiUser.getCurrentUser();
        String userID = user.getID();
        String accessToken = user.getAccessToken();
        TypedID typedUserID = new TypedID(TypedID.Types.USER, userID);
        Owner owner = new Owner(typedUserID, accessToken);
        this.gatewayApi = ApiBuilder.buildApi(getApplicationContext(), owner);
        handler.postDelayed(onBoarding, 1000);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(onBoarding);
        super.onDestroy();
    }

    private Runnable onBoarding = new Runnable() {
        public void run() {

            Log.i(TAG, "onBoarding");
            if (gatewayApi.onboarded()) {
                android.util.Log.i(TAG, "already onBoard ");
            } else {
                ThingConstant gateway = new ThingConstant("7000", "1234", "gateway");
                onBoardGatewayVendorId(gateway, gatewayApi);
            }

        }
    };


    private void onBoardGatewayVendorId(ThingConstant thingCon, final ThingIFAPI api) {

        final String venderThingID = thingCon.getVenderThingID();
        final String thingPassword = thingCon.getThingPassword();
        final String thingType = thingCon.getThingType();
        IoTCloudPromiseAPIWrapper wp = new IoTCloudPromiseAPIWrapper(api);

        wp.onboardGateWay(venderThingID, thingPassword, thingType).then(new DoneCallback<Target>() {
            @Override
            public void onDone(Target result) {
                toasthandler.post(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "On board succeeded!", Toast.LENGTH_LONG).show();
                    }
                });

                android.util.Log.i(TAG, "onBoard : " + api.onboarded());

                mapping_init();
            }
        }, new FailCallback<Throwable>() {
            @Override
            public void onFail(final Throwable result) {
                toasthandler.post(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "On board failed!: " + result.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    private void mapping_init(){
        //listThingsUnderGateway(gatewayApi);
        mappingAllRestore();
    }

    private void mappingAllRestore(){



        String string = "Hello world!";
        FileOutputStream outputStream;


        if (fileExists(this,Config.MAPPING_FILE_NAME)){
            // TODO
            // restore endNode list


        }
    }

    public boolean fileExists(Context context, String filename) {
        File file = context.getFileStreamPath(filename);
        if(file == null || !file.exists()) {
            return false;
        }
        return true;
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

    private void listThingsUnderGateway(final ThingIFAPI api){
        KiiCloudPromiseAPIWrapper wp = new KiiCloudPromiseAPIWrapper(api);
        wp.loadWithThingID(api.getTarget().getTypedID().getID()).then(new DoneCallback<KiiThing>() {
            @Override
            public void onDone(KiiThing thing) {

                if (thing.getID() != null) {
                    String gatewayThingID = thing.getID();
                    IoTCloudPromiseAPIWrapper wp = new IoTCloudPromiseAPIWrapper(api);

                    wp.listEndNodes(gatewayThingID, 10, null).then(new DoneCallback<List<com.kii.thingif.gateway.EndNode>>() {
                        @Override
                        public void onDone(final List<EndNode> endNodes) {
                            toasthandler.post(new Runnable() {
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "List Things succeeded!", Toast.LENGTH_LONG).show();
                                    for (int i=0; i<endNodes.size(); i++){
                                        Log.i(TAG,"endNode ThingID: "+  endNodes.get(i).getThingID() + "\n " +
                                        " endNode VendorThingID : " + endNodes.get(i).getVendorThingID());
                                    }
                                }
                            });
                        }
                    }, new FailCallback<Throwable>() {
                        @Override
                        public void onFail(final Throwable result) {
                            toasthandler.post(new Runnable() {
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "get list Fail!: " + result.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    });
                }


            }
        }, new FailCallback<Throwable>() {
            @Override
            public void onFail(Throwable result) {
                Toast.makeText(getApplicationContext(), "Unable to get target thing!: " + result.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


}
