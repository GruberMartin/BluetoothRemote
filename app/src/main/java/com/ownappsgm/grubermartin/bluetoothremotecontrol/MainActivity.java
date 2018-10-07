package com.ownappsgm.grubermartin.bluetoothremotecontrol;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    Spinner spDeviceListMain, spActionSelectMain;
    TextView tvSelectDeviceMain, tvChooseAnActionMain;
    Button btnConnectMain, btnSettingsMain;

    List<String> foundedDevicesList;
    List<String> deviceMACadresses;
    List<BluetoothDevice> bluetoothDevices;
    BluetoothDevice mmDevice;
    String selectedAction;
    List<String> supportedActions;

    int REQUEST_ENABLE_BT = 2; // erhält Result Code

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        spDeviceListMain = (Spinner) findViewById(R.id.spDeviceListMain);
        spActionSelectMain = (Spinner) findViewById(R.id.spActionSelectMain);
        tvSelectDeviceMain = (TextView) findViewById(R.id.tvSelectDeviceMain);
        tvChooseAnActionMain = (TextView) findViewById(R.id.tvChooseAnActionMain);
        btnConnectMain = (Button) findViewById(R.id.btnConnectMain);
        btnSettingsMain = (Button) findViewById(R.id.btnSettingsMain);
        generateSupportedActionsList();
        btnConnectMain.setEnabled(false);
        checkForBluetooth();
    }

    public void checkForPairedDevices()
    {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            foundedDevicesList = new ArrayList<String>();
            deviceMACadresses = new ArrayList<String>();
            bluetoothDevices = new ArrayList<BluetoothDevice>();
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                foundedDevicesList.add(deviceName);
                deviceMACadresses.add(deviceHardwareAddress);
                bluetoothDevices.add(device);

            }
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,foundedDevicesList);
            spDeviceListMain.setAdapter(arrayAdapter);
            spDeviceListMain.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    String chlickedItemName = foundedDevicesList.get(position);
                    String selectedDevicesMacAddress = deviceMACadresses.get(position);
                    mmDevice = bluetoothDevices.get(position);
                    btnConnectMain.setEnabled(true);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
        else
        {
            // Es wurden keine gekoppelten Geräte gefunden
            foundedDevicesList.add("Es wurden keine Geräte gefunden");
        }
    }

    public void checkForBluetooth()
    {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Ihr Gerät unterstützt kein Bluetooth", Toast.LENGTH_SHORT).show();
        }
        else
        {
            // Überprüfen, ob Bluetooth eingeschaltet ist
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
            else
            {
                // Wenn Bluetooth eingeschaltet wurde, soll überprüft werden ob Geräte bereits gekoppelt sind
                checkForPairedDevices();

            }

        }
    }

    // Überprüft ob Bluetooth verwendet werden kann und handelt entsprechend
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Überprüfen welche Antwort die enableBluetooth Anfrage geliefert hat
        if(requestCode == REQUEST_ENABLE_BT)
        {
            if(resultCode == RESULT_OK)
            {
                // Wenn Bluetooth eingeschaltet wurde, soll der ConnectButton nicht mehr ausgegraut bleiben und alle gekoppelten
                // Geräte sollen aufgelistet werden
                checkForPairedDevices();


            }
            else if(resultCode == RESULT_CANCELED)
            {
                Toast.makeText(this, "Wenn du Bluetooth nicht einschaltest, kannst du nicht weiterfahren!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void changeToSelctedActionActivity()
    {
        switch (selectedAction)
        {
            case "LED_Control":
                Intent changeToActionActivity = new Intent(this,LED_Control.class);
                changeToActionActivity.putExtra("bluetoothDevice",mmDevice);
                startActivity(changeToActionActivity);
            break;
            default:
                Toast.makeText(this, "Es wurde keine Aktion ausgewählt", Toast.LENGTH_SHORT).show();
                break;
        }

    }

    public void generateSupportedActionsList()
    {
        supportedActions = new ArrayList<String>();
        supportedActions.add("LED_Control");
        ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,supportedActions);
        spActionSelectMain.setAdapter(arrayAdapter2);
        spActionSelectMain.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedAction = supportedActions.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void OnBtnConnectClicked(View v)
    {
        changeToSelctedActionActivity();
    }

    public void OnBtnSettingsClicked(View v)
    {

    }
}
