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

import com.example.restapiapp.entidades.Parametro;
import com.example.restapiapp.entidades.Persona;
import com.example.restapiapp.util.Internetop;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ModificarPersona extends AppCompatActivity {

  private Persona persona;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_modificar_persona);

    if(savedInstanceState!=null){
      try {
        persona.fromJSONString(savedInstanceState.getString("persona"));
      } catch (JSONException e) {
        throw new RuntimeException(e);
      }
    }
    else{
      Intent intent = getIntent();
      persona = new Persona();
      try {
        persona.fromJSONString(intent.getStringExtra("persona"));
      } catch (JSONException e) {
        throw new RuntimeException(e);
      }
    }

    fillInformation();
  }

  private void fillInformation() {
    EditText textNombre = findViewById(R.id.et_modificar_name);
    EditText textApellidos = findViewById(R.id.et_modificar_surname);

    textNombre.setText(persona.getNombre());
    textApellidos.setText(persona.getApellidos());
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    try {
      outState.putString("persona", persona.toJSON().toString());
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
    EditText textNombre = findViewById(R.id.et_modificar_name);
    EditText textApellidos = findViewById(R.id.et_modificar_surname);
    EditText textDni = findViewById(R.id.et_modificar_dni);
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
    if(continuar){
      Button btAceptar=findViewById(R.id.bt_modificar_accept);
      ProgressBar pbAceptar=findViewById(R.id.pb_modificar_persona);
      //Desactivamos el botón y mostramos la barra de progreso
      btAceptar.setEnabled(false);
      btAceptar.setClickable(false);
      pbAceptar.setVisibility(View.VISIBLE);
      if (isNetworkAvailable()) {
        String url = res.getString(R.string.persona_url) + "actualizarPersona";
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
        params.add(new Parametro("idPersona", persona.getIdPersona()+""));
        params.add(new Parametro("nombre", nombre));
        params.add(new Parametro("apellidos", apellidos));
        params.add(new Parametro("dni", dni));
        String result = interopera.putText(url,params);
        handler.post(new Runnable() {
          @Override
          public void run() {
            Button btAceptar= findViewById(R.id.bt_modificar_accept);//Volvemos a activar el botón aceptar
            ProgressBar pbAceptar=(ProgressBar) findViewById(R.id.pb_modificar_persona);
            btAceptar.setEnabled(true);
            btAceptar.setClickable(true);
            pbAceptar.setVisibility(View.GONE);
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

  public void back(View view) {
    this.finish();
  }
}