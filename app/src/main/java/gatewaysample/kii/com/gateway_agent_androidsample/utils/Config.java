package gatewaysample.kii.com.gateway_agent_androidsample.utils;

import android.bluetooth.BluetoothDevice;

import com.kii.cloud.storage.Kii;

import java.util.List;

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
    public static final String MAPPING_FILE_NAME = "mapping.db";
    public static final String GATEWAY_USERNAME = "william.wu";
    public static final String GATEWAY_USER_PASS = "1qaz@WSX";

    // client to server command
    public static final String ENDNODE_ONBOARDING = "EndNode Onboarding";
    public static final String LIST_ENDNODE = "list_endNode";
    public static final String REPLACE_GATEWAY = "replace_gateway";
    public static final String REPLACE_ENDNODE = "replace_endnode";
    public static final String DEL_ENDNODE = "delete_endnode";
    public static final String READ_MAPPING_FILE = "read_mapping_file";
    public static final String SEND_CMD_TO_ENDNODE = "send_cmd_to_endnode";
    public static final String UPDATE_ENDNODE_STATES = "update_endnode_states";
    public static final String GET_ENDNODE_STATES = "get_endnode_states";
    public static final String UPDATE_ENDNODE_CONNECTION_STATUS = "update_endnode_conneciton_states";
    public static final String UPDATE_CMD_RESULT = "update_cmd_result";
    public static final String ENDNODE_ONBOARD = "EndNode Onboard";
    public static final String REGISTER_CMD = "Register";
    public static final String GET_GATEWAY_ID = "Get Gateway ID";
    public static final String GET_PENDING_ENDNODE = "Get Pending EndNode";
    public static final String ONBOARD_SUCCESS = "OnBoard Success";

    // Converter
    public static final String SEARCH_ENDNODE = "Search EndNode";

    // EventBus type
    public static final String SEND_FROM_BLUETOOTH_DEVICES = "Bluetooth Devices";
    public static final String SEND_FROM_BLUETOOTH_CONNECTED_COMPLETE = "Bluetooth Connect Complete";
    public static final String SEND_FROM_BLUETOOTH_CMD = "Bluetooth Cmd";
    public static final String SEND_FROM_GET_TOKEN = "Get token";
    public static final String SEND_FROM_GET_GATEWAY_ID= "Gateway ID";
    public static final String SEND_FROM_GET_PENDING_DEVICE = "Pending Device";
    public static final String SEND_FROM_DISCOVERY = "Discovery Device";
    public static final String SEND_FROM_CONNECT_DEVICE = "Connect Device";
    public static final String SEND_FROM_ENDNODE_ONBOARD = "endNode onboard";
    public static final String SEND_FROM_GET_ONBOARD_LIST = "onboard list";

}
