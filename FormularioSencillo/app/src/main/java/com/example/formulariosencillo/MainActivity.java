package com.example.formulariosencillo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private DatePickerDialog dpdNacimiento;
    private EditText etNacimiento;
    private String snacimiento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState!=null){
            /*Si la actividad se ha recreado por un cambio en la orientación de la pantalla, posiblemente
            hayamos guardado en el SaveInstanceState alguna información que ahora queramos recuperar. En este
            caso se supone que hemos guardado el valor en String de la fecha de nacimiento del usuario*/
            snacimiento = savedInstanceState.getString("snacimiento","");
        }
        else{
            /*Si saveInstanceState es nulo es porque la actividad se está creando por primera vez y no hemos
            almacenado previamente información en saveInstanceState, por lo que la fecha en formato String será
            una cadena vacía*/
            snacimiento="";
        }
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd MM yyyy");
        Date fnacimiento;
        if(snacimiento.isEmpty()){
            //Si no se había establecido una fecha previamente, la fecha que mostrará el calendario de selección de fecha será la de hoy
            fnacimiento=new Date();
        }
        else {
            //En caso de que SÍ hubiera una fecha establecida previamente, esa fecha será la que muestre el calendario de selección de fecha
            try {
                fnacimiento = dateFormatter.parse(snacimiento);
            } catch (ParseException e) {
                e.printStackTrace();
                fnacimiento = new Date();
            }
        }
        prepararFecha(fnacimiento);
        populateSpinners();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        /*Cuando se produce por ejemplo un cambio de orientación que provocará que la actividad se vuelva a crear (es la forma que tiene Android de volver
        * a dibujar el diseño adaptado a la nueva orientación) para no perder la información que tengamos en memoria sobreescribimos este método, guardando
        * en un "manojo" las variables que queramos conservar*/
        if(etNacimiento==null){
            etNacimiento=findViewById(R.id.et_fecha_nacimiento);
        }
        snacimiento=etNacimiento.getText().toString();
        //Es el valor del campo fecha de nacimiento el que queremos salvar
        outState.putString("snacimiento", snacimiento);
    }

    private void prepararFecha(Date inicio){
        if(etNacimiento==null) {
            //Inicializamos la variable del campo de texto fecha nacimiento recuperándola desde la interfaz
            etNacimiento = findViewById(R.id.et_fecha_nacimiento);
        }
        etNacimiento.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            /*Sobreescribimos el evento que se dispara cuando el campo de texto de la fecha obtiene el foco
            para que nos muestre el diálogo con el calendario que usaremos para seleccionar la fecha*/
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {//Si el campo de texto fecha tiene el foco mostramos el diálogo con el calendario
                    dpdNacimiento.show();
                }
                v.clearFocus();
            }
        });
        Calendar newCalendar = Calendar.getInstance();
        newCalendar.setTime(inicio);/*La fecha que recibe este método como argumento será la que se muestre
        marcada por defecto en el calendario*/
        dpdNacimiento = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);/*Establecemos el año, mes y día en el objeto de
                tipo calendar*/
                SimpleDateFormat dateFormatter = new SimpleDateFormat("dd MM yyyy");/*El formato de
                fecha que mostrará el campo de texto es día mes año*/
                etNacimiento.setText(dateFormatter.format(newDate.getTime()));/*Escribimos en el campo de texto
                etNacimiento la fecha que ha seleccionado el usuario con el formato descrito anteriormente*/
            }
        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH),
                newCalendar.get(Calendar.DAY_OF_MONTH));
        /*Al construir un objeto del tipo DatePickerDialog le pasamos como argumentos el contexto (que es
        * nuestra actividad), el evento sobreescrito que se dispara cuando se selecciona una fecha en el
        * calendario, y el año, mes y día del mes que deben aparecer marcados por defecto en el calendario*/
    }

    private void populateSpinners(){
        Resources res = getResources();
        Spinner sestudios = (Spinner) findViewById(R.id.s_estudios);/*Obtenemos desde la interfaz la referencia
        a la lista desplegable*/
        ArrayList<String> estudios = new ArrayList<>();//Creamos un arraylist vacío
        estudios.add(res.getString(R.string.nivel_estudios));/*Añadimos al arraylist los valores que queremos
        que la lista desplegable muestre*/
        estudios.add(res.getString(R.string.secondary_education));
        estudios.add(res.getString(R.string.baccalaureate));
        estudios.add(res.getString(R.string.grado));
        estudios.add(res.getString(R.string.postgrado));
        ArrayAdapter<String> adestudios = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,
                estudios);//Necesitamos un adaptador para mostrar los valores anteriores en la lista desplegable
        adestudios.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);/*Aquí indicamos
        el diseño gráfico que van a tener los distintos elementos de la lista desplegable*/
        sestudios.setAdapter(adestudios);//Y aquí indicamos cuál es el adaptador que va a usar la lista desplegable
    }

    public void aceptar(View view) {
        //Recuperamos desde la interfaz todos los widgets
        TextView tvRes=findViewById(R.id.tv_answer);
        tvRes.setText("");
        EditText etNombre = findViewById(R.id.et_name);
        if(etNacimiento==null){
            etNacimiento= findViewById(R.id.et_fecha_nacimiento);
        }
        CheckBox cbDatos = findViewById(R.id.cb_data_consent);
        RadioGroup rgGenero = findViewById(R.id.rg_genero);
        Spinner sEstudios = (Spinner) findViewById(R.id.s_estudios);
        //Recuperamos los valores de cada uno de los widgets
        snacimiento=etNacimiento.getText().toString();
        String nombre=etNombre.getText().toString();
        boolean consentimiento=cbDatos.isChecked();
        RadioButton rbGenero=(RadioButton) findViewById(rgGenero.getCheckedRadioButtonId());/*Recuperamos desde
        la interfaz el botón de selección única que estuviera marcado en el formulario*/
        String genero="";
        if(rbGenero!=null) {//Puede ser que el botón de selección única no se haya seleccionado
            genero=rbGenero.getText().toString();/*Cuando tenemos el botón de selección única marcado,
            recuperamos el texto que muestra*/
        }
        int estudio=sEstudios.getSelectedItemPosition();/*Recuperamos el índice seleccionado de la lista
        desplegable*/
        Resources res = getResources();
        String nivelEstudio;
        switch(estudio){//Establecemos qué valor de la lista desplegable se corresponde con el índice seleccionado
            case 1: nivelEstudio=res.getString(R.string.secondary_education);
                break;
            case 2: nivelEstudio=res.getString(R.string.baccalaureate);
                break;
            case 3: nivelEstudio=res.getString(R.string.grado);
                break;
            case 4: nivelEstudio=res.getString(R.string.postgrado);
                break;
            default: nivelEstudio=res.getString(R.string.nivel_estudios);
                break;
        }
        String scons;
        if(consentimiento){//Asociamos el valor del checkbox con un texto que se corresponda con su significado
            scons=" Consiente el uso de sus datos";
        }
        else{
            scons=" No consiente el uso de sus datos";
        }
        tvRes.setText("Nombre:"+nombre+" Nacimiento:"+snacimiento+" Género:"+genero+" Nivel de estudios:"+nivelEstudio+scons);
    }

}
