package com.example.myappsqlite;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;

public class ConsultaGeneral extends AppCompatActivity {

    SQLiteDatabase BaseDeDatos = null;
    ConexionBD admin;
    TextView tv3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consulta_general);
        tv3=findViewById(R.id.textView3);
        muestraRegistrosDB();
    }

    public void muestraRegistrosDB(){
        admin = new ConexionBD(this, "pacienteBD", null, 1);
        BaseDeDatos = admin.getWritableDatabase();//abre bd modo leectura y escritura

        Cursor cursor = BaseDeDatos.rawQuery("SELECT * FROM paciente", null);
        int numcol = cursor.getColumnCount();
        int numren = cursor.getCount();

        tv3.append("Registros de los pacientes y sus enfermedades\nNúmero de pacientes registrados " + numren + " registros\n\n\n"+"Registros al dia de hoy\n\n");
        while (cursor.moveToNext()) {
            tv3.append("\n" + cursor.getInt(0) + "   " + cursor.getString(1) + "   " + cursor.getString(2));
        }//while
        cursor.close();
        BaseDeDatos.close();

    }

}
