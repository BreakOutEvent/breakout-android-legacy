package org.break_out.breakout.ui.activities;


import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.SwitchPreference;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.MenuItem;

import org.break_out.breakout.R;


public class SettingsActivity extends AppCompatPreferenceActivity {
    private static final String TAG = "SettingsActivity";
    EditTextPreference urlPref;
    EditTextPreference idPref;
    EditTextPreference secretPref;
    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();
            return true;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
        addPreferencesFromResource(R.xml.pref_general);
        SwitchPreference testPref = (SwitchPreference) findPreference(getString(R.string.PREFERENCE_IS_TEST));
        urlPref = (EditTextPreference) findPreference(getString(R.string.PREFERENCE_URL));
        idPref = (EditTextPreference) findPreference(getString(R.string.PREFERENCE_CLIENTID));
        secretPref = (EditTextPreference) findPreference(getString(R.string.PREFERENCE_CLIENTSECRET));

        boolean testCase = getSharedPreferences(getString(R.string.PREFERENCES_GLOBAL),MODE_PRIVATE).getBoolean(getString(R.string.PREFERENCE_IS_TEST),false);
        setOptions(testCase);

        testPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Log.d(TAG,"onPrefChange called");
                Boolean val = (Boolean) newValue;
                getSharedPreferences(getString(R.string.PREFERENCES_GLOBAL),MODE_PRIVATE).edit().putBoolean(getString(R.string.PREFERENCE_IS_TEST),val).apply();
                setOptions(val);
                return true;
            }
        });
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setOptions(boolean active){
        urlPref.setEnabled(active);
        idPref.setEnabled(active);
        secretPref.setEnabled(active);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (!super.onMenuItemSelected(featureId, item)) {
                NavUtils.navigateUpFromSameTask(this);
            }
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

}
