package com.residencia.guardiantrackitt;

public class ContactoModel {
    private String contactoId;
    private String nombre;
    private String numero;

    public ContactoModel() {
        // Constructor vacío requerido para Firebase
    }

    public ContactoModel(String contactoId, String nombre, String numero) {
        this.contactoId = contactoId;
        this.nombre = nombre;
        this.numero = numero;
    }

    public String getContactoId() {
        return contactoId;
    }

    public void setContactoId(String contactoId) {
        this.contactoId = contactoId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }
}