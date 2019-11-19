package com.example.carcam.sensor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

public class RotationVector {

    private SensorManager sensorManager;
    private Sensor rotationVector;

    private float[] rotationMatrix = new float[9];
    private float[] orientations = new float[3];

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public RotationVector(SensorManager sensorManager)
    {
        this.sensorManager = sensorManager;
        this.rotationVector = sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);
        if (rotationVector == null) {
            Log.i("S/Rotation Vector", "The sensor is null");
        } else {
            Log.i("S/Rotation Vector", "Sensor obtained");
        }
    }

    public float[] getOrientations(SensorEvent sensorEvent)
    {
        SensorManager.getRotationMatrixFromVector(rotationMatrix, sensorEvent.values);
        SensorManager.remapCoordinateSystem(
                rotationMatrix,
                SensorManager.AXIS_X,
                SensorManager.AXIS_Y,
                rotationMatrix
        );

        SensorManager.getOrientation(rotationMatrix, orientations);
        for(int i = 0; i < 3; i++) {
            orientations[i] = (float)(Math.toDegrees(orientations[i]));
        }

        return orientations;
    }

    public Sensor getSensor()
    {
        return rotationVector;
    }

    public void setSensor(Sensor rotationVector) {
        this.rotationVector = rotationVector;
    }

    public SensorManager getManager() {
        return sensorManager;
    }

    public void setManager(SensorManager sensorManager) {
        this.sensorManager = sensorManager;
    }

}
