package br.com.appinbanker.inbanker;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import br.com.appinbanker.inbanker.sqlite.BancoControllerUsuario;
import br.com.appinbanker.inbanker.entidades.Usuario;
import br.com.appinbanker.inbanker.util.AllSharedPreferences;
import br.com.appinbanker.inbanker.webservice.AtualizaTokenGcm;
import br.com.appinbanker.inbanker.webservice.BuscaUsuarioLogin;

public class TelaLogin extends ActionBarActivity {

    private EditText et_email;
    private EditText et_senha;
    private Button btn_entrar;

    private ProgressBar pg_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_tela_login);

        //ativa o actionbar para dar a possibilidade de apertar em voltar para tela anterior
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        et_email = (EditText) findViewById(R.id.et_email);
        et_senha = (EditText) findViewById(R.id.et_senha);
        btn_entrar = (Button) findViewById(R.id.btn_entrar);

        pg_login = (ProgressBar) findViewById(R.id.pg_login);

        btn_entrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(et_email.getText().toString().equals("") || et_senha.getText().toString().equals(""))
                {
                    mensagem("Houve um erro!","Olá, existem campos não preenchidos. Favor preencha todos e tente novamente!","Ok");
                }else{

                    pg_login.setVisibility(View.VISIBLE);
                    btn_entrar.setEnabled(false);
                    new BuscaUsuarioLogin(et_email.getText().toString(),TelaLogin.this,TelaLogin.this).execute();

                }

            }
        });


    }

    public void retornoTask(Usuario usu){

        if(usu != null){
            if(usu.getSenha().equals(et_senha.getText().toString())) {

                String device_id = AllSharedPreferences.getPreferences(AllSharedPreferences.DEVICE_ID,TelaLogin.this);
                String token = AllSharedPreferences.getPreferences(AllSharedPreferences.TOKEN_GCM,TelaLogin.this);


                if(usu.getToken_gcm()!=null){
                    if(!usu.getToken_gcm().equals(token)){

                        usu.setDevice_id(device_id);
                        usu.setToken_gcm(token);

                        //atualizamos do token
                        new AtualizaTokenGcm(usu).execute();
                    }
                }

                BancoControllerUsuario crud = new BancoControllerUsuario(getBaseContext());
                //ordem de parametros - nome,email,cpf,senha,id_face,email_face,nome_face,url_img_face
                String resultado = crud.insereDado(usu.getNome(),usu.getEmail(),usu.getCpf(),usu.getSenha(),usu.getIdFace(),usu.getNomeFace(),usu.getUrlImgFace());
                Log.i("Banco SQLITE","resultado = "+resultado);

                Intent it = new Intent(TelaLogin.this, NavigationDrawerActivity.class);
                startActivity(it);

                //para encerrar a activity atual e todos os parent
                finishAffinity();
            }else{
                mensagem("Houve um erro!","Olá, a senha digita está incorreta. Favor tente novamente!","oK");


                btn_entrar.setEnabled(true);
                pg_login.setVisibility(View.GONE);
            }
        }else{
            mensagem("Houve um erro!","Olá, parece que houve um problema no login. Favor tente novamente!","oK");

            btn_entrar.setEnabled(true);
            pg_login.setVisibility(View.GONE);
        }

    }

    public void mensagem(String titulo,String corpo,String botao)
    {
        AlertDialog.Builder mensagem = new AlertDialog.Builder(this);
        mensagem.setTitle(titulo);
        mensagem.setMessage(corpo);
        mensagem.setNeutralButton(botao,null);
        mensagem.show();
    }
}
