package br.com.appinbanker.inbanker.fragments_navigation;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import br.com.appinbanker.inbanker.NavigationDrawerActivity;
import br.com.appinbanker.inbanker.R;
import br.com.appinbanker.inbanker.TelaLogin;
import br.com.appinbanker.inbanker.adapters.TransacaoEnvAdapter;
import br.com.appinbanker.inbanker.adapters.TransacaoRecebidaAdapter;
import br.com.appinbanker.inbanker.entidades.AlteraPagamento;
import br.com.appinbanker.inbanker.entidades.Historico;
import br.com.appinbanker.inbanker.entidades.KeyAccountPagamento;
import br.com.appinbanker.inbanker.entidades.Transacao;
import br.com.appinbanker.inbanker.entidades.Usuario;
import br.com.appinbanker.inbanker.interfaces.WebServiceReturnString;
import br.com.appinbanker.inbanker.interfaces.WebServiceReturnStringHora;
import br.com.appinbanker.inbanker.interfaces.WebServiceReturnStringPagamento;
import br.com.appinbanker.inbanker.interfaces.WebServiceReturnUsuario;
import br.com.appinbanker.inbanker.sqlite.BancoControllerUsuario;
import br.com.appinbanker.inbanker.sqlite.CriandoBanco;
import br.com.appinbanker.inbanker.webservice.AlteraPagamentoService;
import br.com.appinbanker.inbanker.webservice.BuscaUsuarioCPF;
import br.com.appinbanker.inbanker.webservice.BuscaUsuarioCPFAux;
import br.com.appinbanker.inbanker.webservice.EditaTransacao;
import br.com.appinbanker.inbanker.webservice.EditaTransacaoResposta;
import br.com.appinbanker.inbanker.webservice.EnviaNotificacao;
import br.com.appinbanker.inbanker.webservice.ObterHora;

public class InicioFragment extends Fragment implements WebServiceReturnStringHora,WebServiceReturnUsuario,WebServiceReturnString,WebServiceReturnStringPagamento {

    TextView badge_notification_ped_rec,badge_notification_pag_pen,badge_notification_ped_env;

    Button btn_pedir_emprestimo, btn_pedidos_recebidos, btn_pedidos_enviados, btn_pagamentos_pendentes;

    ProgressBar progress_bar_inicio;

    ProgressBar progress_bar_dialog_enviados;
    Button btn_recusa_recebimento_dialog;
    Button btn_confirma_recebimento_dialog;
    Transacao trans_global;

    private Transacao trans_global_ped_receb,trans_global_ped_env;

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference usuarioReferencia = databaseReference.child("usuarios");

    Dialog dialog;

    String hoje_string;

