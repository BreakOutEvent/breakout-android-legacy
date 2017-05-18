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
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.break_out.breakout.R;
import org.break_out.breakout.manager.BOLocationManager;
import org.break_out.breakout.manager.TeamManager;
import org.break_out.breakout.manager.UserManager;
import org.break_out.breakout.model.BOLocation;
import org.break_out.breakout.model.Team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final String TAG = "MapsActivity";
    private long currentUserId = -1;
    private GoogleMap mMap;
    private boolean mapReady = false;
    private FloatingActionButton _floatingActionButton;
    private ArrayList<BOLocation> _savedLocations;

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
        if (manager.locationServicesAvailable()) {
            if (UserManager.getInstance(getApplicationContext()).getCurrentUser().getRemoteId() != -1) {
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
                Toast.makeText(getApplicationContext(), "Du musst eingeloggt sein", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.toast_enable_location), Toast.LENGTH_LONG).show();
        }
    }

    private void setMarker(BOLocation location) {
        if (mapReady) {
            LatLng newLoc = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.addMarker(new MarkerOptions().position(newLoc));
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(newLoc, 7);
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
        final ArrayList<Team> teams = TeamManager.getInstance().getAllTeams();

        if ((_savedLocations = BOLocationManager.getAllSavedLocations()).isEmpty()) {
            BOLocationManager.getAllLocationsFromServer(this, new BOLocationManager.BOLocationListObtainedListener() {
                @Override
                public void onListObtained() {
                    //TODO
                    // Log.d(TAG, "Map updated! " + BOLocation.listAll(BOLocation.class).size());

                    if (teams != null) {
                        for (Team t : teams) {
                            Log.d(TAG, "team " + t.getRemoteId());
                            ArrayList<BOLocation> currentUserLocationList = BOLocationManager.getAllLocationsFromTeam(t.getRemoteId());
                            if (currentUserLocationList.size() != 0) {
                                addToMap(currentUserLocationList);

                            }
                        }
                    }
                }
            });
        } else {
            if (teams != null) {
                for (Team t : teams) {
                    ArrayList<BOLocation> currentUserLocationList = BOLocationManager.getAllLocationsFromTeam(t.getRemoteId());
                    if (currentUserLocationList.size() != 0) {
                        addToMap(currentUserLocationList);
                    }
                }
            }
        }

        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds(new LatLng(41.3873996, 2.1606497), new LatLng(52.5076291, 13.1459824)), 400));
    }

    private void addToMap(ArrayList<BOLocation> locationList) {
        Log.d(TAG, "add to map");

        Collections.sort(locationList, new Comparator<BOLocation>() {
            @Override
            public int compare(BOLocation o1, BOLocation o2) {
                return o1.getTimestamp().compareTo(o2.getTimestamp());
            }
        });

        ArrayList<LatLng> latLngs = new ArrayList<>();

        for (BOLocation location : locationList) {
            latLngs.add(new LatLng(location.getLatitude(), location.getLongitude()));
        }

        if (locationList.size() > 0 && latLngs.size() > 0) {

            BOLocation firstLocation = locationList.get(0);
            LatLng lastLatLng = latLngs.get(latLngs.size() - 1);


            mMap.addMarker((new MarkerOptions().position(lastLatLng).title(firstLocation.getTeamName())));

            int color = getResources().getColor(R.color.line_otherTeam);
            if (firstLocation.getTeamId() == currentUserId) {
                color = getResources().getColor(R.color.line_ownTeam);
            }

            mMap.addPolyline(new PolylineOptions()
                    .addAll(latLngs)
                    .width(4)
                    .color(color));


        }
    }

}
