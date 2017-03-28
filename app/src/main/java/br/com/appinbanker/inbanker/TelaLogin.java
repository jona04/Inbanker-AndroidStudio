package br.com.appinbanker.inbanker;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
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

import org.json.JSONObject;

import java.text.Normalizer;

import br.com.appinbanker.inbanker.entidades.Usuario;
import br.com.appinbanker.inbanker.interfaces.WebServiceReturnString;
import br.com.appinbanker.inbanker.interfaces.WebServiceReturnStringCPF;
import br.com.appinbanker.inbanker.interfaces.WebServiceReturnUsuario;
import br.com.appinbanker.inbanker.interfaces.WebServiceReturnUsuarioFace;
import br.com.appinbanker.inbanker.sqlite.BancoControllerUsuario;
import br.com.appinbanker.inbanker.util.AllSharedPreferences;
import br.com.appinbanker.inbanker.util.Validador;
import br.com.appinbanker.inbanker.webservice.AtualizaTokenGcm;
import br.com.appinbanker.inbanker.webservice.BuscaUsuarioCPF;
import br.com.appinbanker.inbanker.webservice.BuscaUsuarioCPFAux;
import br.com.appinbanker.inbanker.webservice.BuscaUsuarioFace;
import br.com.appinbanker.inbanker.webservice.EnviaEmailMensagem;
import br.com.appinbanker.inbanker.webservice.EnviaEmailNovaSenha;
import br.com.appinbanker.inbanker.webservice.VerificaCPF;

public class TelaLogin extends AppCompatActivity implements WebServiceReturnUsuario,WebServiceReturnUsuarioFace,WebServiceReturnString {

    private CallbackManager callbackManager;

    private EditText et_cpf,et_senha;
    private Button btn_entrar_usuario,btn_esqueceu_senha;

    private ProgressBar progress_bar_entrar,progress_bar_esq_senha;

    private Dialog dialog;
    private EditText et_dialog_senha;
    private Button btn_confirmar_esq_senha;
    private Button btn_voltar_esq_senha;

