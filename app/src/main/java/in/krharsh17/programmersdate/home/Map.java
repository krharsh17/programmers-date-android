package in.krharsh17.programmersdate.home;


import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Objects;

import in.krharsh17.programmersdate.Constants;
import in.krharsh17.programmersdate.R;
import in.krharsh17.programmersdate.home.managers.LocationManager;


public class Map extends Fragment implements OnMapReadyCallback, Constants {

    private GoogleMap map;
    private LocationCallback locationCallback;

    private boolean gpsEnabled = false;
    private MapView mapView;

    private FusedLocationProviderClient fusedLocationClient;
    private Location myLocation;
    private LatLng partnerLocation;

    private MarkerOptions myMarkerOption, partnerMarkerOption;
    private boolean myLocationShown = true;

    private int rotationCounter = 0;

    private MarkerOptions[] markerOptions;
    private Marker[] markers;
    private int numberOfMarkers = 0;

    private Marker myMarker, partnerMarker;

    private MainActivity mainActivity;

    public Map() {

    }

    Location getMyLocation() {
        return myLocation;
    }

    public LatLng getPartnerLocation() {
        return partnerLocation;
    }

    public void setPartnerLocation(LatLng partnerLocation) {
        this.partnerLocation = partnerLocation;
    }

    public void clearLandmarks() {
        for (int i = 0; i < numberOfMarkers; i++) {
            if (markers[i] != null)
                markers[i].remove();
            markers = new Marker[20];
            markerOptions = new MarkerOptions[20];
            numberOfMarkers = 0;
            refreshMarkers();
        }
    }

    public void setLandmarks(MarkerOptions[] markers) {
        this.markerOptions = markers;
        numberOfMarkers = 0;
        for (MarkerOptions markerOptions : markers) {
            if (markerOptions != null)
                numberOfMarkers++;
        }
        refreshMarkers();
    }

    public void addLandmark(LatLng location, int drawableRes) {
        if (isAdded()) {
            MarkerOptions markerOptions = new MarkerOptions();
            Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), drawableRes);
            Bitmap bitmap = Bitmap.createScaledBitmap(originalBitmap, originalBitmap.getWidth() / 2, originalBitmap.getHeight() / 2, false);
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
            markerOptions.position(location);
            markerOptions.anchor(0.5f, 0.5f);
            this.markerOptions[numberOfMarkers] = markerOptions;
            numberOfMarkers++;
            refreshMarkers();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setMyLocationButtonEnabled(false);
        try {
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            Objects.requireNonNull(getContext()), R.raw.style_json));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }
        map.setMyLocationEnabled(false);
        try {
            MapsInitializer.initialize(Objects.requireNonNull(this.getActivity()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        map.setLatLngBoundsForCameraTarget(NITP_BOUNDS);
        map.setMinZoomPreference(18f);
        map.getUiSettings().setCompassEnabled(false);
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.setIndoorEnabled(true);
        map.setBuildingsEnabled(true);

        animateMarkers();

        focusMap(mainBuilding);

        map.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {
                Log.i(TAG, "onCameraMoveStarted: " + i);
                if (i == REASON_GESTURE)
                    mainActivity.hideOverlays();
            }
        });

        map.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                mainActivity.showOverlays();
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();

        final LocationManager locationManager = new LocationManager(getContext());

        mainActivity = (MainActivity) getActivity();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(Objects.requireNonNull(getActivity()));

        fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                myLocation = location;
                if (location != null)
                    focusMap(location.getLatitude(), location.getLongitude());
            }
        });

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationAvailability(LocationAvailability locationAvailability) {
                super.onLocationAvailability(locationAvailability);
            }

            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                myLocation = locationResult.getLastLocation();
                if (myLocation != null)
                    focusMap(myLocation.getLatitude(), myLocation.getLongitude());
                showPresentLocations();
                locationManager.writeUserLocation(myLocation.getLatitude(), myLocation.getLongitude());
            }
        };
        enableGPS();
        markerOptions = new MarkerOptions[20];
        markers = new Marker[20];
    }

    void enableGPS() {
        if (!gpsEnabled) {
            fusedLocationClient.requestLocationUpdates(
                    new LocationRequest()
                            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                            .setInterval(2),
                    locationCallback,
                    Looper.getMainLooper());
            gpsEnabled = true;
        }
    }

    void disableGPS() {
        if (gpsEnabled) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
            gpsEnabled = false;
        }
    }

    private void focusMap(double latitude, double longitude) {
        if (map != null)
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), mapDefaultHeight));

    }

    private void focusMap(LatLng location) {
        if (map != null)
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(location, mapDefaultHeight));
    }

    private void showPresentLocations() {
        if (map != null) {
            if (myMarkerOption == null) {
                myMarkerOption = new MarkerOptions();
                Bitmap bitmap;
                bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.my_marker_icon), 36, 36, false);
                myMarkerOption.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
                myMarkerOption.position(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()));
                myMarkerOption.anchor(0.5f, 0.5f);
                myMarkerOption.flat(true);
            } else {
                myMarkerOption.position(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()));
            }

            if (partnerLocation != null) {
                if (partnerMarkerOption == null) {
                    partnerMarkerOption = new MarkerOptions();
                    Bitmap bitmap;
                    bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.partner_marker_icon), 36, 36, false);
                    partnerMarkerOption.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
                    partnerMarkerOption.position(partnerLocation);
                    partnerMarkerOption.anchor(0.5f, 0.5f);
                    partnerMarkerOption.flat(true);
                } else {
                    partnerMarkerOption.position(partnerLocation);
                }
            }
            showPlayerLocationMarkers();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map, container, false);
        mapView = v.findViewById(R.id.main_mapview);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        return v;

    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    private void refreshMarkers() {
        clearAllMarkers();
        showPlayerLocationMarkers();
        showMarkers(markerOptions);
    }

    private void clearAllMarkers() {
        if (map != null)
            map.clear();
    }

    private void animateMarkers() {
        if (map != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (myLocationShown && mainActivity != null) {
                        if (mainActivity.appRunning) {
                            mainActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    rotationCounter++;
                                    rotationCounter = rotationCounter % 90;
                                    if (myMarker != null)
                                        myMarker.remove();
                                    if (partnerMarker != null)
                                        partnerMarker.remove();
                                    if (myMarkerOption != null)
                                        myMarkerOption.rotation(rotationCounter * 4);
                                    if (partnerMarkerOption != null)
                                        partnerMarkerOption.rotation(rotationCounter * 4);
                                    showPlayerLocationMarkers();
                                }
                            });
                            try {
                                Thread.sleep(80);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }).start();
        }
    }

    private void showPlayerLocationMarkers() {
        if (map != null) {
            if (myMarker != null)
                myMarker.remove();
            if (partnerMarker != null)
                partnerMarker.remove();
            if (myMarkerOption != null)
                myMarker = map.addMarker(myMarkerOption);
            if (partnerMarkerOption != null)
                partnerMarker = map.addMarker(partnerMarkerOption);
        }
    }

    private void showMarkers(final MarkerOptions... markers) {
        for (int i = 0; i < markers.length; i++) {
            if (map != null) {
                if (markers[i] != null)
                    this.markers[i] = map.addMarker(markers[i]);
            } else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showMarkers(markers);
                    }
                }, 2000);
            }
        }
    }
}
