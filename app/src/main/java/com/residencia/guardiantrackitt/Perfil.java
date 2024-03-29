package com.residencia.guardiantrackitt;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Perfil extends AppCompatActivity {

    private TextView textViewNombre;
    private TextView textViewCorreo;
    private TextView textViewCelular;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private ImageView profileImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        textViewNombre = findViewById(R.id.textViewNombre);
        textViewCorreo = findViewById(R.id.textViewCorreo);
        textViewCelular = findViewById(R.id.textViewCelular);
        profileImageView = findViewById(R.id.profileImageView);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        cargarDatosUsuario();
    }

    private void cargarDatosUsuario() {
        if (currentUser != null) {
            // Obtener la referencia al documento del usuario actual en Cloud Firestore
            db.collection("users").document(currentUser.getUid())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String nombre = document.getString("name");
                                String correo = currentUser.getEmail();
                                String celular = document.getString("phone");

                                textViewNombre.setText("Nombre: " + nombre);
                                textViewCorreo.setText("Correo: " + correo);
                                textViewCelular.setText("Celular: " + celular);

                                // Cargar y mostrar la foto de perfil
                                String fotoPerfil = document.getString("profileImageUri");
                                if (fotoPerfil != null && !fotoPerfil.isEmpty()) {
                                    Glide.with(this)
                                            .load(fotoPerfil)
                                            .placeholder(R.drawable.default_profile_image)
                                            .error(R.drawable.default_profile_image)
                                            .into(profileImageView);
                                }
                            } else {
                                Toast.makeText(Perfil.this, "No se encontraron datos del usuario en la base de datos.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(Perfil.this, "Error al cargar los datos del usuario.", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "No se ha iniciado sesión", Toast.LENGTH_SHORT).show();
        }
    }
}
