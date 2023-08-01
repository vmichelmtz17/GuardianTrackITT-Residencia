package com.residencia.guardiantrackitt;// RegistroPulsaciones.java
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class RegistroPulsaciones extends AppCompatActivity {

    private List<String> listaPulsaciones = new ArrayList<>();
    private DatabaseReference pulsacionesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_pulsaciones);

        // Obtén el UID del usuario familiar autenticado
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Obtén la referencia a la ubicación correcta en Firebase Realtime Database
        pulsacionesRef = FirebaseDatabase.getInstance()
                .getReference("users").child("userType").child("Familiar").child(uid).child("pacientes").child(uid).child("Pulsaciones");

        // Agregar un listener para recibir los datos nuevos de pulsaciones desde Firebase
        pulsacionesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Limpiar la lista antes de agregar los nuevos datos
                listaPulsaciones.clear();

                // Recorrer los hijos del nodo y agregarlos a la lista
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String pulsacion = snapshot.getValue(String.class);
                    listaPulsaciones.add(pulsacion);
                    // Agregar la pulsación a la lista en PulsacionesData
                    PulsacionesData.getInstance().agregarPulsacion(pulsacion);
                }

                // Actualizar la vista con los nuevos datos
                actualizarListView();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Manejar error de lectura desde Firebase
                // (opcional, según tus necesidades)
            }
        });
    }

    private void actualizarListView() {
        // Obtener el ListView desde el layout
        ListView listViewPulsaciones = findViewById(R.id.listViewPulsaciones);

        // Crear un adaptador para mostrar la lista de pulsaciones en el ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaPulsaciones);

        // Asignar el adaptador al ListView
        listViewPulsaciones.setAdapter(adapter);
    }
}