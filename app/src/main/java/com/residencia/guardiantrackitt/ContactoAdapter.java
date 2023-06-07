package com.residencia.guardiantrackitt;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ContactoAdapter extends ArrayAdapter<ContactoModel> {

    private LayoutInflater inflater;
    private int resource;

    public ContactoAdapter(Context context, int resource) {
        super(context, resource);
        this.inflater = LayoutInflater.from(context);
        this.resource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(resource, parent, false);

            holder = new ViewHolder();
            holder.textViewNombre = convertView.findViewById(R.id.textViewNombre);
            holder.textViewNumero = convertView.findViewById(R.id.textViewNumero);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ContactoModel contacto = getItem(position);

        if (contacto != null) {
            holder.textViewNombre.setText(contacto.getNombre());
            holder.textViewNumero.setText(contacto.getNumero());
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView textViewNombre;
        TextView textViewNumero;
    }
}