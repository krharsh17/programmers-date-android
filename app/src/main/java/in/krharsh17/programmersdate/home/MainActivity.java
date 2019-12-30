package in.krharsh17.programmersdate.home;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.transition.Slide;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;

import in.krharsh17.programmersdate.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setAnimation();
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.main_frame, new MapFragment())
                .commit();
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
