package com.example.ej2ud3;

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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private String nombre;
    private String apellidos;
    private Date fecha;
    private String genero;
    private String provincia;
    private boolean consentimiento;

    private ActivityResultLauncher<Intent> modificarResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result){
                    if (result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        nombre = data.getStringExtra("nombre");
                        apellidos = data.getStringExtra("apellidos");
                        fecha=new Date(data.getLongExtra("fecha",-1));
                        genero = data.getStringExtra("genero");
                        provincia = data.getStringExtra("provincia");
                        consentimiento = data.getBooleanExtra("consentimiento", false);
                        actualizarInterfaz();
                    }
                }
            }
    );

    private void actualizarInterfaz(){
        TextView tvNombre=findViewById(R.id.tv_nombre);
        TextView tvApellidos=findViewById(R.id.tv_apellidos);
        TextView tvFecha=findViewById(R.id.tv_fecha);
        TextView tvGenero=findViewById(R.id.tv_genero);
        TextView tvProvincia=findViewById(R.id.tv_provincia);
        TextView tvConsentimiento=findViewById(R.id.tv_consentimiento);
        tvNombre.setText("Nombre: " + nombre);
        tvApellidos.setText("Apellidos: " + apellidos);
        tvFecha.setText("Fecha de nacimiento: " + fecha.toString());
        tvGenero.setText("Genero: " + genero);
        tvProvincia.setText("Provincia: " + provincia);
        if(consentimiento){
            tvConsentimiento.setText("Consiente al tratamiento de sus datos");
        } else {
            tvConsentimiento.setText("No consiente al tratamiento de sus datos");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(savedInstanceState==null){
            nombre = "Juan";
            apellidos = "Garcia Marin";
            fecha = new Date();
            genero = "Masculino";
            provincia = "Málaga";
            consentimiento = true;
        } else {
            nombre=savedInstanceState.getString("nombre");
            apellidos=savedInstanceState.getString("apellidos");
            fecha=new Date(savedInstanceState.getLong("fecha"));
            genero = savedInstanceState.getString("genero");
            provincia = savedInstanceState.getString("provincia");
            consentimiento=savedInstanceState.getBoolean("consentimiento");
        }
        actualizarInterfaz();
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putString("nombre", nombre);
        outState.putString("apellidos", apellidos);
        outState.putLong("fecha", fecha.getTime());
        outState.putString("genero", genero);
        outState.putString("provincia", provincia);
        outState.putBoolean("consentimiento", consentimiento);
    }

    public void editar(View view){
        Intent myIntent = new Intent().setClass(this, Modificar.class);
        myIntent.putExtra("nombre", nombre);
        myIntent.putExtra("apellidos", apellidos);
        myIntent.putExtra("fecha", fecha.getTime());
        myIntent.putExtra("genero", genero);
        myIntent.putExtra("provincia", provincia);
        myIntent.putExtra("consentimiento", consentimiento);
        modificarResultLauncher.launch(myIntent);
    }
}