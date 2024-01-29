package com.example.exud5;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.widget.Toast;

import com.example.exud5.entidades.Disco;
import com.example.exud5.util.DiscosAdapter;
import com.example.exud5.util.Internetop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements DiscosAdapter.DiscosAdapterCallback{

  private ListView listView;
  private ArrayList<Disco> discos;
  private DiscosAdapter discosAdapter;
  ActivityResultLauncher<Intent> nuevoResultLauncher = registerForActivityResult(
      new ActivityResultContracts.StartActivityForResult(),
      new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
          if (result.getResultCode() == Activity.RESULT_OK) {
            cargarDiscos();
          }
        }
      });

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    listView= findViewById(R.id.lv_main);
    cargarDiscos();
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

  private void cargarDiscos() {
    if (isNetworkAvailable()) {
      //Mostramos la barra de progreso
      ProgressBar pbMain = (ProgressBar) findViewById(R.id.pb_main);
      pbMain.setVisibility(View.VISIBLE);
      Resources res = getResources();
      //Llamamos a un método asíncrono con la url para realizar la conexión con el servidor
      String url = res.getString(R.string.main_url) + "listaDiscos";
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
              resetLista(result);
            }
          }
        });
      }
    });
  }

  private void resetLista(String result){
    try {
      JSONArray listaDiscos = new JSONArray(result);
      if(discos==null) {
        discos = new ArrayList<>();
      }
      else{
        discos.clear();
      }
      for (int i = 0; i < listaDiscos.length(); ++i) {
        JSONObject jsonUser = listaDiscos.getJSONObject(i);
        Disco disco = new Disco();
        disco.fromJSON(jsonUser);
        discos.add(disco);
      }
      if(discosAdapter==null) {
        discosAdapter = new DiscosAdapter(this, discos);
        discosAdapter.setCallback(this);
        listView.setAdapter(discosAdapter);
      }
      else{
        discosAdapter.notifyDataSetChanged();
      }
      ProgressBar pbMain = findViewById(R.id.pb_main);
      pbMain.setVisibility(View.GONE);
    }
    catch (JSONException | ParseException e) {
      showError(e.getMessage());
    }
  }

  public void irNuevo(View view) {
    Intent intent = new Intent(this, Nuevo.class);
    nuevoResultLauncher.launch(intent);
  }

  @Override
  public void deletePressed(int position) {
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
            eliminarDisco(position);
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

  private void eliminarDisco(int position){
    if(discos!=null){
      if(discos.size()>position) {
        Disco disco = discos.get(position);
        if (isNetworkAvailable()) {
          ProgressBar pbMain = (ProgressBar) findViewById(R.id.pb_main);
          pbMain.setVisibility(View.VISIBLE);
          Resources res = getResources();
          String url = res.getString(R.string.main_url) + "eliminarDisco" + disco.getIdDisco();
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
              ProgressBar pbMain = (ProgressBar) findViewById(R.id.pb_main);
              pbMain.setVisibility(View.GONE);
              cargarDiscos();
            }
          }
        });
      }
    });
  }

  @Override
  public void editPressed(int position) {
    if(discos!=null) {
      if (discos.size() > position) {
        Disco disco=discos.get(position);
        Intent myIntent = new Intent().setClass(this, Modificar.class);
        myIntent.putExtra("idDisco",disco.getIdDisco());
        nuevoResultLauncher.launch(myIntent);
      }
    }
  }
}