package com.avalladares.ribbit;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.Parse;
import com.parse.ParseUser;

import org.w3c.dom.Text;

import butterknife.Bind;
import butterknife.ButterKnife;


public class ProfileActivity extends ActionBarActivity {

    @Bind(R.id.firstNameField) TextView mFirstNameField;
    @Bind(R.id.lastNameField) TextView mLastNameField;
    @Bind(R.id.homeTownField) TextView mHomeTownField;
    @Bind(R.id.emailField) TextView mEmailField;
    @Bind(R.id.userField) TextView mUserField;
    @Bind(R.id.userHeadLabel) TextView mUserHeadLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ButterKnife.bind(this);

        ParseUser user = ParseUser.getCurrentUser();
        mUserHeadLabel.setText(user.getUsername().toString());
        mUserField.setText(user.getUsername().toString());
        if (user.get(ParseConstants.KEY_FIRSTNAME) != null) mFirstNameField.setText(user.get(ParseConstants.KEY_FIRSTNAME).toString());
        if (user.get(ParseConstants.KEY_LASTNAME) != null) mLastNameField.setText(user.get(ParseConstants.KEY_LASTNAME).toString());
        mEmailField.setText(user.getEmail().toString());
        if (user.get(ParseConstants.KEY_HOMETOWN) != null) mHomeTownField.setText(user.get(ParseConstants.KEY_HOMETOWN).toString());

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
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