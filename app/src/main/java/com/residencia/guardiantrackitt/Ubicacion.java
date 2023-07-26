package com.residencia.guardiantrackitt;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class Ubicacion extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubicacion);

        // Obtener una instancia del fragmento UbicacionPaciente
        UbicacionFragment ubicacionPacienteFragment = new UbicacionFragment();

        // Agregar el fragmento al contenedor (FrameLayout) definido en activity_ubicacion_paciente.xml
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, ubicacionPacienteFragment)
                .commit();
    }
}
