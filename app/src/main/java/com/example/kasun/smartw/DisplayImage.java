package com.example.kasun.smartw;

/**
 * Created by Deepika on 19-Oct-16.
 */
import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kasun.smartw.data.DAOdb;

import java.io.File;

import static com.example.kasun.smartw.R.layout.content_smart_wardrobe_display_image;

public class DisplayImage extends Activity {

    private ImageView imageView;
    private TextView description;
    private String jstring;

    private ImageView backButton;
    private LinearLayout buttonPanel;
    private ImageView deleteButton;
    private ImageView upButton;
    private int slidingHeight;
    private ImageView informationButton;
    private ImageView editInformation;
    private  ImageView suggestionButton;
    private ImageView donationNormalButton;

    private ViewTreeObserver observer;

    private boolean bPanelHidden = true;


    //Ruvinda
    private DAOdb.TextileInfo ti = new DAOdb.TextileInfo();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(content_smart_wardrobe_display_image);
        imageView = (ImageView) findViewById(R.id.display_image_view);
        donationNormalButton = (ImageView) findViewById(R.id.charityButton);

        //description = (TextView) findViewById(R.id.text_view_description);

        //Ruvinda
        /*
        ti.path = getIntent().getStringExtra("path");
        ti.datetime = getIntent().getLongExtra("datetime",0);
        ti.tags = getIntent().getStringArrayExtra("tags");
        ti.textile_type = getIntent().getStringExtra("type");
        ti.textile_material = getIntent().getStringExtra("material");
        ti.textile_variation = getIntent().getStringExtra("variation");
        ti.textile_color_type = getIntent().getStringExtra("colorType");
        ti.primaryColor = getIntent().getStringExtra("primary_color");
        ti.secondaryColor = getIntent().getStringExtra("secondary_color"); */
        long id = getIntent().getLongExtra("id",0);

        if(id > 0) {
            DAOdb db = new DAOdb(this);
            ti = db.getTextileInfo(id);

            File file = new File(ti.path);
            imageView.setImageURI(Uri.fromFile(file));

        }
        else
        {
            Toast.makeText(this,"Invalid Image" , Toast.LENGTH_LONG).show();
        }

        buttonPanel = (LinearLayout) findViewById(R.id.buttonPanel);

        backButton = (ImageView) findViewById(R.id.backButton);
        upButton = (ImageView) findViewById(R.id.upButton);
        informationButton = (ImageView) findViewById(R.id.infoButton);
        deleteButton = (ImageView) findViewById(R.id.deleteButton);
        editInformation = (ImageView) findViewById(R.id.editButton);
        suggestionButton = (ImageView) findViewById(R.id.matchButton);


