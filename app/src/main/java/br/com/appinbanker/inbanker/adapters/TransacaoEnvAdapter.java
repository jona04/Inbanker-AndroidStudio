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
import android.widget.ProgressBar;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import br.com.appinbanker.inbanker.NavigationDrawerActivity;
import br.com.appinbanker.inbanker.R;
import br.com.appinbanker.inbanker.entidades.AlteraPagamento;
import br.com.appinbanker.inbanker.entidades.Historico;
import br.com.appinbanker.inbanker.entidades.KeyAccountPagamento;
import br.com.appinbanker.inbanker.entidades.Transacao;
import br.com.appinbanker.inbanker.entidades.Usuario;
import br.com.appinbanker.inbanker.interfaces.WebServiceReturnString;
import br.com.appinbanker.inbanker.interfaces.WebServiceReturnStringPagamento;
import br.com.appinbanker.inbanker.interfaces.WebServiceReturnUsuario;
import br.com.appinbanker.inbanker.webservice.BuscaUsuarioCPF;
import br.com.appinbanker.inbanker.webservice.AlteraPagamentoService;
import br.com.appinbanker.inbanker.webservice.EditaTransacaoResposta;
import br.com.appinbanker.inbanker.webservice.EnviaNotificacao;

/**
 * Created by jonatasilva on 29/12/16.
 */

public class TransacaoEnvAdapter extends BaseExpandableListAdapter implements WebServiceReturnString,WebServiceReturnUsuario,WebServiceReturnStringPagamento {

    TextView tv_data_pagamento_child ;
    TextView tv_dias_corridos_child;
    TextView tv_taxa_juros_am_child;
    //TextView tv_valor_taxa_servico_child;
    TextView tv_valor_total_child;
    private Button btn_cancelar_pedido_antes_resp_child;
    ProgressBar progress_bar_btn;

    Transacao trans_global;
    private int status_transacao;

    private String hoje_string;

    private Context _context;
    private List<Transacao> _listDataHeader; // header titles
    private HashMap<String, Transacao> _listDataChild; // header child
    public TransacaoEnvAdapter(Context context, List<Transacao> listDataHeader, HashMap<String,Transacao> listDataChild,String hoje) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listDataChild;
        this.hoje_string = hoje;
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
        tv_data_pagamento_child  = (TextView) convertView.findViewById(R.id.tv_data_pagamento);
        tv_dias_corridos_child  = (TextView) convertView.findViewById(R.id.tv_dias_corridos);
        tv_taxa_juros_am_child  = (TextView) convertView.findViewById(R.id.tv_taxa_juros_am);
        //tv_valor_taxa_servico_child  = (TextView) convertView.findViewById(R.id.tv_valor_taxa_servico);
        tv_valor_total_child  = (TextView) convertView.findViewById(R.id.tv_valor_total);
        btn_cancelar_pedido_antes_resp_child = (Button) convertView.findViewById(R.id.btn_cancelar_pedido_antes_resp);
        progress_bar_btn = (ProgressBar) convertView.findViewById(R.id.progress_bar_btn);

        configView(item);

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

        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        DateTimeFormatter dtfOut = DateTimeFormat.forPattern("dd/MM/yyyy");
        DateTimeFormatter dtfOut_hora = DateTimeFormat.forPattern("HH:mm:ss");

        DateTime hora_pedido_parse = fmt.parseDateTime(trans_global.getDataPedido());
        DateTime vencimento_parse_utc = fmt.parseDateTime(trans_global.getVencimento());
        DateTime data_pedido_parse_utc = fmt.parseDateTime(trans_global.getDataPedido());

        String vencimento_parse_string = dtfOut.print(vencimento_parse_utc);
        String data_pedido_parse_string = dtfOut.print(data_pedido_parse_utc);
        String hora_pedido = dtfOut_hora.print(hora_pedido_parse);

        DateTime vencimento_parse = dtfOut.parseDateTime(vencimento_parse_string);
        DateTime data_pedido_parse = dtfOut.parseDateTime(data_pedido_parse_string);

        //calculamos o total de dias para mostramos na tela inicial antes do usuario-2 aceitar ou recusar o pedido recebido
        Days d = Days.daysBetween(data_pedido_parse, vencimento_parse);
        int dias = d.getDays();

