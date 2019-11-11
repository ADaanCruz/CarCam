package com.example.bluetooth;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ConnectedThread extends Thread {
    private Handler bluetoothIn;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    final int handlerState = 0;

    public ConnectedThread(BluetoothSocket socket,    Handler bluetoothIn) {
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
        }
        this.bluetoothIn = bluetoothIn;
        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }

    public void run() {
        byte[] buffer = new byte[256];
        int bytes;

        // Se mantiene en modo escucha para determinar el ingreso de datos
        while (true) {
            try {
                bytes = mmInStream.read(buffer);
                String readMessage = new String(buffer, 0, bytes);
                // Envia los datos obtenidos hacia el evento via handler
                bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
            } catch (IOException e) {
                break;
            }
        }
    }

    //Envio de trama
    public void write(String input) {
        try {
            mmOutStream.write(input.getBytes());
            System.out.println("Enviado");
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
