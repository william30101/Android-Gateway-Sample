package gatewaysample.kii.com.gateway_agent_androidsample;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.kii.cloud.storage.KiiUser;
import com.kii.cloud.storage.callback.KiiUserCallBack;

/**
 * Created by mac on 2/3/16.
 */
public class MainActivity extends Activity{

    private String TAG = "MainActivity";
    private KiiUser user;
    private Button getBtn;
    private TextView accountView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        getBtn = (Button) findViewById(R.id.getBn);
        accountView = (TextView) findViewById(R.id.accountView);

        getBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUser();
            }
        });
    }

    private void getUser(){
        // Get the currently logged in user.
        KiiUser user = KiiUser.getCurrentUser();
        accountView.setText(user.getUsername());

    }
}
