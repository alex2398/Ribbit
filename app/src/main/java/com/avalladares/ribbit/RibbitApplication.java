package com.avalladares.ribbit;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

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

        ParseObject testObject = new ParseObject("TestObject");
        testObject.put("foo", "bar");
        testObject.saveInBackground();


    }
}
