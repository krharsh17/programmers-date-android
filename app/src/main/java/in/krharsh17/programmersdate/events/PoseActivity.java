package in.krharsh17.programmersdate.events;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Pair;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import in.krharsh17.programmersdate.Constants;
import in.krharsh17.programmersdate.R;
import in.krharsh17.programmersdate.ViewUtils;

import static java.lang.Math.abs;

public class PoseActivity extends AppCompatActivity implements Constants {

    private static final int REQUEST_CAMERA_PERMISSION = 35;
    private static SparseIntArray ORIENTATIONS = new SparseIntArray();
    private static String FRAGMENT_DIALOG = "dialog";

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    private Pair<BodyPart, BodyPart>[] bodyJoints = new Pair[]{
            new Pair(BodyPart.LEFT_WRIST, BodyPart.LEFT_ELBOW),
            new Pair(BodyPart.LEFT_ELBOW, BodyPart.LEFT_SHOULDER),
            new Pair(BodyPart.LEFT_SHOULDER, BodyPart.RIGHT_SHOULDER),
            new Pair(BodyPart.RIGHT_SHOULDER, BodyPart.RIGHT_ELBOW),
            new Pair(BodyPart.RIGHT_ELBOW, BodyPart.RIGHT_WRIST),
            new Pair(BodyPart.LEFT_SHOULDER, BodyPart.LEFT_HIP),
            new Pair(BodyPart.LEFT_HIP, BodyPart.RIGHT_HIP),
            new Pair(BodyPart.RIGHT_HIP, BodyPart.RIGHT_SHOULDER),
            new Pair(BodyPart.LEFT_HIP, BodyPart.LEFT_KNEE),
            new Pair(BodyPart.LEFT_KNEE, BodyPart.LEFT_ANKLE),
            new Pair(BodyPart.RIGHT_HIP, BodyPart.RIGHT_KNEE),
            new Pair(BodyPart.RIGHT_KNEE, BodyPart.RIGHT_ANKLE)
    };
    private double minConfidence = 0.5;
    private float circleRadius = 8.0f;
    private Paint paint = new Paint();
    private int PREVIEW_WIDTH = 640;
    private int PREVIEW_HEIGHT = 480;
    private Posenet posenet;
    private String cameraId = null;
    private SurfaceView surfaceView = null;
    private CameraCaptureSession captureSession = null;
    private CameraDevice cameraDevice = null;
    private Size previewSize = null;
    private int previewWidth = 0;
    private int previewHeight = 0;
    private int frameCounter = 0;
    private int[] rgbBytes;
    private byte[][] yuvBytes = new byte[3][];
    private HandlerThread backgroundThread;
    private Handler backgroundHandler;
    private ImageReader imageReader;
    private CaptureRequest.Builder previewRequestBuilder;
    private CaptureRequest previewRequest;
    private Semaphore cameraOpenCloseLock = new Semaphore(1);
    private boolean flashSupported = false;
    private int sensorOrientation;
    private SurfaceHolder surfaceHolder;
    /**
     * A [CameraCaptureSession.CaptureCallback] that handles events related to JPEG capture.
     */
    private CameraCaptureSession.CaptureCallback captureCallback = new CameraCaptureSession.CaptureCallback() {

        @Override
        public void onCaptureProgressed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureResult partialResult) {
            super.onCaptureProgressed(session, request, partialResult);
        }

        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
        }
    };
    /**
     * A [OnImageAvailableListener] to receive frames as they are available.
     */
    private ImageReader.OnImageAvailableListener imageAvailableListener = new ImageReader.OnImageAvailableListener() {


        @Override
        public void onImageAvailable(ImageReader reader) {
            if (previewWidth == 0 || previewHeight == 0) {
                return;
            }

            Image image = imageReader.acquireLatestImage();
            if (image != null)
                fillBytes(image.getPlanes(), yuvBytes);

            Log.i(TAG, "onImageAvailable: ");
            frameCounter = (frameCounter + 1) % 3;
            if (frameCounter == 0) {

                Log.i(TAG, "onImageAvailable: in");


                ImageUtils.convertYUV420ToARGB8888(
                        yuvBytes[0],
                        yuvBytes[1],
                        yuvBytes[2],
                        previewWidth,
                        previewHeight,
                        image.getPlanes()[0].getRowStride(),
                        image.getPlanes()[1].getRowStride(),
                        image.getPlanes()[1].getPixelStride(),
                        rgbBytes);

                final Bitmap imageBitmap = Bitmap.createBitmap(rgbBytes, previewWidth, previewHeight, Bitmap.Config.ARGB_8888);
                Matrix rotateMatrix = new Matrix();
                rotateMatrix.postRotate(90.0f);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((ImageView) findViewById(R.id.preview)).setImageBitmap(null);
                        ((ImageView) findViewById(R.id.preview)).setImageBitmap(imageBitmap);
                        Log.i(TAG, "run: ");
                    }
                });


                Bitmap rotatedBitmap = Bitmap.createBitmap(imageBitmap, 0, 0, previewWidth, previewHeight, rotateMatrix, true);
                try {
                    processImage(rotatedBitmap);
                } catch (Exception ignored) {
                    Log.i(TAG, "onImageAvailable: " + ignored);
                }
            }

            image.close();

        }
    };
    private CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {

        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            cameraOpenCloseLock.release();
            PoseActivity.this.cameraDevice = camera;
            createCameraPreviewSession();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            cameraOpenCloseLock.release();
            cameraDevice.close();
            PoseActivity.this.cameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            onDisconnected(camera);
            PoseActivity.this.finish();
        }

    };

    @Override
    protected void onResume() {
        super.onResume();
        startBackgroundThread();
    }

    @Override
    protected void onStart() {
        super.onStart();
        openCamera();
        posenet = new Posenet(this);
    }

    @Override
    protected void onPause() {
        closeCamera();
        stopBackgroundThread();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            posenet.close();
        } catch (Exception e) {
            Log.i(TAG, "onDestroy: " + e);
        }
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
    }

    private void setUpCameraOutputs() {
        CameraManager cameraManager = (CameraManager) this.getSystemService(CAMERA_SERVICE);
        try {
            for (String cameraId : cameraManager.getCameraIdList()) {
                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
                int cameraDirection = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (cameraDirection == CameraCharacteristics.LENS_FACING_FRONT) {
                    continue;
                }
                previewSize = new Size(PREVIEW_WIDTH, PREVIEW_HEIGHT);
                imageReader = ImageReader.newInstance(PREVIEW_WIDTH, PREVIEW_HEIGHT, ImageFormat.YUV_420_888, 2);

                sensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
                previewHeight = previewSize.getHeight();
                previewWidth = previewSize.getWidth();
                rgbBytes = new int[previewWidth * previewHeight];
                flashSupported = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                this.cameraId = cameraId;
                return;
            }
        } catch (Exception ignored) {
            Log.i(TAG, "setUpCameraOutputs: " + ignored);
        }
    }

    private void openCamera() {
        int permissionCamera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (permissionCamera != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermission();
        }
        setUpCameraOutputs();
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            // Wait for camera to open - 2.5 seconds is sufficient
            if (!cameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw new RuntimeException("Time out waiting to lock camera opening.");
            }
            manager.openCamera(cameraId, stateCallback, backgroundHandler);
        } catch (CameraAccessException ignored) {
            Log.i(TAG, "openCamera: " + ignored);
        } catch (InterruptedException ignored) {
            Log.i(TAG, "openCamera: " + ignored);
        }
    }

    private void closeCamera() {
        if (captureSession == null) {
            return;
        }

        try {
            cameraOpenCloseLock.acquire();
            captureSession.close();
            captureSession = null;
            cameraDevice.close();
            cameraDevice = null;
            imageReader.close();
            imageReader = null;
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera closing.", e);
        } finally {
            cameraOpenCloseLock.release();
        }
    }

    /**
     * Starts a background thread and its [Handler].
     */
    private void startBackgroundThread() {
        backgroundThread = new HandlerThread("imageAvailableListener");
        backgroundThread.start();
        backgroundHandler = new Handler(backgroundThread.getLooper());
    }

    /**
     * Stops the background thread and its [Handler].
     */
    private void stopBackgroundThread() {
        backgroundThread.quitSafely();
        try {
            backgroundThread.join();
            backgroundThread = null;
            backgroundHandler = null;
        } catch (InterruptedException ignored) {
            Log.i(TAG, "stopBackgroundThread: " + ignored);
        }
    }

    /**
     * Fill the yuvBytes with data from image planes.
     */
    private void fillBytes(Image.Plane[] planes, byte[][] yuvBytes) {
        // Row stride is the total number of bytes occupied in memory by a row of an image.
        // Because of the variable row stride it's not possible to know in
        // advance the actual necessary dimensions of the yuv planes.
        for (int i = 0; i < planes.length; i++) {
            ByteBuffer buffer = planes[i].getBuffer();
            if (yuvBytes[i] == null) {
                yuvBytes[i] = new byte[buffer.capacity()];
            }
            buffer.get(yuvBytes[i]);
        }
    }

    private Bitmap cropBitmap(Bitmap bitmap) {
        float bitmapRatio = bitmap.getHeight() / bitmap.getWidth();
        float modelInputRatio = MODEL_HEIGHT / MODEL_WIDTH;
        Bitmap croppedBitmap = bitmap;
        double maxDifference = 1e-5;

        if (abs(modelInputRatio - bitmapRatio) < maxDifference) {
            return croppedBitmap;
        } else if (modelInputRatio < bitmapRatio) {
            float cropHeight = bitmap.getHeight() - (bitmap.getWidth() / modelInputRatio);
            croppedBitmap = Bitmap.createBitmap(bitmap, 0, Math.round(cropHeight / 2), bitmap.getWidth(), Math.round(bitmap.getHeight() - cropHeight));
        } else {
            float cropWidth = bitmap.getWidth() - (bitmap.getHeight() * modelInputRatio);
            croppedBitmap = Bitmap.createBitmap(bitmap, Math.round(cropWidth / 2), 0, Math.round(bitmap.getWidth() - cropWidth), bitmap.getHeight());
        }
        return croppedBitmap;
    }

    private void setPaint() {
        paint.setColor(Color.RED);
        paint.setTextSize(80.0f);
        paint.setStrokeWidth(8f);
    }

    private void draw(Canvas canvas, Person person, Bitmap bitmap) {
        if (canvas != null) {
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            int screenWidth, screenHeight, left, right, top, bottom;
//            if (canvas.getHeight() > canvas.getWidth()) {
//                screenWidth = canvas.getWidth();
//                screenHeight = canvas.getWidth();
//                left = 0;
//                top = (canvas.getHeight() - canvas.getWidth()) / 2;
//            } else {
//                screenWidth = canvas.getHeight();
//                screenHeight = canvas.getHeight();
//                left = (canvas.getHeight() - canvas.getWidth()) / 2;
//                top = 0;
//            }
            screenHeight = canvas.getHeight();
            screenWidth = canvas.getWidth();
            left = 0;
            top = 0;
            right = left + screenWidth;
            bottom = top + screenHeight;

            setPaint();
            canvas.drawBitmap(bitmap, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()), new Rect(left, top, right, bottom), paint);
            float widthRatio = (float) screenWidth / (float) MODEL_WIDTH;
            float heightRatio = (float) screenHeight / (float) MODEL_HEIGHT;

            for (KeyPoint k : person.keyPoints) {
                if (k.score > minConfidence) {
                    Position position = k.position;
                    float adjustedX = position.x * widthRatio * left;
                    float adjustedY = position.y * heightRatio * top;
                    canvas.drawCircle(adjustedX, adjustedY, circleRadius, paint);
                }
                Log.i(TAG, "draw: " + k.score);
            }


            for (Pair<BodyPart, BodyPart> line : bodyJoints) {
                if (person.keyPoints[line.first.ordinal()].score > minConfidence && person.keyPoints[line.second.ordinal()].score > minConfidence) {
                    canvas.drawLine(
                            person.keyPoints[line.first.ordinal()].position.x * widthRatio + left,
                            person.keyPoints[line.first.ordinal()].position.y * widthRatio + left,
                            person.keyPoints[line.second.ordinal()].position.x * widthRatio + left,
                            person.keyPoints[line.second.ordinal()].position.y * widthRatio + left,
                            paint
                    );
                    canvas.drawText(
                            "Score: " + person.score,
                            (15.0f * widthRatio),
                            (30.0f * heightRatio + bottom),
                            paint
                    );
                    canvas.drawText(
                            "Device: " + posenet.device,
                            (15.0f * widthRatio),
                            (50.0f * heightRatio + bottom),
                            paint
                    );
                    canvas.drawText(
                            "Time: " + posenet.lastInferenceTimeNanos * 1.0f / 1_000_000,
                            (15.0f * widthRatio),
                            (70.0f * heightRatio + bottom),
                            paint
                    );

                }

            }

            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    private void processImage(final Bitmap bitmap) throws Exception {
        Bitmap croppedBitmap = cropBitmap(bitmap);
        final Bitmap scaledBitmap = Bitmap.createScaledBitmap(croppedBitmap, MODEL_WIDTH, MODEL_HEIGHT, true);
        new PoseKit().processImage(bitmap).setOnResultListener(new PoseKit.OnResultListener() {
            @Override
            public void onSuccess(HashMap<Integer, Object> outputMap) {
                Log.i(TAG, "onSuccess: " + Thread.currentThread().getName());
//            Person person = posenet.estimateSinglePose(scaledBitmap);
                Person person = posenet.processOutput(bitmap, outputMap);
                Log.i(TAG, "onSuccess: " + outputMap.get(0).toString());
                Canvas canvas = surfaceHolder.lockCanvas();
                draw(canvas, person, scaledBitmap);
            }

            @Override
            public void onFailure(Exception e) {

            }
        });

    }

    private void createCameraPreviewSession() {
        try {
            imageReader = ImageReader.newInstance(previewSize.getWidth(), previewSize.getHeight(), ImageFormat.YUV_420_888, 2);
            imageReader.setOnImageAvailableListener(imageAvailableListener, backgroundHandler);
            Surface recordingSurface = imageReader.getSurface();
            previewRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            previewRequestBuilder.addTarget(recordingSurface);
            List<Surface> surfaces = new ArrayList<>();
            surfaces.add(recordingSurface);
            cameraDevice.createCaptureSession(surfaces, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    if (cameraDevice == null) return;

                    // When the session is ready, we start displaying the preview.
                    captureSession = session;
                    try {
                        // Auto focus should be continuous for camera preview.
                        previewRequestBuilder.set(
                                CaptureRequest.CONTROL_AF_MODE,
                                CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE
                        );
                        // Flash is automatically enabled when necessary.
                        setAutoFlash(previewRequestBuilder);

                        // Finally, we start displaying the camera preview.
                        previewRequest = previewRequestBuilder.build();
                        captureSession.setRepeatingRequest(
                                previewRequest,
                                captureCallback, backgroundHandler
                        );
                    } catch (CameraAccessException e) {
                        Log.e(TAG, e.toString());
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                    ViewUtils.showToast(PoseActivity.this, "Failed!", ViewUtils.DURATION_SHORT);
                }
            }, null);


        } catch (CameraAccessException ignored) {
            Log.i(TAG, "createCameraPreviewSession: " + ignored);
        }

    }

    private void setAutoFlash(CaptureRequest.Builder previewRequestBuilder) {
        if (flashSupported) {
            previewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE,
                    CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pose);

        surfaceView = findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();


    }

}
