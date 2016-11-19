package br.com.appinbanker.inbanker;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

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
import br.com.appinbanker.inbanker.util.AllSharedPreferences;
import br.com.appinbanker.inbanker.util.CheckConection;
import br.com.appinbanker.inbanker.util.CheckPlayServices;
import br.com.appinbanker.inbanker.webservice.AddUsuario;
import br.com.appinbanker.inbanker.webservice.AtualizaTokenGcm;
import br.com.appinbanker.inbanker.webservice.BuscaUsuarioFace;
import br.com.appinbanker.inbanker.webservice.VerificaUsuarioCadastro;

public class Inicio extends AppCompatActivity implements WebServiceReturnString,WebServiceReturnUsuario {

    //utilizado na funcao de saber se o google play service esta instalado
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private CallbackManager callbackManager;

    private LoginButton loginButton;
    //private ProgressBar progress_bar_inicio;

    private String id,email,nome,url_img;

    private WebServiceReturnUsuario ru;

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

        ///progress_bar_inicio = (ProgressBar) findViewById(R.id.progress_bar_inicio);
        Button btn_cadastro = (Button) findViewById(R.id.btn_cadastro);
        Button btn_entrar = (Button) findViewById(R.id.btn_entrar);
        Button btn_fb = (Button) findViewById(R.id.btn_fb);
        loginButton = (LoginButton) findViewById(R.id.fbLoginButton);

        btn_cadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent it = new Intent(Inicio.this, CadastroUsuario.class);
                startActivity(it);

                //finish();
            }
        });

        btn_entrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(Inicio.this, TelaLogin.class);
                startActivity(it);
                //finish();
            }
        });

        btn_fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginButton.performClick();
                //progress_bar_inicio.setVisibility(View.VISIBLE);
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

                            }
                        }
                    });

            request.executeAsync();
        }
        catch (Exception e){
            Log.i("Facebook","Exception graph facebook = "+e);

            ///progress_bar_inicio.setVisibility(View.GONE);
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

                            //progress_bar_inicio.setVisibility(View.GONE);
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

        //progress_bar_inicio.setVisibility(View.GONE);
    }

    //metodo que sera invoca na classe de webserve
    public void retornoStringWebService(String msg){

        if(msg!=null) {
            if (msg.equals("sucesso")) {

                BancoControllerUsuario crud = new BancoControllerUsuario(getBaseContext());
                //ordem de parametros - nome,email,cpf,senha,id_face,email_face,nome_face,url_img_face
                String resultado = crud.insereDado(nome, email, "", "", id, nome, url_img);
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
