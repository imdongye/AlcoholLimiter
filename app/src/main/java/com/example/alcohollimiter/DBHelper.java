package com.example.alcohollimiter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "myAlcohols.db";
    private static final int DATAVASE_VERSION = 2;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATAVASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("create table liquors ( _id integer primary key autoincrement, name text, abv real, botml integer default 360, janml integer default 50, kcal integer default 404);");
        db.execSQL("create table jan_memorys ( _id integer primary key autoincrement, jantime integer, abv real, janml integer, liquorid integer);");
        db.execSQL("create table alcohol_days ( _id integer primary key autoincrement, starttime integer, endtime integer, startjan integer, endjan integer);");
        firstSetting(db);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists liquors;");
        db.execSQL("drop table if exists jan_memorys;");
        db.execSQL("drop table if exists alcohol_days;");
        onCreate(db);
    }
    public void firstSetting(SQLiteDatabase db){
        Log.i("songjo", "set db first");
        db.execSQL("insert into liquors values(null,'"+"소주"+"', 17.8, 360, 50, 120);");
        db.execSQL("insert into liquors values(null,'"+"맥주"+"', 4.5, 500, 41, 200);");
        db.execSQL("insert into liquors values(null,'"+"막걸리"+"', 5, 750, 46, 360);");
        db.execSQL("insert into liquors values(null,'"+"와인"+"', 12.5, 360, 50, 83);");
        db.execSQL("insert into liquors values(null,'"+"보드카"+"', 40, 360, 50, 90);");
        db.execSQL("insert into liquors values(null,'"+"위스키"+"', 39, 360, 50, 95);");
        db.execSQL("insert into liquors values(null,'"+"소맥1:3"+"', 8, 360, 50, 120);");
    }
}
