package com.example.basemoviles.entidades;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;

import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUsuario;
    private String username;
    private String password;
    private String nombre;
    private String apellidos;
    private String dni;
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(nullable = true)
    private Date fechaNacimiento;

    public Usuario() {

    }

    public Usuario(String username, String password, String nombre, String apellidos, String dni, Date fechaNacimiento) {
        this.username = username;
        this.password = password;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.dni = dni;
        this.fechaNacimiento = fechaNacimiento;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
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

    /*Cuando Spring Boot vaya a transformar nuestra entidad en formato JSON para enviarla al dispositivo móvil, vamos
    * a ignorar el getter por defecto de la entidad usando la anotación JsonIgnore y vamos a utilizar en su lugar un
    * método programado por nosotros e indicaremos que se utilice ese método usando la anotación JsonInclude*/
    @JsonIgnore
    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    @JsonInclude
    public String getFechaNacimientoString(){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        if(fechaNacimiento==null){
            return "";
        }
        else {
            return sdf.format(fechaNacimiento);
        }
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }
}
