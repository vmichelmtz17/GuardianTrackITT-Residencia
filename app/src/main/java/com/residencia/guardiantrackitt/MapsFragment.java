package com.residencia.guardiantrackitt;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private Marker homeMarker;
    private FusedLocationProviderClient fusedLocationClient;
    private LatLng currentLocation;
    private SharedPreferences sharedPreferences;

    private Button addHomeButton;
    private Button navigateButton;

    private static final String SHARED_PREFS_KEY = "com.residencia.guardiantrackitt.SHARED_PREFS";
    private static final String HOME_LATITUDE_KEY = "HOME_LATITUDE";
    private static final String HOME_LONGITUDE_KEY = "HOME_LONGITUDE";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);
        addHomeButton = view.findViewById(R.id.addHomeButton);
        navigateButton = view.findViewById(R.id.navigateButton);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        sharedPreferences = requireContext().getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE);

        addHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addHomeLocation();
            }
        });

        navigateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToHome();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        enableMyLocation();
        setCurrentLocation();
        loadHomeLocation();
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        }
    }

    private void setCurrentLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(requireActivity(), location -> {
                        if (location != null) {
                            currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f));
                        }
                    });
        }
    }

    private void addHomeLocation() {
        if (currentLocation != null) {
            if (homeMarker != null) {
                homeMarker.remove();
            }
            homeMarker = googleMap.addMarker(new MarkerOptions().position(currentLocation).title("Home"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f));
            saveHomeLocation(currentLocation);
        } else {
            Toast.makeText(requireContext(), "No se pudo obtener la ubicación actual", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToHome() {
        if (currentLocation != null && homeMarker != null) {
            String destination = homeMarker.getPosition().latitude + "," + homeMarker.getPosition().longitude;

            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(requireActivity(), location -> {
                            if (location != null) {
                                String origin = location.getLatitude() + "," + location.getLongitude();
                                String directionsUrl = "https://www.google.com/maps/dir/?api=1&origin=" + origin + "&destination=" + destination;
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(directionsUrl));
                                intent.setPackage("com.google.android.apps.maps");
                                startActivity(intent);
                            } else {
                                Toast.makeText(requireContext(), "No se pudo obtener la ubicación actual", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(requireActivity(), e -> {
                            Toast.makeText(requireContext(), "Error al obtener la ubicación actual", Toast.LENGTH_SHORT).show();
                        });
            } else {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        2);
            }
        } else {
            Toast.makeText(requireContext(), "No se pudo obtener la ubicación actual o agregar la ubicación del hogar", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveHomeLocation(LatLng homeLocation) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(HOME_LATITUDE_KEY, (float) homeLocation.latitude);
        editor.putFloat(HOME_LONGITUDE_KEY, (float) homeLocation.longitude);
        editor.apply();
    }

    private void loadHomeLocation() {
        if (sharedPreferences.contains(HOME_LATITUDE_KEY) && sharedPreferences.contains(HOME_LONGITUDE_KEY)) {
            float latitude = sharedPreferences.getFloat(HOME_LATITUDE_KEY, 0f);
            float longitude = sharedPreferences.getFloat(HOME_LONGITUDE_KEY, 0f);
            LatLng homeLocation = new LatLng(latitude, longitude);
            if (homeMarker != null) {
                homeMarker.remove();
            }
            homeMarker = googleMap.addMarker(new MarkerOptions().position(homeLocation).title("Home"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(homeLocation, 15f));
        }
    }

    private void editHomeLocation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Editar Hogar");
        final EditText passwordInput = new EditText(requireContext());
        passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(passwordInput);
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String password = passwordInput.getText().toString();
                if (password.equals("contrasena")) { // Reemplaza "contrasena" con la contraseña real
                    showEditHomeLocationDialog();
                } else {
                    Toast.makeText(requireContext(), "Contraseña incorrecta", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void showEditHomeLocationDialog() {
        // Aquí puedes implementar la lógica para permitir al usuario editar la ubicación del hogar
        // por ejemplo, mostrar un diálogo con un formulario para ingresar una nueva dirección
        // y actualizar la ubicación del hogar en SharedPreferences.
        // Puedes usar un Geocoder para obtener las coordenadas de la nueva dirección.

        // Ejemplo:
        /*
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Editar Hogar");

        final EditText addressInput = new EditText(requireContext());
        builder.setView(addressInput);

        builder.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String address = addressInput.getText().toString();
                LatLng newHomeLocation = geocodeAddress(address);
                if (newHomeLocation != null) {
                    saveHomeLocation(newHomeLocation);
                    if (homeMarker != null) {
                        homeMarker.remove();
                    }
                    homeMarker = googleMap.addMarker(new MarkerOptions().position(newHomeLocation).title("Home"));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newHomeLocation, 15f));
                    Toast.makeText(requireContext(), "Ubicación del hogar actualizada", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "No se pudo geocodificar la dirección", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
        */
    }

    private LatLng geocodeAddress(String address) {
        Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(address, 1);
            if (!addresses.isEmpty()) {
                Address firstAddress = addresses.get(0);
                double latitude = firstAddress.getLatitude();
                double longitude = firstAddress.getLongitude();
                return new LatLng(latitude, longitude);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}