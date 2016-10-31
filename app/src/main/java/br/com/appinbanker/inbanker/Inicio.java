package br.com.appinbanker.inbanker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import br.com.appinbanker.inbanker.gcm.RegistrationIntentService;

public class Inicio extends AppCompatActivity {

    //utilizado na funcao de saber se o google play service esta instalado
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_inicio);

        if( checkPlayServices() ){
            Intent it = new Intent(this, RegistrationIntentService.class);
            startService(it);
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

    //para saber se tem o google play service instalado
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i("LOG", "This device is not supported.");
                //finish();
            }
            return false;
        }
        return true;
    }
}
