package gatewaysample.kii.com.gateway_agent_androidsample.converter;


import org.json.JSONObject;

public class BluetoothRetStates {

    String vendorThingID;
    String thingID;
    JSONObject body;

    public BluetoothRetStates(String vendorThingID, String thingID, JSONObject body) {
        this.vendorThingID = vendorThingID;
        this.thingID = thingID;
        this.body = body;
    }

    public String getVendorThingID() {
        return vendorThingID;
    }

    public String getThingID() {
        return thingID;
    }

    public JSONObject getBody() {
        return body;
    }
}
