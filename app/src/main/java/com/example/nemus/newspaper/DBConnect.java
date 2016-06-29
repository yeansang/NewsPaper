package com.example.nemus.fakechat;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Vector;

public class DBConnect extends SQLiteOpenHelper{
    public DBConnect(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE CHAT( id INTEGER PRIMARY KEY AUTOINCREMENT, word TEXT, pos INTEGER);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void dropTable(){

    }

    public boolean input(String in, int pos){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("INSERT INTO CHAT (word, pos) VALUES (\""+in+"\", "+pos+");");
        db.close();
        return true;
    }

    public boolean inputAll(ArrayList<String> in){
        SQLiteDatabase db = getWritableDatabase();
        for(int i=0;i<in.size();i++) {
            db.execSQL("INSERT INTO CHAT (word, pos) VALUES (\""+in.get(i)+"\","+i+");");
        }
        db.close();
        return true;
    }

    public Vector<String> getAll(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM CHAT",null);
        Vector<String> out = new Vector<String>();

        if(cursor.isAfterLast()){
            return null;
        }

        while(cursor.moveToNext()){
            String str = cursor.getString(1);
            out.add(str);
        }
        cursor.close();
        return out;
    }

    public boolean remove(int pos){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM CHAT WHERE pos like "+pos+";");
        db.execSQL("UPDATE CHAT SET pos=pos-1 WHERE pos>"+pos+";");
        db.close();
        return true;
    }

    public boolean removeAll(){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM CHAT;");
        db.close();
        return true;
    }

}
