package com.example.ires;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class CallLogSQLiteDB extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "local_database";
    public static final String USER_TABLE_NAME = "user";
    public static final String USER_COLUMN_ID = "_id";
    public static final String USER_COLUMN_NAME = "name";
    public static final String USER_COLUMN_NUMBER = "number";

    public CallLogSQLiteDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String quary = ("CREATE TABLE " + USER_TABLE_NAME + " (" +
                USER_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                USER_COLUMN_NAME + " TEXT, " +
                USER_COLUMN_NUMBER + " TEXT" + ")");
        sqLiteDatabase.execSQL(quary);
    }
    public void addData(String name, String number){
        // on below line we are creating a variable for
        // our sqlite database and calling writable method
        // as we are writing data in our database.
        SQLiteDatabase db = this.getWritableDatabase();

        // on below line we are creating a
        // variable for content values.
        ContentValues values = new ContentValues();
        // on below line we are passing all values
        // along with its key and value pair.
        values.put(DATABASE_NAME, name);
        values.put(USER_COLUMN_NUMBER, number);

        // after adding all values we are passing
        // content values to our table
        db.insert(USER_TABLE_NAME, null, values);
        // at last we are closing our
        // database after adding database.
        db.close();
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + USER_TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
