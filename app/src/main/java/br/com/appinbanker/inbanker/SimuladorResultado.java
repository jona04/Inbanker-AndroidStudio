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
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import br.com.appinbanker.inbanker.entidades.Transacao;
import br.com.appinbanker.inbanker.entidades.Usuario;
import br.com.appinbanker.inbanker.fragments_navigation.PedirEmprestimoFragment;
import br.com.appinbanker.inbanker.interfaces.WebServiceReturnUsuario;
import br.com.appinbanker.inbanker.sqlite.BancoControllerUsuario;
import br.com.appinbanker.inbanker.sqlite.CriandoBanco;
import br.com.appinbanker.inbanker.util.AllSharedPreferences;
import br.com.appinbanker.inbanker.util.Validador;
import br.com.appinbanker.inbanker.webservice.AddTransacao;
import br.com.appinbanker.inbanker.webservice.AddUsuario;
import br.com.appinbanker.inbanker.webservice.AtualizaTokenGcm;
import br.com.appinbanker.inbanker.webservice.AtualizaUsuario;
import br.com.appinbanker.inbanker.webservice.BuscaUsuarioFace;
import br.com.appinbanker.inbanker.webservice.EnviaNotificacao;
import br.com.appinbanker.inbanker.webservice.VerificaUsuarioCadastro;

public class SimuladorResultado extends AppCompatActivity implements WebServiceReturnUsuario {

    double valor;
    String id,nome,vencimento,url_img;
    int dias;
    TextView tv_nome,tv_valor,tv_vencimento,tv_dias_pagamento,tv_juros_mes,tv_valor_total,tv_valor_juros;
    Transacao trans;
    ProgressBar progress_bar_simulador;
    Button btn_fazer_pedido;

    FloatingActionButton fab_cpf,fab_senha;

    String token_user2;

