package br.com.appinbanker.inbanker;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import br.com.appinbanker.inbanker.entidades.BancoControllerUsuario;
import br.com.appinbanker.inbanker.sqlite.CriandoBanco;

public class Inicio extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_inicio);




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
}
