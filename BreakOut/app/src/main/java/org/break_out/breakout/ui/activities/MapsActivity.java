package org.break_out.breakout.ui.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.break_out.breakout.model.BOLocation;
import org.break_out.breakout.R;
import org.break_out.breakout.manager.BOLocationManager;
import org.break_out.breakout.manager.TeamManager;
import org.break_out.breakout.manager.UserManager;
import org.break_out.breakout.model.Team;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final String TAG = "MapsActivity";
    private final int OFFSET = 3;
    private long currentUserId = -1;
    private GoogleMap mMap;
    private boolean mapReady = false;
    private FloatingActionButton _floatingActionButton;
    private ArrayList<BOLocation> savedLocations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mapReady = false;
        currentUserId = UserManager.getInstance(getApplicationContext()).getCurrentUser().getRemoteId();
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
                locate();
            }
        });

        _floatingActionButton.setVisibility(View.GONE);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void locate() {
        final BOLocationManager manager = BOLocationManager.getInstance(getApplicationContext());
        if(manager.locationServicesAvailable()) {
            if(UserManager.getInstance(getApplicationContext()).getCurrentUser().getRemoteId()!=-1) {
                manager.getLocation(getApplicationContext(), new BOLocationManager.BOLocationRequestListener() {
                    @Override
                    public void onLocationObtained(BOLocation currentLocation) {
                        final BOLocation obtainedLocation = currentLocation;
                        //TODO
                        // obtainedLocation.save();
                        obtainedLocation.setIsPosted(false);
                        setMarker(currentLocation);
                        manager.postUnUploadedLocationsToServer();
                    }
                });
            } else {
                Toast.makeText(getApplicationContext(),"Du musst eingeloggt sein",Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getApplicationContext(),"Bitte aktiviere Ortungsdienste",Toast.LENGTH_LONG).show();
        }
    }

    private void setMarker(BOLocation location) {
        if(mapReady) {
            LatLng newLoc = new LatLng(location.getLatitude(),location.getLongitude());
            mMap.addMarker(new MarkerOptions().position(newLoc));
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(newLoc,7);
            mMap.moveCamera(cameraUpdate);
        }
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
        mapReady = true;
        mMap = googleMap;
        _floatingActionButton.setVisibility(View.VISIBLE);
        if((savedLocations = BOLocationManager.getAllSavedLocations()).isEmpty()) {
            BOLocationManager.getAllLocationsFromServer(this, new BOLocationManager.BOLocationListObtainedListener() {
                @Override
                public void onListObtained() {
                    //TODO
                    // Log.d(TAG, "Map updated! " + BOLocation.listAll(BOLocation.class).size());

                    for (Team t : TeamManager.getInstance().getAllTeams()) {
                        Log.d(TAG, "team " + t.getRemoteId());
                        ArrayList<BOLocation> currentUserLocationList = BOLocationManager.getInstance(getApplicationContext()).getAllLocationsFromTeam(t.getRemoteId());
                        if (currentUserLocationList.size() != 0) {
                            addToMap(currentUserLocationList);
                        }
                    }
                }
            });
        } else {
            for (Team t : TeamManager.getInstance().getAllTeams()) {
                ArrayList<BOLocation> currentUserLocationList = BOLocationManager.getInstance(getApplicationContext()).getAllLocationsFromTeam(t.getRemoteId());
                if (currentUserLocationList.size() != 0) {
                    addToMap(currentUserLocationList);
                }
            }
        }
    }

    private void addToMap(ArrayList<BOLocation> locationList) {
        Log.d(TAG,"add to map");
        int i;
        for(i = 0; i<locationList.size() && (i+OFFSET)<=locationList.size()-1; i+=OFFSET) {

            BOLocation curLoc = locationList.get(i);
            BOLocation nextLoc = locationList.get(i+1);
            LatLng l1 = new LatLng(curLoc.getLatitude(), curLoc.getLongitude());
            LatLng l2 = new LatLng(nextLoc.getLatitude(), nextLoc.getLongitude());
            if(i == 0) {
                mMap.addMarker(new MarkerOptions().position(l1).title(curLoc.getTeamName()));
            }
            if((i+1) == locationList.size()-1){
                mMap.addMarker((new MarkerOptions().position(l2).title(nextLoc.getTeamName())));
            }
            int color = getResources().getColor(R.color.line_otherTeam);
            if(nextLoc.getTeamId() == currentUserId) {
                color = getResources().getColor(R.color.line_ownTeam);
            }
            if(curLoc.getTeamId() == nextLoc.getTeamId()) {
                Polyline line = mMap.addPolyline(new PolylineOptions()
                        .add(l1,l2)
                        .width(16)
                        .color(color));
            }
        }

        if(i < locationList.size()){
        }
    }
}
