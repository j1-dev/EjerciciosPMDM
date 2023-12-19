package com.example.restapiapp;

import android.app.Activity;
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
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.restapiapp.adaptadores.AlumnosAdapter;
import com.example.restapiapp.entidades.Alumno;
import com.example.restapiapp.util.DBHelper;
import com.example.restapiapp.util.Internetop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements AlumnosAdapter.AlumnosAdapterCallback {

  private DBHelper dbHelper;
  private ArrayList<Alumno> alumnos;
  private AlumnosAdapter alumnosAdapter;
  private ListView listView;
  private final ActivityResultLauncher<Intent> nuevoResultLauncher = registerForActivityResult(
      new ActivityResultContracts.StartActivityForResult(),
      new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
          if (result.getResultCode() == Activity.RESULT_OK) {
            cargarAlumnos();
          }
        }
      });

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    dbHelper=DBHelper.getInstance(this);
    listView=findViewById(R.id.lv_alumnos);
    cargarAlumnos();
  }

  private Boolean isNetworkAvailable() {
    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
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

  private void showError(String error) {
    String message;
    Resources res = getResources();
    int duration;
    if (error.equals("error.IOException")||error.equals("error.OKHttp")) {
      message = res.getString(R.string.error_connection);
      duration = Toast.LENGTH_SHORT;
    }
    else if(error.equals("error.undelivered")){
      message = res.getString(R.string.error_undelivered);
      duration = Toast.LENGTH_LONG;
    }
    else {
      message = res.getString(R.string.error_unknown);
      duration = Toast.LENGTH_SHORT;
    }
    Toast toast = Toast.makeText(this, message, duration);
    toast.show();
  }

  public void irNuevoAlumno(View view){
    Intent myIntent = new Intent().setClass(this, NuevoAlumno.class);
    nuevoResultLauncher.launch(myIntent);
  }

  private void cargarAlumnos(){
    if (isNetworkAvailable()) {
      ProgressBar pbMain = (ProgressBar) findViewById(R.id.pb_main);
      pbMain.setVisibility(View.VISIBLE);
      Resources res = getResources();
      String url = res.getString(R.string.alumno_url) + "listaAlumnos";
      getListaTask(url);
    }
    else{
      showError("error.IOException");
    }
  }

  private void getListaTask(String url) {
    ExecutorService executor = Executors.newSingleThreadExecutor();
    Handler handler = new Handler(Looper.getMainLooper());
    executor.execute(new Runnable() {//Ejecutamos el nuevo hilo
      @Override
      public void run() {
        Internetop interopera = Internetop.getInstance();
        String result = interopera.getString(url);
        handler.post(new Runnable() {
          @Override
          public void run() {
            if (result.equalsIgnoreCase("error.IOException") ||
                result.equals("error.OKHttp")) {
              showError(result);
            } else if (result.equalsIgnoreCase("null")) {
              showError("error.desconocido");
            } else {
              resetLista(result);
            }
          }
        });
      }
    });
  }

  private void resetLista(String result){
    System.out.println(result);
    try {
      JSONArray listaAlumnos = new JSONArray(result);
      System.out.println(listaAlumnos.get(0));

      if(alumnos==null) {
        alumnos = new ArrayList<>();
      }
      else{
        alumnos.clear();
      }
      for (int i = 0; i < listaAlumnos.length(); ++i) {
        JSONObject jsonUser = listaAlumnos.getJSONObject(i);
        Alumno alumno = new Alumno();
        alumno.fromJSON(jsonUser);
        alumnos.add(alumno);
      }

      alumnosAdapter = new AlumnosAdapter(this, alumnos);
      alumnosAdapter.setCallback(this);
      listView.setAdapter(alumnosAdapter);

      ProgressBar pbMain = findViewById(R.id.pb_main);
      pbMain.setVisibility(View.GONE);
      TextView noData = findViewById(R.id.tv_no_data);
      noData.setVisibility(View.GONE);
    }
    catch (JSONException | ParseException e) {
      showError(e.getMessage());
    }
  }


//  @Override
//  public void verLibrosPressed(int position) {
//    Intent myIntent = new Intent().setClass(this, AlumnoActivity.class);
//    myIntent.putExtra("position", position);
//    nuevoResultLauncher.launch(myIntent);
//  }
}