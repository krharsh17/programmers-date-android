package in.krharsh17.programmersdate.splash;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import in.krharsh17.programmersdate.R;
import in.krharsh17.programmersdate.SharedPrefManager;
import in.krharsh17.programmersdate.ViewUtils;
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

        ActivityCompat.requestPermissions(SplashActivity.this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},
                1);

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
        FirebaseDatabase.getInstance().getReference().child("settings").child("gameRunning").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                    boolean gameRunning = dataSnapshot.getValue(Boolean.class);
                    if (gameRunning) {
                        ActivityOptions options =
                                ActivityOptions.makeSceneTransitionAnimation(SplashActivity.this);
                        startActivity(new Intent(SplashActivity.this, MainActivity.class), options.toBundle());
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                SplashActivity.this.finish();
                            }
                        }, 2000);
                    } else {
                        ViewUtils.showToast(SplashActivity.this, "The game is not active currently!\nYour couple id " + new SharedPrefManager(SplashActivity.this).getCoupleId(), ViewUtils.DURATION_LONG);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == next.getId()) {
            if (roll.getText().length() == 7) {
                ViewUtils.showProgressDialog(SplashActivity.this, "Preparing game..");
                SharedPrefManager.setLoggedIn(SplashActivity.this, true);
                new SharedPrefManager(SplashActivity.this).setRollNumber(Long.parseLong(roll.getText().toString()));
                new CoupleManager(SplashActivity.this).syncWithServer(SplashActivity.this).setOnSyncedListener(new CoupleManager.OnSyncedListener() {
                    @Override
                    public void onComplete(boolean success) {
                        proceedToHome();
                    }
                });
            } else {
                ViewUtils.showToast(SplashActivity.this, "Roll number invalid!", ViewUtils.DURATION_SHORT);
            }
        }
    }
}