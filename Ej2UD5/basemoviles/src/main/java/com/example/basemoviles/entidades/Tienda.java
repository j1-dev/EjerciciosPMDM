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
public class Tienda {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long idTienda;
  private String nombre;
  private String direccion;
  private double latitud;
  private double longitud;

  public Tienda() {}

  public Tienda(Long idTienda, String nombre, String direccion, double latitud, double longitud) {
    this.idTienda = idTienda;
    this.nombre = nombre;
    this.direccion = direccion;
    this.latitud = latitud;
    this.longitud = longitud;
  }

  public Long getIdTienda() {
    return idTienda;
  }

  public void setIdTienda(Long idTienda) {
    this.idTienda = idTienda;
  }

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public String getDireccion() {
    return direccion;
  }

  public void setDireccion(String direccion) {
    this.direccion = direccion;
  }

  public double getLatitud() {
    return latitud;
  }

  public void setLatitud(double latitud) {
    this.latitud = latitud;
  }

  public double getLongitud() {
    return longitud;
  }

  public void setLongitud(double longitud) {
    this.longitud = longitud;
  }

  @Override
  public String toString() {
    return "Tienda{" +
        "idTienda=" + idTienda +
        ", nombre='" + nombre + '\'' +
        ", direccion='" + direccion + '\'' +
        ", latitud=" + latitud +
        ", longitud=" + longitud +
        '}';
  }
}