package com.residencia.guardiantrackitt;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Ubicacion extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DatabaseReference locationRef;
    private String idPaciente; // Reemplaza esta variable con el UID del paciente que deseas mostrar

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubicacion);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference usuariosRef = FirebaseDatabase.getInstance().getReference().child("users");
        locationRef = usuariosRef.child("ubicacion_actual");
        idPaciente = "ID_PACIENTE"; // Reemplaza "ID_PACIENTE" con el UID del paciente que deseas mostrar
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        locationRef.child(idPaciente).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                obtenerUbicacionDesdeFirebase(dataSnapshot);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                obtenerUbicacionDesdeFirebase(dataSnapshot);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void obtenerUbicacionDesdeFirebase(DataSnapshot dataSnapshot) {
        if (dataSnapshot.exists()) {
            double latitude = dataSnapshot.child("latitude").getValue(Double.class);
            double longitude = dataSnapshot.child("longitude").getValue(Double.class);

            mostrarUbicacionEnMapa(latitude, longitude);
        }
    }

    private void mostrarUbicacionEnMapa(double latitud, double longitud) {
        LatLng ubicacion = new LatLng(latitud, longitud);
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(ubicacion).title("Ubicación del Paciente"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ubicacion, 15.0f));
    }
}