package in.krharsh17.programmersdate.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.transition.Slide;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import in.krharsh17.programmersdate.R;
import in.krharsh17.programmersdate.events.QRActivity;
import in.krharsh17.programmersdate.home.bottompager.BottomPagerAdapter;

public class MainActivity extends AppCompatActivity {

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

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(MainActivity.this, QRActivity.class));
            }
        }, 2000);

    }

    void init() {
        map = new Map();
        bottomPager = findViewById(R.id.bottom_pager);
    }


    void run() {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.main_frame, map)
                .commit();
        setupBottomPager();
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
}
