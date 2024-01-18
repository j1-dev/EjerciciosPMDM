package com.example.restapiapp;

import android.content.Context;
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

import com.example.restapiapp.entidades.Parametro;
import com.example.restapiapp.util.Internetop;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NuevoPersona extends AppCompatActivity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_nuevo_persona);
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
    EditText textNombre = findViewById(R.id.et_nuevo_name);
    EditText textApellidos = findViewById(R.id.et_nuevo_surname);
    EditText textDni = findViewById(R.id.et_nuevo_dni);
    //Conseguir valores
    String nombre=textNombre.getText().toString();
    String apellidos=textApellidos.getText().toString();
    String dni=textDni.getText().toString();
    //Validación
    Resources res = getResources();
    boolean continuar=true;
    if(nombre.isEmpty()){
      textNombre.setError(res.getString(R.string.campo_obligatorio));
      continuar=false;
    }
    if(apellidos.isEmpty()){
      textApellidos.setError(res.getString(R.string.campo_obligatorio));
      continuar=false;
    }
    if(dni.isEmpty()){
      textDni.setError(res.getString(R.string.campo_obligatorio));
      continuar=false;
    }
    if(!isValidDNI(dni)){
      textDni.setError("El dni no tiene el formato correcto");
      continuar=false;
    }

    if(continuar){
      Button btAceptar=findViewById(R.id.bt_nuevo_accept);
      ProgressBar pbAceptar=findViewById(R.id.pb_nuevo_persona);
      //Desactivamos el botón y mostramos la barra de progreso
      btAceptar.setEnabled(false);
      btAceptar.setClickable(false);
      pbAceptar.setVisibility(View.VISIBLE);
      if (isNetworkAvailable()) {
        String url = res.getString(R.string.persona_url) + "nuevoPersona";
        sendTask(url, nombre, apellidos, dni);
      } else {
        showError("error.IOException");
      }
    }
  }

  private void sendTask(String url, String nombre, String apellidos, String dni) {
    ExecutorService executor = Executors.newSingleThreadExecutor();
    Handler handler = new Handler(Looper.getMainLooper());
    executor.execute(new Runnable() {
      @Override
      public void run() {
        Internetop interopera=Internetop.getInstance();
        List<Parametro> params = new ArrayList<>();
        params.add(new Parametro("nombre", nombre));
        params.add(new Parametro("apellidos", apellidos));
        params.add(new Parametro("dni",dni));
        String result = interopera.postText(url,params);
        handler.post(new Runnable() {
          @Override
          public void run() {
            Button btAceptar= findViewById(R.id.bt_nuevo_accept);
            ProgressBar pbAceptar=(ProgressBar) findViewById(R.id.pb_nuevo_persona);
            pbAceptar.setVisibility(View.GONE);
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
      message=res.getString(R.string.error_connection);
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

  public static boolean isValidDNI(String dni) {
    // Check if the length is correct
    if (dni.length() != 9) {
      return false;
    }

    // Check if the first 8 characters are digits
    String digitsPart = dni.substring(0, 8);
    if (!digitsPart.matches("\\d+")) {
      return false;
    }

    // Check if the last character is a letter
    char lastChar = dni.charAt(8);
    return Character.isLetter(lastChar);
  }

  public void back(View view) {
    this.finish();
  }
}