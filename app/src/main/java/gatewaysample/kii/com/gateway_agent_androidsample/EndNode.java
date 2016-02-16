package gatewaysample.kii.com.gateway_agent_androidsample;

/**
 * Created by william.wu on 2/15/16.
 */
public class EndNode {
    String vendorID;
    String authorID;
    String AccessToken;

    public EndNode(String vendorID, String authorID, String accessToken) {
        this.vendorID = vendorID;
        this.authorID = authorID;
        AccessToken = accessToken;
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
}
