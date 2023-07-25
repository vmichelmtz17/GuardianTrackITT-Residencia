package com.residencia.guardiantrackitt;

public class UbicacionActual {
    public double latitude;
    public double longitude;

    public UbicacionActual() {
        // Constructor vacío requerido para Firebase
    }

    public UbicacionActual(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}

