package gatewaysample.kii.com.gateway_agent_androidsample.rest_service;


import android.util.Log;

import com.kii.thingif.exception.ThingIFException;
import com.kii.thingif.gateway.EndNode;

import java.util.List;

import gatewaysample.kii.com.gateway_agent_androidsample.mqtt.MqttClient;

public class OnBoardThread extends ThreadCall implements Runnable {
    @Override
    public void run() {
        // finalGatewayA.login("VENDOR_THING_ID:gatewaytest2", "123456");
        synchronized(syncObject) {
            if (gatewayA != null && owner != null){
                try {
                    //getTID("onBoardThread ");
                    Log.i(TAG, "onBoardThread running");
                    String gatewayThingID = gatewayA.onboardGateway(super.GatewayVendorThingID, GatewayPassword, "led", null, owner.getTypedID().toString());

                    if (gatewayThingID != null){
                        mappingTable.clear();
                        mappingTable.add(new MappingObject("Gateway", gatewayThingID, GatewayVendorThingID,
                                owner.getAccessToken(), owner.getTypedID().toString()));
                        //WriteSharedPreferences("gatewayThingID", gatewayThingID);

                        List<EndNode> endNodes = getEndNodeList();
                        if (endNodes != null){
                            for (int i=0; i< endNodes.size(); i++){
                                mappingTable.add(i, new MappingObject("EndNode", endNodes.get(i).getThingID(),
                                        endNodes.get(i).getVendorThingID(), owner.getAccessToken(), owner.getTypedID().toString()));
                            }
                        }

                        newMappingFile(mappingTable);

                        //MQTT receive connections established.
                        mqttClient = new MqttClient(mContext,gatewayA.getmGateway().getMqttEndpoint());
                        mqttClient.connect();
                    }

                } catch (ThingIFException e) {
                    e.printStackTrace();
                }

                syncObject.notify();
            }
        }

    }
}
