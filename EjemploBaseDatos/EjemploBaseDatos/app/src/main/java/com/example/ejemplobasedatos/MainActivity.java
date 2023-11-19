package com.example.ejemplobasedatos;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ejemplobasedatos.adaptadores.AlumnosAdapter;
import com.example.ejemplobasedatos.entidades.Alumno;
import com.example.ejemplobasedatos.util.DBHelper;

import java.text.ParseException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AlumnosAdapter.AlumnosAdapterCallback {

    private DBHelper dbHelper;
    private ArrayList<Alumno> alumnos;
    private AlumnosAdapter alumnosAdapter;
    private ListView listView;
    private ActivityResultLauncher<Intent> nuevoResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        cargarAlumnos();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper=DBHelper.getInstance(this);
        cargarAlumnos();
    }

    public void irNuevoAlumno(View view){
        Intent myIntent = new Intent().setClass(this, Nuevo.class);
        nuevoResultLauncher.launch(myIntent);
    }

    private void cargarAlumnos(){
        ArrayList<Alumno> auxAlumnos;//En esta lista auxiliar cargaremos los alumnos desde la base de datos
        if(dbHelper==null){
            dbHelper = DBHelper.getInstance(this);/*Obtenemos la instancia de la clase auxiliar
            de acceso a la base de datos*/
        }
        try {//Cargamos los alumnos desde la base de datos
            auxAlumnos=dbHelper.getAlumnos();
        } catch (ParseException e) {
            auxAlumnos=new ArrayList<>();
        }
        if(alumnos==null){/*Inicializamos la lista de alumnos  que asociamos al adaptador, ya sea creando
        un anueva instancia si es la primera vez o vaciando la lista si ya se había creado previamente*/
            alumnos=new ArrayList<>();
        }
        else{
            alumnos.clear();
        }
        alumnos.addAll(auxAlumnos);/*Añadimos a la lista asociada al adaptador los alumnos cargados desde
        la base de datos*/
        if(alumnosAdapter==null) {//Aquí creamos el adaptador por primera vez
            alumnosAdapter = new AlumnosAdapter(this, alumnos);//Creamos el adaptador
            alumnosAdapter.setCallback(this);/*Aquí indicamos que esta actividad será la encargada de
            implementar los métodos de editar y eliminar del adaptador*/
            if(listView==null){//Obtenemos la vista de la lista de alumnos desde la interfaz
                listView=findViewById(R.id.lv_alumnos);
            }
            listView.setAdapter(alumnosAdapter);//Asignamos a la vista de la lista su adaptador
        }
        else{/*Si el adaptador ya estaba creado, sólo tenemos que indicarle que los datos asociados a la
        lista se han modificado*/
            alumnosAdapter.notifyDataSetChanged();
        }
        TextView tvNoDatos=findViewById(R.id.tv_no_data);
        if(alumnos.isEmpty()){//Si no tenemos alumnos mostramos el mensaje de que no hay datos
            tvNoDatos.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        }
        else{
            tvNoDatos.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void deletePressed(int position) {
        AlertDialog diaBox = AskOption(position);
        diaBox.show();//Mostramos un diálogo de confirmación
    }

    private AlertDialog AskOption(final int position)
    {
        AlertDialog myQuittingDialogBox =new AlertDialog.Builder(this)
                /*Establecemos el mensaje, el título del diálogo y los botones de confirmación
                * y cancelación*/
                .setTitle(R.string.eliminar_alumno)
                .setMessage(R.string.are_you_sure)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        eliminarAlumno(position);//Si confirmamos eliminamos el alumno
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();//En caso contrario ocultamos el diálogo
                    }
                })
                .create();
        return myQuittingDialogBox;
    }

    private void eliminarAlumno(int position){
        if(alumnos!=null){//Comprobamos que la lista de alumnos está creada
            if(alumnos.size()>position){//Comprobamos que la posición es correcta
                if(dbHelper!=null){/*Comprobamos que la clase auxiliar de acceso a la base de datos
                está inicializada*/
                    Alumno alumno=alumnos.get(position);/*Obtenemos el alumno que se corresponde
                    con esa posición*/
                    boolean eliminado=dbHelper.eliminarAlumno((long)alumno.getIdAlumno());/*Borramos
                    el alumno de la base de datos*/
                    if(eliminado){//Si hemos borrado el alumno sin problemas, recargamos la lista
                        cargarAlumnos();
                    }
                    else{
                        showError("error.IOException");
                    }
                }
                else{
                    showError("error.desconocido");
                }
            }
            else{
                showError("error.desconocido");
            }
        }
        else{
            showError("error.desconocido");
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

    @Override
    public void editPressed(int position) {
        if(alumnos!=null) {//Comprobamos que la lista de alumnos está creada
            if (alumnos.size() > position) {//Comprobamos que la posición es correcta
                Alumno alumno=alumnos.get(position);//Obtenemos el alumno que se corresponde con esta posición
                Intent myIntent = new Intent().setClass(this, Modificar.class);
                myIntent.putExtra("idAlumno",alumno.getIdAlumno());//Pasamos el identificador del alumno como argumento
                nuevoResultLauncher.launch(myIntent);
            }
        }
    }
}