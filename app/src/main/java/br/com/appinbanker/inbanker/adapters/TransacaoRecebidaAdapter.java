package br.com.appinbanker.inbanker.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import br.com.appinbanker.inbanker.NavigationDrawerActivity;
import br.com.appinbanker.inbanker.R;
import br.com.appinbanker.inbanker.VerPedidoRecebido;
import br.com.appinbanker.inbanker.entidades.Transacao;
import br.com.appinbanker.inbanker.entidades.Usuario;
import br.com.appinbanker.inbanker.interfaces.WebServiceReturnString;
import br.com.appinbanker.inbanker.interfaces.WebServiceReturnUsuario;
import br.com.appinbanker.inbanker.webservice.BuscaUsuarioCPF;
import br.com.appinbanker.inbanker.webservice.EditaTransacao;
import br.com.appinbanker.inbanker.webservice.EditaTransacaoResposta;
import br.com.appinbanker.inbanker.webservice.EnviaNotificacao;

/**
 * Created by jonatasilva on 29/12/16.
 */

public class TransacaoRecebidaAdapter extends BaseExpandableListAdapter implements WebServiceReturnString,WebServiceReturnUsuario {

    //TextView tv_data_pedido_child;
    TextView tv_data_pagamento_child ;
    //TextView tv_nome_usuario_child;
    TextView tv_dias_faltando_child;
    TextView tv_taxa_juros_am_child;
    //TextView tv_taxa_rendimento_child;
    //TextView tv_valor_multa_child;
    TextView tv_valor_iof_child;
    //TextView tv_seguro_child;
    TextView tv_valor_total_child;
   // TextView tv_valor_pedido_child;
    LinearLayout ll_resposta_pedido_child;
    //LinearLayout ll_confirma_recebimento_valor_emprestado_child;
    private Button btn_aceita_pedido,btn_recusa_pedido;
    ProgressBar progress_bar_btn;

    Transacao trans_global;

    TextView msg_ver_pedido_child;

    String hoje_string;
    private boolean aceitou_pedido = false;

