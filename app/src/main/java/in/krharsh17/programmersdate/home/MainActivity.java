package in.krharsh17.programmersdate.home;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import in.krharsh17.programmersdate.Constants;
import in.krharsh17.programmersdate.R;
import in.krharsh17.programmersdate.SharedPrefManager;
import in.krharsh17.programmersdate.ViewUtils;
import in.krharsh17.programmersdate.home.bottompager.BottomPagerAdapter;
import in.krharsh17.programmersdate.home.bottompager.DetailFragment;
import in.krharsh17.programmersdate.home.managers.CoupleManager;
import in.krharsh17.programmersdate.home.managers.LocationManager;
import in.krharsh17.programmersdate.models.Couple;
import in.krharsh17.programmersdate.models.Level;

public class MainActivity extends AppCompatActivity implements Constants {

    public RecyclerView levelRecycler;
    LinearLayoutManager linearLayoutManagerThree;
    RecyclerView.SmoothScroller smoothScroller;
    Couple currentCouple;
    long timeRemaining;
    LocationManager locationManager;

    CountDownTimer countDownTimer;
    int currentLevel;

    boolean appRunning = false;

    long timeLimit;
    Dialog dialog;
    Map map;
    ViewPager bottomPager;
    private boolean areOverlaysShown = true;

    ArrayList<LatLng> gameLocations = new ArrayList<>();

    ExtendedFloatingActionButton time;

    String coupleId;

    ArrayList<LatLng> levelLatLng;

