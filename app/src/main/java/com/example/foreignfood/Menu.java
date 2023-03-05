package com.example.foreignfood;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;

import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;


public class Menu extends AppCompatActivity {

    ImageView imageView;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        imageView = (ImageView)findViewById(R.id.imageView3);
        textView = (TextView)findViewById(R.id.textView2);

        imageView.animate().alpha(0f).setDuration(0);
        textView.animate().alpha(0f).setDuration(0);

        imageView.animate().alpha(1f).setDuration(1000).setListener(new AnimatorListenerAdapter() {
            /**
             * {@inheritDoc}
             *
             * @param animation
             */
            @Override
            public void onAnimationEnd(Animator animation) {
                textView.animate().alpha(1f).setDuration(800);

            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent = new Intent(Menu.this,Menu.class);
                startActivity(intent);
                finish();

            }
        },3000);

    }
}