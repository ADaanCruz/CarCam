package com.example.carcam;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import com.example.carcam.sensor.RotationVector;

public class MainActivity extends AppCompatActivity {

    public SensorManager sensorManager;
    public RotationVector rotationVector;
    public SensorEventListener sensorEventListener;
    public float[] orientations = new float[3];

    public TextView direction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();

        direction = findViewById(R.id.tv_direction);


        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        rotationVector = new RotationVector(sensorManager);

        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                orientations = rotationVector.getOrientations(sensorEvent);

                if(Math.abs(orientations[2]) < 75) {
                    direction.setText(R.string.lbl_right);
                } else if(Math.abs(orientations[2]) > 105) {
                    direction.setText(R.string.lbl_left);
                } else {
                    direction.setText("");
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };

        sensorManager.registerListener(sensorEventListener, rotationVector.getSensor(), SensorManager.SENSOR_DELAY_NORMAL);
    }
}
