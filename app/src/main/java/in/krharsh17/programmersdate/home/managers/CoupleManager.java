package in.krharsh17.programmersdate.home.managers;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import in.krharsh17.programmersdate.SharedPrefManager;
import in.krharsh17.programmersdate.ViewUtils;
import in.krharsh17.programmersdate.models.Couple;
import in.krharsh17.programmersdate.models.Level;
import in.krharsh17.programmersdate.models.Tasks;
import in.krharsh17.programmersdate.splash.SplashActivity;

import static in.krharsh17.programmersdate.Constants.couplesRef;
import static in.krharsh17.programmersdate.Constants.numLevels;
import static in.krharsh17.programmersdate.Constants.numLocs;
import static in.krharsh17.programmersdate.Constants.taskTypeBar;
import static in.krharsh17.programmersdate.Constants.taskTypeLogo;
import static in.krharsh17.programmersdate.Constants.taskTypePose;
import static in.krharsh17.programmersdate.Constants.taskTypeQR;
import static in.krharsh17.programmersdate.Constants.taskTypeTwister;
import static in.krharsh17.programmersdate.Constants.tasksRef;

public class CoupleManager {

    private String id;
    private OnFetchedListener onFetchedListener;
    private Activity context;

    public CoupleManager(Activity context) {
        id = new SharedPrefManager(context).getCoupleId();
        this.context = context;
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
                                                    sharedPrefManager.setCoupleId(couple.getId());
                                                    CoupleManager.createGame(context);
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
                                sharedPrefManager.setCoupleId(couple.getId());
                                CoupleManager.createGame(context);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }
    }

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
                            ViewUtils.showProgressDialog(activity, "Generating levels for you...");
                            generateLevels(activity);
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

    private static void generateLevels(final Activity activity) {
        tasksRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Tasks tasks = dataSnapshot.getValue(Tasks.class);
                String[] taskList = new String[]{taskTypeQR, taskTypePose, taskTypeLogo, taskTypeBar, taskTypeTwister};
                int[] taskCount = new int[5];
                int maxTaskCount = numLevels / 5;
                //TODO Randomly create levels here
                final ArrayList<Level> levels = new ArrayList<>();
                ArrayList<String> qrs = new ArrayList<>();
                ArrayList<String> bars = new ArrayList<>();
                ArrayList<String> poses = new ArrayList<>();
                ArrayList<String> logos = new ArrayList<>();
                ArrayList<String> twisters = new ArrayList<>();

                ArrayList<ArrayList<Double>> qrLocs;
                ArrayList<ArrayList<Double>> barLocs;
                ArrayList<ArrayList<Double>> poseLocs;
                ArrayList<ArrayList<Double>> logoLocs;
                ArrayList<ArrayList<Double>> twisterLocs;

                for (int i = 0; i < numLevels; i++) {
                    int rand = (int) Math.round(4 * Math.random());
                    if (taskCount[rand] < maxTaskCount) {
                        Level level = new Level(i + 1, taskList[rand]);

                        boolean taskAssigned = false;
                        switch (taskList[rand]) {
                            case taskTypeQR:
                                while (!taskAssigned) {
                                    int ind = (int) Math.round(tasks.getQrTask().getQrList().size() * Math.random()) - 1;
                                    if (!qrs.contains(tasks.getQrTask().getQrList().get(ind))) {
                                        level.setQrValue(tasks.getQrTask().getQrList().get(ind));
                                        qrs.add(tasks.getQrTask().getQrList().get(ind));
                                        taskAssigned = true;
                                        qrLocs = new ArrayList<>();
                                        for (int j = 0; j < numLocs; j++) {
                                            boolean locAssigned = false;
                                            while (!locAssigned) {
                                                int locInd = (int) Math.round(tasks.getQrTask().getLocations().size() * Math.random()) - 1;
                                                if (!qrLocs.contains(tasks.getQrTask().getLocations().get(locInd))) {
                                                    qrLocs.add(tasks.getQrTask().getLocations().get(locInd));
                                                    locAssigned = true;
                                                }
                                            }
                                        }
                                        level.setLocations(qrLocs);
                                        levels.add(level);
                                    }
                                }
                                break;
                            case taskTypePose:
                                while (!taskAssigned) {
                                    int ind = (int) Math.round(tasks.getPoseTask().getPoseList().size() * Math.random()) - 1;
                                    if (!poses.contains(tasks.getPoseTask().getPoseList().get(ind))) {
                                        level.setQrValue(tasks.getPoseTask().getPoseList().get(ind));
                                        poses.add(tasks.getPoseTask().getPoseList().get(ind));
                                        taskAssigned = true;
                                        poseLocs = new ArrayList<>();
                                        for (int j = 0; j < numLocs; j++) {
                                            boolean locAssigned = false;
                                            while (!locAssigned) {
                                                int locInd = (int) Math.round(tasks.getPoseTask().getLocations().size() * Math.random()) - 1;
                                                if (!poseLocs.contains(tasks.getPoseTask().getLocations().get(locInd))) {
                                                    poseLocs.add(tasks.getPoseTask().getLocations().get(locInd));
                                                    locAssigned = true;
                                                }
                                            }
                                        }
                                        level.setLocations(poseLocs);
                                        levels.add(level);
                                    }
                                }
                                break;
                            case taskTypeLogo:
                                while (!taskAssigned) {
                                    int ind = (int) Math.round(tasks.getLogoTask().getLogoList().size() * Math.random()) - 1;
                                    if (!logos.contains(tasks.getLogoTask().getLogoList().get(ind))) {
                                        level.setQrValue(tasks.getLogoTask().getLogoList().get(ind));
                                        logos.add(tasks.getLogoTask().getLogoList().get(ind));
                                        taskAssigned = true;
                                        logoLocs = new ArrayList<>();
                                        for (int j = 0; j < numLocs; j++) {
                                            boolean locAssigned = false;
                                            while (!locAssigned) {
                                                int locInd = (int) Math.round(tasks.getLogoTask().getLocations().size() * Math.random()) - 1;
                                                if (!logoLocs.contains(tasks.getLogoTask().getLocations().get(locInd))) {
                                                    logoLocs.add(tasks.getLogoTask().getLocations().get(locInd));
                                                    locAssigned = true;
                                                }
                                            }
                                        }
                                        level.setLocations(logoLocs);
                                        levels.add(level);
                                    }
                                }
                                break;
                            case taskTypeBar:
                                while (!taskAssigned) {
                                    int ind = (int) Math.round(tasks.getBarTask().getBarcodeList().size() * Math.random()) - 1;
                                    if (!bars.contains(tasks.getBarTask().getBarcodeList().get(ind))) {
                                        level.setQrValue(tasks.getBarTask().getBarcodeList().get(ind));
                                        bars.add(tasks.getBarTask().getBarcodeList().get(ind));
                                        taskAssigned = true;
                                        barLocs = new ArrayList<>();
                                        for (int j = 0; j < numLocs; j++) {
                                            boolean locAssigned = false;
                                            while (!locAssigned) {
                                                int locInd = (int) Math.round(tasks.getBarTask().getLocations().size() * Math.random()) - 1;
                                                if (!barLocs.contains(tasks.getBarTask().getLocations().get(locInd))) {
                                                    barLocs.add(tasks.getBarTask().getLocations().get(locInd));
                                                    locAssigned = true;
                                                }
                                            }
                                        }
                                        level.setLocations(barLocs);
                                        levels.add(level);
                                    }
                                }
                                break;
                            case taskTypeTwister:
                                while (!taskAssigned) {
                                    int ind = (int) Math.round(tasks.getTwisterTask().getTwisterList().size() * Math.random()) - 1;
                                    if (!twisters.contains(tasks.getTwisterTask().getTwisterList().get(ind))) {
                                        level.setQrValue(tasks.getTwisterTask().getTwisterList().get(ind));
                                        twisters.add(tasks.getTwisterTask().getTwisterList().get(ind));
                                        taskAssigned = true;
                                        twisterLocs = new ArrayList<>();
                                        for (int j = 0; j < numLocs; j++) {
                                            boolean locAssigned = false;
                                            while (!locAssigned) {
                                                int locInd = (int) Math.round(tasks.getTwisterTask().getLocations().size() * Math.random()) - 1;
                                                if (!twisterLocs.contains(tasks.getTwisterTask().getLocations().get(locInd))) {
                                                    twisterLocs.add(tasks.getTwisterTask().getLocations().get(locInd));
                                                    locAssigned = true;
                                                }
                                            }
                                        }
                                        level.setLocations(twisterLocs);
                                        levels.add(level);
                                    }
                                }
                                break;
                        }
                    }

                }

                new CoupleManager(activity).getCouple().setOnFetchedListener(new OnFetchedListener() {
                    @Override
                    public void onCoupleFetched(Couple couple) {
                        couple.setLevels(levels);
                        couplesRef.child(couple.getId()).setValue(couple);
                        new SharedPrefManager(activity).setGameCreated(true);
                        ViewUtils.removeDialog();
                    }

                    @Override
                    public void onErrorOccured(String message) {
                        ViewUtils.showToast(activity, "Something went wrong!", ViewUtils.DURATION_SHORT);
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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

}
