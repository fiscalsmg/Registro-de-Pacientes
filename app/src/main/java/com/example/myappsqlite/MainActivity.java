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

    EditText edt_id, edt_nombreP, edt_padecimiento;
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
        edt_nombreP = findViewById(R.id.txt_nombreP);
        edt_padecimiento = findViewById(R.id.txt_padecimiento);

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
            admin = new ConexionBD(this, "pacienteBD", null, 1);
            BaseDeDatos = admin.getWritableDatabase();//abre bd modo leectura y escritura

            String id = edt_id.getText().toString();
            String nombreP = edt_nombreP.getText().toString();
            String padecimientoP = edt_padecimiento.getText().toString();


            //valida campos
            if (!id.isEmpty() && !nombreP.isEmpty() && !padecimientoP.isEmpty()) {
                if (BaseDeDatos != null) {
                    BaseDeDatos.execSQL("INSERT INTO paciente values(" + id + ",'" + nombreP + "','" + padecimientoP + "')");
                    BaseDeDatos.close();
                    edt_id.setText("");
                    edt_nombreP.setText("");
                    edt_padecimiento.setText("");

                    Toast.makeText(this, "Paciente Registrado!!", Toast.LENGTH_SHORT).show();
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
        admin = new ConexionBD(this, "pacienteBD", null, 1);
        BaseDeDatos = admin.getWritableDatabase();//abre bd modo leectura y escritura

        String idPaciente=edt_id.getText().toString();

        if(!idPaciente.isEmpty()){

            Cursor mueve=BaseDeDatos.rawQuery("select nombre,padecimiento from paciente where id="+idPaciente, null); //variable de seleccion

            if(mueve.moveToFirst()){
                //verifica la consulta aqui decimos donde mostrara el resultado

                edt_nombreP.setText(mueve.getString(0));
                edt_padecimiento.setText(mueve.getString(1));
                //activa los botones para poder actualizar o eliminar
                btn_update.setEnabled(true);
                btn_delete.setEnabled(true);
                BaseDeDatos.close();
                edt_id.setEnabled(false);

            }else{
                Toast.makeText(this,"No existe el paciente", Toast.LENGTH_SHORT).show();
                BaseDeDatos.close();
                edt_id.setEnabled(true);

            }
        }else{
            Toast.makeText(this,"Ingresa ID", Toast.LENGTH_SHORT).show();
        }

    }

    private void actualizar() {
        admin = new ConexionBD(this, "pacienteBD", null, 1);
        BaseDeDatos = admin.getWritableDatabase();//abre bd modo leectura y escritura

        String id=edt_id.getText().toString();
        String nombreP=edt_nombreP.getText().toString();
        String padecimiento=edt_padecimiento.getText().toString();


        if(!id.isEmpty() && !nombreP.isEmpty() && !padecimiento.isEmpty()){

            ContentValues registro=new  ContentValues();
            registro.put("id",id);
            registro.put("nombre",nombreP);
            registro.put("padecimiento",padecimiento);


            int total=BaseDeDatos.update("paciente",registro,"id="+id,null);//retorna un entero la cantidad de registros modificados
            BaseDeDatos.close();

            //edt_nombreP.setText("");
            //edt_padecimiento.setText("");


            if(total==1){
                Toast.makeText(this,"Datos del paciente modificados", Toast.LENGTH_SHORT).show();
                edt_id.setEnabled(false);


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
        admin = new ConexionBD(this, "pacienteBD", null, 1);
        BaseDeDatos = admin.getWritableDatabase();//abre bd modo leectura y escritura
        edt_id.setEnabled(true);

        String id =edt_id.getText().toString();

        if(!id.isEmpty()){//es diferente a vacio

            int cantidad=BaseDeDatos.delete("paciente","id=" + id , null);//delete retorna un entero
            BaseDeDatos.close();

            edt_id.setText("");
            edt_nombreP.setText("");
            edt_padecimiento.setText("");

            if(cantidad==1){

                Toast.makeText(this,"Paciente eliminado", Toast.LENGTH_SHORT).show();
            }else{

                Toast.makeText(this,"El paciente no existe", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this,"Ingresa ID del paciente a eliminar", Toast.LENGTH_SHORT).show();

        }

        if(BaseDeDatos!=null){
            btn_update.setEnabled(false);
            btn_delete.setEnabled(false);
        }
    }

    //limpia los campos de registro
    private void limpiar() {
        edt_id.setText("");
        edt_nombreP.setText("");
        edt_padecimiento.setText("");
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
