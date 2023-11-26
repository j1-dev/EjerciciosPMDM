package com.example.ej2ud4;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.ej2ud4.adaptadores.AlumnosAdapter;
import com.example.ej2ud4.entidades.Alumno;
import com.example.ej2ud4.util.DBHelper;

import java.text.ParseException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AlumnosAdapter.AlumnosAdapterCallback {

  private DBHelper dbHelper;
  private ArrayList<Alumno> alumnos;
  private AlumnosAdapter alumnosAdapter;
  private ListView listView;
  private final ActivityResultLauncher<Intent> nuevoResultLauncher = registerForActivityResult(
      new ActivityResultContracts.StartActivityForResult(),
      new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
          if (result.getResultCode() == Activity.RESULT_OK) {
            cargarAlumnos();
          }
        }
      });

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    dbHelper=DBHelper.getInstance(this);

    cargarAlumnos();
  }

  public void irNuevoAlumno(View view){
    Intent myIntent = new Intent().setClass(this, NuevoAlumno.class);
    nuevoResultLauncher.launch(myIntent);
  }

  private void cargarAlumnos(){
    ArrayList<Alumno> auxAlumnos;
    if(dbHelper==null){
      dbHelper = DBHelper.getInstance(this);
    }
    try {
      auxAlumnos=dbHelper.getAlumnos();
    } catch (ParseException e) {
      auxAlumnos=new ArrayList<>();
    }
    if(alumnos==null){
      alumnos=new ArrayList<>();
    }
    else{
      alumnos.clear();
    }
    alumnos.addAll(auxAlumnos);
    if(alumnosAdapter==null) {
      alumnosAdapter = new AlumnosAdapter(this, alumnos);
      alumnosAdapter.setCallback(this);
      if(listView==null){
        listView=findViewById(R.id.lv_alumnos);
      }
      listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
          Intent myIntent = new Intent().setClass(parent.getContext(), AlumnoActivity.class);
          myIntent.putExtra("position", position);
          nuevoResultLauncher.launch(myIntent);
        }
      });
      listView.setAdapter(alumnosAdapter);
    }
    else{
      alumnosAdapter.notifyDataSetChanged();
    }
    TextView tvNoDatos=findViewById(R.id.tv_no_data);
    System.out.println(listView.toString());
    if(alumnos.isEmpty()){
      tvNoDatos.setVisibility(View.VISIBLE);
      listView.setVisibility(View.GONE);
    }
    else{
      tvNoDatos.setVisibility(View.GONE);
      listView.setVisibility(View.VISIBLE);
    }
  }


//  @Override
//  public void verLibrosPressed(int position) {
//    Intent myIntent = new Intent().setClass(this, AlumnoActivity.class);
//    myIntent.putExtra("position", position);
//    nuevoResultLauncher.launch(myIntent);
//  }
}