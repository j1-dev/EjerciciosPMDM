package com.example.ejemplobasedatos.adaptadores;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.ejemplobasedatos.R;
import com.example.ejemplobasedatos.entidades.Alumno;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class AlumnosAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Alumno> alumnos;
    private AlumnosAdapterCallback callback;

    public AlumnosAdapter(Context context, ArrayList<Alumno> alumnos){
        super();/*Creamos un constructor al que le pasamos la lista de alumnos, el contexto para
        poder acceder a los recursos y llamamos a su método súper*/
        this.context=context;
        this.alumnos=alumnos;
    }

    @Override
    public int getCount() {
        return alumnos.size();//El número de elementos de la lista será el número de alumnos que tengamos
    }

    @Override
    public Object getItem(int position) {
        if(alumnos==null) {//El ítem se corresponde con el alumno que se encuentra en esa posición
            return null;
        }
        else{
            return alumnos.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;//Podemos establecer como identificador para una posición dada la propia posición
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View item = convertView;
        AlumnoWrapper alWrapper;
        if (item == null) {/*Si la vista correspondiente a esta posición se crea por primera vez,
        crearemos una instancia de AlumnoWrapper a la que le asignaremos los widgets correspondientes
        y lo asociaremos a la correspondiente vista*/
            alWrapper = new AlumnoWrapper();
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            item = inflater.inflate(R.layout.lista_item, parent, false);
            alWrapper.nombreApellidos = item.findViewById(R.id.tv_li_nombre_apellidos);
            alWrapper.nivelEstudios = item.findViewById(R.id.tv_li_nivel_estudios);
            alWrapper.genero = item.findViewById(R.id.tv_li_genero);
            alWrapper.fechaNacimiento = item.findViewById(R.id.tv_li_fecha_nacimiento);
            alWrapper.dni = item.findViewById(R.id.tv_li_dni);
            alWrapper.eliminar = item.findViewById(R.id.ib_li_eliminar);
            alWrapper.editar = item.findViewById(R.id.ib_li_editar);
            item.setTag(alWrapper);//Aquí asociamos a la vista sus variables estáticas
        } else {/*Si la vista ya se había creado anteriormente, únicamente recuperamos las variables
        estáticas que contienen los widgets que asociamos inicialmente a la vista*/
            alWrapper = (AlumnoWrapper) item.getTag();
        }
        Alumno alumno = alumnos.get(position);//Recuperamos el alumno que corresponde a esta posición
        alWrapper.nombreApellidos.setText(alumno.getNombre()+" "+alumno.getApellidos());/*Rellenamos
        los widgets de la vista con los valores correspondientes del alumno*/
        String sestudios;
        Resources res=context.getResources();
        switch(alumno.getNivelEstudios()){
            case 1: sestudios=res.getString(R.string.secondary_education);
                break;
            case 2: sestudios=res.getString(R.string.baccalaureate);
                break;
            case 3: sestudios=res.getString(R.string.grado);
                break;
            case 4: sestudios=res.getString(R.string.postgrado);
                break;
            default: sestudios=res.getString(R.string.nivel_estudios);
                break;
        }
        alWrapper.nivelEstudios.setText(sestudios);
        String sgenero;
        switch(alumno.getGenero()){
            case 0: sgenero=res.getString(R.string.man);
                break;
            case 1: sgenero=res.getString(R.string.woman);
                break;
            case 2: sgenero=res.getString(R.string.other);
                break;
            default: sgenero="";
                break;
        }
        alWrapper.genero.setText(sgenero);
        alWrapper.dni.setText(alumno.getDni());
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
        if(alumno.getFechaNacimiento()!=null){
            alWrapper.fechaNacimiento.setText(dateFormatter.format(alumno.getFechaNacimiento()));
        }
        else{
            alWrapper.fechaNacimiento.setText("");
        }
        alWrapper.editar.setOnClickListener(new View.OnClickListener() {/*Sobreescribimos el evento onClick
        del botón editar de la vista para que al pulsarlo ejecute el método editar del callback de este
        adaptador. Hacemos lo mismo para el botón eliminar*/
            @Override
            public void onClick(View v) {
                callback.editPressed(position);
            }
        });
        alWrapper.eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.deletePressed(position);
            }
        });
        return item;
    }

    static class AlumnoWrapper {/*Esta clase auxiliar la utilizamos para que nuestro ListView consuma
    menos recursos y funcione a mayor velocidad. Tendremos referencias en memoria de cada widget
    que hay en el layout que definimos para los items de la lista*/
        TextView nombreApellidos;
        TextView nivelEstudios;
        TextView dni;
        TextView genero;
        TextView fechaNacimiento;
        ImageButton eliminar;
        ImageButton editar;
    }

    public void setCallback(AlumnosAdapterCallback callback){/*Con este método indicaremos al adaotador cuál
    va a ser la clase encargada de implementar los métodos que definiremos a continuación*/
        this.callback = callback;
    }

    public interface AlumnosAdapterCallback {/*Aquí definimos los métodos que se van a ejecutar cuando se
    pulsen sobre los botones de editar y eliminar, pero estos métodos serán implementados en la actividad
    en la que el ListView se encuentra*/
        public void deletePressed(int position);
        public void editPressed(int position);
    }

}
