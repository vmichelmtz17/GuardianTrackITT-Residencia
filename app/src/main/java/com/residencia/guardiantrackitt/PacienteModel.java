package com.residencia.guardiantrackitt;
public class PacienteModel {
    private String pacienteId;
    private String nombre;
    private String fechaNacimiento;

    public PacienteModel() {
        // Constructor vacío requerido para Firebase Realtime Database
    }

    public PacienteModel(String pacienteId, String nombre, String fechaNacimiento) {
        this.pacienteId = pacienteId;
        this.nombre = nombre;
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getPacienteId() {
        return pacienteId;
    }

    public void setPacienteId(String pacienteId) {
        this.pacienteId = pacienteId;
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
