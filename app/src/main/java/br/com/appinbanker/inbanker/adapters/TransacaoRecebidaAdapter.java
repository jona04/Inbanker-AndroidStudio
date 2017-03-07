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
import org.joda.time.DateTimeZone;
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
import br.com.appinbanker.inbanker.interfaces.WebServiceReturnStringHora;
import br.com.appinbanker.inbanker.interfaces.WebServiceReturnStringPagamento;
import br.com.appinbanker.inbanker.interfaces.WebServiceReturnUsuario;
import br.com.appinbanker.inbanker.webservice.AlteraPagamentoService;
import br.com.appinbanker.inbanker.webservice.BuscaUsuarioCPF;
import br.com.appinbanker.inbanker.webservice.EditaTransacao;
import br.com.appinbanker.inbanker.webservice.EditaTransacaoResposta;
import br.com.appinbanker.inbanker.webservice.EnviaNotificacao;
import br.com.appinbanker.inbanker.webservice.ObterHora;

/**
 * Created by jonatasilva on 29/12/16.
 */

public class TransacaoRecebidaAdapter extends BaseExpandableListAdapter implements WebServiceReturnString,WebServiceReturnUsuario,WebServiceReturnStringPagamento {

    TextView tv_data_pagamento_child ;
    TextView tv_dias_faltando_child;
    TextView tv_taxa_juros_am_child;
    TextView tv_valor_iof_child;
    TextView tv_valor_total_child;
    LinearLayout ll_resposta_pedido_child;
    private Button btn_aceita_pedido,btn_recusa_pedido;
    ProgressBar progress_bar_btn;

    Transacao trans_global;

    TextView msg_ver_pedido_child;

    String hoje_string;
    private boolean aceitou_pedido = false;

