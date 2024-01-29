package com.example.exud5;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
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

import com.example.exud5.entidades.Parametro;
import com.example.exud5.util.Internetop;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Nuevo extends AppCompatActivity {
    private EditText etFecha;
    private DatePickerDialog dpd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo);
        String sfecha="";
        if(savedInstanceState!=null) {
            if(savedInstanceState.containsKey("fecha")){
                sfecha=savedInstanceState.getString("fecha");
            }
        }
        Date nacimiento;
        if(sfecha.isEmpty()){
            nacimiento=new Date();
        }
        else {
            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
            try {
                nacimiento = dateFormatter.parse(sfecha);
            } catch (ParseException e) {
                e.printStackTrace();
                nacimiento = new Date();
            }
        }
        prepararFecha(nacimiento);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(etFecha==null){
            etFecha=findViewById(R.id.et_nuevo_date);
        }
        String sfecha=etFecha.getText().toString();
        if(!sfecha.isEmpty()){
            outState.putString("fecha", sfecha);
        }
    }

    private void prepararFecha(Date nacimiento){
        etFecha= findViewById(R.id.et_nuevo_date);
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

    public void aceptar(View view) {
        EditText textNombre = findViewById(R.id.et_nuevo_nombre);
        if(etFecha==null) {
            etFecha = findViewById(R.id.et_nuevo_date);
        }
        RadioGroup rgTipo = findViewById(R.id.rg_nuevo_tipo);
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
            if(rbsel==R.id.rb_nuevo_social){
                tipo=1;
            }
            else if(rbsel==R.id.rb_nuevo_medical){
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
            Button btAceptar= findViewById(R.id.bt_nuevo);
            ProgressBar pbAceptar= findViewById(R.id.pb_nuevo);
            //Desactivamos el botón y mostramos la barra de progreso
            btAceptar.setEnabled(false);
            btAceptar.setClickable(false);
            pbAceptar.setVisibility(View.VISIBLE);
            if (isNetworkAvailable()) {
                String url = res.getString(R.string.main_url) + "nuevoEvento";
                if(dfecha==null){
                    fecha="";
                }
                sendTask(url, tipo+"", nombre, fecha);
            } else {
                showError("error.IOException");
            }
        }
    }

    private void sendTask(String url, String tipo, String nombre, String fecha) {
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
                String result = interopera.postText(url,params);
                handler.post(new Runnable() {/*Una vez handler recoge el resultado de la tarea en
                segundo plano, hacemos los cambios pertinentes en la interfaz de usuario en función
                del resultado obtenido*/
                    @Override
                    public void run() {
                        Button btAceptar= findViewById(R.id.bt_nuevo);//Volvemos a activar el botón aceptar
                        ProgressBar pbAceptar=(ProgressBar) findViewById(R.id.pb_nuevo);
                        pbAceptar.setVisibility(View.GONE);//Ocultamos la barra de progreso
                        btAceptar.setEnabled(true);
                        btAceptar.setClickable(true);
                        long idCreado;
                        try{//Recuperamos desde el resultado el nuevo ID del objeto que se ha creado
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