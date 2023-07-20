package com.residencia.guardiantrackitt;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private DatabaseReference userTypeRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        userTypeRef = FirebaseDatabase.getInstance().getReference().child("users");

        Button loginButton = findViewById(R.id.loginButton);
        Button registerButton = findViewById(R.id.registerButton);
        Button websiteButton = findViewById(R.id.websiteButton);
        Button aboutButton = findViewById(R.id.aboutButton);

        loginButton.setOnClickListener(this);
        registerButton.setOnClickListener(this);
        websiteButton.setOnClickListener(this);
        aboutButton.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();

            userTypeRef.child(uid).child("userType").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String userType = dataSnapshot.getValue(String.class);

                    if (userType != null) {
                        if (userType.equals("Familiar")) {
                            startActivity(new Intent(MainActivity.this, Home_Familiar.class));
                        } else if (userType.equals("Paciente")) {
                            startActivity(new Intent(MainActivity.this, Paciente.class));
                        }

                        finish(); // Finaliza MainActivity para que no se pueda volver atrás
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(MainActivity.this, "Error al obtener el tipo de usuario", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginButton:
                startActivity(new Intent(this, Login.class));
                break;
            case R.id.registerButton:
                startActivity(new Intent(this, Register.class));
                break;
            case R.id.websiteButton:
                openWebPage("https://alzheimertijuana.netlify.app/");
                break;
            case R.id.aboutButton:
                startActivity(new Intent(this, About.class));
                break;
        }
    }

    private void openWebPage(String url) {
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}
