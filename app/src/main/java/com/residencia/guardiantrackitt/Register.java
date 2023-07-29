package com.residencia.guardiantrackitt;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    private EditText nameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private EditText phoneEditText;
    private Button registerButton;
    private CheckBox showPasswordCheckbox;
    private FirebaseAuth mAuth;
    private DatabaseReference userTypeRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        userTypeRef = FirebaseDatabase.getInstance().getReference().child("users").child("userType");

        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        registerButton = findViewById(R.id.registerButton);
        showPasswordCheckbox = findViewById(R.id.showPasswordCheckbox);
        ImageButton passwordInfoButton = findViewById(R.id.passwordInfoButton);
        passwordInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPasswordFormatDialog();
            }
        });

        showPasswordCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                togglePasswordVisibility(isChecked);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                String confirmPassword = confirmPasswordEditText.getText().toString().trim();
                String phone = phoneEditText.getText().toString().trim();

                if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()
                        || phone.isEmpty()) {
                    Toast.makeText(Register.this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
                } else if (!password.equals(confirmPassword)) {
                    Toast.makeText(Register.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                } else if (password.length() < 6 || !password.matches(".*[A-Z].*") || !password.matches(".*[a-z].*")) {
                    Toast.makeText(Register.this, "La contraseña debe tener al menos 6 caracteres, una mayúscula y una minúscula", Toast.LENGTH_SHORT).show();
                } else {
                    registerFamiliar(name, email, password, phone);
                }
            }
        });
    }
    private void showPasswordFormatDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Formato de Contraseña");
        builder.setMessage("La contraseña debe cumplir los siguientes requisitos:\n" +
                " - Mínimo 6 caracteres\n" +
                " - Mínimo 1 mayúscula\n" +
                " - Mínimo 1 minúscula\n" +
                " - Mínimo 1 número");
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.parseColor("#FFA500"));
            }
        });

        alertDialog.show();
    }

    private void togglePasswordVisibility(boolean showPassword) {
        int passwordInputType = showPassword
                ? InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                : InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD;

        passwordEditText.setInputType(passwordInputType);
        confirmPasswordEditText.setInputType(passwordInputType);
    }

    private void registerFamiliar(String name, String email, String password, String phone) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            if (currentUser != null) {
                                String uid = currentUser.getUid();

                                // Guardar en Firebase Realtime Database
                                DatabaseReference userDataRef = FirebaseDatabase.getInstance().getReference().child("users").child("userData").child(uid);
                                userDataRef.child("name").setValue(name);
                                userDataRef.child("phone").setValue(phone);

                                // Guardar en Firestore
                                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                                DocumentReference userFirestoreRef = firestore.collection("users").document(uid);
                                Map<String, Object> userData = new HashMap<>();
                                userData.put("name", name);
                                userData.put("email", email);
                                userData.put("phone", phone);
                                userData.put("userType", "Familiar"); // Agregar el tipo de usuario
                                userFirestoreRef.set(userData, SetOptions.merge())
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Intent intent = new Intent(Register.this, RegistrarPaciente.class);
                                                    startActivity(intent);
                                                    finish();
                                                } else {
                                                    Toast.makeText(Register.this, "Error al registrar el usuario", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        } else {
                            Toast.makeText(Register.this, "Error al registrar el usuario", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
