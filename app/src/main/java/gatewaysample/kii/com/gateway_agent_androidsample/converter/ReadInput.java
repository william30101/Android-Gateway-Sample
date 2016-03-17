package gatewaysample.kii.com.gateway_agent_androidsample.converter;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

import de.greenrobot.event.EventBus;
import gatewaysample.kii.com.gateway_agent_androidsample.utils.Config;
import gatewaysample.kii.com.gateway_agent_androidsample.utils.EventType;
import gatewaysample.kii.com.gateway_agent_androidsample.utils.MyEvent;
import gatewaysample.kii.com.gateway_agent_androidsample.utils.Util;


class ReadInput implements Runnable {
	private final String TAG = "ReadInput";
	BluetoothSocket mmSocket;
	public static boolean needStop = false;
	public static boolean debugDataEnable = false;
	Handler mHandler ;
	private EventBus mEventBus;
	//We use this flag to decide update state.
	public static String mThingID = "";
	public static String mVendorThingID = "";

	private String[] debugData = {  "{\"messages\":[{\"Recnum\":\"0\",\"Temp\":\"22\",\"humidity\":\"40\",\"date\":\"2016/1/1/10/3/30\"}]}",
									"{\"messages\":[{\"Recnum\":\"1\",\"Temp\":\"23\",\"humidity\":\"39\",\"date\":\"2016/2/1/10/3/31\"}]}", 
									"{\"messages\":[{\"Recnum\":\"2\",\"Temp\":\"24\",\"humidity\":\"38\",\"date\":\"2016/3/1/10/3/32\"}]}",
									"{\"messages\":[{\"Recnum\":\"3\",\"Temp\":\"25\",\"humidity\":\"37\",\"date\":\"2016/4/1/10/3/33\"}]}",
									"{\"messages\":[{\"Recnum\":\"4\",\"Temp\":\"26\",\"humidity\":\"36\",\"date\":\"2016/5/1/10/3/34\"}]}",
									"{\"messages\":[{\"Recnum\":\"5\",\"Temp\":\"27\",\"humidity\":\"35\",\"date\":\"2016/6/1/10/3/35\"}]}"};

	//EndNode OnBoard.
	public ReadInput(BluetoothSocket socket, Handler handler, String vendorThingID, String thingID) {
		mmSocket = socket;
		mHandler = handler;
		mVendorThingID = vendorThingID;
		mThingID = thingID;
		mEventBus = EventBus.getDefault();

	}

	//EndNode not OnBoard.
	public ReadInput(BluetoothSocket socket, Handler handler) {
		mmSocket = socket;
		mHandler = handler;
		mEventBus = EventBus.getDefault();

	}

	public static long getThreadId()
	{
		Thread t = Thread.currentThread();
		return t.getId();
	}



