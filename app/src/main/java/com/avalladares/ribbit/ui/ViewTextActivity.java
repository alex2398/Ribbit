package com.avalladares.ribbit.ui;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.avalladares.ribbit.R;
import com.avalladares.ribbit.utilities.MD5Util;
import com.avalladares.ribbit.utilities.ParseConstants;
import com.parse.FindCallback;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.List;


public class ViewTextActivity extends Activity {

        String mail;
        String name;
        ImageView senderPic;
        TextView sender;
        TextView message_text;
    String gratavarURL;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_view_text);

            senderPic = (ImageView)findViewById(R.id.senderPicture);

            sender = (TextView) findViewById(R.id.senderName);
            message_text = (TextView) findViewById(R.id.messageText);
            String send_from = getIntent().getStringExtra(ParseConstants.KEY_SENDER_ID);

            ParseQuery<ParseUser> query = ParseUser.getQuery();
            query.whereEqualTo(ParseConstants.KEY_USER_ID, send_from);
            query.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> user, com.parse.ParseException e) {
                    if (e == null) {
                        mail = user.get(0).getEmail();
                        name = user.get(0).getUsername();
                        String gravatarHash = MD5Util.md5Hex(mail);
                        gratavarURL = "http://www.gravatar.com/avatar/" + gravatarHash + "?s=204"
                                + "&d=404";


                    } else {
                        Log.d("ALEX", e.getMessage());
                    }

                }
            });

            sender.setText(getString(R.string.message_from) + " " + name);
            message_text.setText(getIntent().getStringExtra(ParseConstants.TYPE_TEXT));
            Picasso.with(getApplicationContext())
                    .load(gratavarURL)
                    .placeholder(R.drawable.avatar_empty) // Si nos devuelve error 404 usamos esta imagen por defecto
                    .into(senderPic);

        }
}
