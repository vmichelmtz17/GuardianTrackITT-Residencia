package com.residencia.guardiantrackitt;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

public class Home_Familiar extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private TextView userNameTextView;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private FirebaseUser mCurrentUser;
    private ListenerRegistration mUserListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_familiar);

        // Configurar el ActionBar y el NavigationDrawer
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);

        // Configurar el ActionBarDrawerToggle para abrir y cerrar el NavigationDrawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Obtener referencias a las vistas
        userNameTextView = navigationView.getHeaderView(0).findViewById(R.id.userNameTextView);

        // Obtener instancia de FirebaseAuth y FirebaseFirestore
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        // Obtener el usuario actual
        mCurrentUser = mAuth.getCurrentUser();

        // Verificar si el usuario actual no es nulo
        if (mCurrentUser != null) {
            // Obtener el ID del usuario actual
            String userId = mCurrentUser.getUid();

            // Escuchar cambios en el documento del usuario en Firestore
            mUserListener = mFirestore.collection("users")
                    .document(userId)
                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                // Manejar el error
                                return;
                            }

                            // Verificar si el documento existe y contiene datos
                            if (documentSnapshot != null && documentSnapshot.exists()) {
                                // Obtener el nombre del usuario del documento
                                String userName = documentSnapshot.getString("name");

                                // Mostrar el nombre del usuario en el TextView correspondiente
                                userNameTextView.setText(userName);
                            }
                        }
                    });
        }

        // Configurar el clic de los botones en el contenido principal
        Button buttonUbicacion = findViewById(R.id.buttonUbicacion);
        Button buttonPaginaWeb = findViewById(R.id.buttonPaginaWeb);
        Button buttonContacto = findViewById(R.id.buttonContacto);
        Button buttonPulsaciones = findViewById(R.id.buttonPulsaciones);

        buttonUbicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home_Familiar.this, Ubicacion.class);
                startActivity(intent);
            }
        });

        buttonPaginaWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://alzheimertijuana.netlify.app/";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

        buttonContacto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home_Familiar.this, Contacto.class);
                startActivity(intent);
            }
        });

        buttonPulsaciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home_Familiar.this, Pulsaciones.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Maneja los clics en los elementos del menú del NavigationDrawer
        switch (item.getItemId()) {
            case R.id.menu_profile:
                Intent profileIntent = new Intent(Home_Familiar.this, Perfil.class);
                startActivity(profileIntent);
                return true;
            case R.id.menu_logout:
                // Cerrar sesión y redirigir a la actividad de inicio de sesión
                FirebaseAuth.getInstance().signOut();
                Intent logoutIntent = new Intent(Home_Familiar.this, MainActivity.class);
                startActivity(logoutIntent);
                finish(); // Cierra la actividad actual
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Maneja los clics en el botón de menú de la ActionBar
        if (item.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // Cierra el NavigationDrawer si está abierto cuando se presiona el botón Atrás
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Detener la escucha de cambios en el documento del usuario en Firestore
        if (mUserListener != null) {
            mUserListener.remove();
        }
    }
}
