package com.example.faizan.samplebluetoothapp;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class ConnectActivity  extends AppCompatActivity implements View.OnClickListener {
    private Button listen_btn, scan_btn, send_btn;
    private EditText messageEd;
    private TextView status_tv,message_tv;
    BluetoothAdapter bluetoothAdapter;
    Intent enablingBluetoothIntent;
    private static int REAQUEST_CODE = 1;
    RelativeLayout layout;
     SendRecieve sendRecieve;
    BluetoothDevice[] btArray;


    private ArrayList<String> stringArrayList = new ArrayList<>();
    private ArrayAdapter<String> arrayAdapter;
    static final int STATE_LISTENING = 1;
    static final int STATE_CONNECTING = 2;
    static final int STATE_CONNECTED = 3;
    static final int STATE_CONNECTION_FAILED = 4;
    static final int STATE_MESSAGE_RECIEVED = 5;
    private static final String APP_NAME= "btChat";

    private static final int CAMERA_PERMISSION_CODE =123;
    private static final UUID UUIDS= UUID.fromString("00000000-0000-1000-8000-00805F9B34FB");


    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        scan_btn = findViewById(R.id.btn_scan);
        message_tv = findViewById(R.id.message_tv);

        scan_btn.setOnClickListener(this);
        listView = findViewById(R.id.scan_list);
        listen_btn = findViewById(R.id.btn_listen);
        listen_btn.setOnClickListener(this);
        send_btn = findViewById(R.id.btn_send);
        send_btn.setOnClickListener(this);
        messageEd = findViewById(R.id.mesg_ed);
        status_tv = findViewById(R.id.tv_status);
        layout = findViewById(R.id.parent_layout);


    /*    Intent discoverableIntent =
                new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 600);
        startActivity(discoverableIntent);
*/
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        enablingBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        if (bluetoothAdapter != null) {
            if (!bluetoothAdapter.isEnabled()) {
                startActivityForResult(enablingBluetoothIntent, REAQUEST_CODE);
            }
        } else {
            Toast.makeText(this, "Your Device does Not support Bluetooth", Toast.LENGTH_SHORT).show();
        }


        //arrayAdapter = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_list_item_1,stringArrayList);
        //listView.setAdapter(arrayAdapter);

        if (bluetoothAdapter != null) {
            if (!bluetoothAdapter.isEnabled()) {
                startActivityForResult(enablingBluetoothIntent, REAQUEST_CODE);
            }
        } else {
            Toast.makeText(this, "Your Device does Not support Bluetooth", Toast.LENGTH_SHORT).show();
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
              ClientCLass clientCLass = new ClientCLass(btArray[position]);
                clientCLass.start();
                status_tv.setText("CONNECTING");
            }
        });


    }

    //Requesting permission
    private void requestCameraPermission(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.CAMERA)){
            // If the user has denied the permission previously your code will come to this block
            // Here you can explain why you need this permission
            // Explain here why you need this permission
        }

        // And finally ask for the permission
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},CAMERA_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == CAMERA_PERMISSION_CODE){
            if(grantResults.length>0 && grantResults[0] ==PackageManager.PERMISSION_GRANTED){

                //Displaying a toast
                Toast.makeText(this,"Permission granted ",Toast.LENGTH_SHORT).show();
            }else{
                //Displaying another toast if permission is not granted
                Toast.makeText(this,"Oops you just denied the permission",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        //unregisterReceiver(broadcastReceiver);
    }
    private boolean isCameraAllowed() {
        //Getting the permission status
        int result = ContextCompat.checkSelfPermission(ConnectActivity.this, Manifest.permission.CAMERA);

        //If permission is granted returning true
        if (result == PackageManager.PERMISSION_GRANTED)
            return true;

        //If permission is not granted returning false
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REAQUEST_CODE) {

            if (resultCode == RESULT_OK) {
                Snackbar.make(layout, " BLuetooth has been Enabled", Snackbar.LENGTH_SHORT).show();

            } else if (resultCode == RESULT_CANCELED) {
                Snackbar.make(layout, " BLuetooth is Not Enabled", Snackbar.LENGTH_SHORT).show();

            }
        }
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //unregisterReceiver(this);
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if(device.getAddress().equalsIgnoreCase("54:B8:02:1A:5C:74")){
                 ClientCLass clientCLass = new ClientCLass(device);
                    clientCLass.start();
                    status_tv.setText("CONNECTING");

                }

                //  Toast.makeText(context,device.getAddress(), Toast.LENGTH_SHORT).show();

                stringArrayList.add(device.getName());
                //arrayAdapter.notifyDataSetChanged();
            }
        }
    };
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            switch (msg.what){
                case STATE_LISTENING:
                    status_tv.setText("LISTENING");
                    break;
                case STATE_CONNECTING:
                    status_tv.setText("CONNECTING");
                    break;
                case STATE_CONNECTED:
                    status_tv.setText("CONNECTED");
                    break;
                case STATE_CONNECTION_FAILED:
                    status_tv.setText("CONNECTION FAILED");
                    break;
                case STATE_MESSAGE_RECIEVED:
                    byte[] readBuffer = (byte[]) msg.obj;
                    String tempMsg = new String(readBuffer,0,msg.arg1);
                    message_tv.setText(tempMsg);
                    status_tv.setText("RECEIVED");
                    break;

                case 6:
                    status_tv.setText("Disconnected");
                    // sendRecieve.stop();
                    break;
            }
            return true;
        }
    });

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btn_listen:
               ServerClass serverClass = new ServerClass();
                serverClass.start();
                break;
            case R.id.btn_scan:
                if (bluetoothAdapter != null) {
                    if (!bluetoothAdapter.isEnabled()) {
                        startActivityForResult(enablingBluetoothIntent, REAQUEST_CODE);


                    } else {
                        startSearching();
                        //  bluetoothAdapter.startDiscovery();

                       /* Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
                        String[] array = new String[pairedDevices.size()];
                        btArray = new BluetoothDevice[pairedDevices.size()];

                        int i = 0;
                        if (pairedDevices.size() > 0) {
                            for (BluetoothDevice device : pairedDevices) {
                                btArray[i] =device;
                                array[i] = device.getName();
                                if (device.getName().equalsIgnoreCase("Galaxy J5 prime")){
                                 ClientCLass clientCLass = new ClientCLass(device);
                                    clientCLass.start();
                                    status_tv.setText("CONNECTING");
                                }
                                i++;
                            }

                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, array);
                            listView.setAdapter(arrayAdapter);
                        }*/
                    }
                } else {
                    Toast.makeText(this, "Your Device does Not support Bluetooth", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.btn_send:
                String string = messageEd.getText().toString();
                sendRecieve.write(string.getBytes());
                break;
        }

    }

    private class ServerClass extends Thread{

        private BluetoothServerSocket bluetoothServerSocket;

        public ServerClass(){
            try {
                bluetoothServerSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(APP_NAME,UUIDS);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run(){
            BluetoothSocket socket = null;
            while (socket== null){
                try {
                    Message message = Message.obtain();
                    message.what= STATE_CONNECTING;
                    handler.sendMessage(message);
                    socket = bluetoothServerSocket.accept();



                    if(socket != null) {
                        Message message1 = Message.obtain();
                        message1.what = STATE_CONNECTED;
                        handler.sendMessage(message1);
                        sendRecieve = new SendRecieve(socket);

            /*        InputStream inputStream = socket.getInputStream();
                    OutputStream outputStream = socket.getOutputStream();
                    if (inputStream != null) {
                        inputStream.close();
                        inputStream = null;
                    }

                    if (outputStream != null) {
                        outputStream.close();
                        outputStream = null;
                    }

                    if (socket != null) {
                        socket.close();
                    }*/
                    }
                } catch (IOException e) {
                    e.printStackTrace();

                    Message message = Message.obtain();
                    message.what= STATE_CONNECTION_FAILED;
                    handler.sendMessage(message);
                }

                sendRecieve.start();
                break;
            }
        }


    }



    private class ClientCLass extends Thread{
        private BluetoothSocket socket;
        private BluetoothDevice device;
        public ClientCLass(BluetoothDevice device1){
            device= device1;
            try {
                Toast.makeText(ConnectActivity.this, device1.getAddress(), Toast.LENGTH_SHORT).show();
                Toast.makeText(ConnectActivity.this, device1.getName(), Toast.LENGTH_SHORT).show();


                socket =  device.createRfcommSocketToServiceRecord(UUIDS);
            } catch (IOException e) {
                e.printStackTrace();
            }


        }

        public void run(){
            try {
                socket.connect();
                Message message = Message.obtain();
                message.what= STATE_CONNECTED;
                handler.sendMessage(message);
                sendRecieve = new SendRecieve(socket);
                sendRecieve.start();

            } catch (IOException e) {
                e.printStackTrace();
                Message message = Message.obtain();
                message.what= STATE_CONNECTION_FAILED;
                handler.sendMessage(message);
            }
        }

    }

    private class SendRecieve extends Thread{
        private final BluetoothSocket bluetoothSocket;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        public SendRecieve(BluetoothSocket socket){
            bluetoothSocket = socket;
            InputStream temp_in= null;
            OutputStream temp_out = null;
            try {
                temp_in = bluetoothSocket.getInputStream();

                temp_out = bluetoothSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            inputStream = temp_in;
            outputStream = temp_out;

        }




        public void run(){
            byte[] buffer = new byte[1024];
            int bytes ;
            while (true){
                try {
                    bytes=   inputStream.read(buffer);
                    handler.obtainMessage(STATE_MESSAGE_RECIEVED,bytes,-1,buffer).sendToTarget();

                    InputStream inputStream = bluetoothSocket.getInputStream();
                    OutputStream outputStream = bluetoothSocket.getOutputStream();
                    if (inputStream != null) {
                        inputStream.close();
                        inputStream = null;
                    }

                    if (outputStream != null) {
                        outputStream.close();
                        outputStream = null;
                    }

                    if (bluetoothSocket != null) {
                        bluetoothSocket.close();
                    }

                    Message message = Message.obtain();
                    message.what= 6;
                    handler.sendMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void write(byte[] bytes){
            try {
                outputStream.write(bytes);
                InputStream inputStream = bluetoothSocket.getInputStream();
                OutputStream outputStream = bluetoothSocket.getOutputStream();
                if (inputStream != null) {
                    inputStream.close();
                    inputStream = null;
                }

                if (outputStream != null) {
                    outputStream.close();
                    outputStream = null;
                }

                if (bluetoothSocket != null) {
                    bluetoothSocket.close();
                }

                Message message = Message.obtain();
                message.what= 6;
                handler.sendMessage(message);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void startSearching() {
       // Log.i("Log", "in the start searching method");
        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);

       // IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(broadcastReceiver, intentFilter);
        // If we're already discovering, stop it
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }

        bluetoothAdapter.startDiscovery();
    }
}
