package com.example.vlad.scruji.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.vlad.scruji.Models.Post;
import com.example.vlad.scruji.Models.Tag;
import com.example.vlad.scruji.Models.User;

import java.util.ArrayList;
import java.util.List;

public class MyDB extends SQLiteOpenHelper {
    public final static int DB_VERSION = 3;
    public final static String DB_NAME = "AppSQLiteDataBase";

    public final static String TAGS_TABLE_NAME = "TagsTable";
    public final static String POSTS_TABLE_NAME = "PostsTable";
    public final static String USERS_TABLE_NAME = "UsersTable";

    public final static String ID = "ID";
    public final static String USER_ID = "USER_ID";

    public final static String TAGS = "TAGS";

    public final static String NAME = "NAMES";
    public final static String LASTNAME = "LASTNAMES";
    public final static String AGE = "AGES";
    public final static String COUNTRY = "COUNTRIES";
    public final static String CITY = "CITIES";

    public final static String DATE = "DATE";
    public final static String DESCRIPTION = "DESCRIPTION";

    String tagsTableQuery = "CREATE TABLE "+
            TAGS_TABLE_NAME+
            " ("+ID+ " INTEGER PRIMARY KEY, "+
            USER_ID+ " TEXT, "+
            TAGS+ " TEXT)";
    String postsTableQuery = "CREATE TABLE "+
            POSTS_TABLE_NAME+
            " ("+ID+ " INTEGER PRIMARY KEY, "+
            USER_ID+ " TEXT, "+
            DATE+ " TEXT, "+
            DESCRIPTION+ " TEXT)";
    String usersTableQuery = "CREATE TABLE "+
            USERS_TABLE_NAME+
            " ("+ID+ " INTEGER PRIMARY KEY, "+
            USER_ID+" TEXT, "+
            NAME+" TEXT, "+
            LASTNAME+" TEXT, "+
            AGE+" TEXT, "+
            COUNTRY+" TEXT, "+
            CITY+" TEXT)";

    public MyDB(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(tagsTableQuery);
        db.execSQL(postsTableQuery);
        db.execSQL(usersTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS "+TAGS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+POSTS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+USERS_TABLE_NAME);
        onCreate(db);
    }
    //===================================================== POSTS TABLE ================================================================
    public void insertPost(Post post) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(USER_ID,post.getUserId());
        contentValues.put(DATE,post.getDate());
        contentValues.put(DESCRIPTION,post.getDescription());

        db.insert(POSTS_TABLE_NAME,null ,contentValues);
        db.close();
    }

    public List<Post> getUserPosts(String user_id) {
        List<Post> list = new ArrayList<Post>();
        Post post;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+POSTS_TABLE_NAME+" WHERE USER_ID=?", new String[] {user_id + ""});

        if (cursor.moveToFirst()) {
            post = new Post();
            do {
                post.setDate(cursor.getString(1));
                post.setDescription(cursor.getString(2));
                list.add(post);
            } while (cursor.moveToNext());
        }
        return list;
    }
    //===================================================== TAGS TABLE ================================================================
    public void insertTag(Tag tags) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(USER_ID,tags.getUser_id());
        contentValues.put(TAGS,tags.getTagname());

        db.insert(TAGS_TABLE_NAME,null ,contentValues);
        db.close();
    }

    public List<String> getUserTags(String user_id) {
        List<String> list = new ArrayList<String>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT TAGS FROM "+TAGS_TABLE_NAME+" WHERE USER_ID=?", new String[] {user_id + ""});

        if (cursor.moveToFirst()) {
            do {
                list.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        return list;
    }

    public void deleteUserTag (String user_id,String tag) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TAGS_TABLE_NAME, USER_ID+" = '"+user_id+"' AND "+TAGS+" = '"+tag+"'", null);
        db.close();
    }

    //===================================================== USERS TABLE ==============================================================
    public void insertUser(User user) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(USER_ID,user.getUser_id());
            contentValues.put(NAME,user.getName());
            contentValues.put(LASTNAME,user.getSurname());
            contentValues.put(AGE,user.getAge());
            contentValues.put(COUNTRY,user.getCountry());
            contentValues.put(CITY,user.getCity());
            db.insert(USERS_TABLE_NAME,null ,contentValues);
            db.close();
    }

    public User getUser(String certain_id) {
        User user = new User();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + USERS_TABLE_NAME + " WHERE USER_ID=?", new String[]{certain_id + ""});

        if (cursor.moveToFirst()) {
            do {
                user.setUser_id(cursor.getString(1));
                user.setName(cursor.getString(2));
                user.setSurname(cursor.getString(3));
                user.setAge(cursor.getString(4));
                user.setCountry(cursor.getString(5));
                user.setCity(cursor.getString(6));
            } while (cursor.moveToNext());
        }
        return user;
    }
}
