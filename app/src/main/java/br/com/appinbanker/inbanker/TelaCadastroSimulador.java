package br.com.appinbanker.inbanker;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.facebook.login.LoginManager;
import com.github.pinball83.maskededittext.MaskedEditText;

import org.json.JSONObject;

import java.text.Normalizer;

import br.com.appinbanker.inbanker.entidades.Endereco;
import br.com.appinbanker.inbanker.entidades.Usuario;
import br.com.appinbanker.inbanker.interfaces.WebServiceReturnCadastroString;
import br.com.appinbanker.inbanker.interfaces.WebServiceReturnString;
import br.com.appinbanker.inbanker.interfaces.WebServiceReturnStringCPF;
import br.com.appinbanker.inbanker.interfaces.WebServiceReturnStringEmail;
import br.com.appinbanker.inbanker.sqlite.BancoControllerUsuario;
import br.com.appinbanker.inbanker.sqlite.CriandoBanco;
import br.com.appinbanker.inbanker.util.AllSharedPreferences;
import br.com.appinbanker.inbanker.util.FunctionUtil;
import br.com.appinbanker.inbanker.util.Mask;
import br.com.appinbanker.inbanker.util.Validador;
import br.com.appinbanker.inbanker.webservice.AddUsuario;
import br.com.appinbanker.inbanker.webservice.EnviaEmail;
import br.com.appinbanker.inbanker.webservice.VerificaCEP;
import br.com.appinbanker.inbanker.webservice.VerificaCPF;
import br.com.appinbanker.inbanker.webservice.VerificaCpfReceita;
import br.com.appinbanker.inbanker.webservice.VerificaEmail;

public class TelaCadastroSimulador extends AppCompatActivity implements WebServiceReturnCadastroString,WebServiceReturnStringEmail,WebServiceReturnString,WebServiceReturnStringCPF {

    ProgressDialog progress;

    EditText et_cpf,et_nasc,et_cep;
    EditText et_logradouro,et_complemento,et_bairro,et_cidade,et_estado,et_numero,et_nome,et_email,et_senha,et_confirma_senha;

    Button btn_cadastrar_continuar_cpf,btn_cadastrar_continuar_endereco,btn_cadastrar_usuario,btn_cadastrar_continuar_senha;
    Button btn_ver_termos_uso,btn_ver_politica_privacidade;

    LinearLayout ll_campos_dados_endereco,cep_endereco_completar,ll_et_nome,ll_campos_senha;

    RadioButton radio_sexo_masc,radio_sexo_fem;
    RadioGroup radio_op;
    CheckBox checkbox_termos_uso;

    String id_face;
    String url_img_face;
    Usuario usu_cadastro;

