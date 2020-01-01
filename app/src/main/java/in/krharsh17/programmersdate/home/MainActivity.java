package in.krharsh17.programmersdate.home;

import android.os.Bundle;
import android.transition.Slide;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;

import androidx.appcompat.app.AppCompatActivity;

import in.krharsh17.programmersdate.R;

public class MainActivity extends AppCompatActivity {

    Map map;

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
    }

    void run() {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.main_frame, map)
                .commit();
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
