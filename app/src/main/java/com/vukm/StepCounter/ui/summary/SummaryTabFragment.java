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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

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
        if (sharedPreferences == null) {
            return;
        }

        String stepHistory = sharedPreferences.getString("stepHistories", "");
        if (stepHistory.isEmpty()){
            return;
        }

        try {
            JSONObject stepHistoryJson = new JSONObject(stepHistory);
            Iterator<String> keys = stepHistoryJson.keys();

            while (keys.hasNext()) {
                String date = keys.next();
                int steps = stepHistoryJson.getInt(date);
                stepCountByDate.put(date, steps);
            }
        } catch (JSONException e) {
            Log.e("CountingTabFragment", "Error parsing JSON", e);
            return;
        }
    }
}