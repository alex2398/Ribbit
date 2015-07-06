package com.avalladares.ribbit.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.avalladares.ribbit.R;
import com.avalladares.ribbit.adapters.MessageAdapter;
import com.avalladares.ribbit.utilities.ParseConstants;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by avalladares on 01/07/2015.
 */
public class InboxFragment extends ListFragment {

    public static final String TAG = MainActivity.class.getSimpleName();

    private ProgressBar mProgressBar;
    // Variable para almacenar la lista de mensajes
    protected List<ParseObject> mMessages;
    protected SwipeRefreshLayout mSwipeRefreshLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_inbox, container, false);

        mSwipeRefreshLayout = (SwipeRefreshLayout)rootView.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);
        mSwipeRefreshLayout.setColorSchemeColors(R.color.swipeRefresh1,R.color.swipeRefresh2,R.color.swipeRefresh3,R.color.swipeRefresh4);


        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        retrieveMessages();

    }

    private void retrieveMessages() {
        mProgressBar = (ProgressBar) getActivity().findViewById(R.id.progressBarInbox);

        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseConstants.CLASS_MESSAGES);
        query.whereEqualTo(ParseConstants.KEY_RECIPIENT_IDS, ParseUser.getCurrentUser().getObjectId());
        query.addDescendingOrder(ParseConstants.KEY_CREATED_AT);


        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> messages, ParseException e) {


                if (mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(false);
                } else {
                    if (e == null) {
                        // Success
                        // Encontramos mensajes para el usuario
                        mMessages = messages;

                        // Creamos el adaptador de nuestro tipo custom (MessageAdapter) para nuestro layout personalizado (messageitem.xml)

                        // Este condicional se usa para mantener la posicion de la pantalla al volver a la activity (metodo refill del adaptador)
                        if (getListView().getAdapter() == null) {
                            MessageAdapter adapter = new MessageAdapter(getListView().getContext(), mMessages);
                            setListAdapter(adapter);
                        } else {
                            // Refill the adapter!
                            ((MessageAdapter) getListView().getAdapter()).refill(mMessages);
                        }


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

        // Obtenemos el mensaje de la posicion que hemos clickado
        ParseObject message = mMessages.get(position);

        // Obtenemos el tipo de mensaje
        String messageType = message.getString(ParseConstants.KEY_FILE_TYPE);

        // Obtenemos el archivo del mensaje si es imagen o video
        ParseFile file = message.getParseFile(ParseConstants.KEY_FILE);
        if (file != null) {
            Uri fileUri = Uri.parse(file.getUrl());
            if (messageType.equals(ParseConstants.TYPE_IMAGE)) {
                // View the image
                Intent intent = new Intent(getActivity(), ViewImageActivity.class);
                intent.setData(fileUri);
                startActivity(intent);

            } else if (messageType.equals(ParseConstants.TYPE_VIDEO)) {
                // View the video
                Intent intent = new Intent(Intent.ACTION_VIEW,fileUri);
                intent.setDataAndType(fileUri, "video/*");
                startActivity(intent);

            }
            // Obtenemos el texto del mensaje de texto
        } else {
            String textMessage = message.getString(ParseConstants.KEY_TEXT);
            if (messageType.equals(ParseConstants.TYPE_TEXT)) {
                // View the text message
                Intent intent = new Intent(getActivity(), ViewTextActivity.class);
                intent.putExtra(ParseConstants.TYPE_TEXT,textMessage);
                intent.putExtra(ParseConstants.KEY_SENDER_NAME,message.getString(ParseConstants.KEY_SENDER_NAME));
                startActivity(intent);
            }
        }

        // Borramos el mensaje
        List<String> ids = message.getList(ParseConstants.KEY_RECIPIENT_IDS);

        // Si solo hay un destinatario, borramos el mensaje
        // Pero si hay varios, borramos al destinatario del mensaje

        if (ids.size() == 1) {
            message.deleteInBackground();

        } else {
            // Borramos el destinatario en local
            ids.remove(ParseUser.getCurrentUser().getObjectId());

            // Y ahora en el backend
            // Creamos un arraylist con los ids a borrar
            ArrayList<String> idsToRemove = new ArrayList<String>();
            // Añadimos el usuario a borrar de la lista de destinatarios
            idsToRemove.add(ParseUser.getCurrentUser().getObjectId());

            // Lo borramos y guardamos los cambios
            message.removeAll(ParseConstants.KEY_RECIPIENT_IDS, idsToRemove);
            message.saveInBackground();

        }
    }

    OnRefreshListener mOnRefreshListener = new OnRefreshListener() {
        @Override
        public void onRefresh() {
            retrieveMessages();
        }
    };
}



