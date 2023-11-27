package com.example.ejemplosqlito;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.EditText;

public class Nuevo extends AppCompatActivity {
    private DatePickerDialog dpdInicio;
    private DatePickerDialog dpdFin;
    private EditText etFechaInicio;
    private EditText etFechaFin;
    private String sInicio;
    private String sFin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo);
    }
}