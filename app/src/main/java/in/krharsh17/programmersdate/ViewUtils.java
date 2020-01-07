package in.krharsh17.programmersdate;

import android.animation.Animator;
import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import static in.krharsh17.programmersdate.Constants.TAG;

public class ViewUtils {
    public static int DURATION_LONG = Toast.LENGTH_LONG;
    public static int DURATION_SHORT = Toast.LENGTH_SHORT;

    private static CardView mainCard;
    private static ImageView background;
    private static ConstraintLayout root;

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
        root = new ConstraintLayout(activity);
        ConstraintLayout parent = activity.findViewById(R.id.root);
        ConstraintLayout.LayoutParams rootParams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        root.setLayoutParams(rootParams);
        root.setElevation(10f);

        background = new ImageView(activity);
        background.setId(View.generateViewId());
        background.setBackgroundColor(Color.parseColor("#99000000"));
        background.setLayoutParams(rootParams);
        background.setAlpha(0f);
        background.setClickable(true);
        background.setFocusable(true);
        root.addView(background);

        mainCard = (CardView) LayoutInflater.from(activity).inflate(R.layout.dialog_complete, root, false);
        mainCard.setId(View.generateViewId());

        mainCard.setLayoutParams(new ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        mainCard.setAlpha(0f);

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

        mainCard.animate().alpha(1f).setDuration(400);
        background.animate().alpha(1f).setDuration(400);
    }

    public static void showProgressDialog(Activity activity, String text) {
        root = new ConstraintLayout(activity);
        ConstraintLayout parent = activity.findViewById(R.id.root);
        ConstraintLayout.LayoutParams rootParams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        root.setLayoutParams(rootParams);
        root.setElevation(10f);

        background = new ImageView(activity);
        background.setId(View.generateViewId());
        background.setBackgroundColor(Color.parseColor("#99000000"));
        background.setLayoutParams(rootParams);
        background.setAlpha(0f);
        background.setClickable(true);
        background.setFocusable(true);
        root.addView(background);

        mainCard = (CardView) LayoutInflater.from(activity).inflate(R.layout.dialog_creating, root, false);
        mainCard.setId(View.generateViewId());

        mainCard.setLayoutParams(new ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        ((TextView) mainCard.findViewById(R.id.preparing_text)).setText(text);
        mainCard.setAlpha(0f);
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

        mainCard.animate().alpha(1f).setDuration(400);
        background.animate().alpha(1f).setDuration(400);
    }

    public static void showConfirmationDialog(final Activity activity, String message, final View.OnClickListener onYes, final View.OnClickListener onNo) {
        root = new ConstraintLayout(activity);
        root.setId(View.generateViewId());
        ConstraintLayout parent = activity.findViewById(R.id.root);
        ConstraintLayout.LayoutParams rootParams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        root.setLayoutParams(rootParams);
        root.setElevation(10f);


        background = new ImageView(activity);
        background.setId(View.generateViewId());
        background.setBackgroundColor(Color.parseColor("#99000000"));
        background.setLayoutParams(rootParams);
        background.setAlpha(0f);
        background.setClickable(true);
        background.setFocusable(true);
        root.addView(background);

        mainCard = (CardView) LayoutInflater.from(activity).inflate(R.layout.dialog_progress, root, false);
        mainCard.setId(View.generateViewId());

        mainCard.setLayoutParams(new ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        mainCard.setAlpha(0f);


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

        mainCard.findViewById(R.id.yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeDialog();
                Log.i(TAG, "onClick: ");
                if (onYes != null)
                    onYes.onClick(v);
            }
        });

        mainCard.findViewById(R.id.no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeDialog();
                Log.i(TAG, "onClick: ");
                if (onNo != null)
                    onNo.onClick(v);
            }
        });

        if (message != null)
            ((TextView) mainCard.findViewById(R.id.question)).setText(message);

        mainCard.animate().alpha(1f).setDuration(400);
        background.animate().alpha(1f).setDuration(400);
    }

    public static void removeDialog() {
        if (mainCard != null)
            mainCard.animate().alpha(0).setDuration(400)
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            if (background != null)
                                background.animate().alpha(0).setDuration(400);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {

                            if (mainCard != null) {
                                root.removeView(mainCard);
                                Log.i(TAG, "removeDialog: ");
                            }

                            if (background != null) {
                                root.removeView(background);
                                Log.i(TAG, "removeDialog: ");
                            }
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });

    }
}
