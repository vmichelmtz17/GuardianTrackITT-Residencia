package com.residencia.guardiantrackitt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Set;

public class DeviceListActivity extends AppCompatActivity {

    public static final String EXTRA_SELECTED_DEVICE = "selected_device";
    private static final int REQUEST_PERMISSION_LOCATION = 4; // Nueva constante

    private ListView listView;
    private ArrayAdapter<String> devicesAdapter;
    private ArrayList<BluetoothDevice> devicesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);

        listView = findViewById(R.id.listView);
        devicesList = new ArrayList<>();
        devicesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1);
        listView.setAdapter(devicesAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BluetoothDevice selectedDevice = devicesList.get(position);
                Intent intent = new Intent();
                intent.putExtra(EXTRA_SELECTED_DEVICE, selectedDevice);
                setResult(RESULT_OK, intent);
                finish();


            }
        });

        loadPairedDevices();
    }

    private void loadPairedDevices() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            // El dispositivo no es compatible con Bluetooth
            finish();
        }

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_LOCATION);
        } else {
            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
            if (!pairedDevices.isEmpty()) {
                devicesAdapter.clear();
                devicesList.clear();
                for (BluetoothDevice device : pairedDevices) {
                    devicesAdapter.add(device.getName());
                    devicesList.add(device);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadPairedDevices();
            } else {
                // Permiso denegado por el usuario, manejar la situación adecuadamente
            }
        }
    }
}
