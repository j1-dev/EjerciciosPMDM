package com.example.restapiapp;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.Toast;

import com.example.restapiapp.entitidades.Usuario;
import com.example.restapiapp.util.Internetop;
import com.example.restapiapp.util.UsuariosAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements UsuariosAdapter.UsuariosAdapterCallback{

    private ListView listView;
    private ArrayList<Usuario> usuarios;
    private UsuariosAdapter usuariosAdapter;
    ActivityResultLauncher<Intent> nuevoResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        cargarUsuarios();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView= findViewById(R.id.lv_main);
        cargarUsuarios();
    }

    private Boolean isNetworkAvailable() {
        /*La clase ConnectivityManager nos devolverá información sobre el estado de la conexión a
         * Internet. Puede ser que no tengamos cobertura o que directamente no tengamos activado
         * la red WiFi o la red de datos móviles*/
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            /*Si la versión de Android es superior a Android M, debemos usar las clase Network
            * en lugar de NetworkInfo para comprobar la conectividad*/
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

    private void cargarUsuarios() {
        if (isNetworkAvailable()) {
            //Mostramos la barra de progreso
            ProgressBar pbMain = (ProgressBar) findViewById(R.id.pb_main);
            pbMain.setVisibility(View.VISIBLE);
            Resources res = getResources();
            //Llamamos a un método asíncrono con la url para realizar la conexión con el servidor
            String url = res.getString(R.string.main_url) + "listaUsuarios";
            getListaTask(url);
        }
        else{
            showError("error.IOException");
        }
    }

    private void getListaTask(String url) {
        //La clase Executor será la encargada de lanzar un nuevo hilo en background con la tarea
        ExecutorService executor = Executors.newSingleThreadExecutor();
        //Handler es la clase encargada de manejar el resultado de la tarea ejecutada en segundo plano
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(new Runnable() {//Ejecutamos el nuevo hilo
            @Override
            public void run() {
                /*Aquí ejecutamos el código en segundo plano, que consiste en obtener del servidor
                * la lista de usuarios*/
                Internetop interopera= Internetop.getInstance();
                String result = interopera.getString(url);
                handler.post(new Runnable() {/*Una vez handler recoge el resultado de la tarea en
                segundo plano, hacemos los cambios pertinentes en la interfaz de usuario en función
                del resultado obtenido*/
                    @Override
                    public void run() {
                        if(result.equalsIgnoreCase("error.IOException")||
                                result.equals("error.OKHttp")) {
                            /*Si se ha producido un error o el resultado no es el esperado, mostramos
                            * el mensaje correspondiente*/
                            showError(result);
                        }
                        else if(result.equalsIgnoreCase("null")){
                            showError("error.desconocido");
                        }
                        else{
                            //Si hemos obtenido la lista de usuarios, la recargamos en la interfaz
                            resetLista(result);
                        }
                    }
                });
            }
        });
    }

    private void resetLista(String result){
        try {
            /*El resultado devuelto por la conexión es una cadena de texto que contiene información en formato
            * JSON. Conviene hacer debug y ver el tipo de información devuelta para establecer las clases más
            * adecuadas para transformar el texto en información útil (esa información en formato texto dependerá
            * de la aplicación usada en el servidor). En este caso devuelve un array de objetos JSON de tipo
            * usuario*/
            JSONArray listaUsuarios = new JSONArray(result);//Primero transformamos el texto en el array de JSON
            if(usuarios==null) {/*Inicializamos la lista de usuarios. Si no estuviera creada obtenemos una nueva
                instancia*/
                usuarios = new ArrayList<>();
            }
            else{//Si la lista está ya creada la vaciamos
                usuarios.clear();
            }
            for (int i = 0; i < listaUsuarios.length(); ++i) {//Recorremos la lista de objetos JSON
                JSONObject jsonUser = listaUsuarios.getJSONObject(i);/*Cada elemento del array JSON se obtiene
                como un objeto JSONObject*/
                Usuario usuario = new Usuario();//Creamos una nueva instancia de la clase usuario
                usuario.fromJSON(jsonUser);/*Aquí hacemos uso del método fromJSON que creamos anteriormente en
                nuestra clase Usuario*/
                usuarios.add(usuario);//Añadimos el usuario a la lista de usuarios
            }
            if(usuariosAdapter==null) {//Si no está creado el adaptador del listView lo creamos
                usuariosAdapter = new UsuariosAdapter(this, usuarios);
                usuariosAdapter.setCallback(this);/*Establecemos que esta actividad implementa los métodos
                de UsuariosAdapterCallback*/
                listView.setAdapter(usuariosAdapter);//Indicamos al listView quién es su adaptador
            }
            else{//Si el adaptador ya estaba creado, sólo indicamos que los datos se han actualizado
                usuariosAdapter.notifyDataSetChanged();
            }
            ProgressBar pbMain = findViewById(R.id.pb_main);
            pbMain.setVisibility(View.GONE);//Ocultamos la barra de progreso
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
        diaBox.show();//Mostramos un diálogo de confirmación
    }

    private AlertDialog AskOption(final int position)
    {
        AlertDialog myQuittingDialogBox =new AlertDialog.Builder(this)
                /*Establecemos el mensaje, el título del diálogo y los botones de confirmación
                 * y cancelación*/
                .setTitle(R.string.eliminar_usuario)
                .setMessage(R.string.are_you_sure)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        eliminarUsuario(position);//Si confirmamos eliminamos el usuario
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();//En caso contrario ocultamos el diálogo
                    }
                })
                .create();
        return myQuittingDialogBox;
    }

    private void eliminarUsuario(int position){
        if(usuarios!=null){//Comprobamos que la lista de usuarios está creada
            if(usuarios.size()>position) {//Comprobamos que la posición es correcta
                Usuario usuario = usuarios.get(position);/*Obtenemos el alumno que se corresponde
                    con esa posición*/
                if (isNetworkAvailable()) {
                    ProgressBar pbMain = (ProgressBar) findViewById(R.id.pb_main);
                    pbMain.setVisibility(View.VISIBLE);
                    Resources res = getResources();
                    String url = res.getString(R.string.main_url) + "eliminarUsuario" + usuario.getIdUsuario();
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
        //La clase Executor será la encargada de lanzar un nuevo hilo en background con la tarea
        ExecutorService executor = Executors.newSingleThreadExecutor();
        //Handler es la clase encargada de manejar el resultado de la tarea ejecutada en segundo plano
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(new Runnable() {//Ejecutamos el nuevo hilo
            @Override
            public void run() {
                /*Aquí ejecutamos el código en segundo plano, que consiste en obtener del servidor
                 * la lista de usuarios*/
                Internetop interopera= Internetop.getInstance();
                String result = interopera.deleteTask(url);
                handler.post(new Runnable() {/*Una vez handler recoge el resultado de la tarea en
                segundo plano, hacemos los cambios pertinentes en la interfaz de usuario en función
                del resultado obtenido*/
                    @Override
                    public void run() {
                        if(result.equalsIgnoreCase("error.IOException")||
                                result.equals("error.OKHttp")) {//Controlamos los posibles errores
                            showError(result);
                        }
                        else if(result.equalsIgnoreCase("null")){
                            showError("error.desconocido");
                        }
                        else{/*Si todo ha ido bien, recargamos la lista de usuarios*/
                            ProgressBar pbMain = (ProgressBar) findViewById(R.id.pb_main);
                            pbMain.setVisibility(View.GONE);
                            cargarUsuarios();
                        }
                    }
                });
            }
        });
    }

    @Override
    public void editPressed(int position) {
        if(usuarios!=null) {//Comprobamos que la lista de usuarios está creada
            if (usuarios.size() > position) {//Comprobamos que la posición es correcta
                Usuario usuario=usuarios.get(position);//Obtenemos el usuario que se corresponde con esta posición
                Intent myIntent = new Intent().setClass(this, Modificar.class);
                myIntent.putExtra("idUsuario",usuario.getIdUsuario());//Pasamos el identificador del usuario como argumento
                nuevoResultLauncher.launch(myIntent);
            }
        }
    }
}