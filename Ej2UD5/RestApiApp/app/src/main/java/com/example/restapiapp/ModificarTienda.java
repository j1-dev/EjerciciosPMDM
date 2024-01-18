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

import com.example.restapiapp.entidades.Parametro;
import com.example.restapiapp.entidades.Tienda;
import com.example.restapiapp.util.Internetop;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ModificarTienda extends AppCompatActivity {
  private Tienda tienda;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_modificar_tienda);
    if(savedInstanceState!=null){
      try {
        tienda.fromJSONString(savedInstanceState.getString("tienda"));
      } catch (JSONException e) {
        throw new RuntimeException(e);
      }
    }
    else{
      Intent intent = getIntent();
      tienda = new Tienda();
      try {
        tienda.fromJSONString(intent.getStringExtra("tienda"));
      } catch (JSONException e) {
        throw new RuntimeException(e);
      }
    }
    fillInformation();
  }

  private void fillInformation() {
    EditText textNombre = findViewById(R.id.et_modificar_tienda_name);
    EditText textDireccion = findViewById(R.id.et_modificar_tienda_address);
    EditText textLatitud = findViewById(R.id.et_modificar_tienda_latitude);
    EditText textLongitud = findViewById(R.id.et_modificar_tienda_longitude);

    textNombre.setText(tienda.getNombre());
    textDireccion.setText(tienda.getDireccion());
    textLatitud.setText(String.valueOf(tienda.getLatitud()));
    textLongitud.setText(String.valueOf(tienda.getLongitud()));
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    try {
      outState.putString("tienda", tienda.toJSON().toString());
    } catch (JSONException e) {
      throw new RuntimeException(e);
    }
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
    EditText textNombre = findViewById(R.id.et_modificar_tienda_name);
    EditText textDireccion = findViewById(R.id.et_modificar_tienda_address);
    EditText textLatitud = findViewById(R.id.et_modificar_tienda_latitude);
    EditText textLongitud = findViewById(R.id.et_modificar_tienda_longitude);

    String nombre = textNombre.getText().toString();
    String direccion = textDireccion.getText().toString();
    String latitud = textLatitud.getText().toString();
    String longitud = textLongitud.getText().toString();
    double lat = 0.0;
    double lon = 0.0;

    Resources res = getResources();
    boolean continuar=true;
    if(nombre.isEmpty()){
      textNombre.setError(res.getString(R.string.campo_obligatorio));
      continuar=false;
    }
    if(direccion.isEmpty()){
      textDireccion.setError(res.getString(R.string.campo_obligatorio));
      continuar=false;
    }
    if(latitud.isEmpty()){
      textLatitud.setError(res.getString(R.string.campo_obligatorio));
      continuar=false;
    }
    if(longitud.isEmpty()){
      textLongitud.setError(res.getString(R.string.campo_obligatorio));
      continuar=false;
    }
    try{
      lat = Double.parseDouble(latitud);
    } catch (NumberFormatException e) {
      continuar=false;
      textLatitud.setError("La latitud debe ser un número decimal");
    }
    try{
      lon = Double.parseDouble(longitud);
    } catch (NumberFormatException e) {
      continuar=false;
      textLongitud.setError("La longitud debe ser un número decimal");
    }

    if (isNetworkAvailable() && continuar) {
      Button btAceptar = findViewById(R.id.bt_modificar_accept_tienda);
      btAceptar.setEnabled(false);
      btAceptar.setClickable(false);
      String url = res.getString(R.string.tienda_url) + ("actualizarTienda");
      sendTask(url, nombre, direccion, latitud, longitud);
      System.out.println(url);
    } else {
      showError("error.IOException");
    }
  }

  private void sendTask(String url, String nombre, String direccion, String latitud, String longitud) {
    ExecutorService executor = Executors.newSingleThreadExecutor();
    Handler handler = new Handler(Looper.getMainLooper());
    executor.execute(new Runnable() {
      @Override
      public void run() {
        Internetop interopera=Internetop.getInstance();

        List<Parametro> params = new ArrayList<>();
        params.add(new Parametro("idTienda", tienda.getIdTienda()+""));
        params.add(new Parametro("nombre", nombre));
        params.add(new Parametro("direccion", direccion));
        params.add(new Parametro("latitud", latitud));
        params.add(new Parametro("longitud", longitud));
        String result = interopera.putText(url,params);
        System.out.println(result);
        handler.post(new Runnable() {
          @Override
          public void run() {
            Button btAceptar = findViewById(R.id.bt_modificar_accept_tienda);//Volvemos a activar el botón aceptar
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