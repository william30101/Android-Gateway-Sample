package gatewaysample.kii.com.gateway_agent_androidsample;

import android.app.Application;

import com.kii.cloud.storage.Kii;

import gatewaysample.kii.com.gateway_agent_androidsample.utils.Config;

/**
 * Created by mac on 2/3/16.
 */
public class GatewayKii extends Application{

    @Override
    public void onCreate() {
        super.onCreate();

        // initialize the Kii SDK!
        Kii.initialize(this,Config.APP_ID, Config.APP_KEY, Config.APP_SITE);
    }
}
