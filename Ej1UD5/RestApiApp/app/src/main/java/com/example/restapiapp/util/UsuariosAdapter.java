package com.example.restapiapp.util;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.restapiapp.R;
import com.example.restapiapp.entitidades.Usuario;

import java.util.ArrayList;

public class UsuariosAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Usuario> usuarios = new ArrayList<>();
    private UsuariosAdapterCallback callback;

    public UsuariosAdapter(Context context, ArrayList<Usuario> usuarios) {
        super();
        this.context = context;
        this.usuarios = usuarios;
    }

    @Override
    public int getCount() {
        if(usuarios!=null) {
            return usuarios.size();
        }
        else return 0;
    }

    @Override
    public Object getItem(int i) {
        if(usuarios!=null) {
            return usuarios.get(i);
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
        UsuarioWrapper mWrapper;
        if (item == null) {
            mWrapper = new UsuarioWrapper();
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            item = inflater.inflate(R.layout.list_item, viewGroup, false);
            mWrapper.nombre = item.findViewById(R.id.tv_li_nombre);
            mWrapper.fecha = item.findViewById(R.id.tv_li_fecha);
            mWrapper.usuario = item.findViewById(R.id.tv_li_usuario);
            mWrapper.dni = item.findViewById(R.id.tv_li_dni);
            mWrapper.edit = item.findViewById(R.id.bt_li_update);
            mWrapper.delete = item.findViewById(R.id.bt_li_delete);
            item.setTag(mWrapper);
        } else {
            mWrapper = (UsuarioWrapper) item.getTag();
        }
        Usuario usuario = usuarios.get(i);
        mWrapper.nombre.setText(usuario.getNombre()+" "+usuario.getApellidos());
        mWrapper.dni.setText(usuario.getDni());
        mWrapper.fecha.setText(usuario.getsFecha());
        mWrapper.usuario.setText(usuario.getUsername());
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

    static class UsuarioWrapper {
        TextView nombre;
        TextView usuario;
        TextView fecha;
        TextView dni;
        ImageButton edit;
        ImageButton delete;
    }

    public void setCallback(UsuariosAdapterCallback callback){
        this.callback = callback;
    }

    public interface UsuariosAdapterCallback {
        public void deletePressed(int position);
        public void editPressed(int position);
    }

}
