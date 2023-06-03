package com.example.proyectforeingfood;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;

    private FirebaseAuth mAuth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        emailEditText = findViewById(R.id.txtEmailAddress);
        passwordEditText = findViewById(R.id.txtPassword);

        Button signInButton = findViewById(R.id.btnIniciarSesion);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        Button btnRegistrar = findViewById(R.id.btnRegistrarse);
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegistrarseActivity.class);
                startActivity(intent);
            }
        });

        Button btnRecuperarContrasena = findViewById(R.id.btnForgotPassword);
        btnRecuperarContrasena.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RecuperarActivity.class);
                startActivity(intent);
            }
        });
    }

    private void signIn() {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Inicio de sesión exitoso, redirigir al usuario a la actividad de mapa
                            Intent intent = new Intent(MainActivity.this, MenuActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // Si el inicio de sesión falla, mostrar un mensaje de error
                            Toast.makeText(MainActivity.this, "Inicio de sesión fallido", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}