package in.krharsh17.programmersdate.home.managers;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import in.krharsh17.programmersdate.SharedPrefManager;
import in.krharsh17.programmersdate.ViewUtils;
import in.krharsh17.programmersdate.models.Couple;

import static com.android.volley.VolleyLog.TAG;
import static in.krharsh17.programmersdate.Constants.couplesRef;

public class CoupleManager {

    private String id;
    private OnFetchedListener onFetchedListener;
    private Activity context;
    private OnSyncedListener onSyncedListener;

    public void setOnSyncedListener(OnSyncedListener onSyncedListener) {
        this.onSyncedListener = onSyncedListener;
    }

    public CoupleManager(Activity context) {
        id = new SharedPrefManager(context).getCoupleId();
        this.context = context;
    }

    public CoupleManager syncWithServer(final Activity context) {
        final Long roll = new SharedPrefManager(context).getRollNumber();
        Log.i(TAG, "syncWithServer: " + roll);
        if (roll != 0) {
            couplesRef.orderByChild("player1Roll")
                    .startAt(roll)
                    .endAt(roll)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            final ObjectMapper objectMapper = new ObjectMapper();
                            HashMap<String, Couple> couples = (HashMap<String, Couple>) dataSnapshot.getValue();
                            Log.i(TAG, "onDataChange: 1");
                            if (couples == null || couples.size() != 1) {
                                couplesRef.orderByChild("player2Roll")
                                        .startAt(roll)
                                        .endAt(roll)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                Log.i(TAG, "onDataChange: 2");
                                                HashMap<String, Couple> couples1 = (HashMap<String, Couple>) dataSnapshot.getValue();

                                                Log.i(TAG, "onDataChange2: " + new SharedPrefManager(context).getCoupleId());
                                                if (couples1 != null) {
                                                    Couple couple = objectMapper.convertValue(couples1.values().toArray()[0], Couple.class);
                                                    SharedPrefManager sharedPrefManager = new SharedPrefManager(context);
                                                    sharedPrefManager.setPlayerIndex(2);
                                                    sharedPrefManager.setCoupleId(couple.getId());
                                                    if (onSyncedListener != null)
                                                        onSyncedListener.onComplete(true);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                if (onSyncedListener != null)
                                                    onSyncedListener.onComplete(false);
                                            }
                                        });
                            } else {
                                Couple couple = objectMapper.convertValue(couples.values().toArray()[0], Couple.class);
                                SharedPrefManager sharedPrefManager = new SharedPrefManager(context);
                                sharedPrefManager.setPlayerIndex(1);
                                sharedPrefManager.setCoupleId(couple.getId());
                                if (onSyncedListener != null)
                                    onSyncedListener.onComplete(true);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            if (onSyncedListener != null)
                                onSyncedListener.onComplete(false);
                        }
                    });
        }

        return this;
    }

    public interface OnFetchedListener {
        void onCoupleFetched(Couple couple);

        void onErrorOccured(String message);
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
        else
            ViewUtils.showToast(context, "Something went wrong!", ViewUtils.DURATION_SHORT);
        return this;
    }

    public interface OnSyncedListener {
        void onComplete(boolean success);
    }

}
