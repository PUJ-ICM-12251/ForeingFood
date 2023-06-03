package com.example.proyectforeingfood;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;


import com.google.gson.Gson;

import com.google.gson.reflect.TypeToken;


import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

public class ListaRestaurantes extends AppCompatActivity {

    private ListView listViewRestaurants;
    private List<Restaurante> restaurantes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_restaurantes);

        listViewRestaurants = findViewById(R.id.listViewRestaurants);

        // Analizar el archivo JSON y obtener la lista de restaurantes
        restaurantes = parseRestaurantesFromJson();

        // Crear y configurar el adaptador personalizado
        RestaurantAdapter restaurantAdapter = new RestaurantAdapter(this, restaurantes);
        listViewRestaurants.setAdapter(restaurantAdapter);

        // Establecer el listener de clic en el ListView
        listViewRestaurants.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Obtener el restaurante seleccionado
                Restaurante restauranteSeleccionado = restaurantes.get(position);

                // Abrir la actividad del mapa y pasar la posición del restaurante
                Intent intent = new Intent(ListaRestaurantes.this, MapaActivity.class);
                intent.putExtra("latitud", restauranteSeleccionado.getLatitud());
                intent.putExtra("longitud", restauranteSeleccionado.getLongitud());
                startActivity(intent);
            }
        });
    }

    private List<Restaurante> parseRestaurantesFromJson() {
        try {
            InputStream inputStream = getResources().openRawResource(R.raw.restaurantes);
            Reader reader = new InputStreamReader(inputStream);

            // Utilizar Gson para analizar el archivo JSON
            Gson gson = new Gson();
            RestaurantesResponse response = gson.fromJson(reader, new TypeToken<RestaurantesResponse>() {}.getType());

            if (response != null) {
                return response.getRestaurantes();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}