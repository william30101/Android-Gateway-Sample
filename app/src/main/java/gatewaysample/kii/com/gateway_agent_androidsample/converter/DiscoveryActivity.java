package gatewaysample.kii.com.gateway_agent_androidsample.converter;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import gatewaysample.kii.com.gateway_agent_androidsample.R;

public class DiscoveryActivity
{
	private final String TAG = "DiscoveryActivity";

	private Handler _handler = new Handler(){
		@Override
		public void handleMessage(android.os.Message msg) {
			int what = msg.what;
			String result = (String)msg.obj;

			if (result == "finished"){
				showDevices();
			}
		}
	};
	/* Get Default Adapter */
	private BluetoothAdapter _bluetooth = BluetoothAdapter.getDefaultAdapter();
	/* Storage the BT devices */
	private List<BluetoothDevice> _devices = new ArrayList<BluetoothDevice>();
	/* Discovery is Finished */
	private volatile boolean _discoveryFinished;

	CallBack discoveryCallback;

	private Context mContext;

	public DiscoveryActivity(Context context){
		mContext = context;
	}

	public void setCallBack(CallBack call){
		this.discoveryCallback = call;
	}

	private Runnable _stopDiscovery = new Runnable() {
		@Override
		public void run() {
			try {
				Thread.sleep(20000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Log.i(TAG, "stop discovery");

			stopScanning();
		}
	};

	private Runnable _discoveryWorkder = new Runnable() {
		public void run() 
		{
			/* Start search device */
			_bluetooth.startDiscovery();
			Log.d(TAG, ">>Starting Discovery");
			for (;;) 
			{
				if (_discoveryFinished) 
				{
					Log.d(TAG, ">>Finished");
					Message message = Message.obtain();
					message.what = 1;
					message.obj = "finished";
					_handler.sendMessage(message);

					break;
				}
				try 
				{
					Thread.sleep(100);
				} 
				catch (InterruptedException e){}
			}
		}
	};
	
	/**
	 * Receiver
	 * When the discovery finished be called.
	 */
	private BroadcastReceiver _foundReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			/* get the search results */
			BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			Log.d(TAG, ">>found device name : " + device.getName());
			/* add to list */
			_devices.add(device);
			/* show the devices list */
			//showDevices();
		}
	};
	private BroadcastReceiver _discoveryReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent)
		{
			/* unRegister Receiver */
			Log.d(TAG, ">>unregisterReceiver");
			mContext.unregisterReceiver(_foundReceiver);
			mContext.unregisterReceiver(this);
			_discoveryFinished = true;
		}
	};
	
	public void onStart()
	{
		
		/* BT isEnable */
		if (!_bluetooth.isEnabled()) {
			Log.w(TAG, ">>BTBee is disable!");

			return;
		}
		/* Register Receiver*/
		IntentFilter discoveryFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		mContext.registerReceiver(_discoveryReceiver, discoveryFilter);
		IntentFilter foundFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		mContext.registerReceiver(_foundReceiver, foundFilter);



		new Thread(_stopDiscovery).start();
		startScanning();


		/* show a dialog "Scanning..." */
//		SamplesUtils.indeterminate(mContext, _handler, "Scanning...", _discoveryWorkder, new OnDismissListener() {
//			public void onDismiss(DialogInterface dialog)
//			{
//
//				for (; _bluetooth.isDiscovering();)
//				{
//
//					_bluetooth.cancelDiscovery();
//				}
//
//				_discoveryFinished = true;
//			}
//		}, true);
	}



	private void startScanning() {

		//_handler.post(_discoveryWorkder);

		new Thread(_discoveryWorkder).start();

	}

	private void stopScanning(){



		for (; _bluetooth.isDiscovering(); )
		{

			_bluetooth.cancelDiscovery();
		}

		_discoveryFinished = true;
	}

	/* Show devices list */
	protected void showDevices()
	{
		List<String> list = new ArrayList<String>();
		if(_devices.size() > 0)
		{	
			for (int i = 0, size = _devices.size(); i < size; ++i)
			{
				StringBuilder b = new StringBuilder();
				BluetoothDevice d = _devices.get(i);
				b.append(d.getAddress());
				b.append('\n');
				b.append(d.getName());
				String s = b.toString();
				list.add(s);
			}
		}
		else
			list.add("There isn't bluetooth devices");
		Log.d(TAG, ">>showDevices");

		discoveryCallback.discoveryDone(_devices);

		//showList(list);



	}

//	private void showList(final List<String> lists){
//
//		Log.d(TAG," finded device size : " + _devices.size());
//		if (_devices.size() > 0){
//
//		}


//		new AlertDialog.Builder(mContext)
//				.setItems(lists.toArray(new String[lists.size()]), new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						String name = lists.get(which);
//						Toast.makeText(mContext, name, Toast.LENGTH_SHORT).show();
//
//						discoveryCallback.discoveryDone(_devices.get(which));
//						//ClientSocketActivity socket = new ClientSocketActivity();
//					}
//				})
//				.show();
//	}

//	/* Select device */
//	protected void onListItemClick(ListView l, View v, int position, long id)
//	{
//		Log.d(TAG, ">>Click device");
//		Intent result = new Intent();
//		result.putExtra(BluetoothDevice.EXTRA_DEVICE, _devices.get(position));
//		setResult(RESULT_OK, result);
//		finish();
//	}
}

