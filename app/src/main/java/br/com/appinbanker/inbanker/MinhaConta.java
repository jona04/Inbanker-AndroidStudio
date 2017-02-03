package br.com.appinbanker.inbanker;

import android.app.Activity;
import android.app.Dialog;
import android.database.Cursor;
import android.graphics.Color;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.net.InterfaceAddress;
import java.text.Normalizer;

import br.com.appinbanker.inbanker.entidades.Usuario;
import br.com.appinbanker.inbanker.interfaces.WebServiceReturnString;
import br.com.appinbanker.inbanker.interfaces.WebServiceReturnUsuario;
import br.com.appinbanker.inbanker.interfaces.WebServiceReturnUsuarioFace;
import br.com.appinbanker.inbanker.sqlite.BancoControllerUsuario;
import br.com.appinbanker.inbanker.sqlite.CriandoBanco;
import br.com.appinbanker.inbanker.util.Validador;
import br.com.appinbanker.inbanker.webservice.AddUsuario;
import br.com.appinbanker.inbanker.webservice.AtualizaUsuario;
import br.com.appinbanker.inbanker.webservice.BuscaUsuarioCPF;
import br.com.appinbanker.inbanker.webservice.BuscaUsuarioCPFAux;
import br.com.appinbanker.inbanker.webservice.BuscaUsuarioFace;
import br.com.appinbanker.inbanker.webservice.EditaSenha;
import br.com.appinbanker.inbanker.webservice.VerificaUsuarioCadastro;

public class MinhaConta extends AppCompatActivity implements WebServiceReturnUsuario,WebServiceReturnString {

    TextView tv_qtd_pedidos_env,tv_qtd_pedidos_rec,tv_nome_usu_minha_conta;
    LinearLayout progress_minha_conta,ll_principal_minha_conta;
    ImageView img_minha_conta;
    //Button btn_redefinir_senha;
    EditText et_cpf, et_email;

    EditText et_edita_nome,et_edita_email,et_edita_cpf,et_edita_senha,et_edita_senha_novamente;

    EditText et_redefinir_senha_atual,et_redefinir_senha_nova,et_redefinir_senha_novamente;

    Usuario usu_global;

    Dialog dialog;
    ProgressBar progress_bar_atualiza;

    //boolean verificaCpfEmail = false;
    //boolean verificaCpf = false;
    //boolean verificaEmail = false;

