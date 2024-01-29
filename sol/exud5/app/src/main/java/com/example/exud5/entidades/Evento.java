package com.example.exud5.entidades;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Evento {

    private long idEvento;
    private String nombre;
    private int tipo;
    private Date fecha;
    private String sFecha;

    public Evento(long idEvento, String nombre, int tipo, Date fecha, String sFecha) {
        this.idEvento = idEvento;
        this.nombre = nombre;
        this.tipo = tipo;
        this.fecha = fecha;
        this.sFecha = sFecha;
    }

    public Evento() {
    }

    public long getIdEvento() {
        return idEvento;
    }

    public void setIdEvento(long idEvento) {
        this.idEvento = idEvento;
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

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getsFecha() {
        return sFecha;
    }

    public void setsFecha(String sFecha) {
        this.sFecha = sFecha;
    }

    public void fromJSON(JSONObject fcjson) throws JSONException, ParseException {
        /*En el objeto JSON iremos comprobando que para cada par clave-valor el valor correspondiente
         * a la clave que estamos consultando no es nulo y en ese caso recuperamos el valor utilizando
         * el tipo de dato que corresponda*/
        if(!fcjson.isNull("idEvento")) {
            this.idEvento = fcjson.getLong("idEvento");
        }
        else{
            this.idEvento=-1;
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
        if(!fcjson.isNull("fechaString")) {
            this.sFecha = fcjson.getString("fechaString");
            if(!this.sFecha.isEmpty()) {
                SimpleDateFormat sfd = new SimpleDateFormat("yyyy-MM-dd");
                this.fecha = sfd.parse(this.sFecha);
            }
            else{
                this.fecha=null;
            }
        }
        else{
            this.sFecha="";
            this.fecha=null;
        }
    }

}
