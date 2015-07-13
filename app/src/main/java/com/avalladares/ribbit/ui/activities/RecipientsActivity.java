package com.avalladares.ribbit.ui.activities;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.avalladares.ribbit.R;
import com.avalladares.ribbit.adapters.UsersAdapter;
import com.avalladares.ribbit.utilities.FileHelper;
import com.avalladares.ribbit.utilities.ParseConstants;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;


public class RecipientsActivity extends ActionBarActivity {
    public static final String TAG = RecipientsActivity.class.getSimpleName();

    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;
    protected List<ParseUser> mFriends;
    protected MenuItem mMenuItem;
    protected TextView mEmptyText;
    protected ProgressBar mProgressBar;

    protected Uri mMediaUri;
    protected String mFileType;
    protected String mTextMessage;
    protected String mSenderMail;
    protected GridView mGridView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_grid);

        mGridView = (GridView)findViewById(R.id.friendsGrid);
        mProgressBar = (ProgressBar)findViewById(R.id.friendsFragmentProgressBar);
        TextView emptyTextView = (TextView)findViewById(android.R.id.empty);
        mGridView.setEmptyView(emptyTextView);
        mGridView.setOnItemClickListener(mOnItemClickListener);

        mMenuItem = (MenuItem) findViewById(R.id.sendTo);

        // Obtenemos el path del archivo mediante el data que hemos pasado en el intent
        // desde el MainActivity

        mMediaUri = getIntent().getData();
        mFileType = getIntent().getExtras().getString(ParseConstants.KEY_FILE_TYPE);
        if (mFileType.equals(ParseConstants.TYPE_TEXT)) {
            mTextMessage = getIntent().getExtras().getString(ParseConstants.TYPE_TEXT);
            mSenderMail = getIntent().getExtras().getString(ParseConstants.KEY_SENDER_MAIL);
        }

        mGridView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

    }

    @Override
    protected void onResume () {
            super.onResume();

        mProgressBar.setVisibility(View.VISIBLE);
        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);

        ParseQuery<ParseUser> query = mFriendsRelation.getQuery();
        query.addAscendingOrder(ParseConstants.KEY_USERNAME);

        query.findInBackground(new FindCallback<ParseUser>() {

            @Override
            public void done(List<ParseUser> friends, ParseException e) {
                mProgressBar.setVisibility(View.INVISIBLE);

                if (e == null) {
                    mFriends = friends;
                    // Creamos el array del mismo tamaño que lo devuelvo en la query
                    String[] usernames = new String[mFriends.size()];

                    // Rellenamos el array de nombres
                    int i = 0;
                    for (ParseUser user : mFriends) {
                        usernames[i] = user.getUsername();
                        i++;
                    }

                    // Pasamos con un adaptador el array de nombres a la ListView del Layout con un contenedor simple_list_item_checked
                    if (mGridView.getAdapter() == null) {
                        UsersAdapter adapter = new UsersAdapter(RecipientsActivity.this, mFriends);
                        mGridView.setAdapter(adapter);
                    } else {
                        // Refill it!
                        ((UsersAdapter)mGridView.getAdapter()).refill(mFriends);
                    }



                } else {
                    Log.e(TAG, e.getMessage());


                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.


            getMenuInflater().inflate(R.menu.menu_recipients, menu);
            mMenuItem = menu.getItem(0);
            return true;


    }

    /*  Este e el metodo que usamos para hacer clic en el boton enviar (al ser una clase que extiende
        ActionBarActivity en lugar de ListActivity, hemos tenido que rehacer el código para asociar al
        MenuItem */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            // Este es el id en el layout con id sendTo:
            case R.id.sendTo :
                ParseObject message = createMessage();
                if (message == null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(getString(R.string.error_creating_message));
                    builder.setTitle(getString(R.string.title_error_message));
                    builder.setPositiveButton(android.R.string.ok,null);
                    AlertDialog dialog = builder.create();
                    dialog.show();

                } else {
                    send(message);
                    finish();

                }

                return true;
        }
        return super.onOptionsItemSelected(item);
    }



    protected ParseObject createMessage() {
        /* Metodo para crear el mensaje en Parse, lo creamos pasandole lo que deseamos que contenga
        el mensaje, en este caso el id y el nombre de usuario de quien envía, los destinatarios
        (obtenidos mediante un metodo que nos devuelve un arraylist) y el
         */
        ParseObject message = new ParseObject(ParseConstants.CLASS_MESSAGES);
        message.put(ParseConstants.KEY_SENDER_ID,ParseUser.getCurrentUser().getObjectId());
        message.put(ParseConstants.KEY_SENDER_NAME, ParseUser.getCurrentUser().getUsername());
        message.put(ParseConstants.KEY_RECIPIENT_IDS, getRecipientsIds());
        message.put(ParseConstants.KEY_FILE_TYPE, mFileType);

        /* NOTA: Helper Classes y la lib IOApache en https://github.com/treehouse/treehouse_android_utilities */

        if (mFileType.equals(ParseConstants.TYPE_TEXT)) {
            message.put(ParseConstants.KEY_TEXT, mTextMessage);

            return message;
        } else {


            // Convertimos el archivo a byte mediante la helper class FileHelper, pasandole el Uri
            byte[] fileBytes = FileHelper.getByteArrayFromFile(this, mMediaUri);
            // Si devuelve nulo hubo un error al crear ese archivo pero lo controlamos para que la aplicacion no termine
            // y el usuario pueda elegir otro archivo
            if (fileBytes == null) {
                return null;
            } else {
                if (mFileType.equals(ParseConstants.TYPE_IMAGE)) {
                    // Si es una imagen, la comprimimos
                    fileBytes = FileHelper.reduceImageForUpload(fileBytes);
                }

                // Obtenemos el nombre del archivo
                String fileName = FileHelper.getFileName(this, mMediaUri, mFileType);
                // Creamos el archivo Parse y lo añadimos al mensaje
                ParseFile file = new ParseFile(fileName, fileBytes);
                message.put(ParseConstants.KEY_FILE, file);

            }   return message;
        }
    }

    protected ArrayList<String> getRecipientsIds() {
        ArrayList<String> recipientsId = new ArrayList<String>();
        for (int i=0; i < mGridView.getCount(); i++) {
            if (mGridView.isItemChecked(i)) {
                recipientsId.add(mFriends.get(i).getObjectId());
            }
        }
        return recipientsId;
    }

    protected void send(ParseObject message) {
        mProgressBar.setVisibility(View.VISIBLE);
        message.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                mProgressBar.setVisibility(View.VISIBLE);
                if (e==null) {
                    Toast.makeText(RecipientsActivity.this, getString(R.string.message_sent),Toast.LENGTH_LONG).show();
                    sendPushNotifications();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RecipientsActivity.this);
                    builder.setMessage(getString(R.string.error_sending_message));
                    builder.setTitle(getString(R.string.title_error_message));
                    builder.setPositiveButton(android.R.string.ok,null);
                    AlertDialog dialog = builder.create();
                    dialog.show();

                }
            }
        });
    }

    protected AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (mGridView.getCheckedItemCount() > 0) {
                mMenuItem.setVisible(true);
            } else {
                mMenuItem.setVisible(false);
            }

            ImageView checkImageView = (ImageView)view.findViewById(R.id.checkImageView);

            if (mGridView.isItemChecked(position)) {
                // add the recipient
                mFriendsRelation.add(mFriends.get(position));
                checkImageView.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.ZoomIn)
                        .duration(200)
                        .playOn(view.findViewById(R.id.checkImageView));

            } else {
                // remove the recipient
                mFriendsRelation.remove(mFriends.get(position));
                YoYo.with(Techniques.ZoomOut)
                        .duration(200)
                        .playOn(view.findViewById(R.id.checkImageView));

            }
        }
    };

    protected void sendPushNotifications() {

        ParseQuery<ParseInstallation> query = ParseInstallation.getQuery();
        query.whereContainedIn(ParseConstants.KEY_USER_ID,getRecipientsIds());


        ParsePush push = new ParsePush();
        push.setQuery(query);
        push.setMessage(getString(R.string.parse_notification_message) + " " + ParseUser.getCurrentUser().getUsername().toString() + "!");
        push.sendInBackground();

    }
}

