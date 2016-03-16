/*******************************************************************************
 * Copyright (c) 1999, 2014 IBM Corp.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v1.0 which accompany this distribution. 
 *
 * The Eclipse Public License is available at 
 *    http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 *   http://www.eclipse.org/org/documents/edl-v10.php.
 */
package gatewaysample.kii.com.gateway_agent_androidsample.mqtt;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.LogManager;

import gatewaysample.kii.com.gateway_agent_androidsample.mqtt.Connection.ConnectionStatus;
import gatewaysample.kii.com.gateway_agent_androidsample.mqtt.ActionListener.Action;

import gatewaysample.kii.com.gateway_agent_androidsample.GatewayService;
import gatewaysample.kii.com.gateway_agent_androidsample.R;
import gatewaysample.kii.com.gateway_agent_androidsample.utils.Config;

public class Listener {

  /** The handle to a {@link Connection} object which contains the {@link MqttAndroidClient} associated with this object **/
  private String clientHandle = null;

  private GatewayService clientConnections = null;
  /** {@link Context} used to load and format strings **/
  private Context context = null;

  private ActionListener callback;

  /** Whether Paho is logging is enabled**/
  static boolean logging = false;

  public Listener(GatewayService clientConnections, String clientHandle, ActionListener callBack) {
    this.clientConnections = clientConnections;
    this.clientHandle = clientHandle;
    this.callback = callBack;
    context = clientConnections;
  }

  /**
   * Perform the needed action required based on the button that
   * the user has clicked.
   * 
   * @param item The menu item that was clicked
   * @return If there is anymore processing to be done
   * 
   */
//  @Override
//  public boolean onMenuItemClick(MenuItem item) {
//
//    int id = item.getItemId();
//
//    switch (id)
//    {
//      case R.id.publish :
//        publish();
//        break;
//      case R.id.subscribe :
//        subscribe();
//        break;
//      case R.id.newConnection :
//        createAndConnect();
//        break;
//      case R.id.disconnect :
//        disconnect();
//        break;
//      case R.id.connectMenuOption :
//        reconnect();
//        break;
//      case R.id.startLogging :
//        enablePahoLogging();
//        break;
//      case R.id.endLogging :
//        disablePahoLogging();
//        break;
//    }
//
//    return false;
//  }

  /**
   *
   * @param id : select item id.
   * @param topic : MQTT Topic
   * @param message : MQTT PUSH message
   */
  public void mqttItemSelect(String id, String topic, String message){
    switch (id){
      case Config.ID_PUBLISH :
        publish(topic, message);
        break;
      case Config.ID_SUBSCRIBE :
        subscribe(topic);
        break;
//      case R.id.newConnection :
//        createAndConnect();
//        break;
//      case R.id.disconnect :
//        disconnect();
//        break;
//      case R.id.connectMenuOption :
//        reconnect();
//        break;
//      case R.id.startLogging :
//        enablePahoLogging();
//        break;
//      case R.id.endLogging :
//        disablePahoLogging();
//        break;
    }

  }

  /**
   * Reconnect the selected client
   */
  private void reconnect() {

    Connections.getInstance(context).getConnection(clientHandle).changeConnectionStatus(ConnectionStatus.CONNECTING);

    Connection c = Connections.getInstance(context).getConnection(clientHandle);
    try {
      c.getClient().connect(c.getConnectionOptions(), null, new ActionListener(context, Action.CONNECT, clientHandle, null));
    }
    catch (MqttSecurityException e) {
      Log.e(this.getClass().getCanonicalName(), "Failed to reconnect the client with the handle " + clientHandle, e);
      c.addAction("Client failed to connect");
    }
    catch (MqttException e) {
      Log.e(this.getClass().getCanonicalName(), "Failed to reconnect the client with the handle " + clientHandle, e);
      c.addAction("Client failed to connect");
    }

  }

  /**
   * Disconnect the client
   */
  private void disconnect() {

    Connection c = Connections.getInstance(context).getConnection(clientHandle);

    //if the client is not connected, process the disconnect
    if (!c.isConnected()) {
      return;
    }

    try {
      c.getClient().disconnect(null, new ActionListener(context, Action.DISCONNECT, clientHandle, null));
      c.changeConnectionStatus(ConnectionStatus.DISCONNECTING);
    }
    catch (MqttException e) {
      Log.e(this.getClass().getCanonicalName(), "Failed to disconnect the client with the handle " + clientHandle, e);
      c.addAction("Client failed to disconnect");
    }

  }

