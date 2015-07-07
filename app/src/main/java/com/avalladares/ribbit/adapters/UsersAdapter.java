package com.avalladares.ribbit.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.avalladares.ribbit.R;
import com.avalladares.ribbit.utilities.MD5Util;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

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

public class UsersAdapter extends ArrayAdapter<ParseUser>{

    protected Context mContext;
    protected List<ParseUser> mUsers;

    public UsersAdapter(Context context, List<ParseUser> users) {
        // Al crear un custom adapter, tenemos que pasar al elemento padre:
        // el contexto, el layout que vamos a usar, y el recurso, en este
        // caso la lista de mensajes
        super(context, R.layout.messageitem, users);
        mContext = context;
        mUsers = users;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            // Convertimos la vista por primera vez
            convertView = LayoutInflater.from(mContext).inflate(R.layout.user_item,null);
            holder = new ViewHolder();
            holder.userImageView = (ImageView) convertView.findViewById(R.id.userImageView);
            holder.nameLabel = (TextView) convertView.findViewById(R.id.nameLabel);
            holder.checkImageView = (ImageView) convertView.findViewById(R.id.checkImageView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ParseUser user = mUsers.get(position);
        String email = user.getEmail().toLowerCase();


        // Obtenemos la imagen de gravatar

        if (email.equals("")) {
            holder.userImageView.setImageResource(R.drawable.avatar_empty);
        } else {
            // Para obtener la imagen de gravatar obtenemos el hash del email
            // Despues pasamos la url con el hash y los modificadores s= para tamaño
            // y d404 para que nos de un error 404 si no tiene imagen asociada
            String gravatarHash = MD5Util.md5Hex(email);
            String gratavarURL = "http://www.gravatar.com/avatar/" + gravatarHash + "?s=204"
                    + "&d404";

            Picasso.with(mContext)
                    .load(gratavarURL)
                    .placeholder(R.drawable.avatar_empty) // Si nos devuelve error 404 usamos esta imagen por defecto
                    .into(holder.userImageView);

        }

        holder.nameLabel.setText(user.getUsername());

        // Obtenemos el gridview padre (parametro parent) para comprobar si los items estan marcados o no
        // Si lo estan añadimos el imageview de check, si no lo quitamos (visible o invisible)

        GridView gridView = (GridView)parent;
        if (gridView.isItemChecked(position)) {
            holder.checkImageView.setVisibility(View.VISIBLE);
        } else {
            holder.checkImageView.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    private static class ViewHolder {
        public ImageView userImageView;
        public TextView nameLabel;
        public ImageView checkImageView;


    }


    // Metodo para volver rellenar el adaptador
    // Se usa para mantener la pantalla en la posicion en que estaba al volver
    // a la activity
    public void refill (List<ParseUser> users) {
        mUsers.clear();
        mUsers.addAll(users);
        notifyDataSetChanged();
    }
}
