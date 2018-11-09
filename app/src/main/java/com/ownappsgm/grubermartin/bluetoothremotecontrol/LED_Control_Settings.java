package com.ownappsgm.grubermartin.bluetoothremotecontrol;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LED_Control_Settings extends AppCompatActivity implements View.OnClickListener, TextView.OnEditorActionListener, View.OnTouchListener {

    TextView tvUserHintLedSettings;
    Button btnLedOnSetting, btnLedOffSetting, btnSaveLedSettings;
    EditText etLedOnCommand, etLedOffCommand;
    ConstraintLayout led_control_constraint_layout;

    BluetoothDevice savedBTdevice;

    final String PrefBtnCommandOnKey = "btnCommandOnKey";
    final String PrefBtnCommandOffKey = "btnCommandOffKey";
    Boolean testBool;
    Boolean onTouchedCalled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_led__control__settings);
        led_control_constraint_layout = (ConstraintLayout) findViewById(R.id.led_control_constraint_layout);
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
        etLedOnCommand.setOnEditorActionListener(this);
        etLedOffCommand.setOnEditorActionListener(this);
        etLedOnCommand.setOnTouchListener(this);
        etLedOffCommand.setOnTouchListener(this);
        etLedOnCommand.setOnClickListener(this);
        btnSaveLedSettings.setOnClickListener(this);
        etLedOnCommand.setCursorVisible(false);
        etLedOffCommand.setCursorVisible(false);
        etLedOffCommand.setFocusableInTouchMode(true);
        etLedOnCommand.setFocusableInTouchMode(true);
        led_control_constraint_layout.setOnTouchListener(this);

        savedBTdevice = getIntent().getExtras().getParcelable("saveCurrentDevice");
    }



    @Override
    public boolean onTouch(View v, MotionEvent event) {
        onTouchedCalled = false;
        switch(v.getId())
        {
            case R.id.led_control_constraint_layout:
                hideKeyboard(this);
                etLedOffCommand.clearFocus();
                etLedOnCommand.clearFocus();
                etLedOnCommand.setCursorVisible(false);
                etLedOffCommand.setCursorVisible(false);
                break;

            case R.id.etLedOffCommand:
                etLedOffCommand.requestFocus();
                etLedOffCommand.setCursorVisible(true);
                etLedOffCommand.setSelection(etLedOffCommand.getText().length());
                break;

            case R.id.etLedOnCommand:
                etLedOnCommand.requestFocus();
                etLedOnCommand.setCursorVisible(true);
                etLedOnCommand.setSelection(etLedOnCommand.getText().length());
                break;
        }
        return onTouchedCalled;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.etLedOffCommand:
//                etLedOffCommand.setCursorVisible(true);
                etLedOffCommand.requestFocus();
                etLedOffCommand.setCursorVisible(true);
                etLedOffCommand.setSelection(etLedOffCommand.getText().length());
                break;
            case R.id.etLedOnCommand:
//                etLedOnCommand.setCursorVisible(true);
                etLedOnCommand.requestFocus();
                etLedOnCommand.setCursorVisible(true);
                etLedOnCommand.setSelection(etLedOnCommand.getText().length());
                break;
            case R.id.btnSaveLedSettings:
                hideKeyboard(this);
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

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        boolean handled = false;

        if (actionId == EditorInfo.IME_ACTION_NEXT) {
            etLedOffCommand.requestFocus();
            etLedOffCommand.setCursorVisible(true);
            etLedOffCommand.setSelection(etLedOffCommand.getText().length());

            handled = true;
        }

        if(actionId == EditorInfo.IME_ACTION_DONE)
        {
            /*etLedOnCommand.setCursorVisible(false);
            etLedOnCommand.setFocusable(false);
            etLedOffCommand.setCursorVisible(false);
            etLedOffCommand.setSelected(false);
            etLedOffCommand.setFocusable(false);*/
            performClick(btnSaveLedSettings);


            handled = true;
        }
        return handled;

    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static boolean performClick(View view) {
        return view.isEnabled() && view.isClickable() && view.performClick();
    }


}
