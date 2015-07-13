package com.avalladares.ribbit.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.avalladares.ribbit.R;
import com.avalladares.ribbit.utilities.ParseConstants;
import com.parse.ParseUser;
 /* Add a button next to the camera in the Action Bar that starts a new Activity with an EditText and a button.
    Capture the text the user enters and take them to the recipients activity to choose recipients. Then adapt
    the code to send a the text as a message instead of the current path for photos or videos.*/

public class MessageActivity extends Activity {

    protected Button mSendButton;
    protected Button mCancelButton;
    protected EditText mMessageText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        mSendButton = (Button) findViewById(R.id.sendTextButton);
        mCancelButton = (Button) findViewById(R.id.cancelMessageButton);
        mMessageText = (EditText) findViewById(R.id.messageEditText);

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String texto = mMessageText.getText().toString();
                Intent recipientsIntent = new Intent(MessageActivity.this,RecipientsActivity.class);
                // Pasamos el mensaje en el intent
                recipientsIntent.putExtra(ParseConstants.KEY_FILE_TYPE,ParseConstants.TYPE_TEXT);
                recipientsIntent.putExtra(ParseConstants.TYPE_TEXT, texto);
                recipientsIntent.putExtra(ParseConstants.KEY_SENDER_ID, ParseUser.getCurrentUser().getObjectId());
                startActivityForResult(recipientsIntent, MainActivity.SEND_TEXT);

                finish();

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
