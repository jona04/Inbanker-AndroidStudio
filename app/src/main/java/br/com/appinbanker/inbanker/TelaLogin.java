package br.com.appinbanker.inbanker;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import br.com.appinbanker.inbanker.entidades.BancoControllerUsuario;
import br.com.appinbanker.inbanker.entidades.Usuario;
import br.com.appinbanker.inbanker.webservice.BuscaUsuarioLogin;

public class TelaLogin extends ActionBarActivity {

    private EditText et_email;
    private EditText et_senha;
    private Button btn_entrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_tela_login);

        //ativa o actionbar para dar a possibilidade de apertar em voltar para tela anterior
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        et_email = (EditText) findViewById(R.id.et_email);
        et_senha = (EditText) findViewById(R.id.et_senha);
        btn_entrar = (Button) findViewById(R.id.btn_entrar);

        btn_entrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(et_email.getText().toString().equals("") || et_senha.getText().toString().equals(""))
                {
                    mensagemCamposVazio();
                }else{
                    new BuscaUsuarioLogin(et_email.getText().toString(),TelaLogin.this,TelaLogin.this).execute();
                }

            }
        });


    }

    public void retornoTask(Usuario usu){

        if(usu != null){
            if(usu.getSenha().equals(et_senha.getText().toString())) {

                BancoControllerUsuario crud = new BancoControllerUsuario(getBaseContext());
                //ordem de parametros - nome,email,cpf,senha,id_face,email_face,nome_face,url_img_face
                String resultado = crud.insereDado(usu.getNome(),usu.getEmail(),usu.getCpf(),usu.getSenha(),usu.getIdFace(),usu.getNomeFace(),usu.getUrlImgFace());
                Log.i("Banco SQLITE","resultado = "+resultado);

                Intent it = new Intent(TelaLogin.this, NavigationDrawerActivity.class);
                startActivity(it);

                //para encerrar a activity atual e todos os parent
                finishAffinity();
            }else{
                mensagemSenha();
            }
        }else{
            mensagem();
        }

    }

    public void mensagem()
    {
        AlertDialog.Builder mensagem = new AlertDialog.Builder(this);
        mensagem.setTitle("Houve um erro!");
        mensagem.setMessage("Olá, parece que houve um problema no login. Favor tente novamente!");
        mensagem.setNeutralButton("OK",null);
        mensagem.show();
    }
    public void mensagemSenha()
    {
        AlertDialog.Builder mensagem = new AlertDialog.Builder(this);
        mensagem.setTitle("Houve um erro!");
        mensagem.setMessage("Olá, a senha digita esta incorreta. Favor tente novamente!");
        mensagem.setNeutralButton("OK",null);
        mensagem.show();
    }

    public void mensagemCamposVazio()
    {
        AlertDialog.Builder mensagem = new AlertDialog.Builder(this);
        mensagem.setTitle("Houve um erro!");
        mensagem.setMessage("Olá, existem campos nao preenchidos. Favor preencha todos e tente novamente!");
        mensagem.setNeutralButton("OK",null);
        mensagem.show();
    }
}
