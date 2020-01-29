package in.krharsh17.programmersdate.events;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.storage.FirebaseStorage;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import in.krharsh17.programmersdate.R;
import in.krharsh17.programmersdate.ViewUtils;
import in.krharsh17.programmersdate.home.MainActivity;
import in.krharsh17.programmersdate.home.managers.CoupleManager;
import in.krharsh17.programmersdate.models.Couple;
import in.krharsh17.programmersdate.models.Level;

import static in.krharsh17.programmersdate.Constants.couplesRef;

public class AudioActivity extends AppCompatActivity {

    Button audioButton;
    TextView speechText;
    String actualTwister;
    String detectedTwister;
    String coupleId;
    int currentlevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);
        init();
        setup();
    }

    void init(){
        audioButton = findViewById(R.id.audio_button);
        speechText = findViewById(R.id.speech_text);
    }

    void setup(){
        CoupleManager coupleManager = new CoupleManager(this);
        coupleManager.getCouple().setOnFetchedListener(new CoupleManager.OnFetchedListener() {
            @Override
            public void onCoupleFetched(Couple couple) {
                coupleId = couple.getId();
                currentlevel = couple.getCurrentLevel();
                Level level =  couple.getLevels().get(currentlevel-1);
                actualTwister = level.getAudioValue();
                for(int i=0;i<9;i++){
                    actualTwister = actualTwister + " " + level.getAudioValue();
                }
            }

            @Override
            public void onErrorOccured(String message) {

            }
        });


        audioButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Need to speak");
                try {
                    startActivityForResult(intent, 100);
                } catch (ActivityNotFoundException a) {
                    Toast.makeText(getApplicationContext(),
                            "Sorry your device not supported",
                            Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 100: {
                if (resultCode == RESULT_OK && data!=null) {
                    ArrayList result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    speechText.setText(result.get(0).toString().toLowerCase());
                    detectedTwister = result.get(0).toString().toLowerCase();
                    if(detectedTwister.equals(actualTwister)){
                        levelSuccess();
                    }

                }
                break;
            }
        }
    }

    public void levelSuccess(){
        couplesRef.child(coupleId).child("currentLevel").setValue(currentlevel+1);
        ViewUtils.showToast(this,"Level completed successfully",ViewUtils.DURATION_LONG);
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AudioActivity.this, MainActivity.class);
        startActivity(intent);
        AudioActivity.this.finish();
    }
}