    public InicioFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_inicio, container, false);

        progress_bar_inicio = (ProgressBar) view.findViewById(R.id.progress_bar_inicio);
        badge_notification_ped_rec = (TextView) view.findViewById(R.id.badge_notification_ped_rec);
        badge_notification_pag_pen = (TextView) view.findViewById(R.id.badge_notification_pag_pen);
        badge_notification_ped_env = (TextView) view.findViewById(R.id.badge_notification_ped_env);

        btn_pedir_emprestimo = (Button) view.findViewById(R.id.btn_pedir_emprestimo);
        btn_pedidos_enviados = (Button) view.findViewById(R.id.btn_pedidos_enviados);
        btn_pedidos_recebidos = (Button) view.findViewById(R.id.btn_pedidos_recebidos);
        btn_pagamentos_pendentes = (Button) view.findViewById(R.id.btn_pagamentos_pedentes);

        //fazemos uma busca do usuario logando no banco para mostrarmos corretamente as notificações interna nos butons da tela incio
        BancoControllerUsuario crud = new BancoControllerUsuario(getActivity());
        Cursor cursor = crud.carregaDados();
        try {
            String cpf = cursor.getString(cursor.getColumnIndexOrThrow(CriandoBanco.CPF));
            //String id_face = cursor.getString(cursor.getColumnIndexOrThrow(CriandoBanco.ID_FACE));
            if(!cpf.equals("")) {
                new BuscaUsuarioCPF(cpf,getActivity(),this).execute();
                //obterDadosUsuarioFireBase(cpf);
            }else
                progress_bar_inicio.setVisibility(View.INVISIBLE);
        }catch (Exception e){

        }
        btn_pedir_emprestimo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(getActivity(),NavigationDrawerActivity.class);
                Bundle b = new Bundle();
                b.putInt("menu_item", NavigationDrawerActivity.MENU_PEDIR_EMPRESTIMO);
                it.putExtras(b);
                startActivity(it);

                getActivity().finish();

            }
        });
        btn_pedidos_enviados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent it = new Intent(getActivity(),NavigationDrawerActivity.class);
                Bundle b = new Bundle();
                b.putInt("menu_item", NavigationDrawerActivity.MENU_PEDIDOS_ENVIADOS);
                it.putExtras(b);
                startActivity(it);

                getActivity().finish();
            }
        });
        btn_pedidos_recebidos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent it = new Intent(getActivity(),NavigationDrawerActivity.class);
                Bundle b = new Bundle();
                b.putInt("menu_item", NavigationDrawerActivity.MENU_PEDIDOS_RECEBIDOS);
                it.putExtras(b);
                startActivity(it);

                getActivity().finish();
            }
        });
        btn_pagamentos_pendentes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent it = new Intent(getActivity(),NavigationDrawerActivity.class);
                Bundle b = new Bundle();
                b.putInt("menu_item", NavigationDrawerActivity.MENU_PAGAMENTOS_ABERTO);
                it.putExtras(b);
                startActivity(it);

                getActivity().finish();
            }
        });

        return view;
    }


    @Override
    public void retornoUsuarioWebService(Usuario usu) {

        progress_bar_inicio.setVisibility(View.INVISIBLE);

        if(usu != null){

            int count_trans_env = 0;
            int count_trans_rec = 0;
            int count_pag_pen = 0;

            if(usu.getTransacoes_enviadas() != null) {

                for(int i = 0; i < usu.getTransacoes_enviadas().size(); i ++){
                    int status = Integer.parseInt(usu.getTransacoes_enviadas().get(i).getStatus_transacao());
                    if(status < 1 )
                       count_trans_env++;
                    if(status == 1 ) {

                        trans_global_ped_env = usu.getTransacoes_enviadas().get(i);

                        //foi constatado que o usuario possui alerta de suas transacoes enviadas
                        //precisamos obter a data hoje atual servidor
                        new ObterHora(this).execute();

                    }
                    if(status >= 3 && status <= 5)
                        count_pag_pen++;
                }

            }

            if(usu.getTransacoes_recebidas() != null) {

                for(int i = 0; i < usu.getTransacoes_recebidas().size(); i ++){
                    int status = Integer.parseInt(usu.getTransacoes_recebidas().get(i).getStatus_transacao());

                    //if(status != 2 && status < 6) //trans recebida antiga
                    if(status < 2)
                        count_trans_rec++;
                    if(status == 4) {


                        //usuario global usado somente aqui
                        trans_global_ped_receb = usu.getTransacoes_recebidas().get(i);

                        //foi constatado que o usuario possui alerta de suas transacoes recebida
                        //precisamos obter a data hoje atual servidor
                        new ObterHora(this).execute();

                    }


                    if(status ==3 || status == 5)
                        count_pag_pen++;
                }

            }

            if(count_pag_pen >0){
                badge_notification_pag_pen.setVisibility(View.VISIBLE);
                badge_notification_pag_pen.setText(String.valueOf(count_pag_pen));
            }
            if(count_trans_env >0){
                badge_notification_ped_env.setVisibility(View.VISIBLE);
                badge_notification_ped_env.setText(String.valueOf(count_trans_env));
            }
            if(count_trans_rec >0){
                badge_notification_ped_rec.setVisibility(View.VISIBLE);
                badge_notification_ped_rec.setText(String.valueOf(count_trans_rec));
            }



        }else{

            Log.i("InicioFragment","Usuario null");

            BancoControllerUsuario crud = new BancoControllerUsuario(getActivity());
            Cursor cursor = crud.carregaDados();
            String cpf = cursor.getString(cursor.getColumnIndexOrThrow(CriandoBanco.CPF));
            crud.deletaRegistro(cpf);

            Intent it = new Intent(getActivity(),TelaLogin.class);
            startActivity(it);
            getActivity().finish();
        }

    }


    @Override
    public void retornoObterHora(String hoje){

        hoje_string = hoje;

        if(trans_global_ped_env != null)
            dialogTransEnviadas(trans_global_ped_env,hoje);
        else
            dialogTransRecebidas(trans_global_ped_receb,hoje);
    }



    public void obterDadosUsuarioFireBase(String cpf) {

        DatabaseReference trans_enviadas = usuarioReferencia.child(cpf);
        trans_enviadas.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Usuario usu = dataSnapshot.getValue(Usuario.class);


                Log.i("Teste firebase","Usuario = "+usu.getNome());

                atualizaBadges(usu);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i("Teste firebase","cancelado firebase procurar por usuario");
            }
        });

    }

    public void atualizaBadges(Usuario usu){
        int count_trans_env = 0;
        int count_trans_rec = 0;
        int count_pag_pen = 0;

        if(usu.getTransacoes_enviadas() != null) {

            for(int i = 0; i < usu.getTransacoes_enviadas().size(); i ++){
                int status = Integer.parseInt(usu.getTransacoes_enviadas().get(i).getStatus_transacao());
                if(status <=1 )
                    count_trans_env++;
                if(status >= 3 && status <= 5)
                    count_pag_pen++;
            }

        }

        if(usu.getTransacoes_recebidas() != null) {

            for(int i = 0; i < usu.getTransacoes_recebidas().size(); i ++){
                int status = Integer.parseInt(usu.getTransacoes_recebidas().get(i).getStatus_transacao());

                if(status != 2 && status < 6)
                    count_trans_rec++;
            }

        }

        if(count_pag_pen >0){
            badge_notification_pag_pen.setVisibility(View.VISIBLE);
            badge_notification_pag_pen.setText(String.valueOf(count_pag_pen));
        }
        if(count_trans_env >0){
            badge_notification_ped_env.setVisibility(View.VISIBLE);
            badge_notification_ped_env.setText(String.valueOf(count_trans_env));
        }
        if(count_trans_rec >0){
            badge_notification_ped_rec.setVisibility(View.VISIBLE);
            badge_notification_ped_rec.setText(String.valueOf(count_trans_rec));
        }

        progress_bar_inicio.setVisibility(View.INVISIBLE);
    }

    public void mensagem(String titulo,String corpo,String botao)
    {
        AlertDialog.Builder mensagem = new AlertDialog.Builder(getActivity());
        mensagem.setTitle(titulo);
        mensagem.setMessage(corpo);
        mensagem.setNeutralButton(botao,null);
        mensagem.show();
    }

    public void dialogTransRecebidas(final Transacao trans,final String hoje){
        dialog = new Dialog(getActivity(),R.style.AppThemeDialog);
        dialog.setContentView(R.layout.dialog_confirma_quitacao_pedido);
        dialog.setTitle("Confirmação necessária");

        progress_bar_dialog_enviados = (ProgressBar) dialog.findViewById(R.id.progress_bar);

        TextView tv_texto_dialog = (TextView) dialog.findViewById(R.id.tv_texto_dialog);
        tv_texto_dialog.setText("Seu amigo(a) "+ trans.getNome_usu1() +" solicitou o pagamento. Confirme aqui o recebimento do valor\n" +
                "referente à quitação do contrato.");

        Locale ptBr = new Locale("pt", "BR");
        NumberFormat nf = NumberFormat.getCurrencyInstance(ptBr);

        //calculamos a diferença de dias entre a data atual ate a data do pedido para calcularmos o juros
        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        DateTimeFormatter dtfOut = DateTimeFormat.forPattern("dd/MM/yyyy");
        DateTimeFormatter dtfOut_hora = DateTimeFormat.forPattern("HH:mm:ss");

        DateTime hora_pedido_parse = fmt.parseDateTime(trans.getDataPedido());
        DateTime vencimento_parse_utc = fmt.parseDateTime(trans.getVencimento());
        DateTime data_pedido_parse_utc = fmt.parseDateTime(trans.getDataPedido());
        DateTime hoje_parse_utc = fmt.parseDateTime(hoje);

        String vencimento_parse_string = dtfOut.print(vencimento_parse_utc);
        String data_pedido_parse_string = dtfOut.print(data_pedido_parse_utc);
        String hoje_parse_string = dtfOut.print(hoje_parse_utc);
        String hora_pedido = dtfOut_hora.print(hora_pedido_parse);

        DateTime vencimento_parse = dtfOut.parseDateTime(vencimento_parse_string);
        DateTime data_pedido_parse = dtfOut.parseDateTime(data_pedido_parse_string);
        DateTime hoje_parse = dtfOut.parseDateTime(hoje_parse_string);

        //calculamos os dias corridos para calcularmos o juros do redimento atual
        Days d_corridos = Days.daysBetween(data_pedido_parse, hoje_parse);
        int dias_corridos = d_corridos.getDays();

        Double multa_atraso = 0.0;
        Double juros_mora = 0.0;
        if(hoje_parse.isAfter(vencimento_parse)){

            Days d_atraso = Days.daysBetween(vencimento_parse, hoje_parse);
            int dias_atraso = d_atraso.getDays();

            juros_mora = Double.parseDouble(trans.getValor()) * (0.00099667 * dias_atraso);

            //Log.i("PagamentoPendente","dias de atraso = "+dias_atraso);

            multa_atraso = Double.parseDouble(trans.getValor())*0.1;

        }

        Double juros_mensal = Double.parseDouble(trans.getValor()) * (0.00066333 * dias_corridos);

        Double valor_total = Double.parseDouble(trans.getValor()) + juros_mensal + multa_atraso + juros_mora;

        String valor_formatado = nf.format (valor_total);

        TextView tv_valor_dialog = (TextView) dialog.findViewById(R.id.tv_valor_dialog);
        tv_valor_dialog.setText(valor_formatado);

        btn_recusa_recebimento_dialog = (Button) dialog.findViewById(R.id.btn_recusa_recebimento);
        btn_recusa_recebimento_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                trans.setStatus_transacao(String.valueOf(Transacao.RESP_QUITACAO_SOLICITADA_RECUSADA));

                List<Historico> list_hist;
                if(trans.getHistorico() == null){
                    list_hist = new ArrayList<Historico>();
                }else{
                    list_hist = trans.getHistorico();
                }

                Historico hist = new Historico();
                hist.setData(hoje);
                hist.setStatus_transacao(String.valueOf(Transacao.RESP_QUITACAO_SOLICITADA_RECUSADA));

                list_hist.add(hist);

                trans.setHistorico(list_hist);

                metodoEditaTrans(trans);


                desabilitaBotoes();

            }
        });

        btn_confirma_recebimento_dialog = (Button) dialog.findViewById(R.id.btn_confirma_recebimento);
        btn_confirma_recebimento_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                trans.setStatus_transacao(String.valueOf(Transacao.RESP_QUITACAO_SOLICITADA_CONFIRMADA));
                trans.setData_recusada("");
                trans.setData_pagamento(hoje);

                List<Historico> list_hist;
                if(trans.getHistorico() == null){
                    list_hist = new ArrayList<Historico>();
                }else{
                    list_hist = trans.getHistorico();
                }

                Historico hist = new Historico();
                hist.setData(hoje);
                hist.setStatus_transacao(String.valueOf(Transacao.RESP_QUITACAO_SOLICITADA_CONFIRMADA));

                list_hist.add(hist);

                trans.setHistorico(list_hist);

                metodoEditaTransResposta(trans);

                desabilitaBotoes();
            }
        });

        dialog.setCancelable(false);
        dialog.show();
    }

    public void dialogTransEnviadas(final Transacao trans,final String hoje){
        dialog = new Dialog(getActivity(),R.style.AppThemeDialog);
        dialog.setContentView(R.layout.dialog_confirma_recebimento_pedido);
        dialog.setTitle("Confirmação necessária");

        progress_bar_dialog_enviados = (ProgressBar) dialog.findViewById(R.id.progress_bar);

        TextView tv_texto_dialog = (TextView) dialog.findViewById(R.id.tv_texto_dialog);
        tv_texto_dialog.setText("Seu amigo(a) "+ trans.getNome_usu2() +" aceitou sua solicitação de empréstimo. Confirme o recebimentos do valor solicitado.");

        Locale ptBr = new Locale("pt", "BR");
        NumberFormat nf = NumberFormat.getCurrencyInstance(ptBr);
        String valor_formatado = nf.format (Double.parseDouble(trans.getValor()));

        TextView tv_valor_dialog = (TextView) dialog.findViewById(R.id.tv_valor_dialog);
        tv_valor_dialog.setText(valor_formatado);

        btn_recusa_recebimento_dialog = (Button) dialog.findViewById(R.id.btn_recusa_recebimento);
        btn_recusa_recebimento_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                desabilitaBotoes();

                trans.setStatus_transacao(String.valueOf(Transacao.AGUARDANDO_RESPOSTA));

                List<Historico> list_hist;
                if(trans.getHistorico() == null){
                    list_hist = new ArrayList<Historico>();
                }else{
                    list_hist = trans.getHistorico();
                }

                Historico hist = new Historico();
                hist.setData(hoje);
                hist.setStatus_transacao(String.valueOf(Transacao.AGUARDANDO_RESPOSTA));

                list_hist.add(hist);

                trans.setHistorico(list_hist);

                metodoEditaTrans(trans);

            }
        });

        btn_confirma_recebimento_dialog = (Button) dialog.findViewById(R.id.btn_confirma_recebimento);
        btn_confirma_recebimento_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                desabilitaBotoes();

                //realiza captura na cielo
                AlteraPagamento cp = new AlteraPagamento();
                cp.setClientAcount(KeyAccountPagamento.CLIENT_ACCOUNT);
                cp.setClientKey(KeyAccountPagamento.CLIENT_KEY);
                cp.setOptionId("8888");
                cp.setPaymentId(trans.getPagamento().getPayment_id_first());
                cp.setNewValue(trans.getPagamento().getAmount_first());

                //antes de finalmente editar a transacao, cancelamos o pedido na cielo
                new AlteraPagamentoService(InicioFragment.this,cp).execute();

            }
        });

        dialog.setCancelable(false);
        dialog.show();
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
            trans_global_ped_env.setStatus_transacao(String.valueOf(Transacao.CONFIRMADO_RECEBIMENTO));

            List<Historico> list_hist;
            if (trans_global_ped_env.getHistorico() == null) {
                list_hist = new ArrayList<Historico>();
            } else {
                list_hist = trans_global_ped_env.getHistorico();
            }

            Historico hist = new Historico();
            hist.setData(hoje_string);
            hist.setStatus_transacao(String.valueOf(Transacao.CONFIRMADO_RECEBIMENTO));

            list_hist.add(hist);

            trans_global_ped_env.setHistorico(list_hist);

            metodoEditaTrans(trans_global_ped_env);
        }else{
            mensagem("Houve um erro!","Olá, parece que tivemos algum problema no cancelamento do pagamento do pedido, por favor tente novamente. Se o erro" +
                    " persistir favor entrar em contato com InBanker","Ok");
            habilitaBotoes();
        }
    }

    public void desabilitaBotoes(){
        progress_bar_dialog_enviados.setVisibility(View.VISIBLE);
        btn_confirma_recebimento_dialog.setEnabled(false);
        btn_recusa_recebimento_dialog.setEnabled(false);
    }

    public void metodoEditaTransResposta(Transacao trans){
        //cpf usuario 1 que recebera a notificacao no retorno desse metodo
        trans_global = trans;

        Log.i("trans edita resposta","resposta = "+trans.getUsu1()+" - "+trans.getUsu2()+" - "+trans.getId_trans());

        new EditaTransacaoResposta(trans,trans.getUsu1(),trans.getUsu2(),this).execute();
    }

    public void metodoEditaTrans(Transacao trans){

        //cpf usuario 2 que recebera a notificacao no retorno desse metodo
        trans_global = trans;

        new EditaTransacao(trans,trans_global.getUsu1(),trans_global.getUsu2(),InicioFragment.this).execute();


    }

    public void retornoStringWebService(String result){


        BancoControllerUsuario crud = new BancoControllerUsuario(getActivity());
        Cursor cursor = crud.carregaDados();
        String cpf = cursor.getString(cursor.getColumnIndexOrThrow(CriandoBanco.CPF));

        if(result.equals("sucesso_edit")){

            //verificamos para qual usuario enviar a notificacao
            if(cpf.equals(trans_global.getUsu1())) {
                //busca token do usuario 2 para enviarmos notificacao
                new BuscaUsuarioCPFAux(trans_global.getUsu2(), getActivity(), this).execute();
            }else{
                //busca token do usuario 1 para enviarmos notificacao
                new BuscaUsuarioCPFAux(trans_global.getUsu1(), getActivity(), this).execute();
            }
        }else{

            habilitaBotoes();

            mensagem("Houve um erro!","Olá, parece que tivemos algum problema de conexão, por favor tente novamente.","Ok");
        }

    }

    public void habilitaBotoes(){
        progress_bar_dialog_enviados.setVisibility(View.GONE);
        btn_confirma_recebimento_dialog.setEnabled(true);
        btn_recusa_recebimento_dialog.setEnabled(true);
    }

    @Override
    public void retornoUsuarioWebServiceAux(Usuario usu){

        dialog.dismiss();


        if(!usu.getToken_gcm().equals("")) {
            //envia notificacao
            new EnviaNotificacao(trans_global, usu.getToken_gcm()).execute();
        }

        if(Integer.parseInt(trans_global.getStatus_transacao()) == Transacao.CONFIRMADO_RECEBIMENTO)
            mensagemIntent("InBanker","Parabéns, você confirmou o recebimento do valor solicitado. Ao efetuar o pagamento de quitação, peça que seu amigo(a) " + trans_global.getNome_usu2() + " confirme o recebimento do valor.", "Ok");
        else if(Integer.parseInt(trans_global.getStatus_transacao()) == Transacao.AGUARDANDO_RESPOSTA)
            mensagemIntent("InBanker", "Você recusou o recebimento do valor solicitado à "+ trans_global.getNome_usu2()+". Seu pedido de empréstimo foi enviado novamente.", "Ok");
        else if(Integer.parseInt(trans_global.getStatus_transacao()) == Transacao.RESP_QUITACAO_SOLICITADA_CONFIRMADA)
            mensagemIntent("InBanker", "Você confirmou o recebimento do valor para quitação do empréstimo solicitado por "+ trans_global.getNome_usu1()+". Parabéns, essa transacão foi finalizada com sucesso.", "Ok");
        else if(Integer.parseInt(trans_global.getStatus_transacao()) == Transacao.RESP_QUITACAO_SOLICITADA_RECUSADA)
            mensagemIntent("InBanker", "Você recusou uma solicitação de quitação da dívida. Entre em contato com "+trans_global.getNome_usu1()+" e aguarde por uma nova solicitação.","Ok");

    }

    public void mensagemIntent(String titulo,String corpo,String botao)
    {
        AlertDialog.Builder mensagem = new AlertDialog.Builder(getActivity());
        mensagem.setTitle(titulo);
        mensagem.setMessage(corpo);
        mensagem.setPositiveButton(botao,new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent it = new Intent(getActivity(),NavigationDrawerActivity.class);
                getActivity().startActivity(it);
                //para encerrar a activity atual e todos os parent
                getActivity().finishAffinity();
            }
        });
        mensagem.show();
    }
}
