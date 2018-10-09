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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {



    // region Deklaration von Variablen

    Spinner spDeviceListMain, spActionSelectMain, spDiscoveredDevicesMain;
    TextView tvPairedDeviceHintMain, tvSelectDeviceMain, tvChooseAnActionMain, tvSearchForDevicesHintMain, tvDiscoveredDevicesMain;
    Button btnConnectMain, btnFindDevicesMain, btnConnectToNewDeviceMain;

    Map<String,BluetoothDevice> pairedDevicesMap;
    List<String> pairedDeviceNamesList;
    List<BluetoothDevice> newDevicesObject;
    BluetoothDevice mmDevice;
    String selectedAction;
    List<String> supportedActions;
    List <String> newDevicesName;
    ArrayAdapter<String> devNameAdapter;

    int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
    int REQUEST_ENABLE_BT = 2; // erhält Result Code
    OutputStream mmOutputStream = null;
    InputStream mmInputStream = null;
    BluetoothSocket mmSocket = null;
    // endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // region Initialisierung aller Widgets
        spDeviceListMain = (Spinner) findViewById(R.id.spDeviceListMain);
        spActionSelectMain = (Spinner) findViewById(R.id.spActionSelectMain);
        spDiscoveredDevicesMain = (Spinner) findViewById(R.id.spDiscoveredDevicesMain);
        tvSelectDeviceMain = (TextView) findViewById(R.id.tvSelectDeviceMain);
        tvChooseAnActionMain = (TextView) findViewById(R.id.tvChooseAnActionMain);
        tvDiscoveredDevicesMain = (TextView) findViewById(R.id.tvDiscoveredDevicesMain);
        tvPairedDeviceHintMain = (TextView) findViewById(R.id.tvPairedDeviceHintMain);
        tvSearchForDevicesHintMain = (TextView) findViewById(R.id.tvSearchForDevicesHintMain);
        btnConnectMain = (Button) findViewById(R.id.btnConnectMain);
        btnFindDevicesMain = (Button) findViewById(R.id.btnFindDevicesMain);
        btnConnectToNewDeviceMain = (Button) findViewById(R.id.btnConnectToNewDeviceMain);
        // endregion
                                                        btnConnectMain.setEnabled(false);
        checkForBluetooth();

        //region Registrierung eines BroadcastReceivers um neue Geräte zu finden
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        registerReceiver(mReceiver,filter);
        //endregion
        generateListWithNewBluetoothDeviceNames();
        generateListWithNewBluetoothDeviceObjects();
        generateListWithActions();
        generateListWithPairedDevices();
        fillListWithPairedDevices();

                                                        btnConnectToNewDeviceMain.setEnabled(false);

    }

    public void generateListWithNewBluetoothDeviceNames()
    {
        newDevicesName = new ArrayList<String>();
        devNameAdapter = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1, newDevicesName);
        spDiscoveredDevicesMain.setAdapter(devNameAdapter);
    }

    public void generateListWithNewBluetoothDeviceObjects()
    {
        newDevicesObject = new ArrayList<BluetoothDevice>();
    }

    public void generateListWithPairedDevices()
    {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        pairedDevicesMap = new HashMap<>();
        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            pairedDeviceNamesList = new ArrayList<String>();
            for (BluetoothDevice device : pairedDevices)
            {
                pairedDevicesMap.put(device.getName(),device);
                pairedDeviceNamesList.add(device.getName());
            }
        }
        else
        {
            // Es wurden keine gekoppelten Geräte gefunden
            Toast.makeText(this, "Es wurden keine verbundenen Geräte gefunden", Toast.LENGTH_SHORT).show();
        }
    }

    public void fillListWithPairedDevices()
    {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, pairedDeviceNamesList);
        spDeviceListMain.setAdapter(arrayAdapter);
        spDeviceListMain.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                mmDevice = pairedDevicesMap.get(pairedDeviceNamesList.get(position).toString());
                btnConnectMain.setEnabled(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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
                generateListWithPairedDevices();

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
                generateListWithPairedDevices();


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
                String deliveringDevice = mmDevice.getName();
                startActivity(changeToActionActivity);
            break;
            default:
                Toast.makeText(this, "Es wurde keine Aktion ausgewählt", Toast.LENGTH_SHORT).show();
                break;
        }

    }

    public void generateListWithActions()
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
        newDevicesName.clear();
        newDevicesObject.clear();
        btnConnectToNewDeviceMain.setEnabled(false);
    }

    //region Implementierung des Broadcast Receivers
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
                newDevicesName.add(newBTdevice);
                newDevicesObject.add(newDiscoveredDevice);
                Toast.makeText(context, "Gerät " + newBTdevice +" gefunden", Toast.LENGTH_SHORT).show();
                devNameAdapter.notifyDataSetChanged();
                btnConnectToNewDeviceMain.setEnabled(true);


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
    // endregion

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

    public void onBtnConnectToNewDeviceClicked(View v)
    {

        int pos = spDiscoveredDevicesMain.getSelectedItemPosition();
        if(!pairedDevicesMap.containsKey(newDevicesObject.get(pos).getName())) {
            mmDevice = newDevicesObject.get(pos);
            changeToSelctedActionActivity();
            pairedDevicesMap.put(newDevicesObject.get(pos).getName().toString(), newDevicesObject.get(pos));
            pairedDeviceNamesList.add(newDevicesObject.get(pos).getName());
            fillListWithPairedDevices();
        }
        else
        {
            Toast.makeText(this, "Dieses Gerät wurde bereits der Liste von gekoppelten Geräten hinzugefügt", Toast.LENGTH_SHORT).show();
        }


    }







}
