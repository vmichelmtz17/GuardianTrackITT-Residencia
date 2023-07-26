package com.residencia.guardiantrackitt;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegistrarPaciente extends AppCompatActivity {

    private EditText pacienteNameEditText;
    private EditText pacienteAgeEditText;
    private Button registrarPacienteButton;
    private FirebaseAuth mAuth;
    private DatabaseReference pacientesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_paciente);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // Si el usuario no está autenticado, redirigir a la actividad de inicio de sesión o registro
            Toast.makeText(this, "Debe iniciar sesión o registrarse como usuario familiar.", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            String uid = currentUser.getUid();
            pacientesRef = FirebaseDatabase.getInstance().getReference().child("users").child("userType")
                    .child("Familiar").child(uid).child("pacientes");

            pacienteNameEditText = findViewById(R.id.pacienteNameEditText);
            pacienteAgeEditText = findViewById(R.id.pacienteAgeEditText);
            registrarPacienteButton = findViewById(R.id.registrarPacienteButton);

            registrarPacienteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String pacienteName = pacienteNameEditText.getText().toString().trim();
                    String pacienteAge = pacienteAgeEditText.getText().toString().trim();

                    if (pacienteName.isEmpty() || pacienteAge.isEmpty()) {
                        Toast.makeText(RegistrarPaciente.this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
                    } else {
                        registrarPaciente(pacienteName, pacienteAge);
                    }
                }
            });
        }
    }
    private void registrarPaciente(String name, String age) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            String pacienteUID = pacientesRef.push().getKey();

            Map<String, Object> pacienteData = new HashMap<>();
            pacienteData.put("name", name);
            pacienteData.put("age", age);

            // Guardar el paciente bajo su propia referencia
            pacientesRef.child(pacienteUID).setValue(pacienteData)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(RegistrarPaciente.this, "Paciente registrado exitosamente", Toast.LENGTH_SHORT).show();
                                // Aquí puedes agregar el código para redirigir a la actividad "Home_Familiar"
                                // Por ejemplo:
                                Intent intent = new Intent(RegistrarPaciente.this, Home_Familiar.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(RegistrarPaciente.this, "Error al registrar el paciente", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}