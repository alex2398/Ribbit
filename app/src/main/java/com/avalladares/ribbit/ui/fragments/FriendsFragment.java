package com.avalladares.ribbit.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.avalladares.ribbit.R;
import com.avalladares.ribbit.adapters.UsersAdapter;
import com.avalladares.ribbit.ui.activities.EditFriendsActivity;
import com.avalladares.ribbit.ui.activities.ProfileActivity;
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
public class FriendsFragment extends android.support.v4.app.Fragment {

    public static final String TAG = EditFriendsActivity.class.getSimpleName();

    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;
    protected List<ParseUser> mFriends;
    protected GridView mGridView;
    protected SwipeRefreshLayout mSwipeRefreshLayout;

    private ProgressBar mProgressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.user_grid, container, false);
        mGridView = (GridView) rootView.findViewById(R.id.friendsGrid);

        // Creamos la variable para empty ya que en Fragment no funciona automaticamente como en Fragmentlist
        TextView emptyTextView = (TextView) rootView.findViewById(android.R.id.empty);
        mGridView.setEmptyView(emptyTextView);
        mGridView.setOnItemClickListener(mOnItemClickListener);

        // Al ser un fragment, para añadir la vista tenemos que referenciar rootView
        mSwipeRefreshLayout = (SwipeRefreshLayout)rootView.findViewById(R.id.swipeRefreshLayout);
        // mOnRefreshListener --> Accion que hace al refrescar (metodo mas abajo)
        mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);
        mSwipeRefreshLayout.setColorSchemeColors(R.color.swipeRefresh1, R.color.swipeRefresh2, R.color.swipeRefresh3, R.color.swipeRefresh4);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        retrieveUsers();

    }

    private void retrieveUsers() {

        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);
        

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


                    if (mSwipeRefreshLayout.isRefreshing()) {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                    if (e == null) {
                        mFriends = friends;

                        // Pasamos con un adaptador el array de nombres a la ListView del Layout con un contenedor simple_list_item_checked
                        if (mGridView.getAdapter() == null) {
                            UsersAdapter adapter = new UsersAdapter(getActivity(), mFriends);
                            mGridView.setAdapter(adapter);
                        } else {
                            // Refill it!
                            ((UsersAdapter) mGridView.getAdapter()).refill(mFriends);
                        }


                    } else {
                        Log.e(TAG, e.getMessage());


                    }
                }
            }
        });
    }

    // Al hacer click en un item vamos a su ficha
    protected AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            String user= mFriends.get(position).getUsername();
            Intent intent = new Intent(getActivity(), ProfileActivity.class);
            intent.putExtra("username",user);
            startActivity(intent);

        }

    };

    SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            retrieveUsers();
        }
    };

}




