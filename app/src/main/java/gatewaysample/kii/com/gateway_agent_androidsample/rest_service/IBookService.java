package gatewaysample.kii.com.gateway_agent_androidsample.rest_service;


import org.json.JSONObject;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

public interface IBookService{
    @Get
    @Post
    String present(Representation entity);

}
