package com.example.restapiapp.entidades;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Alumno {

  private int idAlumno;
  private String nombre;
  private String apellidos;
  private Date fechaNacimiento;

  public Alumno() {

  }

  public Alumno(int idAlumno, String nombre, String apellidos, Date fechaNacimiento) {
    this.idAlumno = idAlumno;
    this.nombre = nombre;
    this.apellidos = apellidos;
    this.fechaNacimiento = fechaNacimiento;
  }

  public int getIdAlumno() {
    return idAlumno;
  }

  public void setIdAlumno(int idAlumno) {
    this.idAlumno = idAlumno;
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

  public Date getFechaNacimiento() {
    return fechaNacimiento;
  }

  public void setFechaNacimiento(Date fechaNacimiento) {
    this.fechaNacimiento = fechaNacimiento;
  }

  public void fromJSON(JSONObject jsonObject) throws JSONException, ParseException {
    if (!jsonObject.isNull("idAlumno")) {
      this.idAlumno = jsonObject.getInt("idAlumno");
    } else {
      this.idAlumno = -1;
    }
    if (!jsonObject.isNull("nombre")) {
      this.nombre = jsonObject.getString("nombre");
    } else {
      this.nombre = "";
    }
    if (!jsonObject.isNull("apellidos")) {
      this.apellidos = jsonObject.getString("apellidos");
    } else {
      this.apellidos = "";
    }
    if (!jsonObject.isNull("fechaNacimiento")) {
      String sFecha = jsonObject.getString("fechaNacimiento");
      if (!sFecha.isEmpty()) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        this.fechaNacimiento = sdf.parse(sFecha);
      } else {
        this.fechaNacimiento = null;
      }
    } else {
      this.fechaNacimiento = null;
    }
  }

  public void fromJSONString(String string) throws JSONException, ParseException {
    JSONObject json = new JSONObject(string);
    fromJSON(json);
  }

  public JSONObject toJSON() throws JSONException {
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("idAlumno", idAlumno);
    jsonObject.put("nombre", nombre);
    jsonObject.put("apellidos", apellidos);

    if (fechaNacimiento != null) {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
      String formattedDate = sdf.format(fechaNacimiento);
      jsonObject.put("fechaNacimiento", formattedDate);
    } else {
      jsonObject.put("fechaNacimiento", JSONObject.NULL);
    }

    return jsonObject;
  }

  @Override
  public String toString() {
    return "Alumno{" +
        "idAlumno=" + idAlumno +
        ", nombre='" + nombre + '\'' +
        ", apellidos='" + apellidos + '\'' +
        ", fechaNacimiento=" + fechaNacimiento +
        '}';
  }
}
