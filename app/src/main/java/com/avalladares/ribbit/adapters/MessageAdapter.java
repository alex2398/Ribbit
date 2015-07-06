package com.avalladares.ribbit.adapters;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.avalladares.ribbit.R;
import com.avalladares.ribbit.utilities.ParseConstants;
import com.parse.ParseObject;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by Alex on 02/07/2015.
 */

/* Los custom adapters siempre se forman del mismo modo:
1. Necesitamos extender la clase a la clase ArrayAdapter o BaseAdapter (los mas usados)
2. Creamos variables miembro para el contexto y la lista de datos que vamos a pasar
3. Creamos el constructor pasandole los dos datos anteriores, contexto y lista de datos.
4. Creamos el método getView (automático)
5. Creamos una clase ViewHolder (manual) con los elementos de nuetro layout personalizado

-----

6. Dentro del método getView "inflamos" el layout con el método LayoutInflater y lo asignamos al convertView
7. Creamos un nuevo ViewHolder para asociar los elementos del layout a variables y poder modificarle el contenido
8. Obtenemos la posicion del elemento con cuyos datos vamos a rellenar el layout
9. Modificamos el contenido de las variables creadas, si es una imagen, setImageResource, si es un texto, setText, etc. Y añadimos el tag al holder para poder reusarlo
10. Devolvemos el convertView
11. Para reciclar el adaptador, hacemos la comprobacion de si es la primera vez que se llena o no, y si no
    holder = (ViewHolder) convertView.getTag();
 */

public class MessageAdapter extends ArrayAdapter<ParseObject>{

    protected Context mContext;
    protected List<ParseObject> mMessages;

    public MessageAdapter(Context context, List<ParseObject> messages) {
        // Al crear un custom adapter, tenemos que pasar al elemento padre:
        // el contexto, el layout que vamos a usar, y el recurso, en este
        // caso la lista de mensajes
        super(context, R.layout.messageitem, messages);
        mContext = context;
        mMessages = messages;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            // Convertimos la vista por primera vez
            convertView = LayoutInflater.from(mContext).inflate(R.layout.messageitem,null);
            holder = new ViewHolder();
            holder.iconImageView = (ImageView) convertView.findViewById(R.id.messageIcon);
            holder.nameLabel = (TextView) convertView.findViewById(R.id.senderLabel);
            holder.sendLabel = (TextView) convertView.findViewById(R.id.sendDateLabel);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ParseObject message = mMessages.get(position);

        // segun el tipo de mensaje, elegimos un icono
        if (message.getString(ParseConstants.KEY_FILE_TYPE).equals(ParseConstants.TYPE_IMAGE)) {
            holder.iconImageView.setImageResource(R.drawable.ic_picture);
        } else if (message.getString(ParseConstants.KEY_FILE_TYPE).equals(ParseConstants.TYPE_VIDEO)) {
            holder.iconImageView.setImageResource(R.drawable.ic_video);
        } else if (message.getString(ParseConstants.KEY_FILE_TYPE).equals(ParseConstants.TYPE_TEXT)){ // mensaje de texto
            holder.iconImageView.setImageResource(R.drawable.ic_chat_bubble_outline_black_24dp);
        }

        holder.nameLabel.setText(message.getString(ParseConstants.KEY_SENDER_NAME));
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm a");


        long date_of_send = message.getCreatedAt().getTime();
        long now = System.currentTimeMillis();

        String relativeTime = DateUtils.getRelativeTimeSpanString(date_of_send,now,DateUtils.SECOND_IN_MILLIS).toString();


        holder.sendLabel.setText(relativeTime);


        return convertView;
    }

    private static class ViewHolder {
        public ImageView iconImageView;
        public TextView nameLabel;
        public TextView sendLabel;

    }


    // Metodo para volver rellenar el adaptador
    // Se usa para mantener la pantalla en la posicion en que estaba al volver
    // a la activity
    public void refill (List<ParseObject> messages) {
        mMessages.clear();
        mMessages.addAll(messages);
        notifyDataSetChanged();
    }
}
