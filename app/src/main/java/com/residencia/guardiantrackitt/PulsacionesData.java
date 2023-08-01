package com.residencia.guardiantrackitt;

import java.util.ArrayList;
import java.util.List;

// PulsacionesData.java
public class PulsacionesData {
    private static PulsacionesData instance;
    private List<String> listaPulsaciones;

    private PulsacionesData() {
        listaPulsaciones = new ArrayList<>();
    }

    public static synchronized PulsacionesData getInstance() {
        if (instance == null) {
            instance = new PulsacionesData();
        }
        return instance;
    }

    public List<String> getListaPulsaciones() {
        return listaPulsaciones;
    }

    public void agregarPulsacion(String pulsacion) {
        listaPulsaciones.add(pulsacion);
    }
}
