package in.krharsh17.programmersdate.home;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.transition.Slide;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;

import in.krharsh17.programmersdate.R;
import in.krharsh17.programmersdate.home.bottompager.BottomPagerAdapter;
import in.krharsh17.programmersdate.home.managers.LevelManager;
import in.krharsh17.programmersdate.models.Level;

public class MainActivity extends AppCompatActivity {

    public RecyclerView levelRecycler;
    LinearLayoutManager linearLayoutManagerThree;
    RecyclerView.SmoothScroller smoothScroller;

    boolean appRunning = false;

    Map map;
    ViewPager bottomPager;
    private boolean areOverlaysShown = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setAnimation();
        setContentView(R.layout.activity_main);
        init();
        run();

    }

    void init() {
        appRunning = true;
        map = new Map();
        bottomPager = findViewById(R.id.bottom_pager);
        levelRecycler = findViewById(R.id.levels_recycler);
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
    }


    void run() {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.main_frame, map)
                .commit();
        setupBottomPager();

        ArrayList<Level> levels = new ArrayList<>();
        levels.add(new Level(1, "POSE"));
        levels.add(new Level(2, "QR"));
        levels.add(new Level(3, "BAR"));
        levels.add(new Level(4, "POSE"));
        levels.add(new Level(5, "LOGO"));
        levels.add(new Level(6, "LOGO"));
        smoothScroller.setTargetPosition(3);
        levelRecycler.setLayoutManager(linearLayoutManagerThree);
        levelRecycler.setHasFixedSize(true);
        levelRecycler.setLayoutFrozen(true);
        levelRecycler.setAdapter(new LevelsAdapter(this, levels, 3));
        linearLayoutManagerThree.startSmoothScroll(smoothScroller);
        checkCurrentPosition();

        LevelManager.createGame(this);
//        Couple couple = new Couple();
//        couple.setId("c1");
//        couple.setPlayer1Name("Kumar Harsh");
//        couple.setPlayer2Name("Parth Sharma");
//        couple.setPlayer1Roll(1806130);
//        couple.setPlayer2Roll(1806172);
//        couplesRef.child(couple.getId()).setValue(couple);
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

    public void setAnimation() {
        Slide slide = new Slide();
        slide.setSlideEdge(Gravity.LEFT);
        slide.setDuration(400);
        slide.setInterpolator(new DecelerateInterpolator());
        getWindow().setExitTransition(slide);
        getWindow().setEnterTransition(slide);
    }

    public void checkCurrentPosition() {
        Runnable r = new Runnable() {
            public void run() {
                while (appRunning) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            smoothScroller.setTargetPosition(3);
                            linearLayoutManagerThree.startSmoothScroll(smoothScroller);
                        }
                    });
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
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
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            areOverlaysShown = false;
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
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
                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    areOverlaysShown = true;
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
        }
    }
}