	@Override
	public void run() {
		InputStream inputStream;
		String inputJson = "";
		// Always read data from endNode , if connected.
		// otherwise , disconnect or exit app.
		while (!needStop) {

			Log.i(TAG, Util.getTID("BT read thread"));
			Log.i(TAG,"thingID : " + mThingID + " vendorthingID:" + mVendorThingID);
			if (debugDataEnable) {
				//generateTestData();
//				if (debugDataCount >= debugData.length - 1)
//					debugDataCount = 0;
//				else
//					debugDataCount++;
//				
//				inputJson = debugData[debugDataCount];
//				parseJsonMulti(inputJson);
//				handler.postDelayed(this, 500);
			} else {

				try {
					inputStream = mmSocket.getInputStream();
					final byte[] bytes = new byte[256];
					if (inputStream.available() > 0) {


						if (inputStream.read(bytes) > -1){
							inputJson = new String(bytes);
							Log.d(TAG, "read back : " + inputJson);
							JSONObject obj = parseJSON(inputJson);
//							boolean cmdResult = false;
//							if (obj != null){
//								//TODO Doing some data transfer from endNode.
//								cmdResult = obj.has("result");
//							}
							if (mThingID != "" && !obj.equals(null) ){
								sendEventToService(new EventType(Config.SEND_FROM_BLUETOOTH_CMD, new BluetoothRetStates(mVendorThingID, mThingID , obj)));
							}
						}




//						int read = -1;
//						for (; (read = inputStream.read(bytes)) > -1;) {
//							final int count = read;
//
//								StringBuilder b = new StringBuilder();
//								for (int i = 0; i < count; ++i) {
//									String s = Integer.toString(bytes[i]);
//									b.append(s);
//									b.append(",");
//								}
//								String s = b.toString();
//								String[] chars = s.split(",");
//								sbu = new StringBuffer();
//								for (int i = 0; i < chars.length; i++) {
//									sbu.append((char) Integer.parseInt(chars[i]));
//								}
//								Log.d(TAG, ">>inputStream");
//								if(str != null)
//								{
//									//sTextView.setText(str + "<-- " + sbu);
//									Log.d(TAG,str + "<-- " + sbu);
//									str += ("<-- " + sbu.toString());
//
//								}
//								else
//								{
//									//sTextView.setText("<-- " + sbu);
//									Log.d(TAG, "<-- " + sbu);
//									str = "<-- " + sbu.toString();
//								}
//								//sendEventToService(new EventType(Config.SEND_FROM_BLUETOOTH_CMD, str));
//
//								str += '\n';
//						}
					}




					} catch (IOException e) {
					e.printStackTrace();
				}
			}

			try {
				Thread.sleep(Config.READ_ENDNODE_TIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	public static int nthIndexOf(final String string, final String token, final int index) {
		int j = 0;

		for (int i = 0; i < index; i++) {
			j = string.indexOf(token, j);
			if (j == -1)
				break;
		}

		return j;
	}

	private JSONObject parseJSON(String msg){

		String subMsg  = "";
		int leftBracket = nthIndexOf(msg, "{", 1);
		int rightBracket = nthIndexOf(msg, "}", 1);
		Log.d(TAG,"left : " + leftBracket + "  right: " + rightBracket);

		if (leftBracket != -1 &&  rightBracket != -1 && leftBracket < rightBracket ){
			subMsg = msg.substring(leftBracket, rightBracket + 1);
			Log.i(TAG,"read subMsg : " + subMsg);
			try {
				JSONObject jsonMsg  = new JSONObject(subMsg);

				return jsonMsg;

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return new JSONObject();


	}
//
//	private void generateTestData(){
//		String date="2015/1/1/10/3/30";
//		String incDate;
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/M/d/HH/mm/ss");
//		Calendar c = Calendar.getInstance();
//		try {
//			c.setTime(sdf.parse(date));
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		int maxDay = c.getActualMaximum(Calendar.DAY_OF_YEAR);
//		for(int co=0; co<maxDay; co++){
//		    c.add(Calendar.DATE, 1);
//		    incDate = sdf.format(c.getTime());
//		    Log.i("william", "increase date : " + incDate);
//		    sensordb.insertData(new SensorData(0,co, plantID, Utility.stringToDate(incDate, "yyyy/M/d/HH/mm/ss"), co % 20, co % 30));
//
//		}
//
//
//
//		//return sensorDataTest;
//
//	}
//
//	// 解析多個數據的Json
//	private void parseJsonMulti(String strResult) {
//
//		Log.i("william", "parseJson str = " + strResult);
//		String sb = "";
//		try {
//
//			JSONTokener jsonParser = new JSONTokener(strResult);
//			JSONObject jsonObject = (JSONObject) jsonParser.nextValue();
//			JSONArray jsonObjs = jsonObject.getJSONArray("messages");
//
//			for (int i = 0; i < jsonObjs.length(); i++) {
//				JSONObject jsonObj = ((JSONObject) jsonObjs.opt(i));
//
//				System.out.println("Jsons parse error 0");
//
//				int id = jsonObj.getInt("Recnum");
//				int tempData = jsonObj.getInt("Temp");
//				int humidityData = jsonObj.getInt("humidity");
//				String date = jsonObj.getString("date");
//				// double d_name = name;
//				// double d_gender = gender;
//				//
//				// if(count < 6){
//				// nums[count] = d_name;
//				// nums2[count] = d_gender;
//				//// x.add(nums);
//				//// y.add(nums2);
//				// count++;
//				// }
//				// x.add(new double[] { 1, 3, 5, 7, 9, 11 });
//				// x.add(new double[] { 0, 2, 4, 6, 8, 10 });
//				// y.add(new double[] { 3, 14, 8, 22, 16, 18 });
//				// y.add(new double[] { 20, 18, 15, 12, 10, 8 });
//				// XYMultipleSeriesDataset dataset = buildDatset(titles, x, y);
//				// // 儲存座標值
//
//				sb += "Recnum:" + id + ",Temp：" + tempData + ",humidity：" + humidityData + " date:" + date + "\n";
//				// System.out.println(sb);
//				//2015/12/1/10/3/29
//				//String dbDate = Utility.arduinoDatStringToDBDateString(date);
//				if (dateExist(Utility.stringToDate(date, "yyyy/M/d/HH/mm/ss")))
//					continue;
//
//				sensorData = new SensorData(id, id ,plantID, Utility.stringToDate(date, "yyyy/M/d/HH/mm/ss"), tempData, humidityData);
//				// connectMonitorPoint.myHandler.sendMessage(message);
//				sensordb.insertData(sensorData);
//				if (mHandler != null && showOnChart == true)
//					mHandler.obtainMessage(Config.HANDLER_UPDATE_DATA, sensorData).sendToTarget();
//
//				Log.i("william", sb);
//
//			}
//
//			// tvJson.setText(sb);
//		} catch (JSONException e) {
//			Log.i("william", "Jsons parse error !!!!");
//			e.printStackTrace();
//		}
//
//	}
//
//	private boolean dateExist(Date compareDate){
//		if (sensordb.getCount() != 0) {
//			List<SensorData> allSensorDate = Utility.getAllSensorDate();
//			for (int i = 0; i < allSensorDate.size(); i++) {
//				if (allSensorDate.get(i).getDate().equals(compareDate)){
//					Log.i("william", "found exist date : " + allSensorDate.get(i).getDate() + "  compareDate : " + compareDate);
//					return true;
//				}
//			}
//			Log.i("william", "couldn't find exist date : " + compareDate);
//			return false;
//		}
//		return false;
//	}
//
//	private Date stringToDate(String aDate,String aFormat) {
//
//	      if(aDate==null) return null;
//	      ParsePosition pos = new ParsePosition(0);
//	      SimpleDateFormat simpledateformat = new SimpleDateFormat(aFormat);
//	      Date stringDate = simpledateformat.parse(aDate, pos);
//	      //Log.i("william", "return date : " + stringDate);
//	      return stringDate;
//
//	 }


	public void sendEventToService(EventType obj){
		MyEvent event = new MyEvent();
		event.setMyEventString(obj);
		mEventBus.post(event);
	}
}
