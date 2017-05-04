package br.com.appinbanker.inbanker.fragments_navigation;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.SimpleShowcaseEventListener;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.joanzapata.iconify.widget.IconButton;

import org.joda.time.DateTime;
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
import br.com.appinbanker.inbanker.TelaNotificacoes;
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
import br.com.appinbanker.inbanker.util.AllSharedPreferences;
import br.com.appinbanker.inbanker.util.AnalyticsApplication;
import br.com.appinbanker.inbanker.util.FunctionUtil;
import br.com.appinbanker.inbanker.webservice.AlteraPagamentoService;
import br.com.appinbanker.inbanker.webservice.BuscaUsuarioCPF;
import br.com.appinbanker.inbanker.webservice.BuscaUsuarioCPFAux;
import br.com.appinbanker.inbanker.webservice.EditaTransacao;
import br.com.appinbanker.inbanker.webservice.EditaTransacaoResposta;
import br.com.appinbanker.inbanker.webservice.EnviaEmailTrans;
import br.com.appinbanker.inbanker.webservice.EnviaNotificacao;
import br.com.appinbanker.inbanker.webservice.ObterHora;
import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;

public class InicioFragment extends Fragment implements WebServiceReturnStringHora,WebServiceReturnUsuario,WebServiceReturnString,WebServiceReturnStringPagamento,MaterialTabListener {

    TextView badge_notification_ped_rec,badge_notification_pag_pen,badge_notification_ped_env;

    Button btn_pedir_emprestimo, btn_pedidos_recebidos, btn_pedidos_enviados, btn_pagamentos_pendentes;

    ProgressBar progress_bar_inicio;

    ProgressBar progress_bar_dialog_enviados;
    Button btn_recusa_recebimento_dialog;
    Button btn_confirma_recebimento_dialog;
    Transacao trans_global;

    private Transacao trans_global_ped_receb,trans_global_ped_env;

    List<Transacao> list_trans_contrato_receber,list_trans_contrato_pagar;
    double total_receber = 0,total_pagar = 0;

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference usuarioReferencia = databaseReference.child("usuarios");

    Dialog dialog;

    String hoje_string;

    Double juros_mora;
    Double multa_atraso;
    Double juros_mensal;


    LinearLayout ll_btn_inicio;

    MaterialTabHost tabHost;
    ViewPager pager;
    ViewPagerAdapter pagerAdapter;

    private Resources res;

    //private FirebaseAnalytics mFirebaseAnalytics;

    public InicioFragment(){}

    //utilizada para verificar se a tela existe
    boolean hasStop = false;

    private Tracker mTracker;

