package in.krharsh17.programmersdate.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import in.krharsh17.programmersdate.R;
import in.krharsh17.programmersdate.models.Level;

public class LevelsAdapter extends RecyclerView.Adapter<LevelsAdapter.ViewHolder> {

    Context context;
    private ArrayList<Level> levels;

    public LevelsAdapter(Context context){
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.level_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return levels.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
