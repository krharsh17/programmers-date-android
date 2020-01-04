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

public class LocationManager {

    LatLng latLng;
    Context context;
    long rollNo;

    public LocationManager(Context context){
        this.context = context;
        rollNo = new SharedPrefManager(context).getRollNumber();
    }


    public LatLng fetchPartnerCoordinates(){
        FirebaseDatabase.getInstance().getReference().child("Couples").orderByChild("player1Roll").startAt(rollNo).endAt(rollNo).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    FirebaseDatabase.getInstance().getReference().child("Couples").orderByChild("player2Roll").startAt(rollNo).endAt(rollNo).
                            addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()){
                                        Couple couple = dataSnapshot.getValue(Couple.class);
                                        new SharedPrefManager(context).setTeamId(couple.getId());
                                        ArrayList<Double> partnerLocation = couple.getPlayer1Location();
                                        latLng = new LatLng(partnerLocation.get(0),partnerLocation.get(1));
                                     }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                }else {
                    Couple couple = dataSnapshot.getValue(Couple.class);
                    ArrayList<Double> partnerLocation = couple.getPlayer2Location();
                    latLng = new LatLng(partnerLocation.get(0),partnerLocation.get(1));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return this.latLng;
    }

    public void writeUserLocation(LatLng latLng){
        ArrayList<Double> userLocation = new ArrayList<>();
        userLocation.add(latLng.latitude);
        userLocation.add(latLng.longitude);
        String s = new SharedPrefManager(context).getCoupleId();
        FirebaseDatabase.getInstance().getReference().child("Couples").child(s).setValue(userLocation);
    }

    public void writeUserLocation(ArrayList<Double> userLocation){
        String s = new SharedPrefManager(context).getCoupleId();
        FirebaseDatabase.getInstance().getReference().child("Couples").child(s).setValue(userLocation);
    }

    public void writeUserLocation(Double lat,Double longi){
        ArrayList<Double> userLocation = new ArrayList<>();
        userLocation.add(lat);
        userLocation.add(longi);
        String s = new SharedPrefManager(context).getCoupleId();
        FirebaseDatabase.getInstance().getReference().child("Couples").child(s).setValue(userLocation);
    }

    public static double distance(double lat1, double lng1,
                                  double lat2, double lng2){
        double a = (lat1-lat2)*LocationManager.distPerLat(lat1);
        double b = (lng1-lng2)*LocationManager.distPerLng(lat1);
        return Math.sqrt(a*a+b*b);
    }

    private static double distPerLng(double lat){
        return 0.0003121092*Math.pow(lat, 4)
                +0.0101182384*Math.pow(lat, 3)
                -17.2385140059*lat*lat
                +5.5485277537*lat+111301.967182595;
    }

    private static double distPerLat(double lat){
        return -0.000000487305676*Math.pow(lat, 4)
                -0.0033668574*Math.pow(lat, 3)
                +0.4601181791*lat*lat
                -1.4558127346*lat+110579.25662316;
    }

}
