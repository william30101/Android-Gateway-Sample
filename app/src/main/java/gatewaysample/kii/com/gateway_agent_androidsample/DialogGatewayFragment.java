package gatewaysample.kii.com.gateway_agent_androidsample;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.kii.cloud.storage.KiiThing;
import com.kii.thingif.ThingIFAPI;

import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;
import org.w3c.dom.Text;

import gatewaysample.kii.com.gateway_agent_androidsample.promise_api_wrapper.KiiCloudPromiseAPIWrapper;

/**
 * Created by william.wu on 2/4/16.
 */
public class DialogGatewayFragment extends DialogFragment {

    private TextView txtThingId;
    private TextView txtVenderThingId;
    private TextView txtThingType;
    private TextView txtVender;
    private TextView txtProductName;
    private TextView txtFirmwareVersion;
    private TextView txtLot;
    private TextView txtLayoutPosition;
    protected Button checkBtn;
    private ThingIFAPI api;

    public DialogGatewayFragment(ThingIFAPI gatewayApi) {
        this.api = gatewayApi;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.dialog_gateway_info, container);

        checkBtn = (Button) view.findViewById(R.id.checkBtn);
        checkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        txtThingId = (TextView) view.findViewById(R.id.textThingId);
        txtVenderThingId = (TextView) view.findViewById(R.id.textVenderThingId);
        txtThingType = (TextView) view.findViewById(R.id.textThingType);
        txtVender = (TextView) view.findViewById(R.id.textVender);
        txtProductName = (TextView) view.findViewById(R.id.textProductName);
        txtFirmwareVersion = (TextView) view.findViewById(R.id.textFirmwareVersion);
        txtLot = (TextView) view.findViewById(R.id.textLot);
        txtLayoutPosition = (TextView) view.findViewById(R.id.textLayoutPosition);


        KiiCloudPromiseAPIWrapper wp = new KiiCloudPromiseAPIWrapper(this.api);
        wp.loadWithThingID(api.getTarget().getTypedID().getID()).then(new DoneCallback<KiiThing>() {
            @Override
            public void onDone(KiiThing thing) {



                txtThingId.setText(thing.getID());
                if (thing.getVendorThingID() != null) {
                    txtVenderThingId.setText(thing.getVendorThingID());
                } else {
                    txtVenderThingId.setText("---");
                }
                if (thing.getThingType() != null) {
                    txtThingType.setText(thing.getThingType());
                } else {
                    txtThingType.setText("---");
                }
                if (thing.getVendor() != null) {
                    txtVender.setText(thing.getVendor());
                } else {
                    txtVender.setText("---");
                }
                if (thing.getProductName() != null) {
                    txtProductName.setText(thing.getProductName());
                } else {
                    txtProductName.setText("---");
                }
                if (thing.getFirmwareVersion() != null) {
                    txtFirmwareVersion.setText(thing.getFirmwareVersion());
                } else {
                    txtFirmwareVersion.setText("---");
                }
                if (thing.getLot() != null) {
                    txtLot.setText(thing.getLot());
                } else {
                    txtLot.setText("---");
                }
                if (thing.getObject("_layoutPosition") != null) {
                    txtLayoutPosition.setText(thing.getObject("_layoutPosition").toString());
                } else {
                    txtLayoutPosition.setText("---");
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
}
