package in.krharsh17.programmersdate.events;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.os.SystemClock;
import android.util.Log;
import android.util.Pair;

import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.gpu.GpuDelegate;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;

import static in.krharsh17.programmersdate.Constants.TAG;
import static java.lang.Math.exp;

enum BodyPart {
    NOSE,
    LEFT_EYE,
    RIGHT_EYE,
    LEFT_EAR,
    RIGHT_EAR,
    LEFT_SHOULDER,
    RIGHT_SHOULDER,
    LEFT_ELBOW,
    RIGHT_ELBOW,
    LEFT_WRIST,
    RIGHT_WRIST,
    LEFT_HIP,
    RIGHT_HIP,
    LEFT_KNEE,
    RIGHT_KNEE,
    LEFT_ANKLE,
    RIGHT_ANKLE
}

enum Device {
    CPU,
    NNAPI,
    GPU
}

class Position {
    int x = 0;
    int y = 0;
}

class KeyPoint {
    BodyPart bodyPart = BodyPart.NOSE;
    Position position = new Position();
    Float score = 0f;
}

class Person {
    KeyPoint[] keyPoints;
    float score = 0f;
}

public class Posenet implements AutoCloseable {
    Interpreter interpreter = null;
    GpuDelegate gpuDelegate = null;
    Context context;
    Device device;
    String filename = "models/posenet.tflite";
    long lastInferenceTimeNanos = -1;

    Posenet(Context context) {
        this.context = context;
        device = Device.CPU;
    }

    private Interpreter getInterpreter() throws IOException {
        AssetFileDescriptor fileDescriptor = context.getAssets().openFd(filename);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();

        final Interpreter.Options tfliteOptions = new Interpreter.Options();
        if (device == Device.GPU) {
            gpuDelegate = new GpuDelegate();
            tfliteOptions.addDelegate(gpuDelegate);
        } else if (device == Device.NNAPI) {
            tfliteOptions.setUseNNAPI(true);
        }


        FileChannel fileChannel = inputStream.getChannel();
        MappedByteBuffer buffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
        tfliteOptions.setNumThreads(2);

        interpreter = new Interpreter(buffer, tfliteOptions);
        return interpreter;
    }


    @Override
    public void close() throws Exception {
        if (interpreter != null)
            interpreter.close();
        interpreter = null;
        if (gpuDelegate != null)
            gpuDelegate.close();
        gpuDelegate = null;
    }

    private float sigmoid(float x) {
        return Float.parseFloat("" + (1.0 / (1.0 + exp(-x))));
    }

    private float[][][] initInputArray(Bitmap bitmap) {
        int bytesPerChannel = 4;
        int inputChannels = 3;
        int batchSize = 1;
        float[][][] array = new float[257][][];
        ByteBuffer inputBuffer = ByteBuffer.allocateDirect(batchSize * bytesPerChannel * bitmap.getHeight() * bitmap.getWidth() * inputChannels);
        inputBuffer.order(ByteOrder.nativeOrder());
        inputBuffer.rewind();
        float mean = 128.0f;
        float std = 128.0f;
        for (int i = 0; i < bitmap.getHeight(); i++) {
            array[i] = new float[257][];
            for (int j = 0; j < bitmap.getWidth(); j++) {
                int pixel = bitmap.getPixel(j, i);
                array[i][j] = new float[4];
                array[i][j][0] = ((pixel >> 16 & 0xFF) - mean) / std;
                array[i][j][1] = ((pixel >> 8 & 0xFF) - mean) / std;
                array[i][j][2] = ((pixel & 0xFF) - mean) / std;
            }
        }
        Byte[] bytes = new Byte[inputBuffer.array().length];
        for (int i = 0; i < inputBuffer.array().length; i++) {
            bytes[i] = inputBuffer.array()[i];
        }
        return array;
    }

    private ByteBuffer initInputArrays(Bitmap bitmap) {
        int bytesPerChannel = 4;
        int inputChannels = 3;
        int batchSize = 1;
        ByteBuffer inputBuffer = ByteBuffer.allocateDirect(batchSize * bytesPerChannel * bitmap.getHeight() * bitmap.getWidth() * inputChannels);
        inputBuffer.order(ByteOrder.nativeOrder());
        inputBuffer.rewind();
        float mean = 128.0f;
        float std = 128.0f;
        for (int i = 0; i < bitmap.getHeight(); i++) {
            for (int j = 0; j < bitmap.getWidth(); j++) {
                int pixel = bitmap.getPixel(j, i);
                inputBuffer.putFloat(((pixel >> 16 & 0xFF) - mean) / std);
                inputBuffer.putFloat(((pixel >> 8 & 0xFF) - mean) / std);
                inputBuffer.putFloat(((pixel & 0xFF) - mean) / std);
            }
        }
        Byte[] bytes = new Byte[inputBuffer.array().length];
        for (int i = 0; i < inputBuffer.array().length; i++) {
            bytes[i] = inputBuffer.array()[i];
        }
        return inputBuffer;

    }

