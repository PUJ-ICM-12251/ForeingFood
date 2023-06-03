package com.example.proyectforeingfood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class RecuperarActivity extends AppCompatActivity {

    private EditText etCorreo;
    private Button btnRecuperarContrasena;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar);

        etCorreo = findViewById(R.id.txtEmailAddressRecuperar);
        btnRecuperarContrasena = findViewById(R.id.btnRecuperar);

        mAuth = FirebaseAuth.getInstance();

        btnRecuperarContrasena.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String correo = etCorreo.getText().toString().trim();

                if (TextUtils.isEmpty(correo)) {
                    Toast.makeText(getApplicationContext(), "Ingrese su correo electrónico registrado", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.sendPasswordResetEmail(correo)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Se ha enviado un correo para recuperar la contraseña", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(RecuperarActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(getApplicationContext(), "No se pudo enviar el correo electrónico", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}