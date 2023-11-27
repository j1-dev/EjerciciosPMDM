package com.example.ejemplosqlito.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.ejemplosqlito.entidades.Alumno;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    private static DBHelper me=null;
    public static final int DATABASE_VERSION=1;
    public static final String DATABASE_NAME="AlumnoBase.db";

    private static final String SQL_CREATE_ALUMNO =
            "CREATE TABLE Alumno (id INTEGER PRIMARY KEY, nombre TEXT NOT NULL, apellidos TEXT NOT NULL, " +
                    "dni TEXT UNIQUE NOT NULL, fecha TEXT NOT NULL, genero INTEGER NOT NULL, consentimiento" +
                    " INTEGER NOT NULL, nivel INTEGER NOT NULL)";

    private static final String SQL_DELETE_ALUMNO =
            "DROP TABLE IF EXISTS Alumno";

    public DBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static DBHelper getInstance(Context context){
        if (me == null){
            synchronized(DBHelper.class){
                if(me==null){
                    me = new DBHelper(context);
                }
            }
        }
        return me;
    }

    public boolean insertarAlumno(Alumno alumno){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nombre", alumno.getNombre());
        values.put("apellidos", alumno.getApellidos());
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        values.put("fecha", df.format(alumno.getFecha()));
        values.put("dni", alumno.getDni());
        values.put("genero", alumno.getGenero());
        values.put("consentimiento", alumno.isConsentimiento());
        long newRowId = db.insert("Alumno", null, values);
        return newRowId > 0;
    }

    public boolean actualizarAlumno(Alumno alumno){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nombre", alumno.getNombre());
        values.put("apellidos", alumno.getApellidos());
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        values.put("fecha", df.format(alumno.getFecha()));
        values.put("dni", alumno.getDni());
        values.put("genero", alumno.getGenero());
        values.put("consentimiento", alumno.isConsentimiento());

        String selection = "id = ?";
        String[] selectionArgs = {String.valueOf(alumno.getId())};

        long newRowId = db.update("Alumno", null, selection, selectionArgs);
        return newRowId > 0;
    }

    public boolean eliminarAlumno(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = "id = ?";
        String[] selectionArgs = {String.valueOf(id)};
        return db.delete("Alumno", selection, selectionArgs) > 0;
    }

    public ArrayList<Alumno> getAlumnos() throws ParseException {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {"id", "nombre", "apellidos", "dni", "fecha", "genero", "consentimiento","nivel"};

        Cursor c = db.query("Alumno",projection, null, null, null, null, null);

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        ArrayList<Alumno> alumnos = new ArrayList<Alumno>();
        while (c.moveToNext()){
            Alumno a= new Alumno();
            a.setId(c.getInt(c.getColumnIndexOrThrow("id")));
            a.setNombre(c.getString(c.getColumnIndexOrThrow("nombre")));
            a.setApellidos(c.getString(c.getColumnIndexOrThrow("apellidos")));
            a.setDni(c.getString(c.getColumnIndexOrThrow("dni")));
            a.setFecha(df.parse(c.getString(c.getColumnIndexOrThrow("fecha"))));
            a.setGenero(c.getInt(c.getColumnIndexOrThrow("genero")));
            a.setConsentimiento(c.getInt(c.getColumnIndexOrThrow("consentimiento"))!=0);
            a.setNivel(c.getInt(c.getColumnIndexOrThrow("nivel")));
            alumnos.add(a);
        }
        return alumnos;
    }

    public Alumno getAlumnoById(int idAlumno) throws ParseException {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {"id", "nombre", "apellidos", "dni", "fecha", "genero", "consentimiento","nivel"};
        String[] selectionArgs = {String.valueOf(idAlumno)};
        String selection = "id=?";
        Cursor c = db.query("Alumno",projection, selection, selectionArgs, null, null, null);

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        ArrayList<Alumno> alumnos = new ArrayList<>();
        while (c.moveToNext()){
            Alumno a= new Alumno();
            a.setId(c.getInt(c.getColumnIndexOrThrow("id")));
            a.setNombre(c.getString(c.getColumnIndexOrThrow("nombre")));
            a.setApellidos(c.getString(c.getColumnIndexOrThrow("apellidos")));
            a.setDni(c.getString(c.getColumnIndexOrThrow("dni")));
            a.setFecha(df.parse(c.getString(c.getColumnIndexOrThrow("fecha"))));
            a.setGenero(c.getInt(c.getColumnIndexOrThrow("genero")));
            a.setConsentimiento(c.getInt(c.getColumnIndexOrThrow("consentimiento"))!=0);
            a.setNivel(c.getInt(c.getColumnIndexOrThrow("nivel")));
            alumnos.add(a);
        }
        if(alumnos.isEmpty()){
            return null;
        } else {
            return alumnos.get(0);
        }
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_ALUMNO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DELETE_ALUMNO);
        onCreate(sqLiteDatabase);
    }
}
