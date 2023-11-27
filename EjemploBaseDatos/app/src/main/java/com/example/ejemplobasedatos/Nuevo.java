package com.example.ejemplobasedatos;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Nuevo extends AppCompatActivity {

    private DatePickerDialog dpdNacimiento;
    private EditText etNacimiento;
    private String snacimiento;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo);

        if(savedInstanceState!=null){
            snacimiento = savedInstanceState.getString("snacimiento","");
        }
        else{
            snacimiento="";
        }
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
        Date fnacimiento;
        if(snacimiento.isEmpty()){
            fnacimiento=new Date();
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
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
       if(etNacimiento==null){
            etNacimiento=findViewById(R.id.et_nuevo_fecha_nacimiento);
        }
        snacimiento=etNacimiento.getText().toString();
        //Es el valor del campo fecha de nacimiento el que queremos salvar
        outState.putString("snacimiento", snacimiento);
    }

    private void prepararFecha(Date inicio){
        if(etNacimiento==null) {
            etNacimiento = findViewById(R.id.et_nuevo_fecha_nacimiento);
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
        Spinner sestudios = (Spinner) findViewById(R.id.s_nuevo_estudios);
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



    public void aceptar(View view) {
        if(dbHelper==null){/*Aquí obtenemos una instancia de la clase DBHelper que usaremos para insertar
            los datos en la base de datos*/
            dbHelper=DBHelper.getInstance(this);
        }
        EditText etNombre = findViewById(R.id.et_nuevo_name);
        EditText etApellidos = findViewById(R.id.et_nuevo_surname);
        EditText etDni = findViewById(R.id.et_nuevo_dni);
        if(etNacimiento==null){
            etNacimiento= findViewById(R.id.et_nuevo_fecha_nacimiento);
        }
        CheckBox cbDatos = findViewById(R.id.cb_nuevo_data_consent);
        RadioGroup rgGenero = findViewById(R.id.rg_nuevo_genero);
        Spinner sEstudios = (Spinner) findViewById(R.id.s_nuevo_estudios);
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
        if(idgen==R.id.rb_nuevo_hombre){
            genero=0;
        }
        else if(idgen==R.id.rb_nuevo_mujer){
            genero=1;
        }
        else if(idgen==R.id.rb_nuevo_otro){
            genero=2;
        }
        else{
            genero=-1;
        }
        int estudio=sEstudios.getSelectedItemPosition();/*Como vamos a almacenarlo en la base de datos
        como un entero, no es necesario aquí saber cuál es el valor equivalente en String*/
        //Antes de este punto habría que realizar una validación de los datos que nosotros omitimos
        Button btAceptar= findViewById(R.id.bt_nuevo_accept);
        btAceptar.setEnabled(false);
        btAceptar.setClickable(false);
        if(dbHelper!=null) {
            Alumno alumno = new Alumno();//Creamos un nuevo alumno con sus correspondientes valores
            alumno.setNivelEstudios(estudio);
            alumno.setGenero(genero);
            alumno.setConsentimiento(consentimiento);
            alumno.setFechaNacimiento(fechaNacimiento);
            alumno.setNombre(nombre);
            alumno.setApellidos(apellidos);
            alumno.setDni(dni);
            boolean insertado;
            insertado = dbHelper.insertarAlumno(alumno);//Insertamos el alumno en la base de datos
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