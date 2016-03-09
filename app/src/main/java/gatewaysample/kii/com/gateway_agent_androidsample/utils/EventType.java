package gatewaysample.kii.com.gateway_agent_androidsample.utils;

public class EventType {

    String who;
    Object body;

    public EventType(String who, Object body) {
        this.who = who;
        this.body = body;
    }

    public String getWho() {
        return who;
    }

    public Object getBody() {
        return body;
    }
}
