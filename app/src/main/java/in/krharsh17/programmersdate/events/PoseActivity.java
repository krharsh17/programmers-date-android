package in.krharsh17.programmersdate.events;

import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import in.krharsh17.programmersdate.Constants;
import in.krharsh17.programmersdate.R;

import in.krharsh17.programmersdate.events.posenet.PosenetActivity;


    PosenetActivity posenetActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pose);


        posenetActivity = new PosenetActivity();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.camera_preview_pose, posenetActivity)
                .commit();


        FrameLayout preview = findViewById(R.id.camera_preview_pose);

    }

}
