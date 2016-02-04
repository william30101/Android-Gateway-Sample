package gatewaysample.kii.com.gateway_agent_androidsample.smart_light_demo;

import com.kii.thingif.command.Action;

public class SetColorTemperature extends Action {
    public int colorTemperature;
    public SetColorTemperature() {
    }
    public SetColorTemperature(int colorTemperature) {
        this.colorTemperature = colorTemperature;
    }
    @Override
    public String getActionName() {
        return "setColorTemperature";
    }
}
