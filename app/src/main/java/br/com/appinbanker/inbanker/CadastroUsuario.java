package br.com.appinbanker.inbanker;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import br.com.appinbanker.inbanker.sqlite.BancoControllerUsuario;
import br.com.appinbanker.inbanker.entidades.Usuario;
import br.com.appinbanker.inbanker.webservice.AddUsuario;

public class CadastroUsuario extends ActionBarActivity {

    Usuario usu;
    EditText et_nome;
    EditText et_email;
    EditText et_cpf;
    EditText et_senha;
    EditText et_senha_novamente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_cadastro_usuario);
        Log.i("Script", "Cadastro Usuario onCreate");

        //ativa o actionbar para dar a possibilidade de apertar em voltar para tela anterior
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //inicializa usuariu
        usu = new Usuario();

        Button btn_cadastro = (Button) findViewById(R.id.btn_cadastrar_usuario);
        btn_cadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Script", "Clicou em cadastrar");

                et_nome = (EditText) findViewById(R.id.et_nome);
                et_email = (EditText) findViewById(R.id.et_email);
                et_cpf = (EditText) findViewById(R.id.et_cpf);
                et_senha = (EditText) findViewById(R.id.et_senha);
                et_senha_novamente = (EditText) findViewById(R.id.et_senha_novamente);

                if(isValid()){

                    if(et_senha.getText().toString().equals(et_senha_novamente.getText().toString())){

                        usu.setCpf(et_cpf.getText().toString());
                        usu.setEmail(et_email.getText().toString());
                        usu.setNome(et_nome.getText().toString());
                        usu.setSenha(et_senha.getText().toString());

                        //setamos esse valores vazio para nao dar problema na hora de serializacao e posteriormente erro no rest de cadastro no banco
                        usu.setIdFace("");
                        usu.setNomeFace("");
                        usu.setUrlImgFace("");

                        //fazemos a chamada a classe responsavel por realizar a tarefa de webservice em doinbackground
                        new AddUsuario(usu,CadastroUsuario.this).execute();
                    }
                }else{
                    mensagemCamposVazio();
                }
            }

        });



    }

    //metodo que sera invoca na classe de webserve
    public void retornoTask(String msg){

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
            mensagem();
        }

    }

    public void mensagem()
    {
        AlertDialog.Builder mensagem = new AlertDialog.Builder(this);
        mensagem.setTitle("Houve um erro!");
        mensagem.setMessage("Olá, parece que houve um problema de conexão. Favor tente novamente!");
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

    public void mensagemSenha()
    {
        AlertDialog.Builder mensagem = new AlertDialog.Builder(this);
        mensagem.setTitle("Houve um erro!");
        mensagem.setMessage("Olá, as senhas digitadas nao sao iguais. Favor tente novamente!");
        mensagem.setNeutralButton("OK",null);
        mensagem.show();
    }

    //metodo para validar se campos do cadastro estao vazios
    public boolean isValid(){

        if(et_cpf.getText().toString().isEmpty() ||
                et_email.getText().toString().isEmpty() ||
                et_nome.getText().toString().isEmpty() ||
                et_senha.getText().toString().isEmpty() ||
                et_senha_novamente.getText().toString().isEmpty()){

            mensagemSenha();

            return false;
        }else{
            return true;
        }

    }
}
