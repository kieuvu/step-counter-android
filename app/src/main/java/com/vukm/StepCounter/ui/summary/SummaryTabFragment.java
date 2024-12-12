package com.vukm.StepCounter.ui.summary;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vukm.StepCounter.R;

import java.util.HashMap;

public class SummaryTabFragment extends Fragment {
    private SharedPreferences sharedPreferences;
    private HashMap<String, Integer> stepCountByDate = new HashMap<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_summary_tab, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context context = getContext();
        if (context != null) {
            sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        } else {
            Log.e("SummaryTabFragment", "Context is null when initializing SharedPreferences");
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        this.getHistories();
        HistoryAdapter adapter = new HistoryAdapter(stepCountByDate);
        recyclerView.setAdapter(adapter);
    }

    private void getHistories() {
        Log.i("SummaryTabFragment", "Getting histories");
        if (sharedPreferences == null) {
            Log.e("SummaryTabFragment", "SharedPreferences is null");
            return;
        }

        String storedData = sharedPreferences.getString("stepHistory", "");

        if (storedData.isEmpty()) {
            Log.e("SummaryTabFragment", "SharedPreferences empty");
            return;
        }
        for (String entry : storedData.split(",")) {
            String[] parts = entry.split(":");
            if (parts.length != 2) {
                System.out.println("Invalid entry: " + entry);
                continue;
            }
            try {
                stepCountByDate.put(parts[0], Integer.parseInt(parts[1]));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }
}