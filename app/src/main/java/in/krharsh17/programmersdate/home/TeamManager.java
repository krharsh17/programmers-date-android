package in.krharsh17.programmersdate.home;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import in.krharsh17.programmersdate.SharedPreferencesManager;
import in.krharsh17.programmersdate.models.Team;

public class TeamManager {

    Team team;
    String id = new SharedPreferencesManager().getID();

    public Team getTeam(){

        FirebaseDatabase.getInstance().getReference().child("Couples").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                team = dataSnapshot.getValue(Team.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return this.team;
    }

}
