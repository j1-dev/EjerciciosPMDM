package com.example.exud13;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Mostrar extends AppCompatActivity {
    private String nombre;
    private String idioma;
    private Date fecha;
    private String municipio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar);

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            nombre = intent.getStringExtra("nombre");
            idioma = intent.getStringExtra("idioma");
            fecha = new Date(intent.getLongExtra("fecha", -1));
            municipio = intent.getStringExtra("municipio");
        } else {
            nombre = savedInstanceState.getString("nombre");
            idioma = savedInstanceState.getString("idioma");
            fecha = new Date(savedInstanceState.getLong("fecha", -1));
            municipio = savedInstanceState.getString("municipio");
        }

        actualizarInterfaz();
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putString("nombre", nombre);
        outState.putString("idioma", idioma);
        outState.putLong("fecha", fecha.getTime());
        outState.putString("municipio", municipio);
    }

    private void actualizarInterfaz(){
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
        TextView tvNombre=findViewById(R.id.tv_nombre);
        TextView tvIdioma=findViewById(R.id.tv_idioma);
        TextView tvFecha=findViewById(R.id.tv_fecha);
        TextView tvMunicipio=findViewById(R.id.tv_municipio);
        System.out.println(nombre);
        System.out.println(idioma);
        System.out.println(fecha.toString());
        System.out.println(municipio);
        tvNombre.setText("Nombre: " + nombre);
        tvIdioma.setText("Idioma: " + idioma);
        tvFecha.setText("Fecha de nacimiento: " + dateFormatter.format(fecha.getTime()));
        tvMunicipio.setText("Municipio: " + municipio);
    }
}
