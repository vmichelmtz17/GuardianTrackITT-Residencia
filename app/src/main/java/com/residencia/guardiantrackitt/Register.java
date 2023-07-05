package com.residencia.guardiantrackitt;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
    private TextView passwordRequirementsTextView;
    private EditText confirmPasswordEditText;
    private CheckBox showPasswordCheckbox;
    private EditText phoneEditText;
    private RadioGroup userTypeRadioGroup;
    private RadioButton pacienteRadioButton;
    private Button registerButton;
    private FirebaseAuth mAuth;
    private DatabaseReference userTypeRef;
    private DatabaseReference userDataRef;
    private FirebaseFirestore firestore;
    private EditText dateOfBirthEditText;
    private ImageButton passwordInfoButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        userTypeRef = FirebaseDatabase.getInstance().getReference().child("users").child("userType");
        userDataRef = FirebaseDatabase.getInstance().getReference().child("users").child("userData");
        firestore = FirebaseFirestore.getInstance();

        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        showPasswordCheckbox = findViewById(R.id.showPasswordCheckbox);
        phoneEditText = findViewById(R.id.phoneEditText);
        userTypeRadioGroup = findViewById(R.id.userTypeRadioGroup);
        pacienteRadioButton = findViewById(R.id.pacienteRadioButton);
        registerButton = findViewById(R.id.registerButton);
        dateOfBirthEditText = findViewById(R.id.dateOfBirthEditText);
        passwordInfoButton = findViewById(R.id.passwordInfoButton);

        showPasswordCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int passwordInputType = isChecked ? InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD : InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD;
                passwordEditText.setInputType(passwordInputType);
                confirmPasswordEditText.setInputType(passwordInputType);
            }
        });

        passwordInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Register.this, "Mínimo 6 caracteres,\nMínimo 1 Mayúscula,\nMínimo 1 Minúscula,\nMínimo 1 Número", Toast.LENGTH_SHORT).show();
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
                int selectedUserType = userTypeRadioGroup.getCheckedRadioButtonId();
                String dateOfBirth = dateOfBirthEditText.getText().toString().trim();

                if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()
                        || phone.isEmpty() || selectedUserType == -1) {
                    Toast.makeText(Register.this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
                } else if (!password.equals(confirmPassword)) {
                    Toast.makeText(Register.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                } else if (password.length() < 6 || !password.matches(".*[A-Z].*") || !password.matches(".*[a-z].*")) {
                    Toast.makeText(Register.this, "La contraseña debe tener al menos 6 caracteres, una mayúscula y una minúscula", Toast.LENGTH_SHORT).show();
                } else {
                    String userType = getUserType(selectedUserType);
                    registerUser(name, email, password, phone, userType, dateOfBirth);
                }
            }
        });
    }

    private String getUserType(int selectedId) {
        RadioButton radioButton = findViewById(selectedId);
        return radioButton.getText().toString();
    }

    private void registerUser(String name, String email, String password, String phone, final String userType, String dateOfBirth) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            if (currentUser != null) {
                                String uid = currentUser.getUid();

                                DatabaseReference userRef;
                                if (userType.equals("Familiar")) {
                                    userRef = userTypeRef.child("Familiar").child(uid);
                                } else if (userType.equals("Paciente")) {
                                    userRef = userTypeRef.child("Paciente").child(uid);
                                } else {
                                    return;
                                }

                                userRef.setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            DatabaseReference userRef = userDataRef.child(uid);
                                            userRef.child("name").setValue(name);
                                            userRef.child("phone").setValue(phone);
                                            userRef.child("dateOfBirth").setValue(dateOfBirth);

                                            DocumentReference userFirestoreRef = firestore.collection("users").document(uid);
                                            Map<String, Object> userData = new HashMap<>();
                                            userData.put("name", name);
                                            userData.put("email", email);
                                            userData.put("phone", phone);
                                            userData.put("userType", userType);
                                            userData.put("dateOfBirth", dateOfBirth);

                                            userFirestoreRef.set(userData, SetOptions.merge())
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                if (userType.equals("Familiar")) {
                                                                    Intent intent = new Intent(Register.this, Home_Familiar.class);
                                                                    startActivity(intent);
                                                                } else if (userType.equals("Paciente")) {
                                                                    Intent intent = new Intent(Register.this, Paciente.class);
                                                                    startActivity(intent);
                                                                }
                                                                finish();
                                                            } else {
                                                                Log.e(TAG, "Error al guardar los campos adicionales en Firestore", task.getException());
                                                                Toast.makeText(Register.this, "Error al registrar el usuario", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                        } else {
                                            Log.e(TAG, "Error al guardar el tipo de usuario en la base de datos", task.getException());
                                            Toast.makeText(Register.this, "Error al registrar el usuario", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        } else {
                            Log.e(TAG, "Error en el registro de usuario", task.getException());
                            Toast.makeText(Register.this, "Error al registrar el usuario", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}