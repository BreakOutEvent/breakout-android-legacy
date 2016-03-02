package org.break_out.breakout;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.orm.SugarContext;

import org.break_out.breakout.manager.BOLocationManager;

import java.util.ArrayList;

//JUST FOR TEST PURPOSES
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private Button _toggleLocatingButton;
    private static ListView _listView_savedLocations;
    private static BOLocationListAdapter _adapter;
    private static ArrayList<BOLocation> _locationArrayList;
    private static SharedPreferences _preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SugarContext.init(this);
        _preferences = getSharedPreferences(getString(R.string.PREFERENCES_GLOBAL),MODE_PRIVATE);

        _listView_savedLocations = (ListView) findViewById(R.id.main_listView_locations);
        _toggleLocatingButton = (Button) findViewById(R.id.main_button_start);

        _toggleLocatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isCurrentlyLocating()) {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION}
                            ,1);
                    setCurrentlyLocating(true);

                } else {
                    final BOLocationManager manager = BOLocationManager.getInstance(MainActivity.this);
                    manager.stopUpdateLocationPeriodically(MainActivity.this);
                    Toast.makeText(MainActivity.this,"Stop obtaining",Toast.LENGTH_SHORT).show();
                    setCurrentlyLocating(false);
                }
            }
        });
        _locationArrayList = new ArrayList<>();

        _adapter = new BOLocationListAdapter(this,R.id.main_listView_locations, _locationArrayList);
        updateLocationList();
        _listView_savedLocations.setAdapter(_adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * called when Location request is granted. will be later replaced by methods from BOActivity
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 1) {
            Toast.makeText(MainActivity.this,"Start obtaining",Toast.LENGTH_SHORT).show();
            final BOLocationManager manager = BOLocationManager.getInstance(this);
            manager.startUpdateLocationPeriodically(this);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onDestroy() {
        SugarContext.terminate();
        super.onDestroy();
    }

    /**
     * receives broadcast when new elements are entered into the database
     */
    public static final class LocationListUpdateBroadcastReceiver extends BroadcastReceiver {

        public LocationListUpdateBroadcastReceiver() {

        }

        @Override
        public void onReceive(Context context, Intent intent) {
            updateLocationList();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_main_clear) {
            clearList();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     *
     * @return true if the app is currently locating, false otherwise
     */
    private boolean isCurrentlyLocating() {
        return _preferences.getBoolean(getString(R.string.PREFERENCE_CURRENTLY_LOCATING),false);
    }

    /**
     * set if the app is currently updating
     * @param isCurrentlyLocating
     */
    private void setCurrentlyLocating(boolean isCurrentlyLocating) {
        _preferences.edit().putBoolean(getString(R.string.PREFERENCE_CURRENTLY_LOCATING),isCurrentlyLocating).apply();
    }

    /**
     * Clears the location database
     */
    private static void clearList() {
        BOLocation.deleteAll(BOLocation.class);
        updateLocationList();
    }

    /**
     * Updates the visible representation of the Location database
     */
    private static void updateLocationList() {
        if(_locationArrayList != null) {
            _locationArrayList.clear();
            _locationArrayList.addAll(BOLocation.listAll(BOLocation.class));
            _adapter.notifyDataSetChanged();
            _listView_savedLocations.smoothScrollToPosition(_locationArrayList.size()-1);
        }

    }
}
