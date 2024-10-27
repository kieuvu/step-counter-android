package com.vukm.StepCounter;

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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor stepCounterSensor;

    private TextView stepCountView;
    private Button toggleButtonView;

    private boolean isCounting = false;
    private int stepCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            this.requestActivityPermission();
        }

        this.stepCountView = findViewById(R.id.stepNumberTextView);
        this.toggleButtonView = findViewById(R.id.toggleCountingButton);

        this.sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (this.sensorManager != null) {
            this.stepCounterSensor = this.sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
            if (this.stepCounterSensor != null) {
                Toast.makeText(this, "Step Counter connected successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Step Counter connected failed!", Toast.LENGTH_SHORT).show();
            }
        }

        this.toggleButtonView.setOnClickListener(v -> {
            this.isCounting = !this.isCounting;
            if (this.isCounting) {
                Toast.makeText(this, "Started counting", Toast.LENGTH_SHORT).show();
                Log.d("Vukm", "Started counting");
                this.toggleButtonView.setText(R.string.StopCountingButtonLabel);
                this.stepCount = 0;
                this.sensorManager.registerListener(this, this.stepCounterSensor, SensorManager.SENSOR_DELAY_FASTEST);
            } else {
                Toast.makeText(this, "Stopped counting", Toast.LENGTH_SHORT).show();
                Log.d("Vukm", "Stopped counting");
                this.toggleButtonView.setText(R.string.StartCountingButtonLabel);
                this.sensorManager.unregisterListener(this);
            }
        });
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.d("Vukm", "Handling sensor event");

        if (this.isCounting && event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            Log.d("Vukm", "Increasing counter");
            this.stepCount++;
            this.stepCountView.setText(String.valueOf(this.stepCount));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void requestActivityPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 1);
        }
    }
}
