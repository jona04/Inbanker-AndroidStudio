package br.com.appinbanker.inbanker;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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
import br.com.appinbanker.inbanker.fragments_navigation.HistoricoFragment;
import br.com.appinbanker.inbanker.sqlite.BancoControllerUsuario;
import br.com.appinbanker.inbanker.sqlite.CriandoBanco;
import br.com.appinbanker.inbanker.webservice.BuscaUsuarioCPF;

public class VerHistorico extends AppCompatActivity {

    //private String id,nome2,cpf1,cpf2,data_pedido = null,nome1,valor,vencimento,img1,img2,data_cancelamento,data_pagamento;

    //esse objeto ira receber a transacao atual, vinda da lista ou da notificacao
    private Transacao trans_atual;

    private TextView tv_valor,tv_data_historico,tv_vencimento,tv_juros_mes,tv_valor_total,tv_data_pedido,tv_dias_corridos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_ver_historico);

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

        BancoControllerUsuario crud = new BancoControllerUsuario(this);
        Cursor cursor = crud.carregaDados();
        String cpf = cursor.getString(cursor.getColumnIndexOrThrow(CriandoBanco.CPF));

        ImageView img = (ImageView) findViewById(R.id.img_amigo);
        TextView tv = (TextView) findViewById(R.id.nome_amigo);

        Transformation transformation = new RoundedTransformationBuilder()
                .borderColor(Color.GRAY)
                .borderWidthDp(3)
                .cornerRadiusDp(70)
                .oval(false)
                .build();

        //identicamos qual é o usuario 1 e 2 na transacao, para exibir os dados corretoa na tela
        if(cpf.equals(trans_atual.getUsu1())) {
            Picasso.with(getBaseContext())
                    .load(trans_atual.getUrl_img_usu2())
                    .transform(transformation)
                    .into(img);
            tv.setText(trans_atual.getNome_usu2());
        }else {
            Picasso.with(getBaseContext())
                    .load(trans_atual.getUrl_img_usu1())
                    .transform(transformation)
                    .into(img);
            tv.setText(trans_atual.getNome_usu1());
        }

        tv_valor = (TextView) findViewById(R.id.tv_valor);
        tv_vencimento= (TextView) findViewById(R.id.tv_vencimento);
        tv_data_pedido= (TextView) findViewById(R.id.tv_data_pedido);
        tv_data_historico= (TextView) findViewById(R.id.tv_data_historico);
        tv_juros_mes= (TextView) findViewById(R.id.tv_juros_mes);
        tv_valor_total= (TextView) findViewById(R.id.tv_valor_total);
        tv_dias_corridos = (TextView) findViewById(R.id.tv_dias_corridos);

    }

    public void configView(){

        //calculamos a diferença de dias entre a data do pedido ate a data de finalizacao do pedido para calcularmos o juros
        DateTimeFormatter fmt = DateTimeFormat.forPattern("dd/MM/YYYY");
        DateTime data_pedido_parse = fmt.parseDateTime(trans_atual.getDataPedido());
        DateTime data_finalizado;

        int dias_corridos = 0;

        Locale ptBr = new Locale("pt", "BR");
        NumberFormat nf = NumberFormat.getCurrencyInstance(ptBr);

        //se o tamanho da variavel data for maior que 5 é por que existe uma data registrada
        if(trans_atual.getData_recusada() != null) {
            if(trans_atual.getData_recusada().length() > 5) {
                tv_data_historico.setText("Data cancelamento");
                tv_vencimento.setText(trans_atual.getData_recusada());
                data_finalizado = fmt.parseDateTime(trans_atual.getData_recusada());

                Days d = Days.daysBetween(data_pedido_parse, data_finalizado);
                dias_corridos = d.getDays();
            }
        }
        if(trans_atual.getData_pagamento() != null) {
            if(trans_atual.getData_pagamento().length() > 5) {
                tv_data_historico.setText("Data quitação");
                tv_vencimento.setText(trans_atual.getData_pagamento());
                data_finalizado = fmt.parseDateTime(trans_atual.getData_pagamento());

                Days d = Days.daysBetween(data_pedido_parse, data_finalizado);
                dias_corridos = d.getDays();
            }
        }

        double juros_mensal = Double.parseDouble(trans_atual.getValor()) * (0.00066333 * dias_corridos);
        double valor_total = juros_mensal +  Double.parseDouble(trans_atual.getValor());

        String valor_total_formatado = nf.format (valor_total);
        String valor_formatado = nf.format (Double.parseDouble(trans_atual.getValor()));

        tv_dias_corridos.setText(String.valueOf(dias_corridos));
        tv_valor.setText(valor_formatado);
        tv_valor_total.setText(valor_total_formatado);
        tv_data_pedido.setText(trans_atual.getDataPedido());
        tv_juros_mes.setText(nf.format (juros_mensal));

    }

    @Override
    public void onBackPressed()
    {

        Log.i("Script","onBackPressed");

        if(!isActivityRunning(NavigationDrawerActivity.class)){
            Intent it = new Intent(this,NavigationDrawerActivity.class);
            startActivity(it);
        }
        // code here to show dialog
        super.onBackPressed();  // optional depending on your needs

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
