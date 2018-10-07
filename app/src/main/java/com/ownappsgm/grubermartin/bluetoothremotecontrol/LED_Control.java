package com.ownappsgm.grubermartin.bluetoothremotecontrol;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class LED_Control extends AppCompatActivity {

    TextView tvActionDescriptionLedControl;
    Button btnLedOnLedControl, btnLedOffLedControl;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    OutputStream mmOutputStream;
    InputStream mmInputStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_led__control);
        tvActionDescriptionLedControl = (TextView) findViewById(R.id.tvActionDescriptionLedControl);
        btnLedOnLedControl = (Button) findViewById(R.id.btnLedOnLedControl);
        btnLedOffLedControl = (Button) findViewById(R.id.btnLedOffLedControl);
        Intent recieveDevice = getIntent();
        mmDevice = recieveDevice.getExtras().getParcelable("bluetoothDevice");
        if(mmDevice != null)
        {
            try {
                openBT();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Kann keine Bluetoothverbindung herstellen", Toast.LENGTH_SHORT).show();
                btnLedOnLedControl.setEnabled(false);
                btnLedOffLedControl.setEnabled(false);
            }
        }
    }

    public void openBT() throws IOException {
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); //Standard SerialPortService ID
        mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
        mmSocket.connect();
        mmOutputStream = mmSocket.getOutputStream();
        mmInputStream = mmSocket.getInputStream();

        //Optional

        Toast.makeText(getApplicationContext(), "Verbindung wurde hergestellt", Toast.LENGTH_SHORT).show();

    }

    public void sendData(char msg) throws IOException{
        mmOutputStream.write(msg);
    }

    public void OnBtnLedOnClicked(View v)
    {
        try {
            sendData('e');
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Senden fehlgeschlagen", Toast.LENGTH_SHORT).show();
        }
    }

    public void OnBtnLedOffClicked(View v)
    {
        try {
            sendData('a');
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Senden fehlgeschlagen", Toast.LENGTH_SHORT).show();
        }
    }
}
