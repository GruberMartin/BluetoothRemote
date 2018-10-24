package com.ownappsgm.grubermartin.bluetoothremotecontrol;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

// List View With ChekBoxes : https://www.dev2qa.com/android-custom-listview-with-checkbox-example/

public class Settings extends AppCompatActivity {

    TextView tvUserInfoSettings;
    ListView lvSelectDevicesToDisplayInPairedListSettings;
    ArrayList<String> list;
    Button btnSave;
    final String prefKey = "filteredPairedDevicesList";
    HashSet<String> filteredPairedDevicesList;
    Set<String> getFilteredPairedDevicesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        tvUserInfoSettings = (TextView) findViewById(R.id.tvUserInfoSettings);
        btnSave = (Button) findViewById(R.id.btnSave);
        lvSelectDevicesToDisplayInPairedListSettings = (ListView) findViewById(R.id.lvSelectDevicesToDisplayInPairedListSettings);
        list = new ArrayList<String>();
        Intent getExtras = getIntent();
        list = getExtras.getStringArrayListExtra("pairedDevicesList");
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,list);
        lvSelectDevicesToDisplayInPairedListSettings.setAdapter(adapter);
        lvSelectDevicesToDisplayInPairedListSettings.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        getFilteredPairedDevicesList = new HashSet<String>();
        final SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        getFilteredPairedDevicesList = myPrefs.getStringSet(prefKey,getFilteredPairedDevicesList);

        for(String element : getFilteredPairedDevicesList)
        {

            lvSelectDevicesToDisplayInPairedListSettings.setItemChecked( list.indexOf(element),true);
        }

    }

    public void onBtnClicked(View view)
    {
        filteredPairedDevicesList = new HashSet<String>();
        for(int i = 0; i < lvSelectDevicesToDisplayInPairedListSettings.getCount();i++)
        {

            if(lvSelectDevicesToDisplayInPairedListSettings.isItemChecked(i))
            {
                //Toast.makeText(this, list.get(i) + " wurde ausgewählt", Toast.LENGTH_SHORT).show();
                filteredPairedDevicesList.add(list.get(i));


            }
        }
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editPrefs = prefs.edit();
        editPrefs.putStringSet(prefKey,filteredPairedDevicesList);
        editPrefs.commit();
        Intent deliverFilteredPairedDevicesList = getIntent();
        ArrayList<String> sendFilteredPairedDevicesList= new ArrayList<String>(); // Casting damit man die Liste zurück an Main senden kann, da nur Sets mit Preferences abgespeichert werden konnten
        for(String element : filteredPairedDevicesList)
        {
            sendFilteredPairedDevicesList.add(element);
        }
        deliverFilteredPairedDevicesList.putExtra("deliveredFilteredPairedDevicesList",sendFilteredPairedDevicesList);
        setResult(Activity.RESULT_OK,deliverFilteredPairedDevicesList);
        finish();
    }




}
