package in.krharsh17.programmersdate.events;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.util.Pair;
import android.view.View;

import org.tensorflow.lite.examples.posenet.lib.KeyPoint;
import org.tensorflow.lite.examples.posenet.lib.Person;
import org.tensorflow.lite.examples.posenet.lib.Position;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import in.krharsh17.programmersdate.R;
import in.krharsh17.programmersdate.events.posenet.PosenetActivity;

import static in.krharsh17.programmersdate.Constants.MODEL_HEIGHT;
import static in.krharsh17.programmersdate.Constants.MODEL_WIDTH;

public class CanvasToBitmap extends View {

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

    private Pair[] bodyJoints = {
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

    Paint paint = new Paint();
    Rect mRect = new Rect();
    public Bitmap bitmap = Bitmap.createBitmap(2000, 2000, Bitmap.Config.ARGB_8888);
    Person person;

    public CanvasToBitmap(Context context, Person person) {
        super(context);
        Canvas canvas = new Canvas(bitmap);
        this.person = person;
        draw(canvas);
    }

    @Override
    public void onDraw(Canvas canvas) {

        //mRect.set(0, 0, 200, 200);
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.FILL);
        //canvas.drawRect(mRect, paint);
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        // Draw `bitmap` and `person` in square canvas.
        int screenWidth;
        int screenHeight;
        int left;
        int right;
        int top;
        int bottom;
        if (canvas.getHeight() > canvas.getWidth()) {
            screenWidth = canvas.getWidth();
            screenHeight = canvas.getWidth();
            left = 0;
            top = (canvas.getHeight() - canvas.getWidth()) / 2;
        } else {
            screenWidth = canvas.getHeight();
            screenHeight = canvas.getHeight();
            left = (canvas.getWidth() - canvas.getHeight()) / 2;
            top = 0;
        }
        right = left + screenWidth;
        bottom = top + screenHeight;

        setPaint();
//        canvas.drawBitmap(
//            bitmap,
//            Rect(0, 0, bitmap.width, bitmap.height),
//            Rect(left, top, right, bottom),
//            paint
//        )

        float widthRatio = screenWidth / MODEL_WIDTH;
        float heightRatio = screenHeight / MODEL_HEIGHT;
        if (person != null) {
            // Draw key points over the image.
            for (KeyPoint keyPoint : person.getKeyPoints()) {
                if (keyPoint.getScore() > 0.5) {
                    Position position = keyPoint.getPosition();
                    float adjustedX = position.getX() * widthRatio + left;
                    float adjustedY = position.getY() * heightRatio + top;
                    canvas.drawCircle(adjustedX, adjustedY, 8.0f, paint);
                }
            }

            for (Pair pair : bodyJoints) {
                if (
                        (person.getKeyPoints().get(((BodyPart)pair.first).ordinal()).getScore() > 0.5) &&
                        (person.getKeyPoints().get(((BodyPart)pair.second).ordinal()).getScore() > 0.5)
                ) {
                    canvas.drawLine(
                            person.getKeyPoints().get(((BodyPart)pair.first).ordinal()).getPosition().getX() * widthRatio + left,
                            person.getKeyPoints().get(((BodyPart)pair.first).ordinal()).getPosition().getY() * heightRatio + top,
                            person.getKeyPoints().get(((BodyPart)pair.second).ordinal()).getPosition().getX() * widthRatio + left,
                            person.getKeyPoints().get(((BodyPart)pair.second).ordinal()).getPosition().getY() * heightRatio + top,
                            paint
                    );
                }
            }


        }
        canvas.setBitmap(bitmap);

        final ByteArrayOutputStream mByteArrayOutputStream = new ByteArrayOutputStream();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, mByteArrayOutputStream);

                        bitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(mByteArrayOutputStream.toByteArray()));
                        mByteArrayOutputStream.flush();
                        mByteArrayOutputStream.close();
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            });


    }

    private void setPaint() {
        paint.setColor(getResources().getColor(R.color.colorAccent));
        paint.setTextSize(80.0f);
        paint.setStrokeWidth(6.0f);
    }
}