package br.com.appinbanker.inbanker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class VerPedidoEnviado extends AppCompatActivity {

    //status da transacao
    public static final int AGUARDANDO_RESPOSTA = 0;
    public static final int PEDIDO_ACEITO = 1;
    public static final int PEDIDO_RECUSADO = 2;

    private String id,nome2,cpf1,cpf2,data_pedido = null,nome1,valor,vencimento,img1,img2;

    private TextView tv_valor,tv_data_pagamento,tv_juros_mes,tv_valor_total,tv_dias_corridos,msg_ver_pedido;

    private int status_transacao;

    private LinearLayout ll_confirma_recebimento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_ver_pedido_enviado);

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

        ll_confirma_recebimento = (LinearLayout) findViewById(R.id.ll_confirma_recebimento);

        msg_ver_pedido = (TextView) findViewById(R.id.msg_ver_pedido);
        tv_valor = (TextView) findViewById(R.id.tv_valor);
        tv_data_pagamento = (TextView) findViewById(R.id.tv_data_pagamento);
        tv_juros_mes = (TextView) findViewById(R.id.tv_juros_mes);
        tv_valor_total = (TextView) findViewById(R.id.tv_valor_total);
        tv_dias_corridos = (TextView) findViewById(R.id.tv_dias_corridos);


        switch (status_transacao){
            case AGUARDANDO_RESPOSTA:
                msg_ver_pedido.setText("Voce esta aguardando o seu pedido de emprestimo ser respondido");
                break;
            case PEDIDO_ACEITO:
                msg_ver_pedido.setText("Seu pedido de emprestimo foi aceito! Quando o valor solicitado estiver em suas maos, voce deve confirmar o recebimento do mesmo no botao abaixo, para dar continuidade a transaçao.");
                ll_confirma_recebimento.setVisibility(View.VISIBLE);
                break;
           /* case PEDIDO_RECUSADO: //esse pedido recusado deve estar somente no historico
                break;*/
        }

        //calculamos a diferença de dias entre a data atual ate a data do pedido para calcularmos o juros
        DateTimeFormatter fmt = DateTimeFormat.forPattern("dd/MM/YYYY");
        DateTime hoje = new DateTime();
        DateTime data_pedido_parse = fmt.parseDateTime(data_pedido);
        Days d = Days.daysBetween(data_pedido_parse, hoje);
        int dias = d.getDays();

        //isso é para discontar 1 dia de juros, pois é dado prazo maximo de 1 dia para o usuario-2 aceitar o pedido
        if(dias >0)
            dias = dias -1;

        DecimalFormat decimal = new DecimalFormat( "0.00" );

        double juros_mensal = Double.parseDouble(decimal.format(Double.parseDouble(valor) * (0.00066333 * dias)));
        //double taxa_fixa = Double.parseDouble(decimal.format(valor * 0.0099));

        double valor_total = juros_mensal +  Double.parseDouble(valor);

        Locale ptBr = new Locale("pt", "BR");
        NumberFormat nf = NumberFormat.getCurrencyInstance(ptBr);
        String valor_formatado = nf.format (Double.parseDouble(valor));
        String juros_mensal_formatado = nf.format (juros_mensal);
        String valor_total_formatado = nf.format (valor_total);

        tv_valor.setText(valor_formatado);
        tv_data_pagamento.setText(vencimento);
        tv_dias_corridos.setText(String.valueOf(dias));
        tv_juros_mes.setText(juros_mensal_formatado);
        tv_valor_total.setText(valor_total_formatado);
    }
}
