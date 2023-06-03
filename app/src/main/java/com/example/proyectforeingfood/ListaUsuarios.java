package com.example.proyectforeingfood;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ListaUsuarios extends AppCompatActivity {

    private ListView mListView;
    private List<User> mUserList;
    private UserListAdapter mAdapter;

    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_usuarios);

        mListView = findViewById(R.id.userListView);
        mUserList = new ArrayList<>();
        mAdapter = new UserListAdapter(this, mUserList);
        mListView.setAdapter(mAdapter);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference("users");

        // Aquí obtienes los datos de los usuarios desde Firebase y los agregas a la lista
        // en el método onDataChange del Listener
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    assert user != null;
                    user.setUserId(dataSnapshot.getKey()); // Agrega el Uid del usuario a la variable user
                    mUserList.add(user);
                }
                mAdapter.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("UserListActivity", "Error al obtener datos de usuarios", error.toException());
            }
        });
    }

    private class UserListAdapter extends ArrayAdapter<User> {

        private Context context;
        private List<User> userList;

        public UserListAdapter(Context context, List<User> userList) {
            super(context, 0, userList);
            this.context = context;
            this.userList = userList;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            ViewHolder viewHolder;

            if (view == null) {
                view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.profileImageView = view.findViewById(R.id.profileImageView);
                viewHolder.emailTextView = view.findViewById(R.id.emailTextView);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            User user = userList.get(position);

            // Mostrar la foto de perfil utilizando Picasso
            //Picasso.get().load(user.getPhotoUrl()).placeholder(R.drawable.default_profile).into(viewHolder.profileImageView);

            // Mostrar el correo electrónico
            viewHolder.emailTextView.setText(user.getEmail());

            return view;
        }

        private class ViewHolder {
            ImageView profileImageView;
            TextView emailTextView;
        }

    }
}