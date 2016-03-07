package gatewaysample.kii.com.gateway_agent_androidsample.utils;


public class KiiThingInfo {

       String   thingID;
       String   vendorThingID;
       String   thingType;
       String   layoutPosition;
       boolean  isDisabled;
       boolean  isOnline;

    public KiiThingInfo(String thingID, String vendorThingID, String thingType, String layoutPosition, boolean isDisabled, boolean isOnline) {
        this.thingID = thingID;
        this.vendorThingID = vendorThingID;
        this.thingType = thingType;
        this.layoutPosition = layoutPosition;
        this.isDisabled = isDisabled;
        this.isOnline = isOnline;
    }

    public String getThingID() {
        return thingID;
    }

    public String getVendorThingID() {
        return vendorThingID;
    }

    public String getThingType() {
        return thingType;
    }

    public String getLayoutPosition() {
        return layoutPosition;
    }

    public boolean isDisabled() {
        return isDisabled;
    }

    public boolean isOnline() {
        return isOnline;
    }
}
