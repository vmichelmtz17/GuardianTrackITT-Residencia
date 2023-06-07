package com.residencia.guardiantrackitt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Contacto extends AppCompatActivity {

    private EditText editTextNombre;
    private EditText editTextNumero;
    private Button buttonAgregar;
    private ListView listViewContactos;
    private DatabaseReference contactosRef;
    private ContactoAdapter contactoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacto);

        editTextNombre = findViewById(R.id.editTextNombre);
        editTextNumero = findViewById(R.id.editTextNumero);
        buttonAgregar = findViewById(R.id.buttonAgregar);
        listViewContactos = findViewById(R.id.listViewContactos);

        contactosRef = FirebaseDatabase.getInstance().getReference("contactos");

        contactoAdapter = new ContactoAdapter(this, R.layout.item_contacto);
        listViewContactos.setAdapter(contactoAdapter);

        buttonAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombre = editTextNombre.getText().toString();
                String numero = editTextNumero.getText().toString();

                guardarContactoEnFirebase(nombre, numero);

                editTextNombre.setText("");
                editTextNumero.setText("");
            }
        });

        mostrarContactosDesdeFirebase();
    }

    private void mostrarContactosDesdeFirebase() {
        Query query = contactosRef.orderByKey();
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                contactoAdapter.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ContactoModel contacto = snapshot.getValue(ContactoModel.class);
                    if (contacto != null) {
                        contactoAdapter.add(contacto);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar el error al obtener los contactos de Firebase
            }
        });
    }

    private void guardarContactoEnFirebase(String nombre, String numero) {
        String contactoId = contactosRef.push().getKey();
        ContactoModel contacto = new ContactoModel(contactoId, nombre, numero);
        if (contactoId != null) {
            contactosRef.child(contactoId).setValue(contacto);
        }
    }
}