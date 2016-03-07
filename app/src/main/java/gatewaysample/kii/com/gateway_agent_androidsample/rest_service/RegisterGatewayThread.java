package gatewaysample.kii.com.gateway_agent_androidsample.rest_service;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;

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

public class RegisterGatewayThread extends ThreadCall implements Runnable {

    private Context mContext;
    private String mUserName;
    private String mPassWord;
    Runnable r;

    public RegisterGatewayThread(String userName, String passWord) {

        this.mUserName = userName;
        this.mPassWord = passWord;
    }

    public void run() {

        while(!mStop){

            Log.i(TAG,"RegisterGatewayThread running");
            login(mUserName, mPassWord);
            onBoardGateWay();
            mStop = true;

        }

        if(mStop){
            this.interrupt();

            Log.i(TAG,"RegisterGatewayThread stop");
        }

    }
}
