package com.example.exud2;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.exud2.adaptadores.EventosAdapter;
import com.example.exud2.entidades.Evento;
import com.example.exud2.util.DBHelper;

import java.text.ParseException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements EventosAdapter.EventosAdapterCallback{

  private DBHelper dbHelper;
  private ArrayList<Evento> eventos;
  private EventosAdapter eventosAdapter;
  private ListView listView;

  private ActivityResultLauncher<Intent> nuevoResultLauncher = registerForActivityResult(
      new ActivityResultContracts.StartActivityForResult(),
      new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
          if (result.getResultCode() == Activity.RESULT_OK) {
            cargarEventos();
          }
        }
      });

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    dbHelper=DBHelper.getInstance(this);
    cargarEventos();
  }

  public void irNuevoEvento(View view){
    Intent myIntent = new Intent().setClass(this, Nuevo.class);
    nuevoResultLauncher.launch(myIntent);
  }

  private void cargarEventos(){
    ArrayList<Evento> auxEventos;
    if(dbHelper==null){
      dbHelper = DBHelper.getInstance(this);
    }
    try {
      auxEventos=dbHelper.getEventos();
    } catch (ParseException e) {
      auxEventos=new ArrayList<>();
    }
    if(eventos==null){
      eventos=new ArrayList<>();
    }
    else{
      eventos.clear();
    }
    eventos.addAll(auxEventos);
    if(eventosAdapter==null) {
      eventosAdapter = new EventosAdapter(this, eventos);
      eventosAdapter.setCallback(this);
      if(listView==null){
        listView=findViewById(R.id.lv_eventos);
      }
      listView.setAdapter(eventosAdapter);
    }
    else{
      eventosAdapter.notifyDataSetChanged();
    }
    TextView tvNoDatos=findViewById(R.id.tv_no_data);
    if(eventos.isEmpty()){
      tvNoDatos.setVisibility(View.VISIBLE);
      listView.setVisibility(View.GONE);
    }
    else{
      tvNoDatos.setVisibility(View.GONE);
      listView.setVisibility(View.VISIBLE);
    }
  }

  @Override
  public void deletePressed(int position) {
    AlertDialog diaBox = AskOption(position);
    diaBox.show();
  }

  private AlertDialog AskOption(final int position)
  {
    AlertDialog myQuittingDialogBox =new AlertDialog.Builder(this)
        .setTitle("Borrar evento")
        .setMessage("Estás seguro?")
        .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int whichButton) {
            eliminarEvento(position);
            dialog.dismiss();
          }
        })
        .setNegativeButton("No", new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
          }
        })
        .create();
    return myQuittingDialogBox;
  }

  private void eliminarEvento(int position){
    if(eventos!=null){
      if(eventos.size()>position){
        if(dbHelper!=null){
          Evento evento=eventos.get(position);
          boolean eliminado=dbHelper.eliminarEvento((long)evento.getIdEvento());
          if(eliminado){
            cargarEventos();
          }
          else{
            showError("error.IOException");
          }
        }
        else{
          showError("error.desconocido");
        }
      }
      else{
        showError("error.desconocido");
      }
    }
    else{
      showError("error.desconocido");
    }
  }

  private void showError(String error) {
    String message;
    Resources res = getResources();
    int duration;
    if(error.equals("error.IOException")){//Aquí establecemos el mensaje de error y cuánto durará
      duration = Toast.LENGTH_LONG;
      message="Error de la base de datos";
    }
    else {
      duration = Toast.LENGTH_SHORT;
      message = "Error desconocido";
    }
    Context context = this.getApplicationContext();
    Toast toast = Toast.makeText(context, message, duration);/*Aquí creamos el toast, indicando
        el mensaje y la duración*/
    toast.setGravity(Gravity.CENTER, 0, 0);//Establecemos la posición donde se mostrará
    toast.show();//Mostramos el mensaje
  }

  @Override
  public void editPressed(int position) {
    if(eventos!=null) {
      if (eventos.size() > position) {
        Evento evento=eventos.get(position);
        Intent myIntent = new Intent().setClass(this, Modificar.class);
        myIntent.putExtra("idEvento",evento.getIdEvento());
        nuevoResultLauncher.launch(myIntent);
      }
    }
  }
}