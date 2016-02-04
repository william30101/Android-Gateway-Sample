package gatewaysample.kii.com.gateway_agent_androidsample;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.kii.cloud.storage.Kii;
import com.kii.cloud.storage.KiiUser;
import com.kii.cloud.storage.callback.KiiUserCallBack;
import com.kii.thingif.Owner;
import com.kii.thingif.PushBackend;
import com.kii.thingif.Site;
import com.kii.thingif.ThingIFAPI;
import com.kii.thingif.ThingIFAPIBuilder;
import com.kii.thingif.TypedID;
import com.kii.thingif.exception.StoredThingIFAPIInstanceNotFoundException;
import com.kii.thingif.exception.ThingIFException;

import java.io.IOException;

import gatewaysample.kii.com.gateway_agent_androidsample.smart_light_demo.ApiBuilder;
import gatewaysample.kii.com.gateway_agent_androidsample.utils.Config;
import gatewaysample.kii.com.gateway_agent_androidsample.utils.GCMPreference;

/**
 * Created by mac on 2/3/16.
 */
public class MainActivity extends Activity{

    private String TAG = "MainActivity";
    private KiiUser user;
    private Button getBtn;
    private TextView accountView;
    private GoogleCloudMessaging gcm;

    private ThingIFAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        getBtn = (Button) findViewById(R.id.getBn);
        accountView = (TextView) findViewById(R.id.accountView);
        KiiUser user = KiiUser.getCurrentUser();
        String userID = user.getID();
        String accessToken = user.getAccessToken();
        TypedID typedUserID = new TypedID(TypedID.Types.USER, userID);
        Owner owner = new Owner(typedUserID, accessToken);

        this.api = ApiBuilder.buildApi(getApplicationContext(), owner);

        gcm = GoogleCloudMessaging.getInstance(this.getApplicationContext());

        // if the id is saved in the preference, it skip the registration and just install push.
        String regId = GCMPreference.getRegistrationId(this.getApplicationContext());
        if (regId.isEmpty()) {
            new GCMRegisterTask(this.api).execute();
        }

        getBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUser();
            }
        });
    }

    private void getUser(){
        // Get the currently logged in user.
        KiiUser user = KiiUser.getCurrentUser();
        accountView.setText(user.getUsername());

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
