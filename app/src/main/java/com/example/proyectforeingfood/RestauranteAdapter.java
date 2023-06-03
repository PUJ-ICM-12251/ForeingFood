package com.example.proyectforeingfood;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import java.util.List;


class RestaurantAdapter extends BaseAdapter {

    private Context context;
    private List<Restaurante> restaurantes;

    public RestaurantAdapter(Context context, List<Restaurante> restaurantes) {
        this.context = context;
        this.restaurantes = restaurantes;
    }

    @Override
    public int getCount() {
        return restaurantes.size();
    }

    @Override
    public Object getItem(int position) {
        return restaurantes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_restaurante, null);
        }

        Restaurante restaurante = restaurantes.get(position);

        TextView textViewNombre = convertView.findViewById(R.id.txtNombre);
        textViewNombre.setText(restaurante.getNombre());

        return convertView;
    }
}