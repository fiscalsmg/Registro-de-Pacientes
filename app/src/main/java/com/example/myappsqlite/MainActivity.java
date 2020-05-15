package com.example.myappsqlite;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    EditText edt_id, edt_raza, edt_color;
    Button btn_create, btn_read, btn_update, btn_delete, btn_lector, btn_borraDatos;
    SQLiteDatabase BaseDeDatos = null;
    ConexionBD admin;
    TextView tv1;
    Intent inte1;

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
        btn_lector = findViewById(R.id.btn_lector);
        btn_borraDatos = findViewById(R.id.btn_borraDatos);


        btn_create.setOnClickListener(this);
        btn_read.setOnClickListener(this);
        btn_update.setOnClickListener(this);
        btn_delete.setOnClickListener(this);
        btn_lector.setOnClickListener(this);
        btn_borraDatos.setOnClickListener(this);



        btn_update.setEnabled(false);
        btn_delete.setEnabled(false);

        btn_read.setOnLongClickListener(this);//click largo manda a otra vista

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
                borrar();
                break;
            case R.id.btn_lector:
                leerCodigos();
                break;
            case R.id.btn_borraDatos:
                limpiar();
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (v.getId()==R.id.btn_read){
            inte1 = new Intent(this,ConsultaGeneral.class);
            startActivity(inte1);
        }
        return true;
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
        } catch (SQLiteConstraintException e) {
            Toast.makeText(this, "Ups! verifica id, \nid ya registrado!! ", Toast.LENGTH_SHORT).show();

        }catch (Exception e){
            Toast.makeText(this, "error!"+e.getMessage(), Toast.LENGTH_SHORT).show();
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
                //activa los botones para poder actualizar o eliminar
                btn_update.setEnabled(true);
                btn_delete.setEnabled(true);
                BaseDeDatos.close();
                edt_id.setEnabled(false);

            }else{
                Toast.makeText(this,"No existe la tortuga", Toast.LENGTH_SHORT).show();
                BaseDeDatos.close();
                edt_id.setEnabled(true);

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

            edt_raza.setText("");
            edt_color.setText("");


            if(cantidad==1){
                Toast.makeText(this,"Datos de la tortuga modificados", Toast.LENGTH_SHORT).show();
                edt_id.setEnabled(true);


            }else{
                Toast.makeText(this,"No se puede Actualizar el id,\nColoca otro id", Toast.LENGTH_SHORT).show();

            }
        }else{
            Toast.makeText(this,"Llena todos los datos!", Toast.LENGTH_SHORT).show();
        }
        if(BaseDeDatos!=null){
            btn_update.setEnabled(false);
            btn_delete.setEnabled(false);

        }

    }

    private void borrar() {
        admin = new ConexionBD(this, "tortugaBD", null, 1);
        BaseDeDatos = admin.getWritableDatabase();//abre bd modo leectura y escritura
        edt_id.setEnabled(true);

        String codigo =edt_id.getText().toString();

        if(!codigo.isEmpty()){//es diferente a vacio

            int cantidad=BaseDeDatos.delete("tortuga","id=" + codigo , null);//delete retorna un entero
            BaseDeDatos.close();

            edt_id.setText("");
            edt_raza.setText("");
            edt_color.setText("");

            if(cantidad==1){

                Toast.makeText(this,"Tortuga Eliminada", Toast.LENGTH_SHORT).show();
            }else{

                Toast.makeText(this,"Tortuga no existe", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this,"Ingresa ID de la tortuga a eliminar", Toast.LENGTH_SHORT).show();

        }

        if(BaseDeDatos!=null){
            btn_update.setEnabled(false);
            btn_delete.setEnabled(false);
        }
    }

    //limpia los campos de registro
    private void limpiar() {
        edt_id.setText("");
        edt_raza.setText("");
        edt_color.setText("");
        edt_id.setEnabled(true);

        btn_update.setEnabled(false);
        btn_delete.setEnabled(false);
    }




    //lector de codigos
    private void leerCodigos() {
        //Se instancia un objeto de la clase IntentIntegrator
        IntentIntegrator scanIntegrator = new IntentIntegrator(this);
        //Se procede con el proceso de scaneo
        scanIntegrator.initiateScan();
    }

    //metodo copiado de tibu
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        //Se obtiene el resultado del proceso de scaneo y se parsea
        super.onActivityResult(requestCode, resultCode, intent);
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            //Quiere decir que se obtuvo resultado pro lo tanto:
            //Desplegamos en pantalla el contenido del código de barra scaneado

            String scanContent = scanningResult.getContents();
            String id = edt_id.getText().toString();

            edt_id.getText().append("Contenido:" + scanContent);

        } else {
            //Quiere decir que NO se obtuvo resultado
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No se ha recibido datos del scaneo!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

}
