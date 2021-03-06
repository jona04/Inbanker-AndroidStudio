package br.com.appinbanker.inbanker.fragments_navigation;

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
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.joanzapata.iconify.widget.IconButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import br.com.appinbanker.inbanker.R;
import br.com.appinbanker.inbanker.adapters.TransacaoRecebidaAdapter;
import br.com.appinbanker.inbanker.entidades.Transacao;
import br.com.appinbanker.inbanker.entidades.Usuario;
import br.com.appinbanker.inbanker.interfaces.RecyclerViewOnClickListenerHack;
import br.com.appinbanker.inbanker.interfaces.WebServiceReturnStringHora;
import br.com.appinbanker.inbanker.interfaces.WebServiceReturnUsuario;
import br.com.appinbanker.inbanker.sqlite.BancoControllerUsuario;
import br.com.appinbanker.inbanker.sqlite.CriandoBanco;
import br.com.appinbanker.inbanker.util.AnalyticsApplication;
import br.com.appinbanker.inbanker.webservice.BuscaUsuarioCPF;
import br.com.appinbanker.inbanker.webservice.ObterHora;

public class PedidosRecebidosFragment extends Fragment implements WebServiceReturnStringHora,WebServiceReturnUsuario {

    private List<Transacao> mList;

    private int lastExpandedPosition = -1;

    private TransacaoRecebidaAdapter listAdapter;
    private ExpandableListView expListView;
    ArrayList<Transacao> listDataHeader;
    HashMap<String,Transacao> listDataChild;

    private BancoControllerUsuario crud;
    private Cursor cursor;
    private String cpf;

    private LinearLayout progress_lista_pedidos_recebidos;

    private RelativeLayout msg_lista_pedidos;

    private Tracker mTracker;

    public PedidosRecebidosFragment() {
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
        View view =  inflater.inflate(R.layout.fragment_pedidos_recebidos, container, false);

        setHasOptionsMenu(true);

        getActivity().setTitle("Pedidos Recebidos");

        // Obtain the shared Tracker instance.
        AnalyticsApplication application = (AnalyticsApplication) getActivity().getApplication();
        mTracker = application.getDefaultTracker();

        mTracker.setScreenName("PedidosRecebidosFragment");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        progress_lista_pedidos_recebidos = (LinearLayout) view.findViewById(R.id.progress_lista_pedidos_recebidos);

        msg_lista_pedidos = (RelativeLayout) view.findViewById(R.id.msg_lista_pedidos);

        crud = new BancoControllerUsuario(getActivity());
        cursor = crud.carregaDados();
        try {
            String cpf = cursor.getString(cursor.getColumnIndexOrThrow(CriandoBanco.CPF));
            String id_face = cursor.getString(cursor.getColumnIndexOrThrow(CriandoBanco.ID_FACE));
            if(!cpf.equals(""))
                new BuscaUsuarioCPF(cpf,getActivity(),this).execute();
            else{
                progress_lista_pedidos_recebidos.setVisibility(View.GONE);
                msg_lista_pedidos.setVisibility(View.VISIBLE);
            }
        }catch (Exception e){
            Log.i("Exception","Excessao Pedido recebido cpf = "+e);
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

    public void retornoUsuarioWebService(Usuario usu){

        msg_lista_pedidos.setVisibility(View.GONE);
        progress_lista_pedidos_recebidos.setVisibility(View.GONE);

        if(usu != null){

            //iremos adicionar a uma nova lista apenas as trasacoes de status diferente de 2, para posteriormente adicionarmos no adapter
            ArrayList<Transacao> list = new ArrayList<Transacao>();

            if(usu.getTransacoes_recebidas() != null) {
                mList = usu.getTransacoes_recebidas();


                for(int i = 0; i < mList.size(); i++){
                    int status = Integer.parseInt(mList.get(i).getStatus_transacao());
                    if(status  < 2){
                        list.add(mList.get(i));
                    }
                }

            }
            if(list.size() > 0) {
                mList.clear();
                mList = list;

                expListView.setVisibility(View.VISIBLE);

                setValue(list);

                //obter data atual do servidor para calcular os juros corretos
                new ObterHora(this).execute();

            }else{
                msg_lista_pedidos.setVisibility(View.VISIBLE);
            }
        }else{
            mensagem();
        }

    }

    @Override
    public void retornoObterHora(String hoje){
        Collections.sort(listDataHeader, new CustomComparator());
        listAdapter = new TransacaoRecebidaAdapter(getActivity(),listDataHeader, listDataChild,hoje);
        // setting list adapter
        expListView.setAdapter(listAdapter);
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
        //Transacao f = new Transacao();

        Log.i("Tamanho forum","tamanho eh "+forums.size());

        listDataHeader = new ArrayList<Transacao>();
        listDataChild = new HashMap<String,Transacao>();

        //String previous_header = null;
        for (int i = 0; i < forums.size(); i++) {

            listDataHeader.add(forums.get(i));
            listDataChild.put(listDataHeader.get(i).getId_trans(), forums.get(i));
        }

        //listDataChild.put(listDataHeader.get(0), generalList);

    }

    @Override
    public void retornoUsuarioWebServiceAux(Usuario usu){}

}
