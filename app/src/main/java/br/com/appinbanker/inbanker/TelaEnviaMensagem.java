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

import br.com.appinbanker.inbanker.entidades.Usuario;
import br.com.appinbanker.inbanker.interfaces.WebServiceReturnString;
import br.com.appinbanker.inbanker.sqlite.BancoControllerUsuario;
import br.com.appinbanker.inbanker.sqlite.CriandoBanco;
import br.com.appinbanker.inbanker.webservice.EnviaEmailMensagem;

public class TelaEnviaMensagem extends AppCompatActivity implements WebServiceReturnString {

    private ProgressDialog progress;
    private TextView nome_usuario;
    private Button btn_enviar_mensagem;
    private EditText et_mensagem,et_email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_tela_envia_mensagem);

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
        nome_usuario = (TextView) findViewById(R.id.nome_usuario);
        et_mensagem = (EditText) findViewById(R.id.et_mensagem);

        et_email.setText(email);
        nome_usuario.setText(nome_usu_logado);

        btn_enviar_mensagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new EnviaEmailMensagem(usu,et_mensagem.getText().toString(),TelaEnviaMensagem.this).execute();

                progress = ProgressDialog.show(TelaEnviaMensagem.this, "Enviando Mensagem",
                        "Olá, esse processo pode demorar alguns segundos...", true);
            }
        });

    }

    @Override
    public void retornoStringWebService(String result) {
        progress.dismiss();
        if(result!=null) {
            if (result.equals("feito")){
                mensagem("Mensagem enviada!","Olá, sua mensagem foi enviada com sucesso.","Ok");
            }else{
                mensagem("Erro mensagem!","Olá, houve um erro no envio da sua mensagem. Por favor tente novamente.","Ok");
            }
        }else{
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
