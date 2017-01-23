package br.com.appinbanker.inbanker;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Process;
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
import br.com.appinbanker.inbanker.webservice.EditaTransacaoResposta;
import br.com.appinbanker.inbanker.webservice.EnviaNotificacao;

public class VerPedidoEnviado extends AppCompatActivity implements WebServiceReturnUsuario,WebServiceReturnString {

    //private String id,nome2,cpf1,cpf2,data_pedido,nome1,valor,vencimento,img1,img2;

    //esse objeto ira receber a transacao atual, vinda da lista ou da notificacao
    private Transacao trans_atual;

    private TextView tv_data_pedido,tv_valor,tv_vencimento,tv_juros_mes,tv_valor_total,tv_dias_corridos,msg_ver_pedido;

    private int status_transacao;

    private LinearLayout ll_confirma_recebimento,ll_cancelar_pedido;

    //private TableRow tr_dias_corridos;

    private Button btn_confirma_recebimento,btn_cancelar_pedido_antes_receb, btn_cancelar_pedido_antes_resp;

    private ProgressBar progress_bar_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_ver_pedido_enviado);

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

        ll_confirma_recebimento = (LinearLayout) findViewById(R.id.ll_confirma_recebimento);
        ll_cancelar_pedido = (LinearLayout) findViewById(R.id.ll_cancelar_pedido);

        progress_bar_btn = (ProgressBar) findViewById(R.id.progress_bar_btn);

        btn_confirma_recebimento = (Button) findViewById(R.id.btn_confirma_recebimento);
        btn_cancelar_pedido_antes_receb = (Button) findViewById(R.id.btn_cancelar_pedido_antes_receb);
        btn_cancelar_pedido_antes_resp = (Button) findViewById(R.id.btn_cancelar_pedido_antes_resp);

        //tr_dias_corridos = (TableRow) findViewById(R.id.tr_dias_corridos);
        msg_ver_pedido = (TextView) findViewById(R.id.msg_ver_pedido);
        tv_valor = (TextView) findViewById(R.id.tv_valor);
        tv_data_pedido = (TextView) findViewById(R.id.tv_data_pedido);
        tv_vencimento = (TextView) findViewById(R.id.tv_vencimento);
        tv_juros_mes = (TextView) findViewById(R.id.tv_juros_mes);
        tv_valor_total = (TextView) findViewById(R.id.tv_valor_total);
        tv_dias_corridos = (TextView) findViewById(R.id.tv_dias_corridos);

    }


    public void configView(){

        //calculamos a diferença de dias entre a data atual ate a data do pedido para calcularmos o juros
        DateTimeFormatter fmt = DateTimeFormat.forPattern("dd/MM/YYYY");
        DateTime hoje = new DateTime();
        //DateTime data_pedido_parse = fmt.parseDateTime(data_pedido);
        DateTime vencimento_parse = fmt.parseDateTime(trans_atual.getVencimento());

        //calculamos o total de dias para mostramos na tela inicial antes do usuario-2 aceitar ou recusar o pedido recebido
        Days d = Days.daysBetween(hoje, vencimento_parse);
        int dias = d.getDays();

        //calculamos os dias corridos para calcularmos o juros do redimento atual
        //Days d_corridos = Days.daysBetween(data_pedido_parse, hoje);
        //int dias_corridos = d_corridos.getDays();

        //isso é para discontar 1 dia de juros, pois é dado prazo maximo de 1 dia para o usuario-2 aceitar o pedido
        //if(dias_corridos >0)
        //     dias_corridos = dias_corridos -1;

        //DecimalFormat decimal = new DecimalFormat( "0.00" );

        double juros_mensal = Double.parseDouble(trans_atual.getValor()) * (0.00066333 * dias);

        status_transacao = Integer.parseInt(trans_atual.getStatus_transacao());

        switch (status_transacao){
            case Transacao.AGUARDANDO_RESPOSTA:
                msg_ver_pedido.setText("Você esta aguardando o seu pedido de empréstimo ser respondido.");
                ll_cancelar_pedido.setVisibility(View.VISIBLE);
                break;
            case Transacao.PEDIDO_ACEITO:
                msg_ver_pedido.setText("Seu pedido de empréstimo foi aceito! Quando o valor solicitado estiver em suas mãos, você deve confirmar o recebimento do mesmo no botão abaixo, para dar continuidade a transação.");
                ll_confirma_recebimento.setVisibility(View.VISIBLE);
                break;
            /*case CONFIRMADO_RECEBIMENTO: //esse pedido confirmado deve estar em pagamentos pendentes
                juros_mensal = Double.parseDouble(decimal.format(Double.parseDouble(valor) * (0.00066333 * dias_corridos)));
                tr_dias_corridos.setVisibility(View.VISIBLE);
                break;*/
           /* case PEDIDO_RECUSADO: //esse pedido recusado deve estar somente no historico
                break;*/
        }

        double valor_total = juros_mensal +  Double.parseDouble(trans_atual.getValor());

        Locale ptBr = new Locale("pt", "BR");
        NumberFormat nf = NumberFormat.getCurrencyInstance(ptBr);
        String valor_formatado = nf.format (Double.parseDouble(trans_atual.getValor()));
        String juros_mensal_formatado = nf.format (juros_mensal);
        String valor_total_formatado = nf.format (valor_total);

        tv_valor.setText(valor_formatado);
        tv_data_pedido.setText(trans_atual.getDataPedido());
        tv_vencimento.setText(trans_atual.getVencimento());
        tv_juros_mes.setText(juros_mensal_formatado);
        tv_valor_total.setText(valor_total_formatado);

        btn_confirma_recebimento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Transacao trans = new Transacao();
                trans.setId_trans(trans_atual.getId_trans());
                trans.setStatus_transacao(String.valueOf(Transacao.CONFIRMADO_RECEBIMENTO));

                //esse valor sera passado para o metodo notificacao
                status_transacao = Transacao.CONFIRMADO_RECEBIMENTO;

                metodoEditaTrans(trans);

                progress_bar_btn.setVisibility(View.VISIBLE);
                btn_confirma_recebimento.setEnabled(false);
                btn_cancelar_pedido_antes_receb.setEnabled(false);

            }
        });

        btn_cancelar_pedido_antes_receb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //data do cancelamento
                DateTimeFormatter fmt = DateTimeFormat.forPattern("dd/MM/YYYY");
                DateTime hoje = new DateTime();
                String hoje_string = fmt.print(hoje);

                Transacao trans = new Transacao();
                trans.setId_trans(trans_atual.getId_trans());
                trans.setStatus_transacao(String.valueOf(Transacao.ENVIO_CANCELADO_ANTES_RECEBIMENTO));
                trans.setData_recusada(hoje_string);
                trans.setData_pagamento("");

                //esse valor sera passado para o metodo notificacao
                status_transacao = Transacao.ENVIO_CANCELADO_ANTES_RECEBIMENTO;

                metodoEditaTransResp(trans);

                progress_bar_btn.setVisibility(View.VISIBLE);
                btn_confirma_recebimento.setEnabled(false);
                btn_cancelar_pedido_antes_receb.setEnabled(false);
            }
        });

        btn_cancelar_pedido_antes_resp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //data do cancelamento
                DateTimeFormatter fmt = DateTimeFormat.forPattern("dd/MM/YYYY");
                DateTime hoje = new DateTime();
                String hoje_string = fmt.print(hoje);

                Transacao trans = new Transacao();
                trans.setId_trans(trans_atual.getId_trans());
                trans.setStatus_transacao(String.valueOf(Transacao.ENVIO_CANCELADO_ANTES_RESPOSTA));
                trans.setData_recusada(hoje_string);
                trans.setData_pagamento("");

                //esse valor sera passado para o metodo notificacao
                status_transacao = Transacao.ENVIO_CANCELADO_ANTES_RESPOSTA;

                metodoEditaTransResp(trans);

                progress_bar_btn.setVisibility(View.VISIBLE);
                btn_confirma_recebimento.setEnabled(false);
                btn_cancelar_pedido_antes_resp.setEnabled(false);

            }
        });

    }

    public void metodoEditaTrans(Transacao trans){
        new EditaTransacao(trans,trans_atual.getUsu1(),trans_atual.getUsu2(),this).execute();
    }

    public void metodoEditaTransResp(Transacao trans){
        new EditaTransacaoResposta(trans,trans_atual.getUsu1(),trans_atual.getUsu2(),this).execute();
    }

    public void retornoStringWebService(String result){

        Log.i("webservice","resultado edita transao = "+result);

        progress_bar_btn.setVisibility(View.GONE);
        btn_confirma_recebimento.setEnabled(true);

        if(result.equals("sucesso_edit")){

            //busca token do usuario 2 para enviarmos notificacao
            new BuscaUsuarioCPF(trans_atual.getUsu2(),this,this).execute();


        }else{
            mensagem("Houve um erro!","Olá, parece que tivemos algum problema de conexão, por favor tente novamente.","Ok");
        }

    }

    @Override
    public void retornoUsuarioWebService(Usuario usu) {

        Transacao trans = new Transacao();

        trans.setNome_usu1(trans_atual.getNome_usu1());
        trans.setNome_usu2(trans_atual.getNome_usu2());
        trans.setStatus_transacao(String.valueOf(status_transacao));
        trans.setId_trans(trans_atual.getId_trans());
        trans.setUsu1(trans_atual.getUsu1());
        trans.setUsu2(trans_atual.getUsu2());
        trans.setDataPedido(trans_atual.getDataPedido());
        trans.setValor(trans_atual.getValor());
        trans.setVencimento(trans_atual.getVencimento());
        trans.setUrl_img_usu1(trans_atual.getUrl_img_usu1());
        trans.setUrl_img_usu2(trans_atual.getUrl_img_usu2());


        //envia notificacao
        new EnviaNotificacao(trans,usu.getToken_gcm()).execute();

        if(status_transacao == Transacao.CONFIRMADO_RECEBIMENTO)
            mensagemIntent("InBanker","Parabéns, você confirmou o recebimento do valor solicitado. Ao efetuar o pagamento de quitação, peça que seu amigo(a) " + trans_atual.getNome_usu2() + " confirme o recebimento do valor.", "Ok");
        else if(status_transacao == Transacao.ENVIO_CANCELADO_ANTES_RESPOSTA)
            mensagemIntent("InBanker","Você acaba de cancelar o pedido que foi enviado ao seu amigo(a) " + trans_atual.getNome_usu2()+".", "Ok");
        else if(status_transacao == Transacao.ENVIO_CANCELADO_ANTES_RECEBIMENTO)
            mensagemIntent("InBanker","Você acaba de cancelar o pedido que foi enviado ao seu amigo(a) " + trans_atual.getNome_usu2()+".", "Ok");
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
                Intent it = new Intent(VerPedidoEnviado.this,NavigationDrawerActivity.class);
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
    public void retornoUsuarioWebServiceAuxInicioToken(Usuario usu){

    }
}
