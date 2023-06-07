package com.residencia.guardiantrackitt;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Perfil extends AppCompatActivity {

    private TextView textViewNombre;
    private TextView textViewCorreo;
    private TextView textViewCelular;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        textViewNombre = findViewById(R.id.textViewNombre);
        textViewCorreo = findViewById(R.id.textViewCorreo);
        textViewCelular = findViewById(R.id.textViewCelular);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        cargarDatosUsuario();
    }

    private void cargarDatosUsuario() {
        if (currentUser != null) {
            // Obtener la referencia al documento del usuario actual en Firestore
            DocumentReference userRef = db.collection("users").document(currentUser.getUid());

            userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    // Obtener los datos del documento y mostrarlos en los TextView correspondientes
                    String nombre = documentSnapshot.getString("name");
                    String correo = currentUser.getEmail();
                    String celular = currentUser.getPhoneNumber();

                    textViewNombre.setText("Nombre: " + nombre);
                    textViewCorreo.setText("Correo: " + correo);
                    textViewCelular.setText("Celular: " + celular);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Manejar el error
                    Toast.makeText(Perfil.this, "Error al cargar los datos del usuario.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "No se ha iniciado sesión", Toast.LENGTH_SHORT).show();
        }
    }
}
