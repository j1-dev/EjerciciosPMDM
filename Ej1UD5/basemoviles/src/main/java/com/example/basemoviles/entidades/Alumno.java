package com.example.basemoviles.entidades;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Alumno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAlumno;
    private String nombre;
    private String apellidos;
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(nullable = true, columnDefinition = "date")
    private Date fechaNacimiento;
    @JsonBackReference
    @OneToMany(mappedBy = "alumno", cascade = CascadeType.ALL)
    private Set<Libro> libros= new HashSet<>();

    public Alumno() {
    }

    public Alumno(String nombre, String apellidos, Date fechaNacimiento) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.fechaNacimiento = fechaNacimiento;
    }

    public Long getIdAlumno() {
        return idAlumno;
    }

    public void setIdAlumno(Long idAlumno) {
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

    public Set<Libro> getLibros() {
        return libros;
    }

    public void setLibros(Set<Libro> libros) {
        this.libros = libros;
    }

  @Override
  public String toString() {
    return "Alumno{" +
        "idAlumno=" + idAlumno +
        ", nombre='" + nombre + '\'' +
        ", apellidos='" + apellidos + '\'' +
        ", fechaNacimiento=" + fechaNacimiento +
        ", libros=" + libros +
        '}';
  }
}
