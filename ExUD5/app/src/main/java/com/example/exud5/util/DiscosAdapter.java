package com.example.exud5.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.exud5.R;
import com.example.exud5.entidades.Disco;

import java.util.ArrayList;

public class DiscosAdapter extends BaseAdapter {
  private Context context;
  private ArrayList<Disco> discos = new ArrayList<>();
  private DiscosAdapterCallback callback;

  public DiscosAdapter(Context context, ArrayList<Disco> discos) {
    super();
    this.context = context;
    this.discos = discos;
  }

  @Override
  public int getCount() {
    if(discos!=null) {
      return discos.size();
    }
    else return 0;
  }

  @Override
  public Object getItem(int i) {
    if(discos!=null) {
      return discos.get(i);
    }
    else return null;
  }

  @Override
  public long getItemId(int i) {
    return i;
  }

  @Override
  public View getView(int i, View view, ViewGroup viewGroup) {
    View item = view;
    DiscoWrapper mWrapper;
    if (item == null) {
      mWrapper = new DiscoWrapper();
      LayoutInflater inflater = ((Activity) context).getLayoutInflater();
      item = inflater.inflate(R.layout.list_item, viewGroup, false);
      mWrapper.nombre = item.findViewById(R.id.tv_li_nombre);
      mWrapper.escuchado = item.findViewById(R.id.tv_li_escuchado);
      mWrapper.tipo = item.findViewById(R.id.tv_li_tipo);
      mWrapper.edit = item.findViewById(R.id.bt_li_update);
      mWrapper.delete = item.findViewById(R.id.bt_li_delete);
      item.setTag(mWrapper);
    } else {
      mWrapper = (DiscoWrapper) item.getTag();
    }
    Disco disco = discos.get(i);
    mWrapper.nombre.setText(disco.getNombre());
    String stipo;
    Resources res = context.getResources();
    switch (disco.getTipo()){
      case 0: stipo = "rock";
        break;
      case 1: stipo = "pop";
        break;
      case 2: stipo = "hip-hop";
        break;
      case 3: stipo = "country";
        break;
      case 4: stipo = "latino";
        break;
      case 5: stipo = "jazz";
        break;
      case 6: stipo = "blues";
        break;
      case 7: stipo = "indie";
        break;
      default: stipo = "";
        break;
    }
    mWrapper.tipo.setText(stipo);
    mWrapper.escuchado.setText(disco.isEscuchado()+"");
    mWrapper.edit.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        callback.editPressed(i);
      }
    });
    mWrapper.delete.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        callback.deletePressed(i);
      }
    });
    return item;
  }

  static class DiscoWrapper {
    TextView nombre;
    TextView tipo;
    TextView escuchado;
    ImageButton edit;
    ImageButton delete;
  }

  public void setCallback(DiscosAdapterCallback callback){
    this.callback = callback;
  }

  public interface DiscosAdapterCallback {
    public void deletePressed(int position);
    public void editPressed(int position);
  }
}
