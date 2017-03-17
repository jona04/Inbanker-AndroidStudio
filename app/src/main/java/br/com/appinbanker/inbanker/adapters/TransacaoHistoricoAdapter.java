package br.com.appinbanker.inbanker.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
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
import br.com.appinbanker.inbanker.entidades.Transacao;
import br.com.appinbanker.inbanker.entidades.Usuario;
import br.com.appinbanker.inbanker.interfaces.WebServiceReturnString;
import br.com.appinbanker.inbanker.interfaces.WebServiceReturnUsuario;
import br.com.appinbanker.inbanker.sqlite.BancoControllerUsuario;
import br.com.appinbanker.inbanker.sqlite.CriandoBanco;
import br.com.appinbanker.inbanker.webservice.BuscaUsuarioCPF;
import br.com.appinbanker.inbanker.webservice.EditaTransacao;
import br.com.appinbanker.inbanker.webservice.EditaTransacaoResposta;
import br.com.appinbanker.inbanker.webservice.EnviaNotificacao;

/**
 * Created by jonatasilva on 29/12/16.
 */

public class TransacaoHistoricoAdapter extends BaseExpandableListAdapter{

    TextView tv_data_pagamento_child ;
    TextView tv_dias_corridos_child;
    TextView tv_taxa_juros_am_child;
    TextView tv_valor_multa_child;
    //TextView tv_valor_taxa_servico_child;
    //TextView tv_taxa;
    TextView tv_valor_total_child;
    TextView tv_valor_juros_totais_child;

    TextView tv_tipo_finalizado_child;

