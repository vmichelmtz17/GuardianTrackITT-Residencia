package com.residencia.guardiantrackitt;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.residencia.guardiantrackitt.databinding.ActivityMainBinding;
import com.residencia.guardiantrackitt.databinding.ActivityPacienteBinding;

public class Paciente extends AppCompatActivity {
    @androidx.annotation.NonNull ActivityPacienteBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPacienteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        remplaceFragment(new HomeFragment());

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.navigation_home:
                    remplaceFragment(new HomeFragment());
                    break;
                case R.id.navigation_photos:
                    remplaceFragment(new PhotosFragment());
                    break;
                case R.id.menu_informacion:
                    remplaceFragment(new InfoFragment());
                    break;
            }
            return true;
        });
    }
    private void remplaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_layout, fragment);
        transaction.commit();
    }
}
