package com.avalladares.ribbit.ui;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ImageView;

import com.avalladares.ribbit.R;
import com.squareup.picasso.Picasso;

import java.util.Timer;
import java.util.TimerTask;


public class ViewImageActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);

        ImageView imageView = (ImageView)findViewById(R.id.imageView);

        Uri imageUri = getIntent().getData();
        // Usamos la lib Picasso para leer la imagen de nuestra url y colocarla en el imageView
        Picasso.with(this).load(imageUri.toString()).into(imageView);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                finish();
            }
        }, 10 * 1000); // 10 segundos
    }
}
