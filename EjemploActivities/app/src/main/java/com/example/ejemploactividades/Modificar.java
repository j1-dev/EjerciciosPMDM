package com.example.ejemploactividades;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

public class Modificar extends AppCompatActivity {

    private String nombre;
    private int edad;
    private boolean consentimiento;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificar);
        if(savedInstanceState==null){
            Intent intent = getIntent();
            nombre = intent.getStringExtra("nombre");
            edad = intent.getIntExtra("edad", 0);
            consentimiento = intent.getBooleanExtra("consentimiento", false);
        } else {
            nombre=savedInstanceState.getString("nombre");
            edad=savedInstanceState.getInt("edad");
            consentimiento=savedInstanceState.getBoolean("consentimiento");
        }
        EditText etNombre=findViewById(R.id.et_nombre);
        EditText etEdad=findViewById(R.id.et_edad);
        CheckBox cbConsentimiento=findViewById(R.id.cb_consentimiento);
        etNombre.setText(nombre);
        etEdad.setText(edad);
        cbConsentimiento.setChecked(consentimiento);
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        EditText etNombre=findViewById(R.id.et_nombre);
        EditText etEdad=findViewById(R.id.et_edad);
        CheckBox cbConsentimiento=findViewById(R.id.cb_consentimiento);

        nombre = etNombre.getText().toString();
        edad = Integer.parseInt(etEdad.getText().toString());
        consentimiento = cbConsentimiento.isChecked();

        outState.putString("nombre", nombre);
        outState.putInt("edad", edad);
        outState.putBoolean("consentimiento", consentimiento);
    }

    public void aceptar(View view){
        EditText etNombre=findViewById(R.id.et_nombre);
        EditText etEdad=findViewById(R.id.et_edad);
        CheckBox cbConsentimiento=findViewById(R.id.cb_consentimiento);

        nombre = etNombre.getText().toString();
        edad = Integer.parseInt(etEdad.getText().toString());
        consentimiento = cbConsentimiento.isChecked();
        Intent backIntent = new Intent();
        backIntent.putExtra("nombre",nombre);
        backIntent.putExtra("edad",edad);
        backIntent.putExtra("consentimiento",consentimiento);
        setResult(Activity.RESULT_OK, backIntent);
        finish();
    }
}