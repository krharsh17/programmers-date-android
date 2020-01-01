package in.krharsh17.programmersdate.home.bottompager;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import in.krharsh17.programmersdate.Constants;
import in.krharsh17.programmersdate.R;
import in.krharsh17.programmersdate.events.BarActivity;
import in.krharsh17.programmersdate.events.LogoActivity;
import in.krharsh17.programmersdate.events.PoseActivity;
import in.krharsh17.programmersdate.events.QRActivity;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends Fragment implements Constants {

    FloatingActionButton begin;
    TextView info;
    String taskType;
    int currentLevel;

    public DetailFragment() {
        // Required empty public constructor
    }

    public DetailFragment setTaskType(String taskType, int currentLevel) {
        this.taskType = taskType;
        this.currentLevel = currentLevel;
        if (info != null && begin != null) {
            switch (taskType) {
                case taskTypeLogo:
                    begin.setImageResource(R.drawable.task_logo_icon);
                    info.setText(helpTextLogo);
                    break;
                case taskTypeBar:
                    begin.setImageResource(R.drawable.task_bar_icon);
                    info.setText(helpTextBar);
                    break;
                case taskTypePose:
                    begin.setImageResource(R.drawable.task_camera_icon);
                    info.setText(helpTextPose);
                    break;
                case taskTypeQR:
                    begin.setImageResource(R.drawable.task_qr_icon);
                    info.setText(helpTextQR);
                    break;
                case taskTypeTwister:
                    break;
            }
        }
        return this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        begin = getView().findViewById(R.id.task_detail_begin);
        info = getView().findViewById(R.id.task_detail_info);
        if (taskType != null)
            setTaskType(taskType, 1);

        if (taskType != null)
            begin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (taskType) {
                        case taskTypeLogo:
                            Intent logoIntent = new Intent(getActivity(), LogoActivity.class);
                            logoIntent.putExtra("currentLevel", currentLevel);
                            startActivity(logoIntent);
                            break;
                        case taskTypeBar:
                            Intent barIntent = new Intent(getActivity(), BarActivity.class);
                            barIntent.putExtra("currentLevel", currentLevel);
                            startActivity(barIntent);
                            break;
                        case taskTypePose:
                            Intent poseIntent = new Intent(getActivity(), PoseActivity.class);
                            poseIntent.putExtra("currentLevel", currentLevel);
                            startActivity(poseIntent);
                            break;
                        case taskTypeQR:
                            Intent qrIntent = new Intent(getActivity(), QRActivity.class);
                            qrIntent.putExtra("currentLevel", currentLevel);
                            startActivity(qrIntent);
                            break;
                        case taskTypeTwister:
                            break;
                    }

                }
            });
    }
}