    private Context _context;
    private List<Transacao> _listDataHeader; // header titles
    private HashMap<String, Transacao> _listDataChild; // header child
    public TransacaoRecebidaAdapter(Context context, List<Transacao> listDataHeader, HashMap<String,Transacao> listDataChild,String hoje) {
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
        trans_global = (Transacao) getChild(groupPosition, childPosition);
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.adapter_transacao_recebida_child, parent, false);
        }
        ll_resposta_pedido_child = (LinearLayout) convertView.findViewById(R.id.ll_resposta_pedido);
        msg_ver_pedido_child = (TextView)convertView.findViewById(R.id.msg_ver_pedido);
        tv_data_pagamento_child  = (TextView) convertView.findViewById(R.id.tv_data_pagamento);
        tv_dias_faltando_child  = (TextView) convertView.findViewById(R.id.tv_dias_faltando);
        tv_taxa_juros_am_child  = (TextView) convertView.findViewById(R.id.tv_taxa_juros_am);
        tv_valor_iof_child = (TextView) convertView.findViewById(R.id.tv_valor_iof);
        tv_valor_total_child  = (TextView) convertView.findViewById(R.id.tv_valor_total);
        btn_aceita_pedido = (Button) convertView.findViewById(R.id.btn_aceita_pedido);
        btn_recusa_pedido = (Button) convertView.findViewById(R.id.btn_recusa_pedido);
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

        //calculamos a diferença de dias entre a data atual ate a data do pedido para calcularmos o juros
        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        DateTimeFormatter dtfOut = DateTimeFormat.forPattern("dd/MM/yyyy");
        DateTimeFormatter dtfOut_hora = DateTimeFormat.forPattern("HH:mm:ss");

        DateTime hora_pedido_parse = fmt.parseDateTime(item.getDataPedido());
        DateTime vencimento_parse_utc = fmt.parseDateTime(item.getVencimento());
        DateTime data_pedido_parse_utc = fmt.parseDateTime(item.getDataPedido());

        String vencimento_parse_string = dtfOut.print(vencimento_parse_utc);
        String data_pedido_parse_string = dtfOut.print(data_pedido_parse_utc);
        String hora_pedido = dtfOut_hora.print(hora_pedido_parse);

        DateTime vencimento_parse = dtfOut.parseDateTime(vencimento_parse_string);
        DateTime data_pedido_parse = dtfOut.parseDateTime(data_pedido_parse_string);

        //calculamos o total de dias para mostramos na tela inicial antes do usuario-2 aceitar ou recusar o pedido recebido
        Days d = Days.daysBetween(data_pedido_parse, vencimento_parse);
        int dias = d.getDays();

        double redimento = Double.parseDouble(item.getValor()) * (0.00066333 * dias);
        String juros_total_formatado = nf.format (redimento);

        tv_data_pedido.setText(data_pedido_parse_string.substring(0, data_pedido_parse_string.length() - 5));
        tv_nome_usuario.setText(item.getNome_usu1());
        tv_valor_pedido.setText(nf.format(Double.parseDouble(item.getValor())));
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
        //calculamos a diferença de dias entre a data atual ate a data do pedido para calcularmos o juros
        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        DateTimeFormatter dtfOut = DateTimeFormat.forPattern("dd/MM/yyyy");

        DateTime vencimento_parse_utc = fmt.parseDateTime(trans_global.getVencimento());
        DateTime data_pedido_parse_utc = fmt.parseDateTime(trans_global.getDataPedido());
        DateTime hoje_parse_utc = fmt.parseDateTime(hoje_string);

        String vencimento_parse_string = dtfOut.print(vencimento_parse_utc);
        String data_pedido_parse_string = dtfOut.print(data_pedido_parse_utc);
        String hoje_parse_string = dtfOut.print(hoje_parse_utc);

        DateTime vencimento_parse = dtfOut.parseDateTime(vencimento_parse_string);
        DateTime data_pedido_parse = dtfOut.parseDateTime(data_pedido_parse_string);
        DateTime hoje_parse = dtfOut.parseDateTime(hoje_parse_string);

        //calculamos o total de dias para mostramos na tela inicial antes do usuario-2 aceitar ou recusar o pedido recebido
        Days d = Days.daysBetween(data_pedido_parse, vencimento_parse);
        int dias = d.getDays();

        //calculamos o total de dias para mostramos na tela inicial antes do usuario-2 aceitar ou recusar o pedido recebido
        Days d_faltando = Days.daysBetween(hoje_parse, vencimento_parse);
        int dias_faltando = d_faltando.getDays();

        //Log.i("Enviados","dias = "+dias+ " - " +hoje);

        double juros_mensal = Double.parseDouble(item.getValor()) * (0.00066333 * dias);
        double valor_total = juros_mensal +  Double.parseDouble(item.getValor());

        Locale ptBr = new Locale("pt", "BR");
        NumberFormat nf = NumberFormat.getCurrencyInstance(ptBr);

        String juros_total_formatado = nf.format (valor_total);

        tv_data_pagamento_child.setText(vencimento_parse_string.substring(0, vencimento_parse_string.length() - 5));
        tv_dias_faltando_child.setText(String.valueOf(dias_faltando));
        tv_taxa_juros_am_child.setText("1.99%");
        tv_valor_iof_child.setText("0,00");
        tv_valor_total_child.setText(juros_total_formatado);

        int status_transacao = Integer.parseInt(item.getStatus_transacao());


        switch (status_transacao){
            case Transacao.AGUARDANDO_RESPOSTA:
                ll_resposta_pedido_child.setVisibility(View.VISIBLE);
                msg_ver_pedido_child.setVisibility(View.GONE);

                break;
            case Transacao.PEDIDO_ACEITO:

                ll_resposta_pedido_child.setVisibility(View.GONE);
                msg_ver_pedido_child.setVisibility(View.VISIBLE);
                msg_ver_pedido_child.setText("Você esta aguardando que seu amigo(a) " + trans_global.getNome_usu1() + " confirme o recebimento do valor.");


                break;
            case Transacao.QUITACAO_SOLICITADA:

                ll_resposta_pedido_child.setVisibility(View.GONE);
                break;

        }

        btn_aceita_pedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Click","click 1");

                aceitou_pedido = true;

                escondeBotoes();

                Transacao trans = new Transacao();
                trans.setId_trans(trans_global.getId_trans());
                trans.setStatus_transacao(String.valueOf(Transacao.PEDIDO_ACEITO));

                List<Historico> list_hist;
                if(item.getHistorico() == null){
                    list_hist = new ArrayList<Historico>();
                }else{
                    list_hist = item.getHistorico();
                }

                Historico hist = new Historico();
                hist.setData(hoje_string);
                hist.setStatus_transacao(String.valueOf(Transacao.PEDIDO_ACEITO));

                list_hist.add(hist);

                trans.setHistorico(list_hist);

                new EditaTransacao(trans,trans_global.getUsu1(),trans_global.getUsu2(),TransacaoRecebidaAdapter.this).execute();

            }
        });
        btn_recusa_pedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Click","click 2");

                escondeBotoes();

                //cancela pagamento cielo
                AlteraPagamento cp = new AlteraPagamento();
                cp.setClientAcount(KeyAccountPagamento.CLIENT_ACCOUNT);
                cp.setClientKey(KeyAccountPagamento.CLIENT_KEY);
                cp.setOptionId("9999");
                cp.setPaymentId(trans_global.getPagamento().getPayment_id_first());
                cp.setNewValue(trans_global.getPagamento().getAmount_first());

                //metodoEditaTransResp(cp);

                //antes de finalmente editar a transacao, cancelamos o pedido na cielo
                new AlteraPagamentoService(TransacaoRecebidaAdapter.this,cp).execute();

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
            trans.setStatus_transacao(String.valueOf(Transacao.PEDIDO_RECUSADO));
            trans.setData_recusada(hoje_string);
            trans.setData_pagamento("");

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
            mostraBotoes();
        }
    }

    @Override
    public void retornoStringWebService(String result){
        Log.i("webservice","resultado edita transao = "+result);

        if(result!=null) {

            if (result.equals("sucesso_edit")) {

                //busca token do usuario 1
                new BuscaUsuarioCPF(trans_global.getUsu1(), _context, this).execute();

            } else {
                mensagem("Houve um erro!", "Olá, parece que tivemos algum problema de conexão, por favor tente novamente.", "Ok");
                mostraBotoes();
            }
        }else{
            mensagem("Houve critico!", "Olá, parece que tivemos algum problema de conexão, por favor tente novamente.", "Ok");
            mostraBotoes();
        }

    }

    public void  escondeBotoes(){
        progress_bar_btn.setVisibility(View.VISIBLE);
        btn_recusa_pedido.setEnabled(false);
        btn_aceita_pedido.setEnabled(false);
    }
    public void mostraBotoes(){
        progress_bar_btn.setVisibility(View.GONE);
        btn_recusa_pedido.setEnabled(true);
        btn_aceita_pedido.setEnabled(true);
    }

    @Override
    public void retornoUsuarioWebService(Usuario usu){

        mostraBotoes();

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

            //usado para enviar a mensagem correta ao outro usuario
            trans.setStatus_transacao(String.valueOf(Transacao.PEDIDO_ACEITO));

            if (!usu.getToken_gcm().equals("")){
                //envia notificacao
                new EnviaNotificacao(trans, usu.getToken_gcm()).execute();
            }

            mensagemIntent("InBanker", "Parabéns, você aceitou o pedido. Ao efetuar o pagamento, peça que seu amigo(a) " + trans_global.getNome_usu1() + " confirme o recebimento do valor.", "Ok");
        }else {
            trans.setStatus_transacao(String.valueOf(Transacao.PEDIDO_RECUSADO));

            //usado para enviar a mensagem correta ao outro usuario
            trans.setData_recusada(hoje_string);

            if (!usu.getToken_gcm().equals("")) {
                //envia notificacao
                new EnviaNotificacao(trans, usu.getToken_gcm()).execute();
            }

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
    public void retornoUsuarioWebServiceAux(Usuario usu){}


}
