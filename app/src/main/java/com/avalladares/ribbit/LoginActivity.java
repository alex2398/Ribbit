package com.avalladares.ribbit;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parse.LogInCallback;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.text.ParseException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class LoginActivity extends Activity {
    @Bind(R.id.signupText) TextView mSignupText;
    @Bind(R.id.usernameField) EditText mUserNameField;
    @Bind(R.id.passwordField) EditText mPasswordField;
    @Bind(R.id.loginButton) Button mLoginButton;
    @Bind(R.id.progressBar) ProgressBar mProgressBar;
    @Bind(R.id.forgotTextView) TextView mForgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);


        mForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotPassword.class);
                startActivity(intent);
            }
        });

        mSignupText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }


        @OnClick(R.id.loginButton)
        public void login() {
        String userName = mUserNameField.getText().toString().trim();
        String userPassword = mPasswordField.getText().toString().trim();

        if (userName.isEmpty() || userPassword.isEmpty()) {
            // Bloque para crear un cuadro de dialogo (prefiero toast)
            AlertDialog.Builder builder= new AlertDialog.Builder(LoginActivity.this);
            builder.setMessage(R.string.login_error_message);
            builder.setTitle(R.string.login_error_title);
            builder.setPositiveButton(android.R.string.ok, null);
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            //login
            mProgressBar.setVisibility(View.VISIBLE);
            ParseUser.logInInBackground(userName, userPassword, new LogInCallback() {
                @Override
                public void done(ParseUser parseUser, com.parse.ParseException e) {
                    if (parseUser != null) {
                        mProgressBar.setVisibility(View.INVISIBLE);
                        // Success!!
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        // Signup failed. Look at the ParseException to see what happened.
                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                        builder.setMessage(e.getMessage());
                        builder.setTitle(R.string.login_error_title);
                        builder.setPositiveButton(android.R.string.ok, null);
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                }
            });



        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
