package com.vukm.StepCounter.ui.counting;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.vukm.StepCounter.R;

import org.jetbrains.annotations.Contract;

public class CountingTabFragment extends Fragment implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor stepCounterSensor;

    private TextView stepCountView;
    private Button toggleButtonView;

    private boolean isCounting = false;
    private int stepCount = 0;
    private int initialStepCount = -1;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_counting_tab, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            this.requestActivityPermission();
        }

        this.stepCountView = view.findViewById(R.id.stepNumberTextView);
        this.toggleButtonView = view.findViewById(R.id.toggleCountingButton);

        this.sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        if (this.sensorManager == null) {
            Toast.makeText(requireContext(), "Can't get sensor services!", Toast.LENGTH_SHORT).show();
            return;
        }

        this.stepCounterSensor = this.sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (this.stepCounterSensor == null) {
            Toast.makeText(requireContext(), "Step Counter connected failed!", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(requireContext(), "Step Counter connected successfully!", Toast.LENGTH_SHORT).show();
        this.toggleButtonView.setOnClickListener(v -> this.changeCountStatus(!this.isCounting));
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void requestActivityPermission() {
        if (getContext() == null) {
            return;
        }

        String permission = Manifest.permission.ACTIVITY_RECOGNITION;
        int permissionGranted = PackageManager.PERMISSION_GRANTED;

        if (ContextCompat.checkSelfPermission(getContext(), permission) != permissionGranted) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{permission}, 1);
        }
    }

    @Contract(pure = true)
    private void changeCountStatus(Boolean status) {
        this.isCounting = status;
        if (this.isCounting) {
            Toast.makeText(requireContext(), "Started counting", Toast.LENGTH_SHORT).show();
            Log.d("Vukm", "Started counting");
            this.toggleButtonView.setText(R.string.StopCountingButtonLabel);
            this.stepCount = 0;
            this.sensorManager.registerListener(this, this.stepCounterSensor, SensorManager.SENSOR_DELAY_FASTEST);
        } else {
            Toast.makeText(requireContext(), "Stopped counting", Toast.LENGTH_SHORT).show();
            Log.d("Vukm", "Stopped counting");
            this.toggleButtonView.setText(R.string.StartCountingButtonLabel);
            this.sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (this.isCounting && event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            int latestCount =  (int) event.values[0];
            if (this.initialStepCount < 0) {
                this.initialStepCount = latestCount + 1;
            }
            this.stepCount = latestCount - initialStepCount;
            this.stepCountView.setText(String.valueOf(stepCount));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        if (sensor.getType() != Sensor.TYPE_STEP_COUNTER) {
            return;
        }
        String message = "Step Counter accuracy changed: " + accuracy;
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPause() {
        super.onPause();
        this.sensorManager.unregisterListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        this.sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (this.sensorManager != null) {
            this.sensorManager.unregisterListener(this);
        }
    }
}