package br.com.appinbanker.inbanker;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import br.com.appinbanker.inbanker.entidades.Transacao;
import br.com.appinbanker.inbanker.entidades.Usuario;
import br.com.appinbanker.inbanker.sqlite.BancoControllerUsuario;
import br.com.appinbanker.inbanker.sqlite.CriandoBanco;
import br.com.appinbanker.inbanker.webservice.AddTransacao;
import br.com.appinbanker.inbanker.webservice.BuscaUsuarioFace;

public class SimuladorResultado extends AppCompatActivity {

    BancoControllerUsuario crud;
    Cursor cursor;

    double valor;
    String id,nome,vencimento,url_img;
    int dias;
    TextView tv_nome,tv_valor,tv_vencimento,tv_dias_pagamento,tv_juros_mes,tv_valor_total;
    Transacao trans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_simulador_resultado);

        crud = new BancoControllerUsuario(getBaseContext());
        cursor = crud.carregaDados();
        trans = new Transacao();

        Intent it = getIntent();
        Bundle parametro = it.getExtras();
        id = parametro.getString("id");
        nome = parametro.getString("nome");
        valor = Double.parseDouble(parametro.getString("valor"));
        vencimento = parametro.getString("vencimento");
        dias = parametro.getInt("dias");
        url_img = parametro.getString("url_img");

        DecimalFormat decimal = new DecimalFormat( "0.00" );

        double juros_mensal = Double.parseDouble(decimal.format(valor * (0.00066333 * dias)));
        //double taxa_fixa = Double.parseDouble(decimal.format(valor * 0.0099));

        double valor_total = juros_mensal +  valor;

        ImageView img = (ImageView) findViewById(R.id.img_amigo);
        Picasso.with(getBaseContext()).load(url_img).into(img);

        tv_nome = (TextView) findViewById(R.id.nome_amigo);
        tv_dias_pagamento = (TextView) findViewById(R.id.tv_dias_pagamento);
        tv_juros_mes = (TextView) findViewById(R.id.tv_juros_mes);
        tv_valor = (TextView) findViewById(R.id.tv_valor);
        tv_valor_total = (TextView) findViewById(R.id.tv_valor_total);
        tv_vencimento = (TextView) findViewById(R.id.tv_vencimento);
        //tv_valor_servico = (TextView) findViewById(R.id.tv_valor_servico);

        Button btn_fazer_pedido = (Button) findViewById(R.id.btn_fazer_pedido);

        Locale ptBr = new Locale("pt", "BR");
        NumberFormat nf = NumberFormat.getCurrencyInstance(ptBr);
        String valor_formatado = nf.format (valor);
        //String taxa_fixa_formatado = nf.format (taxa_fixa);
        String juros_mensal_formatado = nf.format (juros_mensal);
        String valor_total_formatado = nf.format (valor_total);

       // tv_valor_servico.setText(taxa_fixa_formatado);
        tv_nome.setText(tv_nome.getText().toString()+nome);
        tv_dias_pagamento.setText(String.valueOf(dias));
        tv_juros_mes.setText(juros_mensal_formatado);
        tv_valor.setText(valor_formatado);
        tv_valor_total.setText(valor_total_formatado);
        tv_vencimento.setText(vencimento);

        btn_fazer_pedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

                        String senha = cursor.getString(cursor.getColumnIndexOrThrow(CriandoBanco.SENHA));

                        if(senha.equals(et_senha.getText().toString())){

                            new BuscaUsuarioFace(id,SimuladorResultado.this).execute();

                            dialog.dismiss();

                        }else{

                            msg_dialog.setVisibility(View.VISIBLE);
                        }

                    }
                });

                dialog.show();
            }
        });

    }
    public void retornoBuscaUsuario(Usuario usu){

        if(usu != null) {
            //data do pedido
            DateTimeFormatter fmt = DateTimeFormat.forPattern("dd/MM/YYYYY");
            DateTime hoje = new DateTime();
            final String hoje_string = fmt.print(hoje);

            //adicionamos a trasacao em ambas as contas
            trans.setDataPedido(hoje_string);
            trans.setUsu1(cursor.getString(cursor.getColumnIndexOrThrow(CriandoBanco.CPF)));
            trans.setUsu2(usu.getCpf());
            trans.setValor(String.valueOf(valor));
            trans.setVencimento(vencimento);

            new AddTransacao(trans,SimuladorResultado.this).execute();

        }else{
            mensagem();
        }


    }

    public void retornoAddTransacao(String result){

    }

    public void mensagem()
    {
        AlertDialog.Builder mensagem = new AlertDialog.Builder(this);
        mensagem.setTitle("Houve um erro!");
        mensagem.setMessage("Olá, parece que houve um problema de conexao. Favor tente novamente!");
        mensagem.setNeutralButton("OK",null);
        mensagem.show();
    }
}
