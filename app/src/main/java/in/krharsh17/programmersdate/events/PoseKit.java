package in.krharsh17.programmersdate.events;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.custom.FirebaseCustomLocalModel;
import com.google.firebase.ml.custom.FirebaseModelInputOutputOptions;
import com.google.firebase.ml.custom.FirebaseModelInputs;
import com.google.firebase.ml.custom.FirebaseModelInterpreter;
import com.google.firebase.ml.custom.FirebaseModelInterpreterOptions;
import com.google.firebase.ml.custom.FirebaseModelOutputs;

import java.util.HashMap;

import static com.google.firebase.ml.custom.FirebaseModelDataType.FLOAT32;
import static in.krharsh17.programmersdate.Constants.TAG;

/**
 * @see PoseKit
 * uses mlkit to load local tflite model
 * to be used when posenet poses problems
 **/
public class PoseKit {

    FirebaseCustomLocalModel model;
    FirebaseModelInterpreter interpreter;
    FirebaseModelInputOutputOptions inputOutputOptions;
    float[][][][] input;
    OnResultListener onResultListener;

    PoseKit() {
        loadModel();
        createInterpreter();
        prepareIOOptions();

    }

    void loadModel() {
        model = new FirebaseCustomLocalModel.Builder()
                .setAssetFilePath("models/posenet.tflite")
                .build();
    }

    void createInterpreter() {
        try {
            FirebaseModelInterpreterOptions options = new FirebaseModelInterpreterOptions.Builder(model)
                    .build();
            interpreter = FirebaseModelInterpreter.getInstance(options);
        } catch (Exception e) {
            Log.i(TAG, "createInterpreter: " + e);
        }
    }

    void prepareIOOptions() {
        try {
            inputOutputOptions =
                    new FirebaseModelInputOutputOptions.Builder()
                            .setInputFormat(0, FLOAT32, new int[]{1, 257, 257, 3})
                            .setOutputFormat(0, FLOAT32, new int[]{1, 9, 9, 17})
                            .setOutputFormat(1, FLOAT32, new int[]{1, 9, 9, 34})
                            .setOutputFormat(2, FLOAT32, new int[]{1, 9, 9, 32})
                            .setOutputFormat(3, FLOAT32, new int[]{1, 9, 9, 32})
                            .build();
        } catch (Exception e) {
            Log.i(TAG, "prepareInterpreter: " + e);
        }
    }

    void prepareImage(Bitmap bitmap) {
        bitmap = Bitmap.createScaledBitmap(bitmap, 257, 257, true);

        int batchNum = 0;
        input = new float[1][257][257][3];
        for (int x = 0; x < 224; x++) {
            for (int y = 0; y < 224; y++) {
                int pixel = bitmap.getPixel(x, y);
                // Normalize channel values to [-1.0, 1.0]. This requirement varies by
                // model. For example, some models might require values to be normalized
                // to the range [0.0, 1.0] instead.
                input[batchNum][x][y][0] = (Color.red(pixel) - 127) / 128.0f;
                input[batchNum][x][y][1] = (Color.green(pixel) - 127) / 128.0f;
                input[batchNum][x][y][2] = (Color.blue(pixel) - 127) / 128.0f;
            }
        }
    }

    PoseKit processImage(Bitmap bitmap) {
        prepareImage(bitmap);
        try {
            FirebaseModelInputs inputs = new FirebaseModelInputs.Builder()
                    .add(input)  // add() as many input arrays as your model requires
                    .build();
            interpreter.run(inputs, inputOutputOptions)
                    .addOnSuccessListener(
                            new OnSuccessListener<FirebaseModelOutputs>() {
                                @Override
                                public void onSuccess(FirebaseModelOutputs result) {
                                    if (onResultListener != null) {
                                        HashMap<Integer, Object> outputMap = new HashMap<>();
                                        outputMap.put(0, result.getOutput(0));
                                        outputMap.put(1, result.getOutput(1));
                                        outputMap.put(2, result.getOutput(2));
                                        outputMap.put(3, result.getOutput(3));
                                        onResultListener.onSuccess(outputMap);
                                    }
                                }
                            })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    if (onResultListener != null)
                                        onResultListener.onFailure(e);
                                }
                            });
        } catch (Exception e) {
            Log.i(TAG, "processImage: " + e);
        }
        return this;
    }

    public void setOnResultListener(OnResultListener onResultListener) {
        this.onResultListener = onResultListener;
    }

    public interface OnResultListener {
        void onSuccess(HashMap<Integer, Object> outputMap);

        void onFailure(Exception e);
    }
}
