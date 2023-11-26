package com.example.ej2ud4.adaptadores;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.ej2ud4.entidades.Alumno;
import com.example.ej2ud4.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class AlumnosAdapter extends BaseAdapter {

    private final Context context;
    private final ArrayList<Alumno> alumnos;
    private AlumnosAdapterCallback callback;

    public AlumnosAdapter(Context context, ArrayList<Alumno> alumnos){
        super();
        this.context=context;
        this.alumnos=alumnos;
    }

    @Override
    public int getCount() {
        return alumnos.size();
    }

    @Override
    public Object getItem(int position) {
        if(alumnos==null) {
            return null;
        }
        else{
            return alumnos.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View item = convertView;
        AlumnoWrapper alWrapper;
        if (item == null) {
            alWrapper = new AlumnoWrapper();
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            item = inflater.inflate(R.layout.alumno_item, parent, false);
            alWrapper.nombreApellidos = item.findViewById(R.id.tv_li_nombre_apellidos);
            alWrapper.fechaNacimiento = item.findViewById(R.id.tv_li_fecha_nacimiento);
//            alWrapper.verLibros = item.findViewById(R.id.bt_ver_libros);
            item.setTag(alWrapper);
        } else {
            alWrapper = (AlumnoWrapper) item.getTag();
        }
        Alumno alumno = alumnos.get(position);
        alWrapper.nombreApellidos.setText(alumno.getNombre()+" "+alumno.getApellidos());

        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
        if(alumno.getFechaNacimiento()!=null){
            alWrapper.fechaNacimiento.setText(dateFormatter.format(alumno.getFechaNacimiento()));
        }
        else{
            alWrapper.fechaNacimiento.setText("");
        }
//        alWrapper.verLibros.setOnClickListener(new View.OnClickListener(){
//          @Override
//          public void onClick(View v){callback.verLibrosPressed(position);}
//        });
        return item;
    }

    static class AlumnoWrapper {
        TextView nombreApellidos;
        TextView fechaNacimiento;
//        Button verLibros;
    }

    public void setCallback(AlumnosAdapterCallback callback){
        this.callback = callback;
    }

    public interface AlumnosAdapterCallback {
//      public void verLibrosPressed(int position);
    }

}
