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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

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

    private FirebaseStorage storage;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paciente);

        // Referencias a los componentes del layout
        editTextNombre = findViewById(R.id.editTextNombre);
        editTextFechaNacimiento = findViewById(R.id.editTextFechaNacimiento);
        buttonAgregarFotoPapa = findViewById(R.id.buttonAgregarFotoPapa);
        buttonAgregarFotoMama = findViewById(R.id.buttonAgregarFotoMama);
        buttonAgregarFotoPaciente = findViewById(R.id.buttonAgregarFotoPaciente);
        imageViewFotoPapa = findViewById(R.id.imageViewFotoPapa);
        imageViewFotoMama = findViewById(R.id.imageViewFotoMama);
        imageViewFotoPaciente = findViewById(R.id.imageViewFotoPaciente);

        // Inicializar Firebase Storage
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        // Acción del botón "Agregar foto de papá"
        buttonAgregarFotoPapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        // Acción del botón "Agregar foto de mamá"
        buttonAgregarFotoMama.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        // Acción del botón "Agregar foto del paciente"
        buttonAgregarFotoPaciente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageViewFotoPapa.setImageBitmap(imageBitmap);
            imageViewFotoMama.setImageBitmap(imageBitmap);
            imageViewFotoPaciente.setImageBitmap(imageBitmap);

            // Crear una referencia al archivo en Firebase Storage
            StorageReference imageRef = storageRef.child("fotos").child("foto.jpg");

            // Comprimir la imagen en formato JPEG
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageData = baos.toByteArray();

            // Subir la imagen a Firebase Storage
            UploadTask uploadTask = imageRef.putBytes(imageData);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // La imagen se ha subido exitosamente
                    // Obtener la URL de descarga de la imagen
                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri downloadUrl) {
                            // La URL de descarga de la imagen
                            String imageUrl = downloadUrl.toString();
                            // Guardar la URL en la base de datos o realizar otras acciones
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Ocurrió un error al subir la imagen a Firebase Storage
                }
            });
        }
    }
}
