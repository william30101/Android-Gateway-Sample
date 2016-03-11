package gatewaysample.kii.com.gateway_agent_androidsample.utils;


public class MappingObject {
    String position;
    String thingID;
    String vendorThingID;
    String accessToken;
    String ownerID;
    boolean isRegister;
    boolean isOnline;

    /***
     * use this class for mapping file .
     *
     * @param position      Gateway or EndNode
     * @param thingID       return ID from REST API
     * @param vendorThingID vendorThingID from onBoarding.
     * @param accessToken   owner AccessToken
     * @param ownerID       ownerID
     */
    public MappingObject(String position, String thingID, String vendorThingID, String accessToken, String ownerID, boolean isOnline, boolean isRegister) {
        this.position = position;
        this.thingID = thingID;
        this.vendorThingID = vendorThingID;
        this.accessToken = accessToken;
        this.ownerID = ownerID;
        this.isOnline = isOnline;
        this.isRegister = isRegister;
    }

    public String getPosition() {
        return position;
    }

    public String getThingID() {
        return thingID;
    }

    public String getVendorThingID() {
        return vendorThingID;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getOwnerID() {
        return ownerID;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setThingID(String thingID) {
        this.thingID = thingID;
    }

    public void setVendorThingID(String vendorThingID) {
        this.vendorThingID = vendorThingID;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public boolean isRegister() {
        return isRegister;
    }

    public void setIsRegister(boolean isRegister) {
        this.isRegister = isRegister;
    }

    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }

    public boolean isOnline() {
        return isOnline;
    }


}
