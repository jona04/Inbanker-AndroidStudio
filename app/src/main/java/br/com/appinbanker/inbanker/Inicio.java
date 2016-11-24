package br.com.appinbanker.inbanker;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import br.com.appinbanker.inbanker.entidades.Amigos;
import br.com.appinbanker.inbanker.entidades.Usuario;
import br.com.appinbanker.inbanker.gcm.RegistrationIntentService;
import br.com.appinbanker.inbanker.interfaces.WebServiceReturnString;
import br.com.appinbanker.inbanker.interfaces.WebServiceReturnUsuario;
import br.com.appinbanker.inbanker.sqlite.BancoControllerUsuario;
import br.com.appinbanker.inbanker.sqlite.CriandoBanco;
import br.com.appinbanker.inbanker.util.AllSharedPreferences;
import br.com.appinbanker.inbanker.util.CheckConection;
import br.com.appinbanker.inbanker.util.CheckPlayServices;
import br.com.appinbanker.inbanker.util.Validador;
import br.com.appinbanker.inbanker.webservice.AddUsuario;
import br.com.appinbanker.inbanker.webservice.AtualizaTokenGcm;
import br.com.appinbanker.inbanker.webservice.BuscaUsuarioFace;
import br.com.appinbanker.inbanker.webservice.BuscaUsuarioLogin;
import br.com.appinbanker.inbanker.webservice.VerificaUsuarioCadastro;

public class Inicio extends AppCompatActivity implements WebServiceReturnString,WebServiceReturnUsuario {

    //utilizado na funcao de saber se o google play service esta instalado
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private CallbackManager callbackManager;

    private LoginButton loginButton;
    private ProgressBar progress_bar_inicio,progress_bar_entrar,progress_bar_cadastro;

    private String id="",email="",nome="",url_img="",cpf="",senha="";

    private EditText et_cpf,et_senha;
    private Button btn_entrar_usuario;

    //cadastro
    EditText et_nome_cadastro,et_email_cadastro,et_cpf_cadastro,et_senha_cadastro,et_senha_novamente_cadastro;
    Button btn_cadastrar,btn_voltar_cadastro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        FacebookSdk.sdkInitialize(getApplicationContext(), new FacebookSdk.InitializeCallback() {
            @Override
            public void onInitialized() {
                if(AccessToken.getCurrentAccessToken() == null){
                    Log.i("Facebook","nao logado");

                    //utilizamos para deixar a lista no modo hide
                    //usuario_logado = false;
                } else {
                    Log.i("Facebook","logando accestoken = "+AccessToken.getCurrentAccessToken());

                    //utilizamos para deixar a lista no modo hide
                    //usuario_logado = true;

                    //graphFacebook(AccessToken.getCurrentAccessToken());
                }
            }
        });

        setContentView(R.layout.layout_inicio);


        callbackManager = CallbackManager.Factory.create();

        //já pegamos de agora o token do usuario para utilizar nas notificações
        if (CheckConection.temConexao(this)){
            if (CheckPlayServices.checkPlayServices(this)) {
                Intent it = new Intent(this, RegistrationIntentService.class);
                startService(it);
            } else {
                Log.i("playservice", "sem playservice");
                mensagem("Alerta","Você precisa ter o Google Play instalado para utilizar todos os serviços do Inbanker.","Ok");
            }
        }else{
            mensagem("Alerta","Você não esta conectado a uma rede de internet. Para utilizar todos os nosso servços conecte-se a uma rede local.","Ok");
        }

        progress_bar_inicio = (ProgressBar) findViewById(R.id.progress_bar_inicio);
        Button btn_cadastro_usuario = (Button) findViewById(R.id.btn_cadastro);
        Button btn_entrar = (Button) findViewById(R.id.btn_entrar);
        loginButton = (LoginButton) findViewById(R.id.fbLoginButton);

        btn_cadastro_usuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Intent it = new Intent(Inicio.this, CadastroUsuario.class);
                //startActivity(it);

                //finish();

                // custom dialog
                final Dialog dialog = new Dialog(Inicio.this,R.style.AppThemeDialog);
                dialog.setContentView(R.layout.dialog_cadastro_usuario_inicio);
                dialog.setTitle("Cadastro");