    private Context _context;
    private List<Transacao> _listDataHeader; // header titles
    private HashMap<String, Transacao> _listDataChild; // header child
    public TransacaoRecebidaAdapter(Context context, List<Transacao> listDataHeader, HashMap<String,Transacao> listDataChild) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listDataChild;
    }
    @Override
    public Object getChild(int groupPosition, int childPosititon) {

        //to get children of the respective header(group).
        //return  this._listDataChild.get(this._listDataHeader.get(groupPosition));
        return  this._listDataHeader.get(groupPosition);
    }
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    // set the child view with value
    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        trans_global = (Transacao) getChild(groupPosition, childPosition);
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.adapter_transacao_recebida_child, parent, false);
        }
        ll_resposta_pedido_child = (LinearLayout) convertView.findViewById(R.id.ll_resposta_pedido);
        //ll_confirma_recebimento_valor_emprestado_child = (LinearLayout) convertView.findViewById(R.id.ll_confirma_recebimento_valor_emprestado);
        msg_ver_pedido_child = (TextView)convertView.findViewById(R.id.msg_ver_pedido);
        tv_data_pagamento_child  = (TextView) convertView.findViewById(R.id.tv_data_pagamento);
        //tv_nome_usuario_child  = (TextView) convertView.findViewById(R.id.tv_nome_usuario);
        tv_dias_faltando_child  = (TextView) convertView.findViewById(R.id.tv_dias_faltando);
        tv_taxa_juros_am_child  = (TextView) convertView.findViewById(R.id.tv_taxa_juros_am);
        //tv_taxa_rendimento_child  = (TextView) convertView.findViewById(R.id.tv_taxa_rendimento);
        //tv_valor_multa_child  = (TextView) convertView.findViewById(R.id.tv_valor_multa);
        tv_valor_iof_child = (TextView) convertView.findViewById(R.id.tv_valor_iof);
        //tv_seguro_child = (TextView) convertView.findViewById(R.id.tv_seguro);
        tv_valor_total_child  = (TextView) convertView.findViewById(R.id.tv_valor_total);
        //tv_valor_pedido_child  = (TextView) convertView.findViewById(R.id.tv_valor_pedido);
        btn_aceita_pedido = (Button) convertView.findViewById(R.id.btn_aceita_pedido);
        btn_recusa_pedido = (Button) convertView.findViewById(R.id.btn_recusa_pedido);
        //btn_confirma_quitacao = (Button) convertView.findViewById(R.id.btn_confirma_quitacao);
        //btn_recusa_quitacao = (Button) convertView.findViewById(R.id.btn_recusa_quitacao);
        progress_bar_btn = (ProgressBar) convertView.findViewById(R.id.progress_bar_btn);

        configView(trans_global,groupPosition,parent);

        return convertView;
    }
    @Override
    public int getChildrenCount(int groupPosition) {

        return 1;
        //return this._listDataChild.get(this._listDataHeader.get(groupPosition)).size();
    }
    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }
    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    //set the header view with values
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        //String headerTitle = (String) getGroup(groupPosition);
        final Transacao item = (Transacao) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.adapter_transacao_recebida_header, null);
        }

        Locale ptBr = new Locale("pt", "BR");
        NumberFormat nf = NumberFormat.getCurrencyInstance(ptBr);

        TextView tv_data_pedido = (TextView)convertView.findViewById(R.id.tv_data_pedido);
        TextView tv_nome_usuario = (TextView)convertView.findViewById(R.id.tv_nome_usuario);
        TextView tv_valor_pedido = (TextView)convertView.findViewById(R.id.tv_valor_pedido);
        TextView tv_valor_redimento = (TextView) convertView.findViewById(R.id.tv_valor_redimento);
        //tv_data_pedido.setTypeface(null, Typeface.BOLD);
        tv_data_pedido.setText(item.getDataPedido().substring(0, item.getDataPedido().length() - 5));
        tv_nome_usuario.setText(item.getNome_usu1());
        tv_valor_pedido.setText(nf.format(Double.parseDouble(item.getValor())));

        //calculamos a diferença de dias entre a data atual ate a data do pedido para calcularmos o juros
        DateTimeFormatter fmt = DateTimeFormat.forPattern("dd/MM/YYYY");
        DateTime hoje = new DateTime();
        DateTime vencimento_parse = fmt.parseDateTime(item.getVencimento());
        DateTime data_pedido_parse = fmt.parseDateTime(item.getDataPedido());

        //calculamos o total de dias para mostramos na tela inicial antes do usuario-2 aceitar ou recusar o pedido recebido
        Days d = Days.daysBetween(data_pedido_parse, vencimento_parse);
        int dias = d.getDays();

        double redimento = Double.parseDouble(item.getValor()) * (0.00066333 * dias);
        String juros_total_formatado = nf.format (redimento);

        tv_valor_redimento.setText(juros_total_formatado);

        /*int status_transacao = Integer.parseInt(item.getStatus_transacao());
        if(status_transacao==Transacao.AGUARDANDO_RESPOSTA) {

            ExpandableListView mExpandableListView = (ExpandableListView) parent;
            mExpandableListView.expandGroup(groupPosition);
        }*/

        return convertView;

    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void configView(final Transacao item,int groupPosition,ViewGroup parent){

        //calculamos a diferença de dias entre a data atual ate a data do pedido para calcularmos o juros
        DateTimeFormatter fmt = DateTimeFormat.forPattern("dd/MM/YYYY");
        DateTime hoje = new DateTime();
        DateTime data_pedido_parse = fmt.parseDateTime(item.getDataPedido());
        DateTime vencimento_parse = fmt.parseDateTime(item.getVencimento());

        //calculamos o total de dias para mostramos na tela inicial antes do usuario-2 aceitar ou recusar o pedido recebido
        Days d = Days.daysBetween(data_pedido_parse, vencimento_parse);
        int dias = d.getDays();

        //calculamos o total de dias para mostramos na tela inicial antes do usuario-2 aceitar ou recusar o pedido recebido
        Days d_faltando = Days.daysBetween(hoje, vencimento_parse);
        int dias_faltando = d_faltando.getDays();

        //Log.i("Enviados","dias = "+dias+ " - " +hoje);

        double juros_mensal = Double.parseDouble(item.getValor()) * (0.00066333 * dias);
        double valor_total = juros_mensal +  Double.parseDouble(item.getValor());

        Locale ptBr = new Locale("pt", "BR");
        NumberFormat nf = NumberFormat.getCurrencyInstance(ptBr);

        String juros_mensal_formatado = nf.format (juros_mensal);
        String valor_pedido_formatado = nf.format (Double.parseDouble(item.getValor()));
        String juros_total_formatado = nf.format (valor_total);
        //String juros_mensal_formatado = nf.format (Double.parseDouble(valor));

        tv_data_pagamento_child.setText(item.getVencimento().substring(0, item.getVencimento().length() - 5));
        //tv_data_pedido_child.setText(item.getDataPedido().substring(0, item.getDataPedido().length() - 5));
        //tv_nome_usuario_child.setText(item.getNome_usu1());
        tv_dias_faltando_child.setText(String.valueOf(dias_faltando));
        tv_taxa_juros_am_child.setText("1.99%");
        //tv_taxa_rendimento_child.setText(juros_mensal_formatado);
        //tv_valor_multa_child.setText("0,00");
        tv_valor_iof_child.setText("0,00");
        //tv_valor_pedido_child.setText(valor_pedido_formatado);
        tv_valor_total_child.setText(juros_total_formatado);
        //tv_seguro_child.setText("Não");

        int status_transacao = Integer.parseInt(item.getStatus_transacao());


        switch (status_transacao){
            case Transacao.AGUARDANDO_RESPOSTA:
                ll_resposta_pedido_child.setVisibility(View.VISIBLE);
                //ll_confirma_recebimento_valor_emprestado_child.setVisibility(View.GONE);
                msg_ver_pedido_child.setVisibility(View.GONE);

                break;
            case Transacao.PEDIDO_ACEITO:

                ll_resposta_pedido_child.setVisibility(View.GONE);
                msg_ver_pedido_child.setVisibility(View.VISIBLE);
                msg_ver_pedido_child.setText("Você esta aguardando que seu amigo(a) " + trans_global.getNome_usu1() + " confirme o recebimento do valor.");


                break;
            case Transacao.QUITACAO_SOLICITADA:

                ll_resposta_pedido_child.setVisibility(View.GONE);
                //ll_confirma_recebimento_valor_emprestado_child.setVisibility(View.VISIBLE);
                break;
            /*case Transacao.CONFIRMADO_RECEBIMENTO:


                ll_resposta_pedido_child.setVisibility(View.GONE);
                //msg_ver_pedido.setText("Você esta aguardando que seu amigo(a) " + trans_atual.getNome_usu1() + " solicite a confirmação de quitação do empréstimo.");
                break;

            case Transacao.RESP_QUITACAO_SOLICITADA_RECUSADA:

                //tr_dias_atraso.setVisibility(View.VISIBLE);
                //tr_valor_multa.setVisibility(View.VISIBLE);

                data_vencimento_parse = fmt.parseDateTime(trans_atual.getVencimento());
                if(hoje.isAfter(data_vencimento_parse)){

                    Days d_atraso = Days.daysBetween(data_vencimento_parse, hoje);
                    dias_atraso = d_atraso.getDays();

                    Log.i("PagamentoPendente","dias de atraso = "+dias_atraso);

                    multa_atraso = Double.parseDouble(trans_atual.getValor())*0.1;
                    tv_valor_multa.setText(String.valueOf(nf.format(multa_atraso)));
                    tv_dias_atraso.setText(String.valueOf(dias_atraso));

                }

                if(dias_faltando < 0){
                    Double multa_atraso = Double.parseDouble(item.getValor())*0.1;
                    //tv_valor_multa_child.setText(String.valueOf(nf.format(multa_atraso)));
                }


                ll_resposta_pedido_child.setVisibility(View.GONE);
                //juros_mensal = Double.parseDouble(trans_atual.getValor()) * (0.00066333 * dias_corridos);
                //tr_dias_corridos.setVisibility(View.VISIBLE);
                //msg_ver_pedido.setText("Você já recusou uma confirmação de quitação dessa dívida com "+ trans_atual.getNome_usu1()+". Agora está aguardando por outra solicitação de quitação.");
                break;
                */
        }

        btn_aceita_pedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Click","click 1");

                aceitou_pedido = true;

                Transacao trans = new Transacao();
                trans.setId_trans(trans_global.getId_trans());
                trans.setStatus_transacao(String.valueOf(Transacao.PEDIDO_ACEITO));

                metodoEditaTrans(trans);

                progress_bar_btn.setVisibility(View.VISIBLE);
                btn_recusa_pedido.setEnabled(false);
                btn_aceita_pedido.setEnabled(false);

            }
        });
        btn_recusa_pedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Click","click 2");

                //data do cancelamento
                DateTimeFormatter fmt = DateTimeFormat.forPattern("dd/MM/YYYY");
                DateTime hoje = new DateTime();
                hoje_string = fmt.print(hoje);

                Transacao trans = new Transacao();
                trans.setId_trans(item.getId_trans());
                trans.setStatus_transacao(String.valueOf(Transacao.PEDIDO_RECUSADO));
                trans.setData_recusada(hoje_string);
                trans.setData_pagamento("");

                new EditaTransacaoResposta(trans,item.getUsu1(),item.getUsu2(),TransacaoRecebidaAdapter.this).execute();

                progress_bar_btn.setVisibility(View.VISIBLE);
                btn_recusa_pedido.setEnabled(false);
                btn_aceita_pedido.setEnabled(false);

            }
        });
        /*btn_confirma_quitacao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Click","click 3");

                //data de confirmacao de pagamento
                DateTimeFormatter fmt = DateTimeFormat.forPattern("dd/MM/YYYY");
                DateTime hoje = new DateTime();
                hoje_string = fmt.print(hoje);


                Transacao trans2 = new Transacao();
                trans2.setId_trans(trans_global.getId_trans());
                trans2.setStatus_transacao(String.valueOf(Transacao.RESP_QUITACAO_SOLICITADA_CONFIRMADA));
                trans2.setData_recusada("");
                trans2.setData_pagamento(hoje_string);

                new EditaTransacaoResposta(trans2,trans_global.getUsu1(),trans_global.getUsu2(),TransacaoRecebidaAdapter.this).execute();

                progress_bar_btn.setVisibility(View.VISIBLE);
                btn_recusa_pedido.setEnabled(false);
                btn_aceita_pedido.setEnabled(false);

            }
        });
        btn_recusa_quitacao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Click","click 4");

                Transacao trans = new Transacao();
                trans.setId_trans(trans_global.getId_trans());
                trans.setStatus_transacao(String.valueOf(Transacao.RESP_QUITACAO_SOLICITADA_RECUSADA));

                metodoEditaTrans(trans);

                progress_bar_btn.setVisibility(View.VISIBLE);
                btn_recusa_pedido.setEnabled(false);
                btn_aceita_pedido.setEnabled(false);
            }
        });
*/

    }

    public void metodoEditaTrans(Transacao trans){
        new EditaTransacao(trans,trans_global.getUsu1(),trans_global.getUsu2(),this).execute();
    }

    @Override
    public void retornoStringWebService(String result){
        Log.i("webservice","resultado edita transao = "+result);

        progress_bar_btn.setVisibility(View.GONE);
        btn_recusa_pedido.setEnabled(true);
        btn_aceita_pedido.setEnabled(true);

        if(result.equals("sucesso_edit")){

            //busca token do usuario 1
            new BuscaUsuarioCPF(trans_global.getUsu1(),_context,this).execute();

        }else{
            mensagem("Houve um erro!","Olá, parece que tivemos algum problema de conexão, por favor tente novamente.","Ok");
        }

    }

    @Override
    public void retornoUsuarioWebService(Usuario usu){

        Transacao trans = new Transacao();
        trans.setNome_usu1(trans_global.getNome_usu1());
        trans.setNome_usu2(trans_global.getNome_usu2());
        trans.setId_trans(trans_global.getId_trans());
        trans.setUsu1(trans_global.getUsu1());
        trans.setUsu2(trans_global.getUsu2());
        trans.setDataPedido(trans_global.getDataPedido());
        trans.setValor(trans_global.getValor());
        trans.setVencimento(trans_global.getVencimento());
        trans.setUrl_img_usu1(trans_global.getUrl_img_usu1());
        trans.setUrl_img_usu2(trans_global.getUrl_img_usu2());

        if(aceitou_pedido) {

            trans.setStatus_transacao(String.valueOf(Transacao.PEDIDO_ACEITO));

            //envia notificacao
            new EnviaNotificacao(trans, usu.getToken_gcm()).execute();

            mensagemIntent("InBanker", "Parabéns, você aceitou o pedido. Ao efetuar o pagamento, peça que seu amigo(a) " + trans_global.getNome_usu1() + " confirme o recebimento do valor.", "Ok");
        }else {
            trans.setStatus_transacao(String.valueOf(Transacao.PEDIDO_RECUSADO));

            trans.setData_recusada(hoje_string);

            //envia notificacao
            new EnviaNotificacao(trans, usu.getToken_gcm()).execute();

            mensagemIntent("InBanker", "Você recusou esse pedido de empréstimo de " + trans_global.getNome_usu1() + ".", "Ok");
        }

    }

    public void mensagemIntent(String titulo,String corpo,String botao)
    {
        AlertDialog.Builder mensagem = new AlertDialog.Builder(_context);
        mensagem.setTitle(titulo);
        mensagem.setMessage(corpo);
        mensagem.setPositiveButton(botao,new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent it = new Intent(_context,NavigationDrawerActivity.class);
                it.putExtra("menu_item",NavigationDrawerActivity.MENU_INICIO);
                _context.startActivity(it);

                //para encerrar a activity atual e todos os parent
                ((NavigationDrawerActivity) _context).finishAffinity();

            }
        });
        mensagem.show();
    }

    public void mensagem(String titulo,String corpo,String botao)
    {
        AlertDialog.Builder mensagem = new AlertDialog.Builder(_context);
        mensagem.setTitle(titulo);
        mensagem.setMessage(corpo);
        mensagem.setNeutralButton(botao,null);
        mensagem.show();
    }

    @Override
    public void retornoUsuarioWebServiceAuxInicioToken(Usuario usu){}

}
