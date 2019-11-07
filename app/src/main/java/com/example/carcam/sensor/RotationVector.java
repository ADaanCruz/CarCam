package com.example.carcam.sensor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;

public class RotationVector {

    private SensorManager sensorManager;
    private Sensor rotationVector;

    private float[] rotationMatrix = new float[16];
    private float[] remappedRotationMatrix = new float[16];
    private float[] orientations = new float[3];

    public RotationVector(SensorManager sensorManager)
    {
        this.sensorManager = sensorManager;
        this.rotationVector = this.sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
    }

    public float[] getOrientations(SensorEvent sensorEvent)
    {
        SensorManager.getRotationMatrixFromVector(rotationMatrix, sensorEvent.values);
        SensorManager.remapCoordinateSystem(
                rotationMatrix,
                SensorManager.AXIS_X,
                SensorManager.AXIS_Z,
                remappedRotationMatrix
        );

        SensorManager.getOrientation(remappedRotationMatrix, orientations);
        for(int i = 0; i < 3; i++) {
            this.orientations[i] = (float)(Math.toDegrees(orientations[i]));
        }

        return this.orientations;
    }

    public Sensor getSensor()
    {
        return this.rotationVector;
    }
}
