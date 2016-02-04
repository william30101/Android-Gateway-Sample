package gatewaysample.kii.com.gateway_agent_androidsample.smart_light_demo;

import com.kii.thingif.command.ActionResult;

public class SetColorResult extends ActionResult {
    @Override
    public String getActionName() {
        return "setColor";
    }
}
