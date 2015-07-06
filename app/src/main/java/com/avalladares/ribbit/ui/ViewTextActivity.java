package com.avalladares.ribbit.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;

import com.avalladares.ribbit.R;
import com.avalladares.ribbit.utilities.ParseConstants;


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
