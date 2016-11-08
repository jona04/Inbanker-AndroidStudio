package br.com.appinbanker.inbanker;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableRow;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import br.com.appinbanker.inbanker.entidades.Transacao;
import br.com.appinbanker.inbanker.entidades.Usuario;
import br.com.appinbanker.inbanker.util.AllSharedPreferences;
import br.com.appinbanker.inbanker.webservice.BuscaUsuarioCPF;
import br.com.appinbanker.inbanker.webservice.EditaTransacao;
import br.com.appinbanker.inbanker.webservice.EditaTransacaoResposta;
import br.com.appinbanker.inbanker.webservice.EnviaNotificacao;

public class VerPedidoRecebido extends AppCompatActivity {

    private boolean aceitou_pedido = false,resp_quitacao = false,confirmou_recebimento = false;

    private String id,nome2,cpf1,cpf2,data_pedido = null,nome1,valor,vencimento,img1,img2;

    private TextView tv_valor,tv_data_pagamento,tv_juros_mes,tv_valor_total,tv_dias_corridos,msg_ver_pedido;

    private int status_transacao;

    private LinearLayout ll_resposta_pedido,ll_confirma_recebimento_valor_emprestado;

    private Button btn_aceita_pedido,btn_recusa_pedido,btn_confirma_quitacao,btn_recusa_quitacao;

    private TableRow tr_dias_corridos;

    private ProgressBar progress_bar_btn;

