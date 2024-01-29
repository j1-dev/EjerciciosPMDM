package com.example.exud5.entidades;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;

public class Disco {
  private long idDisco;
  private String nombre;
  private int tipo;
  private boolean escuchado;

  public Disco() {}

  public Disco(String nombre, int tipo, boolean escuchado) {
    this.nombre = nombre;
    this.tipo = tipo;
    this.escuchado = escuchado;
  }

  public long getIdDisco() {
    return idDisco;
  }

  public void setIdDisco(long idDisco) {
    this.idDisco = idDisco;
  }

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public int getTipo() {
    return tipo;
  }

  public void setTipo(int tipo) {
    this.tipo = tipo;
  }

  public boolean isEscuchado() {
    return escuchado;
  }

  public void setEscuchado(boolean escuchado) {
    this.escuchado = escuchado;
  }

  public void fromJSON(JSONObject fcjson) throws JSONException, ParseException {
    /*En el objeto JSON iremos comprobando que para cada par clave-valor el valor correspondiente
     * a la clave que estamos consultando no es nulo y en ese caso recuperamos el valor utilizando
     * el tipo de dato que corresponda*/
    if(!fcjson.isNull("idDisco")) {
      this.idDisco = fcjson.getLong("idDisco");
    }
    else{
      this.idDisco=-1;
    }
    if(!fcjson.isNull("nombre")) {
      this.nombre = fcjson.getString("nombre");
    }
    else{
      this.nombre="";
    }
    if(!fcjson.isNull("tipo")) {
      this.tipo = fcjson.getInt("tipo");
    }
    else{
      this.tipo=-1;
    }
    if(!fcjson.isNull("escuchado")) {
      this.escuchado = fcjson.getBoolean("escuchado");
    } else {
      this.escuchado = false;
    }
  }
}
