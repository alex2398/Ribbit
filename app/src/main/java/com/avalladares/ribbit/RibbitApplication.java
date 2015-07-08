package com.avalladares.ribbit;

import android.app.Application;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.avalladares.ribbit.utilities.ParseConstants;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by avalladares on 30/06/2015.
 */
public class RibbitApplication extends Application{




    @Override
    public void onCreate() {
        super.onCreate();
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "pIlXyyqk073KKogbMbz5pPqMJ6v1F3TiVG36S3Bz", "iG6gsvPsP9reNVYomwQbG5LeEnQetA53rfezrmC9");
        ParseInstallation.getCurrentInstallation().saveInBackground();

    }

    public static void updateParseInstallation(ParseUser user) {
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put(ParseConstants.KEY_USER_ID, user.getObjectId());
        installation.saveInBackground();
    }

}
