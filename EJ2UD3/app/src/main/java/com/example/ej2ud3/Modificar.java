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
    private int apellidos;
    private Date fecha;
    private DatePickerDialog dpdFecha;
    private EditText etFecha;
    private String sFecha;
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
            apellidos = intent.getIntExtra("apellidos", -1);
            try {
                fecha = new SimpleDateFormat("dd/MM/yyyy").parse(intent.getStringExtra("fecha"));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            genero = intent.getStringExtra("genero");
            provincia = intent.getStringExtra("provincia");
            consentimiento = intent.getBooleanExtra("consentimiento", false);
        } else {
            nombre=savedInstanceState.getString("nombre");
            apellidos=savedInstanceState.getInt("apellidos");
            try {
                fecha = new SimpleDateFormat("dd/MM/yyyy").parse(savedInstanceState.getString("fecha"));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            genero = savedInstanceState.getString("genero");
            provincia = savedInstanceState.getString("provincia");
            consentimiento=savedInstanceState.getBoolean("consentimiento");
        }
        EditText etNombre=findViewById(R.id.et_nombre);
        EditText etApellidos=findViewById(R.id.et_apellidos);
        CheckBox cbConsentimiento=findViewById(R.id.cb_consentimiento);
        etNombre.setText(nombre);
        etApellidos.setText(apellidos+"");
        cbConsentimiento.setChecked(consentimiento);
        prepararFecha(fecha);
        populateSpinners();
    }

    private void prepararFechas(Date date){
        EditText etFecha;
        if(date==null) {
            etFecha=findViewById(R.id.et_fecha);
        }

        etFecha.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    dpdInicio.show();
                }
                v.clearFocus();
            }
        });


        Calendar calendarInicio = Calendar.getInstance();
        calendarInicio.setTime(inicio);

        dpdInicio = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                SimpleDateFormat dateFormatter = new SimpleDateFormat("dd MM yyyy");
                etFechaInicio.setText(dateFormatter.format(newDate.getTime()));
            }
        },calendarInicio.get(Calendar.YEAR), calendarInicio.get(Calendar.MONTH),
                calendarInicio.get(Calendar.DAY_OF_MONTH));


    }

    private void populateSpinners(){
        Resources res = getResources();
        Spinner sestudios = (Spinner) findViewById(R.id.s_estudios);
        ArrayList<String> estudios = new ArrayList<>();

        estudios.add(res.getString(R.string.choose_language));
        estudios.add(res.getString(R.string.frances));
        estudios.add(res.getString(R.string.espanol));
        estudios.add(res.getString(R.string.ingles));
        estudios.add(res.getString(R.string.aleman));
        estudios.add(res.getString(R.string.chino));
        estudios.add(res.getString(R.string.ruso));

        ArrayAdapter<String> adestudios = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,
                estudios);
        adestudios.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sestudios.setAdapter(adestudios);
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        EditText etNombre=findViewById(R.id.et_nombre);
        EditText etApellidos=findViewById(R.id.et_apellidos);
        CheckBox cbConsentimiento=findViewById(R.id.cb_consentimiento);

        nombre = etNombre.getText().toString();
        apellidos = Integer.parseInt(etApellidos.getText().toString());
        consentimiento = cbConsentimiento.isChecked();

        outState.putString("nombre", nombre);
        outState.putInt("apellidos", apellidos);
        outState.putBoolean("consentimiento", consentimiento);
    }

    public void guardar(View view){
        EditText etNombre=findViewById(R.id.et_nombre);
        EditText etApellidos=findViewById(R.id.et_apellidos);
        CheckBox cbConsentimiento=findViewById(R.id.cb_consentimiento);

        nombre = etNombre.getText().toString();
        apellidos = Integer.parseInt(etApellidos.getText().toString());
        consentimiento = cbConsentimiento.isChecked();
        Intent backIntent = new Intent();
        backIntent.putExtra("nombre",nombre);
        backIntent.putExtra("apellidos",apellidos);
        backIntent.putExtra("consentimiento",consentimiento);
        setResult(Activity.RESULT_OK, backIntent);
        finish();
    }
}