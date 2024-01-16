package com.example.basemoviles.entidades;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Persona {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long idPersona;
  private String nombre;
  private String apellidos;
  private String dni;

  public Persona() {
  }

  public Persona(String nombre, String apellidos, String dni) {
    this.nombre = nombre;
    this.apellidos = apellidos;
    this.dni = dni;
  }

  public Long getIdPersona() {
    return idPersona;
  }

  public void setIdPersona(Long idPersona) {
    this.idPersona = idPersona;
  }

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public String getApellidos() {
    return apellidos;
  }

  public void setApellidos(String apellidos) {
    this.apellidos = apellidos;
  }

  public String getDni() {
    return dni;
  }

  public void setDni(String dni) {
    this.dni = dni;
  }

  @Override
  public String toString() {
    return "Persona{" +
        "idPersona=" + idPersona +
        ", nombre='" + nombre + '\'' +
        ", apellidos='" + apellidos + '\'' +
        ", dni='" + dni + '\'' +
        '}';
  }
}
