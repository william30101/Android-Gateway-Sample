package gatewaysample.kii.com.gateway_agent_androidsample.promise_api_wrapper;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;

import com.kii.thingif.Target;
import com.kii.thingif.ThingIFAPI;
import com.kii.thingif.command.Action;
import com.kii.thingif.command.Command;
import com.kii.thingif.gateway.EndNode;
import com.kii.thingif.trigger.Predicate;
import com.kii.thingif.trigger.Trigger;

import org.jdeferred.Promise;
import org.jdeferred.android.AndroidDeferredManager;
import org.jdeferred.android.DeferredAsyncTask;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class IoTCloudPromiseAPIWrapper {

    private AndroidDeferredManager adm;
    private ThingIFAPI api;

    public IoTCloudPromiseAPIWrapper(ThingIFAPI api) {
        this.adm = new AndroidDeferredManager();
        this.api = api;
    }

    public IoTCloudPromiseAPIWrapper(AndroidDeferredManager manager, ThingIFAPI api) {
        this.adm = manager;
        this.api = api;
    }

    public Promise<Target, Throwable, Void> onboard(final String thingID, final String thingPassword) {
        return adm.when(new DeferredAsyncTask<Void, Void, Target>() {
            @Override
            protected Target doInBackgroundSafe(Void... voids) throws Exception {
                return api.onboard(thingID, thingPassword);
            }
        });
    }
    public Promise<Target, Throwable, Void> onboardGateWay(final String venderThingID, final String thingPassword, final String thingType) {
        return adm.when(new DeferredAsyncTask<Void, Void, Target>() {
            @Override
            protected Target doInBackgroundSafe(Void... voids) throws Exception {
                return api.onboard(venderThingID, thingPassword, thingType, null, "GATEWAY");
            }
        });
    }
    public Promise<Target, Throwable, Void> onboardEndNode(final String endNodeVendorThingID, final String endNodeThingPassword, final String gatewayVendorThingID,
                                                           final String endNodeThingType, final String owner, final JSONObject endNodeThingProperties) {
        return adm.when(new DeferredAsyncTask<Void, Void, Target>() {
            @Override
            protected Target doInBackgroundSafe(Void... voids) throws Exception {
                return api.onboard(endNodeVendorThingID, endNodeThingPassword, gatewayVendorThingID, owner, endNodeThingProperties, endNodeThingType );
            }
        });
    }
    public Promise<Target, Throwable, Void> onboard(final String venderThingID, final String thingPassword, final String thingType) {
        return adm.when(new DeferredAsyncTask<Void, Void, Target>() {
            @Override
            protected Target doInBackgroundSafe(Void... voids) throws Exception {
                return api.onboard(venderThingID, thingPassword, thingType, null);
            }
        });
    }
    public Promise<List<EndNode>, Throwable, Void> listEndNodes(final String gatewayThingID, @Nullable final int limit, @Nullable final String nextPaginationKey) {
        return adm.when(new DeferredAsyncTask<Void, Void, List<EndNode>>() {
            @Override
            protected List<EndNode> doInBackgroundSafe(Void... voids) throws Exception {
                List<EndNode> endNodes = new ArrayList<>();
                String paginationKey = null;
                do {
                    Pair<List<EndNode>, String> result = api.listEndNodes(gatewayThingID, limit, nextPaginationKey);
                    endNodes.addAll(result.first);
                    paginationKey = result.second;
                } while (paginationKey != null);
                return endNodes;
            }
        });
    }

    public Promise<String, Throwable, Void> deleteGateway(
            final String gatewayThingID) {
        return adm.when(new DeferredAsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackgroundSafe(Void... voids) throws Exception {
                return api.deleteGateway(gatewayThingID);
            }
        });
    }

    public Promise<String, Throwable, Void> deleteEndNode(
            final String gatewayThingID, final String thingID) {
        return adm.when(new DeferredAsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackgroundSafe(Void... voids) throws Exception {
                return api.deleteEndNode(gatewayThingID, thingID);
            }
        });
    }

    public Promise<Target, Throwable, Void> replaceGateWay(final String gatewayThingID, final String vendorThingID,
                                                           final String thingPassword, final Target gatewayTarget) {
        return adm.when(new DeferredAsyncTask<Void, Void, Target>() {
            @Override
            protected Target doInBackgroundSafe(Void... voids) throws Exception {
                return api.replaceGateway(gatewayThingID, vendorThingID, thingPassword, gatewayTarget);
            }
        });
    }

    public Promise<Target, Throwable, Void> replaceEndNode(final String gatewayThingID, final String endNodeVendorThingID,
                                                           final String endNodePassword, final Target endNodeTarget) {
        return adm.when(new DeferredAsyncTask<Void, Void, Target>() {
            @Override
            protected Target doInBackgroundSafe(Void... voids) throws Exception {
                return api.replaceEndNode(gatewayThingID, endNodeVendorThingID, endNodePassword, endNodeTarget);
            }
        });
    }

    public Promise<List<Command>, Throwable, Void> listCommands() {
        return adm.when(new DeferredAsyncTask<Void, Void, List<Command>>() {
            @Override
            protected List<Command> doInBackgroundSafe(Void... voids) throws Exception {
                List<Command> commands = new ArrayList<Command>();
                String paginationKey = null;
                do {
                    Pair<List<Command>, String> result = api.listCommands(0, paginationKey);
                    commands.addAll(result.first);
                    paginationKey = result.second;
                } while (paginationKey != null);
                return commands;
            }
        });
    }
    public Promise<Command, Throwable, Void> postNewCommand(final String schemaName, final int schemaVersion, final List<Action> actions) {
        return adm.when(new DeferredAsyncTask<Void, Void, Command>() {
            @Override
            protected Command doInBackgroundSafe(Void... voids) throws Exception {
                return api.postNewCommand(schemaName, schemaVersion, actions);
            }
        });
    }

    public Promise<List<Trigger>, Throwable, Void> listTriggers() {
        return adm.when(new DeferredAsyncTask<Void, Void, List<Trigger>>() {
            @Override
            protected List<Trigger> doInBackgroundSafe(Void... voids) throws Exception {
                List<Trigger> triggers = new ArrayList<Trigger>();
                String paginationKey = null;
                do {
                    Pair<List<Trigger>, String> result = api.listTriggers(0, paginationKey);
                    triggers.addAll(result.first);
                    paginationKey = result.second;
                } while (paginationKey != null);
                return triggers;
            }
        });
    }

    public Promise<Trigger, Throwable, Void> postNewTrigger(
            final String schemaName,
            final int schemaVersion,
            final List<Action> actions,
            final Predicate predicate) {
        return adm.when(new DeferredAsyncTask<Void, Void, Trigger>() {
            @Override
            protected Trigger doInBackgroundSafe(Void... voids) throws Exception {
                return api.postNewTrigger(schemaName, schemaVersion, actions, predicate);
            }
        });
    }

    public Promise<Trigger, Throwable, Void> patchTrigger(
            final String triggerID,
            final String schemaName,
            final int schemaVersion,
            final List<Action> actions,
            final Predicate predicate) {
        return adm.when(new DeferredAsyncTask<Void, Void, Trigger>() {
            @Override
            protected Trigger doInBackgroundSafe(Void... voids) throws Exception {
                return api.patchTrigger(triggerID, schemaName, schemaVersion, actions, predicate);
            }
        });
    }

    public Promise<Trigger, Throwable, Void> enableTrigger(
            final String triggerID,
            final boolean enable) {
        return adm.when(new DeferredAsyncTask<Void, Void, Trigger>() {
            @Override
            protected Trigger doInBackgroundSafe(Void... voids) throws Exception {
                return api.enableTrigger(triggerID, enable);
            }
        });
    }

    public Promise<Trigger, Throwable, Void> deleteTrigger(
            final String triggerID) {
        return adm.when(new DeferredAsyncTask<Void, Void, Trigger>() {
            @Override
            protected Trigger doInBackgroundSafe(Void... voids) throws Exception {
                return api.deleteTrigger(triggerID);
            }
        });
    }
//
//    public Promise<LightState, Throwable, Void> getLightState() {
//        return adm.when(new DeferredAsyncTask<Void, Void, LightState>() {
//            @Override
//            protected LightState doInBackgroundSafe(Void... voids) throws Exception {
//                return api.getTargetState(LightState.class);
//            }
//        });
//    }
}
