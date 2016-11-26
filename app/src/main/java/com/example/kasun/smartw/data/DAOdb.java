package com.example.kasun.smartw.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class DAOdb {

    private SQLiteDatabase database;
    private DBhelper dbHelper;

    public DAOdb(Context context) {
        dbHelper = new DBhelper(context);
        database = dbHelper.getWritableDatabase();
    }

    /**
     * close any database object
     */
    public void close() {
        dbHelper.close();
    }

    /**
     * insert a text report item to the location database table
     *
     * @param ti
     * @return the row ID of the newly inserted row, or -1 if an error occurred
     */
    public long addImage(TextileInfo ti) {
        ContentValues cv = new ContentValues();
        cv.put(DBhelper.COLUMN_PATH, ti.path);
        cv.put("id", ti.image_id);
       // cv.put(DBhelper.COLUMN_DESCRIPTION, image.getDescription());
        cv.put(DBhelper.COLUMN_DATETIME, ti.datetime);
        return database.insert(DBhelper.TABLE_NAME, null, cv);
    }

    //insert textile
    public long addTextile(TextileInfo ti )
    {
        ContentValues cv = new ContentValues();
        cv.put( "id" , ti._id);
        cv.put( "textile_type" , ti.textile_type);
        cv.put( "textile_material",ti.textile_material );
        cv.put("textile_variation",ti.textile_variation);
        cv.put("textile_color_type",ti.textile_color_type);
        cv.put("textile_image",ti.image_id);
        cv.put("primary_color",ti.primaryColor);

        if(ti.textile_color_type.equalsIgnoreCase("mixed") == true)
        {
            cv.put("secondary_color",ti.secondaryColor);
        }

        return  database.insert("textile",null,cv);
    }

    public void addTextileTags(TextileInfo ti)
    {
        int i = 0;
        for(String s : ti.tags)
        {
            if(s.trim().length() == 0) continue;

            ContentValues cv = new ContentValues();
            cv.put( "textile_id" , ti._id);
            cv.put( "tag_index",i );
            cv.put("tag_text",s);
            database.insert("textile_tag",null,cv);
            i++;
        }
    }

    /**
     * delete the given image from database
     *
     */
    public void deleteImage(long textile_id,long image_id) {
        String whereClause ="id=?";
        String[] whereArgs = new String[]{ String.valueOf(image_id)};
        database.delete("image", whereClause, whereArgs);

        whereClause ="textile_id=?";
        whereArgs = new String[]{ String.valueOf(textile_id)};
        database.delete("textile_tag", whereClause, whereArgs);

        whereClause ="id=?";
        whereArgs = new String[]{ String.valueOf(textile_id)};
        database.delete("textile", whereClause, whereArgs);
    }

    public static final int INDEX_ID_COLUMN = 0, INDEX_TYPE_COLUMN =1,INDEX_VARIATION_COLUMN=2,INDEX_MATERIAL_COLUMN=3,
    INDEX_COLOR_TYPE_COLUMN=4,INDEX_IMAGE_ID_COLUMN =5,INDEX_IMAGE_PATH_COLUMN =6 , INDEX_DATETIME_COLUMN = 7,
            INDEX_TAG_INDEX_COLUMN=8, INDEX_TAG_TEXT_COLUMN=9,INDEX_PRIMARY_COLOR_COLUMN=10,INDEX_SECONDARY_COLOR=11,INDEX_SELECTED_FOR_CHARITY=12;


    /**
     * @return all image as a List
     */
    public List<DAOdb.TextileInfo> getImages() {



        ArrayList<DAOdb.TextileInfo> textiles = new ArrayList<DAOdb.TextileInfo>();

       String sql = "SELECT " +
                "t.id,t.textile_type,t.textile_variation,t.textile_material,t.textile_color_type,t.textile_image" +
                ",image.path, image.datetime,tt.tag_index,tt.tag_text,t.primary_color,t.secondary_color,t.selected_for_charity  FROM textile t INNER JOIN image " +
                "ON t.textile_image = image.id INNER JOIN " +
                "textile_tag tt ON t.id=tt.textile_id order by image.datetime desc";

       // String sql = "SELECT *   FROM  textile  [INNER] JOIN textile_tag  ON _id = textile_id";

        Cursor cursor =
                database.rawQuery(sql,null);

        long tid = -1;

        String tags=",";
        TextileInfo ti = null;



            while (cursor.moveToNext())  {

                long curID = cursor.getLong(INDEX_ID_COLUMN);

                if (curID != tid) {

                    if (ti != null) {
                        ti.tags = tags.substring(0,tags.length()-1).split(",");
                        textiles.add(ti);
                    }

                    ti = new TextileInfo();
                    ti._id = curID;
                    ti.image_id = cursor.getLong(INDEX_IMAGE_ID_COLUMN);
                    ti.textile_color_type = cursor.getString(INDEX_COLOR_TYPE_COLUMN);
                    ti.datetime = cursor.getLong(INDEX_DATETIME_COLUMN);
                    ti.path = cursor.getString(INDEX_IMAGE_PATH_COLUMN);
                    ti.textile_material = cursor.getString(INDEX_MATERIAL_COLUMN);
                    ti.textile_type = cursor.getString(INDEX_TYPE_COLUMN);
                    ti.textile_variation = cursor.getString(INDEX_VARIATION_COLUMN);
                    ti.primaryColor = cursor.getString(INDEX_PRIMARY_COLOR_COLUMN);
                    ti.secondaryColor = cursor.getString(INDEX_SECONDARY_COLOR);
                    ti.selectForCharity = cursor.getInt(INDEX_SELECTED_FOR_CHARITY);


                    tags = "";
                    tags=(cursor.getString(INDEX_TAG_TEXT_COLUMN))  + ",";

                } else {
                    tags+=(cursor.getString(INDEX_TAG_TEXT_COLUMN)) + ",";
                }

                tid = curID;

            }


        if(ti != null){
            ti.tags = tags.substring(0,tags.length()-1).split(",");
            textiles.add(ti);
        }

        cursor.close();
        return textiles;
    }

    //get editing details
    public TextileInfo getTextileInfo(long textile_id) {



        DAOdb.TextileInfo ti = new DAOdb.TextileInfo();

        String sql = "SELECT " +
                "t.id,t.textile_type,t.textile_variation,t.textile_material,t.textile_color_type,t.textile_image" +
                ",image.path, image.datetime,tt.tag_index,tt.tag_text,t.primary_color,t.secondary_color,t.selected_for_charity  FROM " +
                " (SELECT * FROM textile WHERE id=?) t INNER JOIN image " +
                "ON t.textile_image = image.id INNER JOIN " +
                "textile_tag tt ON t.id=tt.textile_id";

        // String sql = "SELECT *   FROM  textile  [INNER] JOIN textile_tag  ON _id = textile_id";

        Cursor cursor =
                database.rawQuery(sql,new String[]{ String.valueOf(textile_id)});


        if(cursor.getCount() < 1)
            return null;

        String tags=",";

        while (cursor.moveToNext())  {

            long curID = cursor.getLong(INDEX_ID_COLUMN);

            if (ti._id==0) {


               // ti = new TextileInfo();
                ti._id = curID;
                ti.image_id = cursor.getLong(INDEX_IMAGE_ID_COLUMN);
                ti.textile_color_type = cursor.getString(INDEX_COLOR_TYPE_COLUMN);
                ti.datetime = cursor.getLong(INDEX_DATETIME_COLUMN);
                ti.path = cursor.getString(INDEX_IMAGE_PATH_COLUMN);
                ti.textile_material = cursor.getString(INDEX_MATERIAL_COLUMN);
                ti.textile_type = cursor.getString(INDEX_TYPE_COLUMN);
                ti.textile_variation = cursor.getString(INDEX_VARIATION_COLUMN);
                ti.primaryColor = cursor.getString(INDEX_PRIMARY_COLOR_COLUMN);
                ti.secondaryColor = cursor.getString(INDEX_SECONDARY_COLOR);
                ti.selectForCharity = cursor.getInt(INDEX_SELECTED_FOR_CHARITY);


                tags = "";
                tags=(cursor.getString(INDEX_TAG_TEXT_COLUMN))  + ",";

            } else {
                tags+=(cursor.getString(INDEX_TAG_TEXT_COLUMN)) + ",";
            }



        }



        ti.tags = tags.substring(0,tags.length()-1).split(",");


        cursor.close();
        return ti;
    }

    //udpate textile table
    public boolean updateTextiles(TextileInfo ti) {
        ContentValues cv = new ContentValues();
        cv.put("textile_type", ti.textile_type);
        cv.put("textile_material", ti.textile_material);
        cv.put("textile_variation", ti.textile_variation);
        cv.put("textile_color_type", ti.textile_color_type);
        cv.put("textile_image", ti.image_id);
        cv.put("primary_color", ti.primaryColor);


        if (ti.textile_color_type.equalsIgnoreCase("mixed") == true) {
            cv.put("secondary_color", ti.secondaryColor);
        }

       return database.update("textile",cv,"id=?" , new String[] {String.valueOf(ti._id)}) >= 0;
    }

    public boolean updateTags(TextileInfo ti)
    {

        database.delete("textile_tag","textile_id=?" , new String[]{String.valueOf(ti._id)});

        addTextileTags(ti);

        return true;

    }

    //this is for update image date time for gallery
    public  boolean updateImageDetails(TextileInfo ti)
    {
        ContentValues cv = new ContentValues();
        cv.put("datetime", ti.datetime);

        return database.update("image",cv,"id=?" , new String[]{String.valueOf(ti.image_id)})  >= 0;
    }

    public static class TextileInfo
    {
        /** ID of the image of textile */
        public long image_id;
        public long _id ;
        public String textile_type ;
        public String textile_variation;
        public String textile_material;
        public String textile_color_type;
        public String tags[];
        public String path;
        public long datetime;
        public  String primaryColor;
        public  String secondaryColor;
        public int selectForCharity;


    }

    public String [] getTagSuggestion()
    {
        String sql = "SELECT DISTINCT tag_text FROM textile_tag";
        Cursor cursor = database.rawQuery(sql,null);

        if(cursor.getCount()>0)
        {
            int i = 0;
            String [] tags = new String[cursor.getCount()];
            while (cursor.moveToNext())
            {
                tags[i] = cursor.getString(0);
                i++;
            }
            return tags;
        }
        else
            return new String []{};

    }

    public SQLiteDatabase getDatabase()
    {
        return this.database;
    }

    //delete outfit

    public void deleteOutfit(long topId,long botId) {

        String whereClause ="top_id =? AND botm_id =?";
        String[] whereArgs = new String[]{ String.valueOf(topId),String.valueOf(botId)};
        database.delete("outfit", whereClause, whereArgs);

    }

    public int getOutFitCount()
    {
        String sql = "SELECT COUNT(top_id) as ocount FROM outfit GROUP BY top_id";
        Cursor cursor = database.rawQuery(sql,null);
        if(!cursor.moveToNext())
            return 0;


        int res =cursor.getInt(0);
        return res;
    }

}