package com.avalladares.ribbit;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.os.Build;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;


public class RecipientsActivity extends ActionBarActivity {
    public static final String TAG = RecipientsActivity.class.getSimpleName();

    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;
    protected List<ParseUser> mFriends;
    protected MenuItem mMenuItem;
    protected ListView mListView;
    protected TextView mEmptyText;

    private ProgressBar mProgressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipients);

        mListView = (ListView) findViewById(R.id.listFriends);
        mEmptyText = (TextView) findViewById(R.id.listEmpty);
        mMenuItem = (MenuItem) findViewById(R.id.sendTo);

        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(getCheckedItemCount() > 0){
                    mMenuItem.setVisible(true);
                }else{
                    mMenuItem.setVisible(false);
                }
            }
        });
    }

    @Override
    protected void onResume () {
            super.onResume();

        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);

        mProgressBar = (ProgressBar) this.findViewById(R.id.recipientsFragmentProgressBar);
        mProgressBar.setVisibility(View.VISIBLE);

        ParseQuery<ParseUser> query = mFriendsRelation.getQuery();
        query.addAscendingOrder(ParseConstants.KEY_USERNAME);

        query.findInBackground(new FindCallback<ParseUser>() {

            @Override
            public void done(List<ParseUser> friends, ParseException e) {


                mProgressBar.setVisibility(View.INVISIBLE);

                if (friends.size() == 0) {
                    mEmptyText.setVisibility(View.VISIBLE);
                }

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
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(RecipientsActivity.this, android.R.layout.simple_list_item_checked, usernames);

                    // Primero obtenemos los datos
                    mListView.setAdapter(adapter);


                } else {
                    Log.e(TAG, e.getMessage());
                    AlertDialog.Builder builder = new AlertDialog.Builder(RecipientsActivity.this);
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
        getMenuInflater().inflate(R.menu.menu_recipients, menu);
        mMenuItem = menu.getItem(0);
        return true;
    }

    public int getCheckedItemCount() {
        ListView listView = mListView;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return listView.getCheckedItemCount();
        }

        SparseBooleanArray checkedItems = listView.getCheckedItemPositions();
        int count = 0;
        for (int i = 0, size = checkedItems.size(); i < size; ++i) {
            if (checkedItems.valueAt(i)) {
                count++;
            }
        }
        return count;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.sendTo :
                ParseObject message = createMessage();
                //send(message);
                return true;
        }

        return super.onOptionsItemSelected(item);

    }
    protected ParseObject createMessage() {
        ParseObject message = new ParseObject(ParseConstants.CLASS_MESSAGES);
        message.put(ParseConstants.KEY_SENDER_ID,ParseUser.getCurrentUser().getObjectId());
        message.put(ParseConstants.KEY_SENDER_NAME, ParseUser.getCurrentUser().getUsername());
        message.put(ParseConstants.KEY_RECIPIENT_IDS, getRecipientsIds());

        return message;

    }

    protected ArrayList<String> getRecipientsIds() {
        ArrayList<String> recipientsId = new ArrayList<String>();
        for (int i=0; i < mListView.getCount(); i++) {
            if (mListView.isItemChecked(i)) {
                recipientsId.add(mFriends.get(i).getObjectId());
            }
        }
        return recipientsId;
    }

}

