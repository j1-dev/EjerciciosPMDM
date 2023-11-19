package com.example.ej1ud4.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.ej1ud4.entidades.Videojuego;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

  private static DBHelper me=null;
  public static final int DATABASE_VERSION = 1;
  public static final String DATABASE_NAME = "VideojuegoBase.db";

  private static final String SQL_CREATE_VIDEOJUEGO =
      "CREATE TABLE Videojuego (idVideojuego INTEGER PRIMARY KEY, nombre TEXT NOT NULL, fechaSalida TEXT, genero INTEGER, consola INTEGER, seHaJugado INTEGER)";
  private static final String SQL_DELETE_VIDEOJUEGO =
      "DROP TABLE IF EXISTS Videojuego";

  @Override
  public void onCreate(SQLiteDatabase sqLiteDatabase) {
    sqLiteDatabase.execSQL(SQL_CREATE_VIDEOJUEGO);
  }

  @Override
  public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    sqLiteDatabase.execSQL(SQL_DELETE_VIDEOJUEGO);
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

  public boolean insertarVideojuego(Videojuego videojuego) {
    SQLiteDatabase db = this.getWritableDatabase();

    ContentValues values = new ContentValues();
    values.put("nombre", videojuego.getNombre());
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    values.put("fechaSalida", dateFormat.format(videojuego.getFechaSalida()));
    values.put("genero", videojuego.getGenero());
    values.put("consola", videojuego.getConsola());
    values.put("seHaJugado", videojuego.isSeHaJugado());
    long newRowId = db.insert("Videojuego", null, values);

    return newRowId > 0;

  }

  public boolean actualizarVideojuego(Videojuego videojuego) {
    SQLiteDatabase db = this.getWritableDatabase();

    ContentValues values = new ContentValues();
    values.put("nombre", videojuego.getNombre());
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    values.put("fechaSalida", dateFormat.format(videojuego.getFechaSalida()));
    values.put("genero", videojuego.getGenero());
    values.put("consola", videojuego.getConsola());
    values.put("seHaJugado", videojuego.isSeHaJugado());

    String selection = "idVideojuego = ?";
    String[] selectionArgs = { String.valueOf(videojuego.getIdVideojuego()) };
    return db.update("Videojuego", values, selection, selectionArgs)>0;
  }

  public boolean eliminarVideojuego(Long idVideojuego){
    SQLiteDatabase db = this.getWritableDatabase();
    String selection = "idVideojuego = ?";
    String[] selectionArgs = { String.valueOf(idVideojuego) };
    return db.delete("Videojuego", selection, selectionArgs)>0;
  }

  public ArrayList<Videojuego> getVideojuegos() throws ParseException {
    SQLiteDatabase db = this.getReadableDatabase();

    String[] projection = {"idVideojuego","nombre","fechaSalida","genero","consola", "seHaJugado"};
    Cursor c = db.query("Videojuego",projection,null,null,null,null,null);
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    ArrayList<Videojuego> lista=new ArrayList<>();
    while (c.moveToNext()) {
      Videojuego elem=new Videojuego();
      elem.setIdVideojuego(c.getInt(c.getColumnIndexOrThrow("idVideojuego")));
      elem.setNombre(c.getString(c.getColumnIndexOrThrow("nombre")));
      elem.setFechaSalida(dateFormat.parse(c.getString(c.getColumnIndexOrThrow("fechaSalida"))));
      elem.setGenero(c.getInt(c.getColumnIndexOrThrow("genero")));
      elem.setConsola(c.getInt(c.getColumnIndexOrThrow("consola")));
      elem.setSeHaJugado(c.getInt(c.getColumnIndexOrThrow("seHaJugado"))!=0);
      lista.add(elem);
    }
    return lista;
  }

  public Videojuego getVideojuegoById(int idVideojuego) throws ParseException {
    SQLiteDatabase db = this.getReadableDatabase();

    String[] projection = {"idVideojuego","nombre","fechaSalida","genero","consola", "seHaJugado"};
    String[] selectionArgs = { String.valueOf(idVideojuego) };
    String selection="idVideojuego=?";
    Cursor c = db.query("Videojuego",projection,selection,selectionArgs,null,null,null);
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    ArrayList<Videojuego> lista=new ArrayList<>();
    while (c.moveToNext()) {
      Videojuego elem=new Videojuego();
      elem.setIdVideojuego(c.getInt(c.getColumnIndexOrThrow("idVideojuego")));
      elem.setNombre(c.getString(c.getColumnIndexOrThrow("nombre")));
      elem.setFechaSalida(dateFormat.parse(c.getString(c.getColumnIndexOrThrow("fechaSalida"))));
      elem.setGenero(c.getInt(c.getColumnIndexOrThrow("genero")));
      elem.setConsola(c.getInt(c.getColumnIndexOrThrow("consola")));
      elem.setSeHaJugado(c.getInt(c.getColumnIndexOrThrow("seHaJugado"))!=0);
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
