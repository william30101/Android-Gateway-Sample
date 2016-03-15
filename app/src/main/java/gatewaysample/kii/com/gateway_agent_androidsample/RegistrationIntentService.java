package gatewaysample.kii.com.gateway_agent_androidsample;


import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import gatewaysample.kii.com.gateway_agent_androidsample.utils.Config;

public class RegistrationIntentService extends IntentService {
    private static final String TAG = "RegIntentService";
    String token;

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String error = null;
        try {
            synchronized (TAG) {
                InstanceID instanceID = InstanceID.getInstance(this);
                //String senderId = getString(R.string.gcm_defaultSenderId);
                token = instanceID.getToken(Config.SENDERID, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

            }
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            error = e.getLocalizedMessage();
        }
        Intent registrationComplete = new Intent("gatewaysample.kii.com.gateway_agent_androidsample.rest_service.COMPLETED");
        registrationComplete.putExtra("ErrorMessage", error);
        registrationComplete.putExtra("token", token);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }
}