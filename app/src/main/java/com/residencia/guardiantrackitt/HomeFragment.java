package com.residencia.guardiantrackitt;

import android.content.Intent;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private TextView nombreTextView;
    private TextView fechaNacimientoTextView;
    private TextView edadTextView;
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
        edadTextView = view.findViewById(R.id.edadTextView);
        Button btnCerrarSesion = view.findViewById(R.id.btnCerrarSesion);
        btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cerrarSesion();
            }
        });
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
                        nombreTextView.setText("Nombre: " + nombre);
                        fechaNacimientoTextView.setText("Fecha de Nacimiento: " + fechaNacimiento);

                        // Calcular la edad actual
                        int edad = calcularEdad(fechaNacimiento);
                        edadTextView.setText(String.valueOf("Edad actual: " + edad));

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

    private void cerrarSesion() {
        // Cerrar sesión en Firebase
        FirebaseAuth.getInstance().signOut();

        // Redirigir a la clase MainActivity
        startActivity(new Intent(getActivity(), MainActivity.class));
        getActivity().finish();
    }

    private int calcularEdad(String fechaNacimiento) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            Date fechaNac = dateFormat.parse(fechaNacimiento);
            Calendar calendarNac = Calendar.getInstance();
            calendarNac.setTime(fechaNac);

            Calendar calendarActual = Calendar.getInstance();
            int diffYear = calendarActual.get(Calendar.YEAR) - calendarNac.get(Calendar.YEAR);
            int diffMonth = calendarActual.get(Calendar.MONTH) - calendarNac.get(Calendar.MONTH);
            int diffDay = calendarActual.get(Calendar.DAY_OF_MONTH) - calendarNac.get(Calendar.DAY_OF_MONTH);

            // Ajustar la edad si aún no se ha cumplido el mes o el día de nacimiento
            if (diffMonth < 0 || (diffMonth == 0 && diffDay < 0)) {
                diffYear--;
            }

            return diffYear;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }
}