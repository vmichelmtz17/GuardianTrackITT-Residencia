package com.residencia.guardiantrackitt;

import androidx.fragment.app.FragmentManager;

import com.google.android.gms.maps.model.LatLng;
import com.residencia.guardiantrackitt.UbicacionFragment;

public class Ubicacion {
    private double latitude;
    private double longitude;
    public Ubicacion(){}

    public Ubicacion(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void mostrarUbicacionFragmentConUbicacionActual(LatLng ubicacionActual, FragmentManager fragmentManager, int fragmentContainerId) {
        UbicacionFragment ubicacionFragment = new UbicacionFragment();
        ubicacionFragment.setUbicacionActual(ubicacionActual);

        fragmentManager.beginTransaction()
                .replace(fragmentContainerId, ubicacionFragment) // fragmentContainerId es el ID del contenedor donde deseas mostrar el fragmento
                .commit();
    }
}
