package com.example.restapiapp.adaptadores;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.restapiapp.R;
import com.example.restapiapp.entidades.Persona;

import org.json.JSONException;

import java.util.ArrayList;

public class PersonasAdapter extends BaseAdapter {

  private final Context context;
  private final ArrayList<Persona> personas;
  private PersonasAdapterCallback callback;

  public PersonasAdapter(Context context, ArrayList<Persona> personas){
    super();
    this.context=context;
    this.personas=personas;
  }

  @Override
  public int getCount() {
    return personas.size();
  }

  @Override
  public Object getItem(int position) {
    if(personas==null) {
      return null;
    }
    else{
      return personas.get(position);
    }
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    View item = convertView;
    PersonaWrapper alWrapper;
    if (item == null) {
      alWrapper = new PersonaWrapper();
      LayoutInflater inflater = ((Activity) context).getLayoutInflater();
      item = inflater.inflate(R.layout.persona_item, parent, false);
      alWrapper.nombreApellidos = item.findViewById(R.id.tv_li_nombre_apellidos);
      alWrapper.dni = item.findViewById(R.id.tv_li_dni);
      alWrapper.editar = item.findViewById(R.id.bt_li_update);
      alWrapper.eliminar = item.findViewById(R.id.bt_li_delete);
      item.setTag(alWrapper);
    } else {
      alWrapper = (PersonaWrapper) item.getTag();
    }
    Persona persona = personas.get(position);
    alWrapper.nombreApellidos.setText(persona.getNombre()+" "+persona.getApellidos());
    alWrapper.dni.setText(persona.getDni());

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

  static class PersonaWrapper {
    TextView nombreApellidos;
    TextView dni;
    ImageButton editar;
    ImageButton eliminar;

  }

  public void setCallback(PersonasAdapterCallback callback){
    this.callback = callback;
  }

  public interface PersonasAdapterCallback {
    public void eliminarPressed(int position);
    public void editarPressed(int position) throws JSONException;
  }

}