    boolean logado_com_face = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_minha_conta);

        tv_qtd_pedidos_env = (TextView) findViewById(R.id.tv_qtd_pedidos_env);
        tv_qtd_pedidos_rec = (TextView) findViewById(R.id.tv_qtd_pedidos_rec);
        img_minha_conta = (ImageView) findViewById(R.id.img_minha_conta);
        tv_nome_usu_minha_conta = (TextView) findViewById(R.id.tv_nome_usu_minha_conta);

        //btn_redefinir_senha = (Button) findViewById(R.id.btn_redefinir_senha);

        et_cpf = (EditText) findViewById(R.id.et_cpf);
        et_email = (EditText) findViewById(R.id.et_email);


        progress_minha_conta = (LinearLayout) findViewById(R.id.progress_minha_conta);
        ll_principal_minha_conta = (LinearLayout) findViewById(R.id.ll_principal_minha_conta);

        //fazemos uma busca do usuario logando no banco para mostrarmos corretamente as notificações interna nos butons da tela incio
        BancoControllerUsuario crud = new BancoControllerUsuario(this);
        Cursor cursor = crud.carregaDados();
        try {
            String cpf = cursor.getString(cursor.getColumnIndexOrThrow(CriandoBanco.CPF));
            String id_face = cursor.getString(cursor.getColumnIndexOrThrow(CriandoBanco.ID_FACE));
            if(!cpf.equals("")) {
                Log.i("CPF","cpf = "+cpf);
                new BuscaUsuarioCPF(cpf,this,this).execute();
                //obterDadosUsuarioFireBase(cpf);

            }else if(!id_face.equals("")){
                Log.i("CPF","id_face = "+id_face);
                //new BuscaUsuarioFace(id_face,this,this).execute();

                String token = cursor.getString(cursor.getColumnIndexOrThrow(CriandoBanco.TOKEN_FCM));
                String device_id = cursor.getString(cursor.getColumnIndexOrThrow(CriandoBanco.DEVICE_ID));
                String nome = cursor.getString(cursor.getColumnIndexOrThrow(CriandoBanco.NOME));
                String url_img = cursor.getString(cursor.getColumnIndexOrThrow(CriandoBanco.URL_IMG_FACE));

                Usuario usu_face = new Usuario();

                usu_face.setToken_gcm(token);
                usu_face.setDevice_id(device_id);
                usu_face.setCpf("");
                usu_face.setEmail("");
                usu_face.setNome(nome);
                usu_face.setSenha("");
                usu_face.setId_face(id_face);
                usu_face.setUrl_face(url_img);

                configConta(usu_face);

                //para verificacao quando o usuario for atualizar senha
                //significa que o usuario nao possui cpf cadastrado,
                // logo se ele inserir algo no campo cpf, obrigatoriamente tera de inserir no campo senha tambem
                //util tambem para saber se o usuario logado possui cpf ou nao
                logado_com_face = true;
            }
        }catch (Exception e){
            progress_minha_conta.setVisibility(View.GONE);
            Log.i("Erro Minha Conta","Exception = "+e);
        }

    }

    public void redefinirSenha(View view){
        if(usu_global.getCpf().equals(""))
            dialog_atualiza_dados();
        else
            dialog_redefinir_senha();
    }

    public void editarUsuario(View view){
        dialog_atualiza_dados();
    }

    public void dialog_redefinir_senha(){
        dialog = new Dialog(MinhaConta.this,R.style.AppThemeDialog);
        dialog.setContentView(R.layout.dialog_redefinir_senha);
        dialog.setTitle("Redefinir senha");

        progress_bar_atualiza = (ProgressBar) dialog.findViewById(R.id.progress_bar_atualiza);
        et_redefinir_senha_atual = (EditText) dialog.findViewById(R.id.et_redefinir_senha_atual);
        et_redefinir_senha_nova =(EditText) dialog.findViewById(R.id.et_redefinir_senha_nova);
        et_redefinir_senha_novamente = (EditText) dialog.findViewById(R.id.et_redefinir_senha_novamente);

        Button btn_cancela_redefinir = (Button) dialog.findViewById(R.id.btn_cancela_redefinir);
        Button btn_redefinir_senha = (Button) dialog.findViewById(R.id.btn_redefinir_senha);

        btn_cancela_redefinir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
            }
        });

        btn_redefinir_senha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progress_bar_atualiza.setVisibility(View.VISIBLE);

                //criamos um webservice so pra mudar a senha

                if(validaEditaSenha()){

                    usu_global.setSenha(et_redefinir_senha_nova.getText().toString());

                    edita_senha();
                }


            }
        });

        dialog.show();
    }

    public void edita_senha(){
        new EditaSenha(usu_global,this).execute();
    }

    public void dialog_atualiza_dados(){
        dialog = new Dialog(MinhaConta.this,R.style.AppThemeDialog);
        dialog.setContentView(R.layout.dialog_editar_usuario);
        dialog.setTitle("Atualizar dados");

        progress_bar_atualiza = (ProgressBar) dialog.findViewById(R.id.progress_bar_atualiza);
        et_edita_nome = (EditText) dialog.findViewById(R.id.et_edita_nome);
        et_edita_email = (EditText) dialog.findViewById(R.id.et_edita_email);
        et_edita_cpf = (EditText) dialog.findViewById(R.id.et_edita_cpf);
        et_edita_senha =(EditText) dialog.findViewById(R.id.et_edita_senha);
        et_edita_senha_novamente = (EditText) dialog.findViewById(R.id.et_edita_senha_novamente);

        Button btn_cancela_editar = (Button) dialog.findViewById(R.id.btn_cancela_editar);
        Button btn_atualiza_usuario = (Button) dialog.findViewById(R.id.btn_atualiza_usuario);

        et_edita_nome.setText(usu_global.getNome());
        et_edita_email.setText(usu_global.getEmail());

        if(!usu_global.getCpf().equals("")) {
            et_edita_cpf.setText(usu_global.getCpf());
            et_edita_cpf.setEnabled(false);
            et_edita_senha.setVisibility(View.GONE);
            et_edita_senha_novamente.setVisibility(View.GONE);
        }

        btn_cancela_editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
            }
        });

        btn_atualiza_usuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progress_bar_atualiza.setVisibility(View.VISIBLE);

                if(usu_global.getSenha().equals(""))
                    usu_global.setSenha(et_edita_senha.getText().toString());

                BancoControllerUsuario crud = new BancoControllerUsuario(MinhaConta.this);
                Cursor cursor = crud.carregaDados();
                String cpf = cursor.getString(cursor.getColumnIndexOrThrow(CriandoBanco.CPF));

                //se usuario logado com face, entao realiza cadastro
                if(logado_com_face) {
                    Log.i("MinhaConta","1");

                    Log.i("MinhaConta","1.1");
                    //verificaCpfEmail = true;

                    usu_global.setNome(et_edita_nome.getText().toString());
                    usu_global.setEmail(et_edita_email.getText().toString());
                    usu_global.setCpf(et_edita_cpf.getText().toString());
                    usu_global.setSenha(et_edita_senha.getText().toString());

                    if(validaCadastrar())
                        verificaUsuario();

                //se usuario nao logado com face, entao atualiza
                }else{

                    Log.i("MinhaConta","2");

                    usu_global.setEmail(removerAcentos(et_edita_email.getText().toString()));
                    usu_global.setNome(removerAcentos(et_edita_nome.getText().toString()));

                    if(validaCadastrar()) {
                        verificaUsuarioApenasEmail();
                    }
                }

            }
        });

        dialog.show();
    }

    //nesse metodo, o usuario ja possui cpf, por tanto nao é necessario a verificacao no banco de dados, apenas do email
    public void verificaUsuarioApenasEmail(){
        new VerificaUsuarioCadastro(usu_global.getEmail(),"0",this).execute();
    }

    //nesse metodo, o usuario esta logado apenas com o facebook, por tanto é necessario verificar o cpf e email informado
    public void verificaUsuario(){
        new VerificaUsuarioCadastro(usu_global.getEmail(),usu_global.getCpf(),this).execute();
    }

    public void atualiza_tipo_cpf(){
        new AtualizaUsuario(usu_global,this,"cpf").execute();

    }
    public void add_usuario(){
        new AddUsuario(usu_global,this,this).execute();

    }

    public void retornoTaskVerificaCadastro(String result) {

        BancoControllerUsuario crud = new BancoControllerUsuario(this);
        Cursor cursor = crud.carregaDados();
        String cpf = cursor.getString(cursor.getColumnIndexOrThrow(CriandoBanco.CPF));
        String email = cursor.getString(cursor.getColumnIndexOrThrow(CriandoBanco.EMAIL));

        Log.i("MinhaConta","5 - result = "+result);

        //verifica se cpf ou email informado ja existe
        if(result==null){
            mensagem("Houve um erro!","Olá, parece que tivemos algum problema de conexão, por favor tente novamente.","Ok");
            progress_bar_atualiza.setVisibility(View.GONE);
        //se o email ja existir
        }else if(result.equals("email")) {
            if (!usu_global.getEmail().equals("") && !usu_global.getEmail().equals(email)){ //se o email nao for o mesmo do usuario logado
                Log.i("MinhaConta", "5.2");
                mensagem("EMAIL já existe", "Olá, o EMAIL informado já esta em uso, por favor tente outro.", "Ok");
                progress_bar_atualiza.setVisibility(View.GONE);
            }else{ //se o email for o mesmo do usuario logado, entao pode atualizar
                Log.i("MinhaConta","5.2.1");
                usu_global.setEmail(removerAcentos(et_edita_email.getText().toString()));
                usu_global.setCpf(et_edita_cpf.getText().toString());
                usu_global.setNome(removerAcentos(et_edita_nome.getText().toString()));

                if(logado_com_face)
                    add_usuario();
                else
                    atualiza_tipo_cpf();
            }
        // lembrar usuario logado, nunca chegara nesse ponto, pois foi colocado 0 no valor do cpf como parametro para verificacao
        //se cpf existir
        }else if(result.equals("cpf")){
            Log.i("MinhaConta", "5.3");
            mensagem("CPF já existe", "Olá, o CPF informado já esta em uso, por favor tente outro, ou tente fazer o login por ele na tela inicial.", "Ok");
            progress_bar_atualiza.setVisibility(View.GONE);

        }else if(result.equals("vazio")){
            Log.i("MinhaConta","5.4");

            usu_global.setEmail(removerAcentos(et_edita_email.getText().toString()));
            usu_global.setCpf(et_edita_cpf.getText().toString());
            usu_global.setNome(removerAcentos(et_edita_nome.getText().toString()));

            if(logado_com_face)
                add_usuario();
            else
                atualiza_tipo_cpf();
        }

    }

    @Override
    public void retornoStringWebService(String result) {

        progress_bar_atualiza.setVisibility(View.GONE);

        Log.i("MinhaConta","8");
        if(result.equals("sucesso_edit") || result.equals("sucesso")){
            Log.i("MinhaConta","8.1");
            mensagem("Dados Atualizados","Olá, seus dados foram atualizados com sucesso.","Ok");

            Log.i("MinhaConta","Result cadastro = cpf = "+usu_global.getCpf());

            BancoControllerUsuario crud = new BancoControllerUsuario(this);

            if(logado_com_face){
                crud.alteraRegistroCpf(usu_global.getId_face(),usu_global.getCpf(),usu_global.getSenha(), usu_global.getEmail(),usu_global.getToken_gcm(),usu_global.getDevice_id(),usu_global.getNome());

            }else{
                crud.alteraRegistroFace(usu_global.getCpf(),usu_global.getId_face(),usu_global.getNome(), usu_global.getUrl_face(),usu_global.getEmail(),usu_global.getSenha());
            }

            progress_bar_atualiza.setVisibility(View.GONE);
            dialog.dismiss();
        }else{
            Log.i("MinhaConta","8.2");
            progress_bar_atualiza.setVisibility(View.GONE);
            mensagem("Houve um erro!","Olá, parece que tivemos algum problema de conexão, por favor tente novamente.","Ok");
        }


    }

    public void configConta(Usuario usu){
        int qtd_env = 0;
        int qtd_rec = 0;

        if(usu!=null){

            usu_global = usu;

            ll_principal_minha_conta.setVisibility(View.VISIBLE);
            progress_minha_conta.setVisibility(View.GONE);

            if(usu.getTransacoes_enviadas() != null) {
                qtd_env = usu.getTransacoes_enviadas().size();
            }

            if(usu.getTransacoes_recebidas() != null) {
                qtd_rec = usu.getTransacoes_recebidas().size();
            }

            try {
                Transformation transformation = new RoundedTransformationBuilder()
                        .borderColor(Color.GRAY)
                        .borderWidthDp(3)
                        .cornerRadiusDp(70)
                        .oval(false)
                        .build();
                Picasso.with(getBaseContext())
                        .load(usu.getUrl_face())
                        .error(R.drawable.icon)
                        .transform(transformation)
                        .into(img_minha_conta);
            }catch (Exception e)
            {
                Log.i("Excpetion","Imagem pedido = "+ e);
            }

            tv_nome_usu_minha_conta.setText(usu.getNome());
            tv_qtd_pedidos_env.setText(String.valueOf(qtd_env));
            tv_qtd_pedidos_rec.setText(String.valueOf(qtd_rec));

            if(!usu.getCpf().equals(""))
                et_cpf.setText(usu.getCpf());
            else
                et_cpf.setText("Informe seu CPF");

            if(!usu.getEmail().equals(""))
                et_email.setText(usu.getEmail());
            else
                et_email.setText("Informe seu Email");

        }else{
            mensagem("Houve um erro!","Olá, parece que tivemos algum problema de conexão, por favor tente novamente.","Ok");

            progress_minha_conta.setVisibility(View.GONE);
        }
    }

    @Override
    public void retornoUsuarioWebService(Usuario usu) {

        configConta(usu);

    }

    @Override
    public void retornoUsuarioWebServiceAux(Usuario usu) {

    }

    /*@Override
    public void retornoUsuarioWebServiceFace(Usuario usu) {
        configConta(usu);
    }*/

    public void mensagem(String titulo, String corpo, String botao)
    {
        AlertDialog.Builder mensagem = new AlertDialog.Builder(this);
        mensagem.setTitle(titulo);
        mensagem.setMessage(corpo);
        mensagem.setNeutralButton(botao,null);
        mensagem.show();
    }

    /*public void esconderTeclado() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        //inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }*/

    public static String removerAcentos(String str) {
        return Normalizer.normalize(str, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    }

    public boolean validaCadastrar(){

        boolean campos_ok = true;

        boolean nome_valido = Validador.validateNotNull(et_edita_nome.getText().toString());
        if(!nome_valido) {
            et_edita_nome.setError("Campo vazio");
            et_edita_nome.setFocusable(true);
            et_edita_nome.requestFocus();

            campos_ok = false;
        }

        if(!et_edita_cpf.getText().toString().equals("")) {
            boolean cpf_valido = Validador.isCPF(et_edita_cpf.getText().toString());
            if (!cpf_valido) {
                et_edita_cpf.setError("CPF inválido");
                et_edita_cpf.setFocusable(true);
                et_edita_cpf.requestFocus();

                campos_ok = false;
            }
        }
        if(!et_edita_senha.getText().toString().equals("")) {
            boolean cpf_valido = Validador.isCPF(et_edita_cpf.getText().toString());
            if (!cpf_valido) {
                et_edita_cpf.setError("CPF inválido");
                et_edita_cpf.setFocusable(true);
                et_edita_cpf.requestFocus();

                campos_ok = false;
            }
        }


        if (!et_edita_email.getText().toString().equals("")) {
            boolean email_valido = Validador.validateEmail(et_edita_email.getText().toString());
            if (!email_valido) {
                et_edita_email.setError("Email inválido");
                et_edita_email.setFocusable(true);
                et_edita_email.requestFocus();

                campos_ok = false;
            }
        }

        //significa que o osuario nao possui cpf cadastrado, logo esses campos devem ser validados
        if(logado_com_face) {

            if(et_edita_senha.getText().toString().length()<6) {
                et_edita_senha.setError("Mínimo de 6 letras");
                et_edita_senha.setFocusable(true);
                et_edita_senha.requestFocus();

                campos_ok = false;
            }

            if (!et_edita_cpf.getText().toString().equals("")) {
                boolean valida_senha = Validador.validateNotNull(et_edita_senha.getText().toString());
                if (!valida_senha) {
                    Log.i("validaCadastro","aqui");
                    et_edita_senha.setError("Campo Vazio");
                    et_edita_senha.setFocusable(true);
                    et_edita_senha.requestFocus();

                    campos_ok = false;
                }
            }


            if (!et_edita_cpf.getText().toString().equals("")) {
                boolean valida_confirm_senha = Validador.validateNotNull(et_edita_senha_novamente.getText().toString());
                if (!valida_confirm_senha) {
                    Log.i("validaCadastro","aqui 2");
                    et_edita_senha_novamente.setError("Campo Vazio");
                    et_edita_senha_novamente.setFocusable(true);
                    et_edita_senha_novamente.requestFocus();

                    campos_ok = false;
                }
            }
        }

        if (!et_edita_senha.getText().toString().equals(et_edita_senha_novamente.getText().toString())) {

                campos_ok = false;
                et_edita_senha_novamente.setError("Senha diferente");
                et_edita_senha_novamente.setFocusable(true);
                et_edita_senha_novamente.requestFocus();
        }

        if(!campos_ok)
            progress_bar_atualiza.setVisibility(View.GONE);

        return campos_ok;
    }

    public boolean validaEditaSenha(){

        boolean campos_ok = true;

        boolean senha_valido = Validador.validateNotNull(et_redefinir_senha_atual.getText().toString());
        if(!senha_valido) {
            et_redefinir_senha_atual.setError("Campo vazio");
            et_redefinir_senha_atual.setFocusable(true);
            et_redefinir_senha_atual.requestFocus();

            campos_ok = false;
        }
        boolean senha_valido_nova = Validador.validateNotNull(et_redefinir_senha_nova.getText().toString());
        if(!senha_valido_nova) {
            et_redefinir_senha_nova.setError("Campo vazio");
            et_redefinir_senha_nova.setFocusable(true);
            et_redefinir_senha_nova.requestFocus();

            campos_ok = false;
        }

        BancoControllerUsuario crud = new BancoControllerUsuario(this);
        Cursor cursor = crud.carregaDados();
        String senha = cursor.getString(cursor.getColumnIndexOrThrow(CriandoBanco.SENHA));
        if(!senha.equals(et_redefinir_senha_atual.getText().toString())) {
            et_redefinir_senha_atual.setError("Senha incorreta");
            et_redefinir_senha_atual.setFocusable(true);
            et_redefinir_senha_atual.requestFocus();

            campos_ok = false;
        }

        if(et_redefinir_senha_nova.getText().toString().length()<6) {
            et_redefinir_senha_nova.setError("Mínimo de 6 letras");
            et_redefinir_senha_nova.setFocusable(true);
            et_redefinir_senha_nova.requestFocus();

            campos_ok = false;
        }

        if (!et_redefinir_senha_nova.getText().toString().equals(et_redefinir_senha_novamente.getText().toString())) {

            campos_ok = false;
            et_redefinir_senha_novamente.setError("Senha diferente");
            et_redefinir_senha_novamente.setFocusable(true);
            et_redefinir_senha_novamente.requestFocus();
        }

        if(!campos_ok)
            progress_bar_atualiza.setVisibility(View.GONE);

        return campos_ok;
    }
}