    private String nome_usu_logado_analytics;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_inicio, container, false);

        setHasOptionsMenu(true);
        getActivity().setTitle("Inicio");

        // Obtain the shared Tracker instance.
        AnalyticsApplication application = (AnalyticsApplication) getActivity().getApplication();
        mTracker = application.getDefaultTracker();

        mTracker.setScreenName("InicioFragment");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        // Obtain the FirebaseAnalytics instance.
        //mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());

        res = this.getResources();

        list_trans_contrato_receber = new ArrayList<>();
        list_trans_contrato_pagar = new ArrayList<>();;

        tabHost = (MaterialTabHost) view.findViewById(R.id.tabHost);
        pager = (ViewPager) view.findViewById(R.id.pager);
        // init view pager
        pagerAdapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());

        //chamamos tabhost na criacao da tela para gerar estrutura copleta com tabs e titulos
        geraTabHost();

       ll_btn_inicio = (LinearLayout) view.findViewById(R.id.ll_btn_inicio);
        progress_bar_inicio = (ProgressBar) view.findViewById(R.id.progress_bar_inicio);
        badge_notification_ped_rec = (TextView) view.findViewById(R.id.badge_notification_ped_rec);
        badge_notification_pag_pen = (TextView) view.findViewById(R.id.badge_notification_pag_pen);
        badge_notification_ped_env = (TextView) view.findViewById(R.id.badge_notification_ped_env);

        btn_pedir_emprestimo = (Button) view.findViewById(R.id.btn_pedir_emprestimo);
        btn_pedidos_enviados = (Button) view.findViewById(R.id.btn_pedidos_enviados);
        btn_pedidos_recebidos = (Button) view.findViewById(R.id.btn_pedidos_recebidos);
        btn_pagamentos_pendentes = (Button) view.findViewById(R.id.btn_pagamentos_pedentes);

        ImageView img_logo_inicio = (ImageView) view.findViewById(R.id.img_logo_inicio);
        img_logo_inicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mensagemIntent("Tutorial","Olá, você deseja visualizar os tutoriais novamente?","Sim","Não");
            }
        });

        //fazemos uma busca do usuario logando no banco para mostrarmos corretamente as notificações interna nos butons da tela incio
        BancoControllerUsuario crud = new BancoControllerUsuario(getActivity());
        Cursor cursor = crud.carregaDados();

        try {
            String cpf = cursor.getString(cursor.getColumnIndexOrThrow(CriandoBanco.CPF));
            nome_usu_logado_analytics = cursor.getString(cursor.getColumnIndexOrThrow(CriandoBanco.NOME));
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

                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Btn_Fragment_Inicio")
                        .setAction("Pedir_Emprestimo")
                        .setLabel(nome_usu_logado_analytics)
                        .build());

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

                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Btn_Fragment_Inicio")
                        .setAction("Pedidos_Enviados")
                        .setLabel(nome_usu_logado_analytics)
                        .build());

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

                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Btn_Fragment_Inicio")
                        .setAction("Pedidos_Recebidos")
                        .setLabel(nome_usu_logado_analytics)
                        .build());

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

                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Btn_Fragment_Inicio")
                        .setAction("Contratos")
                        .setLabel(nome_usu_logado_analytics)
                        .build());

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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);



        if(AllSharedPreferences.getPreferencesBoolean(AllSharedPreferences.VERIFY_TUTORIAL_INICIO,getActivity())==false) {
            new ShowcaseView.Builder(getActivity())
                    .setStyle(R.style.CustomShowcaseTheme)
                    .withMaterialShowcase()
                    .setTarget(new ViewTarget(btn_pedir_emprestimo))
                    .setContentTitle("Fazendo pedido")
                    .setContentText("Toque no botão 'Pedir Empréstimo' para exibir a lista de amigos disponíveis para você pedir um empréstimo.")
                    .setShowcaseEventListener(new SimpleShowcaseEventListener() {

                        @Override
                        public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
                            new ShowcaseView.Builder(getActivity())
                                    .setStyle(R.style.CustomShowcaseTheme)
                                    .withMaterialShowcase()
                                    .setTarget(new ViewTarget(btn_pedidos_enviados))
                                    .setContentTitle("Visualizar pedidos enviados")
                                    .setContentText("Toque no botão 'Pedidos Enviados' para exibir a lista de pedidos que foram enviados por você, mas que ainda não foram aceitos por seus amigos.")
                                    .setShowcaseEventListener(new SimpleShowcaseEventListener() {

                                        @Override
                                        public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
                                            new ShowcaseView.Builder(getActivity())
                                                    .setStyle(R.style.CustomShowcaseTheme)
                                                    .withMaterialShowcase()
                                                    .setTarget(new ViewTarget(btn_pagamentos_pendentes))
                                                    .setContentTitle("Visualizar contratos")
                                                    .setContentText("Toque no botão 'Contratos' para exibir a lista de pedidos que já foram aceitos por você ou por seus amigos, e portanto já existe um contrato formalizado.")
                                                    .setShowcaseEventListener(new SimpleShowcaseEventListener() {

                                                        @Override
                                                        public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
                                                            new ShowcaseView.Builder(getActivity())
                                                                    .setStyle(R.style.CustomShowcaseTheme)
                                                                    .withMaterialShowcase()
                                                                    .setTarget(new ViewTarget(btn_pedidos_recebidos))
                                                                    .setContentTitle("Visualizar pedidos recebidos")
                                                                    .setContentText("Toque no botão 'Pedidos Recebidos' para exibir a lista de pedidos que você recebeu de seus amigos, mas que ainda não foram aceitos por você.")
                                                                    .build();
                                                        }

                                                    })
                                                    .build();
                                        }

                                    })
                                    .build();
                        }

                    })
                    .build();


            AllSharedPreferences.putPreferencesBooleanTrue(AllSharedPreferences.VERIFY_TUTORIAL_INICIO,getActivity());

        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Do something that differs the Activity's menu here
        super.onCreateOptionsMenu(menu, inflater);

        View menuNotificacao = menu.findItem(R.id.menu_notificacao).getActionView();
        TextView itemMessagesBadgeTextView = (TextView) menuNotificacao.findViewById(R.id.badge_textView);

        int count = 0;
        if(AllSharedPreferences.getPreferences(AllSharedPreferences.COUNT_NOTIFY_CARTA,getActivity()) != null) {
            if (!AllSharedPreferences.getPreferences(AllSharedPreferences.COUNT_NOTIFY_CARTA, getActivity()).equals("")) {
                count = Integer.parseInt(AllSharedPreferences.getPreferences(AllSharedPreferences.COUNT_NOTIFY_CARTA, getActivity()));
            }
        }

        if(count == 0){
            itemMessagesBadgeTextView.setVisibility(View.GONE); // initially hidden
        }else{
            itemMessagesBadgeTextView.setVisibility(View.VISIBLE); // initially hidden
            itemMessagesBadgeTextView.setText(String.valueOf(count));
        }

        Log.i("Script","numero count ="+count);

        itemMessagesBadgeTextView.setText(String.valueOf(count));

        IconButton iconButtonNotify = (IconButton) menuNotificacao.findViewById(R.id.iconButton);
        //iconButtonMessages.setText("30");

        iconButtonNotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //esconde badge
                //Log.i("Script","some bagde menu cartinha");

                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Toolbar")
                        .setAction("TelaNotificacoes")
                        .setLabel(nome_usu_logado_analytics)
                        .build());


                Intent it = new Intent(getActivity(),TelaNotificacoes.class);
                startActivity(it);
            }
        });

    }

    @Override
    public void retornoUsuarioWebService(Usuario usu) {

        progress_bar_inicio.setVisibility(View.INVISIBLE);

        if(usu != null){

            //count notificacoes cartinha
            if(usu.getNotificacaoContrato()!= null){

                int result_count = 0;
                int count_notify = 0;

                count_notify = usu.getNotificacaoContrato().size();
                //Log.i("Script","count_notify 0 = "+count_notify);

                if(!AllSharedPreferences.getPreferences(AllSharedPreferences.COUNT_NOTIFY_CARTA,getActivity()).equals("")) {
                    if(!AllSharedPreferences.getPreferences(AllSharedPreferences.VERIFY_NOTIFY_CARTA,getActivity()).equals("")) {
                        Log.i("Script", "count_notify neutro");
                        //int count_aux = Integer.parseInt(AllSharedPreferences.getPreferences(AllSharedPreferences.COUNT_NOTIFY_CARTA_AUX, getActivity()));
                        int count = Integer.parseInt(AllSharedPreferences.getPreferences(AllSharedPreferences.COUNT_NOTIFY_CARTA_AUX, getActivity()));
                        Log.i("Script","count = "+count);
                        //AllSharedPreferences.putPreferences(AllSharedPreferences.COUNT_NOTIFY_CARTA_AUX, String.valueOf(count_notify), getActivity());

                        result_count = count_notify - count;
                        if(result_count > 0) {
                            Log.i("Script", "count_notify 1 = " + result_count);
                            AllSharedPreferences.putPreferences(AllSharedPreferences.COUNT_NOTIFY_CARTA, String.valueOf(result_count), getActivity());
                            AllSharedPreferences.putPreferences(AllSharedPreferences.COUNT_NOTIFY_CARTA_AUX, String.valueOf(count_notify), getActivity());
                        }
                    }else{
                        Log.i("Script", "VERIFY_NOTIFY_CARTA vazio = ");
                        AllSharedPreferences.putPreferences(AllSharedPreferences.COUNT_NOTIFY_CARTA, String.valueOf(count_notify), getActivity());
                        AllSharedPreferences.putPreferences(AllSharedPreferences.COUNT_NOTIFY_CARTA_AUX, String.valueOf(count_notify), getActivity());
                    }
                }else {
                    Log.i("Script","count_notify 2 = "+count_notify);
                    AllSharedPreferences.putPreferences(AllSharedPreferences.COUNT_NOTIFY_CARTA, String.valueOf(count_notify), getActivity());
                    AllSharedPreferences.putPreferences(AllSharedPreferences.COUNT_NOTIFY_CARTA_AUX, String.valueOf(count_notify), getActivity());
                }
            }

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
                    if(status >= 3 && status <= 5) {
                        count_pag_pen++;
                        list_trans_contrato_pagar.add(usu.getTransacoes_enviadas().get(i));
                    }
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

                    if(status ==3 || status == 5) {
                        count_pag_pen++;
                        list_trans_contrato_receber.add(usu.getTransacoes_recebidas().get(i));
                    }
                }
            }

            if(count_pag_pen >0){
                badge_notification_pag_pen.setVisibility(View.VISIBLE);
                badge_notification_pag_pen.setText(String.valueOf(count_pag_pen));

                new ObterHora(this).execute();
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
        else if(trans_global_ped_receb != null)
            dialogTransRecebidas(trans_global_ped_receb,hoje);

        if(list_trans_contrato_receber != null) {
            for (int i = 0; i < list_trans_contrato_receber.size(); i++) {
                //calculamos a diferença de dias entre a data atual ate a data do pedido para calcularmos o juros
                DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
                DateTimeFormatter dtfOut = DateTimeFormat.forPattern("dd/MM/yyyy");

                DateTime data_pedido_parse_utc = fmt.parseDateTime(list_trans_contrato_receber.get(i).getDataPedido());
                DateTime hoje_parse_utc = fmt.parseDateTime(hoje_string);
                DateTime vencimento_parse_utc = fmt.parseDateTime(list_trans_contrato_receber.get(i).getVencimento());

                String data_pedido_parse_string = dtfOut.print(data_pedido_parse_utc);
                String hoje_parse_string = dtfOut.print(hoje_parse_utc);
                String vencimento_parse_string = dtfOut.print(vencimento_parse_utc);

                DateTime data_pedido_parse = dtfOut.parseDateTime(data_pedido_parse_string);
                DateTime hoje_parse = dtfOut.parseDateTime(hoje_parse_string);
                DateTime vencimento_parse = dtfOut.parseDateTime(vencimento_parse_string);

                //calculamos o total de dias para mostramos na tela inicial antes do usuario-2 aceitar ou recusar o pedido recebido
                Days d = Days.daysBetween(data_pedido_parse, hoje_parse);
                int dias = d.getDays();

                Double multa_atraso = 0.0;
                Double juros_mora = 0.0;
                if (hoje_parse.isAfter(vencimento_parse)) {

                    //alteramos o valor de dias para até a data limite do vencimento
                    //pois a partir daqui será adicionado um novo valor de juros, juros mora referente aos dias que ultrapssou o vencimento
                    d = Days.daysBetween(data_pedido_parse, vencimento_parse);
                    dias = d.getDays();

                    Days d_atraso = Days.daysBetween(vencimento_parse, hoje_parse);
                    int dias_atraso = d_atraso.getDays();

                    juros_mora = Double.parseDouble(list_trans_contrato_receber.get(i).getValor()) * (0.00099667 * dias_atraso);

                    multa_atraso = Double.parseDouble(list_trans_contrato_receber.get(i).getValor()) * 0.1;
                }

                double juros_mensal = Double.parseDouble(list_trans_contrato_receber.get(i).getValor()) * (0.00066333 * dias);
                double valor_total = juros_mora + multa_atraso + juros_mensal + Double.parseDouble(list_trans_contrato_receber.get(i).getValor());

                total_receber += valor_total;
                Log.i("Script","valor total aba pagar na aba frag 4 = "+total_receber);
            }

            //geramos tabhost apos receber o valor total a receber
            geraTabHost();
        }
        if(list_trans_contrato_pagar != null) {
            for (int i = 0; i < list_trans_contrato_pagar.size(); i++) {
                //calculamos a diferença de dias entre a data atual ate a data do pedido para calcularmos o juros
                DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
                DateTimeFormatter dtfOut = DateTimeFormat.forPattern("dd/MM/yyyy");

                DateTime data_pedido_parse_utc = fmt.parseDateTime(list_trans_contrato_pagar.get(i).getDataPedido());
                DateTime hoje_parse_utc = fmt.parseDateTime(hoje_string);
                DateTime vencimento_parse_utc = fmt.parseDateTime(list_trans_contrato_pagar.get(i).getVencimento());

                String data_pedido_parse_string = dtfOut.print(data_pedido_parse_utc);
                String hoje_parse_string = dtfOut.print(hoje_parse_utc);
                String vencimento_parse_string = dtfOut.print(vencimento_parse_utc);

                DateTime data_pedido_parse = dtfOut.parseDateTime(data_pedido_parse_string);
                DateTime hoje_parse = dtfOut.parseDateTime(hoje_parse_string);
                DateTime vencimento_parse = dtfOut.parseDateTime(vencimento_parse_string);

                //calculamos o total de dias para mostramos na tela inicial antes do usuario-2 aceitar ou recusar o pedido recebido
                Days d = Days.daysBetween(data_pedido_parse, hoje_parse);
                int dias = d.getDays();

                Double multa_atraso = 0.0;
                Double juros_mora = 0.0;
                if (hoje_parse.isAfter(vencimento_parse)) {

                    //alteramos o valor de dias para até a data limite do vencimento
                    //pois a partir daqui será adicionado um novo valor de juros, juros mora referente aos dias que ultrapssou o vencimento
                    d = Days.daysBetween(data_pedido_parse, vencimento_parse);
                    dias = d.getDays();

                    Days d_atraso = Days.daysBetween(vencimento_parse, hoje_parse);
                    int dias_atraso = d_atraso.getDays();

                    juros_mora = Double.parseDouble(list_trans_contrato_pagar.get(i).getValor()) * (0.00099667 * dias_atraso);

                    multa_atraso = Double.parseDouble(list_trans_contrato_pagar.get(i).getValor()) * 0.1;
                }

                double juros_mensal = Double.parseDouble(list_trans_contrato_pagar.get(i).getValor()) * (0.00066333 * dias);
                double valor_total = juros_mora + multa_atraso + juros_mensal + Double.parseDouble(list_trans_contrato_pagar.get(i).getValor());

                total_pagar += valor_total;
                Log.i("Script","valor total aba pagar 3 = "+total_pagar);
            }

            //geramos tabhost apos receber o valor total a pagar
            geraTabHost();
        }

    }



    /*public void obterDadosUsuarioFireBase(String cpf) {

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

    }*/

    /*public void atualizaBadges(Usuario usu){
        int count_trans_env = 0;
        int count_trans_rec = 0;
        int count_pag_pen = 0;

        if(usu.getTransacoes_enviadas() != null) {

            for(int i = 0; i < usu.getTransacoes_enviadas().size(); i ++){
                int status = Integer.parseInt(usu.getTransacoes_enviadas().get(i).getStatus_transacao());
                if(status <=1 ) {
                    count_trans_env++;
                }
                if(status >= 3 && status <= 5) {
                    count_pag_pen++;
                    trans_global_contrato.add(usu.getTransacoes_enviadas().get(i));
                }
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
    }*/

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

        final EditText et_dialog_senha = (EditText) dialog.findViewById(R.id.et_dialog_senha);
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

        multa_atraso = 0.0;
        juros_mora = 0.0;
        if(hoje_parse.isAfter(vencimento_parse)){

            //alteramos o valor de dias para até a data limite do vencimento
            //pois a partir daqui será adicionado um novo valor de juros, juros mora referente aos dias que ultrapssou o vencimento
            d_corridos = Days.daysBetween(data_pedido_parse, vencimento_parse);
            dias_corridos = d_corridos.getDays();

            Days d_atraso = Days.daysBetween(vencimento_parse, hoje_parse);
            int dias_atraso = d_atraso.getDays();

            juros_mora = Double.parseDouble(trans.getValor()) * (0.00099667 * dias_atraso);

            Log.i("PagamentoPendente","dias de atraso = "+dias_atraso);

            multa_atraso = Double.parseDouble(trans.getValor())*0.1;

        }

        juros_mensal = Double.parseDouble(trans.getValor()) * (0.00066333 * dias_corridos);

        double valor_total = juros_mora+multa_atraso + juros_mensal +  Double.parseDouble(trans.getValor());

        String valor_formatado = nf.format (valor_total);

        TextView tv_valor_dialog = (TextView) dialog.findViewById(R.id.tv_valor_dialog);
        tv_valor_dialog.setText(valor_formatado);

        et_dialog_senha.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if (keyCode == EditorInfo.IME_ACTION_SEARCH ||
                        keyCode == EditorInfo.IME_ACTION_DONE ||
                        event.getAction() == KeyEvent.ACTION_DOWN &&
                                event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

                    esconderTeclado();

                    return true;
                }
                return false;
            }
        });

        btn_recusa_recebimento_dialog = (Button) dialog.findViewById(R.id.btn_recusa_recebimento);
        btn_recusa_recebimento_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                BancoControllerUsuario crud = new BancoControllerUsuario(getActivity());
                Cursor cursor = crud.carregaDados();

                String senha = cursor.getString(cursor.getColumnIndexOrThrow(CriandoBanco.SENHA));

                if (senha.equals(FunctionUtil.md5(et_dialog_senha.getText().toString()))) {

                    trans.setStatus_transacao(String.valueOf(Transacao.RESP_QUITACAO_SOLICITADA_RECUSADA));
                    trans.setId_recibo("");

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

                } else {

                    et_dialog_senha.setError("Senha incorreta");
                    et_dialog_senha.setFocusable(true);
                    et_dialog_senha.requestFocus();
                }

            }
        });

        btn_confirma_recebimento_dialog = (Button) dialog.findViewById(R.id.btn_confirma_recebimento);
        btn_confirma_recebimento_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                BancoControllerUsuario crud = new BancoControllerUsuario(getActivity());
                Cursor cursor = crud.carregaDados();

                String senha = cursor.getString(cursor.getColumnIndexOrThrow(CriandoBanco.SENHA));

                if (senha.equals(FunctionUtil.md5(et_dialog_senha.getText().toString()))) {

                    trans.setStatus_transacao(String.valueOf(Transacao.RESP_QUITACAO_SOLICITADA_CONFIRMADA));
                    trans.setData_recusada("");
                    trans.setData_pagamento(hoje);

                    trans.setValor_juros_mora(String.valueOf(juros_mora));
                    trans.setValor_multa(String.valueOf(multa_atraso));
                    trans.setValor_juros_mensal(String.valueOf(juros_mensal));

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

                } else {

                    et_dialog_senha.setError("Senha incorreta");
                    et_dialog_senha.setFocusable(true);
                    et_dialog_senha.requestFocus();
                }

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

        final EditText et_dialog_senha = (EditText) dialog.findViewById(R.id.et_dialog_senha);
        TextView tv_texto_dialog = (TextView) dialog.findViewById(R.id.tv_texto_dialog);
        tv_texto_dialog.setText("Seu amigo(a) "+ trans.getNome_usu2() +" aceitou sua solicitação de empréstimo. Confirme o recebimentos do valor solicitado.");

        Locale ptBr = new Locale("pt", "BR");
        NumberFormat nf = NumberFormat.getCurrencyInstance(ptBr);
        String valor_formatado = nf.format (Double.parseDouble(trans.getValor()));

        TextView tv_valor_dialog = (TextView) dialog.findViewById(R.id.tv_valor_dialog);
        tv_valor_dialog.setText(valor_formatado);

        et_dialog_senha.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if (keyCode == EditorInfo.IME_ACTION_SEARCH ||
                        keyCode == EditorInfo.IME_ACTION_DONE ||
                        event.getAction() == KeyEvent.ACTION_DOWN &&
                                event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

                    esconderTeclado();

                    return true;
                }
                return false;
            }
        });

        btn_recusa_recebimento_dialog = (Button) dialog.findViewById(R.id.btn_recusa_recebimento);
        btn_recusa_recebimento_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                BancoControllerUsuario crud = new BancoControllerUsuario(getActivity());
                Cursor cursor = crud.carregaDados();

                String senha = cursor.getString(cursor.getColumnIndexOrThrow(CriandoBanco.SENHA));

                if (senha.equals(FunctionUtil.md5(et_dialog_senha.getText().toString()))) {

                    desabilitaBotoes();

                    trans.setStatus_transacao(String.valueOf(Transacao.AGUARDANDO_RESPOSTA));
                    trans.setId_contrato("");
                    trans.setId_recibo("");

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

                } else {

                    et_dialog_senha.setError("Senha incorreta");
                    et_dialog_senha.setFocusable(true);
                    et_dialog_senha.requestFocus();
                }


            }
        });

        btn_confirma_recebimento_dialog = (Button) dialog.findViewById(R.id.btn_confirma_recebimento);
        btn_confirma_recebimento_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                BancoControllerUsuario crud = new BancoControllerUsuario(getActivity());
                Cursor cursor = crud.carregaDados();

                String senha = cursor.getString(cursor.getColumnIndexOrThrow(CriandoBanco.SENHA));

                if (senha.equals(FunctionUtil.md5(et_dialog_senha.getText().toString()))) {

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

                } else {

                    et_dialog_senha.setError("Senha incorreta");
                    et_dialog_senha.setFocusable(true);
                    et_dialog_senha.requestFocus();
                }

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

        //se confirmacao sucesso
        if(success) {

            //cria recibo e contrato

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

            trans_global_ped_env.setId_contrato("");
            trans_global_ped_env.setId_recibo("");

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

        if(result != null) {
            if (!result.equals("error_edit_trans")) {

                //verifica se result é contrato ou recibo
                if(trans_global.getId_contrato().equals("")){
                    trans_global.setId_contrato(result);
                }else{
                    trans_global.setId_recibo(result);
                }

                //verificamos para qual usuario enviar a notificacao
                if (cpf.equals(trans_global.getUsu1())) {
                    //busca token do usuario 2 para enviarmos notificacao
                    new BuscaUsuarioCPFAux(trans_global.getUsu2(), getActivity(), this).execute();
                } else {
                    //busca token do usuario 1 para enviarmos notificacao
                    new BuscaUsuarioCPFAux(trans_global.getUsu1(), getActivity(), this).execute();
                }
            } else {

                habilitaBotoes();

                mensagem("Houve um erro!", "Olá, parece que tivemos algum problema de conexão, por favor tente novamente.", "Ok");
            }
        }else{
            mensagem("Erro crítico!", "Olá, parece que tivemos algum problema de conexão, por favor tente novamente.", "Ok");
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

        //fazemos uma busca do usuario logando no banco para mostrarmos corretamente as notificações interna nos butons da tela incio
        BancoControllerUsuario crud = new BancoControllerUsuario(getActivity());
        Cursor cursor = crud.carregaDados();
        String email_user = cursor.getString(cursor.getColumnIndexOrThrow(CriandoBanco.EMAIL));
        String cpf = cursor.getString(cursor.getColumnIndexOrThrow(CriandoBanco.CPF));

        if(!usu.getToken_gcm().equals("")) {
            //envia notificacao
            new EnviaNotificacao(trans_global, usu.getToken_gcm()).execute();
        }

        if(Integer.parseInt(trans_global.getStatus_transacao()) == Transacao.CONFIRMADO_RECEBIMENTO) {

            //verificamos para qual usuario enviar o email
            if(cpf.equals(trans_global.getUsu1())) { //usuario esta enviando pedido
                //enviaemail para usuario
                new EnviaEmailTrans(trans_global, email_user ,usu.getEmail()).execute();
            }else{ //usuario esta recebendo pedido
                //enviaemail para usuario
                new EnviaEmailTrans(trans_global, usu.getEmail(),email_user).execute();
            }

            mensagemIntent("InBanker", "Parabéns, você confirmou o recebimento do valor solicitado. Ao efetuar o pagamento de quitação, peça que seu amigo(a) " + trans_global.getNome_usu2() + " confirme o recebimento do valor.", "Ok");
        }else if(Integer.parseInt(trans_global.getStatus_transacao()) == Transacao.AGUARDANDO_RESPOSTA) {
            mensagemIntent("InBanker", "Você recusou o recebimento do valor solicitado à " + trans_global.getNome_usu2() + ". Seu pedido de empréstimo foi enviado novamente.", "Ok");
        }else if(Integer.parseInt(trans_global.getStatus_transacao()) == Transacao.RESP_QUITACAO_SOLICITADA_CONFIRMADA) {

            //verificamos para qual usuario enviar o email
            if(cpf.equals(trans_global.getUsu1())) { //usuario esta enviando pedido
                //enviaemail para usuario
                new EnviaEmailTrans(trans_global, email_user ,usu.getEmail()).execute();
            }else{ //usuario esta recebendo pedido
                //enviaemail para usuario
                new EnviaEmailTrans(trans_global, usu.getEmail(),email_user).execute();
            }

            mensagemIntent("InBanker", "Você confirmou o recebimento do valor para quitação do empréstimo solicitado por " + trans_global.getNome_usu1() + ". Parabéns, essa transacão foi finalizada com sucesso.", "Ok");
        }else if(Integer.parseInt(trans_global.getStatus_transacao()) == Transacao.RESP_QUITACAO_SOLICITADA_RECUSADA) {
            mensagemIntent("InBanker", "Você recusou uma solicitação de quitação da dívida. Entre em contato com " + trans_global.getNome_usu1() + " e aguarde por uma nova solicitação.", "Ok");
        }
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

    public void esconderTeclado() {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        //inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    public void mensagemIntent(String titulo,String corpo,String botao_positivo,String botao_neutro)
    {
        AlertDialog.Builder mensagem = new AlertDialog.Builder(getActivity());
        mensagem.setTitle(titulo);
        mensagem.setMessage(corpo);
        mensagem.setNeutralButton(botao_neutro,null);
        mensagem.setPositiveButton(botao_positivo,new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                AllSharedPreferences.putPreferencesBooleanFalse(AllSharedPreferences.VERIFY_TUTORIAL_INICIO,getActivity());
                AllSharedPreferences.putPreferencesBooleanFalse(AllSharedPreferences.VERIFY_TUTORIAL_MENSAGEM,getActivity());
                AllSharedPreferences.putPreferencesBooleanFalse(AllSharedPreferences.VERIFY_TUTORIAL_NOTIFICACOES,getActivity());
                AllSharedPreferences.putPreferencesBooleanFalse(AllSharedPreferences.VERIFY_TUTORIAL_PEDIR_LOGAR_FACE,getActivity());
                AllSharedPreferences.putPreferencesBooleanFalse(AllSharedPreferences.VERIFY_TUTORIAL_PEDIR_LISTA_AMIGOS,getActivity());
                AllSharedPreferences.putPreferencesBooleanFalse(AllSharedPreferences.VERIFY_TUTORIAL_PAGAMENTO,getActivity());
                AllSharedPreferences.putPreferencesBooleanFalse(AllSharedPreferences.VERIFY_TUTORIAL_HISTORICO,getActivity());

                Intent it = new Intent(getActivity(),NavigationDrawerActivity.class);
                startActivity(it);
                //para encerrar a activity atual e todos os parent
                getActivity().finish();
            }
        });
        mensagem.show();
    }

    public void geraTabHost(){

        if(!hasStop) {
            pager.setAdapter(pagerAdapter);
            pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                @Override
                public void onPageSelected(int position) {
                    // when user do a swipe the selected tab change
                    tabHost.setSelectedNavigationItem(position);

                }
            });

            // insert all tabs from pagerAdapter data
            for (int i = 0; i < pagerAdapter.getCount(); i++) {
                tabHost.addTab(
                        tabHost.newTab()
                                .setText(pagerAdapter.getPageTitle(i))
                                .setTabListener(this)
                );

            }
        }

    }


    @Override
    public void onTabSelected(MaterialTab tab) {
        //Log.i("Script","tab.getPosition() = "+tab.getPosition());
        pager.setCurrentItem(tab.getPosition());

        if(tab.getPosition() == 0){
            /*Bundle params = new Bundle();
            params.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "A Receber");
            mFirebaseAnalytics.logEvent("tab_inicio_valores", params);*/

            mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory("Tab_inicio_valores")
                    .setAction("A Receber")
                    .setLabel(nome_usu_logado_analytics)
                    .build());

        }
        if(tab.getPosition() == 1){
            /*Bundle params = new Bundle();
            params.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "A Pagar");
            mFirebaseAnalytics.logEvent("tab_inicio_valores", params);*/

            mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory("Tab_inicio_valores")
                    .setAction("A Pagar")
                    .setLabel(nome_usu_logado_analytics)
                    .build());

        }



    }

    @Override
    public void onTabReselected(MaterialTab tab) {

    }

    @Override
    public void onTabUnselected(MaterialTab tab) {

    }

    private class ViewPagerAdapter extends FragmentStatePagerAdapter {

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public Fragment getItem(int num) {

            Fragment frag = new FragmentAbaReceber();
            Bundle bundle=new Bundle();
            switch (num){
                case 0:
                    frag = new FragmentAbaReceber();
                    bundle.putDouble("total", total_receber);
                    frag.setArguments(bundle);
                    break;
                case 1:
                    frag = new FragmentAbaPagar();
                    bundle.putDouble("total", total_pagar);
                    frag.setArguments(bundle);
                    break;
            }
            return frag;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            //return "Sezione " + position;

            switch(position) {
                case 0: return "A Receber";
                case 1: return "A Pagar";
                default: return "";
            }
        }
    }

    private Drawable getIcon(int position) {
        switch(position) {
            case 0:
                return res.getDrawable(R.drawable.ic_menu_send);
            case 1:
                return res.getDrawable(R.drawable.ic_menu_share);
        }
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hasStop=true;
    }
}
