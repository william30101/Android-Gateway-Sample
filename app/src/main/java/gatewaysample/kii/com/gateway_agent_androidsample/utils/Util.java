package gatewaysample.kii.com.gateway_agent_androidsample.utils;


import com.kii.thingif.internal.utils.Log;

import java.util.Set;

public class Util {

    public static String getTID(String msg) {
        Thread t = Thread.currentThread();
        long l = t.getId();
        String name = t.getName();

        return msg + " ID : " + l;
    }


    public static Thread[] getAllThreads( ) {
        Set<Thread> threadSet = Thread.getAllStackTraces().keySet();

        Thread[] threadArray = threadSet.toArray(new Thread[threadSet.size()]);

        return threadArray;
    }
}
