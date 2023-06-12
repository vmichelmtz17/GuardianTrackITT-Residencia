package com.residencia.guardiantrackitt;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class InfoFragment extends Fragment {

    private EditText editTextNombre;
    private EditText editTextFechaNacimiento;
    private Button buttonAgregarInformacion;
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
        buttonAgregarInformacion = view.findViewById(R.id.buttonAgregarInformacion);
        buttonGuardar = view.findViewById(R.id.buttonGuardar);

        // Establecer los datos del paciente en los EditText
        if (pacienteModel != null) {
            editTextNombre.setText(pacienteModel.getNombre());
            editTextFechaNacimiento.setText(pacienteModel.getFechaNacimiento());
            editTextNombre.setEnabled(false);
            editTextFechaNacimiento.setEnabled(false);
        }

        buttonAgregarInformacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextNombre.setEnabled(true);
                editTextFechaNacimiento.setEnabled(true);
            }
        });

        buttonGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombre = editTextNombre.getText().toString();
                String fechaNacimiento = editTextFechaNacimiento.getText().toString();

                // Llama al método de la actividad Paciente para guardar los datos en Firebase
                if (getActivity() instanceof Paciente) {
                    ((Paciente) getActivity()).guardarDatosPaciente(nombre, fechaNacimiento);
                }

                editTextNombre.setEnabled(false);
                editTextFechaNacimiento.setEnabled(false);
            }
        });

        return view;
    }
}