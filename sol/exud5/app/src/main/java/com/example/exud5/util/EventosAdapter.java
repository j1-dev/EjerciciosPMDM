package com.example.exud5.util;

import android.app.Activity;
import android.app.RecoverableSecurityException;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.exud5.R;
import com.example.exud5.entidades.Evento;

import java.util.ArrayList;

public class EventosAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Evento> eventos = new ArrayList<>();
    private EventosAdapterCallback callback;

    public EventosAdapter(Context context, ArrayList<Evento> eventos) {
        super();
        this.context = context;
        this.eventos = eventos;
    }

    @Override
    public int getCount() {
        if(eventos!=null) {
            return eventos.size();
        }
        else return 0;
    }

    @Override
    public Object getItem(int i) {
        if(eventos!=null) {
            return eventos.get(i);
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
        EventoWrapper mWrapper;
        if (item == null) {
            mWrapper = new EventoWrapper();
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            item = inflater.inflate(R.layout.list_item, viewGroup, false);
            mWrapper.nombre = item.findViewById(R.id.tv_li_nombre);
            mWrapper.fecha = item.findViewById(R.id.tv_li_fecha);
            mWrapper.tipo = item.findViewById(R.id.tv_li_tipo);
            mWrapper.edit = item.findViewById(R.id.bt_li_update);
            mWrapper.delete = item.findViewById(R.id.bt_li_delete);
            item.setTag(mWrapper);
        } else {
            mWrapper = (EventoWrapper) item.getTag();
        }
        Evento evento = eventos.get(i);
        mWrapper.nombre.setText(evento.getNombre());
        String stipo;
        Resources res = context.getResources();
        switch (evento.getTipo()){
            case 1: stipo = res.getString(R.string.social);
                break;
            case 2: stipo = res.getString(R.string.medical_appointment);
                break;
            default: stipo = res.getString(R.string.professional);
                break;
        }
        mWrapper.tipo.setText(stipo);
        mWrapper.fecha.setText(evento.getsFecha());
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

    static class EventoWrapper {
        TextView nombre;
        TextView tipo;
        TextView fecha;
        ImageButton edit;
        ImageButton delete;
    }

    public void setCallback(EventosAdapterCallback callback){
        this.callback = callback;
    }

    public interface EventosAdapterCallback {
        public void deletePressed(int position);
        public void editPressed(int position);
    }
}
