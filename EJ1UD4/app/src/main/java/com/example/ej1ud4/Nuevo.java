package com.example.ej1ud4;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
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

import com.example.ej1ud4.entidades.Videojuego;
import com.example.ej1ud4.util.DBHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Nuevo extends AppCompatActivity {
  private DatePickerDialog dpdSalida;
  private EditText etSalida;
  private String ssalida;
  private DBHelper dbHelper;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_nuevo);

    if(savedInstanceState!=null){
      ssalida = savedInstanceState.getString("ssalida","");
    }
    else{
      ssalida="";
    }
    SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
    Date fsalida=null;
    if(ssalida.isEmpty()){
        fsalida = new Date();
    }
    else {
      try {
        fsalida = dateFormatter.parse(ssalida);
      } catch (ParseException e) {
        e.printStackTrace();
        fsalida = new Date();
      }
    }
    prepararFecha(fsalida);
    populateSpinners();
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    if(etSalida==null){
      etSalida=findViewById(R.id.et_nuevo_fecha_salida);
    }
    ssalida=etSalida.getText().toString();
    //Es el valor del campo fecha de nacimiento el que queremos salvar
    outState.putString("ssalida", ssalida);
  }

  private void prepararFecha(Date inicio){
    System.out.println(etSalida);
    System.out.println(R.id.et_nuevo_fecha_salida);
    if(etSalida==null) {
      etSalida = findViewById(R.id.et_nuevo_fecha_salida);
    }
    System.out.println(etSalida);
    System.out.println(R.id.et_nuevo_fecha_salida);

    etSalida.setOnFocusChangeListener(new View.OnFocusChangeListener() {
      @Override
      public void onFocusChange(View v, boolean hasFocus) {
        if(hasFocus) {
          dpdSalida.show();
        }
        v.clearFocus();
      }
    });
    Calendar newCalendar = Calendar.getInstance();
    newCalendar.setTime(inicio);
    dpdSalida = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
      public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Calendar newDate = Calendar.getInstance();
        newDate.set(year, monthOfYear, dayOfMonth);
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
        etSalida.setText(dateFormatter.format(newDate.getTime()));
      }
    },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH),
        newCalendar.get(Calendar.DAY_OF_MONTH));
  }

  private void populateSpinners(){
    Resources res = getResources();
    Spinner sgenero = findViewById(R.id.s_nuevo_genero);
    ArrayList<String> generos = new ArrayList<>();
    generos.add(res.getString(R.string.gender));
    generos.add(res.getString(R.string.action));
    generos.add(res.getString(R.string.sports));
    generos.add(res.getString(R.string.adventure));
    generos.add(res.getString(R.string.strategy));
    generos.add(res.getString(R.string.arcade));
    generos.add(res.getString(R.string.simulation));
    generos.add(res.getString(R.string.musical));
    generos.add(res.getString(R.string.other));
    ArrayAdapter<String> adgenero = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,
        generos);
    adgenero.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    sgenero.setAdapter(adgenero);
  }

  public void aceptar(View view) {
    if(dbHelper==null){
      dbHelper=DBHelper.getInstance(this);
    }
    EditText etNombre = findViewById(R.id.et_nuevo_name);
    if(etSalida==null){
      etSalida= findViewById(R.id.et_nuevo_fecha_salida);
    }
    CheckBox cbHaJugado = findViewById(R.id.cb_nuevo_has_played);
    Spinner sGenero = findViewById(R.id.s_nuevo_genero);
    RadioGroup rgConsola=findViewById(R.id.rg_nuevo_consola);
    ssalida=etSalida.getText().toString();
    SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
    Date fechaSalida;
    try {
      fechaSalida = dateFormatter.parse(ssalida);
    }catch(ParseException ex){
      ex.printStackTrace();
      fechaSalida=null;
    }
    String nombre=etNombre.getText().toString();
    boolean haJugado=cbHaJugado.isChecked();
    int idConsola=rgConsola.getCheckedRadioButtonId();
    int consola;/*Como vamos a almacenarlo en la base de datos como un entero, no es necesario
        aquí saber cuál es el valor equivalente en String*/
    if(idConsola==R.id.rb_nuevo_xbox){
      consola=0;
    }
    else if(idConsola==R.id.rb_nuevo_playstation){
      consola=1;
    }
    else if(idConsola==R.id.rb_nuevo_switch){
      consola=2;
    }
    else if(idConsola==R.id.rb_nuevo_multi){
      consola=3;
    }
    else{
      consola=-1;
    }
    int genero=sGenero.getSelectedItemPosition();
    Button btAceptar= findViewById(R.id.bt_nuevo_accept);
    btAceptar.setEnabled(false);
    btAceptar.setClickable(false);
    if(dbHelper!=null) {
      Videojuego videojuego = new Videojuego();
      videojuego.setNombre(nombre);
      videojuego.setFechaSalida(fechaSalida);
      videojuego.setGenero(genero);
      videojuego.setConsola(consola);
      videojuego.setSeHaJugado(haJugado);
      boolean insertado;
      insertado = dbHelper.insertarVideojuego(videojuego);
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
      message=res.getString(R.string.error_database);
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
