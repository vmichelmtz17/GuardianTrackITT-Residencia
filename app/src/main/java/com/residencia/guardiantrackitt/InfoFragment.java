package com.residencia.guardiantrackitt;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class InfoFragment extends Fragment {

    private EditText editTextNombre;
    private EditText editTextFechaNacimiento;
    private Button buttonGuardar;

    private PacienteModel pacienteModel;

    public InfoFragment() {
        // Required empty public constructor
    }

    public static InfoFragment newInstance(PacienteModel pacienteModel) {
        InfoFragment fragment = new InfoFragment();
        fragment.pacienteModel = pacienteModel;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info, container, false);

        editTextNombre = view.findViewById(R.id.editTextNombre);
        editTextFechaNacimiento = view.findViewById(R.id.editTextFechaNacimiento);
        buttonGuardar = view.findViewById(R.id.buttonGuardar);

        // Establecer los datos del paciente en los EditText
        if (pacienteModel != null) {
            editTextNombre.setText(pacienteModel.getNombre());
            editTextFechaNacimiento.setText(pacienteModel.getFechaNacimiento());
            editTextNombre.setEnabled(false);
            editTextFechaNacimiento.setEnabled(false);
            buttonGuardar.setText("Editar Información");
        } else {
            // Obtén el último nombre y fecha de nacimiento registrados desde Firebase
            obtenerUltimosDatosRegistrados();
            buttonGuardar.setText("Agregar Información");
        }

        buttonGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonGuardar.getText().equals("Agregar Información")) {
                    editTextNombre.setEnabled(true);
                    editTextFechaNacimiento.setEnabled(true);
                    buttonGuardar.setText("Guardar Información");
                } else if (buttonGuardar.getText().equals("Editar Información")) {
                    editTextNombre.setEnabled(true);
                    editTextFechaNacimiento.setEnabled(true);
                    buttonGuardar.setText("Guardar Información");
                } else if (buttonGuardar.getText().equals("Guardar Información")) {
                    String nombre = editTextNombre.getText().toString();
                    String fechaNacimiento = editTextFechaNacimiento.getText().toString();

                    // Llama al método de la actividad Paciente para guardar los datos en Firebase
                    if (getActivity() instanceof Paciente) {
                        ((Paciente) getActivity()).guardarDatosPaciente(nombre, fechaNacimiento);
                    }

                    editTextNombre.setEnabled(false);
                    editTextFechaNacimiento.setEnabled(false);
                    buttonGuardar.setText("Editar Información");
                }
            }
        });

        return view;
    }

    private void obtenerUltimosDatosRegistrados() {
        // Obtén una referencia a la base de datos de Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference pacientesRef = database.getReference("pacientes");

        // Realiza una consulta para obtener el último paciente registrado
        pacientesRef.orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    PacienteModel paciente = snapshot.getValue(PacienteModel.class);
                    if (paciente != null) {
                        editTextNombre.setText(paciente.getNombre());
                        editTextFechaNacimiento.setText(paciente.getFechaNacimiento());
                    }
                    break;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Manejo de errores de la consulta
            }
        });
    }
}