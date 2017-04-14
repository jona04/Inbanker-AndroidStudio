package br.com.appinbanker.inbanker;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.joanzapata.iconify.widget.IconButton;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.json.JSONObject;

import java.text.Normalizer;

import br.com.appinbanker.inbanker.entidades.Endereco;
import br.com.appinbanker.inbanker.entidades.Usuario;
import br.com.appinbanker.inbanker.interfaces.WebServiceReturnCadastroString;
import br.com.appinbanker.inbanker.interfaces.WebServiceReturnString;
import br.com.appinbanker.inbanker.interfaces.WebServiceReturnStringAlteraEndereco;
import br.com.appinbanker.inbanker.interfaces.WebServiceReturnStringAlteraSenha;
import br.com.appinbanker.inbanker.interfaces.WebServiceReturnUsuario;
import br.com.appinbanker.inbanker.sqlite.BancoControllerUsuario;
import br.com.appinbanker.inbanker.sqlite.CriandoBanco;
import br.com.appinbanker.inbanker.util.AllSharedPreferences;
import br.com.appinbanker.inbanker.util.FunctionUtil;
import br.com.appinbanker.inbanker.util.Mask;
import br.com.appinbanker.inbanker.util.Validador;
import br.com.appinbanker.inbanker.webservice.AddUsuario;
import br.com.appinbanker.inbanker.webservice.AtualizaUsuario;
import br.com.appinbanker.inbanker.webservice.BuscaUsuarioCPF;
import br.com.appinbanker.inbanker.webservice.EditaEndereco;
import br.com.appinbanker.inbanker.webservice.EditaSenha;
import br.com.appinbanker.inbanker.webservice.VerificaCEP;
import br.com.appinbanker.inbanker.webservice.VerificaUsuarioCadastro;

public class MinhaConta extends AppCompatActivity implements WebServiceReturnUsuario,WebServiceReturnString,WebServiceReturnStringAlteraSenha,WebServiceReturnCadastroString,WebServiceReturnStringAlteraEndereco {

    TextView tv_qtd_pedidos_env,tv_qtd_pedidos_rec,tv_nome_usu_minha_conta;
    ImageView img_minha_conta;

    TextView tv_cpf_conta,tv_nome_conta,tv_email_conta,tv_nasc_conta,tv_sexo_conta;
    TextView tv_cep_conta,tv_rua_conta,tv_numero_conta,tv_complemento_conta,tv_bairro_conta,tv_cidade_conta,tv_estado_conta;

    //edita dados
    EditText et_edita_nome,et_edita_email,et_edita_cpf,et_edita_nasc;
    Spinner spinner_edita_sexo;
    TextWatcher nascMask;

    //edita endereco
    EditText et_edita_cep,et_edita_logradouro,et_edita_numero,et_edita_complemento,et_edita_bairro,et_edita_cidade,et_edita_estado;
    TextWatcher cepMask;

    //altera senha
    EditText et_redefinir_senha_atual,et_redefinir_senha_nova,et_redefinir_senha_novamente;
    Button btn_cancela_redefinir, btn_redefinir_senha;

    Usuario usu_global;

    Dialog dialog;
    ProgressBar progress_bar_atualiza;

