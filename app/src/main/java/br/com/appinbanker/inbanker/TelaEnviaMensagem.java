package br.com.appinbanker.inbanker;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.SimpleShowcaseEventListener;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import br.com.appinbanker.inbanker.entidades.Usuario;
import br.com.appinbanker.inbanker.interfaces.WebServiceReturnString;
import br.com.appinbanker.inbanker.sqlite.BancoControllerUsuario;
import br.com.appinbanker.inbanker.sqlite.CriandoBanco;
import br.com.appinbanker.inbanker.util.AllSharedPreferences;
import br.com.appinbanker.inbanker.util.AnalyticsApplication;
import br.com.appinbanker.inbanker.util.Validador;
import br.com.appinbanker.inbanker.webservice.EnviaEmailMensagem;

public class TelaEnviaMensagem extends AppCompatActivity implements WebServiceReturnString {

    private ProgressDialog progress;
    private Button btn_enviar_mensagem;
    private EditText et_mensagem,et_email,et_titulo_mensagem,nome_usuario;

    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_tela_envia_mensagem);

        // Obtain the shared Tracker instance.
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();

        mTracker.setScreenName("TelaEnviaMensagem");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        BancoControllerUsuario crud = new BancoControllerUsuario(getBaseContext());
        Cursor cursor = crud.carregaDados();

        String url = cursor.getString(cursor.getColumnIndexOrThrow(CriandoBanco.URL_IMG_FACE));
        String nome_usu_logado = cursor.getString(cursor.getColumnIndexOrThrow(CriandoBanco.NOME));
        String email = cursor.getString(cursor.getColumnIndexOrThrow(CriandoBanco.EMAIL));
        String cpf = cursor.getString(cursor.getColumnIndexOrThrow(CriandoBanco.CPF));

        final Usuario usu = new Usuario();
        usu.setCpf(cpf);
        usu.setNome(nome_usu_logado);
        usu.setEmail(email);
        usu.setUrl_face(url);

        et_email = (EditText) findViewById(R.id.et_email);;
        btn_enviar_mensagem = (Button) findViewById(R.id.btn_enviar_mensagem);
        nome_usuario = (EditText) findViewById(R.id.nome_usuario);
        et_mensagem = (EditText) findViewById(R.id.et_mensagem);
        et_titulo_mensagem = (EditText) findViewById(R.id.et_titulo_mensagem);

        et_email.setText(email);
        nome_usuario.setText(nome_usu_logado);

        btn_enviar_mensagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("TelaEnviaMensagem")
                        .setAction("Click_Btn_envia_mensagem")
                        .build());

                if(clickEnviaMensagem()) {
                    new EnviaEmailMensagem(usu, et_mensagem.getText().toString(),et_titulo_mensagem.getText().toString(), TelaEnviaMensagem.this).execute();

                    progress = ProgressDialog.show(TelaEnviaMensagem.this, "Enviando Mensagem",
                            "Olá, esse processo pode demorar alguns segundos...", true);
                }
            }
        });

        if(AllSharedPreferences.getPreferencesBoolean(AllSharedPreferences.VERIFY_TUTORIAL_MENSAGEM,this)==false) {
            new ShowcaseView.Builder(this)
                    .setStyle(R.style.CustomShowcaseTheme)
                    .withMaterialShowcase()
                    .setContentTitle("Entre em contato conosco")
                    .setContentText("Utilize essa sessão para nos enviar sugestões, reclamações ou qualquer tipo de contato.")
                    .build();

            AllSharedPreferences.putPreferencesBooleanTrue(AllSharedPreferences.VERIFY_TUTORIAL_MENSAGEM,this);

        }

    }

    public boolean clickEnviaMensagem(){

        boolean campos_ok = true;

        boolean dialog_email = Validador.validateNotNull(et_email.getText().toString());
        if(!dialog_email){
            et_email.setError("Informe Email");
            et_email.setFocusable(true);
            et_email.requestFocus();

            campos_ok = false;
        }

        boolean dialog_titulo = Validador.validateNotNull(et_titulo_mensagem.getText().toString());
        if(!dialog_titulo){
            et_titulo_mensagem.setError("Informe o assunto");
            et_titulo_mensagem.setFocusable(true);
            et_titulo_mensagem.requestFocus();

            campos_ok = false;
        }

        boolean dialog_mensagem = Validador.validateNotNull(et_mensagem.getText().toString());
        if(!dialog_mensagem){
            et_mensagem.setError("Informe Mensagem");
            et_mensagem.setFocusable(true);
            et_mensagem.requestFocus();

            campos_ok = false;
        }

        return campos_ok;

    }

    @Override
    public void retornoStringWebService(String result) {
        progress.dismiss();
        if(result!=null) {
            if (result.equals("feito")){

                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("TelaEnviaMensagem")
                        .setAction("Mensagem_enviada_sucesso")
                        .build());

                mensagem("Mensagem enviada!","Olá, sua mensagem foi enviada com sucesso.","Ok");
            }else{

                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("TelaEnviaMensagem")
                        .setAction("error envio da sua mensagem")
                        .build());

                mensagem("Erro mensagem!","Olá, houve um erro no envio da sua mensagem. Por favor tente novamente.","Ok");
            }
        }else{

            mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory("TelaEnviaMensagem")
                    .setAction("tivemos um problema de conexão")
                    .build());

            mensagem("Erro conexão!","Olá, parece que tivemos um problema de conexão. Por favor tente novamente.","Ok");
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
