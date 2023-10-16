package com.example.ej1ud3;

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

    private DatePickerDialog dpdInicio;
    private DatePickerDialog dpdFin;
    private EditText etFechaInicio;
    private EditText etFechaFin;
    private String sInicio;
    private String sFin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState!=null){
            sInicio = savedInstanceState.getString("sinicio","");
            sFin = savedInstanceState.getString("sfin", "");
        }
        else{
            sInicio="";
            sFin="";
        }
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd MM yyyy");
        Date finicio = stringToDate(sInicio);
        Date ffin = stringToDate(sFin);
        prepararFechas(finicio, ffin);
        populateSpinners();
    }

    public Date stringToDate(String date){
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if(etFechaInicio==null){
            etFechaInicio=findViewById(R.id.et_fecha_inicio);
        }

        if(etFechaFin==null){
            etFechaFin=findViewById(R.id.et_fecha_fin);
        }

        sInicio=etFechaInicio.getText().toString();
        sFin=etFechaFin.getText().toString();

        outState.putString("sinicio", sInicio);
        outState.putString("sfin", sFin);
    }

    private void prepararFechas(Date inicio, Date fin){
        if(etFechaInicio==null) {
            etFechaInicio=findViewById(R.id.et_fecha_inicio);
        }
        if(etFechaFin==null){
            etFechaFin=findViewById(R.id.et_fecha_fin);
        }

        etFechaInicio.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    dpdInicio.show();
                }
                v.clearFocus();
            }
        });
        etFechaFin.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    dpdFin.show();
                }
                v.clearFocus();
            }
        });

        Calendar calendarInicio = Calendar.getInstance();
        calendarInicio.setTime(inicio);
        Calendar calendarFin = Calendar.getInstance();
        calendarFin.setTime(fin);
        dpdInicio = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                SimpleDateFormat dateFormatter = new SimpleDateFormat("dd MM yyyy");
                etFechaInicio.setText(dateFormatter.format(newDate.getTime()));
            }
        },calendarInicio.get(Calendar.YEAR), calendarInicio.get(Calendar.MONTH),
                calendarInicio.get(Calendar.DAY_OF_MONTH));

        dpdFin = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                SimpleDateFormat dateFormatter = new SimpleDateFormat("dd MM yyyy");
                etFechaFin.setText(dateFormatter.format(newDate.getTime()));
            }
        },calendarFin.get(Calendar.YEAR), calendarFin.get(Calendar.MONTH),
                calendarFin.get(Calendar.DAY_OF_MONTH));

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

    public void aceptar(View view) {
        TextView tvRes=findViewById(R.id.tv_answer);
        tvRes.setText("");
        EditText etNombre = findViewById(R.id.et_name);
        EditText etApellidos = findViewById(R.id.et_apellidos);
        if(etFechaInicio==null) {
            etFechaInicio=findViewById(R.id.et_fecha_inicio);
        }
        if(etFechaFin==null){
            etFechaFin=findViewById(R.id.et_fecha_fin);
        }
        CheckBox cbMayorEdad = findViewById(R.id.cb_mayor_de_edad);
        RadioGroup rgNacionalidad = findViewById(R.id.rg_nacionalidad);
        Spinner sEstudios = (Spinner) findViewById(R.id.s_estudios);

        sInicio=etFechaInicio.getText().toString();
        sFin=etFechaFin.getText().toString();
        String nombre=etNombre.getText().toString();
        String apellidos=etApellidos.getText().toString();
        boolean mayorEdad=cbMayorEdad.isChecked();
        RadioButton rbNacionalidad=(RadioButton) findViewById(rgNacionalidad.getCheckedRadioButtonId());
        String nacionalidad="";
        if(rbNacionalidad!=null) {
            nacionalidad=rbNacionalidad.getText().toString();
        }
        int estudio=sEstudios.getSelectedItemPosition();
        Resources res = getResources();
        String idiomaQueEstudiar;
        switch(estudio){
            case 1:
                idiomaQueEstudiar=res.getString(R.string.frances);
                break;
            case 2:
                idiomaQueEstudiar=res.getString(R.string.espanol);
                break;
            case 3:
                idiomaQueEstudiar=res.getString(R.string.ingles);
                break;
            case 4:
                idiomaQueEstudiar=res.getString(R.string.aleman);
                break;
            case 5:
                idiomaQueEstudiar=res.getString(R.string.chino);
                break;
            case 6:
                idiomaQueEstudiar=res.getString(R.string.ruso);
                break;
            default: idiomaQueEstudiar="";
                break;
        }

        if(!(nombre.isEmpty()||apellidos.isEmpty()||sInicio.isEmpty()||sFin.isEmpty()||nacionalidad.isEmpty()||idiomaQueEstudiar.isEmpty()) && cbMayorEdad.isChecked()){
            if(fechasValidas(sInicio,sFin)){
                tvRes.setText(res.getText(R.string.nombre)+": "+nombre+"\n"+res.getText(R.string.apellidos)+": "+apellidos+
                         "\n"+res.getText(R.string.fecha_inicio)+": "+sInicio+"\n"+res.getText(R.string.fecha_fin)+": "+sFin+
                         "\n"+res.getText(R.string.nacionalidad)+": "+nacionalidad+"\n"+res.getText(R.string.choose_language)+": "+idiomaQueEstudiar);
            } else {
                tvRes.setText(res.getString(R.string.error_fechas));
            }
        }else{
            if(!cbMayorEdad.isChecked()){
                tvRes.setText(res.getString(R.string.debe_ser_mayor));
            } else {
                tvRes.setText(res.getString(R.string.error_campos));
            }
        }

    }

    private boolean fechasValidas(String inicio, String fin){
        Date fInicio=stringToDate(inicio);
        Date fFin=stringToDate(fin);
        return fInicio.before(fFin);
    }
}