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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.restapiapp.entidades.Tienda;
import com.example.restapiapp.entidades.Parametro;
import com.example.restapiapp.util.DBHelper;
import com.example.restapiapp.util.Internetop;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NuevoTienda extends AppCompatActivity {
  private int position;
  private int idPersona;
  private DBHelper dbHelper;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_nuevo_tienda);
    if(savedInstanceState!=null){/*Si hemos hecho onSaveInstanceState previamente y hemos almacenado información,
        la recuperamos*/
      position = savedInstanceState.getInt("position",-1);
      idPersona = savedInstanceState.getInt("idPersona", -1);
    }
    else{//Si es la primera vez que se crea la actividad, obtenemos la información que pasamos a través del Intent
      Intent intent = getIntent();
      position = intent.getIntExtra("position", -1);
      idPersona = intent.getIntExtra("idPersona", -1);
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
    EditText textNombre = findViewById(R.id.et_nuevo_tienda_name);
    EditText textDireccion = findViewById(R.id.et_nuevo_tienda_address);
    EditText textLatitud = findViewById(R.id.et_nuevo_tienda_latitude);
    EditText textLongitud = findViewById(R.id.et_nuevo_tienda_longitude);

    String nombre = textNombre.getText().toString();
    String direccion = textDireccion.getText().toString();
    String latitud = textLatitud.getText().toString();
    String longitud = textLongitud.getText().toString();

    Button btAceptar= findViewById(R.id.bt_nuevo_accept);
    btAceptar.setEnabled(false);
    btAceptar.setClickable(false);
    Resources res = getResources();
    Tienda tienda = new Tienda();
    tienda.setNombre(nombre);
    tienda.setDireccion(direccion);
    tienda.setLatitud(Double.parseDouble(latitud));
    tienda.setLongitud(Double.parseDouble(longitud));
    if (isNetworkAvailable()) {
      String url = res.getString(R.string.tienda_url) + "nuevoTienda";
      sendTask(url, tienda);
    } else {
      showError("error.IOException");
    }
  }

  private void sendTask(String url, Tienda tienda) {
    ExecutorService executor = Executors.newSingleThreadExecutor();
    Handler handler = new Handler(Looper.getMainLooper());
    executor.execute(new Runnable() {
      @Override
      public void run() {
        Internetop interopera=Internetop.getInstance();
        List<Parametro> params = new ArrayList<>();
        params.add(new Parametro("nombre", tienda.getNombre()));
        params.add(new Parametro("direccion", tienda.getDireccion()));
        params.add(new Parametro("latitud", String.valueOf(tienda.getLatitud())));
        params.add(new Parametro("longitud", String.valueOf(tienda.getLongitud())));
        String result = interopera.postText(url,params);
        System.out.println(url);
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