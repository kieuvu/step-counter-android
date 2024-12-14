package com.vukm.StepCounter.ui.summary;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vukm.StepCounter.R;

import java.util.HashMap;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private final HashMap<String, Integer> stepHistory;

    public HistoryAdapter(HashMap<String, Integer> stepHistory) {
        this.stepHistory = stepHistory;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_step_count, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String date = (String) stepHistory.keySet().toArray()[position];
        int steps = stepHistory.get(date);

        holder.tvDate.setText(date);
        holder.tvStepCount.setText(String.valueOf(steps)  + " bước");
    }

    @Override
    public int getItemCount() {
        return stepHistory.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate;
        TextView tvStepCount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvStepCount = itemView.findViewById(R.id.tvStepCount);
        }
    }
}
