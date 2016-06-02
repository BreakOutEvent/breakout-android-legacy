package org.break_out.breakout.ui.activities;

import android.location.LocationManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.break_out.breakout.BOLocation;
import org.break_out.breakout.R;
import org.break_out.breakout.manager.BOLocationManager;
import org.break_out.breakout.manager.TeamManager;
import org.break_out.breakout.manager.UserManager;
import org.break_out.breakout.model.Team;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final String TAG = "MapsActivity";
    private GoogleMap mMap;
    private FloatingActionButton _floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.maps_toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setTitle(getString(R.string.title_activity_maps));

        _floatingActionButton = (FloatingActionButton) findViewById(R.id.map_fab);

        _floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Funktioniert noch nicht. Update kommt in Kürze",Toast.LENGTH_LONG).show();
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        /*mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/


        BOLocationManager.getAllLocationsFromServer(this,new BOLocationManager.BOLocationListObtainedListener() {
            @Override
            public void onListObtained() {
                Log.d(TAG,"Map updated! " + BOLocation.listAll(BOLocation.class).size());
                for(Team t : TeamManager.getInstance().getAllTeams()) {
                    Log.d(TAG,"team "+t.getRemoteId());
                    ArrayList<BOLocation> currentUserLocationList = BOLocationManager.getInstance(getApplicationContext()).getAllLocationsFromTeam(t.getRemoteId());
                    if(currentUserLocationList.size()!=0) {
                        LatLng firstLatLng = new LatLng(currentUserLocationList.get(0).getLatitude(),currentUserLocationList.get(0).getLongitude());
                        LatLng lastLatLng = new LatLng(currentUserLocationList.get(currentUserLocationList.size()-1).getLatitude(),currentUserLocationList.get(currentUserLocationList.size()-1).getLongitude());
                        mMap.addMarker(new MarkerOptions().position(firstLatLng).title(t.getTeamName()));
                        mMap.addMarker((new MarkerOptions().position(lastLatLng).title(t.getTeamName())));
                    }
                }
                Log.d(TAG,"teams : "+TeamManager.getInstance().getAllTeams().size());

            }
        });
    }
}
