package com.avalladares.ribbit.ui.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.avalladares.ribbit.R;
import com.avalladares.ribbit.adapters.UsersAdapter;
import com.avalladares.ribbit.utilities.ParseConstants;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;


public class EditFriendsActivity extends ActionBarActivity {

    public static final String TAG=EditFriendsActivity.class.getSimpleName();

    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;
    protected List<ParseUser> mUsers;
    protected GridView mGridView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_grid);

        mGridView = (GridView)findViewById(R.id.friendsGrid);
        mGridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);
        mGridView.setOnItemClickListener(mOnItemClickListener);


        TextView emptyTextView = (TextView)findViewById(android.R.id.empty);
        mGridView.setEmptyView(emptyTextView);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation= mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);





        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.orderByAscending(ParseConstants.KEY_USERNAME);
        query.setLimit(1000);


        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, com.parse.ParseException e) {

                if (e == null) {
                    // Success
                    // Pasamos el array de usuarios parseUser a un array de strings solo con el nombre
                    mUsers = users;
                    // Creamos el array del mismo tamaño que lo devuelvo en la query
                    String[] usernames = new String[mUsers.size()];

                    // Rellenamos el array de nombres
                    int i = 0;
                    for(ParseUser user : mUsers) {
                        usernames[i] = user.getUsername();
                        i++;
                    }

                    // Pasamos con un adaptador el array de nombres a la ListView del Layout con un contenedor simple_list_item_checked
                    if (mGridView.getAdapter() == null) {
                        UsersAdapter adapter = new UsersAdapter(EditFriendsActivity.this, mUsers);
                        mGridView.setAdapter(adapter);
                    } else {
                        // Refill it!
                        ((UsersAdapter)mGridView.getAdapter()).refill(mUsers);
                    }
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

    protected AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // OJO!! Hay que hacer referencia a la view, si no no encuentra el item correcto
            ImageView checkImageView = (ImageView)view.findViewById(R.id.checkImageView);

            if (mGridView.isItemChecked(position)) {
                // add friend
                mFriendsRelation.add(mUsers.get(position));
                checkImageView.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.ZoomIn)
                        .duration(200)
                        .playOn(view.findViewById(R.id.checkImageView));

            } else {
                // remove friend
                mFriendsRelation.remove(mUsers.get(position));
                YoYo.with(Techniques.ZoomOut)
                        .duration(200)
                        .playOn(view.findViewById(R.id.checkImageView));
                //checkImageView.setVisibility(View.INVISIBLE);


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
    };

    private void addFriendCheckmarks() {

        // En mFriendsRelation tenemos la lista de ParseUsers que tienen relacion con el usuario actual (mCurrentUser)
        // Los extraemos con getQuery() y los almacenamos en friends
        mFriendsRelation.getQuery().findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> friends, ParseException e) {
                if (e == null) {
                    // Para cada usuario existente en la aplicación
                    for (int i = 0; i < mUsers.size(); i++) {
                        // Guardamos el usuario recorrido (i)
                        ParseUser user = mUsers.get(i);
                        // y lo comparamos con los usuarios que tienen relacion
                        for (ParseUser friend : friends) {
                            if (friend.getObjectId().equals(user.getObjectId())) {
                                // Si el usuario existente en la aplicación está en la lista de usuarios relacionados,
                                // marcamos el check
                                mGridView.setItemChecked(i, true);
                            }
                        }
                    }
                } else {
                    Log.e(TAG, e.getMessage());
                }
            }
        });
    }


}
