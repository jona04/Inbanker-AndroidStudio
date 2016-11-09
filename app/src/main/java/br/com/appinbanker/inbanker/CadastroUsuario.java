package br.com.appinbanker.inbanker;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import br.com.appinbanker.inbanker.sqlite.BancoControllerUsuario;
import br.com.appinbanker.inbanker.entidades.Usuario;
import br.com.appinbanker.inbanker.util.AllSharedPreferences;
import br.com.appinbanker.inbanker.util.CheckConection;
import br.com.appinbanker.inbanker.util.Validador;
import br.com.appinbanker.inbanker.webservice.AddUsuario;
import br.com.appinbanker.inbanker.webservice.VerificaUsuarioCadastro;

public class CadastroUsuario extends AppCompatActivity {

    Usuario usu;
    EditText et_nome;
    EditText et_email;
    EditText et_cpf;
    EditText et_senha;
    EditText et_senha_novamente;
    Button btn_cadastro;
    FloatingActionButton fab_cpf;

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

        fab_cpf = (FloatingActionButton) findViewById(R.id.fab_cpf);
        btn_cadastro = (Button) findViewById(R.id.btn_cadastrar_usuario);

        fab_cpf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mensagem("Por que precisam do meu CPF?","Seu CPF é necessario para atendermos as normas Brasileiras. Fique tranquilo, seus dados estão protegidos e ninguém tem acesso a eles." ,"OK");
            }
        });

        btn_cadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Script", "Clicou em cadastrar");

                if(!CheckConection.temConexao(CadastroUsuario.this)){
                    mensagem("Sem conexao!","Olá, para realizar o cadastro você precisa estar conectado em alguma rede.","Ok");
                }else {

                    boolean campos_ok = true;

                    boolean nome_valido = Validador.validateNotNull(et_nome.getText().toString());
                    if(!nome_valido) {
                        et_nome.setError("Campo vazio");
                        et_nome.setFocusable(true);
                        et_nome.requestFocus();

                        campos_ok = false;
                    }

                    boolean cpf_valido = Validador.isCPF(et_cpf.getText().toString());
                    if(!cpf_valido) {
                        et_cpf.setError("CPF inválido");
                        et_cpf.setFocusable(true);
                        et_cpf.requestFocus();

                        campos_ok = false;
                    }

                    boolean email_valido = Validador.validateEmail(et_email.getText().toString());
                    if(!email_valido){
                        et_email.setError("Email inválido");
                        et_email.setFocusable(true);
                        et_email.requestFocus();

                        campos_ok = false;
                    }

                    boolean valida_senha = Validador.validateNotNull(et_senha.getText().toString());
                    if(!valida_senha){
                        et_senha.setError("Campo Vazio");
                        et_senha.setFocusable(true);
                        et_senha.requestFocus();

                        campos_ok = false;
                    }

                    boolean valida_confirm_senha = Validador.validateNotNull(et_senha_novamente.getText().toString());
                    if(!valida_confirm_senha){
                        et_senha_novamente.setError("Campo Vazio");
                        et_senha_novamente.setFocusable(true);
                        et_senha_novamente.requestFocus();

                        campos_ok = false;
                    }

                    if (et_senha.getText().toString().equals(et_senha_novamente.getText().toString())) {

                        if(campos_ok) {

                            btn_cadastro.setEnabled(false);
                            progress_bar_cadastro.setVisibility(View.VISIBLE);

                            new VerificaUsuarioCadastro(et_email.getText().toString(), et_cpf.getText().toString(), CadastroUsuario.this, CadastroUsuario.this).execute();
                        }

                    } else {

                        et_senha_novamente.setError("Senha diferente");
                        et_senha_novamente.setFocusable(true);
                        et_senha_novamente.requestFocus();

                        campos_ok = false;
                    }

                }

            }

        });



    }

    public void retornoTaskVerifica(String result){

        if(result == null){

            String device_id = AllSharedPreferences.getPreferences(AllSharedPreferences.DEVICE_ID,CadastroUsuario.this);
            String token = AllSharedPreferences.getPreferences(AllSharedPreferences.TOKEN_GCM,CadastroUsuario.this);

            usu.setToken_gcm(token);
            usu.setDevice_id(device_id);
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

}
