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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.exud5.entidades.Evento;
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
    private EditText etFecha;
    private DatePickerDialog dpd;
    private long idEvento;
    private String sfecha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificar);

        if(savedInstanceState!=null){/*Si hemos hecho onSaveInstanceState previamente y hemos almacenado información,
        la recuperamos*/
            sfecha = savedInstanceState.getString("snacimiento","");
            idEvento = savedInstanceState.getLong("idEvento",-1);
        }
        else{//Si es la primera vez que se crea la actividad, obtenemos la información que pasamos a través del Intent
            sfecha="";
            Intent intent = getIntent();
            idEvento = intent.getLongExtra("idEvento", -1);
        }
        //Cargamos la información del usuario que vamos a modificar
        cargarUsuario(idEvento);
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

    private void cargarUsuario(long idUsuario) {
        if (isNetworkAvailable()) {
            ProgressBar pbMain = (ProgressBar) findViewById(R.id.pb_modificar);
            pbMain.setVisibility(View.VISIBLE);
            Resources res = getResources();
            String url = res.getString(R.string.main_url) + "getEvento" + idUsuario;
            getUsuarioTask(url);
        }
        else{
            showError("error.IOException");
        }
    }

    private void getUsuarioTask(String url){
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
                            //Si hemos obtenido el usuario, cargamos su información en la interfaz
                            fillInformation(result);
                        }
                    }
                });
            }
        });
    }

    private void fillInformation(String result){
        try {
            //En lugar de obtener un array de objetos JSON, obtenemos un único objeto JSON
            JSONObject jsonUser=new JSONObject(result);
            Evento evento = new Evento();
            evento.fromJSON(jsonUser);//Inicializamos nuestra clase con la función fromJSON que creamos
            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
            Date fecha=null;
            if(sfecha.isEmpty()){/*Si la fecha en formato String está vacía, es porque no la seleccionamos antes desde
            el widget de calendario, por lo que debemos darle el valor que tenga la fecha de nacimiento del usuario*/
                if(evento!=null){
                    fecha=evento.getFecha();
                }
                if(fecha==null) {/*Puede ser que el usuario no tuviera fecha de nacimiento, así que le damos el valor
                del día de hoy para mostrarlo en el calendario cuando se abra*/
                    fecha = new Date();
                }
            }
            else {
                try {
                    fecha = dateFormatter.parse(sfecha);
                } catch (ParseException e) {
                    e.printStackTrace();
                    fecha = new Date();
                }
            }
            prepararFecha(fecha);
            //Rellenar la información
            EditText textNombre = findViewById(R.id.et_modificar_nombre);
            if(etFecha==null) {
                etFecha = findViewById(R.id.et_modificar_date);
            }
            RadioButton rbSocial = findViewById(R.id.rb_modificar_social);
            RadioButton rbMedical = findViewById(R.id.rb_modificar_medical);
            RadioButton rbProfesional = findViewById(R.id.rb_modificar_profesional);
            rbSocial.setChecked(false);
            rbProfesional.setChecked(false);
            rbMedical.setChecked(false);
            switch (evento.getTipo()){
                case 1: rbSocial.setChecked(true);
                    break;
                case 2: rbMedical.setChecked(true);
                    break;
                default: rbProfesional.setChecked(true);
                    break;
            }
            if(evento.getFecha()!=null){
                etFecha.setText(evento.getsFecha());
            }
            textNombre.setText(evento.getNombre());
            //Al terminar de rellenar la información ocultamos la barra de progreso
            ProgressBar pbMain = (ProgressBar) findViewById(R.id.pb_modificar);
            pbMain.setVisibility(View.GONE);
        } catch (JSONException | ParseException e) {
            showError("error.json");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(etFecha==null){
            etFecha=findViewById(R.id.et_modificar_date);
        }
        String sfecha=etFecha.getText().toString();
        if(!sfecha.isEmpty()){
            outState.putString("fecha", sfecha);
        }
        outState.putLong("idEvento",idEvento);
    }

    private void prepararFecha(Date nacimiento){
        etFecha= findViewById(R.id.et_modificar_date);
        etFecha.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    dpd.show();
                }
                v.clearFocus();
            }
        });
        Calendar newCalendar = Calendar.getInstance();
        newCalendar.setTime(nacimiento);
        dpd = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
                etFecha.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    public void aceptar(View view) {
        EditText textNombre = findViewById(R.id.et_modificar_nombre);
        if(etFecha==null) {
            etFecha = findViewById(R.id.et_modificar_date);
        }
        RadioGroup rgTipo = findViewById(R.id.rg_modificar_tipo);
        //Conseguir valores
        String nombre=textNombre.getText().toString();
        String fecha=etFecha.getText().toString();
        int rbsel = rgTipo.getCheckedRadioButtonId();
        //Validación
        Resources res = getResources();
        boolean continuar=true;
        if(nombre.isEmpty()){
            textNombre.setError(res.getString(R.string.campo_obligatorio));
            continuar=false;
        }
        int tipo=-1;
        if(rbsel>=0){
            if(rbsel==R.id.rb_modificar_social){
                tipo=1;
            }
            else if(rbsel==R.id.rb_modificar_medical){
                tipo=2;
            }
            else{
                tipo=3;
            }
        }
        Date dfecha=null;
        if(!fecha.isEmpty()){
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
            try {
                dfecha=sdf.parse(fecha);
            } catch (ParseException e) {
                e.printStackTrace();
                etFecha.setError(res.getString(R.string.incorrect_date_format));
                continuar=false;
            }
        }
        if(continuar){
            Button btAceptar= findViewById(R.id.bt_modificar);
            ProgressBar pbAceptar= findViewById(R.id.pb_modificar);
            btAceptar.setEnabled(false);
            btAceptar.setClickable(false);
            pbAceptar.setVisibility(View.VISIBLE);
            if (isNetworkAvailable()) {
                String url = res.getString(R.string.main_url) + "actualizarEvento";
                if(dfecha==null){
                    fecha="";
                }
                sendTask(url, tipo+"", nombre, fecha, idEvento+"");
            } else {
                showError("error.IOException");
            }
        }
    }

    private void sendTask(String url, String tipo, String nombre, String fecha, String idEvento) {
        //La clase Executor será la encargada de lanzar un nuevo hilo en background con la tarea
        ExecutorService executor = Executors.newSingleThreadExecutor();
        //Handler es la clase encargada de manejar el resultado de la tarea ejecutada en segundo plano
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(new Runnable() {//Ejecutamos el nuevo hilo
            @Override
            public void run() {
                /*Aquí ejecutamos el código en segundo plano, que consiste en obtener del servidor
                 * la lista de usuarios*/
                Internetop interopera=Internetop.getInstance();
                List<Parametro> params = new ArrayList<>();
                params.add(new Parametro("tipo", tipo));
                params.add(new Parametro("nombre", nombre));
                params.add(new Parametro("fecha", fecha));
                params.add(new Parametro("idEvento", idEvento));
                String result = interopera.putText(url,params);
                handler.post(new Runnable() {/*Una vez handler recoge el resultado de la tarea en
                segundo plano, hacemos los cambios pertinentes en la interfaz de usuario en función
                del resultado obtenido*/
                    @Override
                    public void run() {
                        Button btAceptar= findViewById(R.id.bt_modificar);//Volvemos a activar el botón aceptar
                        ProgressBar pbAceptar=(ProgressBar) findViewById(R.id.pb_modificar);
                        pbAceptar.setVisibility(View.GONE);//Ocultamos la barra de progreso
                        btAceptar.setEnabled(true);
                        btAceptar.setClickable(true);
                        long idCreado;
                        try{//Recuperamos desde el resultado el nuevo ID del objeto que se ha modificado
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