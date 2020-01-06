package in.krharsh17.programmersdate.home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
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

import java.util.ArrayList;

import in.krharsh17.programmersdate.R;
import in.krharsh17.programmersdate.events.LogoActivity;
import in.krharsh17.programmersdate.models.Level;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import in.krharsh17.programmersdate.home.bottompager.BottomPagerAdapter;

public class MainActivity extends AppCompatActivity {

    public RecyclerView levelRecycler;
    LinearLayoutManager linearLayoutManagerThree;
    RecyclerView.SmoothScroller smoothScroller;

    Map map;
    ViewPager bottomPager;

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
        map = new Map();
        bottomPager = findViewById(R.id.bottom_pager);
        levelRecycler = findViewById(R.id.levels_recycler);
        linearLayoutManagerThree = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        smoothScroller = new
                LinearSmoothScroller(this) {
                    @Override protected int getHorizontalSnapPreference() {
                        return LinearSmoothScroller.SNAP_TO_START;
                    }

                    @Override
                    protected float calculateSpeedPerPixel
                            (DisplayMetrics displayMetrics) {
                        return 200f/displayMetrics.densityDpi;
                    }
                };
    }


    void run() {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.main_frame, map)
                .commit();
        setupBottomPager();

        ArrayList<Level> levels = new ArrayList<>();
        levels.add(new Level(1,"POSE",false));
        levels.add(new Level(2,"QR",true));
        levels.add(new Level(3,"BAR",false));
        levels.add(new Level(4,"POSE",false));
        levels.add(new Level(5,"LOGO",false));
        levels.add(new Level(6,"LOGO",false));
        smoothScroller.setTargetPosition(3);
        levelRecycler.setLayoutManager(linearLayoutManagerThree);
        levelRecycler.setHasFixedSize(true);
        levelRecycler.setLayoutFrozen(true);
        levelRecycler.setAdapter(new LevelsAdapter(this,levels,3));
        linearLayoutManagerThree.startSmoothScroll(smoothScroller);
        checkCurrentPosition();
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        map.enableGPS();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        map.disableGPS();
    }

    public void setAnimation() {
        Slide slide = new Slide();
        slide.setSlideEdge(Gravity.LEFT);
        slide.setDuration(400);
        slide.setInterpolator(new DecelerateInterpolator());
        getWindow().setExitTransition(slide);
        getWindow().setEnterTransition(slide);
    }

    public void checkCurrentPosition(){
        Runnable r = new Runnable() {
            public void run() {
                while (true) {
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
}
