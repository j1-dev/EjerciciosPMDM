package com.example.exud2.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.exud2.entidades.Evento;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

  private static DBHelper me=null;
  public static final int DATABASE_VERSION = 1;
  public static final String DATABASE_NAME = "EventosBase.db";

  private static final String SQL_CREATE_EVENTO =
      "CREATE TABLE Evento (idEvento INTEGER PRIMARY KEY, nombre TEXT NOT NULL, fecha TEXT, tipo INTEGER)";
  private static final String SQL_DELETE_EVENTO=
      "DROP TABLE IF EXISTS Evento";

  @Override
  public void onCreate(SQLiteDatabase sqLiteDatabase) {
    sqLiteDatabase.execSQL(SQL_CREATE_EVENTO);
  }

  @Override
  public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    sqLiteDatabase.execSQL(SQL_DELETE_EVENTO);
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

  public boolean insertarEvento(Evento evento) {
    SQLiteDatabase db = this.getWritableDatabase();

    ContentValues values = new ContentValues();
    values.put("nombre", evento.getNombre());
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    values.put("fecha", dateFormat.format(evento.getFecha()));
    values.put("tipo", evento.getTipo());
    long newRowId = db.insert("Evento", null, values);

    return newRowId > 0;

  }

  public boolean actualizarEvento(Evento evento) {
    SQLiteDatabase db = this.getWritableDatabase();

    ContentValues values = new ContentValues();
    values.put("nombre", evento.getNombre());
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    values.put("fecha", dateFormat.format(evento.getFecha()));
    values.put("tipo", evento.getTipo());

    String selection = "idEvento = ?";
    String[] selectionArgs = { String.valueOf(evento.getIdEvento()) };
    long rowId = db.update("Evento", values, selection, selectionArgs);
    System.out.println(rowId);
    return rowId>0;
  }

  public boolean eliminarEvento(Long idEvento){
    SQLiteDatabase db = this.getWritableDatabase();
    String selection = "idEvento = ?";
    String[] selectionArgs = { String.valueOf(idEvento) };
    return db.delete("Evento", selection, selectionArgs)>0;
  }

  public ArrayList<Evento> getEventos() throws ParseException {
    SQLiteDatabase db = this.getReadableDatabase();

    String[] projection = {"idEvento","nombre","fecha","tipo"};
    Cursor c = db.query("Evento",projection,null,null,null,null,null);
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    ArrayList<Evento> lista=new ArrayList<>();
    while (c.moveToNext()) {
      Evento elem=new Evento();
      elem.setIdEvento(c.getInt(c.getColumnIndexOrThrow("idEvento")));
      elem.setNombre(c.getString(c.getColumnIndexOrThrow("nombre")));
      elem.setFecha(dateFormat.parse(c.getString(c.getColumnIndexOrThrow("fecha"))));
      elem.setTipo(c.getInt(c.getColumnIndexOrThrow("tipo")));
      lista.add(elem);
    }
    return lista;
  }

  public Evento getEventoById(int idEvento) throws ParseException {
    SQLiteDatabase db = this.getReadableDatabase();

    String[] projection = {"idEvento","nombre","fecha","tipo"};
    String[] selectionArgs = { String.valueOf(idEvento) };
    String selection="idEvento=?";
    Cursor c = db.query("Evento",projection,selection,selectionArgs,null,null,null);
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    ArrayList<Evento> lista=new ArrayList<>();
    while (c.moveToNext()) {
      Evento elem=new Evento();
      elem.setIdEvento(c.getInt(c.getColumnIndexOrThrow("idEvento")));
      elem.setNombre(c.getString(c.getColumnIndexOrThrow("nombre")));
      elem.setFecha(dateFormat.parse(c.getString(c.getColumnIndexOrThrow("fecha"))));
      elem.setTipo(c.getInt(c.getColumnIndexOrThrow("tipo")));
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
