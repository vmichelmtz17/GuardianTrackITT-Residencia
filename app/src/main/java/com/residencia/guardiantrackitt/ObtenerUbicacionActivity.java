package com.residencia.guardiantrackitt;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ObtenerUbicacionActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DatabaseReference ubicacionRef;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_obtener_ubicacion);

        // Obtén el UID del usuario Paciente actual
        String pacienteUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Obtén una referencia a la ubicación del usuario Paciente en la base de datos
        ubicacionRef = FirebaseDatabase.getInstance().getReference().child("users").child("userData").child(pacienteUid).child("ubicacion");

        // Cargar el mapa asincrónicamente
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Puedes personalizar el mapa aquí si es necesario, por ejemplo, estableciendo la ubicación inicial, el nivel de zoom, etc.

        // Escuchar cambios en la ubicación del Paciente
        ubicacionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Obtén la latitud y longitud del Paciente desde la base de datos
                    Double latitud = dataSnapshot.child("latitud").getValue(Double.class);
                    Double longitud = dataSnapshot.child("longitud").getValue(Double.class);

                    // Muestra la ubicación del Paciente en el mapa
                    mostrarUbicacionEnMapa(latitud, longitud);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar el caso de error de lectura de datos si es necesario
            }
        });
    }

    private void mostrarUbicacionEnMapa(Double latitud, Double longitud) {
        if (mMap != null) {
            // Limpiar el mapa de marcadores previos
            mMap.clear();

            // Crear un objeto LatLng para la ubicación del Paciente
            LatLng pacienteLatLng = new LatLng(latitud, longitud);

            // Agregar un marcador en la ubicación del Paciente
            mMap.addMarker(new MarkerOptions().position(pacienteLatLng).title("Ubicación del Paciente"));

            // Mover la cámara del mapa para que muestre la ubicación del Paciente
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(pacienteLatLng, 15);
            mMap.animateCamera(cameraUpdate);
        }
    }
}