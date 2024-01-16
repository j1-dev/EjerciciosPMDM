package com.example.restapiapp;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.restapiapp.adaptadores.PersonasAdapter;
import com.example.restapiapp.entidades.Persona;
import com.example.restapiapp.util.DBHelper;
import com.example.restapiapp.util.Internetop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements PersonasAdapter.PersonasAdapterCallback {

  private DBHelper dbHelper;
  private ArrayList<Persona> personas;
  private PersonasAdapter personasAdapter;
  private ListView listView;
  private final ActivityResultLauncher<Intent> nuevoResultLauncher = registerForActivityResult(
      new ActivityResultContracts.StartActivityForResult(),
      new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
          if (result.getResultCode() == Activity.RESULT_OK) {
            cargarPersonas();
          }
        }
      });

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    dbHelper=DBHelper.getInstance(this);
    listView=findViewById(R.id.lv_personas);
    cargarPersonas();
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

  public void irNuevoPersona(View view){
    Intent myIntent = new Intent().setClass(this, NuevoPersona.class);
    nuevoResultLauncher.launch(myIntent);
  }

  private void cargarPersonas(){
    if (isNetworkAvailable()) {
      ProgressBar pbMain = (ProgressBar) findViewById(R.id.pb_personas);
      pbMain.setVisibility(View.VISIBLE);
      Resources res = getResources();
      String url = res.getString(R.string.persona_url) + "listaPersonas";
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
    try {
      JSONArray listaPersonas = new JSONArray(result);

      if(personas==null) {
        personas = new ArrayList<>();
      }
      else{
        personas.clear();
      }
      for (int i = 0; i < listaPersonas.length(); ++i) {
        JSONObject jsonObject = listaPersonas.getJSONObject(i);
        Persona persona = new Persona();
        persona.fromJSON(jsonObject);
        personas.add(persona);
      }

      personasAdapter = new PersonasAdapter(this, personas);
      personasAdapter.setCallback(this);
      listView.setAdapter(personasAdapter);

      ProgressBar pbMain = findViewById(R.id.pb_personas);
      pbMain.setVisibility(View.GONE);
      TextView noData = findViewById(R.id.tv_empty_personas);
      noData.setVisibility(View.GONE);
      if(personas.size() == 0) {
        noData.setVisibility(View.VISIBLE);
      }
    }
    catch (JSONException e) {
      showError(e.getMessage());
    }
  }


  @Override
  public void verTiendasPressed(int position) {
    Intent myIntent = new Intent().setClass(this, PersonaActivity.class);
    myIntent.putExtra("position", position);
    myIntent.putExtra("idPersona", personas.get(position).getIdPersona());
    nuevoResultLauncher.launch(myIntent);
  }

  @Override
  public void eliminarPressed(int position) {
    AlertDialog diaBox = AskOption(position);
    diaBox.show();
  }

  private AlertDialog AskOption(final int position)
  {
    AlertDialog myQuittingDialogBox =new AlertDialog.Builder(this)
        .setTitle(R.string.eliminar_usuario)
        .setMessage(R.string.are_you_sure)
        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int whichButton) {
            eliminarPersona(position);
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

  private void eliminarPersona(int position){
    if(personas!=null){
      if(personas.size()>position) {
        Persona persona = personas.get(position);
        if (isNetworkAvailable()) {
          ProgressBar pbMain = (ProgressBar) findViewById(R.id.pb_personas);
          pbMain.setVisibility(View.VISIBLE);
          Resources res = getResources();
          String url = res.getString(R.string.persona_url) + "eliminarPersona" + persona.getIdPersona();
          eliminarTask(url);
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

  private void eliminarTask(String url){
    ExecutorService executor = Executors.newSingleThreadExecutor();
    Handler handler = new Handler(Looper.getMainLooper());
    executor.execute(new Runnable() {
      @Override
      public void run() {
        Internetop interopera= Internetop.getInstance();
        String result = interopera.deleteTask(url);
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
              ProgressBar pbMain = (ProgressBar) findViewById(R.id.pb_personas);
              pbMain.setVisibility(View.GONE);
              cargarPersonas();
            }
          }
        });
      }
    });
  }

  @Override
  public void editarPressed(int position) throws JSONException {
    if(personas!=null) {
      if (personas.size() > position) {
        Persona persona=personas.get(position);
        Intent myIntent = new Intent().setClass(this, ModificarPersona.class);
        myIntent.putExtra("persona",persona.toJSON().toString());
        nuevoResultLauncher.launch(myIntent);
      }
    }
  }
}