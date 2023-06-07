
package com.residencia.guardiantrackitt;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ingenieriajhr.blujhr.BluJhr;

import java.util.ArrayList;
import java.util.List;

public class Pulsaciones extends AppCompatActivity {

    BluJhr blue;
    List<String> requiredPermissions;
    ArrayList<String> devicesBluetooth = new ArrayList<String>();
    LinearLayout viewConn;
    ListView listDeviceBluetooth;
    Button buttonSend;
    TextView consola;
    EditText edtTx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pulsaciones);

        blue = new BluJhr(this);
        blue.onBluetooth();
        listDeviceBluetooth = findViewById(R.id.listDeviceBluetooth);
        viewConn = findViewById(R.id.viewConn);
        buttonSend = findViewById(R.id.buttonSend);
        consola = findViewById(R.id.consola);
        edtTx = findViewById(R.id.edtTx);

        listDeviceBluetooth.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (!devicesBluetooth.isEmpty()){
                    blue.connect(devicesBluetooth.get(i));
                    blue.setDataLoadFinishedListener(new BluJhr.ConnectedBluetooth() {
                        @Override
                        public void onConnectState(@NonNull BluJhr.Connected connected) {
                            if (connected == BluJhr.Connected.True){
                                Toast.makeText(getApplicationContext(),"True",Toast.LENGTH_SHORT).show();
                                listDeviceBluetooth.setVisibility(View.GONE);
                                viewConn.setVisibility(View.VISIBLE);
                                rxReceived();
                            }else{
                                if (connected == BluJhr.Connected.Pending){
                                    Toast.makeText(getApplicationContext(),"Pending",Toast.LENGTH_SHORT).show();
                                }else{
                                    if (connected == BluJhr.Connected.False){
                                        Toast.makeText(getApplicationContext(),"False",Toast.LENGTH_SHORT).show();
                                    }else{
                                        if (connected == BluJhr.Connected.Disconnect){
                                            Toast.makeText(getApplicationContext(),"Disconnect",Toast.LENGTH_SHORT).show();
                                            listDeviceBluetooth.setVisibility(View.VISIBLE);
                                            viewConn.setVisibility(View.GONE);
                                        }
                                    }
                                }
                            }
                        }
                    });
                }
            }
        });

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                blue.bluTx(edtTx.getText().toString());
            }
        });

        buttonSend.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                blue.closeConnection();
                return false;
            }
        });

    }

    private void rxReceived() {

        blue.loadDateRx(new BluJhr.ReceivedData() {
            @Override
            public void rxDate(@NonNull String s) {
                consola.setText(consola.getText().toString()+s);
            }
        });

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (blue.checkPermissions(requestCode,grantResults)){
            Toast.makeText(this, "Exit", Toast.LENGTH_SHORT).show();
            blue.initializeBluetooth();
        }else{
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.S){
                blue.initializeBluetooth();
            }else{
                Toast.makeText(this, "Algo salio mal", Toast.LENGTH_SHORT).show();
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (!blue.stateBluetoooth() && requestCode == 100){
            blue.initializeBluetooth();
        }else{
            if (requestCode == 100){
                devicesBluetooth = blue.deviceBluetooth();
                if (!devicesBluetooth.isEmpty()){
                    ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_expandable_list_item_1,devicesBluetooth);
                    listDeviceBluetooth.setAdapter(adapter);
                }else{
                    Toast.makeText(this, "No tienes vinculados dispositivos", Toast.LENGTH_SHORT).show();
                }

            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
