package com.example.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import java.util.Set;

public class Bluetooth {

    BluetoothAdapter adapter;

     public Bluetooth(){
         adapter = BluetoothAdapter.getDefaultAdapter();

     }
    public Set<BluetoothDevice> getListBluetooth() {
        return adapter.getBondedDevices();
    }

    public BluetoothAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(BluetoothAdapter adapter) {
        this.adapter = adapter;
    }
}
