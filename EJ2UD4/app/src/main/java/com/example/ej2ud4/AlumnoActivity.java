package com.example.ej2ud4;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ej2ud4.adaptadores.LibrosAdapter;
import com.example.ej2ud4.entidades.Libro;
import com.example.ej2ud4.util.DBHelper;

import java.text.ParseException;
import java.util.ArrayList;

public class AlumnoActivity extends AppCompatActivity implements LibrosAdapter.LibrosAdapterCallback {
  private int position;
  private DBHelper dbHelper;
  private ArrayList<Libro> libros;
  private LibrosAdapter librosAdapter;
  private ListView listView;
  private final ActivityResultLauncher<Intent> nuevoResultLauncher = registerForActivityResult(
      new ActivityResultContracts.StartActivityForResult(),
      new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
          if (result.getResultCode() == Activity.RESULT_OK) {
            cargarLibros(position);
          }
        }
      });

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_alumno);
    if(savedInstanceState!=null){
      position = savedInstanceState.getInt("position",-1);
    }
    else{
      Intent intent = getIntent();
      position = intent.getIntExtra("position", -1);
    }
    dbHelper=DBHelper.getInstance(this);
    cargarLibros(position);
  }

  public void irNuevoLibro(View view){
    Intent myIntent = new Intent().setClass(this, NuevoLibro.class);
    myIntent.putExtra("position", position);
    nuevoResultLauncher.launch(myIntent);
  }

  private void cargarLibros(int position){
    ArrayList<Libro> auxLibros;
    if(dbHelper==null){
      dbHelper = DBHelper.getInstance(this);
    }try {
      auxLibros=dbHelper.getLibrosByAlumno(position);
    } catch (ParseException e) {
      auxLibros=new ArrayList<>();
    }
    if(libros==null){
      libros=new ArrayList<>();
    }else{
      libros.clear();
    }
    libros.addAll(auxLibros);
    if(librosAdapter==null) {
      librosAdapter = new LibrosAdapter(this, libros);
      librosAdapter.setCallback(this);
      if(listView==null){
        listView=findViewById(R.id.lv_libros);
      }
      listView.setAdapter(librosAdapter);
    } else{
      librosAdapter.notifyDataSetChanged();
    }
    TextView tvNoDatos=findViewById(R.id.tv_empty);
    if(libros.isEmpty()){
      tvNoDatos.setVisibility(View.VISIBLE);
      listView.setVisibility(View.GONE);
    }
    else{
      tvNoDatos.setVisibility(View.GONE);
      listView.setVisibility(View.VISIBLE);
    }
  }

  public void back(View view){
    this.finish();
  }
}