    private Context _context;
    private List<Transacao> _listDataHeader; // header titles
    private HashMap<String, Transacao> _listDataChild; // header child
    public TransacaoHistoricoAdapter(Context context, List<Transacao> listDataHeader, HashMap<String,Transacao> listDataChild) {
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
                    .inflate(R.layout.adapter_historico_child, parent, false);
        }
        tv_valor_juros_totais_child = (TextView) convertView.findViewById(R.id.tv_valor_juros_totais);
        tv_data_pagamento_child  = (TextView) convertView.findViewById(R.id.tv_data_pagamento);
        tv_dias_corridos_child  = (TextView) convertView.findViewById(R.id.tv_dias_corridos);
        tv_taxa_juros_am_child  = (TextView) convertView.findViewById(R.id.tv_taxa_juros_am);
        tv_valor_multa_child = (TextView) convertView.findViewById(R.id.tv_valor_multa);
        //tv_valor_taxa_servico_child  = (TextView) convertView.findViewById(R.id.tv_valor_taxa_servico);
       // tv_taxa  = (TextView) convertView.findViewById(R.id.tv_taxa);
        tv_valor_total_child  = (TextView) convertView.findViewById(R.id.tv_valor_total);
        tv_tipo_finalizado_child  = (TextView) convertView.findViewById(R.id.tv_tipo_finalizado);

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
        Transacao trans_global = (Transacao) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.adapter_historico_header, null);
        }

        Locale ptBr = new Locale("pt", "BR");
        NumberFormat nf = NumberFormat.getCurrencyInstance(ptBr);

        TextView tv_data_pedido = (TextView)convertView.findViewById(R.id.tv_data_pedido);
        TextView tv_nome_usuario = (TextView)convertView.findViewById(R.id.tv_nome_usuario);
        TextView tv_valor_pedido = (TextView)convertView.findViewById(R.id.tv_valor_pedido);
        TextView tv_valor_juros = (TextView) convertView.findViewById(R.id.tv_valor_juros);
        TextView tv_tipo_retorno = (TextView) convertView.findViewById(R.id.tv_tipo_retorno);

        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        DateTimeFormatter dtfOut = DateTimeFormat.forPattern("dd/MM/yyyy");

        DateTime data_pedido_parse_utc = fmt.parseDateTime(trans_global.getDataPedido());

        String data_pedido_parse_string = dtfOut.print(data_pedido_parse_utc);

        double redimento = Double.parseDouble(trans_global.getValor_juros_mora())
                + Double.parseDouble(trans_global.getValor_multa())
                + Double.parseDouble(trans_global.getValor_juros_mensal());

        if(!trans_global.getData_recusada().equals(""))
            redimento = 0.0;

        String juros_total_formatado = nf.format (redimento);

        tv_valor_juros.setText(juros_total_formatado);
        //tv_data_pedido.setTypeface(null, Typeface.BOLD);
        tv_data_pedido.setText(data_pedido_parse_string.substring(0, data_pedido_parse_string.length() - 5));
        tv_nome_usuario.setText(trans_global.getNome_usu2());
        tv_valor_pedido.setText(nf.format(Double.parseDouble(trans_global.getValor())));

        //pega cpf do usuario online para fazer ajustes na lista
        BancoControllerUsuario crud = new BancoControllerUsuario(_context);
        Cursor cursor = crud.carregaDados();
        String cpf = cursor.getString(cursor.getColumnIndexOrThrow(CriandoBanco.CPF));

        //usamos para expandir a lista correta
        //ExpandableListView mExpandableListView = (ExpandableListView) parent;

        if(cpf.equals(trans_global.getUsu1())){
            tv_valor_juros.setTextColor(ColorStateList.valueOf(_context.getResources().getColor(R.color.colorRed)));
            tv_tipo_retorno.setText("Juros");
            tv_nome_usuario.setText(trans_global.getNome_usu2());

        }else{
            tv_valor_juros.setTextColor(ColorStateList.valueOf(_context.getResources().getColor(R.color.colorGreen)));
            tv_tipo_retorno.setText("Rendimentos");
            tv_nome_usuario.setText(trans_global.getNome_usu1());
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

        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        DateTimeFormatter dtfOut = DateTimeFormat.forPattern("dd/MM/yyyy");
        //DateTimeFormatter dtfOut_hora = DateTimeFormat.forPattern("HH:mm:ss");

        DateTime data_pedido_parse_utc = fmt.parseDateTime(item.getDataPedido());

        String data_pedido_parse_string = dtfOut.print(data_pedido_parse_utc);
        DateTime data_pedido_parse = dtfOut.parseDateTime(data_pedido_parse_string);

        //calculamos o total de dias para mostramos na tela inicial antes do usuario-2 aceitar ou recusar o pedido recebido
        Days d;
        DateTime data_finalizado;
        int dias = 0;
        if(!item.getData_pagamento().equals("")){ //verifica se o pedido foi quitado ou cancelado
            DateTime data_finalizado_parse_utc = fmt.parseDateTime(item.getData_pagamento());
            String data_finalizado_string = dtfOut.print(data_finalizado_parse_utc);
            data_finalizado = dtfOut.parseDateTime(data_finalizado_string);
            d = Days.daysBetween(data_pedido_parse, data_finalizado);
            dias = d.getDays();

            tv_data_pagamento_child.setText(data_finalizado_string.substring(0, data_finalizado_string.length() - 5));
            tv_tipo_finalizado_child.setText("Pagamento");
        }else{
            DateTime data_finalizado_parse_utc = fmt.parseDateTime(item.getData_recusada());
            String data_finalizado_string = dtfOut.print(data_finalizado_parse_utc);
            data_finalizado = dtfOut.parseDateTime(data_finalizado_string);
            d = Days.daysBetween(data_pedido_parse, data_finalizado);
            dias = d.getDays();

            tv_data_pagamento_child.setText(data_finalizado_string.substring(0, data_finalizado_string.length() - 5));
            tv_tipo_finalizado_child.setText(" Recusado  "); //colocamos espaço para regular no layout
        }

        double valor_total;

        if(!item.getData_recusada().equals(""))
            valor_total = 0.0;

        Locale ptBr = new Locale("pt", "BR");
        NumberFormat nf = NumberFormat.getCurrencyInstance(ptBr);

        double juros_totais = Double.parseDouble(item.getValor_juros_mensal())+Double.parseDouble(item.getValor_juros_mora());

        //String juros_mensal_formatado = nf.format (juros_mensal);
        //String taxa_fixa_formatado = nf.format (Double.parseDouble(item.getValor_servico()));
        String juros_totais_formatado = nf.format (juros_totais);
        String multa_formatado = nf.format (Double.parseDouble(item.getValor_multa()));

        tv_dias_corridos_child.setText(String.valueOf(dias));
        tv_taxa_juros_am_child.setText("1.99%");
        tv_valor_juros_totais_child.setText(juros_totais_formatado);
        //tv_valor_taxa_servico_child.setText(taxa_fixa_formatado);
        tv_valor_multa_child.setText(multa_formatado);

        BancoControllerUsuario crud = new BancoControllerUsuario(_context);
        Cursor cursor = crud.carregaDados();
        String cpf = cursor.getString(cursor.getColumnIndexOrThrow(CriandoBanco.CPF));
        if(cpf.equals(item.getUsu1())) {
            //colocamos o valor da taxa fixa no total do pedido para o usuario que enviou o pedido
            valor_total = Double.parseDouble(item.getValor())
                    + Double.parseDouble(item.getValor_juros_mora())
                    + Double.parseDouble(item.getValor_multa())
                    + Double.parseDouble(item.getValor_juros_mensal());
                    //+ Double.parseDouble(item.getValor_servico())


            tv_dias_corridos_child.setTextColor(ColorStateList.valueOf(_context.getResources().getColor(R.color.colorRed)));
            tv_taxa_juros_am_child.setTextColor(ColorStateList.valueOf(_context.getResources().getColor(R.color.colorRed)));
            tv_valor_multa_child.setTextColor(ColorStateList.valueOf(_context.getResources().getColor(R.color.colorRed)));
            //tv_valor_taxa_servico_child.setTextColor(ColorStateList.valueOf(_context.getResources().getColor(R.color.colorRed)));
            tv_valor_juros_totais_child.setTextColor(ColorStateList.valueOf(_context.getResources().getColor(R.color.colorRed)));
            tv_valor_total_child.setTextColor(ColorStateList.valueOf(_context.getResources().getColor(R.color.colorOrange)));


        }else{
            //tiramos o valor da taxa fixa do total do pedido para o usuario que apenas recebeu o pedido
            valor_total = Double.parseDouble(item.getValor())
                    + Double.parseDouble(item.getValor_juros_mora())
                    + Double.parseDouble(item.getValor_multa())
                    + Double.parseDouble(item.getValor_juros_mensal());

            //deixamos tava de servico invisievl para que recebeu o pedido
            //tv_valor_taxa_servico_child.setVisibility(View.GONE);
            //tv_taxa.setVisibility(View.GONE);

            tv_dias_corridos_child.setTextColor(ColorStateList.valueOf(_context.getResources().getColor(R.color.colorGreen)));
            tv_taxa_juros_am_child.setTextColor(ColorStateList.valueOf(_context.getResources().getColor(R.color.colorGreen)));
            tv_valor_multa_child.setTextColor(ColorStateList.valueOf(_context.getResources().getColor(R.color.colorGreen)));
            //tv_valor_taxa_servico_child.setTextColor(ColorStateList.valueOf(_context.getResources().getColor(R.color.colorGreen)));
            tv_valor_juros_totais_child.setTextColor(ColorStateList.valueOf(_context.getResources().getColor(R.color.colorGreen)));
            tv_valor_total_child.setTextColor(ColorStateList.valueOf(_context.getResources().getColor(R.color.colorOrange)));

        }

        String valor_total_formatado = nf.format (valor_total);
        tv_valor_total_child.setText(valor_total_formatado);
    }



    public void mensagem(String titulo,String corpo,String botao)
    {
        AlertDialog.Builder mensagem = new AlertDialog.Builder(_context);
        mensagem.setTitle(titulo);
        mensagem.setMessage(corpo);
        mensagem.setNeutralButton(botao,null);
        mensagem.show();
    }
}
