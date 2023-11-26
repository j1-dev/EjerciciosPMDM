package com.example.ej2ud4.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.ej2ud4.entidades.Alumno;
import com.example.ej2ud4.entidades.Libro;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {
  private static DBHelper me=null;
  public static final int DATABASE_VERSION = 1;
  public static final String DATABASE_NAME = "final.db";
  private static final String SQL_CREATE_ALUMNO =
      "CREATE TABLE Alumno (idalumno INTEGER PRIMARY KEY, nombre TEXT NOT NULL, apellidos TEXT, fechaNacimiento TEXT)";
  private static final String SQL_CREATE_LIBRO =
      "CREATE TABLE Libro (idlibro INTEGER PRIMARY KEY, nombre TEXT, idalumno INTEGER, FOREIGN KEY (idalumno) REFERENCES alumno(idalumno))";
  private static final String SQL_DELETE_ALUMNO =
      "DROP TABLE IF EXISTS Alumno";
  private static final String SQL_DELETE_LIBRO =
      "DROP TABLE IF EXISTS Libro";

  @Override
  public void onCreate(SQLiteDatabase sqLiteDatabase) {
    sqLiteDatabase.execSQL(SQL_CREATE_ALUMNO);
    sqLiteDatabase.execSQL(SQL_CREATE_LIBRO);
  }

  @Override
  public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    sqLiteDatabase.execSQL(SQL_DELETE_ALUMNO);
    sqLiteDatabase.execSQL(SQL_DELETE_LIBRO);
    onCreate(sqLiteDatabase);
  }

  public DBHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  public static DBHelper getInstance(Context context){
    if (me == null) {
      synchronized(DBHelper.class){
        if(me==null){
          me = new DBHelper(context);
        }
      }
    }
    return me;
  }

  public boolean insertarAlumno(Alumno alumno) {
    SQLiteDatabase db = this.getWritableDatabase();

    ContentValues values = new ContentValues();
    values.put("nombre", alumno.getNombre());
    values.put("apellidos", alumno.getApellidos());
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    values.put("fechaNacimiento", dateFormat.format(alumno.getFechaNacimiento()));

    long newRowId = db.insert("Alumno", null, values);

    return newRowId > 0;
  }

  public boolean actualizarAlumno(Alumno alumno) {
    SQLiteDatabase db = this.getWritableDatabase();

    ContentValues values = new ContentValues();
    values.put("nombre", alumno.getNombre());
    values.put("apellidos", alumno.getApellidos());
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    values.put("fechaNacimiento", dateFormat.format(alumno.getFechaNacimiento()));

    String selection = "idalumno = ?";

    String[] selectionArgs = { String.valueOf(alumno.getIdAlumno()) };

    return db.update("Alumno", values, selection, selectionArgs)>0;
  }

  public boolean eliminarAlumno(Long idAlumno){
    SQLiteDatabase db = this.getWritableDatabase();
    String selection = "idalumno = ?";
    String[] selectionArgs = { String.valueOf(idAlumno) };
    return db.delete("Alumno", selection, selectionArgs)>0;
  }

  public ArrayList<Alumno> getAlumnos() throws ParseException {
    SQLiteDatabase db = this.getReadableDatabase();

    String[] projection = {"idalumno","nombre","apellidos","fechaNacimiento"};

    Cursor c = db.query("Alumno",projection,null,null,null,null,null);

    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

    ArrayList<Alumno> lista=new ArrayList<>();

    while (c.moveToNext()) {
      Alumno elem=new Alumno();
      elem.setIdAlumno(c.getInt(c.getColumnIndexOrThrow("idalumno")));
      elem.setNombre(c.getString(c.getColumnIndexOrThrow("nombre")));
      elem.setApellidos(c.getString(c.getColumnIndexOrThrow("apellidos")));
      elem.setFechaNacimiento(dateFormat.parse(c.getString(c.getColumnIndexOrThrow("fechaNacimiento"))));
      lista.add(elem);
    }
    return lista;
  }

  public Alumno getAlumnoById(int idAlumno) throws ParseException {
    SQLiteDatabase db = this.getReadableDatabase();

    String[] projection = {"idalumno","nombre","apellidos","fechaNacimiento"};

    String[] selectionArgs = { String.valueOf(idAlumno) };

    String selection="idalumno=?";

    Cursor c = db.query("Alumno",projection,selection,selectionArgs,null,null,null);

    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

    ArrayList<Alumno> lista=new ArrayList<>();

    while (c.moveToNext()) {
      Alumno elem=new Alumno();
      elem.setIdAlumno(c.getInt(c.getColumnIndexOrThrow("idalumno")));
      elem.setNombre(c.getString(c.getColumnIndexOrThrow("nombre")));
      elem.setApellidos(c.getString(c.getColumnIndexOrThrow("apellidos")));
      elem.setFechaNacimiento(dateFormat.parse(c.getString(c.getColumnIndexOrThrow("fechaNacimiento"))));
      lista.add(elem);
    }
    if(lista.isEmpty()){
      return null;
    }
    else{
      return lista.get(0);
    }
  }

  public boolean insertarLibro(Libro libro) {
    SQLiteDatabase db = this.getWritableDatabase();

    ContentValues values = new ContentValues();
    values.put("nombre", libro.getNombre());
    values.put("idalumno", libro.getIdAlumno());

    long newRowId = db.insert("Libro", null, values);

    return newRowId > 0;
  }

  public boolean actualizarLibro(Libro libro) {
    SQLiteDatabase db = this.getWritableDatabase();

    ContentValues values = new ContentValues();
    values.put("nombre", libro.getNombre());

    String selection = "idlibro = ?";

    String[] selectionArgs = { String.valueOf(libro.getIdLibro()) };

    return db.update("Libro", values, selection, selectionArgs)>0;
  }

  public boolean eliminarLibro(Long idAlumno){
    SQLiteDatabase db = this.getWritableDatabase();
    String selection = "idlibro = ?";
    String[] selectionArgs = { String.valueOf(idAlumno) };
    return db.delete("Libro", selection, selectionArgs)>0;
  }

  public ArrayList<Libro> getLibros() throws ParseException {
    SQLiteDatabase db = this.getReadableDatabase();

    String[] projection = {"idlibro","nombre","idalumno"};

    Cursor c = db.query("Libro",projection,null,null,null,null,null);

    ArrayList<Libro> lista=new ArrayList<>();

    while (c.moveToNext()) {
      Libro elem=new Libro();
      elem.setIdLibro(c.getInt(c.getColumnIndexOrThrow("idlibro")));
      elem.setNombre(c.getString(c.getColumnIndexOrThrow("nombre")));
      elem.setIdAlumno(c.getInt(c.getColumnIndexOrThrow("idalumno")));
      lista.add(elem);
      System.out.println(elem.getIdLibro());
      System.out.println(elem.getNombre());
      System.out.println(elem.getIdAlumno());
    }
    return lista;
  }

  public ArrayList<Libro> getLibrosByAlumno(int idAlulmno) throws ParseException {
    ArrayList<Libro> libros = getLibros();

    libros.removeIf(libro -> libro.getIdAlumno() != idAlulmno);

    return libros;
  }

  public Libro getLibroById(int idLibro) throws ParseException {
    SQLiteDatabase db = this.getReadableDatabase();

    String[] projection = {"idlibro","nombre","idalumno"};

    String[] selectionArgs = { String.valueOf(idLibro) };

    String selection="idlibro=?";

    Cursor c = db.query("Libro",projection,selection,selectionArgs,null,null,null);

    ArrayList<Libro> lista=new ArrayList<>();

    while (c.moveToNext()) {
      Libro elem=new Libro();
      elem.setIdLibro(c.getInt(c.getColumnIndexOrThrow("idlibro")));
      elem.setNombre(c.getString(c.getColumnIndexOrThrow("nombre")));
      elem.setIdAlumno(c.getInt(c.getColumnIndexOrThrow("idalumno")));
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
