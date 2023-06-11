package com.residencia.guardiantrackitt;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {
    private List<com.residencia.guardiantrackitt.Photo> photoList;

    public PhotoAdapter(List<com.residencia.guardiantrackitt.Photo> photoList) {
        this.photoList = photoList;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflar el diseño XML para cada elemento de foto
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        // Obtener la foto en la posición especificada
        com.residencia.guardiantrackitt.Photo photo = photoList.get(position);

        // Aquí puedes utilizar una biblioteca de carga de imágenes, como Glide o Picasso, para cargar la foto en el ImageView
        // Glide es una biblioteca popular para cargar imágenes:
        Glide.with(holder.itemView.getContext()).load(photo.getImageUrl()).into(holder.imageView);

        // También puedes agregar lógica adicional para interactuar con las fotos, como editar o eliminar
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lógica para editar o ver detalles de la foto
            }
        });
    }

    @Override
    public int getItemCount() {
        return photoList.size();
    }

    public static class PhotoViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;

        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            // Obtener referencia al ImageView en el diseño XML del elemento de foto
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}