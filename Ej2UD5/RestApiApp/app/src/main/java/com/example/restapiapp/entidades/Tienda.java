package com.example.restapiapp.entidades;

import org.json.JSONException;
import org.json.JSONObject;

public class Tienda {
  private Long idTienda;
  private String nombre;
  private String direccion;
  private double latitud;
  private double longitud;
  private double distancia;

  public Tienda() {}

  public Tienda(Long idTienda, String nombre, String direccion, double latitud, double longitud, double distancia) {
    this.idTienda = idTienda;
    this.nombre = nombre;
    this.direccion = direccion;
    this.latitud = latitud;
    this.longitud = longitud;
    this.distancia = distancia;
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

  public double getDistancia() {
    return distancia;
  }

  public void setDistancia(double distancia) {
    this.distancia = distancia;
  }

  public void fromJSON(JSONObject jsonObject) throws JSONException {
    if (!jsonObject.isNull("idTienda")) {
      this.idTienda = jsonObject.getLong("idTienda");
    } else {
      this.idTienda = -1L;
    }
    if (!jsonObject.isNull("nombre")) {
      this.nombre = jsonObject.getString("nombre");
    } else {
      this.nombre = "";
    }
    if (!jsonObject.isNull("direccion")) {
      this.direccion = jsonObject.getString("direccion");
    } else {
      this.direccion = "";
    }
    if (!jsonObject.isNull("latitud")) {
      this.latitud = jsonObject.getDouble("latitud");
    } else {
      this.latitud = 0.0;
    }
    if (!jsonObject.isNull("longitud")) {
      this.longitud = jsonObject.getDouble("longitud");
    } else {
      this.longitud = 0.0;
    }
    if (!jsonObject.isNull("distancia")) {
      this.distancia = jsonObject.getDouble("distancia");
    } else {
      this.distancia = 0.0;
    }
  }

  public void fromJSONString(String string) throws JSONException {
    JSONObject json = new JSONObject(string);
    fromJSON(json);
  }

  public JSONObject toJSON() throws JSONException {
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("idTienda", idTienda);
    jsonObject.put("nombre", nombre);
    jsonObject.put("direccion", direccion);
    jsonObject.put("latitud", latitud);
    jsonObject.put("longitud", longitud);
    jsonObject.put("distancia", distancia);
    return jsonObject;
  }

  @Override
  public String toString() {
    return "Tienda{" +
        "idTienda=" + idTienda +
        ", nombre='" + nombre + '\'' +
        ", direccion='" + direccion + '\'' +
        ", latitud=" + latitud +
        ", longitud=" + longitud +
        ", distancia=" + distancia +
        '}';
  }
}
