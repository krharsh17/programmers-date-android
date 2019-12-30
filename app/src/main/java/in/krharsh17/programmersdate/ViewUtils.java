package in.krharsh17.programmersdate;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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
}
