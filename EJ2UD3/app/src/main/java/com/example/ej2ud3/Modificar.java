package com.example.ej2ud3;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Modificar extends AppCompatActivity {

    private String nombre;
    private String apellidos;
    private Date fecha;
    private DatePickerDialog dpdFecha;
    private EditText etFecha;
    private String genero;
    private String provincia;
    private boolean consentimiento;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificar);

        if(savedInstanceState==null){
            Intent intent = getIntent();
            nombre = intent.getStringExtra("nombre");
            apellidos = intent.getStringExtra("apellidos");
            fecha=new Date(intent.getLongExtra("fecha",-1));
            genero = intent.getStringExtra("genero");
            provincia = intent.getStringExtra("provincia");
            consentimiento = intent.getBooleanExtra("consentimiento", false);
        } else {
            nombre=savedInstanceState.getString("nombre");
            apellidos=savedInstanceState.getString("apellidos");
            fecha=new Date(savedInstanceState.getLong("fecha",-1));
            genero = savedInstanceState.getString("genero");
            provincia = savedInstanceState.getString("provincia");
            consentimiento=savedInstanceState.getBoolean("consentimiento");
        }

        EditText etNombre=findViewById(R.id.et_nombre);
        EditText etApellidos=findViewById(R.id.et_apellidos);
        if(etFecha==null) {
            etFecha=findViewById(R.id.et_fecha);
        }
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd MM yyyy");
        Spinner sGenero=findViewById(R.id.s_genero);
        Spinner sProvincia=findViewById(R.id.s_provincia);
        CheckBox cbConsentimiento=findViewById(R.id.cb_consentimiento);

        prepararFecha(fecha);
        populateSpinners();

        etNombre.setText(nombre);
        etApellidos.setText(apellidos);
        etFecha.setText(dateFormatter.format(fecha.getTime()));
        sGenero.setSelection(getGeneroIndex(genero));
        sProvincia.setSelection(getProvinciaIndex(provincia));
        cbConsentimiento.setChecked(consentimiento);

    }

    private int getGeneroIndex(String genero){
        switch (genero){
            case "Masculino": return 0;
            case "Femenino": return 1;
            case "No Binario": return 2;
            case "Prefiere no decir": return 3;
            default: return -1;
        }
    }

    private int getProvinciaIndex(String provincia){
        switch (provincia){
            case "Almería": return 0;
            case "Cádiz": return 1;
            case "Córdoba": return 2;
            case "Granada": return 3;
            case "Huelva": return 4;
            case "Jaén": return 5;
            case "Málaga": return 6;
            case "Sevilla": return 7;
            default: return -1;
        }
    }

    private void prepararFecha(Date date){
        if(etFecha==null) {
            etFecha=findViewById(R.id.et_fecha);
        }

        etFecha.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    dpdFecha.show();
                }
                v.clearFocus();
            }
        });


        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fecha);

        dpdFecha = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                SimpleDateFormat dateFormatter = new SimpleDateFormat("dd MM yyyy");
                etFecha.setText(dateFormatter.format(newDate.getTime()));
            }
        },calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));


    }

    private void populateSpinners(){
        Spinner generoSpinner = (Spinner) findViewById(R.id.s_genero);
        Spinner provinciaSpinner = (Spinner) findViewById(R.id.s_provincia);

        ArrayList<String> generos = new ArrayList<>();

        generos.add("Masculino");
        generos.add("Femenino");
        generos.add("No Binario");
        generos.add("Prefiere no decir");

        ArrayAdapter<String> adestudios = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,
                generos);
        adestudios.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        generoSpinner.setAdapter(adestudios);

        ArrayList<String> provincias = new ArrayList<>();

        provincias.add("Almería");
        provincias.add("Cádiz");
        provincias.add("Córdoba");
        provincias.add("Granada");
        provincias.add("Huelva");
        provincias.add("Jaén");
        provincias.add("Málaga");
        provincias.add("Sevilla");

        ArrayAdapter<String> adprovincias = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,
                provincias);
        adprovincias.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        provinciaSpinner.setAdapter(adprovincias);
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        EditText etNombre=findViewById(R.id.et_nombre);
        EditText etApellidos=findViewById(R.id.et_apellidos);
        if(etFecha==null) {
            etFecha=findViewById(R.id.et_fecha);
        }
        Spinner sGenero = (Spinner) findViewById(R.id.s_genero);
        Spinner sProvincia = (Spinner) findViewById(R.id.s_provincia);
        CheckBox cbConsentimiento=findViewById(R.id.cb_consentimiento);

        nombre = etNombre.getText().toString();
        apellidos = etApellidos.getText().toString();
        fecha = Modificar.stringToDate(etFecha.getText().toString());
        genero = sGenero.getSelectedItem().toString();
        provincia = sProvincia.getSelectedItem().toString();
        consentimiento = cbConsentimiento.isChecked();

        outState.putString("nombre", nombre);
        outState.putString("apellidos", apellidos);
        outState.putLong("fecha", fecha.getTime());
        outState.putString("genero", genero);
        outState.putString("provincia", provincia);
        outState.putBoolean("consentimiento", consentimiento);
    }

    public void guardar(View view){
        EditText etNombre=findViewById(R.id.et_nombre);
        EditText etApellidos=findViewById(R.id.et_apellidos);
        if(etFecha==null) {
            etFecha=findViewById(R.id.et_fecha);
        }
        Spinner sGenero=findViewById(R.id.s_genero);
        Spinner sProvincia=findViewById(R.id.s_provincia);
        CheckBox cbConsentimiento=findViewById(R.id.cb_consentimiento);

        nombre = etNombre.getText().toString();
        apellidos = etApellidos.getText().toString();
        fecha=stringToDate(etFecha.getText().toString());
        genero=sGenero.getSelectedItem().toString();
        provincia=sProvincia.getSelectedItem().toString();
        consentimiento = cbConsentimiento.isChecked();

        if((!(nombre.isEmpty()||apellidos.isEmpty()||fecha.toString().isEmpty()))&&consentimiento){
            Intent backIntent = new Intent();
            backIntent.putExtra("nombre",nombre);
            backIntent.putExtra("apellidos",apellidos);
            backIntent.putExtra("fecha",fecha.getTime());
            backIntent.putExtra("genero", genero);
            backIntent.putExtra("provincia", provincia);
            backIntent.putExtra("consentimiento",consentimiento);
            setResult(Activity.RESULT_OK, backIntent);
            finish();
        } else {
            if (nombre.isEmpty()){
                etNombre.setError("No puede dejar el campo vacío");
            }
            if (apellidos.isEmpty()){
                etApellidos.setError("No puede dejar el campo vacío");
            }
        }
    }

    public static Date stringToDate(String date){
        Date d;
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd MM yyyy");
        if(date.isEmpty()){
            d=new Date();
        } else {
            try {
                d=dateFormatter.parse(date);
            } catch (ParseException e){
                e.printStackTrace();
                d=new Date();
            }
        }
        return d;
    }
}