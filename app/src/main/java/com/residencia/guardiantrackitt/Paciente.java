package com.residencia.guardiantrackitt;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.residencia.guardiantrackitt.databinding.ActivityPacienteBinding;

public class Paciente extends AppCompatActivity {

    private ActivityPacienteBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityPacienteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView bottomNavigation = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_photos, R.id.navigation_information)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_paciente);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_photos:
                        Intent intent = new Intent(Paciente.this, PacienteActivity.class);
                        startActivity(intent);
                        return true;
                    // Agrega casos para otros elementos del Bottom Navigation si los tienes
                }
                return false;
            }
        });
    }

}