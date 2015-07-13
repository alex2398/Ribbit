package com.avalladares.ribbit.ui.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.avalladares.ribbit.R;
import com.avalladares.ribbit.RibbitApplication;
import com.parse.LogInCallback;
import com.parse.ParseUser;

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
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
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
                    mProgressBar.setVisibility(View.INVISIBLE);
                    if (parseUser != null) {
                        // Success!!

                        // Instalamos las notificaciones para ese usuario
                        RibbitApplication.updateParseInstallation(parseUser);

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


}
