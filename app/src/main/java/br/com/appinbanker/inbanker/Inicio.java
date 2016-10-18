package br.com.appinbanker.inbanker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class Inicio extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_inicio);

        Button btn_cadastro = (Button) findViewById(R.id.btn_cadastro);
        btn_cadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Script", "Clicou em techa tela de apresentação");

                //quando ele apertar em fechar apresentação voltamos para o splash para mostrar o login novamente
                Intent it = new Intent(Inicio.this, CadastroUsuario.class);
                startActivity(it);

                finish();
            }
        });
    }
}
