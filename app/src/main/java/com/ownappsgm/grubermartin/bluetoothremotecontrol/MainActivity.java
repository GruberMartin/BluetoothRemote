package com.ownappsgm.grubermartin.bluetoothremotecontrol;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    ArrayList<String> updateForPairedDevicesList;
    final String prefPairedDevicesNameListKey = "pairedDevicesNameList";

    int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
    int REQUEST_ENABLE_BT = 2; // erhält Result Code
    static final int FilteredPairedDevicesListRequest = 4;
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
        setTitle("Bluetooth Geräte Steuern");
        // endregion
        btnConnectMain.setEnabled(false);
        checkForBluetooth();
        //region Registrierung eines BroadcastReceivers um neue Geräte zu finden
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        registerReceiver(mReceiver,filter);
        //endregion
        //region Listen generieren und füllen
        generateListWithNewBluetoothDeviceNames();
        generateListWithNewBluetoothDeviceObjects();
        generateListWithActions();
        generateListWithPairedDevices();
        fillListWithPairedDevices();
        //endregion
        btnConnectToNewDeviceMain.setEnabled(false);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.mainmenu,menu);

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selectedItem = item.getItemId();

        if(selectedItem == R.id.displayedDevicesInPairedListMenuItem)
        {
            Intent goToSettingsActivity = new Intent(this,Settings.class);
            goToSettingsActivity.putExtra("pairedDevicesList", (Serializable) castSetToList(pairedDevicesMap.keySet()));

            startActivityForResult(goToSettingsActivity,FilteredPairedDevicesListRequest);
        }

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        generateListWithPairedDevices();
        fillListWithPairedDevices();

    }

    @Override
    protected void onPause() {
        super.onPause();
        clearListWithNewDeviceNames();

    }

    public void generateListWithNewBluetoothDeviceNames()
    {
        newDevicesName = new ArrayList<String>();
        devNameAdapter = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1, newDevicesName);
        spDiscoveredDevicesMain.setAdapter(devNameAdapter);
    }

    public void clearListWithNewDeviceNames()
    {
        newDevicesName.clear();
        devNameAdapter.notifyDataSetChanged();

    }



    public void generateListWithNewBluetoothDeviceObjects()
    {
        newDevicesObject = new ArrayList<BluetoothDevice>();
    }

    public void generateListWithPairedDevices()
    {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter.isEnabled()) {
            // Überprüfen ob bereits einstellungen vorenommen wurden
            final SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            Set<String> mySavedHashSet = new HashSet<>();
            mySavedHashSet = myPrefs.getStringSet(prefPairedDevicesNameListKey,new HashSet<String>());

            // TODO Es muss noch überprüft werden, wass passiert wenn ein neues Gerät hinzugefügt wird

            // Wenn noch keine Geräte in der Option anzuzeigende Geräte ausgewählt wurde, soll das ausgeführt werden


            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            pairedDevicesMap = new HashMap<>();
            if (pairedDevices.size() > 0) {
                // There are paired devices. Get the name and address of each paired device.
                pairedDeviceNamesList = new ArrayList<String>();
                for (BluetoothDevice device : pairedDevices) {
                    pairedDevicesMap.put(device.getName(), device);
                    if(mySavedHashSet.isEmpty()) {
                        pairedDeviceNamesList.add(device.getName());
                    }
                }
            } else {
                // Es wurden keine gekoppelten Geräte gefunden
                Toast.makeText(this, "Es wurden keine verbundenen Geräte gefunden", Toast.LENGTH_SHORT).show();
            }
            if(!mySavedHashSet.isEmpty()) {
                pairedDeviceNamesList = castSetToList(mySavedHashSet);
            }
        }

    }

    public void fillListWithPairedDevices()
    {
        BluetoothAdapter testAdapter = BluetoothAdapter.getDefaultAdapter();
        if(testAdapter.isEnabled()) {
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, pairedDeviceNamesList);
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
                fillListWithPairedDevices();


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

        if(requestCode == FilteredPairedDevicesListRequest)
        {
            if(resultCode == RESULT_OK) {
                Intent getFileredPairedDevicesList = data;
                updateForPairedDevicesList = new ArrayList<String>();
                updateForPairedDevicesList = getFileredPairedDevicesList.getStringArrayListExtra("deliveredFilteredPairedDevicesList");
                if (updateForPairedDevicesList != null) {
                    //ToDo überprüfen ob die unteren beiden Statments nötig sind
                    final SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editPrefs = myPrefs.edit();
                    editPrefs.putStringSet(prefPairedDevicesNameListKey,castListToSet(updateForPairedDevicesList));
                    editPrefs.commit();
                    generateListWithPairedDevices();
                    fillListWithPairedDevices();
                }
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
                if(!pairedDeviceNamesList.contains(newBTdevice) && newDiscoveredDevice != null && newBTdevice != null) {
                    newDevicesName.add(newBTdevice);
                    newDevicesObject.add(newDiscoveredDevice);
                    Toast.makeText(context, "Gerät " + newBTdevice + " gefunden", Toast.LENGTH_SHORT).show();
                    devNameAdapter.notifyDataSetChanged();
                    btnConnectToNewDeviceMain.setEnabled(true);
                }


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

        }
        else
        {
            Toast.makeText(this, "Dieses Gerät wurde bereits der Liste von gekoppelten Geräten hinzugefügt", Toast.LENGTH_SHORT).show();
        }


    }

    // Weil ArrayLists nicht als Prefs gespeichert werden können, wird hier ein Set daraus gemacht
    public Set<String> castListToSet(ArrayList<String> list)
    {
        Set<String> castedArrayList = new HashSet<String>();
        for(String element : list)
        {
            castedArrayList.add(element);
        }
        return castedArrayList;
    }

    public ArrayList<String> castSetToList(Set<String> set)
    {
        ArrayList<String> castedSet = new ArrayList<String>();
        for(String element : set)
        {
            castedSet.add(element);
        }
        return castedSet;
    }




}
