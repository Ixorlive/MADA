package com.example.mada_2.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.mada_2.Catalog.Meter;

public class DBWorker extends SQLiteOpenHelper {

    public static final String DB_NAME = "madaDB.db";

    public static final String USERS = "USERS";
    public static final String COLUMN_DISTRICT = "district";
    public static final String COLUMN_PASSWORD = "password";

    public static final String METERS = "METERS";
    public static final String ID_USER = "id_user";
    public static final String NAME = "meter_name";
    public static final String OLD_STATEMENT = "old_statement";

    public DBWorker(@Nullable Context context) {
        super(context, DB_NAME, null, 1);
    }

    //Первое открытие - создаём БД
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createUsersTable = "CREATE TABLE " + USERS
                + " (" + COLUMN_PASSWORD +
                " TEXT PRIMARY KEY, "
                + COLUMN_DISTRICT + " TEXT )";
        sqLiteDatabase.execSQL(createUsersTable);

        //таблица счетчиков
        String createMetersTable = "CREATE TABLE " + METERS
                + " (" + ID_USER + " TEXT NOT NULL, "
                + NAME + " TEXT NOT NULL, "
                + OLD_STATEMENT + " REAL DEFAULT 0, "
                + "FOREIGN KEY (" + ID_USER
                    + ") REFERENCES " + USERS
                    + "(" + COLUMN_PASSWORD + ")"
                + ")";
        sqLiteDatabase.execSQL(createMetersTable);
    }

    //Изменение версии БД
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    //добавление пользователя
    public boolean addUser(User user) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        if(!isUserExists(user))
        {
            contentValues.put(COLUMN_PASSWORD, user.getPassword());
            contentValues.put(COLUMN_DISTRICT, user.getDistrict());
            if(sqLiteDatabase.insert(USERS, null, contentValues) == -1) {
                return false;
            }
            else {
                return true;
            }
        }
        return true;
    }

    //добавление счетчика
    public boolean addMeter(User user, Meter meter)
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID_USER, user.getPassword());
        contentValues.put(NAME, meter.getName());
        contentValues.put(OLD_STATEMENT, meter.getMeter_reading());
        if(sqLiteDatabase.insert(METERS, null, contentValues) == -1) {
            return false;
        }
        else {
            return true;
        }
    }

    //обновление счетчика
    public boolean updateMeter(User user, Meter meter)
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(OLD_STATEMENT, meter.getMeter_reading());
        if(sqLiteDatabase.update(METERS, contentValues, ID_USER + " = ?", new String[] {user.getPassword()}) == -1) {
            return false;
        }
        else {
            return true;
        }
    }

    //проверка на существование пользователя
    public boolean isUserExists(User user)
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase
                .query(USERS, null,
                        COLUMN_PASSWORD + " = ?", new String[] { user.getPassword() },
                        null, null, null);
        if(!Integer.valueOf(cursor.getCount()).equals(0))
        {
            cursor.close();
            return true;
        }
        else
        {
            cursor.close();
            return false;
        }
    }

    //проверка на пустоту бд
    public boolean isDbEmpty()
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase
                .query(USERS, null,
                        null, null,
                        null, null, null);
        if(Integer.valueOf(cursor.getCount()).equals(0))
        {
            cursor.close();
            return true;
        }
        else
        {
            cursor.close();
            return false;
        }
    }

}