        double redimento = Double.parseDouble(trans_global.getValor()) * (0.00066333 * dias);
        String juros_total_formatado = nf.format (redimento);

        //tv_data_pedido.setText(data_pedido);
        tv_data_pedido.setText(data_pedido_parse_string.substring(0, data_pedido_parse_string.length() - 5));
        tv_nome_usuario.setText(trans_global.getNome_usu2());
        tv_valor_pedido.setText(nf.format(Double.parseDouble(trans_global.getValor())));
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

        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        DateTimeFormatter dtfOut = DateTimeFormat.forPattern("dd/MM/yyyy");

        DateTime vencimento_parse_utc = fmt.parseDateTime(trans_global.getVencimento());
        DateTime data_pedido_parse_utc = fmt.parseDateTime(trans_global.getDataPedido());

        String vencimento_parse_string = dtfOut.print(vencimento_parse_utc);
        String data_pedido_parse_string = dtfOut.print(data_pedido_parse_utc);
        String data_pagamento_parse = dtfOut.print(vencimento_parse_utc);

        DateTime vencimento_parse = dtfOut.parseDateTime(vencimento_parse_string);
        DateTime data_pedido_parse = dtfOut.parseDateTime(data_pedido_parse_string);

        //calculamos o total de dias para mostramos na tela inicial antes do usuario-2 aceitar ou recusar o pedido recebido
        Days d = Days.daysBetween(data_pedido_parse, vencimento_parse);
        int dias = d.getDays();

        Log.i("Enviados","dias = "+dias);

        double juros_mensal = Double.parseDouble(item.getValor()) * (0.00066333 * dias);
        //double taxa_fixa = Double.parseDouble(item.getValor_servico());
        double valor_total = juros_mensal +  Double.parseDouble(item.getValor());

        Locale ptBr = new Locale("pt", "BR");
        NumberFormat nf = NumberFormat.getCurrencyInstance(ptBr);

        String juros_total_formatado = nf.format (valor_total);
        //String valor_fixo_formatado = nf.format (taxa_fixa);

        tv_data_pagamento_child.setText(data_pagamento_parse.substring(0, data_pagamento_parse.length() - 5));
        tv_dias_corridos_child.setText(String.valueOf(dias));
        tv_taxa_juros_am_child.setText("1.99%");
        //tv_valor_taxa_servico_child.setText(valor_fixo_formatado);
        tv_valor_total_child.setText(juros_total_formatado);

        btn_cancelar_pedido_antes_resp_child.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Click","click 2");

                //cancela pagamento cielo
                AlteraPagamento cp = new AlteraPagamento();
                cp.setClientAcount(KeyAccountPagamento.CLIENT_ACCOUNT);
                cp.setClientKey(KeyAccountPagamento.CLIENT_KEY);
                cp.setOptionId("9999");
                cp.setPaymentId(trans_global.getPagamento().getPayment_id_first());
                cp.setNewValue(trans_global.getPagamento().getAmount_first());

                //antes de finalmente editar a transacao, cancelamos o pedido na cielo
                new AlteraPagamentoService(TransacaoEnvAdapter.this,cp).execute();

