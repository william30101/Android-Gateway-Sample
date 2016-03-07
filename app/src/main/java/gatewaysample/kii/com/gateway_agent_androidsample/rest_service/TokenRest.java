package gatewaysample.kii.com.gateway_agent_androidsample.rest_service;


import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.Form;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import gatewaysample.kii.com.gateway_agent_androidsample.GatewayService;

public class TokenRest extends ServerResource implements IBookService {

    private final String TAG = "Bookservice";
    GatewayService mGatewayService;

    @Override
    @Post
    @Get
    public String present(Representation entity) {
        //getTID("rest ");
        //mThreadCall = (ThreadCall) getContext().getAttributes().get("threadCall");
        mGatewayService = (GatewayService) getContext().getAttributes().get("mGatewayService");
        Form form = new Form(entity);
        Set<String> names = form.getNames();
        List listOfNames = new ArrayList(names);
        //String bodyStr = form.get(0).toString();
        //String bodyStr = names.
//        String methodPost = form.getFirstValue("username");
//        Log.i(TAG,"method : " + methodPost);



        //String methodGet = (String) getRequestAttributes().get("method");
        JSONObject responseBody =  new JSONObject();
        //Log.i(TAG, "method : " + methodGet);


        if (mGatewayService != null){
            try {
                responseBody.put("accessToken",mGatewayService.getCrediental("william.wu","1qaz@WSX"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        return responseBody.toString();

    }

}