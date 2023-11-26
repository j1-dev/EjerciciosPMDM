package com.example.ej2ud4.entidades;

public class Libro {
  private int idLibro;
  private String nombre;
  private int idAlumno;

  public Libro(){

  }

  public Libro(int idLibro, String nombre, int idAlumno) {
    this.idLibro = idLibro;
    this.nombre = nombre;
    this.idAlumno = idAlumno;
  }

  public int getIdLibro() {
    return idLibro;
  }

  public void setIdLibro(int idLibro) {
    this.idLibro = idLibro;
  }

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public int getIdAlumno() {
    return idAlumno;
  }

  public void setIdAlumno(int idAlumno) {
    this.idAlumno = idAlumno;
  }
}
