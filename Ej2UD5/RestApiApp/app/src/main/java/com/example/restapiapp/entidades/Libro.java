package com.example.restapiapp.entidades;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;

public class Libro {
  private int idLibro;
  private String nombre;
  private int idAlumno;
  private Alumno alumno;

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

  public Alumno getAlumno() {
    return alumno;
  }

  public void setAlumno(Alumno alumno) {
    this.alumno = alumno;
  }

  public void fromJSON(JSONObject jsonObject) throws JSONException, ParseException {
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

    if (!jsonObject.isNull("alumno")) {
      JSONObject jsonAlumno = jsonObject.getJSONObject("alumno");
      Alumno parsedAlumno = new Alumno();
      parsedAlumno.fromJSON(jsonAlumno);
      this.alumno = parsedAlumno;
    }
  }

  public void fromJSONString(String string) throws JSONException, ParseException {
    JSONObject json = new JSONObject(string);
    fromJSON(json);
  }

  public JSONObject toJSON() throws JSONException {
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("idLibro", idLibro);
    jsonObject.put("nombre", nombre);
    jsonObject.put("idAlumno", idAlumno);
    jsonObject.put("alumno", alumno.toJSON());

    return jsonObject;
  }
}
