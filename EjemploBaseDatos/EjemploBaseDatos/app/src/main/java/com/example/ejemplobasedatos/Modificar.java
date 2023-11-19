package com.example.ejemplobasedatos;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.ejemplobasedatos.entidades.Alumno;
import com.example.ejemplobasedatos.util.DBHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;

public class Modificar extends AppCompatActivity {

    private DatePickerDialog dpdNacimiento;
    private EditText etNacimiento;
    private String snacimiento;
    private DBHelper dbHelper;
    private int idAlumno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificar);

        if(savedInstanceState!=null){/*Si hemos hecho onSaveInstanceState previamente y hemos almacenado información,
        la recuperamos*/
            snacimiento = savedInstanceState.getString("snacimiento","");
            idAlumno = savedInstanceState.getInt("idAlumno",-1);
        }
        else{//Si es la primera vez que se crea la actividad, obtenemos la información que pasamos a través del Intent
            snacimiento="";
            Intent intent = getIntent();
            idAlumno = intent.getIntExtra("idAlumno", -1);
        }
        dbHelper=DBHelper.getInstance(this);
        Alumno alumno;
        Log.d("MyApp","alumno "+idAlumno);
        try {//Recuperamos al alumno desde la base de datos a través de su identificador
          alumno=dbHelper.getAlumnoById((int) idAlumno);
        } catch (ParseException e) {
            alumno = null;
        }
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
        Date fnacimiento=null;
        if(snacimiento.isEmpty()){/*Si la fecha en formato String está vacía, es porque no la seleccionamos antes desde
        el widget de calendario, por lo que debemos darle el valor que tenga la fecha de nacimiento del alumno*/
            if(alumno!=null){
                fnacimiento=alumno.getFechaNacimiento();
            }
            if(fnacimiento==null) {/*Puede ser que el alumno no tuviera fecha de nacimiento, así que le damos el valor
            del día de hoy para mostrarlo en el calendario cuando se abra*/
                fnacimiento = new Date();
            }
        }
        else {
            try {
                fnacimiento = dateFormatter.parse(snacimiento);
            } catch (ParseException e) {
                e.printStackTrace();
                fnacimiento = new Date();
            }
        }
        prepararFecha(fnacimiento);
        populateSpinners();
        fillInformation(alumno);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(etNacimiento==null){
            etNacimiento=findViewById(R.id.et_modificar_fecha_nacimiento);
        }
        snacimiento=etNacimiento.getText().toString();
        //Es el valor del campo fecha de nacimiento el que queremos salvar
        outState.putString("snacimiento", snacimiento);
        outState.putInt("idAlumno",idAlumno);
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
                SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
                etNacimiento.setText(dateFormatter.format(newDate.getTime()));
            }
        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH),
                newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    private void populateSpinners(){
        Resources res = getResources();
        Spinner sestudios = findViewById(R.id.s_modificar_estudios);
        ArrayList<String> estudios = new ArrayList<>();
        estudios.add(res.getString(R.string.nivel_estudios));
        estudios.add(res.getString(R.string.secondary_education));
        estudios.add(res.getString(R.string.baccalaureate));
        estudios.add(res.getString(R.string.grado));
        estudios.add(res.getString(R.string.postgrado));
        ArrayAdapter<String> adestudios = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,
                estudios);
        adestudios.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sestudios.setAdapter(adestudios);
    }

    private void fillInformation(Alumno alumno){/*Con este método rellenamos la información de los campos con los datos
    del alumno*/
        EditText etNombre = findViewById(R.id.et_modificar_name);
        EditText etApellidos = findViewById(R.id.et_modificar_surname);
        EditText etDni = findViewById(R.id.et_modificar_dni);
        if(etNacimiento==null){
            etNacimiento= findViewById(R.id.et_modificar_fecha_nacimiento);
        }
        CheckBox cbDatos = findViewById(R.id.cb_modificar_data_consent);
        Spinner sEstudios = findViewById(R.id.s_modificar_estudios);
        etNombre.setText(alumno.getNombre());
        etApellidos.setText(alumno.getApellidos());
        etDni.setText(alumno.getDni());
        cbDatos.setChecked(alumno.isConsentimiento());
        sEstudios.setSelection(alumno.getNivelEstudios());
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
        if(alumno.getFechaNacimiento()!=null) {
            etNacimiento.setText(dateFormatter.format(alumno.getFechaNacimiento()));
        }
        RadioGroup rgGenero=findViewById(R.id.rg_modificar_genero);/*Para seleccionar un valor
        en un radio group debemos usar el método check del mismo y pasar como argumento el
        identificador del radiobutton que queramos seleccionar*/
        switch (alumno.getGenero()){
            case 0: rgGenero.check(R.id.rb_modificar_hombre);
                break;
            case 1: rgGenero.check(R.id.rb_modificar_mujer);
                break;
            case 2: rgGenero.check(R.id.rb_modificar_otro);
                break;
            default:
                break;
        }
    }

    public void aceptar(View view) {
        if(dbHelper==null){/*Aquí obtenemos una instancia de la clase DBHelper que usaremos para insertar
            los datos en la base de datos*/
            dbHelper=DBHelper.getInstance(this);
        }
        EditText etNombre = findViewById(R.id.et_modificar_name);
        EditText etApellidos = findViewById(R.id.et_modificar_surname);
        EditText etDni = findViewById(R.id.et_modificar_dni);
        if(etNacimiento==null){
            etNacimiento= findViewById(R.id.et_modificar_fecha_nacimiento);
        }
        CheckBox cbDatos = findViewById(R.id.cb_modificar_data_consent);
        RadioGroup rgGenero = findViewById(R.id.rg_modificar_genero);
        Spinner sEstudios = (Spinner) findViewById(R.id.s_modificar_estudios);
        //Recuperamos los valores de cada uno de los widgets
        snacimiento=etNacimiento.getText().toString();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
        Date fechaNacimiento;
        try {
            fechaNacimiento = dateFormatter.parse(snacimiento);
        }catch(ParseException ex){
            ex.printStackTrace();
            fechaNacimiento=null;
        }
        String nombre=etNombre.getText().toString();
        String apellidos=etApellidos.getText().toString();
        String dni=etDni.getText().toString();
        boolean consentimiento=cbDatos.isChecked();
        int idgen=rgGenero.getCheckedRadioButtonId();
        int genero;/*Como vamos a almacenarlo en la base de datos como un entero, no es necesario
        aquí saber cuál es el valor equivalente en String*/
        if(idgen==R.id.rb_modificar_hombre){
            genero=0;
        }
        else if(idgen==R.id.rb_modificar_mujer){
            genero=1;
        }
        else if(idgen==R.id.rb_modificar_otro){
            genero=2;
        }
        else{
            genero=-1;
        }
        int estudio=sEstudios.getSelectedItemPosition();/*Como vamos a almacenarlo en la base de datos
        como un entero, no es necesario aquí saber cuál es el valor equivalente en String*/
        //Antes de este punto habría que realizar una validación de los datos que nosotros omitimos
        Button btAceptar= findViewById(R.id.bt_modificar_accept);
        btAceptar.setEnabled(false);
        btAceptar.setClickable(false);
        if(dbHelper!=null) {
            Alumno alumno = new Alumno();//Creamos un modificar alumno con sus correspondientes valores
            alumno.setIdAlumno(idAlumno);
            alumno.setNivelEstudios(estudio);
            alumno.setGenero(genero);
            alumno.setConsentimiento(consentimiento);
            alumno.setFechaNacimiento(fechaNacimiento);
            alumno.setNombre(nombre);
            alumno.setApellidos(apellidos);
            alumno.setDni(dni);
            boolean insertado;
            insertado = dbHelper.actualizarAlumno(alumno);//Actualizamos el alumno en la base de datos
            if (insertado) {//Si el alumno se inserta devolvemos un RESULT OK y cerramos la actividad
                setResult(RESULT_OK);
                finish();
            } else {//En caso contrario mostramos el mensaje de error
                btAceptar.setEnabled(true);
                btAceptar.setClickable(true);
                showError("error.IOException");
            }
        }
    }

    private void showError(String error) {
        String message;
        Resources res = getResources();
        int duration;
        if(error.equals("error.IOException")){//Aquí establecemos el mensaje de error y cuánto durará
            duration = Toast.LENGTH_LONG;
            message=res.getString(R.string.error_database);
        }
        else {
            duration = Toast.LENGTH_SHORT;
            message = res.getString(R.string.error_unknown);
        }
        Context context = this.getApplicationContext();
        Toast toast = Toast.makeText(context, message, duration);/*Aquí creamos el toast, indicando
        el mensaje y la duración*/
        toast.setGravity(Gravity.CENTER, 0, 0);//Establecemos la posición donde se mostrará
        toast.show();//Mostramos el mensaje
    }
}