    ProgressDialog progress,progress_cep,progress_dados;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_minha_conta);

        tv_qtd_pedidos_env = (TextView) findViewById(R.id.tv_qtd_pedidos_env);
        tv_qtd_pedidos_rec = (TextView) findViewById(R.id.tv_qtd_pedidos_rec);
        img_minha_conta = (ImageView) findViewById(R.id.img_minha_conta);
        tv_nome_usu_minha_conta = (TextView) findViewById(R.id.tv_nome_usu_minha_conta);

        tv_cpf_conta =(TextView) findViewById(R.id.tv_cpf_conta);
        tv_nome_conta =(TextView) findViewById(R.id.tv_nome_conta);
        tv_email_conta =(TextView) findViewById(R.id.tv_email_conta);
        tv_nasc_conta =(TextView) findViewById(R.id.tv_nasc_conta);
        tv_sexo_conta =(TextView) findViewById(R.id.tv_sexo_conta);
        tv_cep_conta =(TextView) findViewById(R.id.tv_cep_conta);
        tv_rua_conta =(TextView) findViewById(R.id.tv_rua_conta);
        tv_numero_conta=(TextView) findViewById(R.id.tv_numero_conta);
        tv_complemento_conta=(TextView) findViewById(R.id.tv_complemento_conta);
        tv_bairro_conta=(TextView) findViewById(R.id.tv_bairro_conta);
        tv_cidade_conta=(TextView) findViewById(R.id.tv_cidade_conta);
        tv_estado_conta=(TextView) findViewById(R.id.tv_estado_conta);

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

                progress = ProgressDialog.show(MinhaConta.this, "Verificando Dados",
                        "Olá, esse processo pode demorar alguns segundos...", true);

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

                progress = ProgressDialog.show(MinhaConta.this, "Verificando Dados",
                        "Olá, esse processo pode demorar alguns segundos...", true);

                configConta(usu_face);

            }
        }catch (Exception e){
            Log.i("Erro Minha Conta","Exception = "+e);
        }

    }

    public void redefinirSenha(){
        if(usu_global.getCpf().equals(""))
            mensagemIntent("Cadastro","Olá, você ainda não possui cadastro no InBanker. Deseja cadastrar-se?","Sim","Não");
        else
            dialog_redefinir_senha();
    }

    public void editarUsuario(){
        if(usu_global.getCpf().equals(""))
            mensagemIntent("Cadastro","Olá, você ainda não possui cadastro no InBanker. Deseja cadastrar-se?","Sim","Não");
        else
            dialog_atualiza_dados();
    }

    public void editarEndereco(){
        if(usu_global.getCpf().equals(""))
            mensagemIntent("Cadastro","Olá, você ainda não possui cadastro no InBanker. Deseja cadastrar-se?","Sim","Não");
        else
            dialog_atualiza_endereco();
    }

    public void dialog_atualiza_endereco(){
        dialog = new Dialog(MinhaConta.this,R.style.AppThemeDialog);
        dialog.setContentView(R.layout.dialog_editar_endereco);
        dialog.setTitle("Redefinir endereco");

        et_edita_cep = (EditText) dialog.findViewById(R.id.et_edita_cep);
        et_edita_logradouro = (EditText) dialog.findViewById(R.id.et_edita_logradouro);
        et_edita_numero =(EditText) dialog.findViewById(R.id.et_edita_numero);
        et_edita_complemento = (EditText) dialog.findViewById(R.id.et_edita_complemento);
        et_edita_bairro =(EditText) dialog.findViewById(R.id.et_edita_bairro);
        et_edita_cidade =(EditText) dialog.findViewById(R.id.et_edita_cidade);
        et_edita_estado =(EditText) dialog.findViewById(R.id.et_edita_estado);

        cepMask = Mask.insert("#####-###", et_edita_cep);
        et_edita_cep.addTextChangedListener(cepMask);

        et_edita_cep.setText(usu_global.getEndereco().getCep());
        et_edita_logradouro.setText(usu_global.getEndereco().getLogradouro());
        et_edita_bairro.setText(usu_global.getEndereco().getBairro());
        et_edita_numero.setText(usu_global.getEndereco().getNumero());
        et_edita_complemento.setText(usu_global.getEndereco().getComplemento());
        et_edita_cidade.setText(usu_global.getEndereco().getCidade());
        et_edita_estado.setText(usu_global.getEndereco().getEstado());

        et_edita_complemento.setOnKeyListener(new View.OnKeyListener() {
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

        et_edita_cep.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.i("beforeTextChanged",charSequence+" - i ="+i+" - i1 = "+i1 + " - i2 = "+i2);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.i("onTextChanged",charSequence+" - i ="+i+" - i1 = "+i1 + " - i2 = "+i2);
                if(i2 == 9){
                    Log.i("addTextChangedListener","chama funcao cep");

                    new VerificaCEP(MinhaConta.this, Mask.unmask(et_edita_cep.getText().toString())).execute();

                    progress_cep = ProgressDialog.show(MinhaConta.this, "Verificando CEP",
                            "Olá, esse processo pode demorar alguns segundos...", true);


                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.i("afterTextChanged",editable+"");
            }

        });

        Button btn_cancela_editar = (Button) dialog.findViewById(R.id.btn_cancela_editar);
        Button btn_atualiza_usuario = (Button) dialog.findViewById(R.id.btn_atualiza_usuario);

        btn_cancela_editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
            }
        });

        btn_atualiza_usuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(validaEditaEndereco()){

                    Endereco end = new Endereco();
                    end.setBairro(et_edita_bairro.getText().toString());
                    end.setCep(Mask.unmask(et_edita_cep.getText().toString()));
                    end.setCidade(et_edita_cidade.getText().toString());
                    end.setComplemento(et_edita_complemento.getText().toString());
                    end.setEstado(et_edita_estado.getText().toString());
                    end.setNumero(et_edita_numero.getText().toString());
                    end.setLogradouro(et_edita_logradouro.getText().toString());

                    usu_global.setEndereco(end);

                    //edita endereco
                    new EditaEndereco(usu_global,MinhaConta.this).execute();

                    progress_cep = ProgressDialog.show(MinhaConta.this, "Atualizando Endereco",
                            "Olá, esse processo pode demorar alguns segundos...", true);

                }

            }
        });

        dialog.show();
    }

    public void dialog_redefinir_senha(){
        dialog = new Dialog(MinhaConta.this,R.style.AppThemeDialog);
        dialog.setContentView(R.layout.dialog_redefinir_senha);
        dialog.setTitle("Redefinir senha");

        progress_bar_atualiza = (ProgressBar) dialog.findViewById(R.id.progress_bar_atualiza);
        et_redefinir_senha_atual = (EditText) dialog.findViewById(R.id.et_redefinir_senha_atual);
        et_redefinir_senha_nova =(EditText) dialog.findViewById(R.id.et_redefinir_senha_nova);
        et_redefinir_senha_novamente = (EditText) dialog.findViewById(R.id.et_redefinir_senha_novamente);

        btn_cancela_redefinir = (Button) dialog.findViewById(R.id.btn_cancela_redefinir);
        btn_redefinir_senha = (Button) dialog.findViewById(R.id.btn_redefinir_senha);

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

                    btn_cancela_redefinir.setEnabled(false);
                    btn_redefinir_senha.setEnabled(false);

                    usu_global.setSenha(FunctionUtil.md5(et_redefinir_senha_nova.getText().toString()));

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

        spinner_edita_sexo = (Spinner) dialog.findViewById(R.id.spinner_edita_sexo);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.spinner_sexo, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner_edita_sexo.setAdapter(adapter);

        if(usu_global.getSexo().equals("0")) {
            spinner_edita_sexo.setSelection(0);
        }else{
            spinner_edita_sexo.setSelection(1);
        }

        progress_bar_atualiza = (ProgressBar) dialog.findViewById(R.id.progress_bar_atualiza);
        et_edita_nome = (EditText) dialog.findViewById(R.id.et_edita_nome);
        et_edita_email = (EditText) dialog.findViewById(R.id.et_edita_email);
        et_edita_cpf = (EditText) dialog.findViewById(R.id.et_edita_cpf);
        et_edita_nasc = (EditText) dialog.findViewById(R.id.et_edita_nasc);

        nascMask = Mask.insert("##/##/####", et_edita_nasc);
        et_edita_nasc.addTextChangedListener(nascMask);

        Button btn_cancela_editar = (Button) dialog.findViewById(R.id.btn_cancela_editar);
        Button btn_atualiza_usuario = (Button) dialog.findViewById(R.id.btn_atualiza_usuario);

        et_edita_nome.setText(usu_global.getNome());
        et_edita_email.setText(usu_global.getEmail());
        et_edita_cpf.setText(usu_global.getCpf());
        et_edita_nasc.setText(usu_global.getIdade());

        btn_cancela_editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
            }
        });

        btn_atualiza_usuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(validaCadastrar()) {

                    progress_dados = ProgressDialog.show(MinhaConta.this, "Atualizando Dados",
                            "Olá, esse processo pode demorar alguns segundos...", true);

                    verificaUsuario();
                }


            }
        });

        dialog.show();
    }

    //nesse metodo, o usuario esta logado apenas com o facebook, por tanto é necessario verificar o cpf e email informado
    public void verificaUsuario(){
        new VerificaUsuarioCadastro(usu_global.getEmail(),"0",this).execute();
    }

    public void atualiza_tipo_cpf(){
        new AtualizaUsuario(usu_global,this).execute();

    }

    public void retornoTaskVerificaCadastro(String result) {

        Log.i("MinhaConta","5 - result = "+result);

        //verifica se cpf ou email informado ja existe
        if(result==null){
            mensagem("Houve um erro!","Olá, parece que tivemos algum problema de conexão, por favor tente novamente.","Ok");
            progress_dados.dismiss();
        //se o email ja existir
        }else if(result.equals("email")) {
            if (!usu_global.getEmail().equals("") && !usu_global.getEmail().equals(usu_global.getEmail())) { //se o email nao for o mesmo do usuario logado
                Log.i("MinhaConta", "5.2");
                mensagem("EMAIL já existe", "Olá, o EMAIL informado já esta em uso, por favor tente outro.", "Ok");
                progress_dados.dismiss();
            } else { //se o email for o mesmo do usuario logado, entao pode atualizar

                //passamos o valor digitado para o usuario global
                usu_global.setEmail(et_edita_email.getText().toString());
                usu_global.setSexo(String.valueOf(spinner_edita_sexo.getSelectedItemPosition()));
                usu_global.setIdade(et_edita_nasc.getText().toString());

                atualiza_tipo_cpf();
            }
        }
    }

    @Override
    public void retornoStringWebServiceAlteraSenha(String result) {

        btn_cancela_redefinir.setEnabled(true);
        btn_redefinir_senha.setEnabled(true);
        progress_bar_atualiza.setVisibility(View.GONE);
        dialog.dismiss();

        if(result != null) {
            if (result.equals("sucesso_edit") || result.equals("sucesso")) {

                BancoControllerUsuario crud = new BancoControllerUsuario(this);
                crud.alteraRegistroFace(usu_global.getCpf(), usu_global.getId_face(), usu_global.getNome(), usu_global.getUrl_face(), usu_global.getEmail(), usu_global.getSenha());

                dialog.dismiss();
                mensagemIntent("Dados Atualizados", "Olá, sua senha foi atualizada com sucesso.", "Ok");

            } else {
                mensagem("Houve um erro!", "Olá, parece que tivemos algum problema de conexão, por favor tente novamente.", "Ok");
            }
        }else{
            mensagem("Erro critico!", "Olá, houve um erro de conexão. Se o erro persistir entre em contato com suporte InBanker", "Ok");
        }
    }

    @Override
    public void retornoStringWebService(String result) {

        progress_dados.dismiss();

        Log.i("MinhaConta","8");
        if(result.equals("sucesso_edit") || result.equals("sucesso")){

            BancoControllerUsuario crud = new BancoControllerUsuario(this);
            crud.alteraRegistroFace(usu_global.getCpf(),usu_global.getId_face(),usu_global.getNome(), usu_global.getUrl_face(),usu_global.getEmail(),usu_global.getSenha());

            dialog.dismiss();
            mensagemIntent("Dados Atualizados","Olá, seus dados foram atualizados com sucesso.","Ok");

        }else{
            Log.i("MinhaConta","8.2");
            mensagem("Houve um erro!","Olá, parece que tivemos algum problema de conexão, por favor tente novamente.","Ok");
        }


    }

    public void configConta(Usuario usu){

        progress.dismiss();

        int qtd_env = 0;
        int qtd_rec = 0;

        if(usu!=null){

            usu_global = usu;

            tv_cpf_conta.setText(usu_global.getCpf());
            tv_nome_conta.setText(usu_global.getNome());
            tv_email_conta.setText(usu_global.getEmail());
            tv_nasc_conta.setText(usu_global.getIdade());

            if(usu_global.getSexo()!=null) {
                if (usu_global.getSexo().equals("0"))
                    tv_sexo_conta.setText("Feminino");
                else
                    tv_sexo_conta.setText("Masculino");
            }

            if(usu_global.getEndereco()!=null) {
                tv_cep_conta.setText(usu_global.getEndereco().getCep());
                tv_rua_conta.setText(usu_global.getEndereco().getLogradouro());
                tv_numero_conta.setText(usu_global.getEndereco().getNumero());
                tv_complemento_conta.setText(usu_global.getEndereco().getComplemento());
                tv_bairro_conta.setText(usu_global.getEndereco().getBairro());
                tv_cidade_conta.setText(usu_global.getEndereco().getCidade());
                tv_estado_conta.setText(usu_global.getEndereco().getEstado());
            }

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
                        .error(R.drawable.logo)
                        .transform(transformation)
                        .into(img_minha_conta);
            }catch (Exception e)
            {
                Log.i("Excpetion","Imagem pedido = "+ e);
            }

            tv_nome_usu_minha_conta.setText(usu.getNome());
            tv_qtd_pedidos_env.setText(String.valueOf(qtd_env));
            tv_qtd_pedidos_rec.setText(String.valueOf(qtd_rec));

            /*if(!usu.getCpf().equals(""))
                et_cpf.setText(usu.getCpf());
            else
                et_cpf.setText("CPF não cadastrado");

            if(!usu.getEmail().equals(""))
                et_email.setText(usu.getEmail());
            else
                et_email.setText("Email não cadastrado");
*/
        }else{
            mensagem("Houve um erro!","Olá, parece que tivemos algum problema de conexão, por favor tente novamente.","Ok");
        }
    }

    @Override
    public void retornoUsuarioWebService(Usuario usu) {

        configConta(usu);

    }

    @Override
    public void retornoUsuarioWebServiceAux(Usuario usu) {

    }

    public void mensagem(String titulo, String corpo, String botao)
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

    public boolean validaCadastrar(){

        boolean campos_ok = true;

        boolean nome_valido = Validador.validateNotNull(et_edita_nome.getText().toString());
        if(!nome_valido) {
            et_edita_nome.setError("Campo vazio");
            et_edita_nome.setFocusable(true);
            et_edita_nome.requestFocus();

            campos_ok = false;
        }

        boolean nasc_valido = Validador.validateNotNull(et_edita_nasc.getText().toString());
        if(!nasc_valido) {
            et_edita_nasc.setError("Campo vazio");
            et_edita_nasc.setFocusable(true);
            et_edita_nasc.requestFocus();

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


        if (!et_edita_email.getText().toString().equals("")) {
            boolean email_valido = Validador.validateEmail(et_edita_email.getText().toString());
            if (!email_valido) {
                et_edita_email.setError("Email inválido");
                et_edita_email.setFocusable(true);
                et_edita_email.requestFocus();

                campos_ok = false;
            }
        }

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
        if(!senha.equals(FunctionUtil.md5(et_redefinir_senha_atual.getText().toString()))) {
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

    public boolean validaEditaEndereco(){

        boolean campos_ok = true;

        boolean cep_valido = Validador.validateNotNull(et_edita_cep.getText().toString());
        if(!cep_valido) {
            et_edita_cep.setError("Campo vazio");
            et_edita_cep.setFocusable(true);
            et_edita_cep.requestFocus();

            campos_ok = false;
        }
        boolean logradouro_valido = Validador.validateNotNull(et_edita_logradouro.getText().toString());
        if(!logradouro_valido) {
            et_edita_logradouro.setError("Campo vazio");
            et_edita_logradouro.setFocusable(true);
            et_edita_logradouro.requestFocus();

            campos_ok = false;
        }

        boolean numero_valido = Validador.validateNotNull(et_edita_numero.getText().toString());
        if(!numero_valido) {
            et_edita_numero.setError("Campo vazio");
            et_edita_numero.setFocusable(true);
            et_edita_numero.requestFocus();

            campos_ok = false;
        }

        boolean bairro_valido = Validador.validateNotNull(et_edita_bairro.getText().toString());
        if(!bairro_valido) {
            et_edita_bairro.setError("Campo vazio");
            et_edita_bairro.setFocusable(true);
            et_edita_bairro.requestFocus();

            campos_ok = false;
        }

        boolean cidade_valido = Validador.validateNotNull(et_edita_cidade.getText().toString());
        if(!cidade_valido) {
            et_edita_cidade.setError("Campo vazio");
            et_edita_cidade.setFocusable(true);
            et_edita_cidade.requestFocus();

            campos_ok = false;
        }

        boolean estado_valido = Validador.validateNotNull(et_edita_estado.getText().toString());
        if(!estado_valido) {
            et_edita_estado.setError("Campo vazio");
            et_edita_estado.setFocusable(true);
            et_edita_estado.requestFocus();

            campos_ok = false;
        }


        if(!campos_ok)
            progress_bar_atualiza.setVisibility(View.GONE);

        return campos_ok;
    }

    public void mensagemIntent(String titulo,String corpo,String botao_positivo,String botao_neutro)
    {
        AlertDialog.Builder mensagem = new AlertDialog.Builder(this);
        mensagem.setTitle(titulo);
        mensagem.setMessage(corpo);
        mensagem.setNeutralButton(botao_neutro,null);
        mensagem.setPositiveButton(botao_positivo,new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent it = new Intent(MinhaConta.this,TelaCadastroMinhaConta.class);
                startActivity(it);
                //para encerrar a activity atual e todos os parent
                finishAffinity();
            }
        });
        mensagem.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.navigation_drawer, menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_minha_conta, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (id == R.id.menu_editar_senha) {
            redefinirSenha();

            return true;
        }
        if (id == R.id.menu_editar_dados) {
            editarUsuario();

            return true;
        }
        if (id == R.id.menu_editar_endereco) {
            editarEndereco();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void retornoStringEndereco(String result) {
        progress_cep.dismiss();

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

                et_edita_logradouro.setText(logradouro);
                et_edita_bairro.setText(bairro);
                et_edita_cidade.setText(cidade);
                et_edita_estado.setText(uf);
                et_edita_numero.setText("");
                et_edita_complemento.setText("");

            } catch (Exception e) {
                Log.i("Script", "" + e);
                mensagem("Endereço","Olá, endereço não foi encontrado, verifique o CEP e tente novamente","Ok");

                et_edita_logradouro.setText("");
                et_edita_bairro.setText("");
                et_edita_cidade.setText("");
                et_edita_estado.setText("");
                et_edita_numero.setText("");
                et_edita_complemento.setText("");

            }
        }else{
            mensagem("Endereço","Olá, endereço não foi encontrado, verifique o CEP e tente novamente","Ok");

            et_edita_logradouro.setText("");
            et_edita_bairro.setText("");
            et_edita_cidade.setText("");
            et_edita_estado.setText("");
            et_edita_numero.setText("");
            et_edita_complemento.setText("");
        }
    }

    @Override
    public void retornoStringNomeCpf(String result) {

    }

    @Override
    public void retornoStringWebServiceAlteraEndereco(String result) {

        progress_cep.dismiss();

        if(result!=null){
            if(result.equals("sucesso_edit")){
                dialog.dismiss();
                mensagemIntent("Atualizado com sucesso","Olá, seu endereço foi atualizado com sucesso.","Ok");
            }else{
                mensagem("Houve um erro!","Olá, parece que tivemos algum problema de conexão, por favor tente novamente.","Ok");
            }
        }else{
            mensagem("Erro crítico!","Olá, parece que tivemos algum problema de conexão, por favor tente novamente.","Ok");
        }
    }

    public void esconderTeclado() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        //inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    public void mensagemIntent(String titulo,String corpo,String botao)
    {
        AlertDialog.Builder mensagem = new AlertDialog.Builder(this);
        mensagem.setTitle(titulo);
        mensagem.setMessage(corpo);
        mensagem.setPositiveButton(botao,new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent it = new Intent(MinhaConta.this, MinhaConta.class);
                startActivity(it);

                //para encerrar a activity atual e todos os parent
                finish();
            }
        });
        mensagem.show();
    }
}
