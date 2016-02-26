package gatewaysample.kii.com.gateway_agent_androidsample.smart_light_demo;

import android.content.Context;

import com.kii.thingif.Owner;
import com.kii.thingif.ThingIFAPI;
import com.kii.thingif.ThingIFAPIBuilder;
import com.kii.thingif.schema.Schema;
import com.kii.thingif.schema.SchemaBuilder;

import gatewaysample.kii.com.gateway_agent_androidsample.utils.Config;

public class ApiBuilder {

    public static ThingIFAPI buildApi(Context context, Owner owner) {
        String appId = Config.APP_ID;
        String appKey = Config.APP_KEY;
        String ioTAppBaseUrl = Config.IOTAPPBASEURL;
        ThingIFAPIBuilder builder = ThingIFAPIBuilder.newBuilder(context, appId,
                appKey, ioTAppBaseUrl, owner);
        SchemaBuilder schemaBuilder = SchemaBuilder.newSchemaBuilder(Config.THING_TYPE,
                Config.SCHEMA_NAME, Config.SCHEMA_VERSION, LightState.class);
        schemaBuilder.addActionClass(TurnPower.class, TurnPowerResult.class);
        schemaBuilder.addActionClass(SetBrightness.class, SetBrightnessResult.class);
        schemaBuilder.addActionClass(SetColor.class, SetColorResult.class);
        schemaBuilder.addActionClass(SetColorTemperature.class, SetColorTemperatureResult.class);
        builder.addSchema(schemaBuilder.build());
        return builder.build();
    }


}
