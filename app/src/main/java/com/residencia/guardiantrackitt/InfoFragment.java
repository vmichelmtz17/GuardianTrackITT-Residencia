package com.residencia.guardiantrackitt;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

public class InfoFragment extends Fragment {

    private EditText nombreEditText;
    private EditText fechaNacimientoEditText;
    private Button btnEditarInfo;
    private DatabaseReference userDataRef;
    private FirebaseUser currentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info, container, false);

        nombreEditText = view.findViewById(R.id.nombreEditText);
        fechaNacimientoEditText = view.findViewById(R.id.fechaNacimientoEditText);
        btnEditarInfo = view.findViewById(R.id.btnEditarInfo);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            userDataRef = FirebaseDatabase.getInstance().getReference().child("users").child("userData").child(userId);

            userDataRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String nombre = dataSnapshot.child("name").getValue(String.class);
                        String fechaNacimiento = dataSnapshot.child("dateOfBirth").getValue(String.class);

                        nombreEditText.setText(nombre);
                        fechaNacimientoEditText.setText(fechaNacimiento);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Manejar caso de error de lectura de datos
                }
            });
        }
        btnEditarInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener los valores editados
                String nuevoNombre = nombreEditText.getText().toString();
                String nuevaFechaNacimiento = fechaNacimientoEditText.getText().toString();


                // Actualizar la información en Firebase
                userDataRef.child("name").setValue(nuevoNombre);
                userDataRef.child("dateOfBirth").setValue(nuevaFechaNacimiento);

                // Mostrar un mensaje o realizar alguna acción adicional
                Toast.makeText(requireContext(), "Información actualizada en Firebase", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
}