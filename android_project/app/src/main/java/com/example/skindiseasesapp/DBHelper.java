package com.example.skindiseasesapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "Login.db";
    private static final int DB_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE users(username TEXT PRIMARY KEY, password TEXT)");

        db.execSQL("CREATE TABLE history(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "prediction TEXT, " +
                "symptoms TEXT, " +
                "date TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        onCreate(db);
    }


    // ✅ Здесь метод insertUser, чтобы совпадало с Register.java
    public boolean insertUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("password", password);

        long result = db.insert("users", null, values);
        db.close();
        return result != -1; // если вставка успешна, вернёт true
    }

    // Insert history
    public boolean insertHistory(String prediction, String symptoms, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("prediction", prediction);
        values.put("symptoms", symptoms);
        values.put("date", date);

        long result = db.insert("history", null, values);
        db.close();
        return result != -1;
    }

    // Fetch all history
    public Cursor getHistory() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM history ORDER BY id DESC", null);
    }


    public boolean checkUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE username=?", new String[]{username});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    public boolean checkUsernamePassword(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE username=? AND password=?", new String[]{username, password});
        boolean valid = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return valid;
    }
}
