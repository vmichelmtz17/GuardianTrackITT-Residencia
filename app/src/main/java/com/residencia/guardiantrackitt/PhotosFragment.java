package com.residencia.guardiantrackitt;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class PhotosFragment extends Fragment {

    private static final int PERMISSION_REQUEST_CODE = 2;
    private static final int REQUEST_IMAGE_PICK = 3;
    private static final String STATE_IMAGE_URIS = "imageUris";
    private static final String STATE_IMAGE_NAMES = "imageNames";

    private GridView gridView;
    private ImageAdapter imageAdapter;
    private ArrayList<Uri> imageUris;
    private ArrayList<String> imageNames;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photos, container, false);

        gridView = view.findViewById(R.id.gridView);
        imageUris = new ArrayList<>();
        imageNames = new ArrayList<>();
        imageAdapter = new ImageAdapter(requireContext(), imageUris, imageNames);
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
                imageNames = savedInstanceState.getStringArrayList(STATE_IMAGE_NAMES);
            } else {
                loadSavedImages();
            }
            imageAdapter.notifyDataSetChanged();
        }

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Obtener la URI y el nombre de la imagen seleccionada
                Uri imageUri = imageUris.get(position);
                String imageName = imageNames.get(position);

                // Mostrar el diálogo de opciones
                showImageOptionsDialog(imageUri, imageName);
            }
        });

        return view;
    }

    private void showImageOptionsDialog(Uri imageUri, String imageName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(imageName)
                .setItems(R.array.image_options, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                // Ver imagen en pantalla completa
                                showImageFullscreen(imageUri);
                                break;
                            case 1:
                                // Editar nombre de imagen
                                showEditImageNameDialog(imageUri, imageName);
                                break;
                            case 2:
                                // Eliminar imagen
                                showDeleteImageConfirmationDialog(imageUri);
                                break;
                        }
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void showImageFullscreen(Uri imageUri) {
        Intent intent = new Intent(requireContext(), FullScreenImageActivity.class);
        intent.putExtra("imageUri", imageUri.toString());
        startActivity(intent);
    }

    private void showEditImageNameDialog(Uri imageUri, String imageName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Editar Nombre de Imagen");

        // Crear un EditText para que el usuario ingrese el nuevo nombre de la imagen
        final EditText input = new EditText(requireContext());
        input.setText(imageName);
        builder.setView(input);

        builder.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newImageName = input.getText().toString().trim();
                if (!TextUtils.isEmpty(newImageName)) {
                    // Renombrar el archivo de la imagen en el almacenamiento interno
                    if (renameImageFile(imageUri, newImageName)) {
                        // Actualizar el nombre en la lista y notificar al adaptador
                        int position = imageUris.indexOf(imageUri);
                        if (position != -1) {
                            imageNames.set(position, newImageName);
                            imageAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Toast.makeText(requireContext(), "Error al renombrar la imagen", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireContext(), "Ingresa un nuevo nombre para la imagen", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void showDeleteImageConfirmationDialog(Uri imageUri) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Eliminar Imagen")
                .setMessage("¿Estás seguro de que deseas eliminar esta imagen?")
                .setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Eliminar el archivo de la imagen del almacenamiento interno
                        if (deleteImageFile(imageUri)) {
                            // Eliminar la URI y el nombre de la imagen de las listas y notificar al adaptador
                            int position = imageUris.indexOf(imageUri);
                            if (position != -1) {
                                imageUris.remove(position);
                                imageNames.remove(position);
                                imageAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Toast.makeText(requireContext(), "Error al eliminar la imagen", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private boolean renameImageFile(Uri imageUri, String newImageName) {
        String imagePath = imageUri.getPath();
        if (imagePath != null) {
            File imageFile = new File(imagePath);
            String newImagePath = imageFile.getParent() + File.separator + newImageName;
            File newImageFile = new File(newImagePath);
            return imageFile.renameTo(newImageFile);
        }
        return false;
    }

    private boolean deleteImageFile(Uri imageUri) {
        String imagePath = imageUri.getPath();
        if (imagePath != null) {
            File imageFile = new File(imagePath);
            return imageFile.delete();
        }
        return false;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(STATE_IMAGE_URIS, imageUris);
        outState.putStringArrayList(STATE_IMAGE_NAMES, imageNames);
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    private void handleImagePicked(Uri imageUri) {
        // Verificar si la URI ya existe en la lista
        if (!imageUris.contains(imageUri)) {
            // Guardar la URI de la imagen en el almacenamiento interno
            String imageName = generateImageName(); // Generar un nombre de imagen único
            String imagePath = saveImageToInternalStorage(imageUri, imageName);
            if (imagePath != null) {
                imageUris.add(Uri.parse(imagePath));
                imageNames.add(imageName);
                imageAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(requireContext(), "Error al guardar la imagen", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String generateImageName() {
        // Generar un nombre de imagen único, puedes implementar tu lógica personalizada aquí
        // Por ejemplo, puedes utilizar la fecha y hora actual para crear el nombre
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        String timestamp = dateFormat.format(new Date());
        return "Image_" + timestamp + ".jpg";
    }

    private String saveImageToInternalStorage(Uri imageUri, String imageName) {
        try {
            // Obtener el directorio de almacenamiento interno de la aplicación
            File storageDir = requireContext().getFilesDir();

            // Crear un archivo temporal en el directorio de almacenamiento interno
            File imageFile = new File(storageDir, imageName);

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

        // Recorrer la lista de archivos y obtener las URIs y nombres de las imágenes guardadas
        imageUris.clear(); // Limpiar la lista antes de cargar las imágenes
        imageNames.clear(); // Limpiar la lista de nombres de imágenes
        if (imageFiles != null) {
            for (File imageFile : imageFiles) {
                imageUris.add(Uri.fromFile(imageFile));
                imageNames.add(imageFile.getName());
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
