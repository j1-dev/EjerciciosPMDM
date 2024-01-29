package com.example.exud5;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.example.exud5.entidades.Disco;
import com.example.exud5.entidades.Parametro;
import com.example.exud5.util.Internetop;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Modificar extends AppCompatActivity {
  private long idDisco;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if(savedInstanceState!=null){
      idDisco = savedInstanceState.getLong("idDisco",-1);
    }
    else{
      Intent intent = getIntent();
      idDisco = intent.getLongExtra("idDisco", -1);
    }
    //Cargamos la información del usuario que vamos a modificar
    cargarDisco(idDisco);

    setContentView(R.layout.activity_modificar);
    Spinner spTipo = findViewById(R.id.sp_modificar_tipo);
    List<String> arrayTipos = new ArrayList<>();
    arrayTipos.add("rock");
    arrayTipos.add("pop");
    arrayTipos.add("hip-hop");
    arrayTipos.add("country");
    arrayTipos.add("latino");
    arrayTipos.add("jazz");
    arrayTipos.add("blues");
    arrayTipos.add("indie");
    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item ,arrayTipos);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    spTipo.setAdapter(adapter);
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
  }

  private void cargarDisco(long idDisco) {
    if (isNetworkAvailable()) {
      Resources res = getResources();
      String url = res.getString(R.string.main_url) + "getDisco" + idDisco;
      getDiscoTask(url);
    }
    else{
      showError("error.IOException");
    }
  }

  private void getDiscoTask(String url){
    ExecutorService executor = Executors.newSingleThreadExecutor();
    Handler handler = new Handler(Looper.getMainLooper());
    executor.execute(new Runnable() {
      @Override
      public void run() {

        Internetop interopera= Internetop.getInstance();
        String result = interopera.getString(url);
        handler.post(new Runnable() {
          @Override
          public void run() {
            if(result.equalsIgnoreCase("error.IOException")||
                result.equals("error.OKHttp")) {

              showError(result);
            }
            else if(result.equalsIgnoreCase("null")){
              showError("error.desconocido");
            }
            else{
              fillInformation(result);
            }
          }
        });
      }
    });
  }

  private void fillInformation(String result){
    try {

      JSONObject jsonUser=new JSONObject(result);
      Disco disco = new Disco();
      disco.fromJSON(jsonUser);

      //Rellenar la información
      EditText textNombre = findViewById(R.id.et_modificar_nombre);
      RadioButton rbSi = findViewById(R.id.rb_modificar_si);
      RadioButton rbNo = findViewById(R.id.rb_modificar_no);
      Spinner tipo = findViewById(R.id.sp_modificar_tipo);
      tipo.setSelection(disco.getTipo());
      rbSi.setChecked(false);
      rbNo.setChecked(false);
      if (disco.isEscuchado()){
        rbSi.setChecked(true);
      } else {
        rbNo.setChecked(true);
      }
      textNombre.setText(disco.getNombre());
    } catch (JSONException | ParseException e) {
      showError("error.json");
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
    EditText textNombre = findViewById(R.id.et_modificar_nombre);
    RadioGroup rgEscuchado = findViewById(R.id.rg_modificar_escuchado);
    Spinner spTipo = findViewById(R.id.sp_modificar_tipo);
    //Conseguir valores
    String nombre=textNombre.getText().toString();
    int escuchado = rgEscuchado.getCheckedRadioButtonId();
    int tipo = spTipo.getSelectedItemPosition();
    //Validación
    Resources res = getResources();
    boolean continuar=true;
    if(nombre.isEmpty()){
      textNombre.setError(res.getString(R.string.campo_obligatorio));
      continuar=false;
    }

    boolean escuchadoBool = false;

    if(escuchado==R.id.rb_modificar_si) {
      escuchadoBool = true;
    } else {
      escuchadoBool = false;
    }

    if(continuar){
      Button btAceptar= findViewById(R.id.bt_modificar);
      //Desactivamos el botón y mostramos la barra de progreso
      btAceptar.setEnabled(false);
      btAceptar.setClickable(false);
      if (isNetworkAvailable()) {
        String url = res.getString(R.string.main_url) + "actualizarDisco";
        sendTask(url, idDisco, nombre, escuchadoBool, tipo);
      } else {
        showError("error.IOException");
      }
    }
  }

  private void sendTask(String url, long idDisco, String nombre, boolean escuchado, int tipo) {
    System.out.println("AAAA");
    System.out.println(nombre);
    System.out.println(escuchado+"");
    System.out.println(tipo);
    ExecutorService executor = Executors.newSingleThreadExecutor();
    Handler handler = new Handler(Looper.getMainLooper());
    executor.execute(new Runnable() {
      @Override
      public void run() {
        Internetop interopera=Internetop.getInstance();
        List<Parametro> params = new ArrayList<>();
        params.add(new Parametro("idDisco", idDisco+""));
        params.add(new Parametro("tipo", tipo+""));
        params.add(new Parametro("nombre", nombre));
        params.add(new Parametro("escuchado", escuchado+""));
        String result = interopera.putText(url,params);
        handler.post(new Runnable() {
          @Override
          public void run() {
            Button btAceptar= findViewById(R.id.bt_modificar);//Volvemos a activar el botón aceptar
            btAceptar.setEnabled(true);
            btAceptar.setClickable(true);
            System.out.println(result);
            long idCreado;
            try{
              idCreado=Long.parseLong(result);
            }catch(NumberFormatException ex){//Manejo de posible excepción
              idCreado=-1;
            }
            if(idCreado>0){//El ID devuelto debe ser mayor que 0 para que el resultado sea correcto
              setResult(RESULT_OK);
              finish();
            }
            else {//En caso contrario mostramos el error correspondiente
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

}