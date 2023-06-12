package com.residencia.guardiantrackitt;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.residencia.guardiantrackitt.databinding.ActivityPacienteBinding;

public class Paciente extends AppCompatActivity {
    @androidx.annotation.NonNull
    ActivityPacienteBinding binding;

    private String pacienteId;
    private PacienteModel pacienteModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPacienteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new HomeFragment());

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    replaceFragment(new HomeFragment());
                    break;
                case R.id.navigation_photos:
                    replaceFragment(new PhotosFragment());
                    break;
                case R.id.navigation_information:
                    replaceFragment(new InfoFragment());
                    break;
            }
            return true;
        });

        // Obtén el ID del paciente desde Firebase
        obtenerPacienteId();
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_layout, fragment);
        transaction.commit();
    }

    private void obtenerPacienteId() {
        // Obtén una referencia a la base de datos de Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference pacientesRef = database.getReference("pacientes");

        // Realiza una consulta a la base de datos para obtener el ID del paciente actual
        pacientesRef.orderByKey().limitToFirst(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    pacienteId = snapshot.getKey();
                    break;
                }

                // Obtén los datos del paciente actual
                obtenerDatosPaciente();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejo de errores de la consulta
            }
        });
    }

    private void obtenerDatosPaciente() {
        // Obtén una referencia a la base de datos de Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference pacientesRef = database.getReference("pacientes").child(pacienteId);

        // Realiza una consulta a la base de datos para obtener los datos del paciente actual
        pacientesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    pacienteModel = dataSnapshot.getValue(PacienteModel.class);

                    // Pasa el objeto PacienteModel al fragmento de información
                    Fragment infoFragment = InfoFragment.newInstance(pacienteModel);
                    replaceFragment(infoFragment);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejo de errores de la consulta
            }
        });
    }

    public void guardarDatosPaciente(String nombre, String fechaNacimiento) {
        // Obtén una referencia a la base de datos de Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference pacientesRef = database.getReference("pacientes");

        // Crea un nuevo objeto PacienteModel
        PacienteModel paciente = new PacienteModel(nombre, fechaNacimiento);

        // Si el ID del paciente actual es nulo, genera uno nuevo
        if (pacienteId == null) {
            pacienteId = pacientesRef.push().getKey();
        }

        // Guarda los datos del paciente en la ubicación correspondiente en la base de datos
        pacientesRef.child(pacienteId).setValue(paciente);
    }
}