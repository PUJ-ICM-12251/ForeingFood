package com.example.proyectforeingfood;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity {

    Button btnPerfil, btnMapa, btnChat, btnRestaurantes, btnListaRestaurantes, btnListaUsers;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        btnPerfil = findViewById(R.id.btnPerfil);
        btnMapa = findViewById(R.id.btnMapa);
        btnChat = findViewById(R.id.btnChat);
        btnListaUsers = findViewById(R.id.btnListaUsers);
        btnListaRestaurantes = findViewById(R.id.btnListaRestaurantes);


        btnMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent map = new Intent(MenuActivity.this, MapaActivity.class);
                startActivity(map);
            }
        });

        btnPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent perfil = new Intent(MenuActivity.this, PerfilActivity.class);
                startActivity(perfil);
            }
        });
        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chat = new Intent(MenuActivity.this, chat.class);
                startActivity(chat);
            }
        });

        btnListaUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent listRest = new Intent(MenuActivity.this, ListaUsuarios.class);
                startActivity(listRest);
            }
        });

        btnListaRestaurantes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent listRest = new Intent(MenuActivity.this, ListaRestaurantes.class);
                startActivity(listRest);
            }
        });



    }
}