                progress_bar_cadastro = (ProgressBar) dialog.findViewById(R.id.progress_bar_cadastro);
                et_nome_cadastro = (EditText) dialog.findViewById(R.id.et_nome);
                et_email_cadastro = (EditText) dialog.findViewById(R.id.et_email);
                et_cpf_cadastro = (EditText) dialog.findViewById(R.id.et_cpf);
                et_senha_cadastro = (EditText) dialog.findViewById(R.id.et_senha);
                et_senha_novamente_cadastro = (EditText) dialog.findViewById(R.id.et_senha_novamente);
                btn_cadastrar = (Button) dialog.findViewById(R.id.btn_cadastrar_usuario);

                btn_voltar_cadastro = (Button) dialog.findViewById(R.id.btn_voltar_cadastro);
                btn_voltar_cadastro.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                btn_cadastrar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!CheckConection.temConexao(Inicio.this)){
                            mensagem("Sem conexao!","Olá, para realizar o cadastro você precisa estar conectado em alguma rede.","Ok");
                        }else {

                            clickCadastrar();

                        }
                    }
                });

                dialog.show();
            }
        });

        btn_entrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent it = new Intent(Inicio.this, TelaLogin.class);
                //startActivity(it);
                //finish();

                final Dialog dialog = new Dialog(Inicio.this,R.style.AppThemeDialog);
                dialog.setContentView(R.layout.dialog_login_usuario_inicio);
                dialog.setTitle("Cadastro");

                Button btn_voltar_login = (Button) dialog.findViewById(R.id.btn_voltar_login);
                btn_voltar_login.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                progress_bar_entrar = (ProgressBar) dialog.findViewById(R.id.progress_bar_entrar);
                et_cpf = (EditText) dialog.findViewById(R.id.et_entrar_cpf);
                et_senha = (EditText) dialog.findViewById(R.id.et_entrar_senha);

                et_senha.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View view, int keyCode, KeyEvent event) {
                        if (keyCode == EditorInfo.IME_ACTION_SEARCH ||
                                keyCode == EditorInfo.IME_ACTION_DONE ||
                                event.getAction() == KeyEvent.ACTION_DOWN &&
                                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {


                            clickLogin();

                            return true;
                        }
                        return false;
                    }
                });

                btn_entrar_usuario = (Button) dialog.findViewById(R.id.btn_entrar_usuario);
                btn_entrar_usuario.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        clickLogin();
                    }
                });

                dialog.show();
            }
        });

        loginButton.setReadPermissions("email");
        loginButton.setReadPermissions("user_friends");

        // Other app specific specialization

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.i("Facebook", "onSuceess - loingResult= "+loginResult);

                //chamamos o metedo graphFacebook para obter os dados do usuario logado
                //passando como parametro o accessToken gerado no login
                graphFacebook(loginResult.getAccessToken());
                progress_bar_inicio.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancel() {
                Log.i("Facebook", "onCancel");
            }

            @Override
            public void onError(FacebookException exception) {
                Log.i("Facebook", "onError - exception = "+exception);

                //progress_bar_inicio.setVisibility(View.GONE);
            }

        });

    }

    public void graphFacebook(final AccessToken accessToken){
        try{



            // App code
            GraphRequest request = GraphRequest.newMeRequest(
                    accessToken,
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            //Log.v("Facebook", response.toString());
                            //Log.v("Facebook", object.toString());


                            try {
                                // Application code
                                id = object.getString("id");
                                obterDados(accessToken);

                                //String birthday = object.getString("birthday"); // 01/31/1980 format
                            }catch (Exception e){
                                Log.i("Facebook","Exception JSON graph facebook = "+e);

                                mensagem("Houve um erro!","Ola, parece que tivemos um erro de conexão, por favor tente novamente","OK");

                                progress_bar_inicio.setVisibility(View.GONE);
                            }
                        }
                    });

            request.executeAsync();
        }
        catch (Exception e){
            Log.i("Facebook","Exception graph facebook = "+e);

            progress_bar_inicio.setVisibility(View.GONE);
        }
    }

    public void obterDados(AccessToken accessToken){


        /* make the API call */
        new GraphRequest(
                accessToken,
                "/"+id+"?fields=id,name,email,picture.type(large),friends{id,name,picture.type(large){url}}",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        //Log.i("Facebook","dados gerais = "+response);

                        try {

                            JSONObject object = response.getJSONObject();

                            id = object.getString("id");
                            try {
                                email = (object.getString("email"));
                            }catch (Exception e){
                                Log.i("Facebook","exception email = "+e);
                                email = "";
                            }
                            nome = object.getString("name");

                            JSONObject pic = object.getJSONObject("picture");
                            pic = pic.getJSONObject("data");
                            url_img = pic.getString("url");

                            //metodo para busca usuario face fora do callback facebook
                            buscaUsuarioFace();


                        }
                        catch(Exception e){
                            Log.i("Facebook","exception grah request = "+e);

                            mensagem("Houve um erro!","Ola, parece que tivemos um erro de conexão, por favor tente novamente","OK");

                            progress_bar_inicio.setVisibility(View.GONE);
                        }
                    }
                }
        ).executeAsync();

    }

    public void buscaUsuarioFace(){
        //verificamos se o osuario recem logado ja esta cadastrado
        new BuscaUsuarioFace(id,this, this).execute();
    }

    @Override
    public void retornoUsuarioWebService(Usuario usu){

        String device_id = AllSharedPreferences.getPreferences(AllSharedPreferences.DEVICE_ID,Inicio.this);
        String token = AllSharedPreferences.getPreferences(AllSharedPreferences.TOKEN_GCM,Inicio.this);

        //se ja for cadastrado
        if(usu != null) {

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
            //Log.i("Banco SQLITE","resultado = "+resultado);

            Intent it = new Intent(Inicio.this, NavigationDrawerActivity.class);
            startActivity(it);

            //para encerrar a activity atual e todos os parent
            finishAffinity();
        }else{

            Usuario new_usu = new Usuario();

            new_usu.setToken_gcm(token);
            new_usu.setDevice_id(device_id);
            new_usu.setCpf("");
            new_usu.setEmail(email);
            new_usu.setNome(nome);
            new_usu.setSenha("");

            //setamos esse valores vazio para nao dar problema na hora de serializacao e posteriormente erro no rest de cadastro no banco
            new_usu.setIdFace(id);
            new_usu.setNomeFace(nome);
            new_usu.setUrlImgFace(url_img);

            //fazemos a chamada a classe responsavel por realizar a tarefa de webservice em doinbackground
            new AddUsuario(new_usu,this,this).execute();


        }

        progress_bar_inicio.setVisibility(View.GONE);
    }

    //metodo que sera invoca na classe de webserve
    public void retornoStringWebService(String msg){

        if(msg!=null) {
            if (msg.equals("sucesso")) {

                BancoControllerUsuario crud = new BancoControllerUsuario(getBaseContext());
                //ordem de parametros - nome,email,cpf,senha,id_face,email_face,nome_face,url_img_face
                String resultado = crud.insereDado(nome, email, cpf,senha, id, nome, url_img);
                Log.i("Banco SQLITE", "resultado = " + resultado);

                Intent it = new Intent(Inicio.this, NavigationDrawerActivity.class);
                startActivity(it);

                //para encerrar a activity atual e todos os parent
                finishAffinity();
            } else {
                mensagem("Houve um erro!", "Olá, parece que houve um problema de conexão. Favor tente novamente!", "Ok");
            }
        }else{
            mensagem("Houve um erro!", "Olá, parece que houve um problema de conexão. Favor tente novamente!", "Ok");
        }

    }

    public void clickLogin(){
        boolean campos_ok = true;

        boolean email_valido = Validador.isCPF(et_cpf.getText().toString());
        if(!email_valido){
            et_cpf.setError("CPF inválido");
            et_cpf.setFocusable(true);
            et_cpf.requestFocus();

            campos_ok = false;
        }

        boolean valida_senha = Validador.validateNotNull(et_senha.getText().toString());
        if(!valida_senha){
            et_senha.setError("Campo Vazio");
            et_senha.setFocusable(true);
            et_senha.requestFocus();

            campos_ok = false;
        }


        if(campos_ok) {
            progress_bar_entrar.setVisibility(View.VISIBLE);
            btn_entrar_usuario.setEnabled(false);
            new BuscaUsuarioLogin(et_cpf.getText().toString(), Inicio.this, Inicio.this).execute();
        }

    }

    public void retornoTaskUsuarioLogin(Usuario usu){

        if(usu != null){
            if(usu.getSenha().equals(et_senha.getText().toString())) {

                String device_id = AllSharedPreferences.getPreferences(AllSharedPreferences.DEVICE_ID,Inicio.this);
                String token = AllSharedPreferences.getPreferences(AllSharedPreferences.TOKEN_GCM,Inicio.this);


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

                Intent it = new Intent(Inicio.this, NavigationDrawerActivity.class);
                startActivity(it);

                //para encerrar a activity atual e todos os parent
                finishAffinity();
            }else{
                mensagem("Houve um erro!","Olá, a senha digita está incorreta. Favor tente novamente!","oK");


                btn_entrar_usuario.setEnabled(true);
                progress_bar_entrar.setVisibility(View.GONE);
            }
        }else{
            mensagem("Houve um erro!","Olá, parece que houve um problema no login ou o seu CPF não está cadastrado. Favor tente novamente!","oK");

            btn_entrar_usuario.setEnabled(true);
            progress_bar_entrar.setVisibility(View.GONE);
        }

    }

    public void clickCadastrar(){

        boolean campos_ok = true;

        boolean nome_valido = Validador.validateNotNull(et_nome_cadastro.getText().toString());
        if(!nome_valido) {
            et_nome_cadastro.setError("Campo vazio");
            et_nome_cadastro.setFocusable(true);
            et_nome_cadastro.requestFocus();

            campos_ok = false;
        }

        boolean cpf_valido = Validador.isCPF(et_cpf_cadastro.getText().toString());
        if(!cpf_valido) {
            et_cpf_cadastro.setError("CPF inválido");
            et_cpf_cadastro.setFocusable(true);
            et_cpf_cadastro.requestFocus();

            campos_ok = false;
        }

        boolean email_valido = Validador.validateEmail(et_email_cadastro.getText().toString());
        if(!email_valido){
            et_email_cadastro.setError("Email inválido");
            et_email_cadastro.setFocusable(true);
            et_email_cadastro.requestFocus();

            campos_ok = false;
        }

        boolean valida_senha = Validador.validateNotNull(et_senha_cadastro.getText().toString());
        if(!valida_senha){
            et_senha_cadastro.setError("Campo Vazio");
            et_senha_cadastro.setFocusable(true);
            et_senha_cadastro.requestFocus();

            campos_ok = false;
        }

        boolean valida_confirm_senha = Validador.validateNotNull(et_senha_novamente_cadastro.getText().toString());
        if(!valida_confirm_senha){
            et_senha_novamente_cadastro.setError("Campo Vazio");
            et_senha_novamente_cadastro.setFocusable(true);
            et_senha_novamente_cadastro.requestFocus();

            campos_ok = false;
        }

        if (et_senha_cadastro.getText().toString().equals(et_senha_novamente_cadastro.getText().toString())) {

            if(campos_ok) {

                btn_cadastrar.setEnabled(false);
                progress_bar_cadastro.setVisibility(View.VISIBLE);

                new VerificaUsuarioCadastro(et_email_cadastro.getText().toString(), et_cpf_cadastro.getText().toString(), Inicio.this).execute();
            }

        } else {

            et_senha_novamente_cadastro.setError("Senha diferente");
            et_senha_novamente_cadastro.setFocusable(true);
            et_senha_novamente_cadastro.requestFocus();

            campos_ok = false;
        }
    }

    public void retornoTaskVerificaCadastro(String result){

        Usuario usu = new Usuario();

        if(result == null){

            String device_id = AllSharedPreferences.getPreferences(AllSharedPreferences.DEVICE_ID,Inicio.this);
            String token = AllSharedPreferences.getPreferences(AllSharedPreferences.TOKEN_GCM,Inicio.this);

            //adicionamos os valores as variaveis globais para serem adicionadas corretamente no sqlite la no metido retornoStringWebService
            nome = et_nome_cadastro.getText().toString();
            email = et_email_cadastro.getText().toString();
            cpf = et_cpf_cadastro.getText().toString();
            senha = et_senha_cadastro.getText().toString();

            usu.setToken_gcm(token);
            usu.setDevice_id(device_id);
            usu.setCpf(cpf);
            usu.setEmail(email);
            usu.setNome(nome);
            usu.setSenha(senha);

            //setamos esse valores vazio para nao dar problema na hora de serializacao e posteriormente erro no rest de cadastro no banco
            usu.setIdFace("");
            usu.setNomeFace("");
            usu.setUrlImgFace("");

            //fazemos a chamada a classe responsavel por realizar a tarefa de webservice em doinbackground
            new AddUsuario(usu, Inicio.this,this).execute();
        }else {

            btn_cadastrar.setEnabled(true);
            progress_bar_cadastro.setVisibility(View.GONE);

            //verificamos o resultado da verificação e continuamos o cadastro
            if (result.equals("email"))
                mensagem("Houve um erro!", "Olá, o EMAIL informado já existe, se você esqueceu sua senha tente recupará-la na sessão anterior.", "Ok");
            else if (result.equals("cpf"))
                mensagem("Houve um erro!", "Olá, o CPF informado já existe, por favor informe outro, ou tente recuperar sua senha", "Ok");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("Facebook", "onActivitResult");
        callbackManager.onActivityResult(requestCode, resultCode, data);
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
