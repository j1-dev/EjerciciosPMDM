package com.example.restapiapp.entidades;

import org.json.JSONException;
import org.json.JSONObject;

public class Persona {
  private Long idPersona;
  private String nombre;
  private String apellidos;
  private String dni;

  public Persona() {}

  public Persona(Long idPersona, String nombre, String apellidos, String dni) {
    this.idPersona = idPersona;
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

  public void fromJSON(JSONObject jsonObject) throws JSONException {
    if (!jsonObject.isNull("idPersona")) {
      this.idPersona = jsonObject.getLong("idPersona");
    } else {
      this.idPersona = -1L;
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
    if (!jsonObject.isNull("dni")) {
      this.dni = jsonObject.getString("dni");
    } else {
      this.dni = "";
    }
  }

  public void fromJSONString(String string) throws JSONException {
    JSONObject json = new JSONObject(string);
    fromJSON(json);
  }

  public JSONObject toJSON() throws JSONException {
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("idPersona", idPersona);
    jsonObject.put("nombre", nombre);
    jsonObject.put("apellidos", apellidos);
    jsonObject.put("dni", dni);
    return jsonObject;
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
