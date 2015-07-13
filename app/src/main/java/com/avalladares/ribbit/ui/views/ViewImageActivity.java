package com.avalladares.ribbit.ui.views;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.avalladares.ribbit.R;
import com.squareup.picasso.Picasso;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.OnClick;


public class ViewImageActivity extends Activity {

    ProgressBar mProgressBar;
    Button mDestroyImageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);

        mProgressBar = (ProgressBar)findViewById(R.id.progressBarImageView);
        mProgressBar.setVisibility(View.VISIBLE);

        ImageView imageView = (ImageView)findViewById(R.id.imageView);
        mDestroyImageButton = (Button)findViewById(R.id.destroyImageButton);

        mDestroyImageButton.setOnClickListener(listener);

        Uri imageUri = getIntent().getData();

        // Usamos la lib Picasso para leer la imagen de nuestra url y colocarla en el imageView
        Picasso.with(this).load(imageUri.toString()).into(imageView);
        mProgressBar.setVisibility(View.INVISIBLE);

        // Desactivamos el quitar la imagen a los 10 segundos
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //finish();
            }
        }, 10 * 1000); // 10 segundos
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };
}
