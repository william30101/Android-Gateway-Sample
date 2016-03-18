package gatewaysample.kii.com.gateway_agent_androidsample;

import android.os.Bundle;

import gatewaysample.kii.com.gateway_agent_androidsample.utils.PreferenceFragment;


public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.setting_layout);
    }
}
