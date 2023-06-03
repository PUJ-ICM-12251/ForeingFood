package com.example.proyectforeingfood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrarseActivity extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword, editTextNombre, editTextNacionalidad;
    private Button btnRegistrar;

    private FirebaseAuth mAuth;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrarse);

        // Inicializar FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Obtener referencias de los controles
        editTextNombre = findViewById(R.id.editTextNombre);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextNacionalidad = findViewById(R.id.editTextNacionalidad);
        btnRegistrar = findViewById(R.id.btnRegistrar);

        // Configurar evento click del botón registrar
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarUsuario();
            }
        });
    }

    private void registrarUsuario() {
        // Obtener los valores ingresados
        String username = editTextNombre.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String nationality = editTextNacionalidad.getText().toString().trim();

        // Validar que los campos no estén vacíos
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(getApplicationContext(), "Ingrese un nombre de usuario", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Ingrese un correo electrónico", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Ingrese una contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(nationality)) {
            Toast.makeText(getApplicationContext(), "Ingrese una nacionalidad", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear el usuario en Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Registro exitoso, obtener el usuario actual
                            FirebaseUser user = mAuth.getCurrentUser();

                            // Guardar la información adicional en la base de datos
                            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
                            String userId = user.getUid();

                            User newUser = new User(userId, username, email, nationality);
                            usersRef.child(userId).setValue(newUser);

                            // Redirigir al usuario a la actividad principal
                            startActivity(new Intent(RegistrarseActivity.this, MainActivity.class));
                            finish();
                        } else {
                            // Registro fallido, mostrar mensaje de error
                            Toast.makeText(getApplicationContext(), "Error al registrar. Inténtelo nuevamente.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}