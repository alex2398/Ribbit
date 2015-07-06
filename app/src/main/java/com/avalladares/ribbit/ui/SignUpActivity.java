package com.avalladares.ribbit.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.avalladares.ribbit.R;
import com.avalladares.ribbit.utilities.ParseConstants;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class SignUpActivity extends Activity {

    @Bind(R.id.usernameField) EditText mUserNameField;
    @Bind(R.id.passwordField) EditText mPasswordField;
    @Bind(R.id.emailField) EditText mEmailField;
    @Bind(R.id.signupProgressBar) ProgressBar mProgressBar;
    @Bind(R.id.nameField) EditText mNameField;
    @Bind(R.id.lastNameField) EditText mLastNameField;
    @Bind(R.id.homeTownField) EditText mHomeTownField;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        ButterKnife.bind(this);

    }
    @OnClick(R.id.cancelButton)
    public void cancel() {
        finish();
    }
    @OnClick(R.id.signInButton)
    public void sign_up() {
        String userName = mUserNameField.getText().toString().trim();
        String userPassword = mPasswordField.getText().toString().trim();
        String userMail = mEmailField.getText().toString().trim();
        String name = mNameField.getText().toString().trim();
        String lastName = mLastNameField.getText().toString().trim();
        String homeTown = mHomeTownField.getText().toString().trim();


        if (userName.isEmpty() || userPassword.isEmpty() || userMail.isEmpty()) {
            // Bloque para crear un cuadro de dialogo (prefiero toast)
            Builder builder= new Builder(SignUpActivity.this);
            builder.setMessage(R.string.signUp_error_message);
            builder.setTitle(R.string.signUp_error_title);
            builder.setPositiveButton(android.R.string.ok, null);
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            //create new user
            mProgressBar.setVisibility(View.VISIBLE);
            ParseUser newUser = new ParseUser();
            newUser.setUsername(userName);
            newUser.setPassword(userPassword);
            newUser.setEmail(userMail);
            newUser.put(ParseConstants.KEY_FIRSTNAME, name);
            newUser.put(ParseConstants.KEY_LASTNAME, lastName);
            newUser.put(ParseConstants.KEY_HOMETOWN,homeTown);

            newUser.signUpInBackground(new SignUpCallback() {

                @Override
                public void done(com.parse.ParseException e) {
                    mProgressBar.setVisibility(View.INVISIBLE);
                    if (e == null) {
                        // Success!!
                        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        // Sign up didn't succeed. Look at the ParseException
                        // to figure out what went wrong
                        Builder builder= new Builder(SignUpActivity.this);
                        builder.setMessage(e.getMessage());
                        builder.setTitle(R.string.signUp_error_title);
                        builder.setPositiveButton(android.R.string.ok, null);
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                }


            });
        }

    }
}
