/**
 * @author Ruvinda Isuru
 */
package com.example.kasun.smartw;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.kasun.smartw.data.DAOdb;

import java.io.File;
import java.util.Random;

public class AddTextileDescription extends AppCompatActivity {

    private String imagepath;
    private MultiAutoCompleteTextView mTagsTextbox;

    //member variables
    private Spinner textileType;
    private Spinner textileVariation;
    private Spinner materialType;
    private Spinner colourType;

    private Button addButton;
    private Button cancelButton;
    private Button primColrButton;
    private Button secColrButton;
    private  boolean editing;

    private  long editingID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_textile_description);

        DAOdb db = new DAOdb(this);
        // add suggestions
        mTagsTextbox = (MultiAutoCompleteTextView) findViewById(R.id.textileTags);


        textileType = (Spinner) findViewById(R.id.textileTypeSpinner);
        textileVariation = (Spinner) findViewById(R.id.textileVariationSpinner);
        materialType = (Spinner) findViewById(R.id.textileMaterial);
        colourType = (Spinner) findViewById(R.id.textileColorTypeSpinner);

        addButton = (Button) findViewById(R.id.addButton);
        cancelButton = (Button) findViewById(R.id.cancelButton);
        primColrButton = (Button) findViewById(R.id.primaryColorButton);
        secColrButton = (Button) findViewById(R.id.secondaryColorButton);

        if(getIntent().hasExtra("editing")) {


            DAOdb.TextileInfo ti = db.getTextileInfo(getIntent().getLongExtra("textile_id",0));

            if(ti == null)
            {
                //TODO Display error message
            }
            else
            {
                File file = new File(ti.path);
                ((ImageView) findViewById(R.id.textileIcon)).setImageURI(Uri.fromFile(file));
                textileType.setSelection(getItemPosition(getResources().getStringArray(R.array.textileType),ti.textile_type));
                textileVariation.setSelection(getItemPosition(getResources().getStringArray(R.array.textileVariation),ti.textile_variation));
                materialType.setSelection(getItemPosition(getResources().getStringArray(R.array.materialtype),ti.textile_material));
                colourType.setSelection(getItemPosition(getResources().getStringArray(R.array.colourType),ti.textile_color_type));

                primColrButton.setBackgroundColor(android.graphics.Color.parseColor(ti.primaryColor));
                primColrButton.setTag(ti.primaryColor);

                mTagsTextbox.setText(implode(ti.tags,","));
                if(colourType.getSelectedItemPosition() == 0)
                {
                    ((View)findViewById(R.id.secondaryColorLayout)).setVisibility(View.INVISIBLE);
                }
                else
                {
                    ((View)findViewById(R.id.secondaryColorLayout)).setVisibility(View.VISIBLE);
                    secColrButton.setBackgroundColor(android.graphics.Color.parseColor(ti.secondaryColor));
                    secColrButton.setTag(ti.secondaryColor);

                }
                addButton.setText("Update");
                editing = true;
                editingID = ti._id;
                imagepath = ti.path;

            }

        }
        else {
            //get image path
            imagepath = getIntent().getStringExtra("imagePath");
            //load image
            File file = new File(imagepath);

            ((ImageView) findViewById(R.id.textileIcon)).setImageURI(Uri.fromFile(file));
        }


        colourType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0)
                {
                    ((View)findViewById(R.id.secondaryColorLayout)).setVisibility(View.INVISIBLE);
                }
                else
                {
                    ((View)findViewById(R.id.secondaryColorLayout)).setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        String tagsSug[] = getResources().getStringArray(R.array.tagSugessions);
        String dbTags[] = db.getTagSuggestion();
        String mergedTags[] = new String[tagsSug.length+dbTags.length];

        int pos = 0;

        for(String s:tagsSug)
                mergedTags[pos++] = s;

        for(String s:dbTags)
                mergedTags[pos++] = s;

        mTagsTextbox.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,mergedTags));
        mTagsTextbox.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        //cancel button event
        cancelButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
             finish();
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertOrUpdateTextiles();
            }
        });
        primColrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                primaryColorFunction();
            }
        });

        secColrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                secondaryColorFunction();
            }
        });
    }

    private void insertOrUpdateTextiles() {


        //Validating tags not empty
        if(mTagsTextbox.getText().toString().trim().length() == 0 )
        {
            Toast.makeText(this,"Tags cannot be empty" , Toast.LENGTH_LONG).show();
            return;
        }
        //validatin primary color
        if(primColrButton.getTag()==null)
        {
            Toast.makeText(this,"Select a primary color" , Toast.LENGTH_LONG).show();
            return;
        }

        //validating secondary color
        if(secColrButton.getTag()==null && colourType.getSelectedItem().toString().equalsIgnoreCase("mixed") == true)
        {
            Toast.makeText(this,"Select a secondary color" , Toast.LENGTH_LONG).show();
            return;
        }


        DAOdb db = new DAOdb(this);

        DAOdb.TextileInfo ti = new DAOdb.TextileInfo();
        ti.path=(imagepath);

        if(!editing){
            Random rand = new Random(System.currentTimeMillis());
            ti.image_id = (System.currentTimeMillis() * 10) +  rand.nextInt();
            ti.datetime=(System.currentTimeMillis());

            long image_id = db.addImage(ti);

            //if failure happend while saving image
            if(image_id < 0) {
                textileFailure();
                return ;
            }

            ti._id = ti.image_id;
        }
        else
        {
            ti.image_id=editingID;
            ti._id = editingID;
        }




        //DAOdb.TextileInfo ti = new DAOdb.TextileInfo();
        ti.textile_type = (String) this.textileType.getSelectedItem();
        ti.textile_material = (String) this.materialType.getSelectedItem();
        ti.textile_color_type= (String) this.colourType.getSelectedItem();
        ti.textile_variation = (String) this.textileVariation.getSelectedItem();

        ti.primaryColor = primColrButton.getTag().toString();

        if(ti.textile_color_type.equalsIgnoreCase("mixed") == true)
        {
            ti.secondaryColor = secColrButton.getTag().toString();
        }

        ti.tags = mTagsTextbox.getText().toString().split(",");

        if(!editing) {
            //if failure happend while saving texile
            if (db.addTextile(ti) < 0) {
                textileFailure();
                return;
            }

            db.addTextileTags(ti);
            textileSuccess();
        }
        else
        {
            ti.datetime=System.currentTimeMillis();
            db.updateImageDetails(ti);
            db.updateTextiles(ti);
            db.updateTags(ti);
            textileUpdateSuccess();
        }

    }

    //showing dialog boxes
    private void textileFailure()
    {
        new AlertDialog.Builder(this).setMessage("Failed saving textile details").setTitle("Error")
                .setCancelable(false).setNegativeButton(android.R.string.cancel,null).show();
    }

    private void textileSuccess()
    {
        new AlertDialog.Builder(this).setMessage("Your details have been saved").setTitle("Success")
                .setCancelable(false).setNegativeButton(android.R.string.ok,new AlertDialog.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                AddTextileDescription.this.finish();
                Intent intent = new Intent(AddTextileDescription.this,SmartWardrobe.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }).show();
    }

    private void textileUpdateSuccess()
    {
        new AlertDialog.Builder(this).setMessage("Your details have been updated").setTitle("Success")
                .setCancelable(false).setNegativeButton(android.R.string.ok,new AlertDialog.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                AddTextileDescription.this.finish();
                Intent intent = new Intent(AddTextileDescription.this,SmartWardrobe.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }).show();
    }

    //calling color picker function
    public void primaryColorFunction()
    {
        ColorPicker cp = new ColorPicker(this, new ColorPicker.ColorPickerEvents() {
            @Override
            public void onColorSelected(String color) {
                primColrButton.setBackgroundColor(android.graphics.Color.parseColor(color));
                primColrButton.setTag(color);
            }
        });

        cp.showDialog();
    }

    public void secondaryColorFunction()
    {
        ColorPicker cp = new ColorPicker(this, new ColorPicker.ColorPickerEvents() {
            @Override
            public void onColorSelected(String color) {
                secColrButton.setBackgroundColor(android.graphics.Color.parseColor(color));
                secColrButton.setTag(color);
            }
        });

        cp.showDialog();
    }

    //Getting the position of the text in the array
    private static int getItemPosition (String[] items, String item)
    {
        int pos = 0;
        for(String s:items)
        {
            if(s.contentEquals(item) == true)
                return pos;

            pos++;
        }

        return 0;
    }

    //get tag values
    private static String implode(String [] items, String join)
    {
        String result ="";
        for(int i =0;i<items.length;i++)
        {
            if(i == items.length-1)
            {
                result+= items[i];
            }
            else
            {
                result += items[i]+join;
            }
        }

        return result;
    }
}
