package br.com.appinbanker.inbanker.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.renderscript.Sampler;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntegerRes;
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
import br.com.appinbanker.inbanker.VerPagamentoPendente;
import br.com.appinbanker.inbanker.entidades.Transacao;
import br.com.appinbanker.inbanker.entidades.Usuario;
import br.com.appinbanker.inbanker.interfaces.WebServiceReturnString;
import br.com.appinbanker.inbanker.interfaces.WebServiceReturnUsuario;
import br.com.appinbanker.inbanker.sqlite.BancoControllerUsuario;
import br.com.appinbanker.inbanker.sqlite.CriandoBanco;
import br.com.appinbanker.inbanker.util.AllSharedPreferences;
import br.com.appinbanker.inbanker.webservice.BuscaUsuarioCPF;
import br.com.appinbanker.inbanker.webservice.EditaTransacao;
import br.com.appinbanker.inbanker.webservice.EnviaNotificacao;

/**
 * Created by jonatasilva on 29/12/16.
 */

public class TransacaoPendenteAdapter extends BaseExpandableListAdapter implements WebServiceReturnUsuario, WebServiceReturnString {

    //TextView tv_data_pedido_child;
    TextView tv_data_pagamento_child ;
    //TextView tv_nome_usuario_child;
    TextView tv_dias_corridos_child;
    TextView tv_taxa_juros_am_child;
    //TextView tv_taxa_juros_child;
    TextView tv_valor_multa_child;
    TextView tv_valor_taxa_servico_child;
    TextView tv_valor_total_child;
    //TextView tv_valor_pedido_child;
    LinearLayout ll_solicita_quitacao_child;
    private Button btn_solicita_quitacao_child;

    TextView msg_ver_pedido_child;

    ProgressBar progress_bar_btn;

    Transacao trans_global;
    int status_transacao;

    private Context _context;
    private List<Transacao> _listDataHeader; // header titles
    private HashMap<String, Transacao> _listDataChild; // header child
    public TransacaoPendenteAdapter(Context context, List<Transacao> listDataHeader, HashMap<String,Transacao> listDataChild) {
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
                    .inflate(R.layout.adapter_transacao_pendente_child, parent, false);
        }
        //ll_confirma_recebimento_child = (LinearLayout) convertView.findViewById(R.id.ll_confirma_recebimento);
        ll_solicita_quitacao_child = (LinearLayout) convertView.findViewById(R.id.ll_solicita_quitacao);
        //tv_data_pedido_child = (TextView)convertView.findViewById(R.id.tv_data_pedido);
        tv_data_pagamento_child  = (TextView) convertView.findViewById(R.id.tv_data_pagamento);
        //tv_nome_usuario_child  = (TextView) convertView.findViewById(R.id.tv_nome_usuario);
        tv_dias_corridos_child  = (TextView) convertView.findViewById(R.id.tv_dias_corridos);
        tv_taxa_juros_am_child  = (TextView) convertView.findViewById(R.id.tv_taxa_juros_am);
        //tv_taxa_juros_child  = (TextView) convertView.findViewById(R.id.tv_taxa_juros);
        tv_valor_multa_child  = (TextView) convertView.findViewById(R.id.tv_valor_multa);
        tv_valor_taxa_servico_child  = (TextView) convertView.findViewById(R.id.tv_valor_taxa_servico);
        tv_valor_total_child  = (TextView) convertView.findViewById(R.id.tv_valor_total);
        msg_ver_pedido_child  = (TextView) convertView.findViewById(R.id.msg_ver_pedido);
        btn_solicita_quitacao_child = (Button) convertView.findViewById(R.id.btn_solicita_quitacao);
        progress_bar_btn = (ProgressBar) convertView.findViewById(R.id.progress_bar_btn);