    //utilizado para armazenar a data que o pedido foi recusado ou finalizado
    private String hoje_string;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_ver_pedido_recebido);

        //ativa o actionbar para dar a possibilidade de apertar em voltar para tela anterior
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent it = getIntent();
        Bundle parametro = it.getExtras();
        if(parametro!=null){
            id = parametro.getString("id");
            nome2 = parametro.getString("nome2");
            cpf1 = parametro.getString("cpf1");
            cpf2 = parametro.getString("cpf2");
            nome1 = parametro.getString("nome1");
            data_pedido = parametro.getString("data_pedido");
            valor = parametro.getString("valor");
            vencimento = parametro.getString("vencimento");
            img1 = parametro.getString("img1");
            img2 = parametro.getString("img2");
            status_transacao = Integer.parseInt(parametro.getString("status_transacao"));
            ///statusss
        }else{
            finish();
        }

        ImageView img = (ImageView) findViewById(R.id.img_amigo);
        Picasso.with(getBaseContext()).load(img1).into(img);

        /*TextView tv = (TextView) findViewById(R.id.nome_amigo);
        tv.setText(tv.getText().toString()+nome1);*/

        ll_resposta_pedido = (LinearLayout) findViewById(R.id.ll_resposta_pedido);
        ll_confirma_recebimento_valor_emprestado = (LinearLayout) findViewById(R.id.ll_confirma_recebimento_valor_emprestado);

        btn_confirma_quitacao = (Button) findViewById(R.id.btn_confirma_quitacao);
        btn_recusa_quitacao = (Button) findViewById(R.id.btn_recusa_quitacao);
        progress_bar_btn = (ProgressBar) findViewById(R.id.progress_bar_btn);
        tr_dias_corridos = (TableRow) findViewById(R.id.tr_dias_corridos);
        btn_aceita_pedido = (Button) findViewById(R.id.btn_aceita_pedido);
        btn_recusa_pedido = (Button) findViewById(R.id.btn_recusa_pedido);
        msg_ver_pedido = (TextView) findViewById(R.id.msg_ver_pedido);
        tv_valor = (TextView) findViewById(R.id.tv_valor);
        tv_data_pagamento = (TextView) findViewById(R.id.tv_data_pagamento);
        tv_juros_mes = (TextView) findViewById(R.id.tv_juros_mes);
        tv_valor_total = (TextView) findViewById(R.id.tv_valor_total);
        tv_dias_corridos = (TextView) findViewById(R.id.tv_dias_corridos);

        //calculamos a diferença de dias entre a data atual ate a data do pedido para calcularmos o juros
        DateTimeFormatter fmt = DateTimeFormat.forPattern("dd/MM/YYYY");
        DateTime hoje = new DateTime();
        DateTime data_pedido_parse = fmt.parseDateTime(data_pedido);
        DateTime vencimento_parse = fmt.parseDateTime(vencimento);

        //calculamos o total de dias para mostramos na tela inicial antes do usuario-2 aceitar ou recusar o pedido recebido
        Days d = Days.daysBetween(hoje, vencimento_parse);
        int dias = d.getDays();

        //calculamos os dias corridos para calcularmos o juros do redimento atual
        Days d_corridos = Days.daysBetween(data_pedido_parse, hoje);
        int dias_corridos = d_corridos.getDays();

        //isso é para discontar 1 dia de juros, pois é dado prazo maximo de 1 dia para o usuario-2 aceitar o pedido
        //if(dias_corridos >0)
        //    dias_corridos = dias_corridos -1;

        DecimalFormat decimal = new DecimalFormat( "0.00" );

        //verificamos se o usuario ja aceitou ou nao o pedido recebido para calcularmos o juros correto
        double juros_mensal = 0;
        switch (status_transacao){
            case Transacao.AGUARDANDO_RESPOSTA:
                juros_mensal = Double.parseDouble(valor) * (0.00066333 * dias);
                msg_ver_pedido.setText(nome1+" esta lhe pedindo um empréstimo. Para aceitar ou recusar utilize os botões abaixo.");
                break;
            case Transacao.PEDIDO_ACEITO:
                ll_resposta_pedido.setVisibility(View.GONE);
                msg_ver_pedido.setText("Você esta aguardando que seu amigo(a) " + nome1 + " confirme o recebimento do valor.");
                break;
            case Transacao.CONFIRMADO_RECEBIMENTO:
                ll_resposta_pedido.setVisibility(View.GONE);
                juros_mensal = Double.parseDouble(valor) * (0.00066333 * dias_corridos);
                tr_dias_corridos.setVisibility(View.VISIBLE);
                msg_ver_pedido.setText("Você esta aguardando que seu amigo(a) " + nome1 + " solicite a confirmação de quitação do empréstimo.");
                break;
            case Transacao.QUITACAO_SOLICITADA:
                ll_resposta_pedido.setVisibility(View.GONE);
                juros_mensal = Double.parseDouble(valor) * (0.00066333 * dias_corridos);
                tr_dias_corridos.setVisibility(View.VISIBLE);
                msg_ver_pedido.setText("Seu amigo(a) "+ nome1 +" esta solicitando que você confirme a quitação do valor que ele solicitou em empréstimo.");
                ll_confirma_recebimento_valor_emprestado.setVisibility(View.VISIBLE);
                break;
            case Transacao.RESP_QUITACAO_SOLICITADA_RECUSADA:
                ll_resposta_pedido.setVisibility(View.GONE);
                juros_mensal = Double.parseDouble(valor) * (0.00066333 * dias_corridos);
                tr_dias_corridos.setVisibility(View.VISIBLE);
                msg_ver_pedido.setText("Você já recusou uma confirmação de quitação dessa dívida com "+ nome1+". Agora está aguardando por outra solicitação de quitação.");
                break;
        }

        double valor_total = juros_mensal +  Double.parseDouble(valor);

        Locale ptBr = new Locale("pt", "BR");
        NumberFormat nf = NumberFormat.getCurrencyInstance(ptBr);
        String valor_formatado = nf.format (Double.parseDouble(valor));
        String juros_mensal_formatado = nf.format (juros_mensal);
        String valor_total_formatado = nf.format (valor_total);

        tv_valor.setText(valor_formatado);
        tv_data_pagamento.setText(vencimento);
        tv_dias_corridos.setText(String.valueOf(dias_corridos));
        tv_juros_mes.setText(juros_mensal_formatado);
        tv_valor_total.setText(valor_total_formatado);

        btn_recusa_pedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //data do cancelamento
                DateTimeFormatter fmt = DateTimeFormat.forPattern("dd/MM/YYYY");
                DateTime hoje = new DateTime();
                hoje_string = fmt.print(hoje);

                Transacao trans = new Transacao();
                trans.setId_trans(id);
                trans.setStatus_transacao(String.valueOf(Transacao.PEDIDO_RECUSADO));
                trans.setData_recusada(hoje_string);
                trans.setData_pagamento("");

                new EditaTransacaoResposta(trans,cpf1,cpf2,VerPedidoRecebido.this).execute();

                progress_bar_btn.setVisibility(View.VISIBLE);
                btn_recusa_pedido.setEnabled(false);
                btn_aceita_pedido.setEnabled(false);

            }
        });

        btn_aceita_pedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                aceitou_pedido = true;

                Transacao trans = new Transacao();
                trans.setId_trans(id);
                trans.setStatus_transacao(String.valueOf(Transacao.PEDIDO_ACEITO));

                new EditaTransacao(trans,cpf1,cpf2,VerPedidoRecebido.this,null,null).execute();

                progress_bar_btn.setVisibility(View.VISIBLE);
                btn_recusa_pedido.setEnabled(false);
                btn_aceita_pedido.setEnabled(false);

            }
        });

        btn_confirma_quitacao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //data de confirmacao de pagamento
                DateTimeFormatter fmt = DateTimeFormat.forPattern("dd/MM/YYYY");
                DateTime hoje = new DateTime();
                hoje_string = fmt.print(hoje);

                resp_quitacao = true;
                confirmou_recebimento = true;

                Transacao trans2 = new Transacao();
                trans2.setId_trans(id);
                trans2.setStatus_transacao(String.valueOf(Transacao.RESP_QUITACAO_SOLICITADA_CONFIRMADA));
                trans2.setData_recusada("");
                trans2.setData_pagamento(hoje_string);

                new EditaTransacaoResposta(trans2,cpf1,cpf2,VerPedidoRecebido.this).execute();

                progress_bar_btn.setVisibility(View.VISIBLE);
                btn_recusa_pedido.setEnabled(false);
                btn_aceita_pedido.setEnabled(false);
            }
        });

        btn_recusa_quitacao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                resp_quitacao = true;

                Transacao trans = new Transacao();
                trans.setId_trans(id);
                trans.setStatus_transacao(String.valueOf(Transacao.RESP_QUITACAO_SOLICITADA_RECUSADA));

                new EditaTransacao(trans,cpf1,cpf2,VerPedidoRecebido.this,null,null).execute();

                progress_bar_btn.setVisibility(View.VISIBLE);
                btn_recusa_pedido.setEnabled(false);
                btn_aceita_pedido.setEnabled(false);
            }
        });


    }

    public void retornoEditaTransacao(String result){
        Log.i("webservice","resultado edita transao = "+result);

        progress_bar_btn.setVisibility(View.GONE);
        btn_recusa_pedido.setEnabled(true);
        btn_aceita_pedido.setEnabled(true);

        if(result.equals("sucesso_edit")){

            //busca token do usuario 1
            new BuscaUsuarioCPF(cpf1,VerPedidoRecebido.this,null,null).execute();

        }else{
            mensagem("Houve um erro!","Olá, parece que tivemos algum problema de conexão, por favor tente novamente.","Ok");
        }

    }

    public void retornoBuscaTokenUsuario(Usuario usu){

        Transacao trans = new Transacao();
        trans.setNome_usu1(nome1);
        trans.setNome_usu2(nome2);

        trans.setId_trans(id);
        trans.setUsu1(cpf1);
        trans.setUsu2(cpf2);
        trans.setDataPedido(data_pedido);
        trans.setValor(valor);
        trans.setVencimento(vencimento);
        trans.setUrl_img_usu1(img1);
        trans.setUrl_img_usu2(img2);


        //verificamos qual foi o tipo de resposta - aceita pedidou ou confirma quitacao
        if(resp_quitacao) {


            if (confirmou_recebimento) {

                trans.setStatus_transacao(String.valueOf(Transacao.RESP_QUITACAO_SOLICITADA_CONFIRMADA));

                trans.setData_pagamento(hoje_string);

                //envia notificacao
                new EnviaNotificacao(trans,usu.getToken_gcm()).execute();

                mensagemIntent("InBanker", "Você confirmou o recebimento do valor para quitação do empréstimo solicitado por "+ nome1+". Parabéns, essa transacão foi finalizada com sucesso.", "Ok");
            } else {

                trans.setStatus_transacao(String.valueOf(Transacao.RESP_QUITACAO_SOLICITADA_RECUSADA));

                //envia notificacao
                new EnviaNotificacao(trans,usu.getToken_gcm()).execute();

                mensagemIntent("InBanker", "Você recusou uma solicitação de quitação da dívida. Entre em contato com "+nome1+" e aguarde por uma nova solicitação.","Ok");

            }
        }else{
            if (aceitou_pedido) {

                trans.setStatus_transacao(String.valueOf(Transacao.PEDIDO_ACEITO));

                //envia notificacao
                new EnviaNotificacao(trans,usu.getToken_gcm()).execute();

                mensagemIntent("InBanker", "Parabéns, você aceitou o pedido. Ao efetuar o pagamento, peça que seu amigo(a) " + nome1 + " confirme o recebimento do valor.", "Ok");
            } else {

                trans.setStatus_transacao(String.valueOf(Transacao.PEDIDO_RECUSADO));

                trans.setData_recusada(hoje_string);

                //envia notificacao
                new EnviaNotificacao(trans,usu.getToken_gcm()).execute();

                mensagemIntent("InBanker", "Você recusou esse pedido de empréstimo de " + nome1+".", "Ok");

            }
        }
    }

    public void mensagem(String titulo,String corpo,String botao)
    {
        AlertDialog.Builder mensagem = new AlertDialog.Builder(this);
        mensagem.setTitle(titulo);
        mensagem.setMessage(corpo);
        mensagem.setNeutralButton(botao,null);
        mensagem.show();
    }

    public void mensagemIntent(String titulo,String corpo,String botao)
    {
        AlertDialog.Builder mensagem = new AlertDialog.Builder(this);
        mensagem.setTitle(titulo);
        mensagem.setMessage(corpo);
        mensagem.setPositiveButton(botao,new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent it = new Intent(VerPedidoRecebido.this,NavigationDrawerActivity.class);
                startActivity(it);
                //para encerrar a activity atual e todos os parent
                finishAffinity();
            }
        });
        mensagem.show();
    }

    @Override
    public void onBackPressed()
    {
        // code here to show dialog
        super.onBackPressed();  // optional depending on your needs

        Log.i("Script","onBackPressed");

        if(!isActivityRunning(NavigationDrawerActivity.class)){
            Intent it = new Intent(this,NavigationDrawerActivity.class);
            startActivity(it);
        }

    }

    protected Boolean isActivityRunning(Class activityClass)
    {
        ActivityManager activityManager = (ActivityManager) getBaseContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

        for (ActivityManager.RunningTaskInfo task : tasks) {
            if (activityClass.getCanonicalName().equalsIgnoreCase(task.baseActivity.getClassName()))
                return true;
        }

        return false;
    }


}
