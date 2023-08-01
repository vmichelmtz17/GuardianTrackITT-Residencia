package com.residencia.guardiantrackitt;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Pulsos extends AppCompatActivity {

    private TextView txtPulsaciones;
    private ListView pulsacionesListView;
    private ArrayAdapter<String> pulsacionesAdapter;
    private List<String> pulsacionesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pulsos);

        txtPulsaciones = findViewById(R.id.txtPulsaciones);
        pulsacionesListView = findViewById(R.id.pulsacionesListView);

        pulsacionesList = new ArrayList<>();
        pulsacionesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, pulsacionesList);
        pulsacionesListView.setAdapter(pulsacionesAdapter);

        // Aquí obtenemos el UID del usuario actualmente autenticado (familiar)
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Creamos la referencia a la ubicación correcta en Firebase Realtime Database
        DatabaseReference pulsacionesRef = FirebaseDatabase.getInstance()
                .getReference("users").child("userType").child("Familiar").child(uid).child("pacientes").child("Pulsaciones");

        // Agregamos un listener para escuchar los cambios en el valor de pulsaciones en Firebase
        pulsacionesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Cuando cambia el valor en Firebase, se ejecuta este método

                // Obtenemos el valor de pulsaciones desde Firebase
                String pulsaciones = dataSnapshot.getValue(String.class);

                // Mostramos el valor en el TextView
                txtPulsaciones.setText(pulsaciones);

                // Agregamos el valor a la lista y notificamos al adaptador para actualizar la ListView
                pulsacionesList.add(pulsaciones);
                pulsacionesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Manejar el error si ocurre alguno al recuperar los datos de Firebase
            }
        });
    }
}
