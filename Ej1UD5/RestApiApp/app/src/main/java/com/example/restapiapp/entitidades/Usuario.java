package com.example.restapiapp.entitidades;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Usuario {

    private long idUsuario;
    private String nombre;
    private String apellidos;
    private Date fechaNacimiento;
    private String dni;
    private String username;
    private String password;
    private String sFecha;

    public Usuario() {

    }

    public Usuario(long idUsuario, String nombre, String apellidos, Date fechaNacimiento,
                   String dni, String username, String password, String sFecha) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.fechaNacimiento = fechaNacimiento;
        this.dni = dni;
        this.username = username;
        this.password = password;
        this.sFecha = sFecha;
    }

    public long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(long idUsuario) {
        this.idUsuario = idUsuario;
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

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
        if(!fcjson.isNull("idUsuario")) {
            this.idUsuario = fcjson.getLong("idUsuario");
        }
        else{
            this.idUsuario=-1;
        }
        if(!fcjson.isNull("nombre")) {
            this.nombre = fcjson.getString("nombre");
        }
        else{
            this.nombre="";
        }
        if(!fcjson.isNull("apellidos")) {
            this.apellidos = fcjson.getString("apellidos");
        }
        else{
            this.apellidos="";
        }
        if(!fcjson.isNull("username")) {
            this.username = fcjson.getString("username");
        }
        else{
            this.username="";
        }
        if(!fcjson.isNull("dni")) {
            this.dni = fcjson.getString("dni");
        }
        else{
            this.dni="";
        }
        if(!fcjson.isNull("password")) {
            this.password = fcjson.getString("password");
        }
        else{
            this.password="";
        }
        if(!fcjson.isNull("fechaNacimientoString")) {
            this.sFecha = fcjson.getString("fechaNacimientoString");
            if(!this.sFecha.isEmpty()) {
                SimpleDateFormat sfd = new SimpleDateFormat("yyyy-MM-dd");
                this.fechaNacimiento = sfd.parse(this.sFecha);
            }
            else{
                this.fechaNacimiento=null;
            }
        }
        else{
            this.sFecha="";
            this.fechaNacimiento=null;
        }
    }

}
