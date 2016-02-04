package gatewaysample.kii.com.gateway_agent_androidsample.smart_light_demo;

import com.kii.thingif.command.Action;

public class SetBrightness extends Action {
    public int brightness;
    public SetBrightness() {
    }
    public SetBrightness(int brightness) {
        this.brightness = brightness;
    }
    @Override
    public String getActionName() {
        return "setBrightness";
    }
}