                progress_bar_btn.setVisibility(View.VISIBLE);
                btn_cancelar_pedido_antes_resp_child.setEnabled(false);

            }
        });

    }

    @Override
    public void retornoStringWebServicePagamento(String result) {
        Log.i("Script","retornoStringWebServicePagamento");
        boolean success = false;

        try {
            JSONObject jObject = new JSONObject(result); // json
            boolean verifica_campo = jObject.has("ReasonMessage"); // check if exist
            if(verifica_campo){
                String msg = jObject.getString("ReasonMessage");
                if(msg.equals("Successful")){
                    success = true;
                }
            }

        }catch (Exception e){
            Log.i("Script","Exception retornoStringWebServicePagamento = "+e);
        }

        //se cancelemento sucesso
        if(success) {
            Transacao trans = new Transacao();
            trans.setId_trans(trans_global.getId_trans());
            trans.setStatus_transacao(String.valueOf(Transacao.ENVIO_CANCELADO_ANTES_RESPOSTA));
            trans.setData_recusada(hoje_string);
            trans.setData_pagamento("");
            trans.setValor_multa("0");
            trans.setValor_juros_mensal("0");
            trans.setValor_juros_mora("0");
            trans.setId_contrato(trans_global.getId_contrato());
            trans.setId_recibo("");

            //esse valor sera passado para o metodo notificacao
            status_transacao = Transacao.ENVIO_CANCELADO_ANTES_RESPOSTA;

            //adicionamos no historico a data que o pediddo esta sendo cancelado
            List<Historico> list_hist;
            if (trans_global.getHistorico() == null) {
                list_hist = new ArrayList<Historico>();
            } else {
                list_hist = trans_global.getHistorico();
            }

            Historico hist = new Historico();
            hist.setData(hoje_string);
            hist.setStatus_transacao(String.valueOf(Transacao.PEDIDO_RECUSADO));

            list_hist.add(hist);

            trans.setHistorico(list_hist);

            new EditaTransacaoResposta(trans, trans_global.getUsu1(), trans_global.getUsu2(), this).execute();
        }else{
            mensagem("Houve um erro!","Olá, parece que tivemos algum problema no cancelamento do pagamento do pedido, por favor tente novamente. Se o erro" +
                    " persistir favor entrar em contato com InBanker","Ok");
            progress_bar_btn.setVisibility(View.GONE);
            btn_cancelar_pedido_antes_resp_child.setEnabled(true);
        }
    }
    public void retornoStringWebService(String result){

        Log.i("webservice","resultado edita transao = "+result);

        //btn_confirma_recebimento_child.setEnabled(true);

        if(result.equals("sucesso_edit")){

            //busca token do usuario 2 para enviarmos notificacao
            new BuscaUsuarioCPF(trans_global.getUsu2(),_context,this).execute();


        }else{
            mensagem("Houve um erro!","Olá, parece que tivemos algum problema de conexão, por favor tente novamente.","Ok");

            progress_bar_btn.setVisibility(View.GONE);
            btn_cancelar_pedido_antes_resp_child.setEnabled(true);
        }

    }

    @Override
    public void retornoUsuarioWebService(Usuario usu) {

        progress_bar_btn.setVisibility(View.GONE);

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

        if(!usu.getToken_gcm().equals("")) {
            //envia notificacao
            new EnviaNotificacao(trans, usu.getToken_gcm()).execute();

            if (status_transacao == Transacao.CONFIRMADO_RECEBIMENTO)
                mensagemIntent("InBanker", "Parabéns, você confirmou o recebimento do valor solicitado. Ao efetuar o pagamento de quitação, peça que seu amigo(a) " + trans_global.getNome_usu2() + " confirme o recebimento do valor.", "Ok");
            else if (status_transacao == Transacao.ENVIO_CANCELADO_ANTES_RESPOSTA)
                mensagemIntent("InBanker", "Você acaba de cancelar o pedido que foi enviado ao seu amigo(a) " + trans_global.getNome_usu2() + ".", "Ok");
            else if (status_transacao == Transacao.ENVIO_CANCELADO_ANTES_RECEBIMENTO)
                mensagemIntent("InBanker", "Você acaba de cancelar o pedido que foi enviado ao seu amigo(a) " + trans_global.getNome_usu2() + ".", "Ok");
        }else{
            if (status_transacao == Transacao.CONFIRMADO_RECEBIMENTO)
                mensagemIntent("InBanker", "Parabéns, você confirmou o recebimento do valor solicitado. Ao efetuar o pagamento de quitação, peça que seu amigo(a) " + trans_global.getNome_usu2() + " confirme o recebimento do valor.", "Ok");
            else if (status_transacao == Transacao.ENVIO_CANCELADO_ANTES_RESPOSTA)
                mensagemIntent("InBanker", "Você acaba de cancelar o pedido que foi enviado ao seu amigo(a) " + trans_global.getNome_usu2() + ".", "Ok");
            else if (status_transacao == Transacao.ENVIO_CANCELADO_ANTES_RECEBIMENTO)
                mensagemIntent("InBanker", "Você acaba de cancelar o pedido que foi enviado ao seu amigo(a) " + trans_global.getNome_usu2() + ".", "Ok");
        }
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
    public void retornoUsuarioWebServiceAux(Usuario usu){}
}
