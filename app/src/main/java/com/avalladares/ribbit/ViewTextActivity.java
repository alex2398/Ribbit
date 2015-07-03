package com.avalladares.ribbit;

import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseObject;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.Timer;
import java.util.TimerTask;


public class ViewTextActivity extends ActionBarActivity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_view_text);


            TextView sender = (TextView) findViewById(R.id.senderName);
            TextView message_text = (TextView) findViewById(R.id.messageText);

            sender.setText(getString(R.string.message_from) + getIntent().getStringExtra(ParseConstants.KEY_SENDER_NAME));
            message_text.setText(getIntent().getStringExtra(ParseConstants.TYPE_TEXT));

        }
}
