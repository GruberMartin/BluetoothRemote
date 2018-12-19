package com.ownappsgm.grubermartin.bluetoothremotecontrol;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.UUID;

public class Terminal extends AppCompatActivity {
    TextView tvTerminalInfo, mlMultiLineTerminal;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    private byte[] mmBuffer; // mmBuffer store for the stream

    // Defines several constants used when transmitting messages between the
    // service and the UI.
    private interface MessageConstants {
        public static final int MESSAGE_READ = 0;
        public static final int MESSAGE_WRITE = 1;
        public static final int MESSAGE_TOAST = 2;

        // ... (Add other message types here as needed.)
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terminal);
        tvTerminalInfo = (TextView) findViewById(R.id.tvTerminalInfo);
        mlMultiLineTerminal = (TextView) findViewById(R.id.mlMultiLineTerminal);
        Intent recieveDevice = getIntent();

        mmDevice = recieveDevice.getExtras().getParcelable("bluetoothDevice");

        if (mmDevice != null) {
            Terminal.MyAsyncTask myAsyncTask = new Terminal.MyAsyncTask(mmDevice);
            myAsyncTask.execute();

        } else {
            Toast.makeText(this, "Es wurde kein Gerät übergeben", Toast.LENGTH_SHORT).show();
        }
    }

    private class MyAsyncTask extends AsyncTask<Void, Void, Boolean> {
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
            progress = new ProgressDialog(Terminal.this);
            progress.setTitle("Verbinde mit " + mmDevice.getName());
            progress.setMessage("Bitte warten");
            progress.setCancelable(false);
            progress.show();

        }

        @Override
        protected void onPostExecute(Boolean connectionState) {
            super.onPostExecute(connectionState);
            if (connectionState) {
                progress.dismiss();
                RecieveMessage receiver = new RecieveMessage(mmInputStream);
                receiver.start();
            } else {
                progress.dismiss();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Terminal.this, android.R.style.Theme_Material_Dialog_Alert);
                alertDialogBuilder.setMessage("Möchten Sie es erneut versuchen?");
                alertDialogBuilder.setTitle("Verbinden zu " + mmDevice.getName() + " fehlgeschlagen");

                alertDialogBuilder.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Terminal.MyAsyncTask newTry = new Terminal.MyAsyncTask(mmDevice);
                        newTry.execute();
                    }
                });
                alertDialogBuilder.setNegativeButton("Nein", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        finish();


                    }
                });
                AlertDialog newAlertDialog = alertDialogBuilder.create();

                newAlertDialog.show();


            }

        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); //Standard SerialPortService ID
            try {
                mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);

            } catch (IOException e) {
                e.printStackTrace();
                Log.e("tag1", "Problem mit Socket");
            }
            try {

                mmSocket.connect();
                connectionState = true;
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("tag2", "Problem mit connect");
                //handelPasswordInput(mmSocket);
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

    private class RecieveMessage extends Thread {

        InputStream mmInStream;
        int a;
        String b;



        private Handler mHandler; // handler that gets info from Bluetooth service


        RecieveMessage(InputStream iStream) {
            mmInStream = iStream;
        }

        public void run() {
            mmBuffer = new byte[1024];
            int numBytes; // bytes returned from read()
            String line;

            while (true) {
                try {
                    numBytes = mmInputStream.read();

                } catch (IOException e) {
                    //Log.d(TAG, "Input stream was disconnected", e);
                    break;
                }

            }
        }
    }
}
