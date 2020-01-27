package in.krharsh17.programmersdate.events;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.aar.tapholdupbutton.TapHoldUpButton;

import java.util.ArrayList;
import java.util.Locale;

import in.krharsh17.programmersdate.Constants;
import in.krharsh17.programmersdate.R;
import in.krharsh17.programmersdate.ViewUtils;
import in.krharsh17.programmersdate.home.managers.CoupleManager;
import in.krharsh17.programmersdate.models.Couple;
import in.krharsh17.programmersdate.models.Level;

public class AudioActivity extends AppCompatActivity implements Constants {

    String actualTwister;
    String detectedTwister;
    String coupleId;
    int currentlevel;
    TextView twister;
    TapHoldUpButton startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);
        init();
        setup();
    }

    void init() {
        startButton = findViewById(R.id.start_button);
        twister = findViewById(R.id.twister);
    }

    void setup() {
        CoupleManager coupleManager = new CoupleManager(this);
        coupleManager.getCouple().setOnFetchedListener(new CoupleManager.OnFetchedListener() {
            @Override
            public void onCoupleFetched(Couple couple) {
                coupleId = couple.getId();
                currentlevel = couple.getCurrentLevel();
                Level level = couple.getLevels().get(currentlevel - 1);
                actualTwister = level.getAudioValue();
                twister.setText(actualTwister);
                Log.i(TAG, "onCoupleFetched: " + actualTwister);
                ViewUtils.showToast(AudioActivity.this, "Tap & hold the button to begin", ViewUtils.DURATION_SHORT);
            }

            @Override
            public void onErrorOccured(String message) {

            }
        });

        startButton.setOnButtonClickListener(new TapHoldUpButton.OnButtonClickListener() {
            @Override
            public void onLongHoldStart(View v) {
                startListening();
            }

            @Override
            public void onLongHoldEnd(View v) {

            }

            @Override
            public void onClick(View v) {
                ViewUtils.showToast(AudioActivity.this, "Tap & hold the button to begin", ViewUtils.DURATION_SHORT);
            }
        });
    }

    private void startListening() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Start when you're ready");
        try {
            startActivityForResult(intent, 100);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "Sorry your device is not supported",
                    Toast.LENGTH_SHORT).show();
        }
        startButton.resetLongHold();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 100: {
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    detectedTwister = result.get(0).toString().toLowerCase();
                    if (detectedTwister.equals(actualTwister)) {
                        levelSuccess();
                    } else {
                        ViewUtils.showToast(AudioActivity.this, "Try again", ViewUtils.DURATION_LONG);
                    }

                }
                break;
            }
        }
    }

    public void levelSuccess() {
        couplesRef.child(coupleId).child("currentLevel").setValue(currentlevel + 1);
        ViewUtils.showToast(getApplicationContext(), "Level completed successfully", ViewUtils.DURATION_LONG);
        onBackPressed();
    }
}
