package com.example.proyectforeingfood;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


public class PerfilActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1;
    private static final int CAMERA_REQUEST_CODE = 2;
    private static final int GALLERY_REQUEST_CODE = 3;

    private ImageView imageViewProfile;
    private Button buttonTakePhoto;
    private Button buttonChoosePhoto;
    private TextView textViewName;
    private TextView textViewUsername;

    private TextView textViewEmail;
    private TextView textViewNationality;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private StorageReference mStorage;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        imageViewProfile = findViewById(R.id.imagePerfil);
        buttonTakePhoto = findViewById(R.id.btnTomarFoto);
        buttonChoosePhoto = findViewById(R.id.btnSeleccionarImagen);
        textViewName = findViewById(R.id.txtNombre);
        textViewUsername = findViewById(R.id.textViewUsername);
        textViewEmail = findViewById(R.id.txtCorreo);
        textViewNationality = findViewById(R.id.txtNacionalidad);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReference();

        // Obtiene el usuario actualmente logeado
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            // Obtiene la información del usuario logeado
            String name = user.getDisplayName();
            String email = user.getEmail();

            // Muestra la información en los TextViews correspondientes
            textViewName.setText("Name: " + name);
            textViewUsername.setText("Username: " + user.getUid());
            textViewEmail.setText("Email: " + email);
            textViewNationality.setText("Nationality: " + "N/A");
        }

        buttonTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(PerfilActivity.this, Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                } else {
                    ActivityCompat.requestPermissions(PerfilActivity.this,
                            new String[]{Manifest.permission.CAMERA},
                            CAMERA_PERMISSION_REQUEST_CODE);
                }
            }
        });

        buttonChoosePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            if (photo != null) {
                uploadPhoto(photo);
            }
        } else if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            if (selectedImage != null) {
                try {
                    Bitmap photo = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                    uploadPhoto(photo);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void uploadPhoto(Bitmap photo) {
        if (photo != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageData = baos.toByteArray();

            StorageReference photoRef = mStorage.child("profile_photos").child(mAuth.getCurrentUser().getUid() + ".jpg");

            UploadTask uploadTask = photoRef.putBytes(imageData);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    photoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // Guarda la URL de la imagen en la base de datos del usuario
                            String imageUrl = uri.toString();
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                String userId = user.getUid();
                                Map<String, Object> profileData = new HashMap<>();
                                profileData.put("imageUrl", imageUrl);
                                mDatabase.child("users").child(userId).updateChildren(profileData);
                            }
                            imageViewProfile.setImageBitmap(photo);
                            Toast.makeText(PerfilActivity.this, "Photo uploaded successfully", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PerfilActivity.this, "Failed to upload photo", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}