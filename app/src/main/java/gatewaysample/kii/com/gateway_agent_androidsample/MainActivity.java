package gatewaysample.kii.com.gateway_agent_androidsample;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

import gatewaysample.kii.com.gateway_agent_androidsample.promise_api_wrapper.IoTCloudPromiseAPIWrapper;
import gatewaysample.kii.com.gateway_agent_androidsample.promise_api_wrapper.KiiCloudPromiseAPIWrapper;
import gatewaysample.kii.com.gateway_agent_androidsample.smart_light_demo.ApiBuilder;
import gatewaysample.kii.com.gateway_agent_androidsample.utils.Config;
import gatewaysample.kii.com.gateway_agent_androidsample.utils.GCMPreference;

/**
 * Created by mac on 2/3/16.
 */
public class MainActivity extends Activity implements View.OnClickListener{

    private String TAG = "MainActivity";
    private KiiUser user;
    private Button getBtn, onboardGatewayBtn, onboardThingBtn, showGatewayBtn, showThingBtn;
    private TextView accountView;
    private GoogleCloudMessaging gcm;


    private ThingIFAPI gatewayApi, thingApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        getBtn = (Button) findViewById(R.id.getBtn);
        onboardGatewayBtn = (Button) findViewById(R.id.onboardGatewayBtn);
        onboardThingBtn = (Button) findViewById(R.id.onboardThingBtn);
        showGatewayBtn = (Button) findViewById(R.id.showGatewayBtn);
        showThingBtn = (Button) findViewById(R.id.showThingBtn);
        accountView = (TextView) findViewById(R.id.accountView);

        KiiUser user = KiiUser.getCurrentUser();
        String userID = user.getID();
        String accessToken = user.getAccessToken();
        TypedID typedUserID = new TypedID(TypedID.Types.USER, userID);
        Owner owner = new Owner(typedUserID, accessToken);

        if (savedInstanceState != null) {
            this.gatewayApi = savedInstanceState.getParcelable("ThingIFGatewayAPI");
        }else{
            this.gatewayApi = ApiBuilder.buildApi(getApplicationContext(), owner);
        }

        thingApi = ApiBuilder.buildApi(getApplicationContext(), owner);


        gcm = GoogleCloudMessaging.getInstance(this.getApplicationContext());

        // if the id is saved in the preference, it skip the registration and just install push.
        String regId = GCMPreference.getRegistrationId(this.getApplicationContext());
        if (regId.isEmpty()) {
            new GCMRegisterTask(this.gatewayApi).execute();
        }

