package gatewaysample.kii.com.gateway_agent_androidsample.converter;


import android.bluetooth.BluetoothDevice;

import java.util.List;

interface CallBack {
        void discoveryDone(List<BluetoothDevice> devices);
}
