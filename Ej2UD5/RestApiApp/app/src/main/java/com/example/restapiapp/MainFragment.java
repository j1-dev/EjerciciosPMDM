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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.restapiapp.adaptadores.PersonasAdapter;
import com.example.restapiapp.entidades.Persona;
import com.example.restapiapp.util.Internetop;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainFragment extends Fragment implements PersonasAdapter.PersonasAdapterCallback {
  private View thisView;
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
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    thisView = inflater.inflate(R.layout.fragment_main, container, false);
    listView = thisView.findViewById(R.id.lv_personas);
    cargarPersonas();
    FloatingActionButton fabNuevoPersona = thisView.findViewById(R.id.fab_nuevo_persona);
    fabNuevoPersona.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        irNuevoPersona(v);
      }
    });
    return thisView;
  }

  private Boolean isNetworkAvailable() {
    ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
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
    Toast toast = Toast.makeText(thisView.getContext(), message, duration);
    toast.show();
  }

  public void irNuevoPersona(View view){
    System.out.println("aaaaaaaaa");
    Intent myIntent = new Intent().setClass(thisView.getContext(), NuevoPersona.class);
    nuevoResultLauncher.launch(myIntent);
  }

  private void cargarPersonas(){
    if (isNetworkAvailable()) {
      ProgressBar pbMain = (ProgressBar) thisView.findViewById(R.id.pb_personas);
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

      personasAdapter = new PersonasAdapter(thisView.getContext(), personas);
      personasAdapter.setCallback(this);
      listView.setAdapter(personasAdapter);

      ProgressBar pbMain = thisView.findViewById(R.id.pb_personas);
      pbMain.setVisibility(View.GONE);
      TextView noData = thisView.findViewById(R.id.tv_empty_personas);
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
  public void eliminarPressed(int position) {
    AlertDialog diaBox = AskOption(position);
    diaBox.show();
  }

  private AlertDialog AskOption(final int position)
  {
    AlertDialog myQuittingDialogBox =new AlertDialog.Builder(thisView.getContext())
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
          ProgressBar pbMain = (ProgressBar) thisView.findViewById(R.id.pb_personas);
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
              ProgressBar pbMain = (ProgressBar) thisView.findViewById(R.id.pb_personas);
              pbMain.setVisibility(View.GONE);
              cargarPersonas();
            }
          }
        });
      }
    });
  }

  public static MainFragment newInstance(String param1, String param2) {
    MainFragment fragment = new MainFragment();
    Bundle args = new Bundle();
    args.putString("param1", param1);
    args.putString("param2", param2);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void editarPressed(int position) throws JSONException {
    if(personas!=null) {
      if (personas.size() > position) {
        Persona persona=personas.get(position);
        Intent myIntent = new Intent().setClass(thisView.getContext(), ModificarPersona.class);
        myIntent.putExtra("persona",persona.toJSON().toString());
        nuevoResultLauncher.launch(myIntent);
      }
    }
  }
}