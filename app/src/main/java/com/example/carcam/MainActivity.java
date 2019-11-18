package com.example.carcam;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bluetooth.ConnectedThread;
import com.example.carcam.sensor.RotationVector;
import com.example.login.LoginActivity;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

import android.provider.Settings.Secure;

import static com.example.carcam.R.*;


public class MainActivity extends AppCompatActivity {

    /********** Sensor *******************/
    public SensorManager sensorManager;
    public RotationVector sensor;
    public SensorEventListener sensorListener;
    public boolean correctSensor = false;

    public float[] orientations = new float[3];
    public TextView direction;

    public static Intent intentListBluetooth = null;


    /**
     * Bluetooth
     **/
    Handler bluetoothIn;
    final int handlerState = 0;
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder DataStringIN = new StringBuilder();
    private static ConnectedThread MyConexionBT;
    //00001101-0000-1000-8000-00805F9B34FB
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static String address;

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        return device.createInsecureRfcommSocketToServiceRecord(BTMODULEUUID);
    }

    /********** Button control**************/
    Button btnAhead;
    Button btnBack;
    Button btnLeft;
    Button btnRight;
    Button btnStop;

    /********** Camera*******************/
    WebView camera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();


        /*********TODO Login init **********/
//        if (intentListBluetooth == null) {
//            intentListBluetooth = new Intent(this, com.example.bluetooth.ListBluetooth.class);
//            startActivity(intentListBluetooth);
//            finish();
//        }
        //direction = findViewById(R.id.tv_direction);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
            correctSensor = this.sensorConfiguration();
        this.btnSetAction();
        this.camera();

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (correctSensor) {
            sensorListener = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    if (event.sensor.getType() == Sensor.TYPE_GAME_ROTATION_VECTOR) {
                        orientations = sensor.getOrientations(event);

                        if (orientations[1] <= -22) {
                            //  direction.setText("Right");
                            MyConexionBT.write("4");

                        } else if (orientations[1] >= 22) {
                            //  direction.setText("Left");
                            MyConexionBT.write("3");

                        } else {
                            //  direction.setText("Ahead");
                        }
                    }
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {

                }
            };

            sensorManager.registerListener(sensorListener, sensor.getSensor(), SensorManager.SENSOR_DELAY_NORMAL);
        }

        this.conection();

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public boolean sensorConfiguration() {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager == null) {
            Log.i("S/Manager", "The manager is null");
            return false;
        } else {
            sensor = new RotationVector(sensorManager);
            return true;
        }
    }

    public void conection() {


        if (MyConexionBT == null) {
            bluetoothIn = new Handler() {
                public void handleMessage(android.os.Message msg) {
                    if (msg.what == handlerState) {
                        String readMessage = (String) msg.obj;
                        DataStringIN.append(readMessage);

                        int endOfLineIndex = DataStringIN.indexOf("#");

                        if (endOfLineIndex > 0) {
                            String dataInPrint = DataStringIN.substring(0, endOfLineIndex);
                            DataStringIN.delete(0, DataStringIN.length());
                        }
                    }
                }
            };
            Bundle date = this.getIntent().getExtras();
            address = (String) date.get("address");
            btAdapter = BluetoothAdapter.getDefaultAdapter(); // get Bluetooth adapter
            BluetoothDevice device = btAdapter.getRemoteDevice(address);
            try {
                btSocket = createBluetoothSocket(device);
            } catch (IOException e) {
                Toast.makeText(getBaseContext(), "La creacción del Socket fallo", Toast.LENGTH_LONG).show();
            }
            try {
                btSocket.connect();
                System.out.println("Connected");
            } catch (IOException e) {
                System.out.println(e);
                try {
                    btSocket.close();
                } catch (IOException e2) {
                    System.out.println("Error btSocket");
                }
            }
            MyConexionBT = new ConnectedThread(btSocket, bluetoothIn);
            MyConexionBT.start();
        }

    }

    public void btnSetAction() {
        btnAhead = findViewById(R.id.btn_ahead);
        btnBack = findViewById(R.id.btn_back);
        btnLeft = findViewById(R.id.btn_left);
        btnRight = findViewById(R.id.btn_right);
        btnStop = findViewById(R.id.btn_stop);

        btnAhead.setOnClickListener((view) ->
                MyConexionBT.write("1")
        );
        btnBack.setOnClickListener((view) ->
                MyConexionBT.write("2")
        );
        btnLeft.setOnClickListener((view) ->
                MyConexionBT.write("3")
        );
        btnRight.setOnClickListener((view) ->
                MyConexionBT.write("4")
        );
        btnStop.setOnClickListener((view) ->
                MyConexionBT.write("0")
        );
    }

    public void camera() {

        camera = findViewById(R.id.wv_camera);
        camera.loadUrl("http://192.168.1.106:8081");

    }


}
