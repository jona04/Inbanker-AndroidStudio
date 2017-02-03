package br.com.appinbanker.inbanker;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.Normalizer;
import java.text.NumberFormat;
import java.util.Locale;

import br.com.appinbanker.inbanker.entidades.Transacao;
import br.com.appinbanker.inbanker.entidades.Usuario;
import br.com.appinbanker.inbanker.interfaces.WebServiceReturnString;
import br.com.appinbanker.inbanker.interfaces.WebServiceReturnStringHora;
import br.com.appinbanker.inbanker.interfaces.WebServiceReturnUsuario;
import br.com.appinbanker.inbanker.interfaces.WebServiceReturnUsuarioFace;
import br.com.appinbanker.inbanker.sqlite.BancoControllerUsuario;
import br.com.appinbanker.inbanker.sqlite.CriandoBanco;
import br.com.appinbanker.inbanker.util.AllSharedPreferences;
import br.com.appinbanker.inbanker.util.CheckConection;
import br.com.appinbanker.inbanker.util.Validador;
import br.com.appinbanker.inbanker.webservice.AddTransacao;
import br.com.appinbanker.inbanker.webservice.AddUsuario;
import br.com.appinbanker.inbanker.webservice.AtualizaUsuario;
import br.com.appinbanker.inbanker.webservice.BuscaUsuarioFace;
import br.com.appinbanker.inbanker.webservice.EnviaNotificacao;
import br.com.appinbanker.inbanker.webservice.ObterHora;
import br.com.appinbanker.inbanker.webservice.VerificaUsuarioCadastro;

public class SimuladorResultado extends AppCompatActivity implements WebServiceReturnStringHora,WebServiceReturnString,WebServiceReturnUsuarioFace {

    double valor;
    String id,nome,vencimento,url_img;
    int dias;
    TextView tv_nome,tv_valor,tv_vencimento,tv_dias_pagamento,tv_juros_mes,tv_valor_total,tv_valor_juros,tv_valor_servico;
    Transacao trans;
    ProgressBar progress_bar_simulador;
    Button btn_fazer_pedido;

    //FloatingActionButton fab_cpf,fab_senha;

    String token_user2;

    //dialog cadastearr
    private Dialog dialog_cadastro;
    private EditText et_nome_cadastro,et_email_cadastro,et_senha_cadastro,et_senha_novamente_cadastro,et_cpf_cadastro;
    private Button btn_cadastrar,btn_voltar_cadastro;
    private ProgressBar progress_bar_cadastro;

