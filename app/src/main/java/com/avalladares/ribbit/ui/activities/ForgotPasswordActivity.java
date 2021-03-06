package com.avalladares.ribbit.ui.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.avalladares.ribbit.R;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;


public class ForgotPasswordActivity extends Activity {

    Button mForgotButton;
    EditText mForgotEmail;
    ProgressBar mProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mForgotButton = (Button) findViewById(R.id.recoverPasswordButton);
        mForgotEmail = (EditText) findViewById(R.id.emailRecoverPasswordField);
        mProgressBar = (ProgressBar) findViewById(R.id.forgotProgressBar);

        mForgotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mForgotEmail.getText().toString().trim().isEmpty()) {
                    // Bloque para crear un cuadro de dialogo (prefiero toast)
                    AlertDialog.Builder builder= new AlertDialog.Builder(ForgotPasswordActivity.this);
                    builder.setMessage(getString(R.string.error_mail));
                    builder.setTitle(R.string.login_error_title);
                    builder.setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();

                } else {

                    String email = mForgotEmail.getText().toString().trim();
                    mProgressBar.setVisibility(View.VISIBLE);
                    ParseUser.requestPasswordResetInBackground(email, new RequestPasswordResetCallback() {
                        @Override
                        public void done(com.parse.ParseException e) {
                            if (e == null) {
                                mProgressBar.setVisibility(View.INVISIBLE);
                                // An email was successfully sent with reset instructions.
                                Toast.makeText(ForgotPasswordActivity.this, getString(R.string.check_mail_recover_password), Toast.LENGTH_LONG).show();
                                finish();

                            } else {
                                mProgressBar.setVisibility(View.INVISIBLE);
                                // Something went wrong. Look at the ParseException to see what's up.
                                AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPasswordActivity.this);
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
        });

    }
}
