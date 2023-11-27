package com.example.ej1ud4.adaptadores;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.ej1ud4.R;
import com.example.ej1ud4.entidades.Videojuego;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class VideojuegosAdapter extends BaseAdapter {
  private Context context;
  private ArrayList<Videojuego> videojuegos;
  private VideojuegosAdapterCallback callback;

  public VideojuegosAdapter(Context context, ArrayList<Videojuego> videojuegos){
    super();
    this.context=context;
    this.videojuegos=videojuegos;
  }

  @Override
  public int getCount() {
    return videojuegos.size();
  }

  @Override
  public Object getItem(int position)  {
    if(videojuegos != null && position > 0 && position < videojuegos.size()){
      return videojuegos.get(position);
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
    VideojuegosWrapper vjWrapper;
    if (item == null) {
      vjWrapper = new VideojuegosWrapper();
      LayoutInflater inflater = ((Activity) context).getLayoutInflater();
      item = inflater.inflate(R.layout.lista_item, parent, false);
      vjWrapper.nombre = item.findViewById(R.id.tv_li_nombre);
      vjWrapper.genero = item.findViewById(R.id.tv_li_genero);
      vjWrapper.consola = item.findViewById(R.id.tv_li_consola);
      vjWrapper.fechaSalida = item.findViewById(R.id.tv_li_fecha_salida);
      vjWrapper.haJugado = item.findViewById(R.id.tv_li_ha_jugado);
      vjWrapper.eliminar = item.findViewById(R.id.ib_li_eliminar);
      vjWrapper.editar = item.findViewById(R.id.ib_li_editar);
      item.setTag(vjWrapper);
    } else {
      vjWrapper = (VideojuegosWrapper) item.getTag();
    }
    Videojuego videojuego = videojuegos.get(position);
    vjWrapper.nombre.setText(videojuego.getNombre());
    String consola;
    Resources res=context.getResources();
    switch(videojuego.getConsola()){
      case 0: consola=res.getString(R.string.xbox);
        break;
      case 1: consola=res.getString(R.string.playstation);
        break;
      case 2: consola=res.getString(R.string.nintendo_switch);
        break;
      case 3: consola=res.getString(R.string.multiplatform);
        break;
      default: consola=res.getString(R.string.console);
        break;
    }
    vjWrapper.consola.setText(consola);
    String sgenero;
    switch(videojuego.getGenero()){
      case 1: sgenero=res.getString(R.string.action);
        break;
      case 2: sgenero=res.getString(R.string.sports);
        break;
      case 3: sgenero=res.getString(R.string.adventure);
        break;
      case 4: sgenero=res.getString(R.string.strategy);
        break;
      case 5: sgenero=res.getString(R.string.arcade);
        break;
      case 6: sgenero=res.getString(R.string.simulation);
        break;
      case 7: sgenero=res.getString(R.string.musical);
        break;
      case 8: sgenero=res.getString(R.string.other);
        break;
      default: sgenero="";
        break;
    }
    vjWrapper.genero.setText(sgenero);
    SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
    if(videojuego.getFechaSalida()!=null){
      vjWrapper.fechaSalida.setText(dateFormatter.format(videojuego.getFechaSalida()));
    }
    else{
      vjWrapper.fechaSalida.setText("");
    }
    if(videojuego.isSeHaJugado()){
      vjWrapper.haJugado.setText(R.string.has_played);
    } else {
      vjWrapper.haJugado.setText(R.string.has_not_played);
    }
    vjWrapper.editar.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        callback.editPressed(position);
      }
    });
    vjWrapper.eliminar.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        callback.deletePressed(position);
      }
    });
    return item;
  }

  static class VideojuegosWrapper {
    TextView nombre;
    TextView fechaSalida;
    TextView genero;
    TextView consola;
    TextView haJugado;
    ImageButton eliminar;
    ImageButton editar;
  }

  public void setCallback(VideojuegosAdapterCallback callback){
    this.callback = callback;
  }

  public interface VideojuegosAdapterCallback {
    public void deletePressed(int position);
    public void editPressed(int position);
  }
}
