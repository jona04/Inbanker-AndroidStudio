package br.com.appinbanker.inbanker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.DecimalFormat;

public class SimuladorResultado extends AppCompatActivity {

    double valor;
    String id,nome,vencimento,url_img;
    int dias;
    TextView tv_nome,tv_valor,tv_vencimento,tv_dias_pagamento,tv_juros_mes,tv_valor_total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_simulador_resultado);

        Intent it = getIntent();
        Bundle parametro = it.getExtras();
        id = parametro.getString("id");
        nome = parametro.getString("nome");
        valor = Double.parseDouble(parametro.getString("valor"));
        vencimento = parametro.getString("vencimento");
        dias = parametro.getInt("dias");
        url_img = parametro.getString("url_img");

        DecimalFormat decimal = new DecimalFormat( "#.00" );

        double juros_mensal = Double.parseDouble(decimal.format(valor * (0.00066333 * dias)));
        double taxa_fixa = Double.parseDouble(decimal.format(valor * 0.0099));

        double valor_total = juros_mensal + taxa_fixa + valor;

        ImageView img = (ImageView) findViewById(R.id.img_amigo);
        Picasso.with(getBaseContext()).load(url_img).into(img);

        tv_nome = (TextView) findViewById(R.id.nome_amigo);
        tv_dias_pagamento = (TextView) findViewById(R.id.tv_dias_pagamento);
        tv_juros_mes = (TextView) findViewById(R.id.tv_juros_mes);
        tv_valor = (TextView) findViewById(R.id.tv_valor);
        tv_valor_total = (TextView) findViewById(R.id.tv_valor_total);
        tv_vencimento = (TextView) findViewById(R.id.tv_vencimento);

        tv_nome.setText(nome);
        tv_dias_pagamento.setText(dias);
        tv_juros_mes.setText(String.valueOf(juros_mensal));
        tv_valor.setText(String.valueOf(valor));
        tv_valor_total.setText(String.valueOf(valor_total));
        tv_vencimento.setText(vencimento);


    }
}
