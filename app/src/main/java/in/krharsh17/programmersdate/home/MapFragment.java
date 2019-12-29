package in.krharsh17.programmersdate.home;


import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.List;
import java.util.Locale;

import in.krharsh17.programmersdate.Constants;
import in.krharsh17.programmersdate.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback, Constants {

    private final static double AVERAGE_RADIUS_OF_EARTH_KM = 6371;
    GoogleMap map;
    LocationCallback locationCallback;

    LatLng lastSelectedLocation;
    boolean gpsEnabled = false;
    private MapView mapView;
    private Location location;

    private FusedLocationProviderClient fusedLocationClient;
    private Location currentLocation;
    private double lastLocationTime;
    private boolean isMapClickEnabled;
    private int REQUEST_CHECK_SETTINGS = 400;
    private static final LatLngBounds NITP_BOUNDS = new LatLngBounds(new LatLng(25.619097, 85.170036),
            new LatLng(25.621264, 85.175347));

    public MapFragment() {
        // Required empty public constructor
    }

    public LatLng getLastSelectedLocation() {
        return lastSelectedLocation;
    }

    Location getCurrentLocation() {
        return currentLocation;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setMyLocationButtonEnabled(false);
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            getContext(), R.raw.style_json));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }
        map.setMyLocationEnabled(false);
        try {
            MapsInitializer.initialize(this.getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }

        map.setLatLngBoundsForCameraTarget(NITP_BOUNDS);
        map.setMinZoomPreference(18f);
        map.getUiSettings().setCompassEnabled(false);
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.setIndoorEnabled(true);
        map.setBuildingsEnabled(true);

        focusMap(mainBuilding);
    }

    @Override
    public void onStart() {
        super.onStart();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

//        fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
//            @Override
//            public void onSuccess(Location location) {
//                MapFragment.this.location = location;
//                currentLocation = location;
//                lastLocationTime = System.currentTimeMillis();
//                if (location != null)
//                    focusMap(location.getLatitude(), location.getLongitude());
//            }
//        });

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationAvailability(LocationAvailability locationAvailability) {
                super.onLocationAvailability(locationAvailability);
            }

            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                currentLocation = locationResult.getLastLocation();
                lastLocationTime = System.currentTimeMillis();
            }
        };


    }

    void enableGPS() {
        if (!gpsEnabled) {
            fusedLocationClient.requestLocationUpdates(
                    new LocationRequest()
                            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                            .setInterval(5),
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

    void focusMap(double latitude, double longitude) {
        if (map != null)
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), mapDefaultHeight));

    }

    void focusMap(LatLng location){
        if (map != null)
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(location, mapDefaultHeight));
    }

    void focusMapWithMarker(double latitude, double longitude) {
        if (map != null) {
//            Log.i(TAG, "focusMapWithMarker: ");
            lastSelectedLocation = new LatLng(latitude, longitude);
            map.clear();
            focusMap(latitude, longitude);
//            addMarker(latitude, longitude);
        }

    }

    void focusRoute(LatLng northeast, LatLng southwest) {
        LatLngBounds route = new LatLngBounds(
                southwest, northeast);

        if (map != null) {
            map.moveCamera(CameraUpdateFactory.newLatLngBounds(route, 50));
            map.animateCamera(CameraUpdateFactory.scrollBy(0, 200));
        }

    }

//    void addMarker(double latitude, double longitude) {
//        if (map != null) {
//            MarkerOptions markerOptions = new MarkerOptions();
//            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_map_pin), 60, 60, false)));
//            lastSelectedLocation = new LatLng(latitude, longitude);
//            markerOptions.position(lastSelectedLocation);
//            map.addMarker(markerOptions);
//        }
//    }

//    void addMarker(double latitude, double longitude, boolean isStart) {
//        if (map != null) {
//            MarkerOptions markerOptions = new MarkerOptions();
//            Bitmap bitmap;
//            if(isStart){
//                bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_start_pin), 60, 60, false);
//            } else {
//                bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_end_pin), 60, 60, false);
//            }
//            BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bitmap);
//            markerOptions.icon(bitmapDescriptor);
//            lastSelectedLocation = new LatLng(latitude, longitude);
//            markerOptions.position(lastSelectedLocation);
//            map.addMarker(markerOptions);
//        }
//    }


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

    void clearAllMarkers() {
        if (map != null)
            map.clear();
    }

    String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder();

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("My Current address", strReturnedAddress.toString());
            } else {
                Log.w("My Current address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("My Current address", "Canont get Address!");
        }
        return strAdd;
    }

    int calculateDistanceInMeter(double userLat, double userLng,
                                 double venueLat, double venueLng) {

        double latDistance = Math.toRadians(userLat - venueLat);
        double lngDistance = Math.toRadians(userLng - venueLng);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(userLat)) * Math.cos(Math.toRadians(venueLat))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        Log.i(TAG, "calculateDistanceInMeter: c: " + c);

        return (int) Math.round(AVERAGE_RADIUS_OF_EARTH_KM * c * 1000);
    }

    double getLastLocationTime() {
        return lastLocationTime;
    }
}
