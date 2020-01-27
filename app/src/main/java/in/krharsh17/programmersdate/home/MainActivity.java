package in.krharsh17.programmersdate.home;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import in.krharsh17.programmersdate.Constants;
import in.krharsh17.programmersdate.R;
import in.krharsh17.programmersdate.SharedPrefManager;
import in.krharsh17.programmersdate.ViewUtils;
import in.krharsh17.programmersdate.events.PoseActivity;
import in.krharsh17.programmersdate.home.bottompager.BottomPagerAdapter;
import in.krharsh17.programmersdate.home.managers.CoupleManager;
import in.krharsh17.programmersdate.models.Couple;
import in.krharsh17.programmersdate.models.Level;

public class MainActivity extends AppCompatActivity implements Constants {

    public RecyclerView levelRecycler;
    LinearLayoutManager linearLayoutManagerThree;
    RecyclerView.SmoothScroller smoothScroller;
    Couple currentCouple;

    int currentLevel;

    boolean appRunning = false;

    Map map;
    ViewPager bottomPager;
    private boolean areOverlaysShown = true;

    ArrayList<LatLng> gameLocations = new ArrayList<>();

    ExtendedFloatingActionButton time;

    String coupleId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        init();
        run();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(MainActivity.this, PoseActivity.class));
            }
        }, 2000);

    }

    void init() {
        appRunning = true;
        map = new Map();
        bottomPager = findViewById(R.id.bottom_pager);
        levelRecycler = findViewById(R.id.levels_recycler);
        time = findViewById(R.id.time_remaining);
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
                    checkCurrentPosition();
                    attachCoupleListener();
                    ViewUtils.removeDialog();
                }

                @Override
                public void onErrorOccured(String message) {
                    ViewUtils.showToast(MainActivity.this, message, ViewUtils.DURATION_SHORT);
                }
            });

        } else {
            CoupleManager.syncWithServer(this);
            ViewUtils.showToast(MainActivity.this, "Some error occured!", ViewUtils.DURATION_SHORT);
        }


        levelRecycler.setLayoutManager(linearLayoutManagerThree);
        levelRecycler.setHasFixedSize(true);
        levelRecycler.setLayoutFrozen(true);
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
    }

    public void setCurrentCouple(Couple currentCouple) {
        this.currentCouple = currentCouple;
        setLevelRecycler();
        setupBottomPager();
        bottomPager.setCurrentItem(currentCouple.getCurrentLevel() - 1, true);
        if (currentCouple.getCurrentLevel() - currentLevel == 1) {
            refreshMap();
        }
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
        map.clearLandmarks();
        Level currentLevel = currentCouple.getLevels().get(currentCouple.getCurrentLevel() - 1);
        if (currentLevel.getLocations() != null)
            for (String location : currentLevel.getLocations()) {
                switch (location) {
                    case sacBuildingText:
                        map.addLandmark(sacBuilding, R.drawable.marker_sac);
                        gameLocations.add(sacBuilding);
                        break;
                    case cafeteriaText:
                        map.addLandmark(cafeteria, R.drawable.marker_nescafe);
                        gameLocations.add(cafeteria);
                        break;
                    case mainBuildingText:
                        map.addLandmark(mainBuilding, R.drawable.marker_main_building);
                        gameLocations.add(mainBuilding);
                        break;
                    case civilDeptText:
                        map.addLandmark(civilDept, R.drawable.marker_main_building);
                        gameLocations.add(civilDept);
                        break;
                    case computerCentreText:
//                    map.addLandmark(computerCentre, R.drawable.marker);
//                    gameLocations.add(computerCentre);
                        break;
                    case tennisCourtText:
                        map.addLandmark(tennisCourt, R.drawable.marker_tennis);
                        gameLocations.add(tennisCourt);
                        break;
                    case directorBungalowText:
//                    map.addLandmark(directorBungalow, R.drawable.marker);
//                    gameLocations.add(directorBungalow);
                        break;
                    case guestHouseText:
//                    map.addLandmark(guestHouse, R.drawable.marker);
//                    gameLocations.add(guestHouse);
                        break;
                    case mechanicalWorkshopText:
//                    map.addLandmark(mechanicalWorkshop, R.drawable.marker);
//                    gameLocations.add(mechanicalWorkshop);
                        break;
                    case gangaHostelText:
                    case cseDeptText:
                    case kosiHostelText:
                    case CWRSText:
                    case libraryText:
                    case canteenGopalJiText:
                    case canteenShuklaJiText:
                    case electricalDeptText:
                    case mechanicalDeptText:
                    case newElectricalDeptText:
                    case electronicsDeptText:
                    case physicsDeptText:
                    case groundText:
                    case soneAHostelText:
                    case soneBHostelText:
                    case miniAuditoriumText:
                }
            }
    }

    void setLevelRecycler() {
        if (currentCouple == null)
            return;
        if (currentCouple.getLevels() != null) {
            if (levelRecycler.getAdapter() == null)
                levelRecycler.setAdapter(new LevelsAdapter(MainActivity.this, currentCouple.getLevels(), currentCouple.getCurrentLevel()));
            else
                ((LevelsAdapter) levelRecycler.getAdapter()).levelSetter(currentCouple.getCurrentLevel());
        }
        smoothScroller.setTargetPosition(currentCouple.getCurrentLevel() - 1);
        linearLayoutManagerThree.startSmoothScroll(smoothScroller);
    }

    @SuppressLint("ClickableViewAccessibility")
    void setupBottomPager() {


        bottomPager.setAdapter(new BottomPagerAdapter(getSupportFragmentManager()));


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

    void setupClock() {

    }

    @Override
    protected void onPause() {
        super.onPause();
        map.disableGPS();
        appRunning = false;
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
}
