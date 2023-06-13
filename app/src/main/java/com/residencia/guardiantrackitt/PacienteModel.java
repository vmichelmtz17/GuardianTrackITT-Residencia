package com.residencia.guardiantrackitt;
public class PacienteModel {

    private String userId;
    private String nombre;
    private String fechaNacimiento;

    public PacienteModel() {
        // Constructor vacío requerido para Firebase
    }

    public PacienteModel(String userId, String nombre, String fechaNacimiento) {
        this.userId = userId;
        this.nombre = nombre;
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }
}