        configView(trans_global);

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
            convertView = infalInflater.inflate(R.layout.adapter_transacao_pendente_header, null);
        }

        TextView tv_data_pedido = (TextView)convertView.findViewById(R.id.tv_data_pedido);
        TextView tv_nome_usuario = (TextView)convertView.findViewById(R.id.tv_nome_usuario);
        TextView tv_valor_pedido = (TextView)convertView.findViewById(R.id.tv_valor_pedido);
        TextView tv_valor_juros = (TextView) convertView.findViewById(R.id.tv_valor_juros);
        TextView tv_tipo_retorno = (TextView) convertView.findViewById(R.id.tv_tipo_retorno);

        Locale ptBr = new Locale("pt", "BR");
        NumberFormat nf = NumberFormat.getCurrencyInstance(ptBr);

        //tv_data_pedido.setTypeface(null, Typeface.BOLD);
        tv_data_pedido.setText(item.getDataPedido().substring(0, item.getDataPedido().length() - 5));
        tv_valor_pedido.setText(nf.format(Double.parseDouble(item.getValor())));

        //calculamos a diferença de dias entre a data atual ate a data do pedido para calcularmos o juros
        DateTimeFormatter fmt = DateTimeFormat.forPattern("dd/MM/YYYY");
        DateTime hoje = new DateTime();
        DateTime data_pedido_parse = fmt.parseDateTime(item.getDataPedido());
        DateTime vencimento_parse = fmt.parseDateTime(item.getVencimento());

        //calculamos o total de dias para mostramos na tela inicial antes do usuario-2 aceitar ou recusar o pedido recebido
        Days d = Days.daysBetween(data_pedido_parse, hoje);
        int dias = d.getDays();

        Double multa_atraso = 0.0;
        Double juros_mora = 0.0;
        if(hoje.isAfter(vencimento_parse.plusDays(1))) {

            d = Days.daysBetween(data_pedido_parse, vencimento_parse);
            dias = d.getDays();

            Days d_atraso = Days.daysBetween(vencimento_parse, hoje);
            int dias_atraso = d_atraso.getDays();

            Log.i("PagamentoPendente","dias de atraso = "+dias_atraso);

            juros_mora = Double.parseDouble(item.getValor()) * (0.00099667 * dias_atraso);
            multa_atraso = Double.parseDouble(item.getValor())*0.1;
        }


        double redimento = juros_mora + multa_atraso + Double.parseDouble(item.getValor()) * (0.00066333 * dias);
        String juros_total_formatado = nf.format (redimento);

        tv_valor_juros.setText(juros_total_formatado);

        //pega cpf do usuario online para fazer ajustes na lista
        BancoControllerUsuario crud = new BancoControllerUsuario(_context);
        Cursor cursor = crud.carregaDados();
        String cpf = cursor.getString(cursor.getColumnIndexOrThrow(CriandoBanco.CPF));

        //usamos para expandir a lista correta
        //ExpandableListView mExpandableListView = (ExpandableListView) parent;

        if(cpf.equals(item.getUsu1())){
            tv_valor_juros.setTextColor(ColorStateList.valueOf(_context.getResources().getColor(R.color.colorRed)));
            tv_tipo_retorno.setText("Juros");
            tv_nome_usuario.setText(item.getNome_usu2());

            //verificamos o status da transacao para expandir ou nao a lista
            /*switch (Integer.parseInt(item.getStatus_transacao())) {
                case Transacao.CONFIRMADO_RECEBIMENTO:
                    mExpandableListView.expandGroup(groupPosition);
                    break;
                case Transacao.RESP_QUITACAO_SOLICITADA_RECUSADA:
                    mExpandableListView.expandGroup(groupPosition);
                    break;
            }*/

        }else{
            tv_valor_juros.setTextColor(ColorStateList.valueOf(_context.getResources().getColor(R.color.colorGreen)));
            tv_tipo_retorno.setText("Rendimentos");
            tv_nome_usuario.setText(item.getNome_usu1());
        }

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
        Days d = Days.daysBetween(data_pedido_parse, hoje);
        int dias = d.getDays();

        //botamos aqui em cima para nao sofre alteracao caso o usuario passe do prazo de vencimento, linha 257
        tv_dias_corridos_child.setText(String.valueOf(dias));

        Locale ptBr = new Locale("pt", "BR");
        NumberFormat nf = NumberFormat.getCurrencyInstance(ptBr);

        Double multa_atraso = 0.0;
        Double juros_mora = 0.0;
        if(hoje.isAfter(vencimento_parse.plusDays(1))) {

            d = Days.daysBetween(data_pedido_parse, vencimento_parse);
            dias = d.getDays();

            Days d_atraso = Days.daysBetween(vencimento_parse, hoje);
            int dias_atraso = d_atraso.getDays();

            Log.i("PagamentoPendente","dias de atraso = "+dias_atraso);

            juros_mora = Double.parseDouble(item.getValor()) * (0.00099667 * dias_atraso);

            multa_atraso = Double.parseDouble(item.getValor())*0.1;
            tv_valor_multa_child.setText(String.valueOf(nf.format(multa_atraso)));
            //tv_dias_atraso.setText(String.valueOf(dias_atraso));
        }else{
            tv_valor_multa_child.setText("R$0,00");
        }

        double juros_mensal = Double.parseDouble(item.getValor()) * (0.00066333 * dias);
        double valor_total = juros_mora+multa_atraso + juros_mensal +  Double.parseDouble(item.getValor());

        String juros_total_formatado = nf.format (valor_total);

        /*if(dias_faltando < 0){
            Double multa_atraso = Double.parseDouble(item.getValor())*0.1;
            //tv_valor_multa_child.setText(String.valueOf(nf.format(multa_atraso)));
        }*/



        tv_data_pagamento_child.setText(item.getVencimento().substring(0, item.getVencimento().length() - 5));

        tv_taxa_juros_am_child.setText("1.99%");

        tv_valor_taxa_servico_child.setText("R$0,00");
        tv_valor_total_child.setText(juros_total_formatado);

        status_transacao = Integer.parseInt(trans_global.getStatus_transacao());

        BancoControllerUsuario crud = new BancoControllerUsuario(_context);
        Cursor cursor = crud.carregaDados();
        String cpf = cursor.getString(cursor.getColumnIndexOrThrow(CriandoBanco.CPF));
        if(cpf.equals(item.getUsu1())){
            switch (status_transacao){
                case Transacao.CONFIRMADO_RECEBIMENTO:
                    //msg_ver_pedido.setText("Quando você realizar a quitação da dívida, aperte no botão abaixo para dar prosseguimento a transação.");
                    ll_solicita_quitacao_child.setVisibility(View.VISIBLE);

                    msg_ver_pedido_child.setVisibility(View.GONE);
                    break;
                case Transacao.QUITACAO_SOLICITADA:
                    ll_solicita_quitacao_child.setVisibility(View.GONE);

                    msg_ver_pedido_child.setText("Voce esta aguardando "+trans_global.getNome_usu2()+" responder sua solicitaçao de quitaçao da divida.");
                    msg_ver_pedido_child.setVisibility(View.VISIBLE);
                    break;
                case Transacao.RESP_QUITACAO_SOLICITADA_RECUSADA:
                    //msg_ver_pedido.setText(trans_atual.getNome_usu2()+" respondeu negativamente ao seu pedido de quitação da divida. Entre em contato com o mesmo e solicite novamente a quitação.");
                    ll_solicita_quitacao_child.setVisibility(View.VISIBLE);

                    msg_ver_pedido_child.setVisibility(View.GONE);
                    break;

            }

            tv_dias_corridos_child.setTextColor(ColorStateList.valueOf(_context.getResources().getColor(R.color.colorRed)));
            tv_taxa_juros_am_child.setTextColor(ColorStateList.valueOf(_context.getResources().getColor(R.color.colorRed)));
            tv_valor_multa_child.setTextColor(ColorStateList.valueOf(_context.getResources().getColor(R.color.colorRed)));
            tv_valor_taxa_servico_child.setTextColor(ColorStateList.valueOf(_context.getResources().getColor(R.color.colorRed)));

        }else{

            switch (status_transacao) {
                case Transacao.CONFIRMADO_RECEBIMENTO:

                    if(hoje.isAfter(vencimento_parse)) {

                        //Days d_atraso = Days.daysBetween(item.getVencimento(), hoje);
                        //dias_atraso = d_atraso.getDays();

                        //Log.i("PagamentoPendente","dias de atraso = "+dias_atraso);

                        //Double multa_atraso = Double.parseDouble(item.getValor())*0.1;
                        //tv_valor_multa_child.setText(String.valueOf(nf.format(multa_atraso)));
                        //tv_dias_atraso.setText(String.valueOf(dias_atraso));
                    }


                    ll_solicita_quitacao_child.setVisibility(View.GONE);

                    msg_ver_pedido_child.setVisibility(View.VISIBLE);
                    msg_ver_pedido_child.setText("Você esta aguardando que seu amigo(a) " + trans_global.getNome_usu1() + " solicite a confirmação de quitação do empréstimo.");
                    break;
                case Transacao.RESP_QUITACAO_SOLICITADA_RECUSADA:

                    ll_solicita_quitacao_child.setVisibility(View.GONE);

                    msg_ver_pedido_child.setVisibility(View.VISIBLE);
                    msg_ver_pedido_child.setText("Você já recusou uma confirmação de quitação dessa dívida com "+ trans_global.getNome_usu1()+". Agora está aguardando por outra solicitação de quitação.");
                    break;

            }

            tv_dias_corridos_child.setTextColor(ColorStateList.valueOf(_context.getResources().getColor(R.color.colorGreen)));
            tv_taxa_juros_am_child.setTextColor(ColorStateList.valueOf(_context.getResources().getColor(R.color.colorGreen)));
            tv_valor_multa_child.setTextColor(ColorStateList.valueOf(_context.getResources().getColor(R.color.colorGreen)));
            tv_valor_taxa_servico_child.setTextColor(ColorStateList.valueOf(_context.getResources().getColor(R.color.colorGreen)));

        }

        btn_solicita_quitacao_child.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Transacao trans = new Transacao();
                trans.setId_trans(item.getId_trans());
                trans.setStatus_transacao(String.valueOf(Transacao.QUITACAO_SOLICITADA));

                metodoEditaTrans(trans);

                progress_bar_btn.setVisibility(View.VISIBLE);
                btn_solicita_quitacao_child.setEnabled(false);

            }
        });


    }

    public void metodoEditaTrans(Transacao trans){
        new EditaTransacao(trans,trans_global.getUsu1(),trans_global.getUsu2(),this).execute();
    }

    public void retornoStringWebService(String result){

        Log.i("webservice","resultado edita transao = "+result);

        progress_bar_btn.setVisibility(View.GONE);
        btn_solicita_quitacao_child.setEnabled(true);

        if(result.equals("sucesso_edit")){
            //busca token do usuario 2 para enviar notificacao
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
        trans.setId_trans(trans_global.getId_trans());
        trans.setUsu1(trans_global.getUsu1());
        trans.setUsu2(trans_global.getUsu2());
        trans.setDataPedido(trans_global.getDataPedido());
        trans.setValor(trans_global.getValor());
        trans.setVencimento(trans_global.getVencimento());
        trans.setUrl_img_usu1(trans_global.getUrl_img_usu1());
        trans.setUrl_img_usu2(trans_global.getUrl_img_usu2());

        trans.setStatus_transacao(String.valueOf(Transacao.QUITACAO_SOLICITADA));

        //envia notificacao
        new EnviaNotificacao(trans,usu.getToken_gcm()).execute();

        mensagemIntent("InBanker","Você realizou a solicitação de quitação do empréstimo. Peça que seu amigo(a) "+trans_global.getNome_usu2()+" confirme o recebimento do valor.", "Ok");

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
