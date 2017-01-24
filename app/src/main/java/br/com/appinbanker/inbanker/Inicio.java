package br.com.appinbanker.inbanker;

import android.app.ActionBar;
import android.app.Activity;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.List;

import br.com.appinbanker.inbanker.entidades.Amigos;
import br.com.appinbanker.inbanker.entidades.Usuario;
import br.com.appinbanker.inbanker.fcm.MyFirebaseInstanceIDService;
import br.com.appinbanker.inbanker.gcm.RegistrationIntentService;
import br.com.appinbanker.inbanker.interfaces.WebServiceReturnString;
import br.com.appinbanker.inbanker.interfaces.WebServiceReturnStringFace;
import br.com.appinbanker.inbanker.interfaces.WebServiceReturnUsuario;
import br.com.appinbanker.inbanker.interfaces.WebServiceReturnUsuarioFace;
import br.com.appinbanker.inbanker.sqlite.BancoControllerUsuario;
import br.com.appinbanker.inbanker.sqlite.CriandoBanco;
import br.com.appinbanker.inbanker.util.AllSharedPreferences;
import br.com.appinbanker.inbanker.util.CheckConection;
import br.com.appinbanker.inbanker.util.CheckPlayServices;
import br.com.appinbanker.inbanker.util.Validador;
import br.com.appinbanker.inbanker.webservice.AddUsuario;
import br.com.appinbanker.inbanker.webservice.AddUsuarioFace;
import br.com.appinbanker.inbanker.webservice.AtualizaTokenGcm;
import br.com.appinbanker.inbanker.webservice.BuscaUsuarioFace;
import br.com.appinbanker.inbanker.webservice.BuscaUsuarioLogin;
import br.com.appinbanker.inbanker.webservice.VerificaUsuarioCadastro;

public class Inicio extends AppCompatActivity implements WebServiceReturnStringFace,WebServiceReturnUsuarioFace,WebServiceReturnString {


    private CallbackManager callbackManager;

    //private LoginButton loginButton;
    private ProgressBar progress_bar_inicio,progress_bar_entrar,progress_bar_cadastro;

    private String id="",email="",nome="",url_img="",cpf="",senha="";

    private EditText et_cpf,et_senha;
    private Button btn_entrar_usuario;

    private Dialog dialog;

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference usuarioReferencia = databaseReference.child("usuarios");

    //cadastro
    EditText et_nome_cadastro,et_email_cadastro,et_cpf_cadastro,et_senha_cadastro,et_senha_novamente_cadastro;
    Button btn_cadastrar,btn_voltar_cadastro;

    Usuario usu_cadastro;
    Usuario usu_cadastro_face;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d("Success", "Login");
                        //Log.i("Facebook", "onSuceess - loingResult= "+loginResult);

