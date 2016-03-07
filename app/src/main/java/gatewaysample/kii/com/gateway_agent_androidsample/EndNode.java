package gatewaysample.kii.com.gateway_agent_androidsample;


public class EndNode {
    String vendorID;
    String authorID;
    String AccessToken;
    boolean isOnline;

    public EndNode(String vendorID, String authorID, String accessToken, boolean isOnline) {
        this.vendorID = vendorID;
        this.authorID = authorID;
        this.AccessToken = accessToken;
        this.isOnline = isOnline;
    }

    public String getVendorID() {
        return vendorID;
    }

    public String getAuthorID() {
        return authorID;
    }

    public String getAccessToken() {
        return AccessToken;
    }

    public boolean isOnline() {
        return isOnline;
    }
}
