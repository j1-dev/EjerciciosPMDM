package com.example.restapiapp.adaptadores;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.restapiapp.entidades.Alumno;
import com.example.restapiapp.R;

import org.json.JSONException;

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
            alWrapper.editar = item.findViewById(R.id.bt_li_update);
            alWrapper.eliminar = item.findViewById(R.id.bt_li_delete);
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

        alWrapper.fechaNacimiento.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            callback.verLibrosPressed(position);
          }
        });

        alWrapper.editar.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v){
            try {
              callback.editarPressed(position);
            } catch (JSONException e) {
              throw new RuntimeException(e);
            }
          }
        });

        alWrapper.eliminar.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v){
            callback.eliminarPressed(position);
          }
        });

        return item;
    }

    static class AlumnoWrapper {
        TextView nombreApellidos;
        TextView fechaNacimiento;
        ImageButton editar;
        ImageButton eliminar;

    }

    public void setCallback(AlumnosAdapterCallback callback){
        this.callback = callback;
    }

    public interface AlumnosAdapterCallback {
      public void verLibrosPressed(int position);
      public void eliminarPressed(int position);
      public void editarPressed(int position) throws JSONException;
    }

}