    private TextWatcher cpfMask;
    private TextWatcher nascMask;
    private TextWatcher cepMask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_tela_cadastro);

        //adicionamos dados do face no usuario recem cadastrado
        BancoControllerUsuario crud = new BancoControllerUsuario(this);
        Cursor cursor = crud.carregaDados();
        id_face = cursor.getString(cursor.getColumnIndexOrThrow(CriandoBanco.ID_FACE));
        url_img_face = cursor.getString(cursor.getColumnIndexOrThrow(CriandoBanco.URL_IMG_FACE));

        et_cpf = (EditText) findViewById(R.id.et_cpf);
        et_nasc = (EditText) findViewById(R.id.et_nasc);
        et_cep = (EditText) findViewById(R.id.et_cep);
        et_nome = (EditText) findViewById(R.id.et_nome);

        et_email = (EditText) findViewById(R.id.et_email);
        et_senha = (EditText) findViewById(R.id.et_senha);
        et_confirma_senha = (EditText) findViewById(R.id.et_confirma_senha);

        radio_sexo_masc = (RadioButton) findViewById(R.id.radio_sexo_masc);
        radio_sexo_fem = (RadioButton) findViewById(R.id.radio_sexo_fem);
        radio_op = (RadioGroup) findViewById(R.id.radio_op);
        checkbox_termos_uso = (CheckBox) findViewById(R.id.checkbox_termos_uso);

        btn_ver_termos_uso = (Button) findViewById(R.id.btn_ver_termos_uso);
        btn_ver_politica_privacidade = (Button) findViewById(R.id.btn_ver_politica_privacidade);
        btn_cadastrar_continuar_cpf = (Button) findViewById(R.id.btn_cadastrar_continuar_cpf);
        btn_cadastrar_continuar_endereco = (Button) findViewById(R.id.btn_cadastrar_continuar_endereco);
        btn_cadastrar_usuario = (Button) findViewById(R.id.btn_cadastrar_usuario);
        btn_cadastrar_continuar_senha = (Button) findViewById(R.id.btn_cadastrar_continuar_senha);

        ll_campos_dados_endereco = (LinearLayout) findViewById(R.id.ll_campos_dados_endereco);
        ll_et_nome = (LinearLayout) findViewById(R.id.ll_et_nome);
        ll_campos_senha = (LinearLayout) findViewById(R.id.ll_campos_senha);

        cep_endereco_completar = (LinearLayout) findViewById(R.id.cep_endereco_completar);
        et_numero = (EditText) findViewById(R.id.et_numero);
        et_logradouro = (EditText) findViewById(R.id.et_logradouro) ;
        et_complemento =(EditText) findViewById(R.id.et_complemento) ;
        et_bairro =(EditText) findViewById(R.id.et_bairro) ;
        et_cidade = (EditText) findViewById(R.id.et_cidade) ;
        et_estado =(EditText) findViewById(R.id.et_estado) ;

        cpfMask = Mask.insert("###.###.###-##", et_cpf);
        et_cpf.addTextChangedListener(cpfMask);

        nascMask = Mask.insert("##/##/####", et_nasc);
        et_nasc.addTextChangedListener(nascMask);

        cepMask = Mask.insert("#####-###", et_cep);
        et_cep.addTextChangedListener(cepMask);

        et_confirma_senha.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if (keyCode == EditorInfo.IME_ACTION_SEARCH ||
                        keyCode == EditorInfo.IME_ACTION_DONE ||
                        event.getAction() == KeyEvent.ACTION_DOWN &&
                                event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

                    esconderTeclado();

                    return true;
                }
                return false;
            }
        });

        et_complemento.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if (keyCode == EditorInfo.IME_ACTION_SEARCH ||
                        keyCode == EditorInfo.IME_ACTION_DONE ||
                        event.getAction() == KeyEvent.ACTION_DOWN &&
                                event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

                    esconderTeclado();

                    return true;
                }
                return false;
            }
        });

        btn_ver_termos_uso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_termos_uso();
            }
        });
        btn_ver_politica_privacidade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_politica_privacidade();
            }
        });
        btn_cadastrar_continuar_cpf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean valida_cpf = Validador.isCPF(Mask.unmask(et_cpf.getText().toString()).toString());
                if(!valida_cpf){
                    et_cpf.setError("CPF Inválido");
                    et_cpf.setFocusable(true);
                    et_cpf.requestFocus();
                }else{
                    verificaCpfBDMongo();
                    //verificaCpfReceita();
                    //Log.i("Script","cpf = "+et_cpf.getUnmaskedText());
                }
            }
        });

        btn_cadastrar_continuar_endereco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verificaEndereco();
            }
        });


        btn_cadastrar_continuar_senha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progress = ProgressDialog.show(TelaCadastroSimulador.this, "Verificando Dados",
                        "Olá, esse processo pode demorar alguns segundos...", true);

                if(clickContinuarSenha()){
                    progress.dismiss();
                    btn_cadastrar_continuar_senha.setVisibility(View.GONE);
                    btn_cadastrar_usuario.setVisibility(View.VISIBLE);
                    ll_campos_senha.setVisibility(View.VISIBLE);
                    et_email.requestFocus();
                }else{
                    progress.dismiss();
                }
            }
        });


        btn_cadastrar_usuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verificaEmail();
            }
        });

    }

    public void verificaCpfBDMongo(){

        progress = ProgressDialog.show(TelaCadastroSimulador.this, "Verificando Dados",
                "Olá, esse processo pode demorar alguns segundos...", true);

        new VerificaCPF(Mask.unmask(et_cpf.getText().toString()),this).execute();
    }

    public void verificaCpfReceita(){
        new VerificaCpfReceita(this,Mask.unmask(et_cpf.getText().toString())).execute();


    }

    public void verificaEndereco(){

        if(verificaCEP()) {
            new VerificaCEP(this, Mask.unmask(et_cep.getText().toString())).execute();

            progress = ProgressDialog.show(TelaCadastroSimulador.this, "Verificando Endereço",
                    "Olá, esse processo pode demorar alguns segundos...", true);
        }
    }

    public void verificaEmail(){
        if(clickCadastro()){
            Log.i("Script",et_email.getText().toString() +" - "+et_senha.getText().toString() +" - sexo ="+opcaoSexo());

            new VerificaEmail(et_email.getText().toString(),this).execute();

            progress = ProgressDialog.show(TelaCadastroSimulador.this, "Verificando Dados",
                    "Olá, esse processo pode demorar alguns segundos...", true);

        }

    }

    @Override
    public void retornoStringEndereco(String result) {

        progress.dismiss();

        if(result!=null) {

            Log.i("Script","endereco = "+result);

            try {
                JSONObject jObject = new JSONObject(result); // json
                //String cep = jObject.getString("cep");
                String logradouro = jObject.getString("logradouro");
                //String complemento = jObject.getString("complemento");
                String bairro = jObject.getString("bairro");
                String cidade = jObject.getString("localidade");
                String uf = jObject.getString("uf");
                //String unidade = jObject.getString("unidade");
                //String ibge = jObject.getString("ibge");
                //String gia = jObject.getString("gia");

                cep_endereco_completar.setVisibility(View.VISIBLE);
                et_cep.setEnabled(false);

                et_logradouro.setText(logradouro);
                et_bairro.setText(bairro);
                et_cidade.setText(cidade);
                et_estado.setText(uf);

                btn_cadastrar_continuar_endereco.setVisibility(View.GONE);
                btn_cadastrar_continuar_senha.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                Log.i("Script", "" + e);
                mensagem("Endereço","Olá, endereço não foi encontrado, verifique o CEP e tente novamente","Ok");
            }
        }else{
            mensagem("Endereço","Olá, endereço não foi encontrado, verifique o CEP e tente novamente","Ok");
        }

    }

    @Override
    public void retornoStringNomeCpf(String result) {
        Log.i("Script","nome = "+result);
        progress.dismiss();

        if(result != null){
            String nome = null;
            try {
                JSONObject jObject = new JSONObject(result); // json
                nome = jObject.getString("nome");

                //String situacaoCadastral = jObject.getString("situacaoCadastral");

            }catch (Exception e){
                Log.i("Script", "" + e);
            }

            if(nome.equals(null) || nome.equals("null")){
                mensagem("CPF","Olá, o CPF não foi encontrado, verifique o número informado e tente novamente","Ok");
            }else {
                //informacoes referente ao nome cpf
                ll_et_nome.setVisibility(View.VISIBLE);
                et_nome.setText(nome);
                et_cpf.setEnabled(false);
                btn_cadastrar_continuar_cpf.setVisibility(View.GONE);

                //informacoes referente ao endereco cep
                ll_campos_dados_endereco.setVisibility(View.VISIBLE);
                btn_cadastrar_continuar_endereco.setVisibility(View.VISIBLE);
            }
        }else{
            mensagem("CPF","Olá, o CPF não foi encontrado, verifique o número informado e tente novamente","Ok");
        }

    }

    public boolean verificaCEP(){
        boolean campos_ok = true;

        boolean cep_valido = Validador.validateNotNull(Mask.unmask(et_cep.getText().toString()));
        if(!cep_valido){
            et_cep.setError("CEP vazio");
            et_cep.setFocusable(true);
            et_cep.requestFocus();

            campos_ok = false;
        }
        return campos_ok;
    }

    public boolean clickCadastro(){

        boolean campos_ok = true;

        boolean email_valido = Validador.validateEmail(et_email.getText().toString());
        if(!email_valido){
            et_email.setError("Email inválido");
            et_email.setFocusable(true);
            et_email.requestFocus();

            campos_ok = false;
        }

        if(et_senha.getText().toString().length()<6) {
            et_senha.setError("Mínimo de 6 letras");
            et_senha.setFocusable(true);
            et_senha.requestFocus();

            campos_ok = false;
        }

        if (!et_senha.getText().toString().equals(et_confirma_senha.getText().toString())) {

            et_confirma_senha.setError("Senha diferente");
            et_confirma_senha.setFocusable(true);
            et_confirma_senha.requestFocus();

            campos_ok = false;
        }

        if (!checkbox_termos_uso.isChecked()) {

            checkbox_termos_uso.setError("Ver termos");
            checkbox_termos_uso.setFocusable(true);
            checkbox_termos_uso.requestFocus();

            campos_ok = false;
        }

        return campos_ok;
    }


    public boolean clickContinuarSenha(){

        boolean campos_ok = true;

        boolean numero = Validador.validateNotNull(et_numero.getText().toString());
        if(!numero){
            et_numero.setError("Campo Vazio");
            et_numero.setFocusable(true);
            et_numero.requestFocus();

            campos_ok = false;
        }

        boolean idade = Validador.validateNotNull(Mask.unmask(et_nasc.getText().toString()).toString());
        if(!idade){
            et_nasc.setError("Campo Vazio");
            et_nasc.setFocusable(true);
            et_nasc.requestFocus();

            campos_ok = false;
        }

        if(Mask.unmask(et_nasc.getText().toString()).toString().length() < 7){
            et_nasc.setError("Complete o campo");
            et_nasc.setFocusable(true);
            et_nasc.requestFocus();

            campos_ok = false;
        }

        if(!radio_sexo_fem.isChecked() && !radio_sexo_masc.isChecked()) {
            radio_sexo_masc.setError("Favor informe o sexo");
            et_nasc.setFocusable(true);
            et_nasc.requestFocus();

            campos_ok = false;
        }

        return campos_ok;
    }

    public int opcaoSexo(){

        int op = radio_op.getCheckedRadioButtonId();

        if(op==R.id.radio_sexo_fem)
            return 0;
        else
            return 1;
    }

    @Override
    public void retornoStringWebServiceEmail(String result) {

        Log.i("Script","result email = "+result);

        if(result!=null){
            if(result.equals("email")) {
                //erro
                mensagem("Email já existe!","Olá, o Email informado já existe. Por favor tente outro!","Ok");
                progress.dismiss();

            }else{

                //realiza cadastro

                usu_cadastro = new Usuario();

                String device_id = AllSharedPreferences.getPreferences(AllSharedPreferences.DEVICE_ID,TelaCadastroSimulador.this);
                String token = AllSharedPreferences.getPreferences(AllSharedPreferences.TOKEN_GCM,TelaCadastroSimulador.this);

                //adicionamos os valores as variaveis globais para serem adicionadas corretamente no sqlite la no metido retornoStringWebService

                Endereco end = new Endereco();
                end.setBairro(et_bairro.getText().toString());
                end.setCep(Mask.unmask(et_cep.getText().toString()));
                end.setCidade(et_cidade.getText().toString());
                end.setComplemento(et_complemento.getText().toString());
                end.setEstado(et_estado.getText().toString());
                end.setNumero(et_numero.getText().toString());
                end.setLogradouro(et_logradouro.getText().toString());


                usu_cadastro.setNome(removerAcentos(et_nome.getText().toString()));
                usu_cadastro.setEmail(et_email.getText().toString());
                usu_cadastro.setSenha(FunctionUtil.md5(et_senha.getText().toString()));
                usu_cadastro.setCpf(Mask.unmask(et_cpf.getText().toString()));
                usu_cadastro.setIdade(et_nasc.getText().toString());
                usu_cadastro.setSexo(String.valueOf(opcaoSexo()));

                usu_cadastro.setToken_gcm(token);
                usu_cadastro.setDevice_id(device_id);

                //nao setamos valor vazio no face pois o usuario ja esta logado pela tela inicial
                usu_cadastro.setId_face(id_face);
                usu_cadastro.setUrl_face(url_img_face);


                usu_cadastro.setEndereco(end);

                //fazemos a chamada a classe responsavel por realizar a tarefa de webservice em doinbackground
                new AddUsuario(usu_cadastro, TelaCadastroSimulador.this,this).execute();
            }
        }else{
            //erro
            mensagem("Houve um erro!","Olá, parece que houve um problema de conexao. Favor tente novamente!","Ok");
            progress.dismiss();

        }

    }

    @Override
    public void retornoStringWebService(String msg) {

        String device_id = AllSharedPreferences.getPreferences(AllSharedPreferences.DEVICE_ID,TelaCadastroSimulador.this);
        String token = AllSharedPreferences.getPreferences(AllSharedPreferences.TOKEN_GCM,TelaCadastroSimulador.this);

        if(msg!=null) {
            if (msg.equals("sucesso")) {


                //deletamos os dados do sqlite anterior
                BancoControllerUsuario crud = new BancoControllerUsuario(getBaseContext());
                Cursor cursor = crud.carregaDados();

                //deleta registro do usuario no sqlite
                String cpf = cursor.getString(cursor.getColumnIndexOrThrow("cpf"));
                crud.deletaRegistro(cpf);

                //ordem de parametros - nome,email,cpf,senha,id_face,email_face,nome_face,url_img_face
                String resultado = crud.insereDado(et_nome.getText().toString(), et_email.getText().toString(), Mask.unmask(et_cpf.getText().toString()),
                        FunctionUtil.md5(et_senha.getText().toString()), id_face, url_img_face,token,device_id);
                Log.i("Banco SQLITE", "cadastro normal resultado = " + resultado);

                //salva no firebase
                //usu_cadastro.salvar();

                //envia email para usuario recem cadastro
                new EnviaEmail(usu_cadastro).execute();

                direcionarNavigation();

            } else {
                progress.dismiss();
                mensagem("Houve um erro!", "Olá, parece que houve um problema de conexão. Favor tente novamente!", "Ok");
            }
        }else{
            progress.dismiss();
            mensagem("Houve um erro!", "Olá, parece que houve um problema de conexão. Favor tente novamente!", "Ok");
        }

    }

    public void direcionarNavigation(){
        progress.dismiss();
        //Intent it = new Intent(TelaCadastroSimulador.this, NavigationDrawerActivity.class);
        //startActivity(it);

        //para encerrar a activity atual e todos os parent
        //finishAffinity();
        finish();
    }

    public void dialog_termos_uso(){
        final Dialog dialog = new Dialog(this,R.style.AppThemeDialog);
        dialog.setContentView(R.layout.dialog_termos_uso);

        Button btn_ok_termos_uso = (Button) dialog.findViewById(R.id.btn_ok_termos_uso);
        btn_ok_termos_uso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void dialog_politica_privacidade(){
        final Dialog dialog = new Dialog(this,R.style.AppThemeDialog);
        dialog.setContentView(R.layout.dialog_politica_privacidade);

        Button btn_ok_termos_uso = (Button) dialog.findViewById(R.id.btn_ok_politica_privacidade);
        btn_ok_termos_uso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void mensagem(String titulo,String corpo,String botao)
    {
        AlertDialog.Builder mensagem = new AlertDialog.Builder(this);
        mensagem.setTitle(titulo);
        mensagem.setMessage(corpo);
        mensagem.setNeutralButton(botao,null);
        mensagem.show();
    }

    public static String removerAcentos(String str) {
        return Normalizer.normalize(str, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    }

    @Override
    public void retornoStringWebServiceCPF(String result) {
        Log.i("Script","result cpf = "+result);

        if(result!=null){
            if(result.equals("vazio")) {
                //realiza cadastro

                verificaCpfReceita();
            }else{
                //erro
                progress.dismiss();
                mensagem("CPF já existe!","Olá, o CPF informado já existe. Por favor tente outro!","Ok");
            }
        }else{
            //erro
            progress.dismiss();
            mensagem("Houve um erro!","Olá, parece que houve um problema de conexao. Favor tente novamente!","Ok");
        }
    }
    public void esconderTeclado() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        //inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }
}
