package edu.unina.natour21.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.location.Location;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.LinkedList;

import edu.unina.natour21.R;
import edu.unina.natour21.utility.NatourResourceHandler;
import edu.unina.natour21.utility.NatourUIDesignHandler;

/**
 * An activity that displays a Google map with a marker (pin) to indicate a particular location.
 */
public class PostCreationMapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = PostCreationMapsActivity.class.getSimpleName();
    private GoogleMap map;
    private CameraPosition cameraPosition;

    private LinkedList<Marker> markerLinkedList;
    private LinkedList<LatLng> pointLinkedList;
    private Polyline routePolyline;
    
    // The entry point to the Places API.
    private PlacesClient placesClient;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient fusedLocationProviderClient;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng defaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean locationPermissionGranted;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location lastKnownLocation;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    private Button doneButton;
    private Button clearButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_post_creation_maps);

        Places.initialize(getApplicationContext(), getResources().getString(R.string.google_maps_key));
        placesClient = Places.createClient(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Get the SupportMapFragment and request notification when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        doneButton = (Button) findViewById(R.id.postCreationMapsDoneButton);
        clearButton = (Button) findViewById(R.id.postCreationMapsClearButton);

        NatourUIDesignHandler designHandler = new NatourUIDesignHandler();

        designHandler.setTextGradient(doneButton);
        designHandler.setTextGradient(clearButton);

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<LatLng> pointArrayList = new ArrayList<LatLng>(pointLinkedList);
                Intent data = getIntent().putParcelableArrayListExtra("pointArrayList", pointArrayList);
                setResult(Activity.RESULT_OK, data);
                finish();
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                markerLinkedList = new LinkedList<Marker>();
                pointLinkedList = new LinkedList<LatLng>();
                map.clear();
                redrawPolyline();
            }
        });

    }

    /**
     * Manipulates the map when it's available.
     * The API invokes this callback when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user receives a prompt to install
     * Play services inside the SupportMapFragment. The API invokes this method after the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.
        this.map = googleMap;
        pointLinkedList = new LinkedList<LatLng>();
        markerLinkedList = new LinkedList<Marker>();

        Intent data = getIntent();
        if(data.getParcelableArrayListExtra("pointArrayList") != null) {
            pointLinkedList = new LinkedList<LatLng>(data.getParcelableArrayListExtra("pointArrayList"));
        }


        if(pointLinkedList != null && !pointLinkedList.isEmpty()) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            int hue;

            for(LatLng point : pointLinkedList) {
                hue = 147;
                if(markerLinkedList.size() == 0) hue = 188;
                Marker tmpMarker = map.addMarker(new MarkerOptions().position(point).icon(BitmapDescriptorFactory.defaultMarker(hue)));
                if(markerLinkedList.size() == 0) {
                    tmpMarker.setTitle("Start");
                    tmpMarker.showInfoWindow();
                }
                markerLinkedList.add(tmpMarker);
                redrawPolyline();
                builder.include(point);
            }

            int padding = 150;
            LatLngBounds bounds = builder.build();
            final CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);

            googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    googleMap.animateCamera(cameraUpdate);
                }
            });
        }

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                int hue = 147;
                if(markerLinkedList.size() == 0 && pointLinkedList.size() == 0) hue = 188;
                Marker tmpMarker = map.addMarker(new MarkerOptions().position(point).icon(BitmapDescriptorFactory.defaultMarker(hue)));
                if(markerLinkedList.size() == 0 && pointLinkedList.size() == 0) {
                    tmpMarker.setTitle("Start");
                    tmpMarker.showInfoWindow();
                }
                markerLinkedList.add(tmpMarker);
                pointLinkedList.add(tmpMarker.getPosition());
                redrawPolyline();
            }
        });

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.remove();
                for(int i = 0; i < pointLinkedList.size(); i++) {
                    if(pointLinkedList.get(i).equals(marker.getPosition())) {
                        markerLinkedList.remove(i);
                        pointLinkedList.remove(i);
                    }
                    if(i == 0 && pointLinkedList.size() != 0 && markerLinkedList.size() != 0) {
                        Marker tmpMarker = map.addMarker(new MarkerOptions().position(markerLinkedList.getFirst().getPosition()).icon(BitmapDescriptorFactory.defaultMarker(188)));
                        markerLinkedList.set(0, tmpMarker).remove();
                        if(tmpMarker != null) tmpMarker.setTitle("Start");
                    }
                }
                redrawPolyline();
                return true;
            }
        });


        /*
        LatLng sydney = new LatLng(-33.852, 151.211);
        googleMap.addMarker(new MarkerOptions()
                .position(sydney)
                .title("Marker in Sydney"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
         */

        getLocationPermission();

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();
    }

    @Override
    public void onBackPressed() {
        ArrayList<LatLng> pointArrayList = new ArrayList<LatLng>();
        Intent data = getIntent().putParcelableArrayListExtra("pointArrayList", pointArrayList);
        setResult(Activity.RESULT_OK, data);
        super.onBackPressed();
    }

    private void redrawPolyline() {
        if(routePolyline != null) routePolyline.remove();
        PolylineOptions polylineOptions = new PolylineOptions();

        for(LatLng point: pointLinkedList) {
            polylineOptions.add(point);
        }

        routePolyline = map.addPolyline(polylineOptions);
        routePolyline.setColor(getResources().getColor(R.color.bluegreen_nav_icon));
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        locationPermissionGranted = false;
        if (requestCode
                == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationPermissionGranted = true;
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        updateLocationUI();
    }

    private void updateLocationUI() {
        if (map == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                map.setMyLocationEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(lastKnownLocation.getLatitude(),
                                                lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            map.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                            map.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }
}