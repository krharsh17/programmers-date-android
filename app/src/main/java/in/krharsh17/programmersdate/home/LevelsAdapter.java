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
import java.util.zip.Inflater;

import in.krharsh17.programmersdate.R;
import in.krharsh17.programmersdate.models.Level;

public class LevelsAdapter extends RecyclerView.Adapter<LevelsAdapter.ViewHolder> {

    Context context;
    private ArrayList<Level> levels;
    private ArrayList<ViewHolder> viewHolders = new ArrayList<>();
    private int currentLevel;

    public LevelsAdapter(Context context){
        this.context = context;
    }

    public LevelsAdapter (Context context, ArrayList<Level> levels, int currentLevel){
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
        if(level.getLevelNumber()<currentLevel){

            setCompleted(holder,level);

        }else if (level.getLevelNumber()==currentLevel){

            setUnlocked(holder,level);

        }else if (level.getLevelNumber()>currentLevel){

            setLocked(holder,level);

        }

    }

    @Override
    public int getItemCount() {
        return levels.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public TextView levelText;
        public ImageView levelIcon;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            levelText = itemView.findViewById(R.id.level_number_text);
            levelIcon = itemView.findViewById(R.id.level_icon_image);
        }
    }

    public void setLocked(ViewHolder viewHolder, Level level){
        viewHolder.levelText.setText("Level "+level.getLevelNumber());
        viewHolder.levelIcon.setImageResource(R.drawable.app_logo_background);
        viewHolder.levelText.getBackground().setAlpha(50);
        viewHolder.levelIcon.getBackground().setAlpha(50);
    }

    public void setUnlocked(ViewHolder viewHolder, Level level){
        viewHolder.levelText.setText("Level "+level.getLevelNumber());
        viewHolder.levelText.getBackground().setAlpha(100);
        viewHolder.levelIcon.getBackground().setAlpha(100);
    }

    public void setCompleted(ViewHolder viewHolder, Level level){
        viewHolder.levelText.setText("Level "+level.getLevelNumber());
        viewHolder.levelText.getBackground().setAlpha(50);
        viewHolder.levelIcon.getBackground().setAlpha(50);
    }

    public void levelSetter(int newLevel){

        if (newLevel>currentLevel){
            for(int i=currentLevel;i<=newLevel;i++){
                if(i==newLevel){
                    setUnlocked(viewHolders.get(newLevel-1),levels.get(newLevel-1));
                }else {
                    setCompleted(viewHolders.get(i-1),levels.get(i-1));
                }
            }
        }else if (newLevel<currentLevel){
            for (int i=currentLevel;i>=newLevel;i--){
                if(i==newLevel){
                    setUnlocked(viewHolders.get(newLevel-1),levels.get(newLevel-1));
                }else {
                    setLocked(viewHolders.get(i-1),levels.get(i-1));
                }
            }
        }else {
            setUnlocked(viewHolders.get(newLevel-1),levels.get(newLevel-1));
        }

    }

}
