package com.example.vlad.scruji.Main_Screen_With_Tabs.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.vlad.scruji.Main_Screen_With_Tabs.Models.MyTag;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vlad on 12.03.2017.
 */

public class TagSQL extends SQLiteOpenHelper {
    public final static int DB_VERSION = 3 ;
    public final static String DB_NAME = "MyTagDB";
    public final static String TABLE_NAME = "TagsTable";
    public final static String COL_1 = "ID";
    public final static String COL_2 = "USER_ID";
    public final static String COL_3 = "TAGNAMES";


    public TagSQL(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE "+TABLE_NAME+" ("+COL_1+" INTEGER PRIMARY KEY, "+COL_2+" TEXT, "+COL_3+" TEXT)";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }
    //=======================================METHODS======================================================================================
    public void insertTag(MyTag tags) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2,tags.getUser_id());
        contentValues.put(COL_3,tags.getTagname());

        db.insert(TABLE_NAME,null ,contentValues);
        Log.d("TAG+","Tag was inserted to SQLite: " + tags.getTagname());
        db.close();
    }

    public List<MyTag> getUserTags(String user_id) {
        List<MyTag> tagsList = new ArrayList<MyTag>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+TABLE_NAME+" WHERE USER_ID=?", new String[] {user_id + ""});

        if (cursor.moveToFirst()) {
            do {
                MyTag tags = new MyTag();
                tags.setUser_id(cursor.getString(1));
                tags.setTagname(cursor.getString(2));
                tagsList.add(tags);
            } while (cursor.moveToNext());
        }
        return tagsList;
    }

    public List<MyTag> getAllTags() {
        List<MyTag> tagsList = new ArrayList<MyTag>();
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);

        if (cursor.moveToFirst()) {
            do {
                MyTag tags = new MyTag();
                tags.setUser_id(cursor.getString(1));
                tags.setTagname(cursor.getString(2));
                tagsList.add(tags);
            } while (cursor.moveToNext());
        }
        return tagsList;
    }

    public int getTagsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
        return cursor.getCount();
    }

    public int deleteTag (String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "ID = ?",new String[] {id});
    }

    public void deleteAllTags()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME,null,null);
        db.execSQL("DELETE * FROM "+ TABLE_NAME);
        db.close();
    }
    //====================================================================================================================================
    @Override
    protected void finalize() throws Throwable {
        this.close();
        super.finalize();
    }

}
