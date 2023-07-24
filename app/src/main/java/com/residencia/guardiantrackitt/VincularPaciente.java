package com.residencia.guardiantrackitt;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class VincularPaciente extends AppCompatActivity {

    private EditText pacienteUidEditText;
    private Button vincularButton;
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;
    private DatabaseReference relationsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vincular_paciente);

        pacienteUidEditText = findViewById(R.id.pacienteUidEditText);
        vincularButton = findViewById(R.id.vincularButton);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(VincularPaciente.this, MainActivity.class));
            finish();
            return;
        }

        String userId = currentUser.getUid();
        usersRef = FirebaseDatabase.getInstance().getReference().child("users").child("userData");
        relationsRef = FirebaseDatabase.getInstance().getReference().child("relations").child(userId);

        vincularButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pacienteUid = pacienteUidEditText.getText().toString().trim();

                usersRef.child(pacienteUid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            relationsRef.setValue(pacienteUid);
                            Toast.makeText(VincularPaciente.this, "Paciente vinculado exitosamente", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(VincularPaciente.this, Home_Familiar.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(VincularPaciente.this, "UID del paciente inválido", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        });
    }
}
