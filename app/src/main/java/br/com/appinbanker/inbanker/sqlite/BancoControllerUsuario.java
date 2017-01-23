package br.com.appinbanker.inbanker.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import br.com.appinbanker.inbanker.sqlite.CriandoBanco;

/**
 * Created by jonatassilva on 20/10/16.
 */

public class BancoControllerUsuario {

    private SQLiteDatabase db;
    private CriandoBanco banco;

    public BancoControllerUsuario(Context context){
        banco = new CriandoBanco(context);
    }

    public String insereDado(String nome, String email, String cpf,String senha,String id_face,String url_img_face,String token_gcm ,String device_id){
        ContentValues valores;
        long resultado;

        db = banco.getWritableDatabase();
        valores = new ContentValues();
        valores.put(CriandoBanco.NOME, nome);
        valores.put(CriandoBanco.EMAIL, email);
        valores.put(CriandoBanco.CPF, cpf);
        valores.put(CriandoBanco.SENHA, senha);
        valores.put(CriandoBanco.ID_FACE, id_face);
        valores.put(CriandoBanco.URL_IMG_FACE, url_img_face);
        valores.put(CriandoBanco.TOKEN_FCM, token_gcm);
        valores.put(CriandoBanco.DEVICE_ID, device_id);

        resultado = db.insert(CriandoBanco.TABELA, null, valores);
        db.close();

        if (resultado ==-1)
            return "Erro ao inserir registro";
        else
            return "Registro Inserido com sucesso";

    }

    public Cursor carregaDados(){
        Cursor cursor;
        String[] campos =  {banco.NOME,banco.CPF,banco.SENHA,banco.ID_FACE,banco.URL_IMG_FACE,banco.EMAIL,banco.TOKEN_FCM,banco.DEVICE_ID};
        db = banco.getReadableDatabase();
        cursor = db.query(banco.TABELA, campos, null, null, null, null, null, null);
        //cursor = db.rawQuery("select * from "+banco.TABELA,null);

        if(cursor!=null){
            cursor.moveToFirst();
        }
        db.close();
        return cursor;
    }

    public Cursor carregaDadoById(String cpf){
        Cursor cursor;
        String[] campos =  {banco.NOME,banco.CPF,banco.SENHA,banco.ID_FACE,banco.URL_IMG_FACE};
        String where = CriandoBanco.CPF + "=" + cpf;
        db = banco.getReadableDatabase();
        cursor = db.query(CriandoBanco.TABELA,campos,where, null, null, null, null, null);

        if(cursor!=null){
            cursor.moveToFirst();
        }
        db.close();
        return cursor;
    }

    public void alteraRegistroFace(String cpf, String id_face, String nome, String url_img_face,String email,String senha){
        ContentValues valores;
        String where;

        db = banco.getWritableDatabase();

        where = CriandoBanco.CPF + "= '" + cpf +"'";

        valores = new ContentValues();
        valores.put(CriandoBanco.ID_FACE, id_face);
        valores.put(CriandoBanco.NOME, nome);
        valores.put(CriandoBanco.URL_IMG_FACE, url_img_face);
        valores.put(CriandoBanco.EMAIL, email);
        valores.put(CriandoBanco.SENHA, senha);

        db.update(CriandoBanco.TABELA,valores,where,null);
        db.close();
    }
    public void alteraRegistroCpf(String id_face,String cpf,String senha,String email,String gcm_token,String device_id,String nome){
        ContentValues valores;
        String where;

        db = banco.getWritableDatabase();

        where = CriandoBanco.ID_FACE + "= '" + id_face +"'";

        valores = new ContentValues();
        valores.put(CriandoBanco.CPF, cpf);
        valores.put(CriandoBanco.SENHA, senha);
        valores.put(CriandoBanco.EMAIL, email);
        valores.put(CriandoBanco.TOKEN_FCM, gcm_token);
        valores.put(CriandoBanco.DEVICE_ID, device_id);
        valores.put(CriandoBanco.NOME, nome);

        db.update(CriandoBanco.TABELA,valores,where,null);
        db.close();
    }

    public void deletaRegistro(String cpf){
        String where = CriandoBanco.CPF + "= '" + cpf +"'";
        db = banco.getReadableDatabase();
        db.delete(CriandoBanco.TABELA,where,null);
        db.close();
    }
}
