package com.example.vlad.scruji.Set_User_Profile_Data.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.vlad.scruji.Main_Screen_With_Tabs.Models.MyTag;
import com.example.vlad.scruji.Set_User_Profile_Data.Models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vlad on 09.03.2017.
 */

public class UserProfileDB extends SQLiteOpenHelper {

    public final static int DB_VERSION = 3 ;
    public final static String DB_NAME = "UserProfileData";
    public final static String TABLE_NAME = "User";
    public final static String COL_1 = "ID";
    public final static String COL_2 = "USER_ID";
    public final static String COL_3 = "NAME";
    public final static String COL_4 = "SURNAME";
    public final static String COL_5 = "AGE";
    public final static String COL_6 = "COUNTRY";
    public final static String COL_7 = "CITY";
    public UserProfileDB(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String query = "CREATE TABLE "+TABLE_NAME+
                " ("+COL_1+" INTEGER PRIMARY KEY, " +
                COL_2+" TEXT," +
                COL_3+" TEXT," +
                COL_4+" TEXT," +
                COL_5+" TEXT," +
                COL_6+" TEXT," +
                COL_7+" TEXT)";
        db.execSQL(query);
    }

    @Override
    protected void finalize() throws Throwable {
        this.close();
        super.finalize();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }
    public void insertData(User user) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2,user.getUser_id());
        contentValues.put(COL_3,user.getName());
        contentValues.put(COL_4,user.getSurname());
        contentValues.put(COL_5,user.getAge());
        contentValues.put(COL_6,user.getCountry());
        contentValues.put(COL_7,user.getCity());

        db.insert(TABLE_NAME,null ,contentValues);
        db.close();

    }

    public List<User> getAllData() {
        List<User> contactList = new ArrayList<User>();
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        if (cursor.moveToFirst()) {
            do {
                User contact = new User();
                contact.setUser_id(cursor.getString(1));
                contact.setName(cursor.getString(2));
                contact.setSurname(cursor.getString(3));
                contact.setAge(cursor.getString(4));
                contact.setCountry(cursor.getString(5));
                contact.setCity(cursor.getString(6));

                contactList.add(contact);
            } while (cursor.moveToNext());
        }

        return contactList;
    }

    public int getContactsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

    public List<User> getCertainUser(String certain_id) {

        List<User> userList = new ArrayList<User>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE USER_ID=?", new String[]{certain_id + ""});

        if (cursor.moveToFirst()) {
            do {
                User user = new User();
                user.setUser_id(cursor.getString(1));
                user.setName(cursor.getString(2));
                user.setSurname(cursor.getString(3));
                user.setAge(cursor.getString(4));
                user.setCountry(cursor.getString(5));
                user.setCity(cursor.getString(6));
                userList.add(user);
            } while (cursor.moveToNext());
        }
        return userList;
    }


    public int updateData(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2,user.getUser_id());
        contentValues.put(COL_3,user.getName());
        contentValues.put(COL_4,user.getSurname());
        contentValues.put(COL_5,user.getAge());
        contentValues.put(COL_6,user.getCountry());
        contentValues.put(COL_7,user.getCity());
        return db.update(TABLE_NAME, contentValues, COL_2 + " = ?", new String[]{user.getUser_id()});

    }

    public int deleteData (String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "ID = ?",new String[] {id});
    }
    public void deleteAll()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME,null,null);
        db.execSQL("delete * from "+ TABLE_NAME);
        db.close();
    }
}
