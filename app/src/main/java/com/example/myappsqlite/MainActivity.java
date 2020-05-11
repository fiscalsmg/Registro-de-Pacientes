package com.example.myappsqlite;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    EditText edt_id, edt_raza, edt_color;
    Button btn_create, btn_read, btn_update, btn_delete;
    SQLiteDatabase BaseDeDatos = null;
    ConexionBD admin;
    TextView tv1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv1 = (TextView) findViewById(R.id.textView);


        edt_id = findViewById(R.id.txt_id);
        edt_raza = findViewById(R.id.txt_raza);
        edt_color = findViewById(R.id.txt_color);

        btn_create = findViewById(R.id.btn_create);
        btn_read = findViewById(R.id.btn_read);
        btn_update = findViewById(R.id.btn_update);
        btn_delete = findViewById(R.id.btn_delete);

        btn_create.setOnClickListener(this);
        btn_read.setOnClickListener(this);
        btn_update.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_create:
                registrar();
                break;
            case R.id.btn_read:
                buscar();
                break;
            case R.id.btn_update:
                actualizar();
                break;
            case R.id.btn_delete:
                break;

        }
    }


    private void registrar() {
        try {
            admin = new ConexionBD(this, "tortugaBD", null, 1);
            BaseDeDatos = admin.getWritableDatabase();//abre bd modo leectura y escritura

            String id = edt_id.getText().toString();
            String raza = edt_raza.getText().toString();
            String color = edt_color.getText().toString();

            //valida campos
            if (!id.isEmpty() && !raza.isEmpty() && !color.isEmpty()) {
                if (BaseDeDatos != null) {
                    BaseDeDatos.execSQL("INSERT INTO tortuga values(" + id + ",'" + raza + "','" + color + "')");
                    BaseDeDatos.close();
                    edt_id.setText("");
                    edt_raza.setText("");
                    edt_color.setText("");

                    Toast.makeText(this, "Registro Exitoso!!", Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(this, "Registra todos los datos", Toast.LENGTH_SHORT).show();

            }
        } catch (Exception e) {
            Toast.makeText(this, "Ups!"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    private void buscar() {
        admin = new ConexionBD(this, "tortugaBD", null, 1);
        BaseDeDatos = admin.getWritableDatabase();//abre bd modo leectura y escritura

        String idtortu=edt_id.getText().toString();

        if(!idtortu.isEmpty()){

            Cursor fila=BaseDeDatos.rawQuery("select raza,color from tortuga where id="+idtortu, null); //variable de seleccion

            if(fila.moveToFirst()){
                //verifica la consulta aqui decimos donde mostrara el resultado

                edt_raza.setText(fila.getString(0));
                edt_color.setText(fila.getString(1));

                BaseDeDatos.close();

            }else{
                Toast.makeText(this,"No existe la tortuga", Toast.LENGTH_SHORT).show();
                BaseDeDatos.close();

            }
        }else{
            Toast.makeText(this,"Ingresa ID", Toast.LENGTH_SHORT).show();
        }

    }

    private void actualizar() {
        admin = new ConexionBD(this, "tortugaBD", null, 1);
        BaseDeDatos = admin.getWritableDatabase();//abre bd modo leectura y escritura


        String id=edt_id.getText().toString();
        String raza=edt_raza.getText().toString();
        String color=edt_color.getText().toString();

        if(!id.isEmpty() && !raza.isEmpty() && !color.isEmpty()){

            ContentValues registro=new  ContentValues();
            registro.put("id",id);
            registro.put("raza",raza);
            registro.put("color",color);

            int cantidad=BaseDeDatos.update("tortuga",registro,"id="+id,null);//retorna un entero la cantidad de registros modificados
            BaseDeDatos.close();
            edt_id.setText("");
            edt_raza.setText("");
            edt_color.setText("");

            if(cantidad==1){
                Toast.makeText(this,"Datos de la tortuga modificados", Toast.LENGTH_SHORT).show();

            }else{
                Toast.makeText(this,"la tortuga no exicte", Toast.LENGTH_SHORT).show();

            }
        }else{
            Toast.makeText(this,"Llena todos los datos!", Toast.LENGTH_SHORT).show();
        }

    }

}
