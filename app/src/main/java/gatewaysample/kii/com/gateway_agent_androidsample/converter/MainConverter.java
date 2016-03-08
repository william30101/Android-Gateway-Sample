package gatewaysample.kii.com.gateway_agent_androidsample.converter;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import gatewaysample.kii.com.gateway_agent_androidsample.R;

import static android.support.v4.app.ActivityCompat.startActivityForResult;

public class MainConverter extends Activity {

    /* Get Default Adapter */
    private BluetoothAdapter	_bluetooth				= BluetoothAdapter.getDefaultAdapter();

    /* request BT enable */
    private static final int	REQUEST_ENABLE			= 0x1;
    /* request BT discover */
    private static final int	REQUEST_DISCOVERABLE	= 0x2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        enableBT();
    }

    private void enableBT(){
        _bluetooth.enable();
//
//        Intent enabler = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
//        startActivityForResult(enabler, REQUEST_DISCOVERABLE);

        Intent enabler = new Intent(this, ClientSocketActivity.class);
        startActivity(enabler);

    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(resultCode == RESULT_OK){
//            if(requestCode == REQUEST_DISCOVERABLE){
//                Intent enabler = new Intent(this, ClientSocketActivity.class);
//                startActivity(enabler);
//            }
//        }
//    }

}
