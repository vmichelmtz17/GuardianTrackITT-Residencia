package com.residencia.guardiantrackitt;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Uri> imageUris;
    private ArrayList<String> imageNames;
    private List<String> opcionesParentezco;
    private SharedPreferences sharedPreferences;

    public ImageAdapter(Context context, ArrayList<Uri> imageUris, ArrayList<String> imageNames) {
        this.context = context;
        this.imageUris = imageUris;
        this.imageNames = imageNames;

        // Define las opciones de parentezco aquí
        opcionesParentezco = new ArrayList<>();
        opcionesParentezco.add("Papá");
        opcionesParentezco.add("Mamá");
        opcionesParentezco.add("Hijo");
        opcionesParentezco.add("Hija");
        opcionesParentezco.add("Hermano");
        opcionesParentezco.add("Hermana");
        // Agrega otras opciones según sea necesario

        // Inicializar SharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public int getCount() {
        return imageUris.size();
    }

    @Override
    public Object getItem(int position) {
        return imageUris.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.grid_item_image, parent, false);
        }

        ImageView imageView = convertView.findViewById(R.id.imageView);
        Spinner spinnerParentezco = convertView.findViewById(R.id.spinnerParentezco);

        Uri imageUri = imageUris.get(position);
        String imageName = imageNames.get(position);

        imageView.setImageURI(imageUri);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, opcionesParentezco);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerParentezco.setAdapter(spinnerAdapter);

        // Obtener el parentezco registrado en SharedPreferences para la imagen actual
        String parentezcoGuardado = sharedPreferences.getString(imageName, opcionesParentezco.get(0));
        int posicionParentezco = opcionesParentezco.indexOf(parentezcoGuardado);
        spinnerParentezco.setSelection(posicionParentezco);

        // Agregar un listener al spinner para guardar el parentezco seleccionado en SharedPreferences
        spinnerParentezco.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String parentezcoSeleccionado = opcionesParentezco.get(position);
                // Guardar el parentezco seleccionado en SharedPreferences para la imagen actual
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(imageName, parentezcoSeleccionado);
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Maneja el evento de que no se seleccionó nada si es necesario.
            }
        });

        return convertView;
    }
}