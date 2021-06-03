package com.example.alcohollimiter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
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
        addLiquor(db,"소주", 17.5, 360, 50, 370);
        addLiquor(db,"맥주", 4.5, 500, 180, 200);
        addLiquor(db,"막걸리", 5, 720, 200, 210);
        addLiquor(db,"와인", 12.5, 720, 180, 83);
        addLiquor(db,"보드카", 40, 500, 50, 90);
        addLiquor(db,"위스키", 39, 700, 50, 95);
        addLiquor(db,"고량주", 50, 250, 50, 400);
        addLiquor(db,"1대3 소맥", 17.8, 360, 180, 200);
    }
    public void addLiquor(SQLiteDatabase db, String name, double abv, int botml, int janml, int kcal) {
        db.execSQL(String.format("insert into liquors values(null,'%s', %f, %d, %d, %d);",name, abv, botml, janml, kcal));
    }
    public void updateLiquor(SQLiteDatabase db, RealtimeFragment.LiquorType lt){
        ContentValues values = new ContentValues();
        values.put("abv", (float)lt.abv);
        values.put("botml", lt.bottleMl);
        values.put("janml", lt.janMl);
        db.update("liquors", values, "_id="+lt.id, null);
    }
    public void addJanMemory(SQLiteDatabase db, int jantime, double abv, int janml, int liquorid) {
        db.execSQL(String.format("insert into liquors values(null, %d, %f, %d, %d);",jantime, abv, janml, liquorid));
    }
    public void addAhcoholDay(SQLiteDatabase db, int starttime, int endtime, int startjanid, int endjanid) {
        db.execSQL(String.format("insert into liquors values(null, %d, %d, %d, %d);",starttime, endtime, startjanid, endjanid));
    }
}
