package com.example.restapiapp.entidades;

import org.json.JSONException;
import org.json.JSONObject;

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

  public void fromJSON(JSONObject jsonObject) throws JSONException {
    if (!jsonObject.isNull("idLibro")) {
      this.idLibro = jsonObject.getInt("idLibro");
    } else {
      this.idLibro = -1;
    }
    if (!jsonObject.isNull("nombre")) {
      this.nombre = jsonObject.getString("nombre");
    } else {
      this.nombre = "";
    }
    if (!jsonObject.isNull("idAlumno")) {
      this.idAlumno = jsonObject.getInt("idAlumno");
    } else {
      this.idAlumno = -1;
    }
  }
}
