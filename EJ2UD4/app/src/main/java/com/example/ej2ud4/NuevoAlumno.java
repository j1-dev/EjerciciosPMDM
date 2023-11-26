package com.example.ej2ud4;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ej2ud4.entidades.Alumno;
import com.example.ej2ud4.util.DBHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class NuevoAlumno extends AppCompatActivity {

  private DatePickerDialog dpdNacimiento;
  private EditText etNacimiento;
  private String snacimiento;
  private DBHelper dbHelper;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_nuevo_alumno);

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
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    if(etNacimiento==null){
      etNacimiento=findViewById(R.id.et_nuevo_fecha_nacimiento);
    }
    snacimiento=etNacimiento.getText().toString();
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


  public void aceptar(View view) {
    if(dbHelper==null){/*Aquí obtenemos una instancia de la clase DBHelper que usaremos para insertar
            los datos en la base de datos*/
      dbHelper=DBHelper.getInstance(this);
    }
    EditText etNombre = findViewById(R.id.et_nuevo_name);
    EditText etApellidos = findViewById(R.id.et_nuevo_surname);
    if(etNacimiento==null){
      etNacimiento= findViewById(R.id.et_nuevo_fecha_nacimiento);
    }
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

    Button btAceptar= findViewById(R.id.bt_nuevo_accept);
    btAceptar.setEnabled(false);
    btAceptar.setClickable(false);
    if(dbHelper!=null) {
      Alumno alumno = new Alumno();
      alumno.setFechaNacimiento(fechaNacimiento);
      alumno.setNombre(nombre);
      alumno.setApellidos(apellidos);
      boolean insertado;
      insertado = dbHelper.insertarAlumno(alumno);
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

  public void back(View view) {
    this.finish();
  }
}