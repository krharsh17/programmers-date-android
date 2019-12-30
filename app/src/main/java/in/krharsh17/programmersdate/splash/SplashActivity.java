package in.krharsh17.programmersdate.splash;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.transition.Slide;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import in.krharsh17.programmersdate.R;
import in.krharsh17.programmersdate.SharedPrefManager;
import in.krharsh17.programmersdate.ViewUtils;
import in.krharsh17.programmersdate.home.MainActivity;

public class SplashActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView logo, logoBackground;
    FloatingActionButton next;
    TextInputEditText roll;
    ConstraintLayout parent;
    TextView heading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        init();
        splash();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SplashActivity.this.run();
            }
        }, 2000);
    }

    private void init() {
        logo = findViewById(R.id.app_logo);
        logoBackground = findViewById(R.id.app_logo_background);
        heading = findViewById(R.id.programmers_text);
        next = findViewById(R.id.splash_next);
        roll = findViewById(R.id.splash_roll);
        parent = findViewById(R.id.splash_parent);
        next.setOnClickListener(this);
    }

    private void splash() {
        logo.animate().alpha(1).setDuration(600).setStartDelay(400);
    }

    private void run() {
        if (SharedPrefManager.isLoggedIn(SplashActivity.this)) {
            proceedToHome();
        } else {
            ConstraintSet parentSet = new ConstraintSet();
            parentSet.clone(parent);

            parentSet.constrainPercentHeight(logoBackground.getId(), 0.5f);
//            parentSet.constrainPercentWidth(logoBackground.getId(), 0.5f);

            parentSet.setVerticalBias(heading.getId(), 0.15f);

            parentSet.applyTo(parent);

            roll.setVisibility(View.VISIBLE);
            next.animate().alpha(1f).setDuration(400);
        }
    }

    private void proceedToHome() {
        ActivityOptions options =
                ActivityOptions.makeSceneTransitionAnimation(this);
        startActivity(new Intent(this, MainActivity.class),options.toBundle());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SplashActivity.this.finish();
            }
        }, 2000);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == next.getId()) {
            if (roll.getText().length() == 7) {
                SharedPrefManager.setLoggedIn(SplashActivity.this, true);
                new SharedPrefManager(SplashActivity.this).setRollNumber(Long.parseLong(roll.getText().toString()));
                proceedToHome();
            } else {
                ViewUtils.showToast(SplashActivity.this, "Roll number invalid!", ViewUtils.DURATION_SHORT);
            }
        }
    }
}
