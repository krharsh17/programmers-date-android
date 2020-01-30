package in.krharsh17.programmersdate.events;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.storage.FirebaseStorage;

import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import in.krharsh17.programmersdate.R;
import in.krharsh17.programmersdate.ViewUtils;
import in.krharsh17.programmersdate.events.posenet.PosenetActivity;
import in.krharsh17.programmersdate.home.managers.CoupleManager;
import in.krharsh17.programmersdate.models.Couple;
import in.krharsh17.programmersdate.models.Level;

import static in.krharsh17.programmersdate.Constants.couplesRef;
import static in.krharsh17.programmersdate.Constants.poseLowerMatchCriteria;
import static in.krharsh17.programmersdate.Constants.poseTotalChecks;
import static in.krharsh17.programmersdate.Constants.poseUpperMatchCriteria;
import static in.krharsh17.programmersdate.Constants.runtime;
import static in.krharsh17.programmersdate.events.LogoActivity.filterMatchesByDistance;

public class PoseActivity extends AppCompatActivity {

    PosenetActivity posenetActivity;
    Canvas canvas;
    Bitmap bitmap;
    int currentlevel;
    String coupleId;
    Level level;
    boolean isSuccess = false;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pose);

        bitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        CoupleManager coupleManager = new CoupleManager(this);
        coupleManager.getCouple().setOnFetchedListener(new CoupleManager.OnFetchedListener() {
            @Override
            public void onCoupleFetched(Couple couple) {
                currentlevel = couple.getCurrentLevel();
                coupleId = couple.getId();
                Log.i("TAG", "onCoupleFetched: " + currentlevel);
                level = couple.getLevels().get(currentlevel-1);
                String root = Environment.getExternalStorageDirectory().toString();
                File myDir = new File(root);
                myDir.mkdirs();
                String logofile = "Pose-match.jpg";
                File lfile = new File(myDir, logofile);
                String imagename = level.getPoseValue();
                FirebaseStorage.getInstance().getReference().child("poses").child(imagename).getFile(lfile);
                ViewUtils.showToast(PoseActivity.this, "Scanning..", ViewUtils.DURATION_SHORT);
                ViewUtils.removeDialog();
            }

            @Override
            public void onErrorOccured(String message) {

            }
        });

        posenetActivity = new PosenetActivity();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.camera_preview_pose, posenetActivity)
                .commit();

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                if (!isSuccess) {
                    ViewUtils.showToast(PoseActivity.this, "Scanning timed out", ViewUtils.DURATION_LONG);
                    onBackPressed();
                }

            }
        }, runtime);

        startMatching();


        FrameLayout preview = findViewById(R.id.camera_preview_pose);

    }

    void matchPose(){
//        this.canvas = posenetActivity.getCanvas();
//        canvas.setBitmap(bitmap);
        this.bitmap = posenetActivity.getBitmap();
        Log.i("bitmaptest",BitMapToString(this.bitmap));
        Matrix matrix = new Matrix();
        matrix.postRotate(0);
        Bitmap bmprotated =  Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root);
        myDir.mkdirs();
        String fname = "Pose-test" + ".jpg";
        File file = new File(myDir, fname);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            bmprotated.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            File sdCard = Environment.getExternalStorageDirectory();
            String path1, path2;
            path1 = sdCard.getAbsolutePath() + "/Pose-test.jpg";
            path2 = sdCard.getAbsolutePath() + "/Pose-match.jpg";

            FeatureDetector detector = FeatureDetector.create(FeatureDetector.ORB);
            DescriptorExtractor extractor = DescriptorExtractor.create(DescriptorExtractor.BRISK);
            DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);
            Mat img1 = Imgcodecs.imread(path1);
            Mat img2 = Imgcodecs.imread(path2);
            if(img1!=null&&img2!=null){
                Log.i("imageaayikya", "aagyi");
            }
            Mat descriptors1 = new Mat();
            MatOfKeyPoint keypoints1 = new MatOfKeyPoint();
            detector.detect(img1, keypoints1);
            extractor.compute(img1, keypoints1, descriptors1);
            Mat descriptors2 = new Mat();
            MatOfKeyPoint keypoints2 = new MatOfKeyPoint();
            detector.detect(img2, keypoints2);
            extractor.compute(img2, keypoints2, descriptors2);
            MatOfDMatch matches = new MatOfDMatch();
            matcher.match(descriptors1,descriptors2,matches);
            MatOfDMatch filtered = filterMatchesByDistance(matches);
            int total = (int) matches.size().height;
            int Match= (int) filtered.size().height;
            Log.i("itnaMatchKiya", "total: " + total + " Match: "+Match);
            if(total>poseTotalChecks){
                if(Match>poseUpperMatchCriteria){
                    if (!isSuccess) {
                        levelSuccess();
                    }
                }
            }else{
                if(Match>poseLowerMatchCriteria){
                    if (!isSuccess) {
                        levelSuccess();
                    }                        }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static {
        System.loadLibrary("opencv_java3");
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void levelSuccess(){
        isSuccess = true;
        couplesRef.child(coupleId).child("currentLevel").setValue(currentlevel+1);
        ViewUtils.showToast(this,"Level completed successfully",ViewUtils.DURATION_LONG);
        onBackPressed();
        PoseActivity.this.finish();
    }

    public void startMatching(){
        Runnable r = new Runnable() {
            public void run() {
                while (true) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!isSuccess) {
                                matchPose();
                            }
                        }
                    });
                    try {
                        Thread.sleep(80);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        new Thread(r).start();

    }

    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

}
