package com.example.exud2;

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
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.exud2.entidades.Evento;
import com.example.exud2.util.DBHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Nuevo extends AppCompatActivity {
  private DatePickerDialog dpdFecha;
  private EditText etFecha;
  private String sfecha;
  private DBHelper dbHelper;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_nuevo);

    if(savedInstanceState!=null){
      sfecha = savedInstanceState.getString("sfecha","");
    }
    else{
      sfecha="";
    }
    SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
    Date fecha=null;
    if(sfecha.isEmpty()){
      fecha = new Date();
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
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    if(etFecha==null){
      etFecha=findViewById(R.id.et_nuevo_fecha_evento);
    }
    sfecha=etFecha.getText().toString();
    outState.putString("sfecha", sfecha);
  }

  private void prepararFecha(Date inicio){
    if(etFecha==null) {
      etFecha = findViewById(R.id.et_nuevo_fecha_evento);
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
    Calendar newCalendar = Calendar.getInstance();
    newCalendar.setTime(inicio);
    dpdFecha = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
      public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Calendar newDate = Calendar.getInstance();
        newDate.set(year, monthOfYear, dayOfMonth);
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
        etFecha.setText(dateFormatter.format(newDate.getTime()));
      }
    },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH),
        newCalendar.get(Calendar.DAY_OF_MONTH));
  }

  public void aceptar(View view) {
    if(dbHelper==null){
      dbHelper=DBHelper.getInstance(this);
    }
    EditText etNombre = findViewById(R.id.et_nuevo_name_evento);
    if(etFecha==null){
      etFecha= findViewById(R.id.et_nuevo_fecha_evento);
    }
    RadioGroup rgTipo=findViewById(R.id.rg_nuevo_tipo_evento);
    sfecha=etFecha.getText().toString();
    SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
    Date fecha;
    try {
      fecha = dateFormatter.parse(sfecha);
    }catch(ParseException ex){
      ex.printStackTrace();
      fecha=null;
    }
    String nombre=etNombre.getText().toString();
    int idTipo=rgTipo.getCheckedRadioButtonId();
    int tipo;
    if(idTipo==R.id.rb_nuevo_evento_social){
      tipo=0;
    }
    else if(idTipo==R.id.rb_nuevo_evento_medico){
      tipo=1;
    }
    else if(idTipo==R.id.rb_nuevo_evento_profesional){
      tipo=2;
    }
    else{
      tipo=-1;
    }
    Button btAceptar= findViewById(R.id.bt_nuevo_accept);
    btAceptar.setEnabled(false);
    btAceptar.setClickable(false);
    if(dbHelper!=null) {
      Evento evento = new Evento();
      evento.setNombre(nombre);
      evento.setFecha(fecha);
      evento.setTipo(tipo);
      boolean insertado;
      insertado = dbHelper.insertarEvento(evento);
      if (insertado) {
        setResult(RESULT_OK);
        finish();
      } else {
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
    if(error.equals("error.IOException")){
      duration = Toast.LENGTH_LONG;
      message="Error de la base de datos";
    }
    else {
      duration = Toast.LENGTH_SHORT;
      message = "Error desconocido";
    }
    Context context = this.getApplicationContext();
    Toast toast = Toast.makeText(context, message, duration);
    toast.setGravity(Gravity.CENTER, 0, 0);
    toast.show();
  }
}
