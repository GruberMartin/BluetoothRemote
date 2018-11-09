package com.ownappsgm.grubermartin.bluetoothremotecontrol;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AlertDialogLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

    char onComand;
    char offCommand;
    final String PrefBtnCommandOnKey = "btnCommandOnKey";
    final String PrefBtnCommandOffKey = "btnCommandOffKey";
    Boolean deviceNeedsPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_led__control);
        tvActionDescriptionLedControl = (TextView) findViewById(R.id.tvActionDescriptionLedControl);
        btnLedOnLedControl = (Button) findViewById(R.id.btnLedOnLedControl);
        btnLedOffLedControl = (Button) findViewById(R.id.btnLedOffLedControl);
        ivIndicatingLedState = (ImageView) findViewById(R.id.ivIndicatingLedState);
        ivIndicatingLedState.setImageResource(R.drawable.greenledoff);
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        onComand = prefs.getString(PrefBtnCommandOnKey,"e").charAt(0);
        offCommand = prefs.getString(PrefBtnCommandOffKey,"a").charAt(0);
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
        finish();

    }



    @Override
    protected void onRestart() {
        super.onRestart();
        Intent returnToMain = new Intent(this, MainActivity.class);
        startActivity(returnToMain);
        finish();
    }

    public void sendData(char msg) throws IOException{
        mmOutputStream.write(msg);
    }

    public void OnBtnLedOnClicked(View v)
    {
        try {
            sendData(onComand);
            ivIndicatingLedState.setImageResource(R.drawable.greenledon);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Senden fehlgeschlagen", Toast.LENGTH_SHORT).show();

        }
    }

    public void OnBtnLedOffClicked(View v)
    {
        try {
            sendData(offCommand);
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
            progress.setTitle("Verbinde mit " + mmDevice.getName());
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
                //Toast.makeText(LED_Control.this, "Verbindung erfolgreich", Toast.LENGTH_SHORT).show();
                btnLedOnLedControl.setEnabled(true);
                btnLedOffLedControl.setEnabled(true);
            }
            else
            {
                progress.dismiss();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LED_Control.this,android.R.style.Theme_Material_Dialog_Alert);
                alertDialogBuilder.setMessage("Möchten Sie es erneut versuchen?");
                alertDialogBuilder.setTitle("Verbinden zu " + mmDevice.getName() + " fehlgeschlagen");

                alertDialogBuilder.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MyAsyncTask newTry = new MyAsyncTask(mmDevice);
                        newTry.execute();
                    }
                });
                alertDialogBuilder.setNegativeButton("Nein", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        finish();


                    }
                });
                AlertDialog newAlertDialog =  alertDialogBuilder.create();

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
                Log.e("tag1","Problem mit Socket");
            }
            try {

                mmSocket.connect();
                connectionState = true;
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("tag2","Problem mit connect");
                deviceNeedsPassword = true;
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.led_control_menu,menu);

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selectedItem = item.getItemId();

        if(selectedItem == R.id.displayCommandChars)
        {
            Intent goToLedControlSettings = new Intent(this, LED_Control_Settings.class);
            goToLedControlSettings.putExtra("saveCurrentDevice",mmDevice);
            startActivity(goToLedControlSettings);
        }

        return true;
    }

    public void handelPasswordInput(BluetoothSocket socket)
    {
        BluetoothSocket newSocket = socket;
        Toast.makeText(this, newSocket.isConnected() + "", Toast.LENGTH_SHORT).show();
    }


}
