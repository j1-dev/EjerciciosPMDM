package com.example.exud2.entidades;

import java.util.Date;

public class Evento {
  private int idEvento;
  private String nombre;
  private Date fecha;
  private int tipo;

  public Evento(){}

  public Evento(int idEvento, String nombre, Date fecha, int tipo) {
    this.idEvento = idEvento;
    this.nombre = nombre;
    this.fecha = fecha;
    this.tipo = tipo;
  }

  public int getIdEvento() {
    return idEvento;
  }

  public void setIdEvento(int idEvento) {
    this.idEvento = idEvento;
  }

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public Date getFecha() {
    return fecha;
  }

  public void setFecha(Date fecha) {
    this.fecha = fecha;
  }

  public int getTipo() {
    return tipo;
  }

  public void setTipo(int tipo) {
    this.tipo = tipo;
  }
}
