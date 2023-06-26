package com.residencia.guardiantrackitt;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.residencia.guardiantrackitt.databinding.ActivityPacienteBinding;

public class Paciente extends AppCompatActivity {
    @androidx.annotation.NonNull
    ActivityPacienteBinding binding;

    private String pacienteId;

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPacienteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Configurar el listener para el BottomNavigationView
        bottomNavigationView.setOnNavigationItemSelectedListener(
                item -> {
                    switch (item.getItemId()) {
                        case R.id.navigation_home:
                            abrirHomeFragment();
                            return true;
                        case R.id.navigation_maps:
                            abrirMapsFragment();
                            return true;
                        case R.id.navigation_photos:
                            abrirPhotosFragment();
                            return true;
                        case R.id.navigation_information:
                            abrirInfoFragment();
                            return true;
                    }
                    return false;
                }
        );

        // Obtén el ID del paciente desde Firebase
        obtenerPacienteId();
    }

    private void obtenerPacienteId() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            // Obtén una referencia a la base de datos de Firebase
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference pacientesRef = database.getReference("Paciente");

            // Realiza una consulta a la base de datos para obtener el paciente del usuario actual
            Query query = pacientesRef.orderByChild("userId").equalTo(userId).limitToFirst(1);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Paciente registrado, obtener el ID del paciente
                        pacienteId = dataSnapshot.getChildren().iterator().next().getKey();
                        abrirHomeFragment();
                    } else {
                        // No hay paciente registrado para el usuario actual
                        abrirInfoFragment();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Manejo de errores de la consulta
                }
            });
        }
    }

    private void abrirHomeFragment() {
        replaceFragment(new HomeFragment());
    }

    private void abrirPhotosFragment() {
        replaceFragment(new PhotosFragment());
    }

    private void abrirInfoFragment() {
        replaceFragment(new InfoFragment());
    }
    private void abrirMapsFragment() {
        replaceFragment(new MapsFragment());
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_layout, fragment);
        transaction.commit();
    }

    public void guardarDatosPaciente(String nombre, String fechaNacimiento) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            // Obtén una referencia a la base de datos de Firebase
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference pacientesRef = database.getReference("Paciente");

            // Crea un nuevo objeto PacienteModel
            PacienteModel paciente = new PacienteModel(userId, nombre, fechaNacimiento);

            // Si el ID del paciente actual es nulo, genera uno nuevo
            if (pacienteId == null) {
                pacienteId = pacientesRef.push().getKey();
            }

            // Guarda los datos del paciente en la ubicación correspondiente en la base de datos
            pacientesRef.child(pacienteId).setValue(paciente);

            // Después de guardar los datos, abrir el HomeFragment
            abrirHomeFragment();
        }
    }
}