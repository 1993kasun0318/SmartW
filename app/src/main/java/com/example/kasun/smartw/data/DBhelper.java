package com.example.kasun.smartw.data;

/**
 * Created by Deepika on 19-Oct-16.
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBhelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "sqliteimage.db";
    public static final int DB_VERSION = 2;

    /*
    public static final String COMMA_SEP = ",";
    public static final String TEXT_TYPE = " TEXT";
    public static final String NUMERIC_TYPE = " NUMERIC";
*/
    public static final String TABLE_NAME = "image";

    public static final String COLUMN_PATH = "path";
   // public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DATETIME = "`datetime`";

    public DBhelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override public void onCreate(SQLiteDatabase db) {

        //Creating image table
        String sql  = "create table image(id int  primary key," +
                " `datetime` int not null, path text not null)";
        db.execSQL(sql);

        sql = "create table textile(id int  primary key, textile_image int not null," +
                "textile_type text not null, textile_variation text not null, textile_material text not null," +
                "textile_color_type text not null,primary_color text not null,secondary_color text default null,selected_for_charity int default null)";
        db.execSQL(sql);

        sql = "create table textile_tag(textile_id int not null, tag_index int default 0 , tag_text text not null," +
                "primary key (textile_id,tag_index))";
        db.execSQL(sql);

        sql = "create table color_swatch(color_code text primary key not null,color_name text )";
        db.execSQL(sql);

        db.execSQL("insert into color_swatch values('#2f64d7','')");
        db.execSQL("insert into color_swatch values('#0f40a8','')");
        db.execSQL("insert into color_swatch values('#e1333b','')");
        db.execSQL("insert into color_swatch values('#e13379','')");
        db.execSQL("insert into color_swatch values('#e133be','')");
        db.execSQL("insert into color_swatch values('#db33e1','')");
        db.execSQL("insert into color_swatch values('#ae33e1','')");
        db.execSQL("insert into color_swatch values('#5833e1','')");
        db.execSQL("insert into color_swatch values('#3389e1','')");
        db.execSQL("insert into color_swatch values('#33bee1','')");
        db.execSQL("insert into color_swatch values('#33e17d','')");
        db.execSQL("insert into color_swatch values('#abed37','')");
        db.execSQL("insert into color_swatch values('#33e137','')");
        db.execSQL("insert into color_swatch values('#e7ed37','')");
        db.execSQL("insert into color_swatch values('#edc437','')");
        db.execSQL("insert into color_swatch values('#ed9e37','')");
        db.execSQL("insert into color_swatch values('#d31e1e','')");
        db.execSQL("insert into color_swatch values('#ed6a37','')");

        //create table outfits

        sql  = "create table outfit(top_id int,botm_id int," +
                " `datetime` int not null, primary key(top_id,botm_id))";
        db.execSQL(sql);



    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //add alter table queries when u r upgarading the system
       // db.rawQuery("drop table if exists textile_tag",null);
        //db.rawQuery("drop table if exists textile",null);
       // db.rawQuery("drop table if exists image",null);
        //db.rawQuery("drop table if exists color_swatch",null);
        //onCreate(db);
       // db.rawQuery("alter table textile add select_for_charity int default null",null);
    }
}