    //dialog cadastearr
    private Dialog dialog_cadastro;
    private EditText et_dialog_cpf;
    private  EditText et_dialog_senha;
    private Button btn_dialog_cancelar;
    private Button btn_dialog_ok;
    private ProgressBar progress_bar_dialog_cadastro;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_simulador_resultado);

        trans = new Transacao();

        progress_bar_simulador = (ProgressBar) findViewById(R.id.progress_bar_simulador);

        Intent it = getIntent();
        Bundle parametro = it.getExtras();
        id = parametro.getString("id");
        nome = parametro.getString("nome");
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
        //tv_valor_servico = (TextView) findViewById(R.id.tv_valor_servico);

        btn_fazer_pedido = (Button) findViewById(R.id.btn_fazer_pedido);

        Locale ptBr = new Locale("pt", "BR");
        NumberFormat nf = NumberFormat.getCurrencyInstance(ptBr);
        String valor_formatado = nf.format (valor);
        //String taxa_fixa_formatado = nf.format (taxa_fixa);
        String juros_mensal_formatado = nf.format (juros_mensal);
        String valor_total_formatado = nf.format (valor_total);

       // tv_valor_servico.setText(taxa_fixa_formatado);
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
                //se usuario nao tiver o cpf cadastrado, mostramos o dialog para cadastrar, se nao continuamos o pedido mostramos o dialog de senha
                if(cpf.equals("")){

                    // custom dialog
                    dialog_cadastro = new Dialog(SimuladorResultado.this);
                    dialog_cadastro.setContentView(R.layout.dialog_cadastro_cpf);
                    dialog_cadastro.setTitle("Finalização de cadastro");

                    et_dialog_cpf = (EditText) dialog_cadastro.findViewById(R.id.et_dialog_cpf);
                    et_dialog_senha = (EditText) dialog_cadastro.findViewById(R.id.et_dialog_senha);
                    btn_dialog_cancelar = (Button) dialog_cadastro.findViewById(R.id.btn_dialog_cancelar);
                    btn_dialog_ok = (Button) dialog_cadastro.findViewById(R.id.btn_dialog_ok);
                    progress_bar_dialog_cadastro = (ProgressBar) dialog_cadastro.findViewById(R.id.progress_bar_dialog_cadastro);

                    fab_cpf = (FloatingActionButton) dialog_cadastro.findViewById(R.id.fab_cpf);
                    fab_cpf.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mensagem("Por que precisam do meu CPF?","Seu CPF é necessario para atendermos as normas Brasileiras. Fique tranquilo, seus dados estão protegidos e ninguém tem acesso a eles." ,"OK");
                        }
                    });
                    fab_senha = (FloatingActionButton) dialog_cadastro.findViewById(R.id.fab_senha);
                    fab_senha.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mensagem("Para que serve essa senha?","Para sua maior segurança, essa senha será exigida sempre que você for realizar qualquer transação no Inbanker." ,"OK");
                        }
                    });

                    btn_dialog_ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.i("Script", "Clicou em cadastrar");

                            boolean campos_ok = true;

                            boolean senha_valido = Validador.validateNotNull(et_dialog_senha.getText().toString());
                            if(!senha_valido) {
                                et_dialog_senha.setError("Campo vazio");
                                et_dialog_senha.setFocusable(true);
                                et_dialog_senha.requestFocus();

                                campos_ok = false;
                            }

                            boolean cpf_valido = Validador.isCPF(et_dialog_cpf.getText().toString());
                            if(!cpf_valido) {
                                et_dialog_cpf.setError("CPF inválido");
                                et_dialog_cpf.setFocusable(true);
                                et_dialog_cpf.requestFocus();

                                campos_ok = false;
                            }

                            if(campos_ok) {

                                btn_dialog_ok.setEnabled(false);
                                btn_dialog_cancelar.setEnabled(false);
                                progress_bar_dialog_cadastro.setVisibility(View.VISIBLE);

                                new VerificaUsuarioCadastro(et_dialog_cpf.getText().toString(), SimuladorResultado.this).execute();
                            }
                        }
                    });
                    // if button is clicked, close the custom dialog
                    btn_dialog_cancelar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog_cadastro.dismiss();
                        }
                    });

                    dialog_cadastro.show();

                }else {
                    // custom dialog
                    final Dialog dialog = new Dialog(SimuladorResultado.this);
                    dialog.setContentView(R.layout.dialog_senha);
                    dialog.setTitle("Informe sua senha");


                    final EditText et_senha = (EditText) dialog.findViewById(R.id.et_dialog_senha);
                    final TextView msg_dialog = (TextView) dialog.findViewById(R.id.msg_dialog);
                    Button btn_dialog_cancelar = (Button) dialog.findViewById(R.id.btn_dialog_cancelar);
                    Button btn_dialog_ok = (Button) dialog.findViewById(R.id.btn_dialog_ok);

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

                                dialog.dismiss();

                                progress_bar_simulador.setVisibility(View.VISIBLE);
                                btn_fazer_pedido.setEnabled(false);
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

    public void buscaUsuarioFace() {
        new BuscaUsuarioFace(id, SimuladorResultado.this, this).execute();
    }

    public void retornoTaskVerificaCadastro(String result){

        if(result == null){

            //atualizamos os dados do usuario que esta no sqlite com os dados dele que acabaram de ser logados no facebook
            BancoControllerUsuario crud = new BancoControllerUsuario(this);
            Cursor cursor = crud.carregaDados();
            String id_face = cursor.getString(cursor.getColumnIndexOrThrow(CriandoBanco.ID_FACE));
            crud.alteraRegistroCpf(id_face,et_dialog_cpf.getText().toString(),et_dialog_senha.getText().toString());

            Usuario usu = new Usuario();

            //para nao da problema botamos vazio nos demais campos
            usu.setNome("");
            usu.setEmail("");
            usu.setSenha(et_dialog_senha.getText().toString());

            usu.setCpf(et_dialog_cpf.getText().toString());
            usu.setUrlImgFace("");
            usu.setNomeFace("");
            usu.setIdFace(id_face);

            //fazemos a chamada a classe responsavel por realizar a tarefa de webservice em doinbackground
            new AtualizaUsuario(usu,SimuladorResultado.this).execute();

        }else {

            progress_bar_dialog_cadastro.setVisibility(View.INVISIBLE);
            btn_dialog_ok.setEnabled(true);
            btn_dialog_cancelar.setEnabled(true);

            //verificamos o resultado da verificação
            if (result.equals("cpf"))
                mensagem("Houve um erro!", "Olá, o CPF informado já existe, por favor informe outro.", "Ok");
        }
    }

    public void retornoAtualizaUsuario(String result){

        Log.i("Webservice","retorno = "+result);

        if(result.equals("sucesso_edit")){
            dialog_cadastro.dismiss();
            mensagem("InBanker", "Seus dados foram atualizados com sucesso.", "Ok");
        }else{
            mensagem("Houve um erro!", "Olá, parece que tivemos algum problema de conexão, por favor tente novamente.", "Ok");
        }

    }

    @Override
    public void retornoUsuarioWebService(Usuario usu){

        if(usu != null) {

            //pegamos o token do usuario para usar na notificação
            token_user2 = usu.getToken_gcm();

            //data do pedido
            DateTimeFormatter fmt = DateTimeFormat.forPattern("dd/MM/YYYY");
            DateTime hoje = new DateTime();
            final String hoje_string = fmt.print(hoje);

            BancoControllerUsuario crud = new BancoControllerUsuario(getBaseContext());
            Cursor cursor = crud.carregaDados();

            //adicionamos a trasacao em ambas as contas
            trans.setDataPedido(hoje_string);
            trans.setUsu1(cursor.getString(cursor.getColumnIndexOrThrow(CriandoBanco.CPF)));
            trans.setUsu2(usu.getCpf());
            trans.setValor(String.valueOf(valor));
            trans.setVencimento(vencimento);
            trans.setNome_usu2(nome);
            trans.setUrl_img_usu2(url_img);
            trans.setNome_usu1(cursor.getString(cursor.getColumnIndexOrThrow(CriandoBanco.NOME_FACE)));
            trans.setUrl_img_usu1(cursor.getString(cursor.getColumnIndexOrThrow(CriandoBanco.URL_IMG_FACE)));
            trans.setStatus_transacao(String.valueOf(Transacao.AGUARDANDO_RESPOSTA));
            //enviamos null para criamos um id aleatorio
            trans.setId_trans(null);

            new AddTransacao(trans,SimuladorResultado.this).execute();

        }else{
            mensagem("Houve um erro!","Olá, parece que houve um problema de conexao. Favor tente novamente!","OK");

            //habilitamos novamente o botao de fazer pedido e tiramos da tela o progress bar
            progress_bar_simulador.setVisibility(View.GONE);
            btn_fazer_pedido.setEnabled(true);

        }


    }

    public void retornoAddTransacao(String result){

        //habilitamos novamente o botao de fazer pedido e tiramos da tela o progress bar
        progress_bar_simulador.setVisibility(View.GONE);
        btn_fazer_pedido.setEnabled(true);

        if(result.equals("sucesso_edit")){


            //envia notificação
            new EnviaNotificacao(trans,token_user2).execute();

            //aletar e redirecionamento para tela inicial
            mensagemIntent("InBanker", "Pedido enviado, aguarde a resposta de seu amigo(a) "+nome, "Ok");
        }else{
            mensagem("Houve um erro!","Parece que houve um erro de conexão, por favor tente novamente.","Ok");
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
}
