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
import br.com.appinbanker.inbanker.webservice.BuscaUsuarioCPF;
import br.com.appinbanker.inbanker.webservice.EditaTransacao;
import br.com.appinbanker.inbanker.webservice.EnviaNotificacao;

public class VerPagamentoPendente extends AppCompatActivity {

    private String id,nome2,cpf1,cpf2,data_pedido,nome1,valor,vencimento,img1,img2;

    private TextView tv_valor,tv_data_pagamento,tv_juros_mes,tv_valor_total,tv_dias_corridos,msg_ver_pedido;

    private int status_transacao;

    private LinearLayout ll_confirma_quitacao;

    private Button btn_confirma_quitacao;

    private ProgressBar progress_bar_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_ver_pagamento_pendente);

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
        Picasso.with(getBaseContext()).load(img2).into(img);

        TextView tv = (TextView) findViewById(R.id.nome_amigo);
        tv.setText(tv.getText().toString()+nome2);

        ll_confirma_quitacao = (LinearLayout) findViewById(R.id.ll_confirma_quitacao);

        progress_bar_btn = (ProgressBar) findViewById(R.id.progress_bar_btn);
        btn_confirma_quitacao = (Button) findViewById(R.id.btn_confirma_quitacao);
        msg_ver_pedido = (TextView) findViewById(R.id.msg_ver_pedido);
        tv_valor = (TextView) findViewById(R.id.tv_valor);
        tv_data_pagamento = (TextView) findViewById(R.id.tv_data_pagamento);
        tv_juros_mes = (TextView) findViewById(R.id.tv_juros_mes);
        tv_valor_total = (TextView) findViewById(R.id.tv_valor_total);
        tv_dias_corridos = (TextView) findViewById(R.id.tv_dias_corridos);

        switch (status_transacao){
            case Transacao.CONFIRMADO_RECEBIMENTO:
                msg_ver_pedido.setText("Quando você realizar a quitação da dívida, aperte no botão abaixo para dar prosseguimento a transação.");
                break;
            case Transacao.QUITACAO_SOLICITADA:
                msg_ver_pedido.setText("Voce esta aguardando "+nome2+" responder sua solicitaçao de quitaçao da divida.");
                ll_confirma_quitacao.setVisibility(View.GONE);
                break;
            case Transacao.RESP_QUITACAO_SOLICITADA_RECUSADA:
                msg_ver_pedido.setText(nome2+" respondeu negativamente ao seu pedido de quitação da divida. Entre em contato com o mesmo e solicite novamente a quitação.");
                ll_confirma_quitacao.setVisibility(View.VISIBLE);
                break;

        }

        //calculamos a diferença de dias entre a data atual ate a data do pedido para calcularmos o juros
        DateTimeFormatter fmt = DateTimeFormat.forPattern("dd/MM/YYYY");
        DateTime hoje = new DateTime();
        DateTime data_pedido_parse = fmt.parseDateTime(data_pedido);

        //calculamos o total de dias para mostramos na tela inicial antes do usuario-2 aceitar ou recusar o pedido recebido
        Days d = Days.daysBetween(data_pedido_parse, hoje);
        int dias_corridos = d.getDays();

        //isso é para discontar 1 dia de juros, pois é dado prazo maximo de 1 dia para o usuario-2 aceitar o pedido
        //if(dias_corridos >0)
        //     dias_corridos = dias_corridos -1;

        DecimalFormat decimal = new DecimalFormat( "0.00" );
        double juros_mensal = Double.parseDouble(valor) * (0.00066333 * dias_corridos);

        double valor_total = juros_mensal +  Double.parseDouble(valor);

        Locale ptBr = new Locale("pt", "BR");
        NumberFormat nf = NumberFormat.getCurrencyInstance(ptBr);
        String valor_formatado = nf.format (Double.parseDouble(valor));
        String juros_mensal_formatado = nf.format (juros_mensal);
        String valor_total_formatado = nf.format (valor_total);

        tv_valor.setText(valor_formatado);
        tv_data_pagamento.setText(vencimento);
        tv_juros_mes.setText(juros_mensal_formatado);
        tv_valor_total.setText(valor_total_formatado);
        tv_dias_corridos.setText(String.valueOf(dias_corridos));

        btn_confirma_quitacao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Transacao trans = new Transacao();
                trans.setId_trans(id);
                trans.setStatus_transacao(String.valueOf(Transacao.QUITACAO_SOLICITADA));

                new EditaTransacao(trans,cpf1,cpf2,null,null,VerPagamentoPendente.this).execute();

                progress_bar_btn.setVisibility(View.VISIBLE);
                btn_confirma_quitacao.setEnabled(false);

            }
        });

    }

    public void retornoEditaTransacao(String result){

        //Log.i("webservice","resultado edita transao = "+result);

        progress_bar_btn.setVisibility(View.GONE);
        btn_confirma_quitacao.setEnabled(true);

        if(result.equals("sucesso_edit")){

            //busca token do usuario 2
            new BuscaUsuarioCPF(cpf2,null,null,VerPagamentoPendente.this).execute();


        }else{
            mensagem("Houve um erro!","Olá, parece que tivemos algum problema de conexão, por favor tente novamente.","Ok");
        }

    }

    public void retornoBuscaTokenUsuario(Usuario usu) {

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

        trans.setStatus_transacao(String.valueOf(Transacao.QUITACAO_SOLICITADA));

        //envia notificacao
        new EnviaNotificacao(trans,usu.getToken_gcm()).execute();

        mensagemIntent("InBanker","Você realizou a solicitação de quitação do emprestimo. Peça que seu amigo(a) "+nome2+" confirme o recebimento do valor.", "Ok");

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
                Intent it = new Intent(VerPagamentoPendente.this,NavigationDrawerActivity.class);
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
