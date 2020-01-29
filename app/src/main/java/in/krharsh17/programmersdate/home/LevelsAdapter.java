package in.krharsh17.programmersdate.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import in.krharsh17.programmersdate.R;
import in.krharsh17.programmersdate.models.Level;

public class LevelsAdapter extends RecyclerView.Adapter<LevelsAdapter.ViewHolder> {

    Context context;
    private ArrayList<Level> levels;
    private ArrayList<ViewHolder> viewHolders = new ArrayList<>();
    private int currentLevel;

    public LevelsAdapter(Context context) {
        this.context = context;
    }

    public LevelsAdapter(Context context, ArrayList<Level> levels, int currentLevel) {
        this.context = context;
        this.levels = levels;
        this.currentLevel = currentLevel;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.level_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final Level level = levels.get(position);
        viewHolders.add(holder);
        if (level.getLevelNumber() < currentLevel) {
            if (level.isSkipped() == true) {
                setSkipped(holder, level);
            } else {
                setCompleted(holder, level);
            }

        } else if (level.getLevelNumber() == currentLevel) {

            setUnlocked(holder, level);

        } else if (level.getLevelNumber() > currentLevel) {

            setLocked(holder, level);

        }

    }

    @Override
    public int getItemCount() {
        return levels.size();
    }

    public void setLocked(ViewHolder viewHolder, Level level) {
        viewHolder.levelText.setText("L E V E L  " + level.getLevelNumber());
        viewHolder.levelIcon.setImageResource(R.drawable.level_locked_icon);
        viewHolder.levelIcon.setBackgroundResource(R.drawable.level_background_dull);
    }

    public void setSkipped(ViewHolder viewHolder, Level level) {
        viewHolder.levelText.setText("L E V E L  " + level.getLevelNumber());
        viewHolder.levelIcon.setImageResource(R.drawable.cross);
        viewHolder.levelIcon.setBackgroundResource(R.drawable.level_background_dull);
    }

    public void setUnlocked(ViewHolder viewHolder, Level level) {
        viewHolder.levelText.setText("L E V E L  " + level.getLevelNumber());
        if (level.getTaskType().equals("POSE")) {
            viewHolder.levelIcon.setImageResource(R.drawable.level_pose_bright);
            viewHolder.levelIcon.setBackgroundResource(R.drawable.level_background_bright);
        } else if (level.getTaskType().equals("BAR")) {
            viewHolder.levelIcon.setImageResource(R.drawable.level_bar_bright);
            viewHolder.levelIcon.setBackgroundResource(R.drawable.level_background_bright);
        } else if (level.getTaskType().equals("QR")) {
            viewHolder.levelIcon.setImageResource(R.drawable.level_qr_bright);
            viewHolder.levelIcon.setBackgroundResource(R.drawable.level_background_bright);
        } else if (level.getTaskType().equals("LOGO")) {
            viewHolder.levelIcon.setImageResource(R.drawable.level_logo_bright);
            viewHolder.levelIcon.setBackgroundResource(R.drawable.level_background_bright);
        } else if (level.getTaskType().equals("AUDIO")) {
//            viewHolder.levelIcon.setImageResource(R.drawable.level_audio);
            viewHolder.levelIcon.setBackgroundResource(R.drawable.level_background_bright);
        }
    }

    public void setCompleted(ViewHolder viewHolder, Level level) {
        viewHolder.levelText.setText("L E V E L  " + level.getLevelNumber());
        if (level.getTaskType().equals("POSE")) {
            viewHolder.levelIcon.setImageResource(R.drawable.level_pose_dull);
            viewHolder.levelIcon.setBackgroundResource(R.drawable.level_background_dull);
        } else if (level.getTaskType().equals("BAR")) {
            viewHolder.levelIcon.setImageResource(R.drawable.level_bar_dull);
            viewHolder.levelIcon.setBackgroundResource(R.drawable.level_background_dull);
        } else if (level.getTaskType().equals("QR")) {
            viewHolder.levelIcon.setImageResource(R.drawable.level_qr_dull);
            viewHolder.levelIcon.setBackgroundResource(R.drawable.level_background_dull);
        } else if (level.getTaskType().equals("LOGO")) {
            viewHolder.levelIcon.setImageResource(R.drawable.level_logo_dull);
            viewHolder.levelIcon.setBackgroundResource(R.drawable.level_background_dull);
        } else if (level.getTaskType().equals("AUDIO")) {
            viewHolder.levelIcon.setImageResource(R.drawable.cross);
            viewHolder.levelIcon.setBackgroundResource(R.drawable.level_background_dull);
        }
    }

    public void levelSetter(int newLevel) {
        currentLevel = newLevel;

        for (int i = 0; i < viewHolders.size(); i++) {
            Level level = levels.get(i);
            if (level.getLevelNumber() < newLevel) {
                if (level.isSkipped()) {
                    setSkipped(viewHolders.get(i), level);
                } else {
                    setCompleted(viewHolders.get(i), level);
                }

            } else if (level.getLevelNumber() == newLevel) {

                setUnlocked(viewHolders.get(i), level);

            } else if (level.getLevelNumber() > newLevel) {

                setLocked(viewHolders.get(i), level);

            }
        }
//
//        if (newLevel>currentLevel){
//            for(int i=currentLevel;i<=newLevel;i++){
//                if(i==newLevel){
//                    setUnlocked(viewHolders.get(newLevel-1),levels.get(newLevel-1));
//                }else {
//                    setCompleted(viewHolders.get(i-1),levels.get(i-1));
//                }
//            }
//        }else if (newLevel<currentLevel){
//            for (int i=currentLevel;i>=newLevel;i--){
//                if(i==newLevel){
//                    setUnlocked(viewHolders.get(newLevel-1),levels.get(newLevel-1));
//                }else {
//                    setLocked(viewHolders.get(i-1),levels.get(i-1));
//                }
//            }
//        }else {
//            setUnlocked(viewHolders.get(newLevel-1),levels.get(newLevel-1));
//        }

    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public TextView levelText;
        public ImageView levelIcon;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            levelText = itemView.findViewById(R.id.level_number_text);
            levelIcon = itemView.findViewById(R.id.level_icon_image);
        }
    }

}
