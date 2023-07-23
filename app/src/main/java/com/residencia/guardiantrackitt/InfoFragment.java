package com.residencia.guardiantrackitt;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class InfoFragment extends Fragment {

    private EditText nombreEditText;
    private EditText fechaNacimientoEditText;
    private Button btnEditarInfo;
    private DatabaseReference userDataRef;
    private FirebaseUser currentUser;
    private TextView uidTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info, container, false);

        nombreEditText = view.findViewById(R.id.nombreEditText);
        fechaNacimientoEditText = view.findViewById(R.id.fechaNacimientoEditText);
        btnEditarInfo = view.findViewById(R.id.btnEditarInfo);
        uidTextView = view.findViewById(R.id.uidTextView);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            userDataRef = FirebaseDatabase.getInstance().getReference().child("users").child("userData").child(userId);

            userDataRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String nombre = dataSnapshot.child("name").getValue(String.class);
                        String fechaNacimiento = dataSnapshot.child("dateOfBirth").getValue(String.class);

                        nombreEditText.setText(nombre);
                        fechaNacimientoEditText.setText(fechaNacimiento);

                        uidTextView.setText("UID del usuario: " + userId); // Mostrar el UID del usuario actual en el TextView
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Manejar caso de error de lectura de datos
                }
            });
        }

        // Agregar OnClickListener al TextView para copiar el UID al portapapeles
        uidTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyUidToClipboard();
            }
        });

        btnEditarInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener los valores editados
                String nuevoNombre = nombreEditText.getText().toString();
                String nuevaFechaNacimiento = fechaNacimientoEditText.getText().toString();

                // Actualizar la información en Firebase
                userDataRef.child("name").setValue(nuevoNombre);
                userDataRef.child("dateOfBirth").setValue(nuevaFechaNacimiento);

                // Mostrar un mensaje o realizar alguna acción adicional
                Toast.makeText(requireContext(), "Información actualizada en Firebase", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    private void copyUidToClipboard() {
        if (currentUser != null) {
            String userId = currentUser.getUid();
            if (!TextUtils.isEmpty(userId)) {
                ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("UID", userId);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(requireContext(), "UID copiado al portapapeles", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