        observer = buttonPanel.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                buttonPanel.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                slidingHeight = backButton.getMeasuredHeight();
                buttonPanel.animate().translationY(slidingHeight );
            }
        });

        upButton.setOnClickListener(upButtonClick);
        informationButton.setOnClickListener(viewDetails);

        //back button event
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //delete button event
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnDeleteOnClick(v);
            }
        });
        //edit info button event
        editInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editInformationOnClick();
            }
        });

        //suggestion button click ewent
        suggestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(),Suggestion.class);
                intent.putExtra("textile_id", ti._id);
                startActivity(intent);
            }
        });
        //charity button event
        donationNormalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectForCharity();


            }
        });

        if(ti.selectForCharity == 0 ) {
            donationNormalButton.setImageDrawable(getResources().getDrawable(R.drawable.donate_normal));
            //Toast.makeText(DisplayImage.this,"You have removed this to charity stuffs",Toast.LENGTH_LONG).show();
        }
        else{
            donationNormalButton.setImageDrawable(getResources().getDrawable(R.drawable.donate_active));
            //Toast.makeText(DisplayImage.this,"You have added this to charity stuffs",Toast.LENGTH_LONG).show();
        }

    }



    /**
     * go back to main activity
     *
     */
    public void btnBackOnClick(View v) {
        startActivity(new Intent(this, SmartWardrobe.class));
        finish();
    }

    /**
     * delete the current item
     */
    public void btnDeleteOnClick(View v) {

        new AlertDialog.Builder(DisplayImage.this).setTitle("Delete Item").setMessage("Are you sure you want to delete this item ?")
                .setNegativeButton("Cancel",null).setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DAOdb db = new DAOdb(getApplication());
                db.deleteImage(ti._id,ti.image_id);
                db.close();
                startActivity(new Intent(getApplication(), SmartWardrobe.class));
                finish();
            }
        }).show();


    }

    //edit textile details
    public void editInformationOnClick()
    {
        Intent intent = new Intent(getBaseContext(), AddTextileDescription.class);
        intent.putExtra("textile_id", ti._id);
        intent.putExtra("editing",true);
        startActivity(intent);

    }

    @Override protected void onSaveInstanceState(Bundle outState) {
        // Save the user's current game state
        if (jstring != null) {
            outState.putString("jstring", jstring);
        }
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(outState);
    }

    @Override protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);

        // Restore state members from saved instance
        if (savedInstanceState.containsKey("jstring")) {
            jstring = savedInstanceState.getString("jstring");
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
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //upbutton lick event
    private View.OnClickListener upButtonClick=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(bPanelHidden == true)
            {
                buttonPanel.animate().translationY(0);
                upButton.setImageDrawable( getResources().getDrawable( R.drawable.ic_panel_down));
            }
            else
            {
                buttonPanel.animate().translationY(slidingHeight);
                upButton.setImageDrawable( getResources().getDrawable( R.drawable.ic_panel_up));
            }

            bPanelHidden = !bPanelHidden;
        }
    };


    //view information dialog
    private  View.OnClickListener viewDetails = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            String tags = "";
            for(String s:ti.tags)
                tags+= "#" +s;

           View view = LayoutInflater.from(DisplayImage.this).inflate(R.layout.display_textile_with_details,null,false);

            ((TextView)view.findViewById(R.id.text2)).setText(ti.textile_type);
            ((TextView)view.findViewById(R.id.text4)).setText(ti.textile_variation);
            ((TextView)view.findViewById(R.id.text6)).setText(ti.textile_material);
            ((TextView)view.findViewById(R.id.text8)).setText(ti.textile_color_type);
            ((TextView)view.findViewById(R.id.text10)).setBackgroundColor(android.graphics.Color.parseColor(ti.primaryColor));

            if(ti.secondaryColor != null) {
                ((TextView) view.findViewById(R.id.text12)).setBackgroundColor(android.graphics.Color.parseColor(ti.secondaryColor));
            }
            else
            {
                view.findViewById(R.id.text11).setVisibility(View.INVISIBLE);
                view.findViewById(R.id.text12).setVisibility(View.INVISIBLE);
            }
            ((TextView)view.findViewById(R.id.text13)).setText(tags);

          new AlertDialog.Builder(DisplayImage.this).setView(view).setTitle("Textile information").setNegativeButton("OK",null)
                  .show();
        }
    };

    //select for charity function
    public void selectForCharity()
    {
        SQLiteDatabase db  = (new DAOdb(this)).getDatabase();

        ContentValues cv = new ContentValues();

        if(ti.selectForCharity == 0) {
            cv.put("selected_for_charity", 1);
        }
        else
        {
            cv.put("selected_for_charity",0);
        }

        if(db.update("textile",cv,"id=?" , new String[] {String.valueOf(ti._id)}) > 0)
        {
            ti.selectForCharity = ti.selectForCharity > 0 ? 0 : 1;

            if(ti.selectForCharity == 0 ) {
                donationNormalButton.setImageDrawable(getResources().getDrawable(R.drawable.donate_normal));
                Toast.makeText(DisplayImage.this,"You have removed this to charity stuffs",Toast.LENGTH_LONG).show();
            }
            else{
                donationNormalButton.setImageDrawable(getResources().getDrawable(R.drawable.donate_active));
                Toast.makeText(DisplayImage.this,"You have added this to charity stuffs",Toast.LENGTH_LONG).show();
            }

        }
    }

}