    Thread timer;
    private boolean blocked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        init();
        run();
    }

    void init() {

        appRunning = true;
        map = new Map();
        levelLatLng = new ArrayList<>();
        bottomPager = findViewById(R.id.bottom_pager);
        levelRecycler = findViewById(R.id.levels_recycler);
        time = findViewById(R.id.time_remaining);
        locationManager = new LocationManager(this);
        linearLayoutManagerThree = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        smoothScroller = new
                LinearSmoothScroller(this) {
                    @Override
                    protected int getHorizontalSnapPreference() {
                        return LinearSmoothScroller.SNAP_TO_START;
                    }

                    @Override
                    protected float calculateSpeedPerPixel
                            (DisplayMetrics displayMetrics) {
                        return 200f / displayMetrics.densityDpi;
                    }
                };
        coupleId = new SharedPrefManager(this).getCoupleId();

        dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.dialog_complete);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
    }


    void run() {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.main_frame, map)
                .commit();

        hideOverlays();
        ViewUtils.showProgressDialog(this, "Please wait");


        if (!coupleId.equals("NOT_FOUND")) {
            new CoupleManager(this)
                    .getCouple().setOnFetchedListener(new CoupleManager.OnFetchedListener() {
                @Override
                public void onCoupleFetched(Couple couple) {
                    currentLevel = couple.getCurrentLevel();
                    setCurrentCouple(couple);
                    setLevelRecycler();
                    checkCurrentPosition();
                    attachCoupleListener();
                    ViewUtils.removeDialog();
                    startTimer();
//                    if (!currentCouple.isEnabled()) {
//                        dialog.show();
//                        blocked = true;
//                    } else {
//                        if(blocked){
//                            dialog.cancel();
//                            blocked = false;
//                        }
//                    }
                    FirebaseDatabase.getInstance().getReference().child("settings").child("gameRunning").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Log.i(TAG, "onDataChange: ");
                            if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                                boolean gameRunning = dataSnapshot.getValue(Boolean.class);
                                if (gameRunning) {

                                    if (ViewUtils.completeShowing) {
                                        ViewUtils.removeDialog();
                                        ViewUtils.completeShowing = false;
                                    }
                                } else {

                                    if (!ViewUtils.completeShowing)
                                        ViewUtils.showCompleteDialog(MainActivity.this);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

                @Override
                public void onErrorOccured(String message) {
                    ViewUtils.showToast(MainActivity.this, message, ViewUtils.DURATION_SHORT);
                }
            });

        } else {
            new CoupleManager(MainActivity.this).syncWithServer(this);
            ViewUtils.showToast(MainActivity.this, "Some error occured!", ViewUtils.DURATION_SHORT);
        }
        levelRecycler.setLayoutManager(linearLayoutManagerThree);
        levelRecycler.setHasFixedSize(true);
        levelRecycler.setLayoutFrozen(true);

    }

    void startTimer() {
        if (timeRemaining == 0)
            timeRemaining = Constants.timeLimit;
        if (timer == null) {
            timer = new Thread(new Runnable() {
                @Override
                public void run() {
                    if (appRunning)
                        try {
                            Thread.sleep(30000);
                            refreshClock(timeLimit);
                        } catch (Exception e) {

                        }
                }
            });
            timer.start();
        }
    }

    void attachCoupleListener() {
        couplesRef
                .child(coupleId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot != null && dataSnapshot.getValue() != null)
                            setCurrentCouple(dataSnapshot.getValue(Couple.class));
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        locationManager.fetchPartnerLocation().setOnLocationChangeListener(new LocationManager.OnLocationChangedListener() {
            @Override
            public void onLocationChanged(LatLng location) {
                map.setPartnerLocation(location);
            }

            @Override
            public void onErrorOccured(String message) {

            }
        });

//        couplesRef
//                .child(coupleId)
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        if (dataSnapshot != null && dataSnapshot.getValue() != null)
//                            setCurrentCouple(dataSnapshot.getValue(Couple.class));
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });

    }

    public void setCurrentCouple(Couple currentCouple) {
        this.currentCouple = currentCouple;
        refreshClock(currentCouple.getTimeLimit());
        timeLimit = currentCouple.getTimeLimit();
        setupBottomPager();
        if (currentLevel != currentCouple.getCurrentLevel() || currentLevel == 0)
            setLevelRecycler();
        bottomPager.setCurrentItem(currentCouple.getCurrentLevel() - 1, true);
        refreshMap();

//        if (!currentCouple.isEnabled() && !blocked) {
//            dialog.show();
//            blocked = true;
//        } else {
//            if (blocked) {
//                dialog.cancel();
//                blocked = false;
//            }
//        }

        currentLevel = currentCouple.getCurrentLevel();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showOverlays();
            }
        }, 1000);
    }

    void refreshMap() {
        gameLocations = new ArrayList<>();
        levelLatLng = new ArrayList<>();
        map.clearLandmarks();
        Level currentLevel = currentCouple.getLevels().get(currentCouple.getCurrentLevel() - 1);
        if (currentLevel.getLocations() != null)
            for (String location : currentLevel.getLocations()) {

                switch (location) {
                    case sacBuildingText:
                        map.addLandmark(sacBuilding, R.drawable.marker_sac);
                        gameLocations.add(sacBuilding);
                        levelLatLng.add(sacBuilding);
                        break;
                    case mainBuildingText:
                        map.addLandmark(mainBuilding, R.drawable.marker_main_building);
                        gameLocations.add(mainBuilding);
                        levelLatLng.add(mainBuilding);
                        break;
                    case civilDeptText:
                        map.addLandmark(civilDept, R.drawable.marker_civil);
                        gameLocations.add(civilDept);
                        levelLatLng.add(civilDept);
                        break;
                    case computerCentreText:
                        map.addLandmark(computerCentre, R.drawable.marker_cc);
                        gameLocations.add(computerCentre);
                        levelLatLng.add(computerCentre);
                        break;
                    case tennisCourtText:
                        map.addLandmark(tennisCourt, R.drawable.marker_tennis);
                        gameLocations.add(tennisCourt);
                        levelLatLng.add(tennisCourt);
                        break;
                    case guestHouseText:
                        map.addLandmark(guestHouse, R.drawable.marker_guest);
                        gameLocations.add(guestHouse);
                        levelLatLng.add(guestHouse);
                        break;
                    case gangaHostelText:
                        map.addLandmark(Constants.gangaHostel, R.drawable.marker_ganga);
                        gameLocations.add(gangaHostel);
                        levelLatLng.add(gangaHostel);
                        break;
                    case CWRSText:
                        map.addLandmark(Constants.CWRS, R.drawable.marker_cwrs);
                        gameLocations.add(CWRS);
                        levelLatLng.add(CWRS);
                        break;
                    case canteenGopalJiText:
                        map.addLandmark(Constants.canteenGopalJi, R.drawable.marker_gopal);
                        gameLocations.add(canteenGopalJi);
                        levelLatLng.add(canteenGopalJi);
                        break;
                    case canteenShuklaJiText:
                        map.addLandmark(Constants.canteenShuklaJi, R.drawable.marker_shukla);
                        gameLocations.add(canteenShuklaJi);
                        levelLatLng.add(canteenShuklaJi);
                        break;
                    case groundText:
                        map.addLandmark(Constants.ground, R.drawable.marker_ground);
                        gameLocations.add(ground);
                        levelLatLng.add(ground);
                        break;
                    case electricalDeptText:
                        map.addLandmark(Constants.electricalDept, R.drawable.marker_electrical);
                        gameLocations.add(electricalDept);
                        levelLatLng.add(electricalDept);
                        break;
                    case cseDeptText:
                        map.addLandmark(Constants.cseDept, R.drawable.marker_cse);
                        gameLocations.add(cseDept);
                        levelLatLng.add(cseDept);
                        break;
                    case electronicsDeptText:
                        map.addLandmark(Constants.electronicsDept, R.drawable.marker_ec);
                        gameLocations.add(electronicsDept);
                        levelLatLng.add(electronicsDept);
                        break;
                    case directorBungalowText:
                        map.addLandmark(directorBungalow, R.drawable.marker_director);
                        gameLocations.add(directorBungalow);
                        levelLatLng.add(directorBungalow);
                        break;
                }
            }
    }

    void setLevelRecycler() {
        if (currentCouple == null)
            return;
        levelRecycler.setAdapter(new LevelsAdapter(MainActivity.this, currentCouple.getLevels(), currentCouple.getCurrentLevel()));
        smoothScroller.setTargetPosition(currentCouple.getCurrentLevel() - 1);
        linearLayoutManagerThree.startSmoothScroll(smoothScroller);
    }

    @SuppressLint("ClickableViewAccessibility")
    void setupBottomPager() {


        BottomPagerAdapter bottomPagerAdapter = new BottomPagerAdapter(getSupportFragmentManager());
        ArrayList<DetailFragment> frags = new ArrayList<>();
        for (int i = 1; i <= numLevels; i++) {
            frags.add(new DetailFragment().setTaskType(currentCouple.getLevels().get(i - 1).getTaskType(), i));
        }
        bottomPagerAdapter.setFragments(frags);
        Log.i(TAG, "setupBottomPager: " + frags.size());
        bottomPager.setAdapter(bottomPagerAdapter);


        bottomPager.setPadding(
                (Math.round(getResources().getDisplayMetrics().widthPixels - TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 288, getResources().getDisplayMetrics())) / 2),
                0,
                Math.round(getResources().getDisplayMetrics().widthPixels - TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 288, getResources().getDisplayMetrics())) / 2,
                0);
        bottomPager.setClipToPadding(false);

        bottomPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

    }

    void refreshClock(long timeLimit) {
        long timeRemaining = startTime + timeLimit / 1000 - System.currentTimeMillis() / 1000;
        Log.i(TAG, "refreshClock: " + timeRemaining);
        this.timeRemaining = timeRemaining;
        if (timeRemaining <= 0) {
            if (countDownTimer != null) {
                countDownTimer.cancel();
                countDownTimer.onFinish();
            }
        }
        time.setText(timeRemaining / 60 / 60 + ":" +
                timeRemaining / 60 % 60 + " HOURS REMAINING");
    }

    @Override
    protected void onPause() {
        super.onPause();
        map.disableGPS();
        appRunning = false;
        Log.i(TAG, "onPause: ");
        new CoupleManager(MainActivity.this).getCouple().setOnFetchedListener(new CoupleManager.OnFetchedListener() {
            @Override
            public void onCoupleFetched(Couple couple) {
                couple.setEnabled(false);
                couplesRef.child(couple.getId()).setValue(couple);
            }

            @Override
            public void onErrorOccured(String message) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        map.enableGPS();
        appRunning = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        map.disableGPS();
        appRunning = false;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        appRunning = true;
    }

    public void checkCurrentPosition() {
        Runnable r = new Runnable() {
            public void run() {
                while (true) {
                    if (appRunning) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (currentCouple != null) {
                                    smoothScroller.setTargetPosition(currentCouple.getCurrentLevel() - 1);
                                    linearLayoutManagerThree.startSmoothScroll(smoothScroller);
                                }
                            }
                        });
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
        new Thread(r).start();

    }

    void hideOverlays() {
        if (areOverlaysShown) {
            levelRecycler.animate().yBy(Math.round(0 - getResources().getDisplayMetrics().heightPixels * 0.16))
                    .setDuration(400)
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            bottomPager.animate().translationY(TypedValue.applyDimension(
                                    TypedValue.COMPLEX_UNIT_DIP,
                                    212,
                                    getResources().getDisplayMetrics()
                            ))
                                    .setDuration(400);
                            time.shrink(new ExtendedFloatingActionButton.OnChangedCallback() {
                                @Override
                                public void onShrunken(ExtendedFloatingActionButton extendedFab) {
                                    time.hide();
                                    super.onShrunken(extendedFab);
                                }
                            });
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {

                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
            areOverlaysShown = false;
        }
    }

    void showOverlays() {
        if (!areOverlaysShown) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    levelRecycler.animate().translationY(0)
                            .setDuration(400)
                            .setListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    bottomPager.animate().translationY(0)
                                            .setDuration(400);
                                    time.extend(new ExtendedFloatingActionButton.OnChangedCallback() {
                                        @Override
                                        public void onExtended(ExtendedFloatingActionButton extendedFab) {
                                            time.show();
                                            super.onExtended(extendedFab);
                                        }
                                    });
                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {

                                }
                            });
                }
            }, 600);

            areOverlaysShown = true;
        }
    }

    public void skipCurrentLevel() {
        Level update = currentCouple.getLevels().get(currentCouple.getCurrentLevel() - 1);
        update.setSkipped(true);
        ArrayList<Level> levels = currentCouple.getLevels();
        levels.set(currentCouple.getCurrentLevel() - 1, update);
        currentCouple.setLevels(levels);
        currentCouple.setCurrentLevel(currentCouple.getCurrentLevel() + 1);

        ViewUtils.showProgressDialog(this, "Skipping level...");

        couplesRef.child(currentCouple.getId()).setValue(currentCouple).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                setCurrentCouple(currentCouple);
                ViewUtils.removeDialog();
            }
        });

    }

    public boolean checkDistance() {

        return LocationManager.distance(levelLatLng, map.getMyLocation().getLatitude(), map.getMyLocation().getLongitude());
    }

}
