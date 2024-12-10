package com.vukm.StepCounter.ui.counting;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CountingTabFragment extends Fragment implements SensorEventListener, LocationListener {
    private SensorManager stepCounterSensorManager;
    private Sensor stepCounterSensor;

    private LocationManager locationManager;
    private Location lastLocation = null;

    private TextView stepCountTextView;
    private Button startStopButtonView;
    private TextView speedTextView;

    private boolean isCounting = false;
    private int initialStepCount = -1;

    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_counting_tab, container, false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            this.requestActivityPermission();
        }

        this.stepCountTextView = view.findViewById(R.id.stepNumberTextView);
        this.startStopButtonView = view.findViewById(R.id.toggleCountingButton);
        this.speedTextView = view.findViewById(R.id.speedTextView);

        this.stepCounterSensorManager = (SensorManager) this.requireActivity().getSystemService(Context.SENSOR_SERVICE);
        if (this.stepCounterSensorManager == null) {
            Toast.makeText(this.requireContext(), "Can't get sensor services!", Toast.LENGTH_SHORT).show();
            return;
        }

        this.stepCounterSensor = this.stepCounterSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (this.stepCounterSensor == null) {
            Toast.makeText(this.requireContext(), "Step Counter connected failed!", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this.requireContext(), "Step Counter connected successfully!", Toast.LENGTH_SHORT).show();
        this.startStopButtonView.setOnClickListener(v -> this.changeCountStatus(!this.isCounting));

        this.locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void requestActivityPermission() {
        if (this.getContext() == null) {
            return;
        }

        String[] permissions = {
                Manifest.permission.ACTIVITY_RECOGNITION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACTIVITY_RECOGNITION
        };

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this.getContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this.requireActivity(), permissions, 1);
            }
        }
    }

    @Contract(pure = true)
    private void changeCountStatus(Boolean status) {
        this.isCounting = status;
        if (this.isCounting) {
            Toast.makeText(this.requireContext(), "Started counting", Toast.LENGTH_SHORT).show();
            Log.d("Vukm", "Started counting");
            this.startStopButtonView.setText(R.string.StopCountingButtonLabel);
            this.stepCounterSensorManager.registerListener(this, this.stepCounterSensor, SensorManager.SENSOR_DELAY_FASTEST);
            this.startLocationUpdates();
        } else {
            Toast.makeText(this.requireContext(), "Stopped counting", Toast.LENGTH_SHORT).show();
            Log.d("Vukm", "Stopped counting");
            this.startStopButtonView.setText(R.string.StartCountingButtonLabel);
            this.stepCounterSensorManager.unregisterListener(this);
            this.stopLocationUpdates();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (this.isCounting && event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            String lastRecordedDate = sharedPreferences.getString("lastRecordedDate", "");

            if (!today.equals(lastRecordedDate)) {
                this.resetDailySteps(today);
            }

            int latestCount = (int) event.values[0];
            if (this.initialStepCount < 0) {
                this.initialStepCount = latestCount;
            }

            int stepCount = latestCount - this.initialStepCount;
            this.stepCountTextView.setText(String.valueOf(stepCount));

            sharedPreferences.edit()
                    .putInt("dailySteps", stepCount)
                    .apply();
        }
    }

    private void resetDailySteps(String today) {
        String lastRecordedDate = sharedPreferences.getString("lastRecordedDate", "");
        int lastDaySteps = sharedPreferences.getInt("dailySteps", 0);
        if (!lastRecordedDate.isEmpty()) {
            saveToDatabase(lastRecordedDate, lastDaySteps);
        }

        this.initialStepCount = -1;
        sharedPreferences.edit()
                .putString("lastRecordedDate", today)
                .putInt("dailySteps", 0)
                .apply();
    }

    private void saveToDatabase(String date, int steps) {
        String storedData = sharedPreferences.getString("stepHistory", "");

        StringBuilder updatedData = new StringBuilder();
        boolean updated = false;

        if (!storedData.isEmpty()) {
            String[] entries = storedData.split(",");
            for (String entry : entries) {
                String[] parts = entry.split(":");
                if (parts[0].equals(date)) {
                    updatedData.append(date).append(":").append(steps).append(",");
                    updated = true;
                } else {
                    updatedData.append(entry).append(",");
                }
            }
        }

        if (!updated) {
            updatedData.append(date).append(":").append(steps).append(",");
        }

        sharedPreferences.edit()
                .putString("stepHistory", updatedData.toString().replaceAll(",$", ""))
                .apply();
    }

//    private void displayStepHistory() {
//        // Retrieve step history from SharedPreferences
//        String storedData = sharedPreferences.getString("stepHistory", "");
//
//        StringBuilder report = new StringBuilder("Step Summary:\n");
//
//        if (!storedData.isEmpty()) {
//            String[] entries = storedData.split(",");
//            for (String entry : entries) {
//                String[] parts = entry.split(":");
//                if (parts.length == 2) {
//                    String date = parts[0];
//                    String steps = parts[1];
//                    report.append(date).append(": ").append(steps).append(" steps\n");
//                }
//            }
//        }
//
//        // Display the report in a TextView
//        TextView reportTextView = findViewById(R.id.reportTextView);
//        reportTextView.setText(report.toString());
//    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        if (sensor.getType() != Sensor.TYPE_STEP_COUNTER) {
            return;
        }
        String message = "Step Counter accuracy changed: " + accuracy;
        Toast.makeText(this.getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPause() {
        super.onPause();
        this.stepCounterSensorManager.unregisterListener(this);
        this.stopLocationUpdates();
    }

    @Override
    public void onResume() {
        super.onResume();
        this.stepCounterSensorManager.registerListener(this, this.stepCounterSensor, SensorManager.SENSOR_DELAY_FASTEST);
        this.startLocationUpdates();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (this.stepCounterSensorManager != null) {
            this.stepCounterSensorManager.unregisterListener(this);
        }
    }

    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0, this);
        } else {
            Toast.makeText(this.requireContext(), "Location permission not granted", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopLocationUpdates() {
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onLocationChanged(@NonNull Location location) {
        if (lastLocation != null) {
            float speed = location.getSpeed() * 3.6f;
            this.speedTextView.setText(String.format("%.2f km/h", speed));
        }
        this.lastLocation = location;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
    }
}