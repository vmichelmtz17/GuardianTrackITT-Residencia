package com.residencia.guardiantrackitt;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class PhotosFragment extends Fragment {

    private static final int PERMISSION_REQUEST_CODE = 2;
    private static final int REQUEST_IMAGE_PICK = 3;
    private static final String STATE_IMAGE_URIS = "imageUris";

    private GridView gridView;
    private ImageAdapter imageAdapter;
    private ArrayList<Uri> imageUris;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photos, container, false);

        gridView = view.findViewById(R.id.gridView);
        imageUris = new ArrayList<>();
        imageAdapter = new ImageAdapter(requireContext(), imageUris);
        gridView.setAdapter(imageAdapter);

        Button buttonAddImageManually = view.findViewById(R.id.buttonAddImageManually);
        buttonAddImageManually.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImageFromGallery();
            }
        });

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        } else {
            if (savedInstanceState != null) {
                imageUris = savedInstanceState.getParcelableArrayList(STATE_IMAGE_URIS);
            } else {
                loadSavedImages();
            }
        }

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Implementa aquí la lógica para abrir la imagen en pantalla completa o realizar alguna acción al hacer clic en una imagen de la galería
                Uri imageUri = imageUris.get(position);
                // Realiza la acción deseada con la imagen seleccionada
            }
        });

        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(STATE_IMAGE_URIS, imageUris);
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    private void handleImagePicked(Uri imageUri) {
        // Guardar la URI de la imagen en el almacenamiento interno
        String imagePath = saveImageToInternalStorage(imageUri);
        if (imagePath != null) {
            imageUris.add(Uri.parse(imagePath));
            imageAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(requireContext(), "Error al guardar la imagen", Toast.LENGTH_SHORT).show();
        }
    }

    private String saveImageToInternalStorage(Uri imageUri) {
        try {
            // Obtener el directorio de almacenamiento interno de la aplicación
            File storageDir = requireContext().getFilesDir();

            // Crear un archivo temporal en el directorio de almacenamiento interno
            File imageFile = File.createTempFile("image_", ".jpg", storageDir);

            // Copiar el contenido de la imagen seleccionada al archivo temporal
            InputStream inputStream = requireContext().getContentResolver().openInputStream(imageUri);
            OutputStream outputStream = new FileOutputStream(imageFile);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.close();
            inputStream.close();

            // Devolver la ruta del archivo guardado
            return imageFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void loadSavedImages() {
        // Obtener el directorio de almacenamiento interno de la aplicación
        File storageDir = requireContext().getFilesDir();

        // Obtener la lista de archivos de imagen en el directorio de almacenamiento interno
        File[] imageFiles = storageDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".jpg");
            }
        });

        // Recorrer la lista de archivos y obtener las URIs de las imágenes guardadas
        if (imageFiles != null) {
            for (File imageFile : imageFiles) {
                imageUris.add(Uri.fromFile(imageFile));
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso de lectura de almacenamiento concedido, puedes realizar alguna acción adicional si es necesario
            } else {
                Toast.makeText(requireContext(), "Permiso denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_PICK && resultCode == getActivity().RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            handleImagePicked(selectedImageUri);
        }
    }
}
