package gatewaysample.kii.com.gateway_agent_androidsample.converter;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.greenrobot.event.EventBus;
import gatewaysample.kii.com.gateway_agent_androidsample.GatewayService;
import gatewaysample.kii.com.gateway_agent_androidsample.utils.Config;
import gatewaysample.kii.com.gateway_agent_androidsample.utils.EventType;
import gatewaysample.kii.com.gateway_agent_androidsample.utils.MappingObject;
import gatewaysample.kii.com.gateway_agent_androidsample.utils.MyEvent;

public class ClientSocketActivity implements CallBack
{
	private static final String TAG = ClientSocketActivity.class.getSimpleName();
	private static final int REQUEST_DISCOVERY = 0x1;
	private Handler _handler = new Handler();
	private BluetoothAdapter _bluetooth = BluetoothAdapter.getDefaultAdapter();
	
	private BluetoothSocket socket = null;
	private TextView sTextView;
	private EditText sEditText;
	private String str;
	private OutputStream outputStream;
	private InputStream inputStream;
	private StringBuffer sbu;
	private Context mContext;
	DiscoveryActivity dis ;
	private EventBus mEventBus;
	public static boolean needStop = false;
	ReadInput readInput;
	private String inputJson;
	List<MappingObject> mMappingTable = new ArrayList<>();

	public ClientSocketActivity(Context context){
		mContext = context;
		dis = new DiscoveryActivity(mContext);
	}

	public void onStart() {

		mEventBus = EventBus.getDefault();

		_bluetooth.enable();


		//Wait 5 sec for BT enable.
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		if (!_bluetooth.isEnabled()) {
			Log.i(TAG,"BT not enabled.");
			return;
		}


		Log.i(TAG,"BT enabled.");

		//Toast.makeText(mContext, "select device to connect", Toast.LENGTH_SHORT).show();
		dis.setCallBack(this);
		dis.onStart();


	}

//
//	/* after select, connect to device */
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		if (requestCode == REQUEST_ENABLE) {
//			//Toast.makeText(mContext, "select device to connect", Toast.LENGTH_SHORT).show();
//			dis.setCallBack(this);
//			dis.onStart();
//			return;
//		}
//	}

	public void connectDevice(BluetoothDevice btdevice, List<MappingObject> mappingTable){
		final BluetoothDevice device = btdevice;
		mMappingTable = mappingTable;
//
//		Thread myThraed = new Thread(new Runnable() {
//			@Override
//			public void run() {
//				connect(device);
//			}
//		});
//
//		myThraed.start();
		connect(device);
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				connect(device);
//			}
//		}).start();
		//connect(device);

	}

	protected void onDestroy() {
//		onDestroy();
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e(TAG, ">>", e);
		}
	}


	protected void connect(BluetoothDevice device) {
		//BluetoothSocket socket = null;
		try {
			//Create a Socket connection: need the server's UUID number of registered
			socket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
			
			socket.connect();
			Log.d(TAG, ">>Client connectted");
		
			inputStream = socket.getInputStream();														
			outputStream = socket.getOutputStream();

			//startGetData();

			sendEventToService(new EventType(Config.SEND_FROM_BLUETOOTH_CONNECTED_COMPLETE, device.getName()));
			//int read = -1;
			//final byte[] bytes = new byte[2048];
			//for (; (read = inputStream.read(bytes)) > -1;) {

			startGetData(device.getName());
//			_handler.post(readInput);

//				_handler.post(new Runnable() {
//					int read = -1;
//					int count = 0;
//
//					public void run() {
//						try {
//							read = inputStream.read(bytes);
//
//							if (read > -1) {
//								//count = read;
//
//								inputJson = new String(bytes);
//								Log.d(TAG, "read back : " + inputJson);
//								sendEventToService(new EventType(Config.SEND_FROM_BLUETOOTH_CMD, inputJson));
//							}
//
//						} catch (IOException e) {
//							e.printStackTrace();
//						}
//
//						_handler.pos
//
//
////						StringBuilder b = new StringBuilder();
////						for (int i = 0; i < count; ++i) {
////							String s = Integer.toString(bytes[i]);
////							b.append(s);
////							b.append(",");
////						}
////						String s = b.toString();
////						String[] chars = s.split(",");
////						sbu = new StringBuffer();
////						 for (int i = 0; i < chars.length; i++) {
////						        sbu.append((char) Integer.parseInt(chars[i]));
////						    }
////						Log.d(TAG, ">>inputStream");
////						if(str != null)
////						{
////							//sTextView.setText(str + "<-- " + sbu);
////							Log.d(TAG,str + "<-- " + sbu);
////							str += ("<-- " + sbu.toString());
////
////						}
////						else
////						{
////							//sTextView.setText("<-- " + sbu);
////							Log.d(TAG, "<-- " + sbu);
////							str = "<-- " + sbu.toString();
////						}
//						//sendEventToService(new EventType(Config.SEND_FROM_BLUETOOTH_CMD, inputJson));
//
//						//str += '\n';
//
//					}
//				});
			//}
			
		} catch (IOException e) {
			Log.e(TAG, ">>", e);

			return ;
		} //finally {
//			if (socket != null) {
//				try {
//					Log.d(TAG, ">>Client Socket Close");
//					socket.close();
//
//					return ;
//				} catch (IOException e) {
//					Log.e(TAG, ">>", e);
//				}
//			}
//		}
	}

