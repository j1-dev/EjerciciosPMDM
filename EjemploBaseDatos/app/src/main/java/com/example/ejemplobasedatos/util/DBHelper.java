package com.example.ejemplobasedatos.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.ejemplobasedatos.entidades.Alumno;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {/*La clase debe extender a SQLiteOpenHelper para
trabajar con este sistema gestor de bases de datos*/

    private static DBHelper me=null;//Referencia estática a la propia clase
    public static final int DATABASE_VERSION = 1;//Versión de la base de datos
    public static final String DATABASE_NAME = "AlumnoBase.db";//Nombre de la base de datos

    private static final String SQL_CREATE_ALUMNO =
            "CREATE TABLE Alumno (idAlumno INTEGER PRIMARY KEY, nombre TEXT NOT NULL, apellidos TEXT," +
                    " dni TEXT NOT NULL UNIQUE, fechaNacimiento TEXT, genero INTEGER," +
                    "consentimiento INTEGER, nivelEstudios INTEGER)";
    private static final String SQL_DELETE_ALUMNO =
            "DROP TABLE IF EXISTS Alumno";

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_ALUMNO);/*Al crear la base de datos únicamente ejecutamos
        el código SQL para crear la tabla alumnos*/
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DELETE_ALUMNO);
        onCreate(sqLiteDatabase);
        /*Al actualizar el esquema de la base de datos únicamente borramos las tablas creadas
        anteriormente y las volvemos a crear*/
    }

    public DBHelper(Context context) {/*Al construir un objeto de esta clase llamaremos a su método
    de la clase de la que hereda en el que debemos incluir el contexto (puede ser la clase que lo
    llama), el nombre de la base de datos y la versión de la base de datos*/
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static DBHelper getInstance(Context context){
        if (me == null) {//Comprobamos que no se haya creado previamente un objeto de nuestra clase
            synchronized(DBHelper.class){/*Controlamos que en el momento en que creamos este objeto
             nadie más esté intentando crearlo a la vez (control de concurrencia)*/
                if(me==null){/*Volvemos a comprobar por última vez que no esté ya creado, de nuevo
                por control de concurrencia*/
                    me = new DBHelper(context);/*Creamos el objeto de la clase DBHelper y lo almacenamos
                    en su correspondiente variable*/
                }
            }
        }
        return me;//Devolvemos el objeto de la clase DBHelper
    }

    public boolean insertarAlumno(Alumno alumno) {
        //Obtenemos una referencia a la base de datos en modo escritura
        SQLiteDatabase db = this.getWritableDatabase();

        //Creamos un mapa de valores en los que las columnas son las claves
        ContentValues values = new ContentValues();
        values.put("nombre", alumno.getNombre());
        values.put("apellidos", alumno.getApellidos());
        values.put("dni", alumno.getDni());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        values.put("fechaNacimiento", dateFormat.format(alumno.getFechaNacimiento()));
        values.put("genero", alumno.getGenero());
        values.put("nivelEstudios", alumno.getNivelEstudios());
        values.put("consentimiento", alumno.isConsentimiento());
        // Insertamos la nueva fila, devolviendo la clave primaria de la misma
        long newRowId = db.insert("Alumno", null, values);

        return newRowId > 0;

    }

    public boolean actualizarAlumno(Alumno alumno) {
        //Obtenemos una referencia a la base de datos en modo escritura
        SQLiteDatabase db = this.getWritableDatabase();

        //Creamos un mapa de valores en los que las columnas son las claves
        ContentValues values = new ContentValues();
        values.put("nombre", alumno.getNombre());
        values.put("apellidos", alumno.getApellidos());
        values.put("dni", alumno.getDni());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        values.put("fechaNacimiento", dateFormat.format(alumno.getFechaNacimiento()));
        values.put("genero", alumno.getGenero());
        values.put("nivelEstudios", alumno.getNivelEstudios());
        values.put("consentimiento", alumno.isConsentimiento());

        // Definir la parte de selección de la consulta
        String selection = "idAlumno = ?";
        // Dar valor a los argumentos de selección
        String[] selectionArgs = { String.valueOf(alumno.getIdAlumno()) };
        // Ejecutar sentencia SQL
        return db.update("Alumno", values, selection, selectionArgs)>0;

    }

    public boolean eliminarAlumno(Long idAlumno){
        //Obtenemos una referencia de la base de datos en modo escritura
        SQLiteDatabase db = this.getWritableDatabase();
        // Definir la parte de selección de la consulta
        String selection = "idAlumno = ?";
        // Dar valor a los argumentos de selección
        String[] selectionArgs = { String.valueOf(idAlumno) };
        // Ejecutar sentencia SQL
        return db.delete("Alumno", selection, selectionArgs)>0;
    }

    public ArrayList<Alumno> getAlumnos() throws ParseException {
        //Obtenemos una referencia de la base de datos en modo lectura
        SQLiteDatabase db = this.getReadableDatabase();

        //Definimos una proyección con las columnas de la tabla que vamos a usar en esta consulta
        String[] projection = {"idAlumno","nombre","apellidos","dni","fechaNacimiento",
                "genero","consentimiento","nivelEstudios"};
        //Usaremos un cursor para ejecutar la consulta
        Cursor c = db.query("Alumno",projection,null,null,null,null,null);
        //Para transformar la fecha de texto a Date
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        //Creamos una lista de alumnos vacía
        ArrayList<Alumno> lista=new ArrayList<>();
        //Recorremos el cursor con los valores que hubiera devuelto la consulta
        while (c.moveToNext()) {
            //Debemos transformar cada fila devuelta por el cursor en un objeto de la clase Alumno
            Alumno elem=new Alumno();
            elem.setIdAlumno(c.getInt(c.getColumnIndexOrThrow("idAlumno")));
            elem.setNombre(c.getString(c.getColumnIndexOrThrow("nombre")));
            elem.setApellidos(c.getString(c.getColumnIndexOrThrow("apellidos")));
            elem.setDni(c.getString(c.getColumnIndexOrThrow("dni")));
            elem.setFechaNacimiento(dateFormat.parse(c.getString(c.getColumnIndexOrThrow("fechaNacimiento"))));
            elem.setGenero(c.getInt(c.getColumnIndexOrThrow("genero")));
            elem.setConsentimiento(c.getInt(c.getColumnIndexOrThrow("consentimiento"))!=0);
            elem.setNivelEstudios(c.getInt(c.getColumnIndexOrThrow("nivelEstudios")));
            //Añadimos el objeto de la clase alumno a la lista de alumnos
            lista.add(elem);
        }
        return lista;//Devolvemos la lista de alumnos
    }

    public Alumno getAlumnoById(int idAlumno) throws ParseException {
        //Obtenemos una referencia de la base de datos en modo lectura
        SQLiteDatabase db = this.getReadableDatabase();

        //Definimos una proyección con las columnas de la tabla que vamos a usar en esta consulta
        String[] projection = {"idAlumno","nombre","apellidos","dni","fechaNacimiento",
                "genero","consentimiento","nivelEstudios"};
        //Damos un valor al argumento de selección
        String[] selectionArgs = { String.valueOf(idAlumno) };
        //Definimos la parte de selección de la consulta
        String selection="idAlumno=?";
        //Usaremos un cursor para ejecutar la consulta
        Cursor c = db.query("Alumno",projection,selection,selectionArgs,null,null,null);
        //Para transformar la fecha de texto a Date
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        //Creamos una lista de alumnos vacía
        ArrayList<Alumno> lista=new ArrayList<>();
        //Recorremos el cursor con los valores que hubiera devuelto la consulta
        while (c.moveToNext()) {
            //Debemos transformar cada fila devuelta por el cursor en un objeto de la clase Alumno
            Alumno elem=new Alumno();
            elem.setIdAlumno(c.getInt(c.getColumnIndexOrThrow("idAlumno")));
            elem.setNombre(c.getString(c.getColumnIndexOrThrow("nombre")));
            elem.setApellidos(c.getString(c.getColumnIndexOrThrow("apellidos")));
            elem.setDni(c.getString(c.getColumnIndexOrThrow("dni")));
            elem.setFechaNacimiento(dateFormat.parse(c.getString(c.getColumnIndexOrThrow("fechaNacimiento"))));
            elem.setGenero(c.getInt(c.getColumnIndexOrThrow("genero")));
            elem.setConsentimiento(c.getInt(c.getColumnIndexOrThrow("consentimiento"))!=0);
            elem.setNivelEstudios(c.getInt(c.getColumnIndexOrThrow("nivelEstudios")));
            //Añadimos el objeto de la clase alumno a la lista de alumnos
            lista.add(elem);
        }
        if(lista.isEmpty()){
            return null;
        }
        else{
            return lista.get(0);
        }
    }

}
