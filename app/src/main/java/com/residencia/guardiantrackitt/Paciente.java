package com.residencia.guardiantrackitt;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.residencia.guardiantrackitt.databinding.ActivityPacienteBinding;

public class Paciente extends AppCompatActivity {
    @androidx.annotation.NonNull
    ActivityPacienteBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPacienteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        remplaceFragment(new HomeFragment());

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    remplaceFragment(new HomeFragment());
                    break;
                case R.id.navigation_photos:
                    remplaceFragment(new PhotosFragment());
                    break;
                case R.id.navigation_information:
                    remplaceFragment(new InfoFragment());
                    break;
            }
            return true;
        });
    }

    private void remplaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_layout, fragment);
        transaction.commit();
    }

    public void guardarDatosPaciente(String nombre, String fechaNacimiento) {
        // Obtén una referencia a la base de datos de Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference pacientesRef = database.getReference("pacientes");

        // Crea un nuevo objeto PacienteModel
        PacienteModel paciente = new PacienteModel(nombre, fechaNacimiento);

        // Genera un nuevo ID para el paciente en la base de datos
        String pacienteId = pacientesRef.push().getKey();

        // Guarda los datos del paciente en la ubicación correspondiente en la base de datos
        pacientesRef.child(pacienteId).setValue(paciente);
    }
}