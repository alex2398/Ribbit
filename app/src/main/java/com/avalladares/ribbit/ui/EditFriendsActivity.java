package com.avalladares.ribbit.ui;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.avalladares.ribbit.R;
import com.avalladares.ribbit.utilities.ParseConstants;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;


public class EditFriendsActivity extends ListActivity {

    public static final String TAG=EditFriendsActivity.class.getSimpleName();

    private ProgressBar mProgressBar;

    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;
    protected List<ParseUser> mUsers;

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
                if (e == null) {
                    // Success
                    // Pasamos el array de usuarios parseUser a un array de strings solo con el nombre
                    mUsers = users;
                    // Creamos el array del mismo tamaño que lo devuelvo en la query
                    String[] usernames = new String[mUsers.size()];

                    // Rellenamos el array de nombres
                    for (int i = 0; i < usernames.length; i++) {
                        ParseUser user = mUsers.get(i);
                        usernames[i] = user.getUsername();
                    }

                    // Pasamos con un adaptador el array de nombres a la ListView del Layout con un contenedor simple_list_item_checked
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(EditFriendsActivity.this, android.R.layout.simple_list_item_checked, usernames);
                    // Primero obtenemos los datos
                    getListView().setAdapter(adapter);
                    // Después marcamos los usuarios con los que el usuario actual tiene relacion
                    addFriendCheckmarks();


                } else {

                    Log.e(TAG, e.getMessage());
                    AlertDialog.Builder builder = new AlertDialog.Builder(EditFriendsActivity.this);
                    builder.setMessage(e.getMessage());
                    builder.setTitle(getString(R.string.title_error_message));
                    builder.setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }

    /*
    No necesitamos el menu en esta pantalla asi que lo borramos
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_edit_friends, menu);
    return true;
    }
    */



    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (getListView().isItemChecked(position)) {
            // add friend
            mFriendsRelation.add(mUsers.get(position));

        } else {
            // remove friend
            mFriendsRelation.remove(mUsers.get(position));
        }
            // save data to backend
            mCurrentUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e!=null) {
                        Log.e(TAG,e.getMessage());
                    }
                }
            });
        }

    private void addFriendCheckmarks() {

        // En mFriendsRelation tenemos la lista de ParseUsers que tienen relacion con el usuario actual (mCurrentUser)
        // Los extraemos con getQuery() y los almacenamos en friends
        mFriendsRelation.getQuery().findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> friends, ParseException e) {
                if (e==null) {
                    // Para cada usuario existente en la aplicación
                    for (int i = 0; i < mUsers.size(); i++) {
                        // Guardamos el usuario recorrido (i)
                        ParseUser user = mUsers.get(i);
                        // y lo comparamos con los usuarios que tienen relacion
                        for(ParseUser friend : friends) {
                            if (friend.getObjectId().equals(user.getObjectId())) {
                                // Si el usuario existente en la aplicación está en la lista de usuarios relacionados,
                                // marcamos el check
                                getListView().setItemChecked(i,true);
                            }
                        }
                    }
                } else {
                    Log.e(TAG, e.getMessage());
                    AlertDialog.Builder builder = new AlertDialog.Builder(EditFriendsActivity.this);
                    builder.setMessage(e.getMessage());
                    builder.setTitle(getString(R.string.title_error_message));
                    builder.setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();

                }
            }
        });


    }
}