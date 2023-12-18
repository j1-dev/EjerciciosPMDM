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
    private final LibroRepositorio libroRepositorio;//Aquí vamos a referenciar nuestro repositorio
    private final AlumnoRepositorio alumnoRepositorio;

    @Autowired//Para tener una instancia de nuestro repositorio usamos la anotación autowired
    public LibroController(LibroRepositorio libroRepositorio, AlumnoRepositorio alumnoRepositorio) {
        this.libroRepositorio = libroRepositorio;
        this.alumnoRepositorio = alumnoRepositorio;
    }

    /*Con GetMapping estamos indicando que el tipo de petición HTTP o HTTPS debe ser GET y también indicamos qué
     * ruta dentro de la url (listaUsuarios) debemos usar para acceder a este método en el controlador*/
    @GetMapping("/listaLibros")
    public List<Libro> getLibros(){
        /*Con un findAll accedemos a todos los usuarios que haya en la base de datos. La lista de usuarios devuelta
         * será transformada a formato JSON automáticamente por SpringBoot usando las reglas que hayamos especificado
         * en el application.properties y en la entidad que corresponda, en este caso Usuario*/
        Iterable<Libro> iterar=libroRepositorio.findAll();
        Iterator<Libro> iterus=iterar.iterator();
        List<Libro> resultado=new ArrayList<>();
        while(iterus.hasNext()){
            resultado.add(iterus.next());
        }
        return resultado;
    }

    /*Con PostMapping estamos indicando que el tipo de petición HTTP o HTTPS debe ser POST y también indicamos qué
     * ruta dentro de la url (nuevoUsuario) debemos usar para acceder a este método en el controlador*/
    @PostMapping("/nuevoLibro")
    /*En este caso el método va a devolver al cliente una entidad de tipo Long (la clave primaria del nuevo usuario
    creado en la base de datos). Además estamos incluyendo como parámetro de este método un objeto de la clase Usuario
    escrito en formato JSON (esto lo estamos indicando con la anotación RequestBody). Desde el cliente controlaremos
    que hacemos una petición de tipo POST y que en el RequestBody incluiremos el usuario en formato JSON*/
    public ResponseEntity<Long> nuevo(@RequestBody Libro libro)
            throws URISyntaxException {
        Libro libroCreado = libroRepositorio.save(libro);
        if (libroCreado == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(libroCreado.getIdLibro());
    }


    /*En este GetMapping vamos a incluir en la url una variable que será la clave primaria del usuario que queremos
     * obtener. En la url la ruta que usaremos para accerder a este método del controlador será getUsuario y entre
     * llaves indicaremos el nombre de la variable que posteriormente usaremos como parámetro*/
    @GetMapping("/getLibro{id}")
    /*En este caso el método va a devolver al cliente una entidad de tipo Usuario. Además indicamos con la anotación
     * PathVariable que la url va a incluir un parámetro cuyo nombre será "id"*/
    public ResponseEntity<Libro> getLibro(@PathVariable("id") Long id) {
        //Obtenemos un único usuario usando el método findById del repositorio que ya conocemos
        Optional<Libro> libroEncontrado = libroRepositorio.findById(id);
        if (libroEncontrado.isEmpty()) {/*Si no se ha encontrado al usuario con la clave primaria correspondiente
        devolveremos un "not found"*/
            return ResponseEntity.notFound().build();
        } else {/*Si se ha encontrado al usuario devolveremos en HTTP OK y el usuario correspondiente en formato JSON
        (esto lo hace SpringBoot automáticamente)*/
            return ResponseEntity.ok(libroEncontrado.get());
        }
    }

    /*Este método es muy parecido al de crear nuevo usuario. La primera diferencia es que usamos una petición HTTP
     * o HTTPS PUT en lugar de POST. La segunda diferencia es que la ruta de la url cambia a "actualizarUsuario". El
     * resto del método es igual (mismos ResponseEntity, RequestBody y método del repositorio de la base de datos).*/
    @PutMapping("/actualizarLibro")
    public ResponseEntity<Long> modificar(@RequestBody Libro libro)
            throws URISyntaxException {
        Libro libroCreado = libroRepositorio.save(libro);
        if (libroCreado == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(libroCreado.getIdLibro());
        }
    }

    /*El método que usaremos para eliminar un usuario deberá utilizar la petición DELETE de HTTP o HTTPS. Icluirá un
     * parámetro con la clave primaria del usuario que queremos eliminar, como ya hicimos cuando quisimos obtener
     * un único usuario. La ruta de la url usada en este caso es "eliminarUsuario" con la variable "id" indicada entre
     * llaves*/
    @DeleteMapping("/eliminarLibro{id}")
    /*Devolvemos un ResponseEntity de tipo Object ya que realmente la respuesta va a ser vacía, bien porque no se haya
     * encontrado al usuario quq deseamos eliminar o bien porque al eliminarlo indicamos que hemos tenido éxito mediante
     * un "NO CONTENT".*/
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
