package com.ownappsgm.grubermartin.bluetoothremotecontrol;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;



public class BluetoothManager {

    int REQUEST_ENABLE_BT = 2; // erhält Result Code
    int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;

    public BluetoothManager()
    {
        BluetoothState bluetoothState;
    }

    public enum BluetoothState
    {   NeedToEnableBluetooth,
        BluetoothStateError,
        BluetoothIsEnabled,
        NeedsBluetoothPermissions
    }

    public enum BluetoothPermissionState
    {
        PermissionNotGaranted,
        PermissionGaranted,
        PermissionDenied // Noch nicht verwendet
    }

    // Überprüft ob Bluetooth eingeschaltet/verfügbar ist
    public BluetoothState checkForBluetooth(Context context)
    {
        BluetoothState currentState = BluetoothState.BluetoothStateError;
        // Adapter um zu überprüfen ob Bluetooth eingeschlatet ist
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if(adapter == null) // Das Gerät unterstützt kein Bluetooth
        {
            Toast.makeText(context, "Ihr Gerät unterstütz kein Bluetooth", Toast.LENGTH_SHORT).show();
        }
        else
        {
            if(!adapter.isEnabled()) // Wenn Bluetooth ausgeschaltet ist
            {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                currentState = BluetoothState.NeedToEnableBluetooth; // Es soll also in der entsprechneden Activity //startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT); ausgeführt werden

            }
            else
            {
                currentState = BluetoothState.BluetoothIsEnabled;
            }
        }

        return currentState;
    }

    public BluetoothPermissionState checkBluetoothPermission(Context context)
    {
        BluetoothPermissionState currentState;

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            currentState = BluetoothPermissionState.PermissionNotGaranted;
        }
        else
        {
            currentState = BluetoothPermissionState.PermissionGaranted;
        }

        return currentState;
    }


}
