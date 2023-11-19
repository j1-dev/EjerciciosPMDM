package com.example.exud13;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
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
    private String nombre;
    private String idioma;
    private Date fecha;
    private DatePickerDialog dpdFecha;
    private EditText etFecha;
    private String municipio;

    private ActivityResultLauncher<Intent> modificarResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result){
                    if (result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        nombre = data.getStringExtra("nombre");
                        idioma = data.getStringExtra("idioma");
                        fecha=new Date(data.getLongExtra("fecha",-1));
                        municipio = data.getStringExtra("municipio");
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState!=null){
            nombre=savedInstanceState.getString("nombre");
            idioma=savedInstanceState.getString("idioma");
            fecha=new Date(savedInstanceState.getLong("fecha"));
            municipio=savedInstanceState.getString("municipio");
        } else {
            nombre="";
            idioma="";
            fecha=stringToDate("");
            municipio="";
        }

        EditText etNombre=findViewById(R.id.et_nombre);
        RadioGroup rgIdioma=findViewById(R.id.rg_idioma);
        if(etFecha==null) {
            etFecha=findViewById(R.id.et_fecha);
        }
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
        Spinner sMunicipio=findViewById(R.id.s_municipio);

        prepararFecha(fecha);
        populateSpinner();

        etNombre.setText(nombre);
        rgIdioma.check(getIdiomaIndex(idioma));
        etFecha.setText("");
        sMunicipio.setSelection(0);
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);

        EditText etNombre=findViewById(R.id.et_nombre);
        RadioGroup rgIdioma=findViewById(R.id.rg_idioma);
        if(etFecha==null) {
            etFecha=findViewById(R.id.et_fecha);
        }
        Spinner sMunicipio=findViewById(R.id.s_municipio);

        nombre=etNombre.getText().toString();
        RadioButton rbSaved=findViewById(rgIdioma.getCheckedRadioButtonId());
        if(rbSaved!=null){
            idioma=rbSaved.getText().toString();
        }
        fecha=stringToDate(etFecha.getText().toString());
        municipio=sMunicipio.getSelectedItem().toString();

        outState.putString("nombre", nombre);
        outState.putString("idioma", idioma);
        outState.putLong("fecha", fecha.getTime());
        outState.putString("municipio", municipio);
    }

    public void guardar(View view){
        EditText etNombre=findViewById(R.id.et_nombre);
        RadioGroup rgIdioma=findViewById(R.id.rg_idioma);
        if(etFecha==null) {
            etFecha=findViewById(R.id.et_fecha);
        }
        Spinner sMunicipio=findViewById(R.id.s_municipio);

        nombre=etNombre.getText().toString();
        RadioButton rbSaved=findViewById(rgIdioma.getCheckedRadioButtonId());
        RadioButton rbLast=findViewById(R.id.rb_frances);
        if(rbSaved!=null){
            idioma=rbSaved.getText().toString();
        }
        fecha=stringToDate(etFecha.getText().toString());
        municipio=sMunicipio.getSelectedItem().toString();

        if(!(nombre.isEmpty()||idioma.isEmpty()||etFecha.getText().toString().isEmpty()||municipio.equals("Seleccione un municipio"))){
            Intent intent = new Intent().setClass(this, Mostrar.class);
            intent.putExtra("nombre",nombre);
            intent.putExtra("idioma", idioma);
            intent.putExtra("fecha", fecha.getTime());
            intent.putExtra("municipio", municipio);
            modificarResultLauncher.launch(intent);
        } else {
            System.out.println(fecha.toString());
            etNombre.setError(null);
            rbLast.setError(null);
            etFecha.setError(null);
            if(nombre.isEmpty()){
                etNombre.setError("Este campo no debe estar vacio");
            }
            if(idioma.isEmpty()){
                rbLast.setError("Debe seleccionar alguna opción");
            }
            if(etFecha.getText().toString().isEmpty()){
                etFecha.setError("Debe seleccionar alguna fecha");
            }
            if(municipio.equals("Seleccione un municipio")){
                TextView errorText = (TextView)sMunicipio.getSelectedView();
                errorText.setError("");
                errorText.setTextColor(Color.RED);
                errorText.setText("Debe seleccionar algún municipio");
            }
        }
    }

    private int getIdiomaIndex(String idioma){
        switch (idioma){
            case "Español": return 0;
            case "Inglés": return 1;
            case "Francés": return 2;
            default: return -1;
        }
    }

    private int getMunicipioIndex(String municipio){
        switch (municipio){
            case "Málaga": return 0;
            case "Mijas": return 1;
            case "Fuengirola": return 2;
            case "Benalmádena": return 3;
            case "Marbella": return 4;
            case "Torremolinos": return 5;
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

    private void populateSpinner(){
        Spinner generoSpinner = (Spinner) findViewById(R.id.s_municipio);

        ArrayList<String> municipios = new ArrayList<>();

        municipios.add("Seleccione un municipio");
        municipios.add("Málaga");
        municipios.add("Mijas");
        municipios.add("Fuengirola");
        municipios.add("Benalmádena");
        municipios.add("Marbella");
        municipios.add("Torremolinos");

        ArrayAdapter<String> admunicipios = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,
                municipios);
        admunicipios.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        generoSpinner.setAdapter(admunicipios);
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