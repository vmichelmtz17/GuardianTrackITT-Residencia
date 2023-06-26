package com.residencia.guardiantrackitt;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeFragment extends Fragment {

    private TextView nombreTextView;
    private TextView fechaNacimientoTextView;
    private TextView edadTextView;
    private Button btnCerrarSesion;
    private FirebaseAuth mAuth;
    private DatabaseReference userDataRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        nombreTextView = view.findViewById(R.id.nombreTextView);
        fechaNacimientoTextView = view.findViewById(R.id.fechaNacimientoTextView);
        edadTextView = view.findViewById(R.id.edadTextView);
        btnCerrarSesion = view.findViewById(R.id.btnCerrarSesion);

        mAuth = FirebaseAuth.getInstance();
        userDataRef = FirebaseDatabase.getInstance().getReference().child("users").child("userData");

        btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                // Redirigir a la actividad de inicio de sesión
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userRef = userDataRef.child(userId);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String nombre = dataSnapshot.child("name").getValue(String.class);
                        if (nombre != null) {
                            nombreTextView.setText("Nombre del Paciente: " + nombre);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Manejar caso de error de lectura de datos
                }
            });
        }
    }
}