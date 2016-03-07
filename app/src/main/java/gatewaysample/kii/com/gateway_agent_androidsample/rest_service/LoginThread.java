package gatewaysample.kii.com.gateway_agent_androidsample.rest_service;


import android.os.Bundle;

import com.kii.cloud.storage.KiiUser;
import com.kii.cloud.storage.exception.app.AppException;
import com.kii.thingif.KiiApp;
import com.kii.thingif.Owner;
import com.kii.thingif.Site;
import com.kii.thingif.TypedID;
import com.kii.thingif.exception.ThingIFException;
import com.kii.thingif.gateway.GatewayAPI4Gateway;
import com.kii.thingif.gateway.GatewayAPIBuilder;

import java.io.IOException;

import gatewaysample.kii.com.gateway_agent_androidsample.utils.Config;

public class LoginThread extends ThreadCall implements Runnable {

    private String mUserName;
    private String mPassWord;

    public LoginThread(String userName, String passWord) {
        this.mUserName = userName;
        this.mPassWord = passWord;
    }

    public void run() {
        synchronized(super.syncObject) {

            KiiApp app = new KiiApp(Config.APP_ID, Config.APP_KEY, Site.JP);
            //KiiApp app = new KiiApp(Config.APP_ID, Config.APP_KEY, "127.0.0.1");
            GatewayAPIBuilder gatewayBuilder = GatewayAPIBuilder.newBuilder(mContext, app, "127.0.0.1", mUserName, mPassWord);
            try {
                gatewayA = gatewayBuilder.build4Gateway();
                gatewayEnd = gatewayBuilder.build4EndNode();

            } catch (ThingIFException e) {
                e.printStackTrace();
            }

            // sign in user
            final GatewayAPI4Gateway finalGatewayA = gatewayA;


            try {
                KiiUser user = KiiUser.logIn(mUserName, mPassWord);
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
}
