package com.example.kasun.smartw;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.kasun.smartw.data.DAOdb;

import java.util.ArrayList;

/**
 * Created by Ruvinda on 11/2/2016.
 */

public final class ColorPicker {

    private Context mContext;
    private ArrayList<Color> colorSwatch;
    private ListView colorlistView;
    private AlertDialog mDialog;

    private ColorPickerEvents mEvents;

    public ColorPicker(Context context, ColorPickerEvents events)
    {
        this.mContext = context;
        mEvents = events;
        loadColorlist();
    }

    public void loadColorlist()
    {
        DAOdb db = new DAOdb(mContext);
        SQLiteDatabase dbo = db.getDatabase();

        String sql = "SELECT * FROM color_swatch";

        Cursor cursor = dbo.rawQuery(sql,null);

        colorSwatch = new ArrayList<Color>();

        while(cursor.moveToNext())
        {
            Color color = new Color();
            color.color_code = cursor.getString(0);
            color.color_name = cursor.getString(1);

            if(color.color_name == "")
            {
                color.color_name = color.color_code;
            }

            colorSwatch.add(color);
        }

    }

    public void showDialog()
    {
        colorlistView = (ListView) LayoutInflater.from(mContext).inflate(R.layout.color_picker_layout,null,false);
        colorlistView.setAdapter(new ColorAdapter(mContext,colorSwatch));

        colorlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                mEvents.onColorSelected(colorSwatch.get(position).color_code);
                mDialog.dismiss();

            }
        });

       mDialog = new AlertDialog.Builder(mContext).setTitle("Color Picker").setNegativeButton("Cancel",null).setView(colorlistView)
                .show();
    }


    public interface  ColorPickerEvents
    {
        public void onColorSelected(String color);
    }


    public static  class Color{
        public String color_code ;
        public String color_name ;
    }

    public static class ColorAdapter extends ArrayAdapter<Color>
    {
        private Context context;
        private ArrayList<Color> colors;

        public ColorAdapter(Context context , ArrayList<Color> colorList)
        {
            super(context, R.layout.color_picker_item,colorList);
            this.context = context;
            colors = colorList;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null)
            {
                convertView = LayoutInflater.from(context).inflate(R.layout.color_picker_item,parent,false);
            }
            Color color = getItem(position);

            ((TextView)convertView.findViewById(R.id.color_picker_color_box))
                    .setBackgroundColor(android.graphics.Color.parseColor(color.color_code));
            ((TextView)convertView.findViewById(R.id.color_picker_color_name)).setText(color.color_code);

            return convertView;

        }

        @Nullable
        @Override
        public Color getItem(int position) {

            return colors.get(position);

        }
    }
}
