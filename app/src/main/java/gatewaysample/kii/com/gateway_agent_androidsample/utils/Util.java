package gatewaysample.kii.com.gateway_agent_androidsample.utils;


import com.kii.thingif.internal.utils.Log;
import com.kii.thingif.schema.Schema;
import com.kii.thingif.schema.SchemaBuilder;

import java.util.Set;

import gatewaysample.kii.com.gateway_agent_androidsample.smart_light_demo.LightState;
import gatewaysample.kii.com.gateway_agent_androidsample.smart_light_demo.SetBrightness;
import gatewaysample.kii.com.gateway_agent_androidsample.smart_light_demo.SetBrightnessResult;
import gatewaysample.kii.com.gateway_agent_androidsample.smart_light_demo.SetColor;
import gatewaysample.kii.com.gateway_agent_androidsample.smart_light_demo.SetColorResult;
import gatewaysample.kii.com.gateway_agent_androidsample.smart_light_demo.SetColorTemperature;
import gatewaysample.kii.com.gateway_agent_androidsample.smart_light_demo.SetColorTemperatureResult;
import gatewaysample.kii.com.gateway_agent_androidsample.smart_light_demo.TurnPower;
import gatewaysample.kii.com.gateway_agent_androidsample.smart_light_demo.TurnPowerResult;

public class Util {

    public static String getTID(String msg) {
        Thread t = Thread.currentThread();
        long l = t.getId();
        String name = t.getName();

        return msg + " ID : " + l;
    }


    public static Thread[] getAllThreads( ) {
        Set<Thread> threadSet = Thread.getAllStackTraces().keySet();

        Thread[] threadArray = threadSet.toArray(new Thread[threadSet.size()]);

        return threadArray;
    }

    public static Schema buildSchema() {
        SchemaBuilder schemaBuilder = SchemaBuilder.newSchemaBuilder(Config.THING_TYPE,
                Config.SCHEMA_NAME, Config.SCHEMA_VERSION, LightState.class);
        schemaBuilder.addActionClass(TurnPower.class, TurnPowerResult.class);
        schemaBuilder.addActionClass(SetBrightness.class, SetBrightnessResult.class);
        schemaBuilder.addActionClass(SetColor.class, SetColorResult.class);
        schemaBuilder.addActionClass(SetColorTemperature.class, SetColorTemperatureResult.class);

        return schemaBuilder.build();
    }
}
