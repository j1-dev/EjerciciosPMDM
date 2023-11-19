package com.example.ej1ud4;

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

import com.example.ej1ud4.adaptadores.VideojuegosAdapter;
import com.example.ej1ud4.entidades.Videojuego;
import com.example.ej1ud4.util.DBHelper;
import com.example.ej1ud4.Nuevo;

import java.text.ParseException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements VideojuegosAdapter.VideojuegosAdapterCallback {

  private DBHelper dbHelper;
  private ArrayList<Videojuego> videojuegos;
  private VideojuegosAdapter videojuegosAdapter;
  private ListView listView;

  private ActivityResultLauncher<Intent> nuevoResultLauncher = registerForActivityResult(
      new ActivityResultContracts.StartActivityForResult(),
      new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
          if (result.getResultCode() == Activity.RESULT_OK) {
            cargarVideojuegos();
          }
        }
      });

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    dbHelper=DBHelper.getInstance(this);
    cargarVideojuegos();
  }

  public void irNuevoVideojuego(View view){
    Intent myIntent = new Intent().setClass(this, Nuevo.class);
    nuevoResultLauncher.launch(myIntent);
  }

  private void cargarVideojuegos(){
    ArrayList<Videojuego> auxVideojuegos;
    if(dbHelper==null){
      dbHelper = DBHelper.getInstance(this);
    }
    try {
      auxVideojuegos=dbHelper.getVideojuegos();
    } catch (ParseException e) {
      auxVideojuegos=new ArrayList<>();
    }
    if(videojuegos==null){
      videojuegos=new ArrayList<>();
    }
    else{
      videojuegos.clear();
    }
    videojuegos.addAll(auxVideojuegos);
    if(videojuegosAdapter==null) {
      videojuegosAdapter = new VideojuegosAdapter(this, videojuegos);
      videojuegosAdapter.setCallback(this);
      if(listView==null){
        listView=findViewById(R.id.lv_videojuegos);
      }
      listView.setAdapter(videojuegosAdapter);
    }
    else{
      videojuegosAdapter.notifyDataSetChanged();
    }
    TextView tvNoDatos=findViewById(R.id.tv_no_data);
    if(videojuegos.isEmpty()){
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
        .setTitle(R.string.delete_videogame)
        .setMessage(R.string.are_you_sure)
        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int whichButton) {
            eliminarVideojuego(position);
            dialog.dismiss();
          }
        })
        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
          }
        })
        .create();
    return myQuittingDialogBox;
  }

  private void eliminarVideojuego(int position){
    if(videojuegos!=null){
      if(videojuegos.size()>position){
        if(dbHelper!=null){
          Videojuego videojuego=videojuegos.get(position);
          boolean eliminado=dbHelper.eliminarVideojuego((long)videojuego.getIdVideojuego());
          if(eliminado){
            cargarVideojuegos();
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
      message=res.getString(R.string.error_database);
    }
    else {
      duration = Toast.LENGTH_SHORT;
      message = res.getString(R.string.error_unknown);
    }
    Context context = this.getApplicationContext();
    Toast toast = Toast.makeText(context, message, duration);/*Aquí creamos el toast, indicando
        el mensaje y la duración*/
    toast.setGravity(Gravity.CENTER, 0, 0);//Establecemos la posición donde se mostrará
    toast.show();//Mostramos el mensaje
  }

  @Override
  public void editPressed(int position) {
    if(videojuegos!=null) {
      if (videojuegos.size() > position) {
        Videojuego videojuego=videojuegos.get(position);
        Intent myIntent = new Intent().setClass(this, Modificar.class);
        myIntent.putExtra("idVideojuego",videojuego.getIdVideojuego());
        nuevoResultLauncher.launch(myIntent);
      }
    }
  }
}