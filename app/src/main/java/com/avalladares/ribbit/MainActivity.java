package com.avalladares.ribbit;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.parse.ParseAnalytics;
import com.parse.ParseUser;

import java.io.File;
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

    // Uri : Uniform Resorce Identifier : identifica recursos en el sistema
    protected Uri mMediaUri;

    SectionsPagerAdapter mSectionsPagerAdapter;

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
                    mMediaUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
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
                    break;
                case 3: // Choose video
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


            // 2. Crear un subdirectorio
            // ** Comprobamos si el directorio existe, si no, lo creamos, si da error al crearlo, lo registramos en el log
            if (! mediaStorageDir.exists()){
                if (! mediaStorageDir.mkdirs()){
                    Log.d(TAG,getString(R.string.fail_create_directory));
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
    private boolean isExternalStorageAvailable() {
        String state = Environment.MEDIA_MOUNTED;
        if (state.equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * The {@link ViewPager} that will host the section contents.
     */

    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            // add it to the gallery
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(mMediaUri);
            sendBroadcast(mediaScanIntent);
        }
        else if (resultCode!= RESULT_CANCELED) {
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
