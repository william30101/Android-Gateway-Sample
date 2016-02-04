package gatewaysample.kii.com.gateway_agent_androidsample.utils;

import com.kii.cloud.storage.Kii;

/**
 * Created by mac on 2/3/16.
 */
public class Config {
    public final static String APP_ID = "dc1df0f2";
    public final static String APP_KEY = "4ada5cf84dc3dd2c608894cf3b8f897e";
    public final static Kii.Site APP_SITE = Kii.Site.JP;
    public final static String SENDERID = "561160951492";
    public final static String IOTAPPBASEURL = "https://api-jp.kii.com";
    public final static String APPBASEURL = IOTAPPBASEURL + "/api";
    public static final String THING_TYPE = "SmartLight-Demo";
    public static final String SCHEMA_NAME = "Smart-Light-Demo";
    public static final int SCHEMA_VERSION = 1;
}
