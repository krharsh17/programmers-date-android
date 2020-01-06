package in.krharsh17.programmersdate.events;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.BackgroundSubtractorKNN;
import org.opencv.video.Video;

import java.io.File;
import java.io.FileOutputStream;

import in.krharsh17.programmersdate.R;
import in.krharsh17.programmersdate.home.MainActivity;

public class PoseActivity extends AppCompatActivity {

    private Camera mCamera;
    private CameraPreview mPreview;
    private BackgroundSubtractorKNN mBGSub = Video.createBackgroundSubtractorKNN();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pose);
        Log.i("activitypose","opened");

        mCamera = getCameraInstance();
        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview_pose);
        Button captureButton = (Button) findViewById(R.id.button_capture_pose);
        ImageView poseImage = findViewById(R.id.pose_image);
        preview.addView(mPreview);

        Log.d("TAG", "onCreate: ");

        captureButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // get an image from the camera
                        mCamera.takePicture(null, null, new Camera.PictureCallback(){

                            @Override
                            public void onPictureTaken(byte[] bytes, Camera camera) {
                                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                Mat mat = new Mat();
                                Mat SubMat = new Mat();
                                Mat intRGB = new Mat();
                                Bitmap bmp32 = bmp.copy(Bitmap.Config.ARGB_8888, true);
                                Utils.bitmapToMat(bmp32, mat);
                                Imgproc.cvtColor(mat, intRGB, Imgproc.COLOR_RGBA2RGB);
                                mBGSub.apply(intRGB, SubMat);
                                Bitmap bmpfinal = Bitmap.createBitmap(SubMat.cols(), SubMat.rows(), Bitmap.Config.ARGB_8888);
                                Utils.matToBitmap(SubMat, bmpfinal);
                                String root = Environment.getExternalStorageDirectory().toString();
                                File myDir = new File(root);
                                myDir.mkdirs();
                                String fname = "Image-test" + ".jpg";
                                File file = new File(myDir, fname);
                                if (file.exists()) file.delete();
                                Log.i("LOAD", root + fname);
                                try {
                                    FileOutputStream out = new FileOutputStream(file);
                                    bmpfinal.compress(Bitmap.CompressFormat.JPEG, 90, out);
                                    out.flush();
                                    out.close();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                    }
                }
        );
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

    static {
        System.loadLibrary("opencv_java3");
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(PoseActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
