package gatewaysample.kii.com.gateway_agent_androidsample.utils;

import com.kii.thingif.Owner;
import com.kii.thingif.command.Action;
import com.kii.thingif.schema.Schema;

import java.util.List;

public class ControllCmd {
     String thingID;
     String schemaName;
     int schemaVersion;
     List<Action> actions;
     Owner owner;
     Schema schema;

    public ControllCmd(String thingID, String schemaName, int schemaVersion, List<Action> actions, Owner owner, Schema schema) {
        this.thingID = thingID;
        this.schemaName = schemaName;
        this.schemaVersion = schemaVersion;
        this.actions = actions;
        this.owner = owner;
        this.schema = schema;
    }

    public String getThingID() {
        return thingID;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public int getSchemaVersion() {
        return schemaVersion;
    }

    public List<Action> getActions() {
        return actions;
    }

    public Owner getOwner() {
        return owner;
    }

    public Schema getSchema() {
        return schema;
    }
}
