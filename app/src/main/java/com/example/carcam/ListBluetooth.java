package com.example.carcam;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bluetooth.Bluetooth;
import com.example.bluetooth.MainInterface;

import java.io.IOException;
import java.util.Set;

public class ListBluetooth extends AppCompatActivity {
    Set<BluetoothDevice> pairedDevices;
    Bluetooth bluetooth;
    ArrayAdapter mPairedDevicesArrayAdapter;
    ListView lista;
    public final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                pairedDevices.add(device);
                String deviceName = device.getName();
                String deviceAddress = device.getAddress();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.bluetooth.R.layout.activity_list_bluetooth);

        bluetooth = new Bluetooth();

        if (bluetooth.getAdapter() != null) {
            if (!bluetooth.getAdapter().isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            } else {
                mPairedDevicesArrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, android.R.id.text1);
                pairedDevices = bluetooth.getListBluetooth();
                if (pairedDevices.size() > 0) {
                    for (BluetoothDevice device : pairedDevices) {
                        String deviceName = device.getName();
                        String deviceHardwareAddress = device.getAddress(); // MAC address
                        mPairedDevicesArrayAdapter.add(deviceName + "\n" + deviceHardwareAddress);
                    }
                }
                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(receiver, filter);
                lista = findViewById(com.example.bluetooth.R.id.listBluetooth);
                lista.setAdapter(mPairedDevicesArrayAdapter);
                lista.setOnItemClickListener((parent, view, position, id) -> {
                    String info = ((TextView) view).getText().toString();
                    String address = info.substring(info.length() - 17);
                    Toast.makeText(getApplicationContext(), address, Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(view.getContext(), MainActivity.class);
                    i.putExtra("address", address);
                    startActivity(i);
                });
            }
        } else {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(receiver);
    }

    public class  AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            BluetoothServerSocket tmp = null;
            mmServerSocket = tmp;
        }

        public void run(){
            BluetoothSocket socket = null;
            while(true){
                try {
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    Log.e("error_blueth", "Socket's accept() method failed", e);
                    break;
                }

                if (socket != null) {
                    // A connection was accepted. Perform work associated with
                    // the connection in a separate thread.
                    //manageMyConnectedSocket(socket);
                    try {
                        mmServerSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }

        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e("error", "Could not close the connect socket", e);
            }
        }

    }

}
