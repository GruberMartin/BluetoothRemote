package com.ownappsgm.grubermartin.bluetoothremotecontrol;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.ownappsgm.grubermartin.bluetoothremotecontrol.BluetoothManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class PairedDevices {

    final String sharedPreferencesFilteredPairedDevicesNameListKey = "filteredPairedDevicesKey";

    Context context;

    public PairedDevices(Context ApplicationContext)
    {
        context = ApplicationContext;
    }

    // Hier wird eine Liste mit den Namen aller gepairten Geräte erzeugt
    public ArrayList generateListWithPairedDevicesNames()
    {
        // Hier wird eine Liste erzeugt, die später mit allen gepairten Geräten gefüllt wird
        ArrayList<String> pairedDeviceNamesList = new ArrayList<String>();
        // Adapter anfordern, um auf die Verbundenen Geräte zugreiffen zu können
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothManager bluetoothManager = new BluetoothManager();
        if(bluetoothManager.checkForBluetooth(context) == BluetoothManager.BluetoothState.BluetoothIsEnabled)
        {
            final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            // Das nachfolgende Set enthält dann die Auswahl der Geräte die angezeigt werden sollen
            Set<String> filteredBondedDevices = sharedPreferences.getStringSet(sharedPreferencesFilteredPairedDevicesNameListKey,new HashSet<String>());

            Set<BluetoothDevice> allPairedDevices = bluetoothAdapter.getBondedDevices(); // Erhalten aller verbundenen Geräte
            if(allPairedDevices.size() > 0)
            {
                for(BluetoothDevice bluetoothDevice : allPairedDevices)
                {
                    pairedDeviceNamesList.add(bluetoothDevice.getName());
                }
            }else
            {
                Toast.makeText(context, "Es sind noch keine Geräte gekoppelt", Toast.LENGTH_SHORT).show();
            }

            // Wenn die Liste bereits gefilter wurde soll diese zurückgegeben werden
            if(!filteredBondedDevices.isEmpty())
            {
                pairedDeviceNamesList = castSetToList(filteredBondedDevices);
            }


        }
        return pairedDeviceNamesList;
    }

    // Hier wird eine Map mit allen Bluetoothgeräten und deren Namen erzeugt
    public HashMap generateMapWithPairedDevices()
    {
        HashMap<String,BluetoothDevice> pairedDevicesMap = new HashMap<>();
        BluetoothManager bluetoothManager = new BluetoothManager();
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothManager.checkForBluetooth(context) == BluetoothManager.BluetoothState.BluetoothIsEnabled)
        {
            Set<BluetoothDevice> allBondedDevices = adapter.getBondedDevices();
            for(BluetoothDevice bluetoothDevice : allBondedDevices)
            {
                pairedDevicesMap.put(bluetoothDevice.getName(), bluetoothDevice);
            }
        }

        return pairedDevicesMap;
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

