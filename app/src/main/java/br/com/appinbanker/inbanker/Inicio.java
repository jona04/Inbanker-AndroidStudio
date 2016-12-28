package br.com.appinbanker.inbanker;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
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
import android.widget.Toast;

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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

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
import br.com.appinbanker.inbanker.webservice.AtualizaTokenGcm;
import br.com.appinbanker.inbanker.webservice.BuscaUsuarioFace;
import br.com.appinbanker.inbanker.webservice.BuscaUsuarioLogin;
import br.com.appinbanker.inbanker.webservice.VerificaUsuarioCadastro;

public class Inicio extends AppCompatActivity{


    private CallbackManager callbackManager;

    private LoginButton loginButton;
    private ProgressBar progress_bar_inicio,progress_bar_entrar,progress_bar_cadastro;

    private String id="",email="",nome="",url_img="",cpf="",senha="";

    private EditText et_cpf,et_senha,et_email;
    private Button btn_entrar_usuario;

    private Dialog dialog;

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference usuarioReferencia = databaseReference.child("usuarios");
    private FirebaseAuth firebaseAuth;

    //cadastro
    EditText et_nome_cadastro,et_email_cadastro,et_cpf_cadastro,et_senha_cadastro,et_senha_novamente_cadastro;
    Button btn_cadastrar,btn_voltar_cadastro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.layout_inicio);

        callbackManager = CallbackManager.Factory.create();

        firebaseAuth = FirebaseAuth.getInstance();

        verificarUsuarioLogado();

        progress_bar_inicio = (ProgressBar) findViewById(R.id.progress_bar_inicio);
        loginButton = (LoginButton) findViewById(R.id.fbLoginButton);

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
        //et_cpf = (EditText) dialog.findViewById(R.id.et_entrar_cpf);
        et_email = (EditText) dialog.findViewById(R.id.et_entrar_email);
        et_senha = (EditText) dialog.findViewById(R.id.et_entrar_senha);

        et_senha.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if (keyCode == EditorInfo.IME_ACTION_SEARCH ||
                        keyCode == EditorInfo.IME_ACTION_DONE ||
                        event.getAction() == KeyEvent.ACTION_DOWN &&
                                event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {


                    if(verificaCamposLogin()){
                        validarLogin(et_email.getText().toString(),et_senha.getText().toString());
                    }

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

                if(verificaCamposLogin()){
                    validarLogin(et_email.getText().toString(),et_senha.getText().toString());
                }

                //dialog.dismiss();
            }
        });

        dialog.show();
    }


    public void validarLogin(String email,String senha){

        firebaseAuth.signInWithEmailAndPassword(email,senha)
                .addOnCompleteListener(Inicio.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            Log.i("Firebase","Logado com sucesso");

                            addDadosSqlite();

                            dialog.dismiss();
                        }else{
                            Log.i("Firebase","Logado semmmmm sucesso = "+task.getException());
                            dialog.dismiss();
                        }

                    }
                });
    }

    public void verificarUsuarioLogado(){
        if(firebaseAuth.getCurrentUser() != null){
            direcionarNavigationDrawer();
        }
    }

    public void direcionarNavigationDrawer(){
        Intent it = new Intent(Inicio.this, NavigationDrawerActivity.class);
        startActivity(it);

        //para encerrar a activity atual e todos os parent
        finishAffinity();
    }

    public void addDadosSqlite(){


        DatabaseReference usuario_logado = usuarioReferencia.child(firebaseAuth.getCurrentUser().getUid());
        usuario_logado.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Usuario usu = dataSnapshot.getValue(Usuario.class);


                Log.i("Login firebase","resultado = "+usu +" - "+nome);

                BancoControllerUsuario crud = new BancoControllerUsuario(getBaseContext());
                //ordem de parametros - nome,email,cpf,senha,id_face,email_face,nome_face,url_img_face
                String resultado = crud.insereDado(usu.getId(),usu.getNome(), usu.getEmail(), usu.getCpf(),usu.getSenha(), usu.getIdFace(), usu.getNomeFace(), usu.getUrlImgFace());
                //Log.i("Banco SQLITE", "resultado = " + resultado);

                direcionarNavigationDrawer();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    public void criarConta(View view){

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

                    if(validaCamposCadastrar()){

                        btn_cadastrar.setEnabled(false);
                        progress_bar_cadastro.setVisibility(View.VISIBLE);

                        final Usuario usu = new Usuario();

                        String device_id = AllSharedPreferences.getPreferences(AllSharedPreferences.DEVICE_ID,Inicio.this);
                        String token = AllSharedPreferences.getPreferences(AllSharedPreferences.TOKEN_GCM,Inicio.this);

                        addPreferencesFaceAndCPF(cpf);

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

                        firebaseAuth.createUserWithEmailAndPassword(usu.getEmail(),usu.getSenha())
                                .addOnCompleteListener(Inicio.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(task.isSuccessful()){

                                            usu.setId(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                            usu.salvar();

                                            addDadosSqlite();

                                            dialog.dismiss();

                                            validarLogin(email,senha);

                                        }else{
                                            Toast.makeText(Inicio.this,""+task.getException(),Toast.LENGTH_LONG).show();
                                            Log.i("Firebase Cadastro","Erro em cadastrar usuario = "+task.getException());

                                            btn_cadastrar.setEnabled(true);
                                            progress_bar_cadastro.setVisibility(View.GONE);
                                        }
                                    }
                                });

                    }

                }
            }
        });

        dialog.show();

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

                            //verificamos se o usuario ja existe no banco com o um metodo fora do callback facebook, para nao ter problemas com o parametro implements
                            //buscaUsuarioFace();

                            verificaUsuarioFacebook(id);


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

    public void verificaUsuarioFacebook(String id_face){

        final String device_id = AllSharedPreferences.getPreferences(AllSharedPreferences.DEVICE_ID,Inicio.this);
        final String token = AllSharedPreferences.getPreferences(AllSharedPreferences.TOKEN_GCM,Inicio.this);

        DatabaseReference usuario_logado = usuarioReferencia.child(id_face);
        usuario_logado.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Usuario usu = dataSnapshot.getValue(Usuario.class);

                if (usu != null) {

                    BancoControllerUsuario crud = new BancoControllerUsuario(getBaseContext());
                    //ordem de parametros - nome,email,cpf,senha,id_face,email_face,nome_face,url_img_face
                    String resultado = crud.insereDado(usu.getId(),usu.getNome(), usu.getEmail(), usu.getCpf(), usu.getSenha(), usu.getIdFace(),usu.getNomeFace(),usu.getUrlImgFace());

                    Log.i("Firebase resultado face", "resultado = " + usu.getToken_gcm());

                    if (usu.getToken_gcm() != null && usu.getToken_gcm() != "") {
                        if (!usu.getToken_gcm().equals(token)) {
                            usuarioReferencia.child("token_gcm").setValue(token);
                        }
                    }

                    //parametro null para informar que tem apenas o id_face
                    addPreferencesFaceAndCPF(null);

                    direcionarNavigationDrawer();

                }else {

                    Usuario new_usu = new Usuario();

                    new_usu.setToken_gcm(token);
                    new_usu.setDevice_id(device_id);
                    new_usu.setCpf("");
                    new_usu.setEmail(email);
                    new_usu.setNome(nome);
                    new_usu.setSenha("");

                    new_usu.setIdFace(id);
                    new_usu.setNomeFace(nome);
                    new_usu.setUrlImgFace(url_img);

                    new_usu.setId(id);
                    new_usu.salvar();

                    BancoControllerUsuario crud = new BancoControllerUsuario(getBaseContext());
                    //ordem de parametros - nome,email,cpf,senha,id_face,email_face,nome_face,url_img_face
                    String resultado = crud.insereDado("",nome, email, cpf, senha, id, nome, url_img);

                    //parametro null para informar que tem apenas o id_face
                    addPreferencesFaceAndCPF(null);

                    direcionarNavigationDrawer();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                progress_bar_inicio.setVisibility(View.GONE);
            }
        });
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

    public void addPreferencesFaceAndCPF(String cpf){

        if(id!=null) {
            //add id face no preferences
            AllSharedPreferences.putPreferences(AllSharedPreferences.ID_FACE, id, Inicio.this);
        }
        if(cpf!=null) {
            //add id face no preferences
            AllSharedPreferences.putPreferences(AllSharedPreferences.CPF,cpf, Inicio.this);
        }

    }

    public boolean verificaCamposLogin(){
        boolean campos_ok = true;

        boolean email_valido = Validador.validateEmail(et_email.getText().toString());
        if(!email_valido){
            et_email.setError("EMAIL inválido");
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


        if(campos_ok) {
            progress_bar_entrar.setVisibility(View.VISIBLE);
            btn_entrar_usuario.setEnabled(false);
            //new BuscaUsuarioLogin(et_cpf.getText().toString(), this, this).execute();

            return true;
        }else {
            return false;
        }
    }

    public boolean validaCamposCadastrar(){

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
}
