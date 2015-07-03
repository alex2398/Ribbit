package com.avalladares.ribbit;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
 /* Add a button next to the camera in the Action Bar that starts a new Activity with an EditText and a button.
    Capture the text the user enters and take them to the recipients activity to choose recipients. Then adapt
    the code to send a the text as a message instead of the current path for photos or videos.*/

public class MessageActivity extends ActionBarActivity {

    protected Button mSendButton;
    protected EditText mMessageText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        mSendButton = (Button) findViewById(R.id.sendTextButton);
        mMessageText = (EditText) findViewById(R.id.messageEditText);

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String texto = mMessageText.getText().toString();
                Intent recipientsIntent = new Intent(MessageActivity.this,RecipientsActivity.class);
                // Pasamos el mensaje en el intent
                recipientsIntent.putExtra(ParseConstants.KEY_FILE_TYPE,ParseConstants.TYPE_TEXT);
                recipientsIntent.putExtra(ParseConstants.TYPE_TEXT, texto);
                startActivityForResult(recipientsIntent,MainActivity.SEND_TEXT);
                finish();

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_message, menu);
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
