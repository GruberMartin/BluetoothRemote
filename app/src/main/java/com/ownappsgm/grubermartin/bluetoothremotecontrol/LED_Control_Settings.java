package com.ownappsgm.grubermartin.bluetoothremotecontrol;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LED_Control_Settings extends AppCompatActivity implements View.OnClickListener {

    TextView tvUserHintLedSettings;
    Button btnLedOnSetting, btnLedOffSetting, btnSaveLedSettings;
    EditText etLedOnCommand, etLedOffCommand;

    BluetoothDevice savedBTdevice;

    final String PrefBtnCommandOnKey = "btnCommandOnKey";
    final String PrefBtnCommandOffKey = "btnCommandOffKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_led__control__settings);
        tvUserHintLedSettings = (TextView) findViewById(R.id.tvUserHintLedSettings);
        btnLedOnSetting = (Button) findViewById(R.id.btnLedOnSetting);
        btnLedOffSetting = (Button) findViewById(R.id.btnLedOffSetting);
        btnSaveLedSettings = (Button) findViewById(R.id.btnSaveLedSettings);
        etLedOnCommand = (EditText) findViewById(R.id.etLedOnCommand);
        etLedOffCommand = (EditText) findViewById(R.id.etLedOffCommand);
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        etLedOffCommand.setText(prefs.getString(PrefBtnCommandOffKey,"a"));
        etLedOnCommand.setText(prefs.getString(PrefBtnCommandOnKey,"e"));
        etLedOffCommand.setOnClickListener(this);
        etLedOnCommand.setOnClickListener(this);
        btnSaveLedSettings.setOnClickListener(this);
        etLedOnCommand.setCursorVisible(false);
        etLedOffCommand.setCursorVisible(false);

        savedBTdevice = getIntent().getExtras().getParcelable("saveCurrentDevice");
    }


    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.etLedOffCommand:
                etLedOffCommand.setCursorVisible(true);
                etLedOffCommand.setFocusable(true);
                break;
            case R.id.etLedOnCommand:
                etLedOnCommand.setCursorVisible(true);
                etLedOnCommand.setFocusable(true);
                break;
            case R.id.btnSaveLedSettings:
                final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(PrefBtnCommandOnKey,etLedOnCommand.getText().toString());
                editor.putString(PrefBtnCommandOffKey,etLedOffCommand.getText().toString());
                editor.commit();
                Intent goToLEDControl = new Intent(this,LED_Control.class);
                goToLEDControl.putExtra("bluetoothDevice",savedBTdevice);
                startActivity(goToLEDControl);
                finish();
                break;
        }
    }
}