        getBtn.setOnClickListener(this);
        onboardGatewayBtn.setOnClickListener(this);
        onboardThingBtn.setOnClickListener(this);
        showGatewayBtn.setOnClickListener(this);
        showThingBtn.setOnClickListener(this);
    }

    private void getUser(){
        // Get the currently logged in user.
        KiiUser user = KiiUser.getCurrentUser();
        accountView.setText(user.getUsername());

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.getBtn:
                getUser();
                break;
            case R.id.onboardGatewayBtn:
                if (gatewayApi.onboarded()){
                    Log.i(TAG, "already onBoard ");
                }else{
                    ThingConstant gateway = new ThingConstant("7000", "1234" , "gateway");
                    onBoardGatewayVendorId(gateway, gatewayApi);
                }
                break;
            case R.id.onboardThingBtn:
                if (thingApi.onboarded()){
                    Toast.makeText(this," Thing already onBoard",Toast.LENGTH_SHORT);
                }else{

                    KiiCloudPromiseAPIWrapper wp = new KiiCloudPromiseAPIWrapper(gatewayApi);
                    wp.loadWithThingID(gatewayApi.getTarget().getTypedID().getID()).then(new DoneCallback<KiiThing>() {
                        @Override
                        public void onDone(KiiThing thing) {
                            if (thing.getVendorThingID() != null) {
                                ThingConstant endNode = new ThingConstant("7001", "1234" ,thing.getVendorThingID(), gatewayApi.getOwner().getTypedID().toString(), "IdleLoop", null);
                                onBoardEndNodeVendorId(endNode, thingApi);

                            } else {
                                Toast.makeText(MainActivity.this, "Unable to get target VendorThingID!: " , Toast.LENGTH_LONG).show();

                            }
                        }
                    }, new FailCallback<Throwable>() {
                        @Override
                        public void onFail(Throwable result) {
                            Toast.makeText(MainActivity.this, "Unable to get target thing!: " + result.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
                break;
            case R.id.showGatewayBtn:
                if (gatewayApi.onboarded()){
                    showGatewayInfo(gatewayApi);
                }
                break;
            case R.id.showThingBtn:
                if (thingApi.onboarded()){
                    showEndNodeInfo(thingApi);
                }
                break;
            default:
                break;
        }
    }

    private void onBoardEndNodeVendorId(ThingConstant thingCon, final ThingIFAPI api) {

        final String endNodeVendorThingID = thingCon.getEndNodeVendorThingID();
        final String endNodeThingPassword = thingCon.getEndNodeThingPassword();
        final String gatewayVendorThingID = thingCon.getGatewayVendorThingID();
        final String owner = thingCon.getOwner();
        final String endNodeThingType = thingCon.getEndNodeThingType();
        final JSONObject endNodeThingProperties = thingCon.getEndNodeThingProperties();

        IoTCloudPromiseAPIWrapper wp = new IoTCloudPromiseAPIWrapper(api);

        wp.onboardEndNode(endNodeVendorThingID, endNodeThingPassword, gatewayVendorThingID,
                endNodeThingType,  owner, endNodeThingProperties).then(new DoneCallback<Target>() {
            @Override
            public void onDone(Target result) {
                Toast.makeText(MainActivity.this, "On board succeeded!", Toast.LENGTH_LONG).show();
                Log.i(TAG,"onBoard : " + api.onboarded());
            }
        }, new FailCallback<Throwable>() {
            @Override
            public void onFail(Throwable result) {
                Toast.makeText(MainActivity.this, "On board failed!: " + result.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void onBoardGatewayVendorId(ThingConstant thingCon, final ThingIFAPI api){

        final String venderThingID = thingCon.getVenderThingID();
        final String thingPassword = thingCon.getThingPassword();
        final String thingType = thingCon.getThingType();
        IoTCloudPromiseAPIWrapper wp = new IoTCloudPromiseAPIWrapper(api);

        wp.onboardGateWay(venderThingID, thingPassword, thingType).then(new DoneCallback<Target>() {
            @Override
            public void onDone(Target result) {
                Toast.makeText(MainActivity.this, "On board succeeded!", Toast.LENGTH_LONG).show();
                Log.i(TAG,"onBoard : " + api.onboarded());
            }
        }, new FailCallback<Throwable>() {
            @Override
            public void onFail(Throwable result) {
                Toast.makeText(MainActivity.this, "On board failed!: " + result.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showGatewayInfo(ThingIFAPI gatewayApi){

        DialogGatewayFragment dialogGateway = new DialogGatewayFragment(gatewayApi);
        dialogGateway.show(getFragmentManager(), "DialogGateway");
    }

    private void showEndNodeInfo(ThingIFAPI endNodeApi){

        DialogEndNodeFragment dialogGateway = new DialogEndNodeFragment(endNodeApi);
        dialogGateway.show(getFragmentManager(), "DialogEndNode");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("ThingIFGatewayAPI", this.gatewayApi);
    }

    public class GCMRegisterTask extends AsyncTask<Void, Void, Exception> {

        private final ThingIFAPI api;

        GCMRegisterTask(ThingIFAPI api) {
            this.api = api;
        }
        @Override
        protected Exception doInBackground(Void... params) {
            if (TextUtils.isEmpty(Config.SENDERID)) {
                return null;
            }
            String registrationId = null;
            int retry = 0;
            Exception lastException = null;
            while (retry < 3) {
                try {
                    GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(MainActivity.this);
                    registrationId = gcm.register(Config.SENDERID);
                    break;
                } catch (IOException ignore) {
                    lastException = ignore;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        lastException = e;
                    }
                    retry++;
                }
            }
            if (TextUtils.isEmpty(registrationId)) {
                return lastException;
            }
            try {
                this.api.installPush(registrationId, PushBackend.GCM);
            } catch (ThingIFException e) {
                return e;
            }
            return null;
        }
        @Override
        protected void onPostExecute(final Exception e) {
            if (e != null) {
                Toast.makeText(MainActivity.this, "Unable to register GCM!: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

}
