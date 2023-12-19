package com.example.basemoviles;

import com.example.basemoviles.entidades.Alumno;
import com.example.basemoviles.entidades.Libro;
import com.example.basemoviles.repositorios.AlumnoRepositorio;
import com.example.basemoviles.repositorios.LibroRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.*;

@RestController
@RequestMapping("/libroapi")
public class LibroController {
    private final LibroRepositorio libroRepositorio;
    private final AlumnoRepositorio alumnoRepositorio;

    @Autowired
    public LibroController(LibroRepositorio libroRepositorio, AlumnoRepositorio alumnoRepositorio) {
        this.libroRepositorio = libroRepositorio;
        this.alumnoRepositorio = alumnoRepositorio;
    }

    @GetMapping("/listaLibros")
    public List<Libro> getLibros(){
        Iterable<Libro> iterar=libroRepositorio.findAll();
        Iterator<Libro> iterus=iterar.iterator();
        List<Libro> resultado=new ArrayList<>();
        while(iterus.hasNext()){
            resultado.add(iterus.next());
        }
        return resultado;
    }

    @GetMapping("/listaLibros{id}")
    public List<Libro> getLibrosById(@PathVariable("id") Long id){
      List<Libro> resultado = new ArrayList<>();
      Optional<Alumno> alumno = alumnoRepositorio.findById(id);
      System.out.println(alumno.toString());
      if(alumno.isPresent()){
        Alumno trueAlumno = alumno.get();
        Set<Libro> libros = trueAlumno.getLibros();
        resultado.addAll(libros);
      }
      return resultado;
    }

    @PostMapping("/nuevoLibro")
    public ResponseEntity<Long> nuevo(@RequestBody Map<String, String> res)
            throws URISyntaxException {
      String nombre = res.get("nombre");
      Long idAlumno = Long.parseLong(res.get("idAlumno"));
      Optional<Alumno> alumno = alumnoRepositorio.findById(idAlumno);
      if(alumno.isPresent()) {
        Libro libro = new Libro();
        libro.setNombre(nombre);
        libro.setAlumno(alumno.get());
        libroRepositorio.save(libro);
        return ResponseEntity.status(HttpStatus.CREATED).body(libro.getIdLibro());
      } else {
        return ResponseEntity.notFound().build();
      }
    }



    @GetMapping("/getLibro{id}")
    public ResponseEntity<Libro> getLibro(@PathVariable("id") Long id) {
        Optional<Libro> libroEncontrado = libroRepositorio.findById(id);
        if (libroEncontrado.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(libroEncontrado.get());
        }
    }

    @PutMapping("/actualizarLibro")
    public ResponseEntity<Long> modificar(@RequestBody Libro libro)
            throws URISyntaxException {
        Libro libroCreado = libroRepositorio.save(libro);
        return ResponseEntity.status(HttpStatus.OK).body(libroCreado.getIdLibro());
    }

    @DeleteMapping("/eliminarLibro{id}")
    public ResponseEntity<Object> eliminar(@PathVariable Long id) {
        Optional<Libro> libroEliminable = libroRepositorio.findById(id);
        if (libroEliminable.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            libroRepositorio.delete(libroEliminable.get());
            return ResponseEntity.noContent().build();
        }
    }
}