    //dado face
    private String id_face,nome_face,email_face,url_img_face;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());

        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.layout_tela_login);

        //faz o logout do usuario logado facebook
        LoginManager.getInstance().logOut();

        LoginButton loginButton = (LoginButton) findViewById(R.id.fbLoginButton);

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
                progress_bar_entrar.setVisibility(View.VISIBLE);
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

        progress_bar_entrar = (ProgressBar) findViewById(R.id.progress_bar_entrar);
        et_cpf = (EditText) findViewById(R.id.et_entrar_cpf);
        et_senha = (EditText) findViewById(R.id.et_entrar_senha);

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

        btn_entrar_usuario = (Button) findViewById(R.id.btn_entrar_usuario);
        btn_entrar_usuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                clickLogin();

                //dialog.dismiss();
            }
        });

        btn_esqueceu_senha = (Button) findViewById(R.id.btn_esqueceu_senha);
        btn_esqueceu_senha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = new Dialog(TelaLogin.this,R.style.AppThemeDialog);
                dialog.setContentView(R.layout.dialog_esqueceu_senha);
                dialog.setTitle("Solicitar nova senha");

                progress_bar_esq_senha = (ProgressBar) dialog.findViewById(R.id.progress_bar_esq_senha);
                btn_confirmar_esq_senha = (Button) dialog.findViewById(R.id.btn_confirmar_esq_senha);
                btn_voltar_esq_senha = (Button) dialog.findViewById(R.id.btn_voltar_esq_senha);
                et_dialog_senha = (EditText) dialog.findViewById(R.id.et_dialog_senha);

                btn_confirmar_esq_senha.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(clickRecuperaSenha()) {
                            new BuscaUsuarioCPFAux(et_dialog_senha.getText().toString(),TelaLogin.this, TelaLogin.this).execute();
                            progress_bar_esq_senha.setVisibility(View.VISIBLE);
                            btn_confirmar_esq_senha.setEnabled(false);
                            btn_voltar_esq_senha.setEnabled(false);
                        }
                    }
                });

                btn_voltar_esq_senha.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                dialog.setCancelable(false);
                dialog.show();

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("Facebook", "onActivitResult");
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public boolean clickRecuperaSenha(){

        esconderTeclado();

        boolean campos_ok = true;

        boolean cpf_valido = Validador.isCPF(et_dialog_senha.getText().toString());
        if(!cpf_valido){
            et_dialog_senha.setError("CPF inválido");
            et_dialog_senha.setFocusable(true);
            et_dialog_senha.requestFocus();

            campos_ok = false;
        }

        boolean dialog_senha = Validador.validateNotNull(et_dialog_senha.getText().toString());
        if(!dialog_senha){
            et_dialog_senha.setError("Informe CPF");
            et_dialog_senha.setFocusable(true);
            et_dialog_senha.requestFocus();

            campos_ok = false;
        }
        return campos_ok;

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

            new BuscaUsuarioCPF(et_cpf.getText().toString(), this, this).execute();
        }

    }

    @Override
    public void retornoUsuarioWebService(Usuario usu) {

        if(usu != null){

            if(usu.getSenha().equals(et_senha.getText().toString())) {

                String device_id = AllSharedPreferences.getPreferences(AllSharedPreferences.DEVICE_ID,TelaLogin.this);
                String token = AllSharedPreferences.getPreferences(AllSharedPreferences.TOKEN_GCM,TelaLogin.this);

                if(usu.getToken_gcm()!=null){
                    if(!usu.getToken_gcm().equals(token)){

                        usu.setDevice_id(device_id);
                        usu.setToken_gcm(token);

                        //usuarioReferencia.child(usu.getCpf()).child("token_gcm").setValue(token);

                        //atualizamos do token
                        new AtualizaTokenGcm(usu).execute();
                    }
                }

                BancoControllerUsuario crud = new BancoControllerUsuario(getBaseContext());
                //ordem de parametros - nome,email,cpf,senha,id_face,email_face,nome_face,url_img_face
                String resultado = crud.insereDado(usu.getNome(),usu.getEmail(),usu.getCpf(),usu.getSenha(),usu.getId_face(),usu.getUrl_face(),token,device_id);
                Log.i("Banco SQLITE","resultado login inicio = "+resultado);

                direcionarNavigationDrawer();


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

    public void direcionarNavigationDrawer(){
        Intent it = new Intent(TelaLogin.this, NavigationDrawerActivity.class);
        startActivity(it);

        //para encerrar a activity atual e todos os parent
        finishAffinity();
    }

    @Override
    public void retornoUsuarioWebServiceAux(Usuario usu) {
        if(usu!=null) {

            Log.i("Esqueceu senha","Cpf encontrado");
            new EnviaEmailNovaSenha(usu,TelaLogin.this).execute();

        }else{
            mensagem("Erro conexão!","Olá, parece que tivemos um problema de conexão. Por favor tente novamente.","Ok");
            progress_bar_esq_senha.setVisibility(View.INVISIBLE);
            btn_confirmar_esq_senha.setEnabled(true);
            btn_voltar_esq_senha.setEnabled(true);
        }
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
                            Log.v("Facebook", object.toString());


                            try {
                                // Application code
                                id_face = object.getString("id");
                                obterDados(accessToken);

                                //String birthday = object.getString("birthday"); // 01/31/1980 format
                            }catch (Exception e){
                                Log.i("Facebook","Exception JSON graph facebook = "+e);

                                mensagem("Houve um erro!","Ola, parece que tivemos um erro de conexão, por favor tente novamente","OK");

                                progress_bar_entrar.setVisibility(View.GONE);
                            }
                        }
                    });

            request.executeAsync();
        }
        catch (Exception e){
            Log.i("Facebook","Exception graph facebook = "+e);

            progress_bar_entrar.setVisibility(View.GONE);
        }
    }

    public void obterDados(AccessToken accessToken){


        /* make the API call */
        new GraphRequest(
                accessToken,
                "/"+id_face+"?fields=id,name,email,picture.type(large),friends{id,name,picture.type(large){url}}",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        //Log.i("Facebook","dados gerais = "+response);

                        try {

                            JSONObject object = response.getJSONObject();

                            id_face = object.getString("id");
                            try {
                                email_face = (object.getString("email"));
                            }catch (Exception e){
                                Log.i("Facebook","exception email = "+e);
                                email_face = "";
                            }
                            nome_face = removerAcentos(object.getString("name"));

                            JSONObject pic = object.getJSONObject("picture");
                            pic = pic.getJSONObject("data");
                            url_img_face = pic.getString("url");

                            //verificamos se o usuario ja existe no banco com o um metodo fora do callback facebook, para nao ter problemas com o parametro implements
                            buscaUsuarioFace();


                        }
                        catch(Exception e){
                            Log.i("Facebook","exception grah request = "+e);

                            mensagem("Houve um erro!","Ola, parece que tivemos um erro de conexão, por favor tente novamente","OK");

                            progress_bar_entrar.setVisibility(View.GONE);
                        }
                    }
                }
        ).executeAsync();

    }

    public void buscaUsuarioFace(){
        //verificamos se o osuario recem logado ja esta cadastrado
        new BuscaUsuarioFace(id_face,this, this).execute();
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

    @Override
    public void retornoUsuarioWebServiceFace(Usuario usu) {

        String device_id = AllSharedPreferences.getPreferences(AllSharedPreferences.DEVICE_ID,TelaLogin.this);
        String token = AllSharedPreferences.getPreferences(AllSharedPreferences.TOKEN_GCM,TelaLogin.this);

        //se ja for cadastrado
        if(usu != null) {

            if(usu.getCpf()!=null) {
                if(usu.getCpf().length()>3) {
                    if (usu.getToken_gcm() != null) {
                        if (!usu.getToken_gcm().equals(token)) {

                            usu.setDevice_id(device_id);
                            usu.setToken_gcm(token);

                            //usuarioReferencia.child(usu.getCpf()).child("token_gcm").setValue(token);

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
            BancoControllerUsuario crud = new BancoControllerUsuario(getBaseContext());
            //ordem de parametros - nome,email,cpf,senha,id_face,email_face,nome_face,url_img_face
            String resultado = crud.insereDado(nome_face, "", "","", id_face, url_img_face,token,device_id);
            Log.i("Banco SQLITE", " face resultado = " + resultado);

            direcionarNavigationDrawer();
        }

        progress_bar_entrar.setVisibility(View.GONE);

    }

    public static String removerAcentos(String str) {
        return Normalizer.normalize(str, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    }

    @Override
    public void retornoStringWebService(String result) {
        progress_bar_esq_senha.setVisibility(View.INVISIBLE);
        btn_confirmar_esq_senha.setEnabled(true);
        btn_voltar_esq_senha.setEnabled(true);
        if(result!=null) {
            if (result.equals("feito")){

                mensagem("Nova senha enviada","Olá, uma nova senha foi enviada para seu email cadastrado.","Ok");
            }else{
                mensagem("Erro mensagem!","Olá, houve um erro no envio da sua senha. Por favor tente novamente.","Ok");
            }
        }else{
            mensagem("Erro conexão!","Olá, parece que tivemos um problema de conexão. Por favor tente novamente.","Ok");
        }
    }
}
