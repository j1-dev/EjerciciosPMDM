package com.example.restapiapp;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.restapiapp.entidades.Libro;
import com.example.restapiapp.entidades.Parametro;
import com.example.restapiapp.util.DBHelper;
import com.example.restapiapp.util.Internetop;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NuevoLibro extends AppCompatActivity {
  private int position;
  private int idAlumno;
  private DBHelper dbHelper;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_nuevo_libro);
    if(savedInstanceState!=null){/*Si hemos hecho onSaveInstanceState previamente y hemos almacenado información,
        la recuperamos*/
      position = savedInstanceState.getInt("position",-1);
      idAlumno = savedInstanceState.getInt("idAlumno", -1);
    }
    else{//Si es la primera vez que se crea la actividad, obtenemos la información que pasamos a través del Intent
      Intent intent = getIntent();
      position = intent.getIntExtra("position", -1);
      idAlumno = intent.getIntExtra("idAlumno", -1);
    }
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
  }

  private Boolean isNetworkAvailable() {
    ConnectivityManager connectivityManager = (ConnectivityManager)
        getSystemService(Context.CONNECTIVITY_SERVICE);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      Network nw = connectivityManager.getActiveNetwork();
      if (nw == null) {
        return false;
      } else {
        NetworkCapabilities actNw = connectivityManager.getNetworkCapabilities(nw);
        return (actNw != null) && (actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));
      }
    } else {
      NetworkInfo nwInfo = connectivityManager.getActiveNetworkInfo();
      return nwInfo != null && nwInfo.isConnected();
    }
  }

  public void aceptar(View view) {
    if(dbHelper==null){
      dbHelper=DBHelper.getInstance(this);
    }
    EditText etNombre = findViewById(R.id.et_nuevo_libro_name);
    String nombre=etNombre.getText().toString();
    Button btAceptar= findViewById(R.id.bt_nuevo_accept);
    btAceptar.setEnabled(false);
    btAceptar.setClickable(false);
    Resources res = getResources();
    Libro libro = new Libro();
    libro.setNombre(nombre);
    libro.setIdAlumno(idAlumno);
    if (isNetworkAvailable()) {
      String url = res.getString(R.string.libro_url) + "nuevoLibro";
      sendTask(url, libro);
    } else {
      showError("error.IOException");
    }
  }

  private void sendTask(String url, Libro libro) {
    ExecutorService executor = Executors.newSingleThreadExecutor();
    Handler handler = new Handler(Looper.getMainLooper());
    executor.execute(new Runnable() {
      @Override
      public void run() {
        Internetop interopera=Internetop.getInstance();
        List<Parametro> params = new ArrayList<>();
        params.add(new Parametro("nombre", libro.getNombre()));
        params.add(new Parametro("idAlumno", libro.getIdAlumno()+""));
        String result = interopera.postText(url,params);
        System.out.println(url);
        System.out.println(libro.getNombre() + "-" + libro.getIdAlumno());
        handler.post(new Runnable() {
          @Override
          public void run() {
            Button btAceptar= findViewById(R.id.bt_nuevo_accept);//Volvemos a activar el botón aceptar
            btAceptar.setEnabled(true);
            btAceptar.setClickable(true);
            long idCreado;
            try{
              idCreado=Long.parseLong(result);
            }catch(NumberFormatException ex){
              idCreado=-1;
            }
            if(idCreado>0){
              setResult(RESULT_OK);
              finish();
            }
            else {
              showError("error.desconocido");
            }
          }
        });
      }
    });
  }

  private void showError(String error) {
    String message;
    Resources res = getResources();
    int duration;
    if(error.equals("error.IOException")){
      duration = Toast.LENGTH_LONG;
      message="ERROR";
    }
    else {
      duration = Toast.LENGTH_SHORT;
      message = res.getString(R.string.error_unknown);
    }
    Context context = this.getApplicationContext();
    Toast toast = Toast.makeText(context, message, duration);
    toast.setGravity(Gravity.CENTER, 0, 0);
    toast.show();
  }



  public void back(View view) {
    this.finish();
  }
}