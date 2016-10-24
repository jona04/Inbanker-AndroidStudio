package br.com.appinbanker.inbanker.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by jonatassilva on 20/10/16.
 */

public class CriandoBanco extends SQLiteOpenHelper {

    private static final String NOME_BANCO = "banco.db";

    public static final String TABELA = "usuario";

    //variaveis para a tabela de usuario
    public static final String ID = "_id";
    public static final String NOME = "nome";
    public static final String EMAIL = "email";
    public static final String CPF = "cpf";
    public static final String SENHA = "senha";
    public static final String ID_FACE = "id_face";
    public static final String NOME_FACE = "nome_face";
    public static final String URL_IMG_FACE = "url_img_face";

    //Se a estrutura do banco for alterada, a versao deve ser alterada tambem, para surtir efeito
    private static final int VERSAO = 4;

    public CriandoBanco(Context context){
        super(context, NOME_BANCO,null,VERSAO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE "+TABELA+"("
                + ID + " integer primary key autoincrement,"
                + CPF + " text,"
                + NOME + " text,"
                + EMAIL + " text,"
                + SENHA + " text,"
                + ID_FACE + " text,"
                + NOME_FACE + " text,"
                + URL_IMG_FACE + " text"
                +")";
        db.execSQL(sql);
        Log.i("BancoSQLite","Oncreate banco de dados");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABELA);
        onCreate(db);
    }
}
