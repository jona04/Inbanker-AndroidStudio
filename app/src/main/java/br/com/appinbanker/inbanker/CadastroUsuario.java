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
import br.com.appinbanker.inbanker.webservice.AddUsuario;
import br.com.appinbanker.inbanker.webservice.VerificaUsuarioCadastro;

public class CadastroUsuario extends ActionBarActivity {

    Usuario usu;
    EditText et_nome;
    EditText et_email;
    EditText et_cpf;
    EditText et_senha;
    EditText et_senha_novamente;
    Button btn_cadastro;

    ProgressBar progress_bar_cadastro;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_cadastro_usuario);
        Log.i("Script", "Cadastro Usuario onCreate");

        //ativa o actionbar para dar a possibilidade de apertar em voltar para tela anterior
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //inicializa usuariu
        usu = new Usuario();

        progress_bar_cadastro = (ProgressBar) findViewById(R.id.progress_bar_cadastro);
        et_nome = (EditText) findViewById(R.id.et_nome);
        et_email = (EditText) findViewById(R.id.et_email);
        et_cpf = (EditText) findViewById(R.id.et_cpf);
        et_senha = (EditText) findViewById(R.id.et_senha);
        et_senha_novamente = (EditText) findViewById(R.id.et_senha_novamente);

        btn_cadastro = (Button) findViewById(R.id.btn_cadastrar_usuario);
        btn_cadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Script", "Clicou em cadastrar");

                if(isValid()){

                    if(et_senha.getText().toString().equals(et_senha_novamente.getText().toString())){

                        btn_cadastro.setEnabled(false);
                        progress_bar_cadastro.setVisibility(View.GONE);

                        new VerificaUsuarioCadastro(et_email.getText().toString(),et_cpf.getText().toString(),CadastroUsuario.this,CadastroUsuario.this).execute();

                    }else{
                        mensagem("Houve um erro!","Olá, as senhas digitadas não são iguais. Favor tente novamente!","Ok");
                    }
                }else{
                    mensagem("Houve um erro!","Olá, existem campos não preenchidos. Favor preencha-os e tente novamente!","OK");
                }
            }

        });



    }

    public void retornoTaskVerifica(String result){

        if(result == null){
            usu.setCpf(et_cpf.getText().toString());
            usu.setEmail(et_email.getText().toString());
            usu.setNome(et_nome.getText().toString());
            usu.setSenha(et_senha.getText().toString());

            //setamos esse valores vazio para nao dar problema na hora de serializacao e posteriormente erro no rest de cadastro no banco
            usu.setIdFace("");
            usu.setNomeFace("");
            usu.setUrlImgFace("");

            //fazemos a chamada a classe responsavel por realizar a tarefa de webservice em doinbackground
            new AddUsuario(usu, CadastroUsuario.this).execute();
        }else {

            btn_cadastro.setEnabled(true);
            progress_bar_cadastro.setVisibility(View.GONE);

            //verificamos o resultado da verificação e continuamos o cadastro
            if (result.equals("email"))
                mensagem("Houve um erro!", "Olá, o EMAIL informado já existe, se você esqueceu sua senha tente recupará-la na sessão anterior.", "Ok");
            else if (result.equals("cpf"))
                mensagem("Houve um erro!", "Olá, o CPF informado já existe, por favor informe outro, ou tente recuperar sua senha", "Ok");
        }
    }

    //metodo que sera invoca na classe de webserve
    public void retornoTaskAdd(String msg){

        btn_cadastro.setEnabled(true);
        progress_bar_cadastro.setVisibility(View.GONE);

        if(msg.equals("sucesso")){

            BancoControllerUsuario crud = new BancoControllerUsuario(getBaseContext());
            //ordem de parametros - nome,email,cpf,senha,id_face,email_face,nome_face,url_img_face
            String resultado = crud.insereDado(usu.getNome(),usu.getEmail(),usu.getCpf(),usu.getSenha(),"","","");
            Log.i("Banco SQLITE","resultado = "+resultado);

            Intent it = new Intent(CadastroUsuario.this, NavigationDrawerActivity.class);
            startActivity(it);

            //para encerrar a activity atual e todos os parent
            finishAffinity();
        }else{
            mensagem("Houve um erro!","Olá, parece que houve um problema de conexão. Favor tente novamente!","Ok");
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

    //metodo para validar se campos do cadastro estao vazios
    public boolean isValid(){

        if(et_cpf.getText().toString().isEmpty() ||
                et_email.getText().toString().isEmpty() ||
                et_nome.getText().toString().isEmpty() ||
                et_senha.getText().toString().isEmpty() ||
                et_senha_novamente.getText().toString().isEmpty()){

            return false;
        }else{
            return true;
        }

    }
}
