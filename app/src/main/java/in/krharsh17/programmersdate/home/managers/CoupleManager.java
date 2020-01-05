package in.krharsh17.programmersdate.home.managers;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import in.krharsh17.programmersdate.SharedPrefManager;
import in.krharsh17.programmersdate.models.Couple;

import static in.krharsh17.programmersdate.Constants.couplesRef;

public class CoupleManager {

    private String id = "c1";
    private OnFetchedListener onFetchedListener;

    public CoupleManager(Context context) {
        //id = new SharedPrefManager(context).getCoupleId();
    }

    public static void syncWithServer(final Activity context) {
        final Long roll = new SharedPrefManager(context).getRollNumber();
        if (roll != 0) {
            couplesRef.orderByChild("player1Roll")
                    .startAt(roll)
                    .endAt(roll)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            final ObjectMapper objectMapper = new ObjectMapper();
                            HashMap<String, Couple> couples = (HashMap<String, Couple>) dataSnapshot.getValue();
                            if (couples == null || couples.size() != 1) {
                                couplesRef.orderByChild("player2Roll")
                                        .startAt(roll)
                                        .endAt(roll)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                HashMap<String, Couple> couples1 = (HashMap<String, Couple>) dataSnapshot.getValue();
                                                if (couples1 != null && couples1.size() == 1) {
                                                    Couple couple = objectMapper.convertValue(couples1.values().toArray()[0], Couple.class);
                                                    SharedPrefManager sharedPrefManager = new SharedPrefManager(context);
                                                    sharedPrefManager.setPlayerIndex(2);
                                                    sharedPrefManager.setTeamId(couple.getId());
                                                    LevelManager.createGame(context);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                            } else {
                                Couple couple = objectMapper.convertValue(couples.values().toArray()[0], Couple.class);
                                SharedPrefManager sharedPrefManager = new SharedPrefManager(context);
                                sharedPrefManager.setPlayerIndex(1);
                                sharedPrefManager.setTeamId(couple.getId());
                                LevelManager.createGame(context);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }
    }

    public void setOnFetchedListener(OnFetchedListener onFetchedListener) {
        this.onFetchedListener = onFetchedListener;
    }

    public CoupleManager getCouple() {
        if (!id.equals("NOT_FOUND"))
            couplesRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (onFetchedListener != null)
                        onFetchedListener.onCoupleFetched(dataSnapshot.getValue(Couple.class));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    if (onFetchedListener != null)
                        onFetchedListener.onErrorOccured(databaseError.getMessage());
                }
            });
        else if (onFetchedListener != null)
            onFetchedListener.onErrorOccured("Not found!");
        return this;
    }

    public interface OnFetchedListener {
        void onCoupleFetched(Couple couple);

        void onErrorOccured(String message);
    }

}