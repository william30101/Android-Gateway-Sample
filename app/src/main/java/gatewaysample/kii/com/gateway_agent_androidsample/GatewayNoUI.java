package gatewaysample.kii.com.gateway_agent_androidsample;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import org.restlet.Component;
import org.restlet.data.Protocol;

import gatewaysample.kii.com.gateway_agent_androidsample.rest_service.BookServiceRestletApplication;

public class GatewayNoUI extends AppCompatActivity {

    private final String TAG = "GatewayNoUI";

    GatewayService gatewayService;
    private Toolbar toolbar;
    Component component;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gateway_service);
        initToolBar();
        GatewayServiceStart();

    }

    private void initToolBar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Gateway Service");

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void GatewayServiceStart() {
        Intent serviceIntent = new Intent(this, GatewayService.class);
        startService(serviceIntent); //Starting the service
        bindService(serviceIntent, mConnectionGatewayService, Context.BIND_AUTO_CREATE); //Binding to the service!

    }


    private ServiceConnection mConnectionGatewayService = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            Toast.makeText(GatewayNoUI.this, "onServiceConnected called", Toast.LENGTH_SHORT).show();
            // We've binded to LocalService, cast the IBinder and get LocalService instance
            GatewayService.LocalBinder binder = (GatewayService.LocalBinder) service;
            gatewayService = binder.getServiceInstance(); //Get instance of your service!
            //gatewayService.registerClient(ContactActivity.this); //Activity register in the service as client for callabcks!
            restServiceStart();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Toast.makeText(GatewayNoUI.this, "onServiceDisconnected called", Toast.LENGTH_SHORT).show();

        }
    };

    public GatewayService getGatewayService() {
        return gatewayService;
    }

    private void restServiceStart() {
        try {
            component = new Component();
            component.getServers().add(Protocol.HTTP, 8080);
            component.getDefaultHost().attach("/",
                    new BookServiceRestletApplication(gatewayService));

            component.start();

        } catch (Exception e) {
            Log.i(TAG, "!!! Could not start restlet based server");
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
          Intent serviceIntent = new Intent(GatewayNoUI.this, GatewayService.class);
        stopService(serviceIntent);

        unbindService(mConnectionGatewayService);

        if (component != null){
            if (!component.isStopped()){
                try {
                    component.stop();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
