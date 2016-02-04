package gatewaysample.kii.com.gateway_agent_androidsample;

import org.json.JSONObject;

/**
 * Created by mac on 2/4/16.
 */
public class ThingConstant {
    private String venderThingID;
    private String thingPassword;
    private String thingType;
    private String endNodeVendorThingID;
    private String endNodeThingPassword;
    private String gatewayVendorThingID;
    private String owner;
    private String endNodeThingType;
    private JSONObject endNodeThingProperties;

    public ThingConstant(String venderThingID, String thingPassword, String thingType) {
        this.venderThingID = venderThingID;
        this.thingPassword = thingPassword;
        this.thingType = thingType;
    }

    public ThingConstant(String endNodeVendorThingID, String endNodeThingPassword, String gatewayVendorThingID, String owner, String endNodeThingType, JSONObject endNodeThingProperties) {
        this.endNodeVendorThingID = endNodeVendorThingID;
        this.endNodeThingPassword = endNodeThingPassword;
        this.gatewayVendorThingID = gatewayVendorThingID;
        this.owner = owner;
        this.endNodeThingType = endNodeThingType;
        this.endNodeThingProperties = endNodeThingProperties;
    }

    public String getVenderThingID() {
        return venderThingID;
    }

    public String getThingPassword() {
        return thingPassword;
    }

    public String getThingType() {
        return thingType;
    }

    public String getEndNodeVendorThingID() {
        return endNodeVendorThingID;
    }

    public String getEndNodeThingPassword() {
        return endNodeThingPassword;
    }

    public String getGatewayVendorThingID() {
        return gatewayVendorThingID;
    }

    public String getOwner() {
        return owner;
    }

    public String getEndNodeThingType() {
        return endNodeThingType;
    }

    public JSONObject getEndNodeThingProperties() {
        return endNodeThingProperties;
    }
}
