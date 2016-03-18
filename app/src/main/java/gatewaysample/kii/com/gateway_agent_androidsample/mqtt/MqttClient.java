package gatewaysample.kii.com.gateway_agent_androidsample.mqtt;

import android.content.Context;
import android.util.Log;

import com.kii.thingif.gateway.MqttEndpoint;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import gatewaysample.kii.com.gateway_agent_androidsample.GatewayService;
import gatewaysample.kii.com.gateway_agent_androidsample.mqtt.Connection.ConnectionStatus;
import gatewaysample.kii.com.gateway_agent_androidsample.utils.Config;
import gatewaysample.kii.com.gateway_agent_androidsample.mqtt.ActionListener.Action;

public class MqttClient {

    private Context mContext;
    private MqttEndpoint mMqttEndPoint;
    MqttAndroidClient client;
    Listener itemListener;
    String topic = "";
    /***
     *
     * @param context returned by the Activity
     * @param mqttEndPoint receive from Gateway onboarding EndoPoint.
     */
    public MqttClient(Context context, MqttEndpoint mqttEndPoint) {
        this.mContext = context;
        this.mMqttEndPoint =  mqttEndPoint;
    }

    /**
     * Process data from the connect action
     */
    public void connect() {

        MqttConnectOptions conOpt = new MqttConnectOptions();

    /*
     * Mutal Auth connections could do something like this
     *
     *
     * SSLContext context = SSLContext.getDefault();
     * context.init({new CustomX509KeyManager()},null,null); //where CustomX509KeyManager proxies calls to keychain api
     * SSLSocketFactory factory = context.getSSLSocketFactory();
     *
     * MqttConnectOptions options = new MqttConnectOptions();
     * options.setSocketFactory(factory);
     *
     * client.connect(options);
     *
     */

        // The basic client information
        String server =  mMqttEndPoint.getHost();
        String clientId =  mMqttEndPoint.getMqttTopic();
        int port = Integer.parseInt( mMqttEndPoint.getPortTCP());

        //boolean cleanSession = (Boolean) data.get(ActivityConstants.cleanSession);
//
//        boolean ssl = (Boolean) data.get(ActivityConstants.ssl);
//        String ssl_key = (String) data.get(ActivityConstants.ssl_key);
        String uri = "tcp://";

        uri = uri + server + ":" + port;


        client = gatewaysample.kii.com.gateway_agent_androidsample.mqtt.Connections.getInstance(mContext).createClient(mContext, uri, clientId);

        // create a client handle
        String clientHandle = uri + clientId;

        // last will message
        String message = "";
        topic = mMqttEndPoint.getMqttTopic();
        Integer qos = 0;





        // connection options

        String username = mMqttEndPoint.getUsername();

        String password = mMqttEndPoint.getPassword();

        int timeout = 300;
        int keepalive = 300;

        Connection connection = new Connection(clientHandle, clientId, server, port,
                mContext, client, false);

        connection.registerChangeListener(changeListener);
        // connect client

        String[] actionArgs = new String[1];
        actionArgs[0] = clientId;
        connection.changeConnectionStatus(ConnectionStatus.CONNECTING);

        conOpt.setCleanSession(false);
        conOpt.setConnectionTimeout(timeout);
        conOpt.setKeepAliveInterval(keepalive);
        if (!username.equals(gatewaysample.kii.com.gateway_agent_androidsample.mqtt.ActivityConstants.empty)) {
            conOpt.setUserName(username);
        }
        if (!password.equals(gatewaysample.kii.com.gateway_agent_androidsample.mqtt.ActivityConstants.empty)) {
            conOpt.setPassword(password.toCharArray());
        }

        final gatewaysample.kii.com.gateway_agent_androidsample.mqtt.ActionListener callback = new gatewaysample.kii.com.gateway_agent_androidsample.mqtt.ActionListener(mContext,
                gatewaysample.kii.com.gateway_agent_androidsample.mqtt.ActionListener.Action.CONNECT, clientHandle, actionArgs);

        boolean doConnect = true;

//        if ((!message.equals(ActivityConstants.empty))
//                || (!topic.equals(ActivityConstants.empty))) {
//            // need to make a message since last will is set
//            try {
//                conOpt.setWill(topic, message.getBytes(), qos.intValue(),
//                        retained.booleanValue());
//            }
//            catch (Exception e) {
//                Log.e(this.getClass().getCanonicalName(), "Exception Occured", e);
//                doConnect = false;
//                callback.onFailure(null, e);
//            }
//        }

        itemListener = new Listener((GatewayService)mContext, clientHandle, callback);

        client.setCallback(new MqttCallbackHandler(mContext, clientHandle));


        //set traceCallback
        client.setTraceCallback(new MqttTraceCallback());

        connection.addConnectionOptions(conOpt);
        Connections.getInstance(mContext).addConnection(connection);
        IMqttToken token = null;
        if (doConnect) {
            try {
                token = client.connect(conOpt, null, callback);

                //itemListener.mqttItemSelect(Config.ID_SUBSCRIBE, topic, message);
            }
            catch (MqttException e) {
                Log.e(this.getClass().getCanonicalName(),
                        "MqttException Occured", e);
            }

            //Prevent MQTT not ready.
            try {
                Thread.sleep(600);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (token != null){
                try {

                    String[] topics = new String[1];
                    topics[0] = topic;
                    client.subscribe(topic, qos, null,  new ActionListener(mContext, Action.SUBSCRIBE, clientHandle, topics));
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public void disConnect(){
        if (client.isConnected()){
            try {
                client.disconnect();
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

    }

    private ChangeListener changeListener = new ChangeListener();

    /**
     * This class ensures that the user interface is updated as the Connection objects change their states
     *
     *
     */
    private class ChangeListener implements PropertyChangeListener {

        /**
         * @see PropertyChangeListener#propertyChange(PropertyChangeEvent)
         */
        @Override
        public void propertyChange(PropertyChangeEvent event) {

            if (!event.getPropertyName().equals(ActivityConstants.ConnectionStatusProperty)) {
                return;
            }
//            clientConnections.runOnUiThread(new Runnable() {
//
//                @Override
//                public void run() {
//                    clientConnections.arrayAdapter.notifyDataSetChanged();
//                }
//
//            });

        }

    }

}
