package com.residencia.guardiantrackitt;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ExpandableListAdapter;

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
        integrantes.add("Integrante 1");
        integrantes.add("Integrante 2");
        integrantes.add("Integrante 3");

        List<String> proyecto = new ArrayList<>();
        proyecto.add("Descripción del proyecto");

        List<String> instituto = new ArrayList<>();
        instituto.add("Nombre del instituto");
        instituto.add("Dirección del instituto");

        List<String> contacto = new ArrayList<>();
        contacto.add("Correo electrónico");
        contacto.add("Teléfono");

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
            listItemTextView.setText(item);
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
}