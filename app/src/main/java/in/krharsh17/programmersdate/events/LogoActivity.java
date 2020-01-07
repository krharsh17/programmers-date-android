package in.krharsh17.programmersdate.events;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.google.firebase.storage.FirebaseStorage;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.DMatch;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import in.krharsh17.programmersdate.Constants;
import in.krharsh17.programmersdate.R;
import in.krharsh17.programmersdate.ViewUtils;
import in.krharsh17.programmersdate.home.MainActivity;
import in.krharsh17.programmersdate.home.managers.CoupleManager;
import in.krharsh17.programmersdate.models.Couple;
import in.krharsh17.programmersdate.models.Level;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

public class LogoActivity extends AppCompatActivity implements Constants {

    private Camera mCamera;
    private CameraPreview mPreview;
    int currentlevel;
    String coupleId;
    Level level;
    boolean isSuccess = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);
        mCamera = getCameraInstance();
        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        Button captureButton = (Button) findViewById(R.id.button_capture);
        preview.addView(mPreview);
        ViewUtils.showToast(this,"Please wait",ViewUtils.DURATION_SHORT);
        startMatching();
        CoupleManager coupleManager = new CoupleManager(this);
        coupleManager.getCouple().setOnFetchedListener(new CoupleManager.OnFetchedListener() {
            @Override
            public void onCoupleFetched(Couple couple) {
                currentlevel = couple.getCurrentLevel();
                coupleId = couple.getId();
                level = couple.getLevels().get(currentlevel-1);
                String root = Environment.getExternalStorageDirectory().toString();
                File myDir = new File(root);
                myDir.mkdirs();
                String logofile = "Image-match.jpg";
                File lfile = new File(myDir, logofile);
                String imagename = level.getLogoValue();
                FirebaseStorage.getInstance().getReference().child("logos").child(imagename).getFile(lfile);
                ViewUtils.showToast(LogoActivity.this,"Scanning",ViewUtils.DURATION_SHORT);
                Handler handler = new Handler();

                handler.postDelayed(new Runnable() {
                    public void run() {
                        if (isSuccess==false) {
                            onBackPressed();
                            ViewUtils.showToast(getParent(), "Scanning timed out", ViewUtils.DURATION_LONG);
                        }

                    }
                }, runtime);
            }

            @Override
            public void onErrorOccured(String message) {

            }
        });



    }

    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            Log.i("cameraloading","not opened");
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    @Override
    protected void onPause() {
        super.onPause();
//        releaseCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mCamera = getCameraInstance();
    }

    private void releaseCamera(){
        if (mCamera != null){
            mCamera.release();
            mCamera = null;
        }
    }

    public void matchImages(){

    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(LogoActivity.this, MainActivity.class);
        startActivity(intent);
        LogoActivity.this.finish();
    }

    static {
        System.loadLibrary("opencv_java3");
    }

    static MatOfDMatch filterMatchesByDistance(MatOfDMatch matches){
        List<DMatch> matches_original = matches.toList();
        List<DMatch> matches_filtered = new ArrayList<DMatch>();

        int DIST_LIMIT = 30;
        // Check all the matches distance and if it passes add to list of filtered matches
        Log.d("DISTFILTER", "ORG SIZE:" + matches_original.size() + "");
        for (int i = 0; i < matches_original.size(); i++) {
            DMatch d = matches_original.get(i);
            if (Math.abs(d.distance) <= DIST_LIMIT) {
                matches_filtered.add(d);
            }
        }
        Log.d("DISTFILTER", "FIL SIZE:" + matches_filtered.size() + "");

        MatOfDMatch mat = new MatOfDMatch();
        mat.fromList(matches_filtered);
        return mat;
    }

    public void startMatching(){
        Runnable r = new Runnable() {
            public void run() {
                while (true) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (isSuccess==false) {
                                captureAndMatch();
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

    public void captureAndMatch(){
        mCamera.takePicture(null, null, new Camera.PictureCallback(){

            @Override
            public void onPictureTaken(byte[] bytes, Camera camera) {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                Bitmap bmprotated =  Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
                String root = Environment.getExternalStorageDirectory().toString();
                File myDir = new File(root);
                myDir.mkdirs();
                String fname = "Image-test" + ".jpg";
                File file = new File(myDir, fname);
                if (file.exists()) file.delete();
                Log.i("LOAD", "" + bmp.getByteCount());
                try {
                    FileOutputStream out = new FileOutputStream(file);
                    bmprotated.compress(Bitmap.CompressFormat.JPEG, 90, out);
                    out.flush();
                    out.close();
                    File sdCard = Environment.getExternalStorageDirectory();
                    String path1, path2;
                    path1 = sdCard.getAbsolutePath() + "/Image-match.jpg";
                    path2 = sdCard.getAbsolutePath() + "/Image-test.jpg";

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
                    if(total>totalCheckPoints){
                        if(Match>upperMatchPoints){
                            if(isSuccess==false) {
                                levelSuccess();
                            }
                        }
                    }else{
                        if(Match>lowerMatchPoints){
                            if(isSuccess==false) {
                                levelSuccess();
                            }                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    public void levelSuccess(){
        isSuccess = true;
        couplesRef.child(coupleId).child("currentLevel").setValue(currentlevel+1);
        ViewUtils.showToast(this,"Level completed successfully",ViewUtils.DURATION_LONG);
        onBackPressed();
        LogoActivity.this.finish();
    }

}