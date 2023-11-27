package com.example.exud2.adaptadores;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.exud2.entidades.Evento;
import com.example.exud2.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class EventosAdapter extends BaseAdapter {
  private Context context;
  private ArrayList<Evento> eventos;
  private EventosAdapterCallback callback;

  public EventosAdapter(android.content.Context context, ArrayList<Evento> eventos){
    super();
    this.context=context;
    this.eventos=eventos;
  }

  @Override
  public int getCount() {
    return eventos.size();
  }

  @Override
  public Object getItem(int position)  {
    if(eventos != null && position > 0 && position < eventos.size()){
      return eventos.get(position);
    }
    return null;
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    View item = convertView;
    EventosWrapper evWrapper;
    if (item == null) {
      evWrapper = new EventosWrapper();
      LayoutInflater inflater = ((Activity) context).getLayoutInflater();
      item = inflater.inflate(R.layout.lista_item, parent, false);
      evWrapper.nombre = item.findViewById(R.id.tv_li_nombre);
      evWrapper.tipo = item.findViewById(R.id.tv_li_tipo_evento);
      evWrapper.fecha = item.findViewById(R.id.tv_li_fecha_evento);
      evWrapper.eliminar = item.findViewById(R.id.ib_li_eliminar);
      evWrapper.editar = item.findViewById(R.id.ib_li_editar);
      item.setTag(evWrapper);
    } else {
      evWrapper = (EventosWrapper) item.getTag();
    }
    Evento evento = eventos.get(position);
    evWrapper.nombre.setText(evento.getNombre());
    String tipo;
    Resources res=context.getResources();
    switch(evento.getTipo()){
      case 0: tipo="Social";
        break;
      case 1: tipo="Medico";
        break;
      case 2: tipo="Profesional";
        break;
      default: tipo="";
        break;
    }
    evWrapper.tipo.setText(tipo);

    SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
    if(evento.getFecha()!=null){
      evWrapper.fecha.setText(dateFormatter.format(evento.getFecha()));
    }
    else{
      evWrapper.fecha.setText("");
    }
    evWrapper.editar.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        callback.editPressed(position);
      }
    });
    evWrapper.eliminar.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        callback.deletePressed(position);
      }
    });
    return item;
  }

  static class EventosWrapper {
    TextView nombre;
    TextView fecha;
    TextView tipo;
    ImageButton eliminar;
    ImageButton editar;
  }

  public void setCallback(EventosAdapterCallback callback){
    this.callback = callback;
  }

  public interface EventosAdapterCallback {
    public void deletePressed(int position);
    public void editPressed(int position);
  }
}