	public void startGetData(String deviceName) {
		if (socket != null ) {
			GatewayService gateway;
			// If device onboard , get thingID from table.
			String thingID = "";
			for (int i=0 ; i< mMappingTable.size(); i++){
				if (mMappingTable.get(i).getVendorThingID().equals(deviceName)){
					thingID = mMappingTable.get(i).getThingID();
				}
			}

			//TODO : If we haven't onboarding , don't run reading status thread.
			//		 fix this when need to get status first.
			if (!thingID.equals(Config.unOnboardingThingID)){
				ReadInput.needStop = false;
				if (thingID != ""){
					readInput = new ReadInput(socket, _handler, deviceName, thingID);
				}else{
					readInput = new ReadInput(socket, _handler);
				}


				//handler.postDelayed(r, currentSensorList.getCollectFre() * 1000);

				Thread myThread = new Thread(readInput, Config.READ_THREAD_NAME);
				myThread.start();

			}

		}

	}

	/*
	*
	* JSON string : {"schema":"Smart-Light-Demo",
	* 				 "schemaVersion":1,
	* 				 "type":"GATEWAY_ENVELOPED",
	* 				 "commandID":"59040aa0-e5d5-11e5-86af-22000b02f3b7",
	* 				 "actions":[{"turnPower":{"power":true}},{"setColor":{"color":[20,50,200]}},{"setBrightness":{"brightness":120}},{"setColorTemperature":{"colorTemperature":35}}],
	* 				 "targets":["THING:th.727f20b00022-69c9-5e11-1c5e-093713a0"],
	* 				 "issuer":"user:2be88ba00022-12b8-5e11-1f0c-01266af7"}
	* */

	// msg including schema and actions.
	public void sendCmdToEndNode(JSONObject msg){


		//TODO according thingID decide target , wrap action

		// Only get action here.
		boolean power = false ;
		String cmdID = "";
		try {
			cmdID = msg.optString("commandID");

			JSONArray actionsArr = msg.getJSONArray("actions");

			power = actionsArr.getJSONObject(0).getJSONObject("turnPower").optBoolean("power");
			JSONArray color = actionsArr.getJSONObject(1).getJSONObject("setColor").getJSONArray("color");
			int colors[] = new int[3];
			for (int i=0 ; i< color.length(); i++){
				colors[i] = (int)color.get(i);
			}
			Log.i(TAG,"power : " + power +  " setColor : " + color.get(0).toString());



		} catch (JSONException e) {
			e.printStackTrace();
		}

		// Use power attribute for test.
			String tmpStr = "0";
		if (power)
			tmpStr = "1";
		else
			tmpStr = "0";

			byte bytesin[] = tmpStr.getBytes();
		try {
			outputStream.write(bytesin);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void discoveryDone( List<BluetoothDevice> devices) {
		//Return to gatewayService , for user.

		//connectDevice(device);


		sendEventToService(new EventType(Config.SEND_FROM_BLUETOOTH_DEVICES, devices));


	}

	public void sendEventToService(EventType obj){
		MyEvent event = new MyEvent();
		event.setMyEventString(obj);
		mEventBus.post(event);
	}
}

