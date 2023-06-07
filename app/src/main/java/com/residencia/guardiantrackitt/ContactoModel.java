package com.residencia.guardiantrackitt;

public class ContactoModel {
    private String id;
    private String nombre;
    private String numero;

    public ContactoModel() {
        // Constructor vacío requerido para Firebase
    }

    public ContactoModel(String id, String nombre, String numero) {
        this.id = id;
        this.nombre = nombre;
        this.numero = numero;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getNumero() {
        return numero;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }
}
