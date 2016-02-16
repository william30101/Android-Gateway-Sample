package gatewaysample.kii.com.gateway_agent_androidsample;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kii.cloud.storage.KiiThing;
import com.kii.thingif.Target;
import com.kii.thingif.ThingIFAPI;
import com.kii.thingif.command.Command;
import com.kii.thingif.gateway.EndNode;

import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;

import java.util.ArrayList;
import java.util.List;

import gatewaysample.kii.com.gateway_agent_androidsample.promise_api_wrapper.IoTCloudPromiseAPIWrapper;
import gatewaysample.kii.com.gateway_agent_androidsample.promise_api_wrapper.KiiCloudPromiseAPIWrapper;


public class DialogListEndNodeFragment extends DialogFragment implements AdapterView.OnItemClickListener{

    private final String TAG = "DialogListEndNodeFragment";

    private ThingIFAPI api;
    private EndNodeArrayAdapter adapter;
    private ListView listEndNodes;

    public DialogListEndNodeFragment(ThingIFAPI gatewayApi) {
        this.api = gatewayApi;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.dialog_list_endnode, container);
        this.listEndNodes = (ListView) view.findViewById(R.id.end_node_list_view);
        this.adapter = new EndNodeArrayAdapter(getActivity());
        this.listEndNodes.setAdapter(this.adapter);
        this.listEndNodes.setOnItemClickListener(this);

        KiiCloudPromiseAPIWrapper wp = new KiiCloudPromiseAPIWrapper(this.api);
        wp.loadWithThingID(api.getTarget().getTypedID().getID()).then(new DoneCallback<KiiThing>() {
            @Override
            public void onDone(KiiThing thing) {

                if (thing.getID() != null) {
                    String gatewayThingID = thing.getID();
                    IoTCloudPromiseAPIWrapper wp = new IoTCloudPromiseAPIWrapper(api);

                    wp.listEndNodes(gatewayThingID, 10, null).then(new DoneCallback<List<EndNode>>() {
                        @Override
                        public void onDone(List<EndNode> endNodes) {
                            adapter.clear();
                            adapter.addAll(endNodes);
                            adapter.notifyDataSetChanged();
                        }
                    }, new FailCallback<Throwable>() {
                        @Override
                        public void onFail(Throwable result) {
                            Toast.makeText(getActivity(), "get list Fail!: " + result.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }


            }
        }, new FailCallback<Throwable>() {
            @Override
            public void onFail(Throwable result) {
                Toast.makeText(getActivity().getApplicationContext(), "Unable to get target thing!: " + result.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        EndNode endNode = (EndNode)this.listEndNodes.getItemAtPosition(position);
//        CommandDetailFragment dialog = CommandDetailFragment.newFragment(this.api, command, this, 0);
//        dialog.show(getFragmentManager(), "CommandDetail");
    }


    private class EndNodeArrayAdapter extends ArrayAdapter<EndNode> {
        private final LayoutInflater inflater;
        private EndNodeArrayAdapter(Context context) {
            super(context, R.layout.end_node_adapter_view);
            this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            EndNodeViewHolder holder = null;
            if (convertView == null) {
                convertView = this.inflater.inflate(R.layout.end_node_adapter_view, parent, false);
                holder = new EndNodeViewHolder();
                holder.thingIDText = (TextView)convertView.findViewById(R.id.thingIDText);
                holder.vendorThingIDText = (TextView)convertView.findViewById(R.id.vendorThingIDText);
                convertView.setTag(holder);
            } else {
                holder = (EndNodeViewHolder)convertView.getTag();
            }
            EndNode item = this.getItem(position);
            holder.thingIDText.setText(item.getThingID());
            holder.vendorThingIDText.setText(item.getVendorThingID());
            return convertView;
        }
    }

    public class EndNodeViewHolder{
        TextView thingIDText;
        TextView vendorThingIDText;
    }

}
