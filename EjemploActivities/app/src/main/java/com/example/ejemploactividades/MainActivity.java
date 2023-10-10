package com.example.ejemploactividades;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private String nombre;
    private int edad;
    private boolean consentimiento;

    private ActivityResultLauncher<Intent> modificarResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result){
                    if (result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(savedInstanceState==null){
            nombre = "Pepe";
            edad = 52;
            consentimiento = false;
        } else {
            nombre=savedInstanceState.getString("nombre");
            edad=savedInstanceState.getInt("edad");
            consentimiento=savedInstanceState.getBoolean("consentimiento");
        }
        TextView tvNombre = findViewById(R.id.tv_nombre);
        TextView tvEdad=findViewById(R.id.tv_edad);
        TextView tvConsentimiento=findViewById(R.id.tv_consentimiento);
        tvNombre.setText("Nombre: " + nombre);
        tvEdad.setText("Edad: " + edad);
        if(consentimiento){
            tvConsentimiento.setText("Consiente");
        } else {
            tvConsentimiento.setText("No consiente");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putString("nombre", nombre);
        outState.putInt("edad", edad);
        outState.putBoolean("consentimiento", consentimiento);
    }

    public void editar(View view){
        Intent myIntent = new Intent().setClass(this, Modificar.class);
        myIntent.putExtra("nombre", nombre);
        myIntent.putExtra("edad", edad);
        myIntent.putExtra("consentimiento", consentimiento);
        modificarResultLauncher.launch(myIntent);
    }


}