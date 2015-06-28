package com.avalladares.ribbit;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.parse.Parse;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.text.ParseException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class SignUpActivity extends ActionBarActivity {

    @Bind(R.id.usernameField) EditText mUserNameField;
    @Bind(R.id.passwordField) EditText mPasswordField;
    @Bind(R.id.emailField) EditText mEmailField;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);

    }
    @OnClick(R.id.signInButton)
    public void sign_up() {
        String userName = mUserNameField.getText().toString().trim();
        String userPassword = mPasswordField.getText().toString().trim();
        String userMail = mEmailField.getText().toString().trim();

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
            setSupportProgressBarIndeterminateVisibility(true);
            ParseUser newUser = new ParseUser();
            newUser.setUsername(userName);
            newUser.setPassword(userPassword);
            newUser.setEmail(userMail);

            newUser.signUpInBackground(new SignUpCallback() {

                @Override
                public void done(com.parse.ParseException e) {
                    setSupportProgressBarIndeterminateVisibility(false);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign_up, menu);
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
