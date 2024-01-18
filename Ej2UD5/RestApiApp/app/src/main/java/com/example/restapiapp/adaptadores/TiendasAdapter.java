package com.example.restapiapp.adaptadores;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.restapiapp.entidades.Tienda;
import com.example.restapiapp.R;

import org.json.JSONException;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class TiendasAdapter extends BaseAdapter {

  private final Context context;
  private final ArrayList<Tienda> tiendas;
  private TiendasAdapterCallback callback;

  public TiendasAdapter(Context context, ArrayList<Tienda> tiendas){
    super();
    this.context=context;
    this.tiendas=tiendas;
  }

  @Override
  public int getCount() {
    return tiendas.size();
  }

  @Override
  public Object getItem(int position) {
    if(tiendas==null) {
      return null;
    }
    else{
      return tiendas.get(position);
    }
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    View item = convertView;
    TiendaWrapper liWrapper;
    if (item == null) {
      liWrapper = new TiendaWrapper();
      LayoutInflater inflater = ((Activity) context).getLayoutInflater();
      item = inflater.inflate(R.layout.tienda_item, parent, false);
      liWrapper.nombre = item.findViewById(R.id.tv_li_nombre_tienda);
      liWrapper.distancia = item.findViewById(R.id.tv_li_distancia_tienda);
      liWrapper.editar = item.findViewById(R.id.bt_li_update);
      liWrapper.eliminar = item.findViewById(R.id.bt_li_delete);
      item.setTag(liWrapper);
    } else {
      liWrapper = (TiendaWrapper) item.getTag();
    }
    Tienda tienda = tiendas.get(position);
    liWrapper.nombre.setText(tienda.getNombre());
    DecimalFormat decimalFormat = new DecimalFormat("#.##");
    liWrapper.distancia.setText("A " + decimalFormat.format(tienda.getDistancia())+" millas de tí");
    liWrapper.editar.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        try {
          callback.editarPressed(position);
        } catch (JSONException e) {
          throw new RuntimeException(e);
        }
      }
    });
    liWrapper.eliminar.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        callback.eliminarPressed(position);
      }
    });
    return item;
  }

  static class TiendaWrapper {
    TextView nombre;
    TextView distancia;
    ImageButton eliminar;
    ImageButton editar;
  }

  public void setCallback(TiendasAdapterCallback callback){
    this.callback = callback;
  }

  public interface TiendasAdapterCallback {
    public void eliminarPressed(int position);
    public void editarPressed(int position) throws JSONException;
  }

}
