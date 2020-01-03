package in.krharsh17.programmersdate.home.managers;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import in.krharsh17.programmersdate.SharedPrefManager;
import in.krharsh17.programmersdate.models.Couple;

public class TeamManager {

    Couple couple;
    String id;

    public TeamManager(Context context){
        id = new SharedPrefManager(context).getTeamId();
    }

    public Couple getCouple() {

        FirebaseDatabase.getInstance().getReference().child("Couples").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                couple = dataSnapshot.getValue(Couple.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return this.couple;
    }

}
