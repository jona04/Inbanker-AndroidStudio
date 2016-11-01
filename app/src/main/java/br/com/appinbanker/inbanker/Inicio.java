package br.com.appinbanker.inbanker;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import br.com.appinbanker.inbanker.gcm.RegistrationIntentService;
import br.com.appinbanker.inbanker.util.CheckConection;
import br.com.appinbanker.inbanker.util.CheckPlayServices;

public class Inicio extends AppCompatActivity {

    //utilizado na funcao de saber se o google play service esta instalado
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_inicio);

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

        Button btn_cadastro = (Button) findViewById(R.id.btn_cadastro);
        Button btn_entrar = (Button) findViewById(R.id.btn_entrar);

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
