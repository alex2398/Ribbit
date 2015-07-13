package com.avalladares.ribbit.ui.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.avalladares.ribbit.R;
import com.avalladares.ribbit.utilities.MD5Util;
import com.avalladares.ribbit.utilities.ParseConstants;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class ProfileActivity extends Activity {

    @Bind(R.id.firstNameField) TextView mFirstNameField;
    @Bind(R.id.lastNameField) TextView mLastNameField;
    @Bind(R.id.homeTownField) TextView mHomeTownField;
    @Bind(R.id.emailField) TextView mEmailField;
    @Bind(R.id.userField) TextView mUserField;
    @Bind(R.id.userHeadLabel) TextView mUserHeadLabel;
    @Bind(R.id.imageView) ImageView mAvatar;
    @Bind(R.id.profileProgressBar) ProgressBar mProgressBar;
    @Bind(R.id.cancelButton) Button mCancelButton;

    protected ParseUser mUserFound = new ParseUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ButterKnife.bind(this);

        String user = getIntent().getStringExtra("username");

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", user);

        mProgressBar.setVisibility(View.VISIBLE);
        query.findInBackground(new FindCallback<ParseUser>() {

            @Override
            public void done(List<ParseUser> user_found, ParseException e) {
                mProgressBar.setVisibility(View.INVISIBLE);

                if (e == null) {
                    mUserFound = user_found.get(0);
                    mUserHeadLabel.setText(mUserFound.getUsername().toString());
                    mUserField.setText(mUserFound.getUsername().toString());
                    if (mUserFound.get(ParseConstants.KEY_FIRSTNAME) != null)
                        mFirstNameField.setText(mUserFound.get(ParseConstants.KEY_FIRSTNAME).toString());
                    if (mUserFound.get(ParseConstants.KEY_LASTNAME) != null)
                        mLastNameField.setText(mUserFound.get(ParseConstants.KEY_LASTNAME).toString());
                    mEmailField.setText(mUserFound.getEmail().toString());
                    if (mUserFound.get(ParseConstants.KEY_HOMETOWN) != null)
                        mHomeTownField.setText(mUserFound.get(ParseConstants.KEY_HOMETOWN).toString());


                    String email = mUserFound.getEmail().toLowerCase();

                    // Obtenemos la imagen de gravatar

                    if (email.equals("")) {
                        mAvatar.setImageResource(R.drawable.avatar_empty);
                    } else {
                        // Para obtener la imagen de gravatar obtenemos el hash del email
                        // Despues pasamos la url con el hash y los modificadores s= para tamaño
                        // y d404 para que nos de un error 404 si no tiene imagen asociada
                        String gravatarHash = MD5Util.md5Hex(email);
                        String gratavarURL = "http://www.gravatar.com/avatar/" + gravatarHash + "?s=204"
                                + "&d=404";

                        Picasso.with(getApplicationContext())
                                .load(gratavarURL)
                                .placeholder(R.drawable.avatar_empty) // Si nos devuelve error 404 usamos esta imagen por defecto
                                .into(mAvatar);


                    }

                } else {

                    AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                    builder.setMessage(e.getMessage());
                    builder.setTitle(getString(R.string.title_error_message));
                    builder.setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();

                }
            }

        });

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });



    }


}
