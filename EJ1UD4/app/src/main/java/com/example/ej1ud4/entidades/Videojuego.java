package com.example.ej1ud4.entidades;

import java.util.Date;

public class Videojuego {
  private int idVideojuego;
  private String nombre;
  private Date fechaSalida;
  private int genero;
  private int consola;
  private boolean seHaJugado;

  public Videojuego(){}

  public Videojuego(int idVideojuego, String nombre, Date fechaSalida, int genero, int consola, boolean seHaJugado) {
    this.idVideojuego = idVideojuego;
    this.nombre = nombre;
    this.fechaSalida = fechaSalida;
    this.genero = genero;
    this.consola = consola;
    this.seHaJugado = seHaJugado;
  }

  public int getIdVideojuego() {
    return idVideojuego;
  }

  public void setIdVideojuego(int idVideojuego) {
    this.idVideojuego = idVideojuego;
  }

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public Date getFechaSalida() {
    return fechaSalida;
  }

  public void setFechaSalida(Date fechaSalida) {
    this.fechaSalida = fechaSalida;
  }

  public int getGenero() {
    return genero;
  }

  public void setGenero(int genero) {
    this.genero = genero;
  }

  public int getConsola() {
    return consola;
  }

  public void setConsola(int consola) {
    this.consola = consola;
  }

  public boolean isSeHaJugado() {
    return seHaJugado;
  }

  public void setSeHaJugado(boolean seHaJugado) {
    this.seHaJugado = seHaJugado;
  }
}
