package br.com.appinbanker.inbanker;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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

import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

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
import br.com.appinbanker.inbanker.interfaces.WebServiceReturnString;
import br.com.appinbanker.inbanker.interfaces.WebServiceReturnUsuario;
import br.com.appinbanker.inbanker.webservice.BuscaUsuarioCPF;
import br.com.appinbanker.inbanker.webservice.EditaTransacao;
import br.com.appinbanker.inbanker.webservice.EnviaNotificacao;

public class VerPagamentoPendente extends AppCompatActivity implements WebServiceReturnUsuario,WebServiceReturnString {

    //private String id,nome2,cpf1,cpf2,data_pedido,nome1,valor,vencimento,img1,img2;

    //esse objeto ira receber a transacao atual, vinda da lista ou da notificacao
    private Transacao trans_atual;

    private TextView tv_valor,tv_data_pedido,tv_vencimento,tv_juros_mes,tv_valor_total,tv_dias_corridos,msg_ver_pedido,tv_multa_atraso,tv_dias_atraso;

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
        it.getExtras();
        if(it.getSerializableExtra("transacao")!=null){
            trans_atual = (Transacao) it.getSerializableExtra("transacao");
            //Log.i("Script","valaor = "+trans.getNome_usu1());

            montaView();

            configView();

        }else{
            Log.i("Script","Nada vindo do parametro");
        }
    }

    public void montaView(){

        ImageView img = (ImageView) findViewById(R.id.img_amigo);

        try {
            Transformation transformation = new RoundedTransformationBuilder()
                    .borderColor(Color.GRAY)
                    .borderWidthDp(3)
                    .cornerRadiusDp(70)
                    .oval(false)
                    .build();
            Picasso.with(getBaseContext())
                    .load(trans_atual.getUrl_img_usu2())
                    .error(R.drawable.icon)
                    .transform(transformation)
                    .into(img);
        }catch (Exception e)
        {
            Log.i("Excpetion","Imagem pedido = "+ e);
        }

        TextView tv = (TextView) findViewById(R.id.nome_amigo);
        tv.setText(trans_atual.getNome_usu2());

        ll_confirma_quitacao = (LinearLayout) findViewById(R.id.ll_confirma_quitacao);

        progress_bar_btn = (ProgressBar) findViewById(R.id.progress_bar_btn);
        btn_confirma_quitacao = (Button) findViewById(R.id.btn_confirma_quitacao);
        msg_ver_pedido = (TextView) findViewById(R.id.msg_ver_pedido);
        tv_valor = (TextView) findViewById(R.id.tv_valor);
        tv_vencimento = (TextView) findViewById(R.id.tv_vencimento);
        tv_juros_mes = (TextView) findViewById(R.id.tv_juros_mes);
        tv_valor_total = (TextView) findViewById(R.id.tv_valor_total);
        tv_multa_atraso = (TextView) findViewById(R.id.tv_multa_atraso);
        tv_dias_atraso = (TextView) findViewById(R.id.tv_dias_atraso);
        tv_dias_corridos = (TextView) findViewById(R.id.tv_dias_corridos);
        tv_data_pedido = (TextView) findViewById(R.id.tv_data_pedido);
    }

    public void configView(){

        status_transacao = Integer.parseInt(trans_atual.getStatus_transacao());

        switch (status_transacao){
            case Transacao.CONFIRMADO_RECEBIMENTO:
                msg_ver_pedido.setText("Quando você realizar a quitação da dívida, aperte no botão abaixo para dar prosseguimento a transação.");
                break;
            case Transacao.QUITACAO_SOLICITADA:
                msg_ver_pedido.setText("Voce esta aguardando "+trans_atual.getNome_usu2()+" responder sua solicitaçao de quitaçao da divida.");
                ll_confirma_quitacao.setVisibility(View.GONE);
                break;
            case Transacao.RESP_QUITACAO_SOLICITADA_RECUSADA:
                msg_ver_pedido.setText(trans_atual.getNome_usu2()+" respondeu negativamente ao seu pedido de quitação da divida. Entre em contato com o mesmo e solicite novamente a quitação.");
                ll_confirma_quitacao.setVisibility(View.VISIBLE);
                break;

        }

        //utilizado para converter numeros em modelo monetario (Real)
        Locale ptBr = new Locale("pt", "BR");
        NumberFormat nf = NumberFormat.getCurrencyInstance(ptBr);

        //calculamos a diferença de dias entre a data atual ate a data do pedido para calcularmos o juros
        DateTimeFormatter fmt = DateTimeFormat.forPattern("dd/MM/YYYY");
        DateTime hoje = new DateTime();
        DateTime data_pedido_parse = fmt.parseDateTime(trans_atual.getDataPedido());

        double multa_atraso = 0;
        int dias_atraso = 0;
        DateTime data_vencimento_parse = fmt.parseDateTime(trans_atual.getVencimento());
        if(hoje.isAfter(data_vencimento_parse)){

            Days d_atraso = Days.daysBetween(data_vencimento_parse, hoje);
            dias_atraso = d_atraso.getDays();

            Log.i("PagamentoPendente","dias de atraso = "+dias_atraso);

            multa_atraso = Double.parseDouble(trans_atual.getValor())*0.1;
            tv_multa_atraso.setText(String.valueOf(nf.format(multa_atraso)));
            tv_dias_atraso.setText(String.valueOf(dias_atraso));

        }

        //calculamos o total de dias para mostramos na tela inicial antes do usuario-2 aceitar ou recusar o pedido recebido
        Days d = Days.daysBetween(data_pedido_parse, hoje);
        int dias_corridos = d.getDays();

        //isso é para discontar 1 dia de juros, pois é dado prazo maximo de 1 dia para o usuario-2 aceitar o pedido
        //if(dias_corridos >0)
        //     dias_corridos = dias_corridos -1;

        Double taxa_atraso = Double.parseDouble(trans_atual.getValor()) * (0.00066333 * dias_atraso);

        double juros_mensal = Double.parseDouble(trans_atual.getValor()) * (0.00066333 * dias_corridos);

        double valor_total = multa_atraso + juros_mensal +  Double.parseDouble(trans_atual.getValor());

        String valor_formatado = nf.format (Double.parseDouble(trans_atual.getValor()));
        String juros_mensal_formatado = nf.format (juros_mensal);
        String valor_total_formatado = nf.format (valor_total);

        tv_valor.setText(valor_formatado);
        tv_vencimento.setText(trans_atual.getVencimento());
        tv_juros_mes.setText(juros_mensal_formatado);
        tv_valor_total.setText(valor_total_formatado);
        tv_dias_corridos.setText(String.valueOf(dias_corridos));
        tv_data_pedido.setText(trans_atual.getDataPedido());

        btn_confirma_quitacao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Transacao trans = new Transacao();
                trans.setId_trans(trans_atual.getId_trans());
                trans.setStatus_transacao(String.valueOf(Transacao.QUITACAO_SOLICITADA));

                metodoEditaTrans(trans);

                progress_bar_btn.setVisibility(View.VISIBLE);
                btn_confirma_quitacao.setEnabled(false);

            }
        });


    }

    public void metodoEditaTrans(Transacao trans){
        new EditaTransacao(trans,trans_atual.getUsu1(),trans_atual.getUsu2(),this).execute();
    }

    public void retornoStringWebService(String result){

        Log.i("webservice","resultado edita transao = "+result);

        progress_bar_btn.setVisibility(View.GONE);
        btn_confirma_quitacao.setEnabled(true);

        if(result.equals("sucesso_edit")){

            //busca token do usuario 2 para enviar notificacao
            new BuscaUsuarioCPF(trans_atual.getUsu2(),VerPagamentoPendente.this,this).execute();


        }else{
            mensagem("Houve um erro!","Olá, parece que tivemos algum problema de conexão, por favor tente novamente.","Ok");
        }

    }

    @Override
    public void retornoUsuarioWebService(Usuario usu) {

        Transacao trans = new Transacao();
        trans.setNome_usu1(trans_atual.getNome_usu1());
        trans.setNome_usu2(trans_atual.getNome_usu2());
        trans.setId_trans(trans_atual.getId_trans());
        trans.setUsu1(trans_atual.getUsu1());
        trans.setUsu2(trans_atual.getUsu2());
        trans.setDataPedido(trans_atual.getDataPedido());
        trans.setValor(trans_atual.getValor());
        trans.setVencimento(trans_atual.getVencimento());
        trans.setUrl_img_usu1(trans_atual.getUrl_img_usu1());
        trans.setUrl_img_usu2(trans_atual.getUrl_img_usu2());

        trans.setStatus_transacao(String.valueOf(Transacao.QUITACAO_SOLICITADA));

        //envia notificacao
        new EnviaNotificacao(trans,usu.getToken_gcm()).execute();

        mensagemIntent("InBanker","Você realizou a solicitação de quitação do emprestimo. Peça que seu amigo(a) "+trans_atual.getNome_usu2()+" confirme o recebimento do valor.", "Ok");

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
    @Override
    public void retornoUsuarioWebServiceAuxInicioToken(Usuario usu){}
}
