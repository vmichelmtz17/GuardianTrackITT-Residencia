package com.residencia.guardiantrackitt;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Paciente extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private EditText editTextNombre;
    private EditText editTextFechaNacimiento;
    private Button buttonAgregarFotoPapa;
    private Button buttonAgregarFotoMama;
    private Button buttonAgregarFotoPaciente;
    private ImageView imageViewFotoPapa;
    private ImageView imageViewFotoMama;
    private ImageView imageViewFotoPaciente;
    private Button buttonEditarGuardar;

    private FirebaseStorage storage;
    private StorageReference storageRef;
    private DatabaseReference databaseRef;

    private int selectedImageViewId;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paciente);

        editTextNombre = findViewById(R.id.editTextNombre);
        editTextFechaNacimiento = findViewById(R.id.editTextFechaNacimiento);
        buttonAgregarFotoPapa = findViewById(R.id.buttonAgregarFotoPapa);
        buttonAgregarFotoMama = findViewById(R.id.buttonAgregarFotoMama);
        buttonAgregarFotoPaciente = findViewById(R.id.buttonAgregarFotoPaciente);
        imageViewFotoPapa = findViewById(R.id.imageViewFotoPapa);
        imageViewFotoMama = findViewById(R.id.imageViewFotoMama);
        imageViewFotoPaciente = findViewById(R.id.imageViewFotoPaciente);
        buttonEditarGuardar = findViewById(R.id.buttonEditarGuardar);

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        databaseRef = FirebaseDatabase.getInstance().getReference("pacientes");

        buttonAgregarFotoPapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedImageViewId = imageViewFotoPapa.getId();
                dispatchTakePictureIntent();
            }
        });

        buttonAgregarFotoMama.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedImageViewId = imageViewFotoMama.getId();
                dispatchTakePictureIntent();
            }
        });

        buttonAgregarFotoPaciente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedImageViewId = imageViewFotoPaciente.getId();
                dispatchTakePictureIntent();
            }
        });

        buttonEditarGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEditMode) {
                    saveChanges();
                    isEditMode = false;
                    buttonEditarGuardar.setText("Editar");
                    editTextNombre.setEnabled(false);
                    editTextFechaNacimiento.setEnabled(false);
                } else {
                    isEditMode = true;
                    buttonEditarGuardar.setText("Guardar");
                    editTextNombre.setEnabled(true);
                    editTextFechaNacimiento.setEnabled(true);
                }
            }
        });
    }

    private void saveChanges() {
        String nombre = editTextNombre.getText().toString();
        String fechaNacimiento = editTextFechaNacimiento.getText().toString();

        // Guardar el nombre y la fecha de nacimiento en Firebase Realtime Database
        String pacienteId = databaseRef.push().getKey();
        PacienteModel paciente = new PacienteModel(pacienteId, nombre, fechaNacimiento);
        databaseRef.child(pacienteId).setValue(paciente);
    }

    private void dispatchTakePictureIntent() {
        Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (pickPhotoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(pickPhotoIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                ImageView selectedImageView = findViewById(selectedImageViewId);
                selectedImageView.setImageBitmap(imageBitmap);

                StorageReference imageRef = storageRef.child("fotos").child("foto.jpg");

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imageData = baos.toByteArray();

                UploadTask uploadTask = imageRef.putBytes(imageData);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String imageUrl = uri.toString();
                                // Aquí puedes guardar la URL de la imagen en la base de datos u realizar otras acciones necesarias
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Error al subir la imagen a Firebase Storage
                        // Manejar el error según sea necesario
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}