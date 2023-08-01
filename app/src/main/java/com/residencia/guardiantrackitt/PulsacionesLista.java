package com.residencia.guardiantrackitt;// OtraActividad.java
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.residencia.guardiantrackitt.PulsacionesData;

import java.util.List;

public class PulsacionesLista extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pulsaciones_lista);

        // Obtener la lista de pulsos desde el intent
        List<String> pulsos = getIntent().getStringArrayListExtra("lista_pulsos");

        // Obtener el ListView desde el layout
        ListView listViewPulsaciones = findViewById(R.id.listViewPulsaciones);

        // Crear un adaptador para mostrar la lista de pulsos en el ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, pulsos);

        // Asignar el adaptador al ListView
        listViewPulsaciones.setAdapter(adapter);
    }

    // ... (más código de la actividad)
}
