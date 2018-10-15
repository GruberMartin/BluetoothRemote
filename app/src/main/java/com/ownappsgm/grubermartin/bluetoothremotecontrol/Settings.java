package com.ownappsgm.grubermartin.bluetoothremotecontrol;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

// List View With ChekBoxes : https://www.dev2qa.com/android-custom-listview-with-checkbox-example/

public class Settings extends AppCompatActivity {

    TextView tvUserInfoSettings;
    ListView lvSelectDevicesToDisplayInPairedListSettings;
    ArrayList<String> list;
    Button button2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        tvUserInfoSettings = (TextView) findViewById(R.id.tvUserInfoSettings);
        button2 = (Button) findViewById(R.id.button2);
        lvSelectDevicesToDisplayInPairedListSettings = (ListView) findViewById(R.id.lvSelectDevicesToDisplayInPairedListSettings);
        list = new ArrayList<String>();
        list.add("Test");
        list.add("2");
        list.add("Super");
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,list);
        lvSelectDevicesToDisplayInPairedListSettings.setAdapter(adapter);
        lvSelectDevicesToDisplayInPairedListSettings.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);


    }

    public void onBtnClicked(View view)
    {

        for(int i = 0; i < lvSelectDevicesToDisplayInPairedListSettings.getCount();i++)
        {
            if(lvSelectDevicesToDisplayInPairedListSettings.isItemChecked(i))
            {
                Toast.makeText(this, list.get(i) + " wurde ausgewählt", Toast.LENGTH_SHORT).show();
            }
        }
    }


}
