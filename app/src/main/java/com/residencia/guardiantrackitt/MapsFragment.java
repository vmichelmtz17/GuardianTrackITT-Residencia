package com.residencia.guardiantrackitt;

import android.Manifest;
import com.google.firebase.auth.FirebaseAuth;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private Marker homeMarker;
    private FusedLocationProviderClient fusedLocationClient;
    private LatLng currentLocation;
    private SharedPreferences sharedPreferences;
    private DatabaseReference databaseReference;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    private Button addHomeButton;
    private Button navigateButton;

    private static final String SHARED_PREFS_KEY = "com.residencia.guardiantrackitt.SHARED_PREFS";
    private static final String HOME_LATITUDE_KEY = "HOME_LATITUDE";
    private static final String HOME_LONGITUDE_KEY = "HOME_LONGITUDE";
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);
        addHomeButton = view.findViewById(R.id.addHomeButton);
        navigateButton = view.findViewById(R.id.navigateButton);
        mAuth = FirebaseAuth.getInstance();
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
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

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

        // Cargamos la ubicación del hogar solo si existe una ubicación guardada
        if (sharedPreferences.contains(HOME_LATITUDE_KEY) && sharedPreferences.contains(HOME_LONGITUDE_KEY)) {
            loadHomeLocation();
        }
    }

    private void loadHomeLocation() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            DatabaseReference userLocationRef = databaseReference.child("userType")
                    .child("Familiar").child(uid).child("pacientes");

            userLocationRef.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                @Override
                public void onSuccess(DataSnapshot dataSnapshot) {
                    for (DataSnapshot pacienteSnapshot : dataSnapshot.getChildren()) {
                        DataSnapshot ubicacionActualSnapshot = pacienteSnapshot.child("ubicacion_actual");
                        Double latitude = ubicacionActualSnapshot.child("latitude").getValue(Double.class);
                        Double longitude = ubicacionActualSnapshot.child("longitude").getValue(Double.class);
                        if (latitude != null && longitude != null) {
                            LatLng homeLocation = new LatLng(latitude, longitude);
                            if (homeMarker != null) {
                                homeMarker.remove();
                            }
                            homeMarker = googleMap.addMarker(new MarkerOptions().position(homeLocation).title("Home"));
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(homeLocation, 15f));
                            break; // Mostrar solo la ubicación del primer paciente (puedes modificar esto si necesitas mostrar múltiples ubicaciones).
                        } else {
                            Toast.makeText(requireContext(), "Error: Datos de ubicación del hogar no disponibles", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(requireContext(), "Error al cargar la ubicación del hogar", Toast.LENGTH_SHORT).show();
                }
            });
        }
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
                            updateCurrentLocationInFirebase(currentLocation);
                            startLocationUpdates();
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

        // Enviar la ubicación actual a Firebase Realtime Database
        sendLocationToFirebase(homeLocation);
    }

    private void sendLocationToFirebase(LatLng location) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            DatabaseReference userLocationRef = databaseReference.child("userType")
                    .child("Familiar").child(uid).child("pacientes").child("ubicacion_actual");

            // Crear un objeto UbicacionActual para representar la ubicación actual
            UbicacionActual ubicacionActual = new UbicacionActual(location.latitude, location.longitude);

            // Enviar la ubicación actual a Firebase Realtime Database en la subruta del usuario actual
            userLocationRef.setValue(ubicacionActual)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(requireContext(), "Ubicación actual enviada a Firebase", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(requireContext(), "Error al enviar la ubicación a Firebase", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void updateCurrentLocationInFirebase(LatLng location) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            DatabaseReference userLocationRef = databaseReference.child("userType")
                    .child("Familiar").child(uid).child("pacientes").child("ubicacion_actual");

            // Crear un objeto UbicacionActual para representar la ubicación actual
            UbicacionActual ubicacionActual = new UbicacionActual(location.latitude, location.longitude);

            // Enviar la ubicación actual a Firebase Realtime Database en la subruta del usuario actual
            userLocationRef.setValue(ubicacionActual)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(requireContext(), "Ubicación actual enviada a Firebase", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(requireContext(), "Error al enviar la ubicación a Firebase", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void startLocationUpdates() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(60000); // Intervalo de actualización de ubicación en milisegundos (5 segundos)
        locationRequest.setFastestInterval(55000); // Intervalo de actualización más rápido en milisegundos (3 segundos)
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    Location lastLocation = locationResult.getLastLocation();
                    currentLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                    updateCurrentLocationInFirebase(currentLocation);
                }
            }
        };


        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        } else {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    3);
        }
    }

    private void stopLocationUpdates() {
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }
    private void startLocationUpdatesIfNotInUbicacionActivity() {
        if (!isUserInUbicacionActivity()) {
            startLocationUpdates();
        }
    }

    private void stopLocationUpdatesIfInUbicacionActivity() {
        if (isUserInUbicacionActivity()) {
            stopLocationUpdates();
        }
    }

    private boolean isUserInUbicacionActivity() {
        AppCompatActivity activity = (AppCompatActivity) requireContext();
        return activity instanceof Ubicacion;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isUserInHomeFamiliarActivity()) {
            startLocationUpdates();
        }
        startLocationUpdatesIfNotInUbicacionActivity();
    }

    private boolean isUserInHomeFamiliarActivity() {
        AppCompatActivity activity = (AppCompatActivity) requireContext();
        return activity instanceof Home_Familiar;
    }

    @Override
    public void onPause() {
        super.onPause();
        stopLocationUpdates();
        stopLocationUpdatesIfInUbicacionActivity();
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