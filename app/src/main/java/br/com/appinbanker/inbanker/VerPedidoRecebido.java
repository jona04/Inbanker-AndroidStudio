package br.com.appinbanker.inbanker;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import br.com.appinbanker.inbanker.entidades.Transacao;
import br.com.appinbanker.inbanker.webservice.EditaTransacao;

public class VerPedidoRecebido extends AppCompatActivity {

    //status da transacao
    public static final int AGUARDANDO_RESPOSTA = 0;
    public static final int PEDIDO_RECUSADO = 1;
    public static final int PEDIDO_ACEITO = 2;

    private boolean aceitou_pedido = false;

    private String id,nome2,cpf1,cpf2,data_pedido = null,nome1,valor,vencimento,img1,img2;

    private TextView tv_valor,tv_data_pagamento,tv_juros_mes,tv_valor_total,tv_dias_corridos,msg_ver_pedido;

    private int status_transacao;

    private LinearLayout ll_resposta_pedido;

    private Button btn_aceita_pedido,btn_recusa_pedido;

    private TableRow tr_dias_corridos;

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
        if(dias_corridos >0)
            dias_corridos = dias_corridos -1;

        DecimalFormat decimal = new DecimalFormat( "0.00" );

        //verificamos se o usuario ja aceitou ou nao o pedido recebido para calcularmos o juros correto
        double juros_mensal = 0;
        switch (status_transacao){
            case AGUARDANDO_RESPOSTA:
                juros_mensal = Double.parseDouble(decimal.format(Double.parseDouble(valor) * (0.00066333 * dias)));
                msg_ver_pedido.setText(nome1+" esta lhe pedindo um emprestimo. Para aceitar ou recusar utilize os botoes abaixo.");
                break;
            case PEDIDO_ACEITO:
                juros_mensal = Double.parseDouble(decimal.format(Double.parseDouble(valor) * (0.00066333 * dias_corridos)));
                tr_dias_corridos.setVisibility(View.VISIBLE);
                break;
           /* case PEDIDO_RECUSADO: //esse pedido recusado deve estar somente no historico
                break;*/
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

                Transacao trans = new Transacao();
                trans.setId_trans(id);
                trans.setStatus_transacao("1");

                new EditaTransacao(trans,cpf1,cpf2,VerPedidoRecebido.this).execute();



            }
        });

        btn_aceita_pedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                aceitou_pedido = true;

                Transacao trans = new Transacao();
                trans.setId_trans(id);
                trans.setStatus_transacao("2");

                new EditaTransacao(trans,cpf1,cpf2,VerPedidoRecebido.this).execute();



            }
        });



    }

    public void retornoEditaTransacao(String result){
        Log.i("webservice","resultado edita transao = "+result);

        if(result.equals("sucesso_edit")){
            if(aceitou_pedido){
                mensagem("InBanker","Parabéns, voce aceitou o pedido. Ao efetuar o pagamento, peça que seu amigo(a) "+nome1+" confirme o recebimento do valor","Ok");

                Intent it = new Intent(VerPedidoRecebido.this,NavigationDrawerActivity.class);
                startActivity(it);
                //para encerrar a activity atual e todos os parent
                finishAffinity();
            }else{
                mensagem("InBanker","Voce recusou esse pedido de emprestimo de " + nome1, "Ok");

                Intent it = new Intent(VerPedidoRecebido.this,NavigationDrawerActivity.class);
                startActivity(it);
                //para encerrar a activity atual e todos os parent
                finishAffinity();
            }
        }else{
            mensagem("Houve um erro!","Ola, parece que tivemos algum problema de conexao, por favor tente novamente.","Ok");
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
}
