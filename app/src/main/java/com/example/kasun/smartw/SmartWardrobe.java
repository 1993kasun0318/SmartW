package com.example.kasun.smartw;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import android.app.Dialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.example.kasun.smartw.data.DAOdb;

import java.util.ArrayList;

public class SmartWardrobe extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ArrayList<DAOdb.TextileInfo> images;
    private ImageAdapter imageAdapter;
    private ListView listView;
    private Uri mCapturedImageURI;
    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;

    //Ruvinda
    //Camera Permission code for marshmallow
    private  static final int RESULT_CAMERA_PERMISSION = 4;
    private boolean showDialog = false;

    //END Ruvinda

    private DAOdb daOdb;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_wardrobe);

        // Construct the data source
        images = new ArrayList();
        // Create the adapter to convert the array to views
        imageAdapter = new ImageAdapter(this, images);
        // Attach the adapter to a ListView
        listView = (ListView) findViewById(R.id.main_list_view);
        listView.setAdapter(imageAdapter);
        addItemClickListener(listView);
        initDB();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();


            }
        });



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this);

        if(getIntent().hasExtra("openDialog"))
        {
            showDialog = true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(showDialog == true)
        {
            showDialog = false;
            btnAddOnClick(null);
        }
    }

    private void initDB() {
        daOdb = new DAOdb(this);
        //        add images from database to images ArrayList
        for (DAOdb.TextileInfo mi : daOdb.getImages()) {
            images.add(mi);
        }
    }

    public void btnAddOnClick(View view) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.content_smart_wardrobe_custom_dialog_box);
        dialog.setTitle("Alert Dialog View");
        Button btnExit = (Button) dialog.findViewById(R.id.btnExit);
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.btnChoosePath).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                activeGallery();
            }
        });
        dialog.findViewById(R.id.btnTakePhoto).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {

                //Ruvinda
                //Check for android version MM or higher
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
                    //Check if the application has been granted permissions to access the external storage
                    if (ActivityCompat.checkSelfPermission(SmartWardrobe.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        //App has permissions, so continue to camera
                        activeTakePhoto();
                    }
                    else
                    {
                        //Asking for permissions to access the storage
                        ActivityCompat.requestPermissions(SmartWardrobe.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                               SmartWardrobe.RESULT_CAMERA_PERMISSION );
                    }
                }
                else
                    activeTakePhoto();

                //End Ruvinda
            }
        });

        // show dialog on screen
        dialog.show();
    }

    //Ruvinda
    // This method is called by the android system after the permission dialog box
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == RESULT_CAMERA_PERMISSION)
        {
            if(grantResults[0] == 0 )
            {
                activeTakePhoto();
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //End Ruvinda

    /**
     * take a photo
     */
    private void activeTakePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            String fileName = "temp.jpg";
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, fileName);
            mCapturedImageURI = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    /**
     * to gallery
     */
    private void activeGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, RESULT_LOAD_IMAGE);
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RESULT_LOAD_IMAGE:
                if (requestCode == RESULT_LOAD_IMAGE &&
                        resultCode == RESULT_OK && null != data) {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();
                    Intent intent = new Intent(this,AddTextileDescription.class);
                    intent.putExtra("imagePath",picturePath);
                    startActivity(intent);

                }
            case REQUEST_IMAGE_CAPTURE:
                if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                    String[] projection = {MediaStore.Images.Media.DATA};
                    Cursor cursor = managedQuery(mCapturedImageURI, projection, null, null, null);
                    int column_index_data = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    cursor.moveToFirst();
                    String picturePath = cursor.getString(column_index_data);
                    Intent intent = new Intent(this,AddTextileDescription.class);
                    intent.putExtra("imagePath",picturePath);
                    startActivity(intent);
                }
        }
    }
    /**
     * item clicked listener used to implement the react action when an item is clicked.
     *
     * @param listView
     */
    private void addItemClickListener(final ListView listView) {
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                DAOdb.TextileInfo ti = (DAOdb.TextileInfo) listView.getItemAtPosition(position);
                Intent intent = new Intent(getBaseContext(), DisplayImage.class);
                intent.putExtra("type", ti.textile_type);
                intent.putExtra("variation", ti.textile_variation);
                intent.putExtra("material", ti.textile_material);
                intent.putExtra("colorType", ti.textile_color_type);
                intent.putExtra("tags",ti.tags);
                intent.putExtra("path",ti.path);
                intent.putExtra("datetime",ti.datetime);
                intent.putExtra("id",ti._id);
                intent.putExtra("primary_color",ti.primaryColor);
                intent.putExtra("secondary_color",ti.secondaryColor);
                startActivity(intent);
            }
        });
    }
    @Override protected void onSaveInstanceState(Bundle outState) {
        // Save the user's current game state
        if (mCapturedImageURI != null) {
            outState.putString("mCapturedImageURI", mCapturedImageURI.toString());
        }
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(outState);
    }

    @Override protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);

        // Restore state members from saved instance
        if (savedInstanceState.containsKey("mCapturedImageURI")) {
            mCapturedImageURI = Uri.parse(savedInstanceState.getString("mCapturedImageURI"));
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.smart_wardrobe, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            //Settings Intent
            Intent settingsIntent=new Intent(this,SmartW_Settings.class);
            startActivity(settingsIntent);
            return true;
        }
        if (id == R.id.action_logout) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.smart_wardrobeMenu) {
            // Handle the camera action
        } else if (id == R.id.style_networkMenu) {
            //Open Style Network Activity When Menu Item Selected
            Intent styleNetworkIntent=new Intent(this,Style_Network.class);
            startActivity(styleNetworkIntent);
        } else if (id == R.id.online_ShopMenu) {
            //Open Online Purchasing Activity When Menu Item Selected
            Intent onlinePurchasingIntent=new Intent(this,Online_Purchasing.class);
            startActivity(onlinePurchasingIntent);
        } else if (id == R.id.settingsMenu) {
            Intent settingsIntent=new Intent(this,SmartW_Settings.class);
            startActivity(settingsIntent);
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