    private Usuario usu_add_trasacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_simulador_resultado);

        trans = new Transacao();

        progress_bar_simulador = (ProgressBar) findViewById(R.id.progress_bar_simulador);

        Intent it = getIntent();
        Bundle parametro = it.getExtras();
        id = parametro.getString("id");
        nome = removerAcentos(parametro.getString("nome"));
        //valor = Double.parseDouble(parametro.getString("valor"));
        vencimento = parametro.getString("vencimento");
        dias = parametro.getInt("dias");
        url_img = parametro.getString("url_img");

        //colocamos um ponto para simular o valor real passado no simulador
        valor = Double.parseDouble(new StringBuffer(parametro.getString("valor")).insert(parametro.getString("valor").length()-2, ".").toString());
        //Log.i("Script","Valor valor ="+valor);

        double juros_mensal = valor * (0.00066333 * dias);
        //double taxa_fixa = Double.parseDouble(decimal.format(valor * 0.0099));

        double valor_total = juros_mensal +  valor;

        ImageView img = (ImageView) findViewById(R.id.img_amigo);

        Transformation transformation = new RoundedTransformationBuilder()
                .borderColor(Color.GRAY)
                .borderWidthDp(3)
                .cornerRadiusDp(70)
                .oval(false)
                .build();

        Picasso.with(getBaseContext())
                .load(url_img)
                .transform(transformation)
                .into(img);

        tv_nome = (TextView) findViewById(R.id.nome_amigo);
        tv_dias_pagamento = (TextView) findViewById(R.id.tv_dias_pagamento);
        tv_juros_mes = (TextView) findViewById(R.id.tv_juros_mes);
        tv_valor_juros = (TextView) findViewById(R.id.tv_valor_juros);
        tv_valor = (TextView) findViewById(R.id.tv_valor);
        tv_valor_total = (TextView) findViewById(R.id.tv_valor_total);
        tv_vencimento = (TextView) findViewById(R.id.tv_vencimento);
        tv_valor_servico = (TextView) findViewById(R.id.tv_valor_servico);

        btn_fazer_pedido = (Button) findViewById(R.id.btn_fazer_pedido);

        Locale ptBr = new Locale("pt", "BR");
        NumberFormat nf = NumberFormat.getCurrencyInstance(ptBr);
        String valor_formatado = nf.format (valor);
        String taxa_fixa_formatado = nf.format (0.0);
        String juros_mensal_formatado = nf.format (juros_mensal);
        String valor_total_formatado = nf.format (valor_total);

        tv_valor_servico.setText(taxa_fixa_formatado);
        tv_nome.setText(nome);
        tv_dias_pagamento.setText(String.valueOf(dias));
        tv_valor_juros.setText(juros_mensal_formatado);
        tv_juros_mes.setText("1,99%");
        tv_valor.setText(valor_formatado);
        tv_valor_total.setText(valor_total_formatado);
        tv_vencimento.setText(vencimento);

        btn_fazer_pedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BancoControllerUsuario crud = new BancoControllerUsuario(getBaseContext());
                Cursor cursor = crud.carregaDados();
                String cpf = cursor.getString(cursor.getColumnIndexOrThrow(CriandoBanco.CPF));
                String nome = cursor.getString(cursor.getColumnIndexOrThrow(CriandoBanco.NOME));
                //se usuario nao tiver o cpf cadastrado, mostramos o dialog para cadastrar, se nao continuamos o pedido mostramos o dialog de senha
                if(cpf.equals("")){

                    // custom dialog
                    dialog_cadastro = new Dialog(SimuladorResultado.this,R.style.AppThemeDialog);
                    dialog_cadastro.setContentView(R.layout.dialog_completar_cadastro);
                    //dialog_cadastro.setTitle("Finalização de cadastro");

                    progress_bar_cadastro = (ProgressBar) dialog_cadastro.findViewById(R.id.progress_bar_cadastro);
                    et_nome_cadastro = (EditText) dialog_cadastro.findViewById(R.id.et_nome);
                    et_email_cadastro = (EditText) dialog_cadastro.findViewById(R.id.et_email);
                    et_cpf_cadastro = (EditText) dialog_cadastro.findViewById(R.id.et_cpf);
                    et_senha_cadastro = (EditText) dialog_cadastro.findViewById(R.id.et_senha);
                    et_senha_novamente_cadastro = (EditText) dialog_cadastro.findViewById(R.id.et_senha_novamente);
                    btn_cadastrar = (Button) dialog_cadastro.findViewById(R.id.btn_cadastrar_usuario);

                    et_nome_cadastro.setText(nome);

                    btn_voltar_cadastro = (Button) dialog_cadastro.findViewById(R.id.btn_voltar_cadastro);
                    btn_voltar_cadastro.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog_cadastro.dismiss();
                        }
                    });
                    btn_cadastrar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(!CheckConection.temConexao(SimuladorResultado.this)){
                                mensagem("Sem conexao!","Olá, para realizar o cadastro você precisa estar conectado em alguma rede.","Ok");
                            }else {

                                clickCadastrar();

                            }
                        }
                    });

                    dialog_cadastro.show();

                }else {
                    // custom dialog
                    final Dialog dialog = new Dialog(SimuladorResultado.this,R.style.AppThemeDialog);
                    dialog.setContentView(R.layout.dialog_senha);
                    dialog.setTitle("Informe sua senha");


                    final EditText et_senha = (EditText) dialog.findViewById(R.id.et_dialog_senha);
                    final TextView msg_dialog = (TextView) dialog.findViewById(R.id.msg_dialog);
                    Button btn_dialog_cancelar = (Button) dialog.findViewById(R.id.btn_voltar_dialog_senha);
                    Button btn_dialog_ok = (Button) dialog.findViewById(R.id.btn_entrar_dialog_senha);

                    // if button is clicked, close the custom dialog
                    btn_dialog_cancelar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    btn_dialog_ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            msg_dialog.setVisibility(View.INVISIBLE);

                            BancoControllerUsuario crud = new BancoControllerUsuario(getBaseContext());
                            Cursor cursor = crud.carregaDados();

                            String senha = cursor.getString(cursor.getColumnIndexOrThrow(CriandoBanco.SENHA));

                            if (senha.equals(et_senha.getText().toString())) {

                                buscaUsuarioFace();

                                progress_bar_simulador.setVisibility(View.VISIBLE);
                                btn_fazer_pedido.setEnabled(false);

                                dialog.dismiss();
                            } else {

                                msg_dialog.setVisibility(View.VISIBLE);
                            }


                        }
                    });

                    dialog.show();
                }
            }
        });

    }

    //buscamos pelo face, pois só temos o id_face do usuario
    public void buscaUsuarioFace() {
        new BuscaUsuarioFace(id, SimuladorResultado.this, this).execute();
    }

    public void clickCadastrar(){

        boolean campos_ok = true;

        boolean nome_valido = Validador.validateNotNull(et_nome_cadastro.getText().toString());
        if(!nome_valido) {
            et_nome_cadastro.setError("Campo vazio");
            et_nome_cadastro.setFocusable(true);
            et_nome_cadastro.requestFocus();

            campos_ok = false;
        }

        boolean cpf_valido = Validador.isCPF(et_cpf_cadastro.getText().toString());
        if(!cpf_valido) {
            et_cpf_cadastro.setError("CPF inválido");
            et_cpf_cadastro.setFocusable(true);
            et_cpf_cadastro.requestFocus();

            campos_ok = false;
        }

        boolean email_valido = Validador.validateEmail(et_email_cadastro.getText().toString());
        if(!email_valido){
            et_email_cadastro.setError("Email inválido");
            et_email_cadastro.setFocusable(true);
            et_email_cadastro.requestFocus();

            campos_ok = false;
        }

        if(et_senha_cadastro.getText().toString().length()<6) {
            et_senha_cadastro.setError("Mínimo de 6 letras");
            et_senha_cadastro.setFocusable(true);
            et_senha_cadastro.requestFocus();

            campos_ok = false;
        }

        boolean valida_senha = Validador.validateNotNull(et_senha_cadastro.getText().toString());
        if(!valida_senha){
            et_senha_cadastro.setError("Campo Vazio");
            et_senha_cadastro.setFocusable(true);
            et_senha_cadastro.requestFocus();

            campos_ok = false;
        }

        boolean valida_confirm_senha = Validador.validateNotNull(et_senha_novamente_cadastro.getText().toString());
        if(!valida_confirm_senha){
            et_senha_novamente_cadastro.setError("Campo Vazio");
            et_senha_novamente_cadastro.setFocusable(true);
            et_senha_novamente_cadastro.requestFocus();

            campos_ok = false;
        }

        if (et_senha_cadastro.getText().toString().equals(et_senha_novamente_cadastro.getText().toString())) {

            if(campos_ok) {

                btn_cadastrar.setEnabled(false);
                progress_bar_cadastro.setVisibility(View.VISIBLE);

                new VerificaUsuarioCadastro(et_email_cadastro.getText().toString(), et_cpf_cadastro.getText().toString(), SimuladorResultado.this).execute();
            }

        } else {

            et_senha_novamente_cadastro.setError("Senha diferente");
            et_senha_novamente_cadastro.setFocusable(true);
            et_senha_novamente_cadastro.requestFocus();

            campos_ok = false;
        }
    }

    public void retornoTaskVerificaCadastro(String result){

        if(result == null){
            mensagem("Houve um erro!", "Olá, o CPF informado já existe, por favor informe outro, ou tente recuperar sua senha", "Ok");
        }else{

            progress_bar_cadastro.setVisibility(View.INVISIBLE);
            btn_cadastrar.setEnabled(true);
            btn_voltar_cadastro.setEnabled(true);

            //verificamos o resultado da verificação e continuamos o cadastro, mas antes vemos tambem se o email é o mesmo do ja existente no banco atras do login no facebook
            if (result.equals("email"))
                mensagem("Houve um erro!", "Olá, o EMAIL informado já existe, se você esqueceu sua senha tente recupará-la na sessão anterior.", "Ok");
            else if (result.equals("cpf"))
                mensagem("Houve um erro!", "Olá, o CPF informado já existe, por favor informe outro, ou tente recuperar sua senha", "Ok");
            else
                addUsuario();
        }
    }

    public void addUsuario(){

        String device_id = AllSharedPreferences.getPreferences(AllSharedPreferences.DEVICE_ID,this);
        String token = AllSharedPreferences.getPreferences(AllSharedPreferences.TOKEN_GCM,this);

        Usuario usu = new Usuario();

        BancoControllerUsuario crud = new BancoControllerUsuario(getBaseContext());
        Cursor cursor = crud.carregaDados();

        //atualizamos os dados do usuario que esta no sqlite com os dados dele que acabaram de ser logados no facebook
        String id_face = cursor.getString(cursor.getColumnIndexOrThrow(CriandoBanco.ID_FACE));
        crud.alteraRegistroCpf(id_face,et_cpf_cadastro.getText().toString(),et_senha_cadastro.getText().toString(),et_email_cadastro.getText().toString(),token,device_id,et_nome_cadastro.getText().toString());

        cursor = crud.carregaDados();

        usu.setCpf(cursor.getString(cursor.getColumnIndexOrThrow(CriandoBanco.CPF)));
        usu.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(CriandoBanco.EMAIL)));
        usu.setNome(cursor.getString(cursor.getColumnIndexOrThrow(CriandoBanco.NOME)));
        usu.setSenha(cursor.getString(cursor.getColumnIndexOrThrow(CriandoBanco.SENHA)));
        usu.setId_face(cursor.getString(cursor.getColumnIndexOrThrow(CriandoBanco.ID_FACE)));
        usu.setUrl_face(cursor.getString(cursor.getColumnIndexOrThrow(CriandoBanco.URL_IMG_FACE)));
        usu.setToken_gcm(cursor.getString(cursor.getColumnIndexOrThrow(CriandoBanco.TOKEN_FCM)));
        usu.setDevice_id(cursor.getString(cursor.getColumnIndexOrThrow(CriandoBanco.DEVICE_ID)));

        usu.salvar();

        //fazemos a chamada a classe responsavel por realizar a tarefa de webservice em doinbackground
        new AddUsuario(usu,this,this).execute();
    }

    @Override
    public void retornoStringWebService(String result) {
        Log.i("Webservice","retorno = "+result);

        if(result.equals("sucesso")){
            dialog_cadastro.dismiss();

            mensagem("InBanker", "Parabéns, você já pode realizar o pedido de empréstimo.", "Ok");
        }else{
            dialog_cadastro.dismiss();
            mensagem("Houve um erro!", "Olá, parece que tivemos algum problema de conexão, por favor tente novamente.", "Ok");
        }
    }


    @Override
    public void retornoUsuarioWebServiceFace(Usuario usu){

        if(usu != null) {
            if(usu.getCpf().equals("")){
                mensagem("Falha no envio!", "Olá, seu amigo(a) "+usu.getNome()+" ainda não completou o cadastro. Solicite que ele vá em configurações e atualize seus dados.", "Ok");

                //habilitamos novamente o botao de fazer pedido e tiramos da tela o progress bar
                progress_bar_simulador.setVisibility(View.GONE);
                btn_fazer_pedido.setEnabled(true);

            }else {

                usu_add_trasacao = usu;

                //obter hora do servidor para adicionar na trasacao
                new ObterHora(this).execute();

            }
        }else{
            mensagem("Houve um erro!","Olá, parece que houve um problema de conexao. Favor tente novamente!","OK");

            //habilitamos novamente o botao de fazer pedido e tiramos da tela o progress bar
            progress_bar_simulador.setVisibility(View.GONE);
            btn_fazer_pedido.setEnabled(true);

        }


    }

    @Override
    public void retornoObterHora(String hoje){

        if(hoje!=null) {
            if (hoje.equals("error")) {
                mensagem("Houve um erro!", "Parece que houve um erro de conexão, por favor tente novamente.", "Ok");

                //habilitamos novamente o botao de fazer pedido e tiramos da tela o progress bar
                progress_bar_simulador.setVisibility(View.GONE);
                btn_fazer_pedido.setEnabled(true);

            } else {

                Log.i("SimuladorResultado","Hora seridor = "+hoje);


                //pegamos o token do usuario para usar na notificação
                token_user2 = usu_add_trasacao.getToken_gcm();

                //data vencimento, convertendo para padrao utc
                DateTimeFormatter fmt = DateTimeFormat.forPattern("dd/MM/YYYY");
                DateTime jodatime = fmt.parseDateTime(vencimento);
                DateTimeFormatter dtfOut = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
                String vencimento_utf = dtfOut.print(jodatime);

                BancoControllerUsuario crud = new BancoControllerUsuario(getBaseContext());
                Cursor cursor = crud.carregaDados();

                //adicionamos a trasacao em ambas as contas
                trans.setDataPedido(hoje);
                trans.setUsu1(cursor.getString(cursor.getColumnIndexOrThrow(CriandoBanco.CPF)));
                trans.setUsu2(usu_add_trasacao.getCpf());
                trans.setValor(String.valueOf(valor));
                trans.setVencimento(vencimento_utf);
                trans.setNome_usu2(usu_add_trasacao.getNome());
                trans.setNome_usu1(cursor.getString(cursor.getColumnIndexOrThrow(CriandoBanco.NOME)));
                trans.setUrl_img_usu2(url_img);
                trans.setUrl_img_usu1(cursor.getString(cursor.getColumnIndexOrThrow(CriandoBanco.URL_IMG_FACE)));
                trans.setStatus_transacao(String.valueOf(Transacao.AGUARDANDO_RESPOSTA));
                //enviamos null para criamos um id aleatorio
                trans.setId_trans(null);

                new AddTransacao(trans, SimuladorResultado.this).execute();

            }
        }else{
            mensagem("Erro crítico!", "Parece que houve um erro de conexão, por favor tente novamente.", "Ok");

            //habilitamos novamente o botao de fazer pedido e tiramos da tela o progress bar
            progress_bar_simulador.setVisibility(View.GONE);
            btn_fazer_pedido.setEnabled(true);
        }
    }


    public void retornoAddTransacao(String result){

        //habilitamos novamente o botao de fazer pedido e tiramos da tela o progress bar
        progress_bar_simulador.setVisibility(View.GONE);
        btn_fazer_pedido.setEnabled(true);
        if(result!=null) {
            if (result.equals("sucesso_edit")) {


                if(!token_user2.equals("")) {
                    //envia notificação
                    new EnviaNotificacao(trans, token_user2).execute();

                    //aletar e redirecionamento para tela inicial
                    mensagemIntent("InBanker", "Pedido enviado, aguarde a resposta de seu amigo(a) " + nome, "Ok");
                }else{
                    mensagemIntent("InBanker","Pedido enviado! Porém recomendamos entrar em contato pessoalmente com seu amigo(a) "+nome+". Pois ele não receberá notifição de aviso por não estar logado.", "Ok");
                }
            } else {
                mensagem("Houve um erro!", "Parece que houve um erro de conexão, por favor tente novamente.", "Ok");
            }
        }else{
            mensagem("Houve um erro!", "Parece que houve um erro de conexão, por favor tente novamente.", "Ok");
        }


    }

    public void mensagemIntent(String titulo,String corpo,String botao)
    {
        AlertDialog.Builder mensagem = new AlertDialog.Builder(this);
        mensagem.setTitle(titulo);
        mensagem.setMessage(corpo);
        mensagem.setPositiveButton(botao,new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent it = new Intent(SimuladorResultado.this,NavigationDrawerActivity.class);
                startActivity(it);
                //para encerrar a activity atual e todos os parent
                finishAffinity();
            }
        });
        mensagem.show();
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
}
