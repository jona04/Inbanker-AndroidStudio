package br.com.appinbanker.inbanker.fragments_navigation;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.joanzapata.iconify.widget.IconButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;


import br.com.appinbanker.inbanker.R;
import br.com.appinbanker.inbanker.TelaNotificacoes;
import br.com.appinbanker.inbanker.adapters.TransacaoHistoricoAdapter;
import br.com.appinbanker.inbanker.entidades.Transacao;
import br.com.appinbanker.inbanker.entidades.Usuario;
import br.com.appinbanker.inbanker.sqlite.BancoControllerUsuario;
import br.com.appinbanker.inbanker.sqlite.CriandoBanco;
import br.com.appinbanker.inbanker.util.AllSharedPreferences;
import br.com.appinbanker.inbanker.util.AnalyticsApplication;
import br.com.appinbanker.inbanker.webservice.BuscaUsuarioHistoricoCPF;

public class HistoricoFragment extends Fragment{

    private int lastExpandedPosition = -1;

    private TransacaoHistoricoAdapter listAdapter;
    private ExpandableListView expListView;
    ArrayList<Transacao> listDataHeader;
    HashMap<String,Transacao> listDataChild;

    private BancoControllerUsuario crud;
    private Cursor cursor;
    private String cpf;

    private LinearLayout progress_lista_historico;

    private RelativeLayout msg_lista_historico;

    private Tracker mTracker;

    public HistoricoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_historico, container, false);

        //habilita manuseio do menu no action bar
        setHasOptionsMenu(true);

        getActivity().setTitle("Histórico");

        // Obtain the shared Tracker instance.
        AnalyticsApplication application = (AnalyticsApplication) getActivity().getApplication();
        mTracker = application.getDefaultTracker();

        mTracker.setScreenName("HistoricoFragment");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        progress_lista_historico = (LinearLayout) view.findViewById(R.id.progress_lista_historico);

        msg_lista_historico = (RelativeLayout) view.findViewById(R.id.msg_lista_historico);

        crud = new BancoControllerUsuario(getActivity());
        cursor = crud.carregaDados();
        try{
            cpf = cursor.getString(cursor.getColumnIndexOrThrow(CriandoBanco.CPF));
            if(!cpf.equals("")) {
                msg_lista_historico.setVisibility(View.GONE);
                progress_lista_historico.setVisibility(View.VISIBLE);
                //busca pedidos enviados
                new BuscaUsuarioHistoricoCPF(cpf, HistoricoFragment.this).execute();
            }else{
                progress_lista_historico.setVisibility(View.GONE);
                msg_lista_historico.setVisibility(View.VISIBLE);
            }
        }catch (Exception e){
            Log.i("Webservice","-"+e);
        }

        expListView = (ExpandableListView) view.findViewById(R.id.transacaoList);

        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                if (lastExpandedPosition != -1
                        && groupPosition != lastExpandedPosition) {
                    expListView.collapseGroup(lastExpandedPosition);
                }
                lastExpandedPosition = groupPosition;
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (AllSharedPreferences.getPreferencesBoolean(AllSharedPreferences.VERIFY_TUTORIAL_HISTORICO, getActivity()) == false) {
            new ShowcaseView.Builder(getActivity())
                    .setStyle(R.style.CustomShowcaseTheme)
                    .withMaterialShowcase()
                    .setContentTitle("Históricos de pedidos")
                    .setContentText("Aqui ficarão todos os pedidos que foram finalizados com sucesso. \n\nLembrete: Pedidos cancelados não serão exibidos nessa tela.")
                    .build();

            AllSharedPreferences.putPreferencesBooleanTrue(AllSharedPreferences.VERIFY_TUTORIAL_HISTORICO, getActivity());

        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Do something that differs the Activity's menu here
        super.onCreateOptionsMenu(menu, inflater);

        View menuNotificacao = menu.findItem(R.id.menu_notificacao).getActionView();
        IconButton iconButtonMessages = (IconButton) menuNotificacao.findViewById(R.id.iconButton);
        TextView itemMessagesBadgeTextView = (TextView) menuNotificacao.findViewById(R.id.badge_textView);
        iconButtonMessages.setVisibility(View.GONE);
        itemMessagesBadgeTextView.setVisibility(View.GONE);

        View menuChat = menu.findItem(R.id.menu_email).getActionView();
        IconButton iconButtonChat = (IconButton) menuChat.findViewById(R.id.iconButton);
        iconButtonChat.setVisibility(View.GONE);
    }

    public void retornoBuscaUsuario(Usuario usu){

        progress_lista_historico.setVisibility(View.GONE);
        if(usu != null){

            //iremos adicionar a uma nova lista apenas as trasacoes de status 2 (historico), para posteriormente adicionarmos no adapter
            ArrayList<Transacao> list = new ArrayList<>();

            if(usu.getTransacoes_enviadas() != null) {

                for(int i = 0; i < usu.getTransacoes_enviadas().size(); i++){
                    int status = Integer.parseInt(usu.getTransacoes_enviadas().get(i).getStatus_transacao());
                    if(status == 6){
                        list.add(usu.getTransacoes_enviadas().get(i));
                    }
                }

            }

            if(usu.getTransacoes_recebidas() != null) {
                for(int i = 0; i < usu.getTransacoes_recebidas().size(); i++){
                    //Log.i("webservice", "lista list = " + i+" - "+mList.get(i).getStatus_transacao());
                    int status = Integer.parseInt(usu.getTransacoes_recebidas().get(i).getStatus_transacao());
                    if(status == 6){
                        list.add(usu.getTransacoes_recebidas().get(i));
                    }
                }

            }

            if(list.size() > 0) {
                expListView.setVisibility(View.VISIBLE);

                setValue(list);

                Collections.sort(listDataHeader, new CustomComparator());
                listAdapter = new TransacaoHistoricoAdapter(getActivity(),listDataHeader, listDataChild);
                expListView.setAdapter(listAdapter);

                //listAdapter = new TransacaoHistoricoAdapter(getActivity(),listDataHeader, listDataChild);
                //expListView.setAdapter(listAdapter);
            }else{
                msg_lista_historico.setVisibility(View.VISIBLE);
            }
        }else{
            mensagem();
        }

    }

    public class CustomComparator implements Comparator<Transacao> {// may be it would be Model
        @Override
        public int compare(Transacao obj1, Transacao obj2) {
            return obj2.getDataPedido().compareTo(obj1.getDataPedido());// compare two objects
        }
    }

    public void mensagem()
    {
        AlertDialog.Builder mensagem = new AlertDialog.Builder(getActivity());
        mensagem.setTitle("Houve um erro!");
        mensagem.setMessage("Olá, parece que houve um problema de conexao. Favor tente novamente!");
        mensagem.setNeutralButton("OK",null);
        mensagem.show();
    }

    private void setValue(List<Transacao> forums) {

        //List generalList = new ArrayList();
        Transacao f = new Transacao();

        listDataHeader = new ArrayList<Transacao>();
        listDataChild = new HashMap<String,Transacao>();

        for (int i = 0; i < forums.size(); i++) {
            listDataHeader.add(forums.get(i));
            listDataChild.put(listDataHeader.get(i).getId_trans(), forums.get(i));
        }

    }

}
