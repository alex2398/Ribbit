package com.avalladares.ribbit.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;

import com.avalladares.ribbit.R;
import com.avalladares.ribbit.utilities.ParseConstants;
import com.parse.ParseUser;

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
}
