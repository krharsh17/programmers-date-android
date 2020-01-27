package in.krharsh17.programmersdate.events;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.BarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import in.krharsh17.programmersdate.R;
import in.krharsh17.programmersdate.ViewUtils;
import in.krharsh17.programmersdate.home.MainActivity;
import in.krharsh17.programmersdate.home.managers.CoupleManager;
import in.krharsh17.programmersdate.models.Couple;
import in.krharsh17.programmersdate.models.Level;

import static in.krharsh17.programmersdate.Constants.couplesRef;

public class QRActivity extends AppCompatActivity {
    public BarcodeView barcodeView;
    private BeepManager beepManager;
    private String lastText;
    private TextView status;
    private ImageView crosshair;
    private int currentLevel;
    private String codeValueActual,coupleId;
    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            lastText = result.getText();

            Log.i("resulthere",lastText);
            if(lastText.equals(codeValueActual)){
                levelSuccess();
            }
            //handle result here

            beepManager.setVibrateEnabled(true);
            beepManager.setBeepEnabled(true);
//            beepManager.playBeepSoundAndVibrate();
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);
        status = findViewById(R.id.scanning_status);
        crosshair = findViewById(R.id.crosshair);
        Collection<BarcodeFormat> formats = Arrays.asList(BarcodeFormat.QR_CODE, BarcodeFormat.CODE_39, BarcodeFormat.CODE_93, BarcodeFormat.CODE_128, BarcodeFormat.CODABAR, BarcodeFormat.ITF, BarcodeFormat.EAN_8, BarcodeFormat.EAN_13, BarcodeFormat.UPC_A, BarcodeFormat.UPC_E, BarcodeFormat.UPC_EAN_EXTENSION);
        barcodeView = findViewById(R.id.scan_result_image);
        barcodeView.setDecoderFactory(new DefaultDecoderFactory(formats, null, null));
        barcodeView.decodeContinuous(callback);
        beepManager = new BeepManager(this);
        CoupleManager coupleManager = new CoupleManager(this);
        coupleManager.getCouple().setOnFetchedListener(new CoupleManager.OnFetchedListener() {
            @Override
            public void onCoupleFetched(Couple couple) {
                coupleId = couple.getId();
                currentLevel = couple.getCurrentLevel();
                Level level =  couple.getLevels().get(currentLevel-1);
                if(level.getTaskType().equals("QR")){
                    codeValueActual = level.getQrValue();
                }else if(level.getTaskType().equals("BAR")){
                    codeValueActual = level.getBarValue();
                }
            }

            @Override
            public void onErrorOccured(String message) {

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        barcodeView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        barcodeView.pause();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    public void levelSuccess(){
        couplesRef.child(coupleId).child("currentLevel").setValue(currentLevel+1);
        ViewUtils.showToast(this,"Level completed successfully",ViewUtils.DURATION_LONG);
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(QRActivity.this, MainActivity.class);
        startActivity(intent);
        QRActivity.this.finish();
    }
}