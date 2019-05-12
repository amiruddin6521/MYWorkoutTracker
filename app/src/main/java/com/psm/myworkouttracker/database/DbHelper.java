package com.psm.myworkouttracker.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper{
    public static final String DATABASE_NAME = "mwt.db";
    public static final int DATABASE_VERSION = 2;

    //Table Machine
    public static final String TABLE_MACHINE = "machine";
    public static final String ID_MACHINE = "_id";
    public static final String NAME_MACHINE = "name";
    public static final String DESC_MACHINE = "description";
    public static final String TYPE_MACHINE = "type";
    public static final String PICTURE_MACHINE = "picture";

    //Table BodyTrack
    public static final String TABLE_BODYTRACK = "bodytrack";
    public static final String ID_BODYTRACK = "_id";
    public static final String BODY_DATE = "date";
    public static final String BODY_PART = "bodypart";
    public static final String BODY_MEASURE = "bodymeasure";
    public static final String USER_ID = "user_id";

    /*//Table Reminder
    public static final String TABLE_REMINDER = "Reminder";
    public static final String ID_REMINDER = "_id";
    public static final String AMOUNT_REMINDER = "Amount";
    public static final String CATEGORY_REMINDER = "Category";
    public static final String DATE_REMINDER = "Date";
    public static final String DESCRIPTION_REMINDER = "Description";
    public static final String TIME_REMINDER = "Time";
    public static final String ADDTO_REMINDER = "AddTo";
    public static final String TITLE_REMINDER = "Title";

    //Table Category
    public static final String TABLE_CATEGORY = "Category";
    public static final String ID_CATEGORY = "_id";
    public static final String LIST_CATEGORY = "ListCategory";

    //Table Password
    public static final String TABLE_PASSWORD = "Password";
    public static final String ID_PASSWORD = "_id";
    public static final String SET_PASSWORD = "SetPassword";
    public static final String SECURITY_WORD = "SecurityWord";*/


    //Create Table Machine
    private static final String CREATE_MACHINE = "CREATE TABLE " + TABLE_MACHINE + "(" + ID_MACHINE
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + NAME_MACHINE + " TEXT NOT NULL, " + DESC_MACHINE
            + " TEXT, " + TYPE_MACHINE + " TEXT, " + PICTURE_MACHINE + " TEXT);";

    //Create Table BodyTrack
    public static final String CREATE_BODYTRACK = "CREATE TABLE " + TABLE_BODYTRACK + "(" + ID_BODYTRACK
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + BODY_DATE + " TEXT NOT NULL, " + BODY_PART
            + " TEXT NOT NULL, " + BODY_MEASURE + " REAL NOT NULL, " + USER_ID + " INTEGER NOT NULL);";

    /*//Create Table Reminder
    public static final String CREATE_REMINDER = "CREATE TABLE " + TABLE_REMINDER + "(" + ID_REMINDER
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + AMOUNT_REMINDER + " REAL NOT NULL, " + CATEGORY_REMINDER
            + " TEXT NOT NULL, " + DATE_REMINDER + " TEXT NOT NULL, " + DESCRIPTION_REMINDER + " TEXT NOT NULL, "
            + TIME_REMINDER + " TEXT NOT NULL, " + ADDTO_REMINDER + " TEXT NOT NULL, " + TITLE_REMINDER + " TEXT NOT NULL);";

    //Create Table Category
    public static final String CREATE_CATEGORY = "CREATE TABLE " + TABLE_CATEGORY + "("
            + ID_CATEGORY + " INTEGER PRIMARY KEY AUTOINCREMENT, " + LIST_CATEGORY + " TEXT NOT NULL);";

    //Create Table Password
    public static final String CREATE_PASSWORD = "CREATE TABLE " + TABLE_PASSWORD + "("
            + ID_PASSWORD + " INTEGER PRIMARY KEY AUTOINCREMENT, " + SET_PASSWORD + " TEXT NOT NULL, "
            + SECURITY_WORD + " TEXT NOT NULL);";*/


    //Drop Table Income
    private static final String DROP_MACHINE = "DROP TABLE IF EXISTS " + TABLE_MACHINE;

    //Drop Table BodyTrack
    public static final String DROP_BODYTRACK = "DROP TABLE IF EXISTS " + TABLE_BODYTRACK;

    /*//Drop Table Reminder
    public static final String DROP_REMINDER = "DROP TABLE IF EXISTS " + TABLE_REMINDER;

    //Drop Table Category
    public static final String DROP_CATEGORY = "DROP TABLE IF EXISTS " + TABLE_CATEGORY;

    //Drop Table Password
    public static final String DROP_PASSWORD = "DROP TABLE IF EXISTS " + TABLE_PASSWORD;*/


    public DbHelper (Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db){
        try{
            //Message.message(context, "onCreate called");
            db.execSQL(CREATE_MACHINE);
            db.execSQL(CREATE_BODYTRACK);
            /*db.execSQL(CREATE_REMINDER);
            db.execSQL(CREATE_CATEGORY);
            db.execSQL(CREATE_PASSWORD);*/
            db.execSQL("INSERT INTO " + TABLE_MACHINE + " (" + NAME_MACHINE + ", " + TYPE_MACHINE + ") VALUES" +
                    "('Dumbbell', 'Body Building')," +
                    "('Treadmill', 'Cardio');");
        }catch (SQLException e){
            //Message.message(context, "onCreate not called" + e);
        }
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        try{
            //Message.message(context, "onUpgrade called");
            db.execSQL(DROP_MACHINE);
            db.execSQL(DROP_BODYTRACK);
            /*db.execSQL(DROP_REMINDER);
            db.execSQL(DROP_CATEGORY);
            db.execSQL(DROP_PASSWORD);*/
            onCreate(db);
        }catch (SQLException e){
            //Message.message(context, "onUpgrade not called" + e);
        }
    }
}
