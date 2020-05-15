package com.example.myappsqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



public class ConexionBD extends SQLiteOpenHelper {


    String sentenciaCreacionSQL = "CREATE TABLE paciente (id INTEGER primary key, " + "nombre TEXT, padecimiento TEXT)";

    public ConexionBD(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sentenciaCreacionSQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS paciente");
        onCreate(db);
    }
}
