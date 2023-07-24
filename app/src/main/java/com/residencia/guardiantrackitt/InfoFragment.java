package com.residencia.guardiantrackitt;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class InfoFragment extends Fragment {

    private EditText nombreEditText;
    private EditText fechaNacimientoEditText;
    private Button btnEditarInfo;
    private DatabaseReference userDataRef;
    private FirebaseUser currentUser;
    private TextView uidTextView;
    private Button btnObtenerUbicacion;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private DatabaseReference locationRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info, container, false);

        nombreEditText = view.findViewById(R.id.nombreEditText);
        fechaNacimientoEditText = view.findViewById(R.id.fechaNacimientoEditText);
        btnEditarInfo = view.findViewById(R.id.btnEditarInfo);
        uidTextView = view.findViewById(R.id.uidTextView);
        btnObtenerUbicacion = view.findViewById(R.id.btnObtenerUbicacion);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            userDataRef = FirebaseDatabase.getInstance().getReference().child("users").child("userData").child(userId);

            userDataRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String nombre = dataSnapshot.child("name").getValue(String.class);
                        String fechaNacimiento = dataSnapshot.child("dateOfBirth").getValue(String.class);

                        nombreEditText.setText(nombre);
                        fechaNacimientoEditText.setText(fechaNacimiento);

                        uidTextView.setText("UID del usuario: " + userId);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(requireContext(), "Error al leer datos de Firebase", Toast.LENGTH_SHORT).show();
                }
            });
        }

        uidTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyUidToClipboard();
            }
        });

        btnEditarInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nuevoNombre = nombreEditText.getText().toString();
                String nuevaFechaNacimiento = fechaNacimientoEditText.getText().toString();

                userDataRef.child("name").setValue(nuevoNombre);
                userDataRef.child("dateOfBirth").setValue(nuevaFechaNacimiento);

                Toast.makeText(requireContext(), "Información actualizada en Firebase", Toast.LENGTH_SHORT).show();
            }
        });

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            locationRef = FirebaseDatabase.getInstance().getReference().child("users").child("userData").child(userId).child("location");
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    Location location = locationResult.getLastLocation();
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();

                        enviarUbicacionFirebase(latitude, longitude);
                    }
                }
            }
        };

        btnObtenerUbicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                obtenerUbicacionActual();
            }
        });

        return view;
    }

    private void copyUidToClipboard() {
        if (currentUser != null) {
            String userId = currentUser.getUid();
            if (!TextUtils.isEmpty(userId)) {
                ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("UID", userId);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(requireContext(), "UID copiado al portapapeles", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void obtenerUbicacionActual() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }

    private void enviarUbicacionFirebase(double latitude, double longitude) {
        locationRef.child("latitude").setValue(latitude);
        locationRef.child("longitude").setValue(longitude);

        Toast.makeText(requireContext(), "Ubicación enviada a Firebase", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }
}
