package com.ownappsgm.grubermartin.bluetoothremotecontrol;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;


    Spinner spDeviceListMain, spActionSelectMain, spDiscoveredDevicesMain;
    TextView tvSelectDeviceMain, tvChooseAnActionMain, tvDiscoveredDevicesMain;
    Button btnConnectMain, btnFindDevicesMain, btnConnectToNewDeviceMain;

    List<String> foundedDevicesList;
    List<String> deviceMACadresses;
    List<BluetoothDevice> bluetoothDevices;
    BluetoothDevice mmDevice;
    String selectedAction;
    List<String> supportedActions;
    List <String> newDevice;
    ArrayAdapter<String> devNameAdapter;

    int REQUEST_ENABLE_BT = 2; // erhält Result Code

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        spDeviceListMain = (Spinner) findViewById(R.id.spDeviceListMain);
        spActionSelectMain = (Spinner) findViewById(R.id.spActionSelectMain);
        spDiscoveredDevicesMain = (Spinner) findViewById(R.id.spDiscoveredDevicesMain);
        tvSelectDeviceMain = (TextView) findViewById(R.id.tvSelectDeviceMain);
        tvChooseAnActionMain = (TextView) findViewById(R.id.tvChooseAnActionMain);
        tvDiscoveredDevicesMain = (TextView) findViewById(R.id.tvDiscoveredDevicesMain);
        btnConnectMain = (Button) findViewById(R.id.btnConnectMain);
        btnFindDevicesMain = (Button) findViewById(R.id.btnFindDevicesMain);
        btnConnectToNewDeviceMain = (Button) findViewById(R.id.btnConnectToNewDeviceMain);
        generateSupportedActionsList();
        btnConnectMain.setEnabled(false);
        checkForBluetooth();
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        registerReceiver(mReceiver,filter);
        //filter.addAction(BluetoothDevice.ACTION_FOUND);
        newDevice = new ArrayList<String>();
        devNameAdapter = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1,newDevice);
        spDiscoveredDevicesMain.setAdapter(devNameAdapter);


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

    public void OnBtnFindDevicesClicked(View v)
    {
        askForBluetoothPermission();
        newDevice.clear();
    }

    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver mReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action))
            {
                BluetoothDevice newDiscoveredDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                String newBTdevice =newDiscoveredDevice.getName();
                newDevice.add(newBTdevice);
                Toast.makeText(context, "Gerät " + newBTdevice +" gefunden", Toast.LENGTH_SHORT).show();
                devNameAdapter.notifyDataSetChanged();


            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))
            {
                Toast.makeText(context, "Suche beendet", Toast.LENGTH_SHORT).show();


            }
            else if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action))
            {
                Toast.makeText(context, "Beginne mit der Suche", Toast.LENGTH_SHORT).show();
            }
        }
    };

    public void askForBluetoothPermission()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            // Permission is not granted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
        }
        else
        {
            BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
            btAdapter.startDiscovery();
        }

    }

    public void connectToDiscoveredBluetoothDevice(BluetoothDevice newDevice) throws IOException {

        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); //Standard SerialPortService ID
        BluetoothSocket mmSocket = newDevice.createRfcommSocketToServiceRecord(uuid);
        mmSocket.connect();
        OutputStream mmOutputStream = mmSocket.getOutputStream();
        InputStream mmInputStream = mmSocket.getInputStream();

        //Optional

        Toast.makeText(getApplicationContext(), "Verbindung wurde hergestellt", Toast.LENGTH_SHORT).show();

    }



}
