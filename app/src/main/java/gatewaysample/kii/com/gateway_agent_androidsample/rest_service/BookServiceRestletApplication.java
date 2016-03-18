package gatewaysample.kii.com.gateway_agent_androidsample.rest_service;



import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;
import org.restlet.routing.Template;

import gatewaysample.kii.com.gateway_agent_androidsample.GatewayService;
import gatewaysample.kii.com.gateway_agent_androidsample.utils.Config;

public class BookServiceRestletApplication extends Application {

    private GatewayService mGatewayService;

    public BookServiceRestletApplication(GatewayService gatewayService){
        mGatewayService = gatewayService;
    }

    @Override
    public synchronized Restlet createInboundRoot() {
        Router router = new Router(getContext());



        // Create the account handler
//        Restlet account = new Restlet() {
//            @Override
//            public void handle(Request request, Response response) {
//                // Print the requested URI path
//                String message = "Account of user \""
//                        + request.getAttributes().get("user") + "\"";
//                response.setEntity(message, MediaType.TEXT_PLAIN);
//            }
//        };

//        router.attach("/users/{user}", account);

        //router.attach("{method}/", BookService.class);
        //router.getContext().getAttributes().put("threadCall", mThreadCall);
        router.getContext().getAttributes().put("mGatewayService", mGatewayService);
        router.attach("token", TokenRest.class).setMatchingMode(Template.MODE_STARTS_WITH);
        router.attach("gateway-app/gateway/{method}", GatewayAppRest.class);
        router.attach("apps/" + Config.APP_ID + "/gateway/{method1}", AppsRest.class);
        router.attach("apps/" + Config.APP_ID + "/gateway/{method1}/{method2}", AppsRest.class);
        router.attach("apps/" + Config.APP_ID + "/gateway/{method1}/{method2}/{method3}", AppsRest.class);
        router.attach("gateway-info", GatewayInfoRest.class).setMatchingMode(Template.MODE_STARTS_WITH);

        return router;
    }
}
