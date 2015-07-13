package com.avalladares.ribbit.ui.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.avalladares.ribbit.R;
import com.avalladares.ribbit.adapters.SectionsPagerAdapter;
import com.avalladares.ribbit.utilities.ParseConstants;
import com.parse.ParseAnalytics;
import com.parse.ParseUser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity implements ActionBar.TabListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    public static final String TAG = MainActivity.class.getSimpleName();
    public static final int TAKE_PHOTO_REQUEST = 0;
    public static final int TAKE_VIDEO_REQUEST = 1;
    public static final int PICK_PHOTO_REQUEST = 2;
    public static final int PICK_VIDEO_REQUEST = 3;

    public static final int MEDIA_TYPE_IMAGE = 4;
    public static final int MEDIA_TYPE_VIDEO = 5;

    public static final int SEND_TEXT = 6;

    public static final int FILE_SIZE_LIMIT = 1024 * 1024 * 10; // 10 Mb

    public static String KEY_MEDIA_URI;

    // Uri : Uniform Resorce Identifier : identifica recursos en el sistema
    protected Uri mMediaUri;

    SectionsPagerAdapter mSectionsPagerAdapter;

    // The {@link ViewPager} that will host the section contents.

    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*And check for it on creating to bring it back:*/
        // Check if there is a saved state to restore
        if (savedInstanceState != null) {
            // Restore the media uri so we can pass it to RecipientsFragment
            mMediaUri = savedInstanceState.getParcelable(KEY_MEDIA_URI);
        }
        setContentView(R.layout.activity_main);

        ParseAnalytics.trackAppOpenedInBackground(getIntent());

        // Comprobamos si hay un usuario logueado en el sistema (caché)
        // Si no, vamos a la pantalla de login
        ParseUser currentUser = ParseUser.getCurrentUser();

        if (currentUser != null) {
            // do stuff with the user
            Log.d(TAG, currentUser.getUsername());
        } else {
            // show the signup or login screen
            navigateToLogin();
        }

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
        actionBar.setIcon(R.drawable.ic_launcher);

        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(this,getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(

                    actionBar.newTab()
                            // Establecemos el icono para cada tab
                            .setIcon(mSectionsPagerAdapter.getIcon(i))
                            //.setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
    }



    /*Also to keep the app from crashing at the point of hitting
        the send button we have to save the mMediaUri of mainActivity
        in the onSaveInstanceState(Bundle bundle) method to keep from
        losing it when the activity is destroyed:*/
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save the media uri in case we get destroyed in the background
        outState.putParcelable(KEY_MEDIA_URI, mMediaUri);
    }

    protected DialogInterface.OnClickListener mDialogListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case 0: // Take picture

                    Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    // Obtenemos el identificador del archivo con el metodo getOutputMediaFileuri (creado mas abajo)
                    mMediaUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                    if (mMediaUri == null) {
                        //Display an error
                        Toast.makeText(MainActivity.this,getString(R.string.error_external_storage),Toast.LENGTH_SHORT).show();
                    } else {
                        // Al intent le pasamos como extra el identificador del archivo
                        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
                        startActivityForResult(takePhotoIntent, TAKE_PHOTO_REQUEST);

                    }

                    break;

                case 1: // Take video

                    Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    // Obtenemos el identificador del archivo con el metodo getOutputMediaFileuri (creado mas abajo)
                    mMediaUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);
                    if (mMediaUri == null) {
                        //Display an error
                        Toast.makeText(MainActivity.this,getString(R.string.error_external_storage),Toast.LENGTH_SHORT).show();
                    } else {
                        // Al intent le pasamos como extra el identificador del archivo
                        takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
                        takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10); // 10 seconds
                        takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0); // 0 = low quality; 1 = high quality NOTE: Parse.com limits size to 10Mb in the free version
                        startActivityForResult(takeVideoIntent, TAKE_VIDEO_REQUEST);
                    }

                    break;

                case 2: // Choose picture
                    Intent choosePhotointent = new Intent(Intent.ACTION_GET_CONTENT);
                    choosePhotointent.setType("image/*");
                    startActivityForResult(choosePhotointent, PICK_PHOTO_REQUEST);
                    //Log.d(TAG, "File " + mMediaUri.getPath());
                    break;

                case 3: // Choose video
                    Intent chooseVideointent = new Intent(Intent.ACTION_GET_CONTENT);
                    chooseVideointent.setType("video/*");
                    Toast.makeText(MainActivity.this,getString(R.string.video_length_warning),Toast.LENGTH_LONG).show();
                    startActivityForResult(chooseVideointent, PICK_VIDEO_REQUEST);
                    break;

            }
        }
    };

    // Metodo que usamos para crear el archivo en el almacenamiento externo y devolver su URI
    private Uri getOutputMediaFileUri(int mediaType) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        if (isExternalStorageAvailable()) {
            // get the Uri

            // 1. Obtener el directorio de almacenamiento externo
            String appName = getString(R.string.app_name);

            File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES),appName);

            //String extStorageDirectory = Environment.getExternalStorageDirectory().getAbsolutePath();


            // 2. Crear un subdirectorio
            // ** Comprobamos si el directorio existe, si no, lo creamos, si da error al crearlo, lo registramos en el log
            if (! mediaStorageDir.exists()){
                if (! mediaStorageDir.mkdirs()){
                    Log.d(TAG,getString(R.string.fail_create_directory));
                    Toast.makeText(this,"ERROR!!",Toast.LENGTH_LONG).show();
                    return null;
                }
            }

            // 3. Crear un nombre de archivo

            File mediaFile;
            Date now = new Date();
            String timestamp = new SimpleDateFormat("yyyyMMdd_hhss",getResources().getConfiguration().locale).format(now);

            String path = mediaStorageDir.getPath() + File.separator;

            // 4. Crear el archivo

            if (mediaType == MEDIA_TYPE_IMAGE) {
                mediaFile = new File(path + "IMG_" + timestamp + ".jpg");

            } else if (mediaType == MEDIA_TYPE_VIDEO) {
                mediaFile = new File(path + "MOV_" + timestamp + ".mp4");
            } else {
                return null;
            }

            Log.d (TAG,"File " + Uri.fromFile(mediaFile));

            // 5. Devolver el URI del archivo

            return Uri.fromFile(mediaFile);

        } else {
            return null;
        }
    }

    // Metodo para comprobar si la memoria externa esta montada (accesible)
    public static boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        } else {
            return false;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            // Si hemos seleccionado foto o video de la galeria nos llegara el parametro data con el
            // Uri del archivo seleccionado.
            if (requestCode == PICK_PHOTO_REQUEST || requestCode == PICK_VIDEO_REQUEST) {
                if (data != null) {
                    mMediaUri = data.getData();

                } else {
                    Toast.makeText(this, getString(R.string.general_error), Toast.LENGTH_SHORT).show();
                }

                if (requestCode == PICK_VIDEO_REQUEST) {
                    // Nos aseguramos de que el video pesa menos de 10Mb
                    int fileSize = 0;
                    InputStream inputStream = null;
                    try {
                        inputStream = getContentResolver().openInputStream(mMediaUri);
                        fileSize = inputStream.available();
                    } catch (FileNotFoundException e) {
                        Toast.makeText(this, getString(R.string.error_selecting_file), Toast.LENGTH_SHORT).show();
                        return;
                    } catch (IOException e) {
                        Toast.makeText(this, getString(R.string.error_selecting_file), Toast.LENGTH_SHORT).show();
                        return;
                    } finally {  // el bloque finally se ejecuta siempre
                        try {
                            inputStream.close();
                        } catch (IOException e) { /* Intentionally blank */}
                    }
                    if (fileSize <= FILE_SIZE_LIMIT) {
                        Toast.makeText(this,getString(R.string.file_size_alert), Toast.LENGTH_SHORT).show();
                        return; // Salimos del método y volvemos a la activity
                    }
                }

            } else {

                // add it to the gallery with broadcasting so the mediagallery knows there is new pictures
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaScanIntent.setData(mMediaUri);
                sendBroadcast(mediaScanIntent);
            }

            Intent recipientsIntent = new Intent(this, RecipientsActivity.class);
            // Pasamos el Uri en el intent
            recipientsIntent.setData(mMediaUri);
            // Vamos a pasar tambien el tipo de archivo, ya que nos hará falta para crear el mensaje en Parse
            // en RecipientsActivity

            String fileType;
            if (requestCode == TAKE_PHOTO_REQUEST || requestCode == PICK_PHOTO_REQUEST){
                fileType = ParseConstants.TYPE_IMAGE;
            } else if (requestCode == TAKE_VIDEO_REQUEST || requestCode == PICK_VIDEO_REQUEST) {
                fileType = ParseConstants.TYPE_VIDEO;
            } else {
                fileType = ParseConstants.TYPE_TEXT;
            }

            recipientsIntent.putExtra(ParseConstants.KEY_FILE_TYPE, fileType);
            startActivity(recipientsIntent);

        } else if (resultCode!= RESULT_CANCELED) {
            Toast.makeText(this,getString(R.string.general_error),Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this,LoginActivity.class);
        // añadimos un flag para evitar que se vaya a la mainActivity desde el boton volver
        // new task añade una nueva tarea a la pila
        // clear task limpia las tareas previas para que no se pueda volver a ella
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void navigateToEditFriends() {
        Intent intent = new Intent(this,EditFriendsActivity.class);
        startActivity(intent);
    }

    // Hemos modificado el menu_main.xml para que muestre logout
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    // Añadimos la opción de pulsar en logout
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_logout:
                ParseUser.logOut();
                navigateToLogin();
                break;
            case R.id.action_edit_friends:
                navigateToEditFriends();
                break;
            case R.id.action_camera:
                // Mostramos el array de opciones (en res) en un cuadro de dialogo
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                // Le asociamos el listener creado para el DialogInterface
                builder.setItems(R.array.camera_choices,mDialogListener);
                AlertDialog dialog =  builder.create();
                dialog.show();
                break;
            case R.id.action_text_message:
                Intent intent = new Intent(this,MessageActivity.class);
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }


}


