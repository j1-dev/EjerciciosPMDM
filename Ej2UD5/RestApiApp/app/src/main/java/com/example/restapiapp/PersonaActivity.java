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

import com.example.restapiapp.adaptadores.TiendasAdapter;
import com.example.restapiapp.entidades.Tienda;
import com.example.restapiapp.util.DBHelper;
import com.example.restapiapp.util.Internetop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PersonaActivity extends AppCompatActivity implements TiendasAdapter.TiendasAdapterCallback {
  private int position;
  private int idPersona;
  private DBHelper dbHelper;
  private ArrayList<Tienda> tiendas;
  private TiendasAdapter tiendasAdapter;
  private ListView listView;
  private final ActivityResultLauncher<Intent> nuevoResultLauncher = registerForActivityResult(
      new ActivityResultContracts.StartActivityForResult(),
      new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
          if (result.getResultCode() == Activity.RESULT_OK) {
            cargarTiendas();
          }
        }
      });

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_persona);
    if(savedInstanceState!=null){
      position = savedInstanceState.getInt("position",-1);
      idPersona = savedInstanceState.getInt("idPersona",-1);
    }
    else{
      Intent intent = getIntent();
      position = intent.getIntExtra("position", -1);
      idPersona = intent.getIntExtra("idPersona",-1);
    }
    dbHelper=DBHelper.getInstance(this);
    listView=findViewById(R.id.lv_tiendas);
    cargarTiendas();
  }

  public void irNuevoTienda(View view){
    Intent myIntent = new Intent().setClass(this, NuevoTienda.class);
    myIntent.putExtra("position", position);
    myIntent.putExtra("idPersona", idPersona);
    nuevoResultLauncher.launch(myIntent);
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
      System.out.println(error);
      message = res.getString(R.string.error_unknown);
      duration = Toast.LENGTH_SHORT;
    }
    Toast toast = Toast.makeText(this, message, duration);
    toast.show();
  }

  private void cargarTiendas(){
    if (isNetworkAvailable()) {
      ProgressBar pbMain = (ProgressBar) findViewById(R.id.pb_tiendas);
      pbMain.setVisibility(View.VISIBLE);
      Resources res = getResources();
      String url = res.getString(R.string.tienda_url) + "listaTiendas" + (idPersona) ;
      System.out.println(url);
      getListaTask(url);
    }
    else{
      showError("error.IOException");
    }
  }

  private void getListaTask(String url) {
    ExecutorService executor = Executors.newSingleThreadExecutor();
    Handler handler = new Handler(Looper.getMainLooper());
    executor.execute(new Runnable() {
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
      JSONArray listaTiendas = new JSONArray(result);

      if(tiendas==null) {
        tiendas = new ArrayList<>();
      }
      else{
        tiendas.clear();
      }
      for (int i = 0; i < listaTiendas.length(); ++i) {
        JSONObject jsonObject = listaTiendas.getJSONObject(i);
        Tienda tienda = new Tienda();
        tienda.fromJSON(jsonObject);
        System.out.println(tienda.toJSON().toString());
        tiendas.add(tienda);
      }

      tiendasAdapter = new TiendasAdapter(this,tiendas);
      tiendasAdapter.setCallback(this);
      listView.setAdapter(tiendasAdapter);

      ProgressBar pbMain = findViewById(R.id.pb_tiendas);
      pbMain.setVisibility(View.GONE);
      TextView noData = findViewById(R.id.tv_empty_tiendas);
      noData.setVisibility(View.GONE);
      if(tiendas.size() == 0) {
        noData.setVisibility(View.VISIBLE);
      }
    }
    catch (JSONException e) {
      showError(e.getMessage());
    }
  }
  public void back(View view){
    this.finish();
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
            eliminarTienda(position);
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

  private void eliminarTienda(int position){
    if(tiendas!=null){
      if(tiendas.size()>position) {
        Tienda tienda = tiendas.get(position);
        if (isNetworkAvailable()) {
          ProgressBar pbMain = (ProgressBar) findViewById(R.id.pb_tiendas);
          pbMain.setVisibility(View.VISIBLE);
          Resources res = getResources();
          String url = res.getString(R.string.tienda_url) + "eliminarTienda" + tienda.getIdTienda();
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
              ProgressBar pbMain = (ProgressBar) findViewById(R.id.pb_tiendas);
              pbMain.setVisibility(View.GONE);
              cargarTiendas();
            }
          }
        });
      }
    });
  }

  @Override
  public void editarPressed(int position) throws JSONException {
    if(tiendas!=null) {
      if (tiendas.size() > position) {
        Tienda tienda=tiendas.get(position);
        tiendas.forEach(tienda1 -> {
          try {
            System.out.println(tienda1.toJSON().toString());
          } catch (JSONException e) {
            throw new RuntimeException(e);
          }
        });
        Intent myIntent = new Intent().setClass(this, ModificarTienda.class);
        myIntent.putExtra("tienda", tienda.toJSON().toString());
        nuevoResultLauncher.launch(myIntent);
      }
    }
  }
}