package in.krharsh17.programmersdate;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

public class ViewUtils {
    public static int DURATION_LONG = Toast.LENGTH_LONG;
    public static int DURATION_SHORT = Toast.LENGTH_SHORT;

    public static void showToast(Activity context, String text, int duration) {
        LayoutInflater inflater = context.getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast,
                (ViewGroup) context.findViewById(R.id.custom_toast_root));

        ((TextView) layout.findViewById(R.id.custom_toast_textview)).setText(text);
        // create a new Toast using context
        Toast toast = new Toast(context);
        toast.setDuration(duration); // set the duration for the Toast
        toast.setView(layout); // set the inflated layout
        toast.show(); // display the custom Toast
    }

    public static void showCompleteDialog(Activity activity) {
        ConstraintLayout root = new ConstraintLayout(activity);
        ConstraintLayout parent = activity.findViewById(R.id.root);
        ConstraintLayout.LayoutParams rootParams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        root.setLayoutParams(rootParams);
        root.setElevation(10f);

        ImageView background = new ImageView(activity);
        background.setId(View.generateViewId());
        background.setBackgroundColor(Color.parseColor("#99000000"));
        background.setLayoutParams(rootParams);
        root.addView(background);

        CardView mainCard = (CardView) LayoutInflater.from(activity).inflate(R.layout.dialog_complete, root, false);
        mainCard.setId(View.generateViewId());

        mainCard.setLayoutParams(new ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        root.addView(mainCard);

        mainCard.setRadius(32f);
        mainCard.setCardElevation(32f);

        ConstraintSet rootConstraints = new ConstraintSet();
        rootConstraints.clone(root);

        rootConstraints.connect(mainCard.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        rootConstraints.connect(mainCard.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
        rootConstraints.connect(mainCard.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
        rootConstraints.connect(mainCard.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);

        root.setConstraintSet(rootConstraints);

        parent.addView(root);
    }

    public static void showGameCreating(Activity activity) {
        ConstraintLayout root = new ConstraintLayout(activity);
        ConstraintLayout parent = activity.findViewById(R.id.root);
        ConstraintLayout.LayoutParams rootParams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        root.setLayoutParams(rootParams);
        root.setElevation(10f);

        ImageView background = new ImageView(activity);
        background.setId(View.generateViewId());
        background.setBackgroundColor(Color.parseColor("#99000000"));
        background.setLayoutParams(rootParams);
        root.addView(background);

        CardView mainCard = (CardView) LayoutInflater.from(activity).inflate(R.layout.dialog_creating, root, false);
        mainCard.setId(View.generateViewId());

        mainCard.setLayoutParams(new ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        root.addView(mainCard);

        mainCard.setRadius(32f);
        mainCard.setCardElevation(32f);

        ConstraintSet rootConstraints = new ConstraintSet();
        rootConstraints.clone(root);

        rootConstraints.connect(mainCard.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        rootConstraints.connect(mainCard.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
        rootConstraints.connect(mainCard.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
        rootConstraints.connect(mainCard.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);

        root.setConstraintSet(rootConstraints);

        parent.addView(root);
    }
}
