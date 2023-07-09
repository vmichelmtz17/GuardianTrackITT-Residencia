package com.residencia.guardiantrackitt;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private Button loginButton;
    private Button registerButton;
    private Button websiteButton;
    private Button aboutButton;
    private FirebaseAuth mAuth;
    private DatabaseReference userTypeRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        userTypeRef = FirebaseDatabase.getInstance().getReference().child("users").child("userType");

        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);
        websiteButton = findViewById(R.id.websiteButton);
        aboutButton = findViewById(R.id.aboutButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Login.class);
                startActivity(intent);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Register.class);
                startActivity(intent);
            }
        });

        websiteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Abrir página web en el navegador
                String url = "https://alzheimertijuana.netlify.app/";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(android.net.Uri.parse(url));
                startActivity(intent);
            }
        });

        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, About.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Verificar si el usuario ya ha iniciado sesión
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Obtener el ID del usuario actual
            String uid = currentUser.getUid();

            // Obtener el tipo de usuario desde Firebase Realtime Database
            userTypeRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String userType = dataSnapshot.getValue(String.class);
                        // Redirigir al usuario a su respectiva actividad según su tipo de usuario
                        if (userType.equals("Familiar")) {
                            Intent intent = new Intent(MainActivity.this, Home_Familiar.class);
                            startActivity(intent);
                        } else if (userType.equals("Paciente")) {
                            Intent intent = new Intent(MainActivity.this, Paciente.class);
                            startActivity(intent);
                        }
                        finish(); // Finalizar MainActivity para que no se pueda volver atrás
                    } else {
                        // No se encontró el tipo de usuario en la base de datos
                        // Realizar alguna acción o mostrar un mensaje de error
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Manejo de errores de la consulta a la base de datos
                }
            });
        }
    }
}