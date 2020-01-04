package in.krharsh17.programmersdate.home.managers;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import in.krharsh17.programmersdate.SharedPrefManager;
import in.krharsh17.programmersdate.models.Couple;
import in.krharsh17.programmersdate.models.Tasks;
import in.krharsh17.programmersdate.splash.SplashActivity;

import static in.krharsh17.programmersdate.Constants.tasksRef;

public class LevelManager {

    public static void createGame(final Activity activity) {
        if (!new SharedPrefManager(activity).getGameCreated()) {
            if (new SharedPrefManager(activity).getPlayerIndex() == 0) {
                CoupleManager.syncWithServer(activity);
            } else if (new SharedPrefManager(activity).getRollNumber() == 0) {
                activity.startActivity(new Intent(activity, SplashActivity.class));
                activity.finish();
            } else {
                new CoupleManager(activity).getCouple().setOnFetchedListener(new CoupleManager.OnFetchedListener() {
                    @Override
                    public void onCoupleFetched(Couple couple) {

                        if (couple.getLevels() == null || couple.getLevels().size() == 0) {
                            generateLevels();
                        } else {
                            new SharedPrefManager(activity).setGameCreated(true);
                        }
                    }

                    @Override
                    public void onErrorOccured(String message) {

                    }
                });
            }
        }
    }

    private static void generateLevels() {
        tasksRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Tasks tasks = dataSnapshot.getValue(Tasks.class);
//                TODO Randomly create levels here

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}