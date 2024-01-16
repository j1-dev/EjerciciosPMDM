package com.example.restapiapp;

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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.restapiapp.entidades.Alumno;
import com.example.restapiapp.entidades.Parametro;
import com.example.restapiapp.util.DBHelper;
import com.example.restapiapp.util.Internetop;

import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ModificarAlumno extends AppCompatActivity {

  private DatePickerDialog dpdNacimiento;
  private EditText etNacimiento;
  private String snacimiento;
  private Alumno alumno;
  SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_modificar_alumno);

    if(savedInstanceState!=null){
      snacimiento = savedInstanceState.getString("snacimiento","");
      try {
        alumno.fromJSONString(savedInstanceState.getString("alumno"));
      } catch (JSONException e) {
        throw new RuntimeException(e);
      } catch (ParseException e) {
        throw new RuntimeException(e);
      }
    }
    else{
      snacimiento="";
      Intent intent = getIntent();
      alumno = new Alumno();
      try {
        alumno.fromJSONString(intent.getStringExtra("alumno"));
      } catch (JSONException | ParseException e) {
        throw new RuntimeException(e);
      }
    }
    Date fnacimiento;
    if(snacimiento.isEmpty()){
      fnacimiento=new Date();
    }
    else {
      try {
        fnacimiento = dateFormatter.parse(snacimiento);
      } catch (ParseException e) {
        e.printStackTrace();
        fnacimiento = new Date();
      }
    }
    fillInformation();
    prepararFecha(fnacimiento);
  }

  private void fillInformation() {
    EditText textNombre = findViewById(R.id.et_modificar_name);
    if(etNacimiento==null) {
      etNacimiento = findViewById(R.id.et_modificar_fecha_nacimiento);
    }
    EditText textApellidos = findViewById(R.id.et_modificar_surname);
    if(alumno.getFechaNacimiento()!=null){
      etNacimiento.setText(dateFormatter.format(alumno.getFechaNacimiento()));
    }
    textNombre.setText(alumno.getNombre());
    textApellidos.setText(alumno.getApellidos());
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    if(etNacimiento==null){
      etNacimiento=findViewById(R.id.et_modificar_fecha_nacimiento);
    }
    snacimiento=etNacimiento.getText().toString();
    outState.putString("snacimiento", snacimiento);
    try {
      outState.putString("alumno", alumno.toJSON().toString());
    } catch (JSONException e) {
      throw new RuntimeException(e);
    }
  }

  private void prepararFecha(Date inicio){
    if(etNacimiento==null) {
      etNacimiento = findViewById(R.id.et_modificar_fecha_nacimiento);
    }
    etNacimiento.setOnFocusChangeListener(new View.OnFocusChangeListener() {
      @Override
      public void onFocusChange(View v, boolean hasFocus) {
        if(hasFocus) {
          dpdNacimiento.show();
        }
        v.clearFocus();
      }
    });
    Calendar newCalendar = Calendar.getInstance();
    newCalendar.setTime(inicio);
    dpdNacimiento = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
      public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Calendar newDate = Calendar.getInstance();
        newDate.set(year, monthOfYear, dayOfMonth);
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        etNacimiento.setText(dateFormatter.format(newDate.getTime()));
      }
    },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH),
        newCalendar.get(Calendar.DAY_OF_MONTH));
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
    if(etNacimiento==null) {
      etNacimiento = findViewById(R.id.et_modificar_fecha_nacimiento);
    }
    EditText textApellidos = findViewById(R.id.et_modificar_surname);
    //Conseguir valores
    String nombre=textNombre.getText().toString();
    String fecha=etNacimiento.getText().toString();
    String apellidos=textApellidos.getText().toString();
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
    Date dfecha=null;
    if(!fecha.isEmpty()){
      SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
      try {
        dfecha=sdf.parse(fecha);
      } catch (ParseException e) {
        e.printStackTrace();
        etNacimiento.setError(res.getString(R.string.incorrect_date_format));
        continuar=false;
      }
    }
    if(continuar){
      Button btAceptar=findViewById(R.id.bt_modificar_accept);
      ProgressBar pbAceptar=findViewById(R.id.pb_modificar_alumno);
      //Desactivamos el botón y mostramos la barra de progreso
      btAceptar.setEnabled(false);
      btAceptar.setClickable(false);
      pbAceptar.setVisibility(View.VISIBLE);
      if (isNetworkAvailable()) {
        String url = res.getString(R.string.alumno_url) + "actualizarAlumno";
        if(dfecha==null){
          fecha="";
        }
        System.out.println(fecha);
        sendTask(url, nombre, apellidos, fecha);
      } else {
        showError("error.IOException");
      }
    }
  }

  private void sendTask(String url, String nombre, String apellidos, String fecha) {
    ExecutorService executor = Executors.newSingleThreadExecutor();
    Handler handler = new Handler(Looper.getMainLooper());
    executor.execute(new Runnable() {
      @Override
      public void run() {
        Internetop interopera=Internetop.getInstance();
        List<Parametro> params = new ArrayList<>();
        params.add(new Parametro("idAlumno", alumno.getIdAlumno()+""));
        params.add(new Parametro("nombre", nombre));
        params.add(new Parametro("apellidos", apellidos));
        params.add(new Parametro("fechaNacimiento", fecha));
        System.out.println(fecha);
        String result = interopera.putText(url,params);
        handler.post(new Runnable() {
          @Override
          public void run() {
            Button btAceptar= findViewById(R.id.bt_modificar_accept);//Volvemos a activar el botón aceptar
            ProgressBar pbAceptar=(ProgressBar) findViewById(R.id.pb_modificar_alumno);
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