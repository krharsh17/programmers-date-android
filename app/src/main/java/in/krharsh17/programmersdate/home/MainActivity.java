package in.krharsh17.programmersdate.home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.transition.Slide;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;

import java.util.ArrayList;

import in.krharsh17.programmersdate.R;
import in.krharsh17.programmersdate.models.Level;

public class MainActivity extends AppCompatActivity {

    public RecyclerView levelRecycler;
    LinearLayoutManager linearLayoutManagerThree;
    RecyclerView.SmoothScroller smoothScroller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setAnimation();
        setContentView(R.layout.activity_main);
        assignVariables();
        setup();

//        getSupportFragmentManager().beginTransaction()
//                .add(R.id.main_frame, new MapFragment())
//                .commit();
    }

    void assignVariables(){
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
                        return 100f/displayMetrics.densityDpi;
                    }
                };
    }

    void setup(){

        ArrayList<Level> levels = new ArrayList<>();
        levels.add(new Level(1,"POSE"));
        levels.add(new Level(2,"QR"));
        levels.add(new Level(3,"BAR"));
        levels.add(new Level(4,"POSE"));
        levels.add(new Level(5,"LOGO"));
        levels.add(new Level(6,"LOGO"));
        smoothScroller.setTargetPosition(3);
        levelRecycler.setLayoutManager(linearLayoutManagerThree);
        levelRecycler.setHasFixedSize(true);
        levelRecycler.setLayoutFrozen(true);
        levelRecycler.setAdapter(new LevelsAdapter(this,levels,3));
        linearLayoutManagerThree.startSmoothScroll(smoothScroller);
        checkCurrentPosition();


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
