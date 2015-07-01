package com.avalladares.ribbit;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;


public class EditFriendsActivity extends ListActivity {

    public static final String TAG=EditFriendsActivity.class.getSimpleName();
    protected List<ParseUser> mUsers;
    private ProgressBar mProgressBar;
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_friends);


        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation= mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);


        mProgressBar = (ProgressBar) findViewById(R.id.searchFriendsProgressBar);

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.orderByAscending(ParseConstants.KEY_USERNAME);
        query.setLimit(1000);

        mProgressBar.setVisibility(View.VISIBLE);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, com.parse.ParseException e) {
                mProgressBar.setVisibility(View.INVISIBLE);
                if (e==null) {
                    // Success
                    // Pasamos el array de usuarios parseUser a un array de strings solo con el nombre
                    mUsers = users;
                    // Creamos el array del mismo tamaño que lo devuelvo en la query
                    String[] usernames = new String[mUsers.size()];

                    // Rellenamos el array de nombres
                    for(int i = 0; i< usernames.length; i++) {
                        ParseUser user = mUsers.get(i);
                        usernames[i] = user.getUsername();
                    }

                    // Pasamos con un adaptador el array de nombres a la ListView del Layout con un contenedor simple_list_item_checked
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(EditFriendsActivity.this, android.R.layout.simple_list_item_1,usernames);
                    getListView().setAdapter(adapter);



                } else {

                    Log.e(TAG, e.getMessage());
                    AlertDialog.Builder builder= new AlertDialog.Builder(EditFriendsActivity.this);
                    builder.setMessage(e.getMessage());
                    builder.setTitle(getString(R.string.title_error_message));
                    builder.setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_friends, menu);
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

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (getListView().isItemChecked(position)) {
            // add friend
            mFriendsRelation.add(mUsers.get(position));
            mCurrentUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e!=null) {
                        Log.e(TAG,e.getMessage());
                    } else {

                    }
                }
            });
        } else {
            // remove friend
            // add friend
            mFriendsRelation.remove(mUsers.get(position));
            mCurrentUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e!=null) {
                        Log.e(TAG,e.getMessage());
                    } else {

                    }
                }
            });
        }



    }
}
