package com.ownappsgm.grubermartin.bluetoothremotecontrol;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
    ImageView ivIndicatingLedState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_led__control);
        tvActionDescriptionLedControl = (TextView) findViewById(R.id.tvActionDescriptionLedControl);
        btnLedOnLedControl = (Button) findViewById(R.id.btnLedOnLedControl);
        btnLedOffLedControl = (Button) findViewById(R.id.btnLedOffLedControl);
        ivIndicatingLedState = (ImageView) findViewById(R.id.ivIndicatingLedState);
        ivIndicatingLedState.setImageResource(R.drawable.greenledoff);
        Intent recieveDevice = getIntent();
        mmDevice = recieveDevice.getExtras().getParcelable("bluetoothDevice");
        if(mmDevice != null)
        {
            try {
                openBT();
            } catch (IOException e) {
                e.printStackTrace();
                //Toast.makeText(this, "Kann keine Bluetoothverbindung herstellen", Toast.LENGTH_SHORT).show();
                btnLedOnLedControl.setEnabled(false);
                btnLedOffLedControl.setEnabled(false);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setMessage("Es konnte keine Verbindung aufgebaut werden, möchtest du es erneut versuche?");
                alertDialogBuilder.setTitle("Verbindungsversuch fehlgeschlagen");
                alertDialogBuilder.setIcon(R.drawable.alert);

                alertDialogBuilder.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            Toast.makeText(LED_Control.this, "Erneuter Verbindungsversuch bitte warten!", Toast.LENGTH_LONG).show();
                            openBT();
                        } catch (IOException e1) {
                            Toast.makeText(LED_Control.this, "Das hat leider immernoch nicht funktioniert", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LED_Control.this, MainActivity.class));
                        }
                    }
                });
                alertDialogBuilder.setNegativeButton("Nein", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(LED_Control.this, MainActivity.class));
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        }
        else
        {
            Toast.makeText(this, "Es wurde kein Gerät übergeben", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        resetConnection();
    }

    public void openBT() throws IOException {
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); //Standard SerialPortService ID
        mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
        mmSocket.connect();
        mmOutputStream = mmSocket.getOutputStream();
        mmInputStream = mmSocket.getInputStream();

        //Optional

        Toast.makeText(getApplicationContext(), "Verbindung wurde hergestellt", Toast.LENGTH_SHORT).show();
        btnLedOnLedControl.setEnabled(true);
        btnLedOffLedControl.setEnabled(true);

    }

    public void sendData(char msg) throws IOException{
        mmOutputStream.write(msg);
    }

    public void OnBtnLedOnClicked(View v)
    {
        try {
            sendData('e');
            ivIndicatingLedState.setImageResource(R.drawable.greenledon);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Senden fehlgeschlagen", Toast.LENGTH_SHORT).show();

        }
    }

    public void OnBtnLedOffClicked(View v)
    {
        try {
            sendData('a');
            ivIndicatingLedState.setImageResource(R.drawable.greenledoff);

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Senden fehlgeschlagen", Toast.LENGTH_SHORT).show();

        }
    }

    private void resetConnection() {
        if (mmInputStream != null) {
            try {mmInputStream.close();} catch (Exception e) {
                Toast.makeText(this, "Inputstream konnte nicht geschlossen werden", Toast.LENGTH_SHORT).show();
            }
            mmInputStream = null;
        }

        if (mmOutputStream != null) {
            try {mmOutputStream.close();} catch (Exception e) {
                Toast.makeText(this, "Outputstream konnte nicht geschlossen werden", Toast.LENGTH_SHORT).show();
            }
            mmOutputStream = null;
        }

        if (mmSocket != null) {
            try {mmSocket.close();} catch (Exception e) {
                Toast.makeText(this, "Socket konnte nicht geschlossen werden", Toast.LENGTH_SHORT).show();
            }
            mmSocket = null;
        }

    }
}
