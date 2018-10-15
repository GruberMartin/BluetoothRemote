package com.ownappsgm.grubermartin.bluetoothremotecontrol;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
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
            MyAsyncTask myAsyncTask = new MyAsyncTask(mmDevice);
            myAsyncTask.execute();

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

    private class MyAsyncTask extends AsyncTask<Void,Void,Boolean>
    {
        BluetoothDevice mmDevice = null;
        Boolean connectionState = false;
        ProgressDialog progress;


        public MyAsyncTask(BluetoothDevice deviceToConnectTo) {
            super();
            mmDevice = deviceToConnectTo;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            btnLedOnLedControl.setEnabled(false);
            btnLedOffLedControl.setEnabled(false);
             progress = new ProgressDialog(LED_Control.this);
            progress.setTitle("Verbinde mit Bluetoothgerät");
            progress.setMessage("Bitte warten");
            progress.setCancelable(false);
            progress.show();

        }

        @Override
        protected void onPostExecute(Boolean connectionState) {
            super.onPostExecute(connectionState);
            if(connectionState)
            {
                progress.dismiss();
                Toast.makeText(LED_Control.this, "Verbindung erfolgreich", Toast.LENGTH_SHORT).show();
                btnLedOnLedControl.setEnabled(true);
                btnLedOffLedControl.setEnabled(true);
            }
            else
            {
                progress.dismiss();
                Toast.makeText(LED_Control.this, "Verbindung fehlgeschlagen", Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); //Standard SerialPortService ID
            try {
                mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);

            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                mmSocket.connect();
                connectionState = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                mmOutputStream = mmSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                mmInputStream = mmSocket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return connectionState;
        }
    }




}
