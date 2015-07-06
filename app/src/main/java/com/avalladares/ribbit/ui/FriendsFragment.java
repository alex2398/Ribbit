package com.avalladares.ribbit.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.List;

/**
 * Created by avalladares on 01/07/2015.
 */
public class FriendsFragment extends android.support.v4.app.ListFragment {

    public static final String TAG = EditFriendsActivity.class.getSimpleName();

    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;
    protected List<ParseUser> mFriends;

    private ProgressBar mProgressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_friends, container, false);


        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);

        mProgressBar = (ProgressBar) getActivity().findViewById(R.id.friendsFragmentProgressBar);
        mProgressBar.setVisibility(View.VISIBLE);

        ParseQuery<ParseUser> query = mFriendsRelation.getQuery();
        query.addAscendingOrder(ParseConstants.KEY_USERNAME);

        query.findInBackground(new FindCallback<ParseUser>() {

            @Override
            public void done(List<ParseUser> friends, ParseException e) {
                if (isAdded()) {        // Comprobamos que el fragment ha sido añadido a la actividad antes de nada (provocaba crashes)

                    /*Ok, so the problem was that on a device with limited memory, android was destroying mainActivity when switching to the camera,
                    but not destroying the fragments associated with them. Then on coming back to the mainActivity from the camera app, the code in the done()
                    method was running before the fragment was reattached to its recreated activity, and so I was getting a nullPointer error when trying to
                    access the activity. The solution was just to wrap the code in the done() method with an if(isAdded()) to check if the fragment
                    is attached to it's activity before trying to access it like so:*/

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
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getListView().getContext(), android.R.layout.simple_list_item_1, usernames);
                        // Primero obtenemos los datos
                        setListAdapter(adapter);


                    } else {
                        Log.e(TAG, e.getMessage());
                        AlertDialog.Builder builder = new AlertDialog.Builder(getListView().getContext());
                        builder.setMessage(e.getMessage());
                        builder.setTitle(getString(R.string.title_error_message));
                        builder.setPositiveButton(android.R.string.ok, null);
                        AlertDialog dialog = builder.create();
                        dialog.show();

                    }
                }
            }
        });


    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent intent = new Intent(getListView().getContext(), ProfileActivity.class);
        startActivity(intent);


    }
}




