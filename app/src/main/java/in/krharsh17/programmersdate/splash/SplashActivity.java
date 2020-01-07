package in.krharsh17.programmersdate.splash;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import in.krharsh17.programmersdate.R;
import in.krharsh17.programmersdate.SharedPrefManager;
import in.krharsh17.programmersdate.ViewUtils;
import in.krharsh17.programmersdate.events.LogoActivity;
import in.krharsh17.programmersdate.home.MainActivity;
import in.krharsh17.programmersdate.home.managers.CoupleManager;

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
        parent = findViewById(R.id.root);
        next.setOnClickListener(this);

        CoupleManager.createGame(this);
    }

    private void splash() {
        logo.animate().alpha(1).setDuration(600).setStartDelay(400);
    }

    @SuppressLint("RestrictedApi")
    private void run() {
        if (SharedPrefManager.isLoggedIn(SplashActivity.this)) {
            proceedToHome();
        } else {
            roll.setVisibility(View.VISIBLE);
            next.setVisibility(View.VISIBLE);
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