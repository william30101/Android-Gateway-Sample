package gatewaysample.kii.com.gateway_agent_androidsample.utils;


public class Util {

    public static String getTID(String msg) {
        Thread t = Thread.currentThread();
        long l = t.getId();
        String name = t.getName();

        return msg + " ID : " + l;
    }
}