                        //chamamos o metedo graphFacebook para obter os dados do usuario logado
                        //passando como parametro o accessToken gerado no login
                        graphFacebook(loginResult.getAccessToken());
                        progress_bar_inicio.setVisibility(View.VISIBLE);

                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(Inicio.this, "Login Cancel", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(Inicio.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                        Log.i("Facebook", "onError - exception = "+exception);

                        //progress_bar_inicio.setVisibility(View.GONE);
                    }
                });


        setContentView(R.layout.layout_inicio);

        progress_bar_inicio = (ProgressBar) findViewById(R.id.progress_bar_inicio);

        Button btn_facebook=(Button)findViewById(R.id.btn_fb);

        btn_facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LoginManager.getInstance().logInWithReadPermissions(Inicio.this, Arrays.asList("public_profile","email", "user_friends"));
            }
        });

        /*loginButton = (LoginButton) findViewById(R.id.fbLoginButton);

        loginButton.setReadPermissions("email");
        loginButton.setReadPermissions("user_friends");

        // Other app specific specialization

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                //Log.i("Facebook", "onSuceess - loingResult= "+loginResult);

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

        });*/

    }

    public void entrar(View view){

        dialog = new Dialog(Inicio.this,R.style.AppThemeDialog);
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

                    //dialog.dismiss();

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

                //dialog.dismiss();
            }
        });

        dialog.show();


    }

    public void cadastrarUsuario(View view) {

        // custom dialog
        final Dialog dialog = new Dialog(Inicio.this, R.style.AppThemeDialog);
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

                esconderTeclado();

                if (!CheckConection.temConexao(Inicio.this)) {
                    mensagem("Sem conexao!", "Olá, para realizar o cadastro você precisa estar conectado em alguma rede.", "Ok");
                } else {



                    if (validaCadastrar()) {
                        new VerificaUsuarioCadastro(et_email_cadastro.getText().toString(), et_cpf_cadastro.getText().toString(), Inicio.this).execute();
                    }

                }
            }
        });

        dialog.show();
    }

    public void direcionarNavigationDrawer(){
        Intent it = new Intent(Inicio.this, NavigationDrawerActivity.class);
        startActivity(it);

        //para encerrar a activity atual e todos os parent
        finishAffinity();
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
                            nome = removerAcentos(object.getString("name"));

                            JSONObject pic = object.getJSONObject("picture");
                            pic = pic.getJSONObject("data");
                            url_img = pic.getString("url");

                            //verificamos se o usuario ja existe no banco com o um metodo fora do callback facebook, para nao ter problemas com o parametro implements
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

    //login do facebook
    @Override
    public void retornoUsuarioWebServiceFace(Usuario usu){

        String device_id = AllSharedPreferences.getPreferences(AllSharedPreferences.DEVICE_ID,Inicio.this);
        String token = AllSharedPreferences.getPreferences(AllSharedPreferences.TOKEN_GCM,Inicio.this);

        //se ja for cadastrado
        if(usu != null) {

            if(usu.getCpf()!=null) {
                if(usu.getCpf().length()>3) {
                    if (usu.getToken_gcm() != null) {
                        if (!usu.getToken_gcm().equals(token)) {

                            usu.setDevice_id(device_id);
                            usu.setToken_gcm(token);

                            usuarioReferencia.child(usu.getCpf()).child("token_gcm").setValue(token);

                            //atualizamos do token
                            new AtualizaTokenGcm(usu).execute();
                        }
                    }
                }


            }

            BancoControllerUsuario crud = new BancoControllerUsuario(getBaseContext());
            //ordem de parametros - nome,email,cpf,senha,id_face,email_face,nome_face,url_img_face
            String resultado = crud.insereDado(usu.getNome(),usu.getEmail(),usu.getCpf(),usu.getSenha(),usu.getId_face(),usu.getUrl_face(),token,device_id);
            //Log.i("Banco SQLITE","resultado = "+resultado);

            direcionarNavigationDrawer();
        }else{

            usu_cadastro_face = new Usuario();

            usu_cadastro_face.setToken_gcm(token);
            usu_cadastro_face.setDevice_id(device_id);
            usu_cadastro_face.setCpf("");
            usu_cadastro_face.setEmail(email);
            usu_cadastro_face.setNome(nome);
            usu_cadastro_face.setSenha("");

            usu_cadastro_face.setId_face(id);
            usu_cadastro_face.setUrl_face(url_img);

            //fazemos a chamada a classe responsavel por realizar a tarefa de webservice em doinbackground
            new AddUsuarioFace(usu_cadastro_face,this,this).execute();

        }

        progress_bar_inicio.setVisibility(View.GONE);
    }

    //metodo que sera invoca na classe de webserve veio do login face
    public void retornoStringWebServiceFace(String msg){

        String device_id = AllSharedPreferences.getPreferences(AllSharedPreferences.DEVICE_ID,Inicio.this);
        String token = AllSharedPreferences.getPreferences(AllSharedPreferences.TOKEN_GCM,Inicio.this);

        if(msg!=null) {
            if (msg.equals("sucesso")) {

                BancoControllerUsuario crud = new BancoControllerUsuario(getBaseContext());
                //ordem de parametros - nome,email,cpf,senha,id_face,email_face,nome_face,url_img_face
                String resultado = crud.insereDado(nome, email, cpf,senha, id, url_img,token,device_id);
                Log.i("Banco SQLITE", " face resultado = " + resultado);

                //so adicionaremos no firebase usuarios com cadastro completo
                //add usuario no firebase
                //usu_cadastro_face.salvar();

                direcionarNavigationDrawer();
            } else {
                mensagem("Houve um erro!", "Olá, parece que houve um problema de conexão. Favor tente novamente!", "Ok");
            }
        }else{
            mensagem("Houve um erro!", "Olá, parece que houve um problema de conexão. Favor tente novamente!", "Ok");
        }

    }

    public void clickLogin(){

        esconderTeclado();

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

            new BuscaUsuarioLogin(et_cpf.getText().toString(), this, this).execute();
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

                        usuarioReferencia.child(usu.getCpf()).child("token_gcm").setValue(token);

                        //atualizamos do token
                        new AtualizaTokenGcm(usu).execute();
                    }
                }

                BancoControllerUsuario crud = new BancoControllerUsuario(getBaseContext());
                //ordem de parametros - nome,email,cpf,senha,id_face,email_face,nome_face,url_img_face
                String resultado = crud.insereDado(usu.getNome(),usu.getEmail(),usu.getCpf(),usu.getSenha(),usu.getId_face(),usu.getUrl_face(),token,device_id);
                Log.i("Banco SQLITE","resultado login inicio = "+resultado);

                direcionarNavigationDrawer();

                dialog.dismiss();
            }else{
                mensagem("Houve um erro!","Olá, a senha digita está incorreta. Favor tente novamente!","oK");


                btn_entrar_usuario.setEnabled(true);
                progress_bar_entrar.setVisibility(View.GONE);
            }
        }else{

            dialog.dismiss();

            mensagem("Houve um erro!","Olá, parece que houve um problema no login ou o seu CPF não está cadastrado. Favor tente novamente!","oK");

            btn_entrar_usuario.setEnabled(true);
            progress_bar_entrar.setVisibility(View.GONE);
        }

    }

    public boolean validaCadastrar(){

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

        if(et_senha_cadastro.getText().toString().length()<6) {
            et_senha_cadastro.setError("Mínimo de 6 letras");
            et_senha_cadastro.setFocusable(true);
            et_senha_cadastro.requestFocus();

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

                //new VerificaUsuarioCadastro(et_email_cadastro.getText().toString(), et_cpf_cadastro.getText().toString(), Inicio.this).execute();


                return true;
            }else{
                return false;
            }


        } else {

            et_senha_novamente_cadastro.setError("Senha diferente");
            et_senha_novamente_cadastro.setFocusable(true);
            et_senha_novamente_cadastro.requestFocus();

            return false;
        }
    }

    public void retornoTaskVerificaCadastro(String result){

        usu_cadastro = new Usuario();

        if(result == null){

            String device_id = AllSharedPreferences.getPreferences(AllSharedPreferences.DEVICE_ID,Inicio.this);
            String token = AllSharedPreferences.getPreferences(AllSharedPreferences.TOKEN_GCM,Inicio.this);

            //adicionamos os valores as variaveis globais para serem adicionadas corretamente no sqlite la no metido retornoStringWebService
            nome = et_nome_cadastro.getText().toString();
            email = et_email_cadastro.getText().toString();
            cpf = et_cpf_cadastro.getText().toString();
            senha = et_senha_cadastro.getText().toString();

            usu_cadastro.setToken_gcm(token);
            usu_cadastro.setDevice_id(device_id);
            usu_cadastro.setCpf(cpf);
            usu_cadastro.setEmail(email);
            usu_cadastro.setNome(removerAcentos(nome));
            usu_cadastro.setSenha(senha);

            //setamos esse valores vazio para nao dar problema na hora de serializacao e posteriormente erro no rest de cadastro no banco
            usu_cadastro.setId_face("");
            usu_cadastro.setUrl_face("");

            //fazemos a chamada a classe responsavel por realizar a tarefa de webservice em doinbackground
            new AddUsuario(usu_cadastro, Inicio.this,this).execute();

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

    //metodo que sera invoca na classe de webserve veio do cadastro
    public void retornoStringWebService(String msg){

        String device_id = AllSharedPreferences.getPreferences(AllSharedPreferences.DEVICE_ID,Inicio.this);
        String token = AllSharedPreferences.getPreferences(AllSharedPreferences.TOKEN_GCM,Inicio.this);

        if(msg!=null) {
            if (msg.equals("sucesso")) {

                BancoControllerUsuario crud = new BancoControllerUsuario(getBaseContext());
                //ordem de parametros - nome,email,cpf,senha,id_face,email_face,nome_face,url_img_face
                String resultado = crud.insereDado(nome, email, cpf,senha, id, url_img,token,device_id);
                Log.i("Banco SQLITE", "cadastro normal resultado = " + resultado);

                usu_cadastro.salvar();

                direcionarNavigationDrawer();
            } else {
                mensagem("Houve um erro!", "Olá, parece que houve um problema de conexão. Favor tente novamente!", "Ok");
            }
        }else{
            mensagem("Houve um erro!", "Olá, parece que houve um problema de conexão. Favor tente novamente!", "Ok");
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

    public void esconderTeclado() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        //inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    public static String removerAcentos(String str) {
        return Normalizer.normalize(str, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    }
}
