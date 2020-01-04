package in.krharsh17.programmersdate.home.managers;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import in.krharsh17.programmersdate.SharedPrefManager;
import in.krharsh17.programmersdate.models.Couple;

import static in.krharsh17.programmersdate.Constants.couplesRef;

public class LocationManager {

    LatLng latLng;
    Context context;
    long rollNo;
    OnLocationChangeListener onLocationChangeListener;

    public LocationManager(Context context) {
        this.context = context;
        rollNo = new SharedPrefManager(context).getRollNumber();
    }

    public void setOnLocationChangeListener(OnLocationChangeListener onLocationChangeListener) {
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


        } else {
            if (onLocationChangeListener != null)
                onLocationChangeListener.onErrorOccured("Not found!");

        }
        return this;
    }

    public void writeUserLocation(LatLng latLng) {
        ArrayList<Double> userLocation = new ArrayList<>();
        userLocation.add(latLng.latitude);
        userLocation.add(latLng.longitude);
        writeUserLocation(userLocation);
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
        writeUserLocation(userLocation);
    }

    public interface OnLocationChangeListener {
        void onLocationChanged(LatLng partnerNewLocation);

        void onErrorOccured(String error);
    }

}
