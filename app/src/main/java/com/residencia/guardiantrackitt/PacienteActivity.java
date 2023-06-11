package com.residencia.guardiantrackitt;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PacienteActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_SELECT_IMAGE = 1;

    private RecyclerView recyclerView;
    private PhotoAdapter photoAdapter;
    private List<Photo> photoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        photoList = new ArrayList<>();
        photoAdapter = new PhotoAdapter(photoList);
        recyclerView.setAdapter(photoAdapter);

        loadPhotosFromFirebase();

        FloatingActionButton fabAddPhoto = findViewById(R.id.fabAddPhoto);
        fabAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
    }

    private void uploadPhotoToFirebase(Uri imageUri) {
        // Obtener una referencia a la carpeta de Firebase Storage donde se almacenarán las fotos
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("photos");

        // Generar un nombre único para la foto
        String photoId = UUID.randomUUID().toString();

        // Crear una referencia para la foto dentro de la carpeta de Firebase Storage
        StorageReference photoRef = storageRef.child(photoId);

        // Subir la foto a Firebase Storage
        photoRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Obtener la URL de descarga de la foto
                        photoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String imageUrl = uri.toString();
                                // Guardar la información de la foto en la base de datos (Firebase Realtime Database o Cloud Firestore)
                                savePhotoInfoToDatabase(imageUrl);
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Manejar errores en la carga de la foto
                    }
                });
    }

    private void savePhotoInfoToDatabase(String imageUrl) {
        // Aquí implementa el código para guardar la información de la foto en la base de datos de Firebase
        // Utiliza las clases y métodos correspondientes de Firebase para realizar las operaciones de escritura
    }

    private void loadPhotosFromFirebase() {
        // Aquí implementa el código para cargar las fotos desde Firebase Storage
        // Utiliza las clases y métodos de Firebase para acceder al almacenamiento y obtener las URLs de las imágenes
        // Actualiza la lista photoList en el adaptador y llama a notifyDataSetChanged() para refrescar la vista del RecyclerView
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            uploadPhotoToFirebase(imageUri);
        }
    }
}