    private HashMap<Integer, Object> initOutputMap(Interpreter interpreter) {
        HashMap<Integer, Object> outputMap = new HashMap<>();
        int[] heatmapsShape = interpreter.getOutputTensor(0).shape();
        outputMap.put(0, new float[heatmapsShape[0]][heatmapsShape[1]][heatmapsShape[2]][heatmapsShape[3]]);

        int[] offsetmapsShape = interpreter.getOutputTensor(1).shape();
        outputMap.put(1, new float[offsetmapsShape[0]][offsetmapsShape[1]][offsetmapsShape[2]][offsetmapsShape[3]]);

        int[] displacementsFwdShape = interpreter.getOutputTensor(1).shape();
        outputMap.put(2, new float[displacementsFwdShape[0]][displacementsFwdShape[1]][displacementsFwdShape[2]][displacementsFwdShape[3]]);

        int[] displacementsBwdShape = interpreter.getOutputTensor(1).shape();
        outputMap.put(1, new float[displacementsBwdShape[0]][displacementsBwdShape[1]][displacementsBwdShape[2]][displacementsBwdShape[3]]);

        return outputMap;
    }

    Person estimateSinglePose(Bitmap bitmap) throws IOException {
        Long estimationStartTimeNanos = SystemClock.elapsedRealtimeNanos();
        float[][][] inputArray = initInputArray(bitmap);
        HashMap<Integer, Object> outputMap = initOutputMap(getInterpreter());
        long inferenceStartTimeNanos = SystemClock.elapsedRealtimeNanos();
        try {
            getInterpreter().runForMultipleInputsOutputs(new float[][][][]{inputArray}, outputMap);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        lastInferenceTimeNanos = SystemClock.elapsedRealtimeNanos() - inferenceStartTimeNanos;
        return processOutput(bitmap, outputMap);

    }

    Person processOutput(Bitmap bitmap, HashMap<Integer, Object> outputMap) {
        float[][][][] heatmaps = (float[][][][]) outputMap.get(0);
        float[][][][] offsets = (float[][][][]) outputMap.get(1);

        int height = heatmaps[0][0].length;
        int width = heatmaps[0][1].length;
        int numKeyPoints = heatmaps[0][0][0].length;

        Pair<Integer, Integer>[] keypointPositions = new Pair[numKeyPoints];
        for (int keyPoint = 0; keyPoint < numKeyPoints; keyPoint++) {
            float maxVal = heatmaps[0][0][0][keyPoint];
            float maxRow = 0;
            float maxCol = 0;
            for (int row = 0; row < height; row++) {
                for (int col = 0; col < width; col++) {
                    if (heatmaps[0][row][col][keyPoint] > maxVal) {
                        maxRow = Math.round(row);
                        maxCol = Math.round(col);
                    }
                }
            }
            keypointPositions[keyPoint] = new Pair(Math.round(maxRow), Math.round(maxCol));
        }

        int[] xCoords = new int[numKeyPoints];
        int[] yCoords = new int[numKeyPoints];
        float[] confidenceScores = new float[numKeyPoints];
        for (int i = 0; i < keypointPositions.length; i++) {
            Pair<Integer, Integer> pair = keypointPositions[i];
            int positionX = pair.first;
            int positionY = pair.second;
            yCoords[i] = (
                    Math.round(pair.first / (height - 1) * bitmap.getHeight() + offsets[0][positionY][positionX][i])
            );
            xCoords[i] = (
                    Math.round(pair.second / (width - 1) * bitmap.getWidth() + offsets[0][positionY][positionX][i + numKeyPoints])
            );
            confidenceScores[i] = sigmoid(heatmaps[0][positionY][positionX][i]);

        }
        Person person = new Person();
        KeyPoint[] keypointList = new KeyPoint[numKeyPoints];
        float totalScore = 0.0f;
        for (int i = 0; i < BodyPart.values().length; i++) {
            keypointList[i] = new KeyPoint();
            keypointList[i].bodyPart = BodyPart.values()[i];
            keypointList[i].position.x = xCoords[i];
            keypointList[i].position.y = yCoords[i];
            totalScore += confidenceScores[i];
            Log.i(TAG, "processOutput: x " + keypointList[i].position.x);
            Log.i(TAG, "processOutput: c " + confidenceScores[i]);
            Log.i(TAG, "processOutput: y" + keypointList[i].position.y);
        }

        person.keyPoints = keypointList;
        person.score = totalScore / numKeyPoints;

        return person;
    }

}

