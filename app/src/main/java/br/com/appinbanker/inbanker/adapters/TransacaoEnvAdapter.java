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
import br.com.appinbanker.inbanker.VerPedidoEnviado;
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

public class TransacaoEnvAdapter extends BaseExpandableListAdapter implements WebServiceReturnString,WebServiceReturnUsuario {

    //TextView tv_data_pedido_child;
    TextView tv_data_pagamento_child ;
    //TextView tv_nome_usuario_child;
    TextView tv_dias_corridos_child;
    TextView tv_taxa_juros_am_child;
    //TextView tv_taxa_juros_child;
    TextView tv_valor_taxa_servico_child;
    TextView tv_valor_total_child;
    //TextView tv_valor_pedido_child;
    LinearLayout ll_cancelar_pedido_child;
    LinearLayout ll_confirma_recebimento_child;
    private Button btn_confirma_recebimento_child, btn_cancelar_pedido_antes_resp_child;
    ProgressBar progress_bar_btn;

    Transacao trans_global;
    private int status_transacao;

    private Context _context;
    private List<Transacao> _listDataHeader; // header titles
    private HashMap<String, Transacao> _listDataChild; // header child
    public TransacaoEnvAdapter(Context context, List<Transacao> listDataHeader, HashMap<String,Transacao> listDataChild) {
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
        final Transacao item = (Transacao) getChild(groupPosition, childPosition);
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.adapter_transacao_env_child, parent, false);
        }
        ll_confirma_recebimento_child = (LinearLayout) convertView.findViewById(R.id.ll_confirma_recebimento);
        ll_cancelar_pedido_child = (LinearLayout) convertView.findViewById(R.id.ll_cancelar_pedido);
        //tv_data_pedido_child = (TextView)convertView.findViewById(R.id.tv_data_pedido);
        tv_data_pagamento_child  = (TextView) convertView.findViewById(R.id.tv_data_pagamento);
        //tv_nome_usuario_child  = (TextView) convertView.findViewById(R.id.tv_nome_usuario);
        tv_dias_corridos_child  = (TextView) convertView.findViewById(R.id.tv_dias_corridos);
        tv_taxa_juros_am_child  = (TextView) convertView.findViewById(R.id.tv_taxa_juros_am);
        //tv_taxa_juros_child  = (TextView) convertView.findViewById(R.id.tv_taxa_juros);
        tv_valor_taxa_servico_child  = (TextView) convertView.findViewById(R.id.tv_valor_taxa_servico);
        tv_valor_total_child  = (TextView) convertView.findViewById(R.id.tv_valor_total);
        //tv_valor_pedido_child  = (TextView) convertView.findViewById(R.id.tv_valor_pedido);
        //btn_cancelar_pedido_antes_receb_child = (Button) convertView.findViewById(R.id.btn_cancelar_pedido_antes_receb);
        btn_cancelar_pedido_antes_resp_child = (Button) convertView.findViewById(R.id.btn_cancelar_pedido_antes_resp);
        btn_confirma_recebimento_child = (Button) convertView.findViewById(R.id.btn_confirma_recebimento);
        progress_bar_btn = (ProgressBar) convertView.findViewById(R.id.progress_bar_btn);

        configView(item);



        /*btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Click","Click butao");
            }
        });*/

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
        trans_global = (Transacao) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.adapter_transacao_env_header, null);
        }

        Locale ptBr = new Locale("pt", "BR");
        NumberFormat nf = NumberFormat.getCurrencyInstance(ptBr);

        TextView tv_data_pedido = (TextView)convertView.findViewById(R.id.tv_data_pedido);
        TextView tv_nome_usuario = (TextView)convertView.findViewById(R.id.tv_nome_usuario);
        TextView tv_valor_pedido = (TextView)convertView.findViewById(R.id.tv_valor_pedido);
        TextView tv_valor_juros = (TextView) convertView.findViewById(R.id.tv_valor_juros);

        //tv_data_pedido.setTypeface(null, Typeface.BOLD);
        tv_data_pedido.setText(trans_global.getDataPedido().substring(0, trans_global.getDataPedido().length() - 5));
        tv_nome_usuario.setText(trans_global.getNome_usu2());
        tv_valor_pedido.setText(nf.format(Double.parseDouble(trans_global.getValor())));

        //calculamos a diferença de dias entre a data atual ate a data do pedido para calcularmos o juros
        DateTimeFormatter fmt = DateTimeFormat.forPattern("dd/MM/YYYY");
        DateTime vencimento_parse = fmt.parseDateTime(trans_global.getVencimento());
        DateTime data_pedido_parse = fmt.parseDateTime(trans_global.getDataPedido());

        //calculamos o total de dias para mostramos na tela inicial antes do usuario-2 aceitar ou recusar o pedido recebido
        Days d = Days.daysBetween(data_pedido_parse, vencimento_parse);
        int dias = d.getDays();

        double redimento = Double.parseDouble(trans_global.getValor()) * (0.00066333 * dias);
        String juros_total_formatado = nf.format (redimento);

        tv_valor_juros.setText(juros_total_formatado);

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

    public void configView(final Transacao item){

        //calculamos a diferença de dias entre a data atual ate a data do pedido para calcularmos o juros
        DateTimeFormatter fmt = DateTimeFormat.forPattern("dd/MM/YYYY");
        DateTime hoje = new DateTime();
        DateTime data_pedido_parse = fmt.parseDateTime(item.getDataPedido());
        DateTime vencimento_parse = fmt.parseDateTime(item.getVencimento());

        //calculamos o total de dias para mostramos na tela inicial antes do usuario-2 aceitar ou recusar o pedido recebido
        Days d = Days.daysBetween(data_pedido_parse, vencimento_parse);
        int dias = d.getDays();

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
        //tv_nome_usuario_child.setText(item.getNome_usu2());
        tv_dias_corridos_child.setText(String.valueOf(dias));
        tv_taxa_juros_am_child.setText("1.99%");
        //tv_taxa_juros_child.setText(juros_mensal_formatado);
        tv_valor_taxa_servico_child.setText("0,00");
        //tv_valor_pedido_child.setText(valor_pedido_formatado);
        tv_valor_total_child.setText(juros_total_formatado);


        int status_transacao_local = Integer.parseInt(item.getStatus_transacao());

        switch (status_transacao_local){
            case Transacao.AGUARDANDO_RESPOSTA:
                ll_cancelar_pedido_child.setVisibility(View.VISIBLE);
                ll_confirma_recebimento_child.setVisibility(View.GONE);
                break;
            case Transacao.PEDIDO_ACEITO:
                btn_confirma_recebimento_child.setVisibility(View.GONE);
                ll_confirma_recebimento_child.setVisibility(View.VISIBLE);
                break;
        }


        btn_confirma_recebimento_child.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Click","click 1");

                Transacao trans = new Transacao();
                trans.setId_trans(trans_global.getId_trans());
                trans.setStatus_transacao(String.valueOf(Transacao.CONFIRMADO_RECEBIMENTO));

                //esse valor sera passado para o metodo notificacao
                status_transacao = Transacao.CONFIRMADO_RECEBIMENTO;

                metodoEditaTrans(trans);

                progress_bar_btn.setVisibility(View.VISIBLE);
                btn_confirma_recebimento_child.setEnabled(false);
                //btn_cancelar_pedido_antes_receb_child.setEnabled(false);

            }
        });

        btn_cancelar_pedido_antes_resp_child.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Click","click 2");

                //data do cancelamento
                DateTimeFormatter fmt = DateTimeFormat.forPattern("dd/MM/YYYY");
                DateTime hoje = new DateTime();
                String hoje_string = fmt.print(hoje);

                Transacao trans = new Transacao();
                trans.setId_trans(trans_global.getId_trans());
                trans.setStatus_transacao(String.valueOf(Transacao.ENVIO_CANCELADO_ANTES_RESPOSTA));
                trans.setData_recusada(hoje_string);
                trans.setData_pagamento("");

                //esse valor sera passado para o metodo notificacao
                status_transacao = Transacao.ENVIO_CANCELADO_ANTES_RESPOSTA;

                metodoEditaTransResp(trans);

                progress_bar_btn.setVisibility(View.VISIBLE);
                btn_confirma_recebimento_child.setEnabled(false);
                btn_cancelar_pedido_antes_resp_child.setEnabled(false);

            }
        });
        /*
        btn_cancelar_pedido_antes_receb_child.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Click","click 3");

                //data do cancelamento
                DateTimeFormatter fmt = DateTimeFormat.forPattern("dd/MM/YYYY");
                DateTime hoje = new DateTime();
                String hoje_string = fmt.print(hoje);

                Transacao trans = new Transacao();
                trans.setId_trans(item.getId_trans());
                trans.setStatus_transacao(String.valueOf(Transacao.ENVIO_CANCELADO_ANTES_RECEBIMENTO));
                trans.setData_recusada(hoje_string);
                trans.setData_pagamento("");

                //esse valor sera passado para o metodo notificacao
                status_transacao = Transacao.ENVIO_CANCELADO_ANTES_RECEBIMENTO;

                metodoEditaTransResp(trans);

                progress_bar_btn.setVisibility(View.VISIBLE);
                btn_confirma_recebimento_child.setEnabled(false);
                btn_cancelar_pedido_antes_receb_child.setEnabled(false);
            }
        });*/

    }

     public void metodoEditaTrans(Transacao trans){
        new EditaTransacao(trans,trans_global.getUsu1(),trans_global.getUsu2(),this).execute();
    }

    public void metodoEditaTransResp(Transacao trans){
        new EditaTransacaoResposta(trans,trans_global.getUsu1(),trans_global.getUsu2(),this).execute();
    }

    public void retornoStringWebService(String result){

        Log.i("webservice","resultado edita transao = "+result);

        progress_bar_btn.setVisibility(View.GONE);
        btn_confirma_recebimento_child.setEnabled(true);

        if(result.equals("sucesso_edit")){

            //busca token do usuario 2 para enviarmos notificacao
            new BuscaUsuarioCPF(trans_global.getUsu2(),_context,this).execute();


        }else{
            mensagem("Houve um erro!","Olá, parece que tivemos algum problema de conexão, por favor tente novamente.","Ok");
        }

    }

    @Override
    public void retornoUsuarioWebService(Usuario usu) {

        Transacao trans = new Transacao();

        trans.setNome_usu1(trans_global.getNome_usu1());
        trans.setNome_usu2(trans_global.getNome_usu2());
        trans.setStatus_transacao(String.valueOf(status_transacao));
        trans.setId_trans(trans_global.getId_trans());
        trans.setUsu1(trans_global.getUsu1());
        trans.setUsu2(trans_global.getUsu2());
        trans.setDataPedido(trans_global.getDataPedido());
        trans.setValor(trans_global.getValor());
        trans.setVencimento(trans_global.getVencimento());
        trans.setUrl_img_usu1(trans_global.getUrl_img_usu1());
        trans.setUrl_img_usu2(trans_global.getUrl_img_usu2());


        //envia notificacao
        new EnviaNotificacao(trans,usu.getToken_gcm()).execute();

        if(status_transacao == Transacao.CONFIRMADO_RECEBIMENTO)
            mensagemIntent("InBanker","Parabéns, você confirmou o recebimento do valor solicitado. Ao efetuar o pagamento de quitação, peça que seu amigo(a) " + trans_global.getNome_usu2() + " confirme o recebimento do valor.", "Ok");
        else if(status_transacao == Transacao.ENVIO_CANCELADO_ANTES_RESPOSTA)
            mensagemIntent("InBanker","Você acaba de cancelar o pedido que foi enviado ao seu amigo(a) " + trans_global.getNome_usu2()+".", "Ok");
        else if(status_transacao == Transacao.ENVIO_CANCELADO_ANTES_RECEBIMENTO)
            mensagemIntent("InBanker","Você acaba de cancelar o pedido que foi enviado ao seu amigo(a) " + trans_global.getNome_usu2()+".", "Ok");
    }

    public void mensagem(String titulo,String corpo,String botao)
    {
        AlertDialog.Builder mensagem = new AlertDialog.Builder(_context);
        mensagem.setTitle(titulo);
        mensagem.setMessage(corpo);
        mensagem.setNeutralButton(botao,null);
        mensagem.show();
    }

    public void mensagemIntent(String titulo,String corpo,String botao)
    {
        AlertDialog.Builder mensagem = new AlertDialog.Builder(_context);
        mensagem.setTitle(titulo);
        mensagem.setMessage(corpo);
        mensagem.setPositiveButton(botao,new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent it = new Intent(_context,NavigationDrawerActivity.class);
                _context.startActivity(it);
                //para encerrar a activity atual e todos os parent
                ((NavigationDrawerActivity) _context).finishAffinity();
            }
        });
        mensagem.show();
    }

    @Override
    public void retornoUsuarioWebServiceAuxInicioToken(Usuario usu){}

}
