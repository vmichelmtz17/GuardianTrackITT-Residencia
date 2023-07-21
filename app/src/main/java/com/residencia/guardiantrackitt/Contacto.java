package com.residencia.guardiantrackitt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Contacto extends AppCompatActivity {

    private EditText editTextNombre;
    private EditText editTextNumero;
    private Button buttonAgregar, buttonMensajeTemporal;
    private ListView listViewContactos;
    private DatabaseReference contactosRef;
    private ContactoAdapter contactoAdapter;
    private FirebaseUser currentUser;
    private boolean hasContact = false;

    private void verificarContactosEnFirebase() {
        if (currentUser != null) {
            String userId = currentUser.getUid();
            Query query = contactosRef.child(userId);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Si hay contactos, deshabilitar los EditText
                        editTextNombre.setEnabled(false);
                        editTextNumero.setEnabled(false);
                        hasContact = true;
                        buttonAgregar.setText("Borrar");
                    } else {
                        // Si no hay contactos, habilitar los EditText
                        editTextNombre.setEnabled(true);
                        editTextNumero.setEnabled(true);
                        hasContact = false;
                        buttonAgregar.setText("Agregar");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Manejar el error si no se pueden obtener los contactos de Firebase
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacto);

        editTextNombre = findViewById(R.id.editTextNombre);
        editTextNumero = findViewById(R.id.editTextNumero);
        buttonAgregar = findViewById(R.id.buttonAgregar);
        listViewContactos = findViewById(R.id.listViewContactos);
        buttonMensajeTemporal = findViewById(R.id.buttonMensajeTemporal);

        contactosRef = FirebaseDatabase.getInstance().getReference("contactos");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        contactoAdapter = new ContactoAdapter(this, R.layout.item_contacto);
        listViewContactos.setAdapter(contactoAdapter);

        verificarContactosEnFirebase();

        buttonAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombre = editTextNombre.getText().toString();
                String numero = editTextNumero.getText().toString();

                // Validar que ambos campos estén completos
                if (!nombre.isEmpty() && !numero.isEmpty()) {
                    // Validar que el número tenga exactamente 10 dígitos
                    if (numero.length() == 10) {
                        guardarContactoEnFirebase(nombre, numero);

                        editTextNombre.setText("");
                        editTextNumero.setText("");
                    } else {
                        Toast.makeText(Contacto.this, "El número debe tener 10 dígitos", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Contacto.this, "Completa ambos campos", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonMensajeTemporal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarMensajeTemporal();
            }
        });

        listViewContactos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ContactoModel contacto = (ContactoModel) parent.getItemAtPosition(position);
                mostrarDialogContacto(contacto);
            }
        });

        mostrarContactosDesdeFirebase();
    }

    private void mostrarMensajeTemporal() {
        if (contactoAdapter.getCount() > 0) {
            Toast.makeText(this, "Ya has agregado un contacto", Toast.LENGTH_SHORT).show();
        } else {
            String numero = editTextNumero.getText().toString();
            if (numero.length() == 10) {
                Toast.makeText(this, "Selecciona un contacto para realizar acciones", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Selecciona un contacto para realizar acciones \n El número debe tener 10 dígitos", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void mostrarContactosDesdeFirebase() {
        if (currentUser != null) {
            String userId = currentUser.getUid();
            Query query = contactosRef.child(userId).orderByKey();
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
    }

    private void guardarContactoEnFirebase(String nombre, String numero) {
        if (currentUser != null && !hasContact) {
            String userId = currentUser.getUid();
            String contactoId = contactosRef.child(userId).push().getKey();
            String numeroCompleto = "+52" + numero; // Agregar el prefijo "+52" al número de teléfono
            ContactoModel contacto = new ContactoModel(contactoId, nombre, numeroCompleto);
            if (contactoId != null) {
                contactosRef.child(userId).child(contactoId).setValue(contacto);
                hasContact = true;
                buttonAgregar.setText("Borrar");
                // Deshabilitar los EditText
                editTextNombre.setEnabled(false);
                editTextNumero.setEnabled(false);
            }
        } else {
            Toast.makeText(this, "Ya has agregado un contacto", Toast.LENGTH_SHORT).show();
        }
    }

    private void mostrarDialogContacto(final ContactoModel contacto) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Editar, eliminar o abrir en WhatsApp");
        dialogBuilder.setMessage("¿Qué acción deseas realizar?");

        SpannableString editarOption = new SpannableString("Editar");
        editarOption.setSpan(new ForegroundColorSpan(Color.BLACK), 0, editarOption.length(), 0);
        dialogBuilder.setPositiveButton(editarOption, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mostrarDialogEditarContacto(contacto);
            }
        });

        SpannableString eliminarOption = new SpannableString("Eliminar");
        eliminarOption.setSpan(new ForegroundColorSpan(Color.BLACK), 0, eliminarOption.length(), 0);
        dialogBuilder.setNegativeButton(eliminarOption, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                eliminarContactoEnFirebase(contacto);
            }
        });

        SpannableString whatsappOption = new SpannableString("Abrir en WhatsApp");
        whatsappOption.setSpan(new ForegroundColorSpan(Color.BLACK), 0, whatsappOption.length(), 0);
        dialogBuilder.setNeutralButton(whatsappOption, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                abrirChatWhatsApp(contacto.getNumero());
            }
        });

        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }

    private void abrirChatWhatsApp(String numero) {
        try {
            String url = "https://api.whatsapp.com/send?phone=" + numero;
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "WhatsApp no está instalado en tu dispositivo", Toast.LENGTH_SHORT).show();
        }
    }

    private void mostrarDialogEditarContacto(final ContactoModel contacto) {
        try {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_editar_contacto, null);
            dialogBuilder.setView(dialogView);

            final EditText editTextNuevoNombre = dialogView.findViewById(R.id.editTextNuevoNombre);
            final EditText editTextNuevoNumero = dialogView.findViewById(R.id.editTextNuevoNumero);

            editTextNuevoNombre.setText(contacto.getNombre());
            editTextNuevoNumero.setText(contacto.getNumero());

            dialogBuilder.setTitle("Editar contacto");
            SpannableString guardarButton = new SpannableString("Guardar");
            guardarButton.setSpan(new ForegroundColorSpan(Color.BLACK), 0, guardarButton.length(), 0);
            dialogBuilder.setPositiveButton(guardarButton, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    String nuevoNombre = editTextNuevoNombre.getText().toString();
                    String nuevoNumero = editTextNuevoNumero.getText().toString();

                    if (!nuevoNombre.isEmpty() && !nuevoNumero.isEmpty()) {
                        // Actualizar los datos del contacto
                        contacto.setNombre(nuevoNombre);
                        contacto.setNumero(nuevoNumero);

                        if (currentUser != null) {
                            String userId = currentUser.getUid();
                            DatabaseReference contactoRef = contactosRef.child(userId).child(contacto.getId());
                            contactoRef.setValue(contacto)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(Contacto.this, "Contacto actualizado", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(Contacto.this, "Error al actualizar el contacto", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        Toast.makeText(Contacto.this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            SpannableString cancelarButton = new SpannableString("Cancelar");
            cancelarButton.setSpan(new ForegroundColorSpan(Color.BLACK), 0, cancelarButton.length(), 0);
            dialogBuilder.setNegativeButton(cancelarButton, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // Cancelar la edición del contacto
                }
            });

            AlertDialog alertDialog = dialogBuilder.create();
            alertDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void eliminarContactoEnFirebase(final ContactoModel contacto) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Eliminar contacto");
        dialogBuilder.setMessage("¿Estás seguro que deseas eliminar este contacto?");

        SpannableString aceptarButton = new SpannableString("Aceptar");
        aceptarButton.setSpan(new ForegroundColorSpan(Color.BLACK), 0, aceptarButton.length(), 0);
        dialogBuilder.setPositiveButton(aceptarButton, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (currentUser != null) {
                    String userId = currentUser.getUid();
                    DatabaseReference contactoRef = contactosRef.child(userId).child(contacto.getId());
                    contactoRef.removeValue()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(Contacto.this, "Contacto eliminado", Toast.LENGTH_SHORT).show();
                                    contactoAdapter.remove(contacto);
                                    hasContact = false;
                                    buttonAgregar.setText("Agregar");
                                    // Deshabilitar los EditText
                                    editTextNombre.setEnabled(true);
                                    editTextNumero.setEnabled(true);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Contacto.this, "Error al eliminar el contacto", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

        SpannableString cancelarButton = new SpannableString("Cancelar");
        cancelarButton.setSpan(new ForegroundColorSpan(Color.BLACK), 0, cancelarButton.length(), 0);
        dialogBuilder.setNegativeButton(cancelarButton, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Cancelar la eliminación del contacto
            }
        });

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }
}