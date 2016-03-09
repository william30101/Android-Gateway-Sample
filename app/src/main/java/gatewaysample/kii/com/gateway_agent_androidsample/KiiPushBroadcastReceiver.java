package gatewaysample.kii.com.gateway_agent_androidsample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.kii.cloud.storage.DirectPushMessage;
import com.kii.cloud.storage.KiiUser;
import com.kii.cloud.storage.PushMessageBundleHelper;
import com.kii.cloud.storage.PushToAppMessage;
import com.kii.cloud.storage.PushToUserMessage;
import com.kii.cloud.storage.ReceivedMessage;

public class KiiPushBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
        String messageType = gcm.getMessageType(intent);
        if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
            Bundle extras = intent.getExtras();
            ReceivedMessage message = PushMessageBundleHelper.parse(extras);
            KiiUser sender = message.getSender();
            PushMessageBundleHelper.MessageType type = message.pushMessageType();
            switch (type) {
                case PUSH_TO_APP:
                    PushToAppMessage appMsg = (PushToAppMessage)message;

                    break;
                case PUSH_TO_USER:
                    PushToUserMessage userMsg = (PushToUserMessage)message;

                    break;
                case DIRECT_PUSH:
                    DirectPushMessage directMsg = (DirectPushMessage)message;

                    break;
            }
        }
    }

}
