package in.krharsh17.programmersdate.home.managers;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import in.krharsh17.programmersdate.SharedPrefManager;
import in.krharsh17.programmersdate.models.Couple;

import static in.krharsh17.programmersdate.Constants.couplesRef;

public class LocationManager {

    LatLng latLng;
    Context context;
    long rollNo;

    OnLocationChangedListener onLocationChangeListener;

    public LocationManager(Context context) {
        this.context = context;
        rollNo = new SharedPrefManager(context).getRollNumber();
    }

//    public static double distance(double lat1, double lng1,
//                                  double lat2, double lng2) {
    public static boolean distance(ArrayList<LatLng> latLngs,double lat2, double lng2) {
        for(LatLng latLng : latLngs){
            double lat1 = latLng.latitude;
            double lng1 = latLng.longitude;
            double a = (lat1 - lat2) * LocationManager.distPerLat(lat1);
            double b = (lng1 - lng2) * LocationManager.distPerLng(lat1);
            double dist = Math.sqrt(a * a + b * b);
            if(dist < 50){
                return true;
            }
        }
        return false;
    }

    private static double distPerLng(double lat) {
        return 0.0003121092 * Math.pow(lat, 4)
                + 0.0101182384 * Math.pow(lat, 3)
                - 17.2385140059 * lat * lat
                + 5.5485277537 * lat + 111301.967182595;
    }

    private static double distPerLat(double lat) {
        return -0.000000487305676 * Math.pow(lat, 4)
                - 0.0033668574 * Math.pow(lat, 3)
                + 0.4601181791 * lat * lat
                - 1.4558127346 * lat + 110579.25662316;
    }

    public void setOnLocationChangeListener(OnLocationChangedListener onLocationChangeListener) {
        this.onLocationChangeListener = onLocationChangeListener;
    }

    public LocationManager fetchPartnerLocation() {
        final String id = new SharedPrefManager(context).getCoupleId();
        final int index = new SharedPrefManager(context).getPlayerIndex();
        if (!id.equals("NOT_FOUND") && (index == 1 || index == 2)) {
            if (index == 1) {
                couplesRef.child(id).child("player2Location")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (onLocationChangeListener != null && dataSnapshot.getValue() != null)
                                    onLocationChangeListener.onLocationChanged(Couple.getLatLng((ArrayList<Double>) dataSnapshot.getValue()));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                if (onLocationChangeListener != null)
                                    onLocationChangeListener.onErrorOccured(databaseError.getMessage());
                            }
                        });
            } else {
                couplesRef.child(id).child("player1Location")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (onLocationChangeListener != null && dataSnapshot.getValue() != null)
                                    onLocationChangeListener.onLocationChanged(Couple.getLatLng((ArrayList<Double>) dataSnapshot.getValue()));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                if (onLocationChangeListener != null)
                                    onLocationChangeListener.onErrorOccured(databaseError.getMessage());
                            }
                        });

            }


        }
        return this;
    }

    public void writeUserLocation(LatLng latLng) {
        ArrayList<Double> userLocation = new ArrayList<>();
        userLocation.add(latLng.latitude);
        userLocation.add(latLng.longitude);
        String s = new SharedPrefManager(context).getCoupleId();
        FirebaseDatabase.getInstance().getReference().child("Couples").child(s).setValue(userLocation);
    }

    public void writeUserLocation(ArrayList<Double> userLocation) {
        String s = new SharedPrefManager(context).getCoupleId();
        int i = new SharedPrefManager(context).getPlayerIndex();
        if (!s.equals("NOT_FOUND") && i != 0) {
            couplesRef.child(s).child("player" + i + "Location").setValue(userLocation);
        }

    }

    public void writeUserLocation(Double lat, Double longi) {
        ArrayList<Double> userLocation = new ArrayList<>();
        userLocation.add(lat);
        userLocation.add(longi);
        String s = new SharedPrefManager(context).getCoupleId();
        FirebaseDatabase.getInstance().getReference().child("Couples").child(s).setValue(userLocation);
    }

    public interface OnLocationChangedListener {
        void onLocationChanged(LatLng location);

        void onErrorOccured(String message);
    }

}
