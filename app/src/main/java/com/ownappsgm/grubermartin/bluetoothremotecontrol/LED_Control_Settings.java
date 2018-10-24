package com.ownappsgm.grubermartin.bluetoothremotecontrol;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LED_Control_Settings extends AppCompatActivity {

    TextView tvUserHintLedSettings;
    Button btnLedOnSetting, btnLedOffSetting;
    EditText etLedOnCommand, etLedOffCommand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_led__control__settings);
        tvUserHintLedSettings = (TextView) findViewById(R.id.tvUserHintLedSettings);
        btnLedOnSetting = (Button) findViewById(R.id.btnLedOnSetting);
        btnLedOffSetting = (Button) findViewById(R.id.btnLedOffSetting);
        etLedOnCommand = (EditText) findViewById(R.id.etLedOnCommand);
        etLedOffCommand = (EditText) findViewById(R.id.etLedOffCommand);
    }
}
