package com.example.kasun.smartw;

/**
 * Created by Deepika on 19-Oct-16.
 */
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kasun.smartw.data.DAOdb;

import java.io.File;
import java.util.ArrayList;

/**
 * A ListView is used to show a vertical list of scrollable items. ArrayAdapter
 * can be used to converts an ArrayList of objects into view items loaded into
 * the ListView container.
 */
public class ImageAdapter extends ArrayAdapter<DAOdb.TextileInfo> {
    private final int THUMBSIZE = 96;

    /**
     * applying ViewHolder pattern to speed up ListView, smoother and faster
     * item loading by caching view in A ViewHolder object
     */
    private static class ViewHolder {
        ImageView imgIcon;
        TextView type,material,tags,variation;
    }

    public ImageAdapter(Context context, ArrayList<DAOdb.TextileInfo> ti) {
        super(context, 0, ti);
    }

    @Override public View getView(int position, View convertView,
                                  ViewGroup parent) {
        // view lookup cache stored in tag
        ViewHolder viewHolder;
        // Check if an existing view is being reused, otherwise inflate the
        // item view
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.content_smart_wardrobe_item_image, parent, false);
            viewHolder.type =
                    (TextView) convertView.findViewById(R.id.list_item_tex_type);
            viewHolder.material =
                    (TextView) convertView.findViewById(R.id.list_item_tex_material);
            viewHolder.variation =
                    (TextView) convertView.findViewById(R.id.list_item_tex_variation);
            viewHolder.imgIcon =
                    (ImageView) convertView.findViewById(R.id.item_img_icon);

            viewHolder.tags = (TextView) convertView.findViewById(R.id.item_textile_tags);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Get the data item for this position
        DAOdb.TextileInfo ti = getItem(position);
        // set description text

        String tags = "";
        for( String s :ti.tags)
            tags += "#" + s + " ";
        viewHolder.tags.setText(tags);

        viewHolder.variation.setText( ti.textile_variation);
        viewHolder.material.setText(ti.textile_material);
        viewHolder.type.setText(ti.textile_type);

        // set image icon
        File file = new File(ti.path);
        viewHolder.imgIcon.setImageURI(Uri.fromFile(file));

        // Return the completed view to render on screen
        return convertView;
    }
}
