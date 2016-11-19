package br.com.appinbanker.inbanker;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import br.com.appinbanker.inbanker.fragments_navigation.HistoricoFragment;
import br.com.appinbanker.inbanker.sqlite.BancoControllerUsuario;
import br.com.appinbanker.inbanker.sqlite.CriandoBanco;
import br.com.appinbanker.inbanker.webservice.BuscaUsuarioCPF;

public class VerHistorico extends AppCompatActivity {

    private String id,nome2,cpf1,cpf2,data_pedido = null,nome1,valor,vencimento,img1,img2,data_cancelamento,data_pagamento;

    private TextView tv_valor,tv_data_vencimento,tv_data_cancelamento,tv_data_pagamento,tv_juros_mes,tv_valor_total,tv_data_pedido;

    private TableRow tr_cancelado,tr_pagamento;

    private int status_transacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_ver_historico);

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
            data_cancelamento = parametro.getString("data_cancelamento");
            data_pagamento = parametro.getString("data_pagamento");
            status_transacao = Integer.parseInt(parametro.getString("status_transacao"));
            ///statusss
        }else{
            finish();
        }

        BancoControllerUsuario crud = new BancoControllerUsuario(this);
        Cursor cursor = crud.carregaDados();
        String cpf = cursor.getString(cursor.getColumnIndexOrThrow(CriandoBanco.CPF));

        ImageView img = (ImageView) findViewById(R.id.img_amigo);
        TextView tv = (TextView) findViewById(R.id.nome_amigo);
        //identicamos qual é o usuario 1 e 2 na transacao, para exibir os dados corretoa na tela
        if(cpf.equals(cpf1)) {
            Picasso.with(getBaseContext()).load(img2).into(img);
            tv.setText("Você enviou esse pedido para "+tv.getText().toString() + nome2);
        }else {
            Picasso.with(getBaseContext()).load(img1).into(img);
            tv.setText("Você recebeu esse pedido de "+tv.getText().toString() + nome1);
        }



        tr_pagamento = (TableRow) findViewById(R.id.tr_pagamento);
        tr_cancelado = (TableRow) findViewById(R.id.tr_cancelado);
        tv_valor = (TextView) findViewById(R.id.tv_valor);
        tv_data_vencimento= (TextView) findViewById(R.id.tv_data_vencimento);
        tv_data_pedido= (TextView) findViewById(R.id.tv_data_pedido);
        tv_data_cancelamento= (TextView) findViewById(R.id.tv_data_cancelamento);
        tv_data_pagamento= (TextView) findViewById(R.id.tv_data_pagamento);
        tv_juros_mes= (TextView) findViewById(R.id.tv_juros_mes);
        tv_valor_total= (TextView) findViewById(R.id.tv_valor_total);

        Locale ptBr = new Locale("pt", "BR");
        NumberFormat nf = NumberFormat.getCurrencyInstance(ptBr);
        String valor_formatado = nf.format (Double.parseDouble(valor));
        //String juros_mensal_formatado = nf.format (juros_mensal);
        //String valor_total_formatado = nf.format (valor_total);

        //se o tamanho da variavel data for maior que 5 é por que existe uma data registrada
        if(data_cancelamento.length() > 5)
            tr_cancelado.setVisibility(View.VISIBLE);

        if(data_pagamento.length() > 5) {
            tr_pagamento.setVisibility(View.VISIBLE);
            //Log.i("webservice","data pagamento"+data_pagamento);
        }

        tv_valor.setText(valor_formatado);
        tv_data_vencimento.setText(vencimento);
        tv_data_cancelamento.setText(data_cancelamento);
        tv_data_pedido.setText(data_pedido);
        tv_data_pagamento.setText(data_pagamento);
        //tv_valor_total.setText(valor_total_formatado);

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
