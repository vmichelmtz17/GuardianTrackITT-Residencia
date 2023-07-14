package com.residencia.guardiantrackitt;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class About extends AppCompatActivity {

    private ExpandableListView expandableListView;
    private ExpandableListAdapter expandableListAdapter;
    private List<String> expandableListTitle;
    private Map<String, List<String>> expandableListDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // Inicializar datos para el ExpandableListView
        expandableListView = findViewById(R.id.expandableListView);
        expandableListDetail = getData();
        expandableListTitle = new ArrayList<>(expandableListDetail.keySet());

        // Crear el adaptador personalizado
        expandableListAdapter = new CustomExpandableListAdapter(this, expandableListTitle, expandableListDetail);

        // Establecer el adaptador en el ExpandableListView
        expandableListView.setAdapter(expandableListAdapter);
    }

    private Map<String, List<String>> getData() {
        Map<String, List<String>> expandableListDetail = new HashMap<>();

        // Agregar los títulos y los elementos de cada sección
        List<String> integrantes = new ArrayList<>();
        integrantes.add("Alan Enrique García Cuestar");
        integrantes.add("Marco Polo Lozano Álvarez");
        integrantes.add("Vicente Michel Martínez Portela");

        List<String> proyecto = new ArrayList<>();
        proyecto.add("El proyecto tiene como finalidad desarrollar una aplicación móvil y web para ayudar a personas que padecen Alzheimer ayudándoles a mantener su ubicación activa, para que le permita a la familia y a los cuidadores de la persona con alzheimer localizarla en caso de que se extravíe, también contará con un dispositivo oxímetro que detectará el ritmo cardiaco y, basándose en el resultado, saber si necesita ayuda antes del extravío.");

        List<String> instituto = new ArrayList<>();
        instituto.add("Instituto Tecnologico de Tijuana");
        instituto.add("Calz del Tecnológico 12950, Tomas Aquino, 22414 Tijuana, B.C.");
        instituto.add("Ir al Instituto en Maps");

        // Obtén la dirección como un String
        String direccion = instituto.get(1);

        List<String> contacto = new ArrayList<>();
        contacto.add("guardiantrack2023@gmail.com");

        expandableListDetail.put("Integrantes", integrantes);
        expandableListDetail.put("Proyecto", proyecto);
        expandableListDetail.put("Instituto", instituto);
        expandableListDetail.put("Contacto", contacto);

        return expandableListDetail;
    }

    private static class CustomExpandableListAdapter extends BaseExpandableListAdapter {

        private Context context;
        private List<String> expandableListTitle;
        private Map<String, List<String>> expandableListDetail;

        CustomExpandableListAdapter(Context context, List<String> expandableListTitle, Map<String, List<String>> expandableListDetail) {
            this.context = context;
            this.expandableListTitle = expandableListTitle;
            this.expandableListDetail = expandableListDetail;
        }

        @Override
        public int getGroupCount() {
            return expandableListTitle.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return expandableListDetail.get(expandableListTitle.get(groupPosition)).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return expandableListTitle.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return expandableListDetail.get(expandableListTitle.get(groupPosition)).get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            String title = (String) getGroup(groupPosition);
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.list_group, null);
            }
            TextView listTitleTextView = convertView.findViewById(R.id.listTitle);
            listTitleTextView.setText(title);
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            String item = (String) getChild(groupPosition, childPosition);
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.list_item, null);
            }

            TextView listItemTextView = convertView.findViewById(R.id.listItem);
            if (item.equals("Instituto Tecnologico de Tijuana")) {
                listItemTextView.setText("Ir al Instituto en Maps");
            } else if (item.equals("guardiantrack2023@gmail.com")) {
                listItemTextView.setText("Enviar correo a guardiantrack2023@gmail.com");
            } else {
                listItemTextView.setText(item);
            }

            listItemTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (item.equals("Ir al Instituto en Maps")) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://goo.gl/maps/vmvyTPzv8oT3kaHN9"));
                        context.startActivity(intent);
                    } else if (item.equals("Enviar correo a guardiantrack2023@gmail.com")) {
                        Intent intent = new Intent(Intent.ACTION_SENDTO);
                        intent.setData(Uri.parse("mailto:guardiantrack2023@gmail.com"));
                        context.startActivity(Intent.createChooser(intent, "Enviar correo"));
                    } else if (item.startsWith("http")) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(item));
                        context.startActivity(intent);
                    }
                }
            });

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
}