  /**
   * Subscribe to a topic that the user has specified
   */
  private void subscribe(String topic)
  {
//    String topic = ((EditText) connectionDetails.findViewById(R.id.topic)).getText().toString();
//    ((EditText) connectionDetails.findViewById(R.id.topic)).getText().clear();
//
//    RadioGroup radio = (RadioGroup) connectionDetails.findViewById(R.id.qosSubRadio);
//    int checked = radio.getCheckedRadioButtonId();
//    int qos = ActivityConstants.defaultQos;

    int qos = 0;
//
//    switch (checked) {
//      case R.id.qos0 :
//        qos = 0;
//        break;
//      case R.id.qos1 :
//        qos = 1;
//        break;
//      case R.id.qos2 :
//        qos = 2;
//        break;
//    }

    try {
      String[] topics = new String[1];
      topics[0] = topic;
      Connections.getInstance(context).getConnection(clientHandle).getClient()
          .subscribe(topic, qos, null, new ActionListener(context, Action.SUBSCRIBE, clientHandle, topics));
    }
    catch (MqttSecurityException e) {
      Log.e(this.getClass().getCanonicalName(), "Failed to subscribe to" + topic + " the client with the handle " + clientHandle, e);
    }
    catch (MqttException e) {
      Log.e(this.getClass().getCanonicalName(), "Failed to subscribe to" + topic + " the client with the handle " + clientHandle, e);
    }
  }

  /**
   * Publish the message the user has specified
   */
  private void publish(String topic, String message)
  {
//    String topic = ((EditText) connectionDetails.findViewById(R.id.lastWillTopic))
//        .getText().toString();
//
//    ((EditText) connectionDetails.findViewById(R.id.lastWillTopic)).getText().clear();
//
//    String message = ((EditText) connectionDetails.findViewById(R.id.lastWill)).getText()
//        .toString();
//
//    ((EditText) connectionDetails.findViewById(R.id.lastWill)).getText().clear();
//
//    RadioGroup radio = (RadioGroup) connectionDetails.findViewById(R.id.qosRadio);
//    int checked = radio.getCheckedRadioButtonId();
//    int qos = ActivityConstants.defaultQos;
//
//    switch (checked) {
//      case R.id.qos0 :
//        qos = 0;
//        break;
//      case R.id.qos1 :
//        qos = 1;
//        break;
//      case R.id.qos2 :
//        qos = 2;
//        break;
//    }

    int qos = 0;

//    boolean retained = ((CheckBox) connectionDetails.findViewById(R.id.retained))
//        .isChecked();

    boolean retained = true;

    String[] args = new String[2];
    args[0] = message;
    args[1] = topic+";qos:"+qos+";retained:"+retained;

    try {
      Connections.getInstance(context).getConnection(clientHandle).getClient()
          .publish(topic, message.getBytes(), qos, retained, null, new ActionListener(context, Action.PUBLISH, clientHandle, args));
    }
    catch (MqttSecurityException e) {
      Log.e(this.getClass().getCanonicalName(), "Failed to publish a messged from the client with the handle " + clientHandle, e);
    }
    catch (MqttException e) {
      Log.e(this.getClass().getCanonicalName(), "Failed to publish a messged from the client with the handle " + clientHandle, e);
    }

  }

  /**
   * Create a new client and connect
   */
  private void createAndConnect()
  {
    Intent createConnection;

    //start a new activity to gather information for a new connection
    createConnection = new Intent();
    createConnection.setClassName(
        clientConnections.getApplicationContext(),
        "org.eclipse.paho.android.service.sample.NewConnection");

//    clientConnections.startActivityForResult(createConnection,
//        ActivityConstants.connect);
  }

  /**
   * Enables logging in the Paho MQTT client
   */
  private void enablePahoLogging() {
//
//    try {
//      InputStream logPropStream = context.getResources().openRawResource(R.raw.jsr47android);
//      LogManager.getLogManager().readConfiguration(logPropStream);
//      logging = true;
//
//      HashMap<String, Connection> connections = (HashMap<String,Connection>)Connections.getInstance(context).getConnections();
//      if(!connections.isEmpty()){
//    	  Entry<String, Connection> entry = connections.entrySet().iterator().next();
//    	  Connection connection = (Connection)entry.getValue();
//    	  connection.getClient().setTraceEnabled(true);
//    	  //change menu state.
//    	 // clientConnections.invalidateOptionsMenu();
//    	  //Connections.getInstance(context).getConnection(clientHandle).getClient().setTraceEnabled(true);
//      }else{
//    	  Log.i("SampleListener", "No connection to enable log in service");
//      }
//    }
//    catch (IOException e) {
//      Log.e("MqttAndroidClient",
//              "Error reading logging parameters", e);
//    }

  }

  /**
   * Disables logging in the Paho MQTT client
   */
  private void disablePahoLogging() {
    LogManager.getLogManager().reset();
    logging = false;
    
    HashMap<String, Connection> connections = (HashMap<String,Connection>)Connections.getInstance(context).getConnections();
    if(!connections.isEmpty()){
  	  Entry<String, Connection> entry = connections.entrySet().iterator().next();
  	  Connection connection = (Connection)entry.getValue();
  	  connection.getClient().setTraceEnabled(false);
  	  //change menu state.
  	  //clientConnections.invalidateOptionsMenu();
    }else{
  	  Log.i("SampleListener", "No connection to disable log in service");
    }
   // clientConnections.invalidateOptionsMenu();
  }

}
