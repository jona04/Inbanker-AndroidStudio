package br.com.appinbanker.inbanker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import br.com.appinbanker.inbanker.entidades.Usuario;
import br.com.appinbanker.inbanker.webservice.SpringFrameworkConfig;
import br.com.appinbanker.inbanker.webservice.teste;

public class CadastroUsuario extends AppCompatActivity {

    teste sfc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_cadastro_usuario);
        Log.i("Script", "Cadastro Usuario onCreate");

        //inicializa spring framework
        sfc = new teste();

        Button btn_cadastro = (Button) findViewById(R.id.btn_cadastrar_usuario);
        btn_cadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Script", "Clicou em cadastrar");

                EditText et_nome = (EditText) findViewById(R.id.et_nome);
                EditText et_email = (EditText) findViewById(R.id.et_email);
                EditText et_cpf = (EditText) findViewById(R.id.et_cpf);
                EditText et_senha = (EditText) findViewById(R.id.et_senha);
                EditText et_senha_novamente = (EditText) findViewById(R.id.et_senha_novamente);

                if(et_senha.getText().toString().equals(et_senha_novamente.getText().toString())){

                    new teste().execute();
                }
            }

        });



    }
}
