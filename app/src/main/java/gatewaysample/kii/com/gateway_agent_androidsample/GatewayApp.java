package gatewaysample.kii.com.gateway_agent_androidsample;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class GatewayApp extends AppCompatActivity implements AdapterView.OnItemClickListener{

    private final String TAG= "GatewayApp";
    ListView listView;
    private ArrayAdapter<String> listAdapter;
    private String[] list = {"register","login"};
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.gateway_app);
        initToolBar();
        initUI();
    }

    private void initUI(){
        listView = (ListView) findViewById(R.id.gateway_app_listview);
        listAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(listAdapter);


    }

    private void initToolBar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Gateway_App");

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String name = (String) parent.getItemAtPosition(position);
        Log.i(TAG, "click name : " + name);
    }
}
