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

import com.example.restapiapp.entidades.Libro;
import com.example.restapiapp.entidades.Parametro;
import com.example.restapiapp.util.DBHelper;
import com.example.restapiapp.util.Internetop;

import org.json.JSONException;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ModificarLibro extends AppCompatActivity {
  private int position;
  private Libro libro;
  private DBHelper dbHelper;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_modificar_libro);
    if(savedInstanceState!=null){
      position = savedInstanceState.getInt("position",-1);
      try {
        libro.fromJSONString(savedInstanceState.getString("libro"));
      } catch (JSONException e) {
        throw new RuntimeException(e);
      } catch (ParseException e) {
        throw new RuntimeException(e);
      }
    }
    else{
      Intent intent = getIntent();
      position = intent.getIntExtra("position", -1);
      libro = new Libro();
      try {
        libro.fromJSONString(intent.getStringExtra("libro"));
      } catch (JSONException e) {
        throw new RuntimeException(e);
      } catch (ParseException e) {
        throw new RuntimeException(e);
      }
    }
    fillInformation();
  }

  private void fillInformation() {
    EditText textNombre = findViewById(R.id.et_modificar_libro_name);
    System.out.println(textNombre.toString());
    System.out.println(libro.getNombre());
    textNombre.setText(libro.getNombre());
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    try {
      outState.putString("libro", libro.toJSON().toString());
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
    if(dbHelper==null){
      dbHelper=DBHelper.getInstance(this);
    }
    EditText etNombre = findViewById(R.id.et_modificar_libro_name);
    String nombre = etNombre.getText().toString();
    Button btAceptar = findViewById(R.id.bt_modificar_accept_libro);
    btAceptar.setEnabled(false);
    btAceptar.setClickable(false);
    Resources res = getResources();
    if (isNetworkAvailable()) {
      String url = res.getString(R.string.libro_url) + ("actualizarLibro"+libro.getIdLibro());
      sendTask(url, nombre);
      System.out.println(url);
    } else {
      showError("error.IOException");
    }
  }

  private void sendTask(String url, String nombre) {
    ExecutorService executor = Executors.newSingleThreadExecutor();
    Handler handler = new Handler(Looper.getMainLooper());
    executor.execute(new Runnable() {
      @Override
      public void run() {
        Internetop interopera=Internetop.getInstance();

        List<Parametro> params = new ArrayList<>();
        params.add(new Parametro("nombre", nombre));
        String result = interopera.putText(url,params);
        handler.post(new Runnable() {
          @Override
          public void run() {
            Button btAceptar = findViewById(R.id.bt_modificar_accept_libro);//Volvemos a activar el botón aceptar
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