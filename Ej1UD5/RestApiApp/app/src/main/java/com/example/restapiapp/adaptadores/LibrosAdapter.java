package com.example.restapiapp.adaptadores;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.restapiapp.entidades.Libro;
import com.example.restapiapp.R;

import java.util.ArrayList;

public class LibrosAdapter extends BaseAdapter {

  private final Context context;
  private final ArrayList<Libro> libros;
  private LibrosAdapterCallback callback;

  public LibrosAdapter(Context context, ArrayList<Libro> libros){
    super();
    this.context=context;
    this.libros=libros;
  }

  @Override
  public int getCount() {
    return libros.size();
  }

  @Override
  public Object getItem(int position) {
    if(libros==null) {
      return null;
    }
    else{
      return libros.get(position);
    }
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    View item = convertView;
    LibroWrapper liWrapper;
    if (item == null) {
      liWrapper = new LibroWrapper();
      LayoutInflater inflater = ((Activity) context).getLayoutInflater();
      item = inflater.inflate(R.layout.libro_item, parent, false);
      liWrapper.nombre = item.findViewById(R.id.tv_li_nombre_libro);
      item.setTag(liWrapper);
    } else {
      liWrapper = (LibroWrapper) item.getTag();
    }
    Libro libro = libros.get(position);
    liWrapper.nombre.setText(libro.getNombre());
    return item;
  }

  static class LibroWrapper {
    TextView nombre;
  }

  public void setCallback(LibrosAdapterCallback callback){
    this.callback = callback;
  }

  public interface LibrosAdapterCallback {
  }

}
