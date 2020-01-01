package in.krharsh17.programmersdate.home;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import in.krharsh17.programmersdate.SharedPrefManager;
import in.krharsh17.programmersdate.models.Team;

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
                                        Team team = dataSnapshot.getValue(Team.class);
                                        new SharedPrefManager(context).setTeamId(team.getId());
                                        ArrayList<Double> partnerLocation = team.getPlayer1Location();
                                        latLng = new LatLng(partnerLocation.get(0),partnerLocation.get(1));
                                     }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                }else {
                    Team team = dataSnapshot.getValue(Team.class);
                    ArrayList<Double> partnerLocation = team.getPlayer2Location();
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
        String s = new SharedPrefManager(context).getTeamId();
        FirebaseDatabase.getInstance().getReference().child("Couples").child(s).setValue(userLocation);
    }

    public void writeUserLocation(ArrayList<Double> userLocation){
        String s = new SharedPrefManager(context).getTeamId();
        FirebaseDatabase.getInstance().getReference().child("Couples").child(s).setValue(userLocation);
    }

    public void writeUserLocation(Double lat,Double longi){
        ArrayList<Double> userLocation = new ArrayList<>();
        userLocation.add(lat);
        userLocation.add(longi);
        String s = new SharedPrefManager(context).getTeamId();
        FirebaseDatabase.getInstance().getReference().child("Couples").child(s).setValue(userLocation);
    }

}
