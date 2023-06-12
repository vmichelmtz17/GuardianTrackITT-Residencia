package com.residencia.guardiantrackitt;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeFragment extends Fragment {

    private TextView nombreTextView;
    private TextView fechaNacimientoTextView;
    private DatabaseReference pacienteRef;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Obtener referencia al nodo del paciente en la base de datos
        pacienteRef = FirebaseDatabase.getInstance().getReference().child("pacientes");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        nombreTextView = view.findViewById(R.id.nombreTextView);
        fechaNacimientoTextView = view.findViewById(R.id.fechaNacimientoTextView);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Leer los datos del paciente desde la base de datos
        pacienteRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        // Obtener los datos del paciente
                        String nombre = snapshot.child("nombre").getValue(String.class);
                        String fechaNacimiento = snapshot.child("fechaNacimiento").getValue(String.class);
                        // Actualizar la interfaz de usuario con los datos del paciente
                        nombreTextView.setText(nombre);
                        fechaNacimientoTextView.setText(fechaNacimiento);
                        // Mostrar solo los datos del primer paciente encontrado
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar el error de la base de datos si es necesario
            }
        });
    }
}
