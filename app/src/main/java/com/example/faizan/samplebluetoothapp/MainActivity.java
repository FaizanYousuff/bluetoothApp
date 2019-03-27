package com.example.faizan.samplebluetoothapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity  implements View.OnClickListener{

    private Button bOn,bOff,bScan;
    BluetoothAdapter bluetoothAdapter;
    RelativeLayout layout;
    Intent enablingBluetoothIntent;
    private static int REAQUEST_CODE = 1;
    private ListView listView;
    private ArrayList<String> stringArrayList = new ArrayList<>();
    private ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bOn = findViewById(R.id.btn_on);
        bOn.setOnClickListener(this);
        bOff  = findViewById(R.id.btn_off);
        bOff.setOnClickListener(this);
        bScan = findViewById(R.id.btn_scan);
        bScan.setOnClickListener(this);
        listView = findViewById(R.id.scan_list);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        layout = findViewById(R.id.parent_layout);
        enablingBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(broadcastReceiver,intentFilter);
        arrayAdapter = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_list_item_1,stringArrayList);
        listView.setAdapter(arrayAdapter);



    }
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)){
              BluetoothDevice device=   intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
              stringArrayList.add(device.getName());
              arrayAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == REAQUEST_CODE) {

            if (resultCode == RESULT_OK) {
                Snackbar.make(layout," BLuetooth has been Enable",Snackbar.LENGTH_SHORT).show();

            }
            else if (resultCode==RESULT_CANCELED){
                Snackbar.make(layout," BLuetooth is Not Enabled",Snackbar.LENGTH_SHORT).show();

            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.btn_on:
                if(bluetoothAdapter !=  null ){
                    if(!bluetoothAdapter.isEnabled()){
                        startActivityForResult(enablingBluetoothIntent,REAQUEST_CODE);
                    }
                } else {
                    Toast.makeText(this, "Your Device does Not support Bluetooth", Toast.LENGTH_SHORT).show();
                }
                break;


            case R.id.btn_off:
                if(bluetoothAdapter.isEnabled()){
                    bluetoothAdapter.disable();
                } else {
                    Snackbar.make(layout," BLuetooth is alredy in OFF state",Snackbar.LENGTH_SHORT).show();
                }
                break;

            case R.id.btn_scan:
                if(bluetoothAdapter !=  null ){
                    if(!bluetoothAdapter.isEnabled()){
                        startActivityForResult(enablingBluetoothIntent,REAQUEST_CODE);


                    }else {
                        bluetoothAdapter.startDiscovery();

                 /*      Set<BluetoothDevice> pairedDevices =  bluetoothAdapter.getBondedDevices();
                       String[] array = new String[pairedDevices.size()];
                       int i=0;
                       if(pairedDevices.size()>0){
                           for(BluetoothDevice device : pairedDevices){
                               array[i] = device.getName();
                               i++;
                           }

                           ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_list_item_1,array);
                           listView.setAdapter(arrayAdapter);
                       }*/
                    }
                } else {
                    Toast.makeText(this, "Your Device does Not support Bluetooth", Toast.LENGTH_SHORT).show();
                }
                break;


        }
    }


}
