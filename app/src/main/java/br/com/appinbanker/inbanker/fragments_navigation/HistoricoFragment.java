package br.com.appinbanker.inbanker.fragments_navigation;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.com.appinbanker.inbanker.R;
import br.com.appinbanker.inbanker.VerHistorico;
import br.com.appinbanker.inbanker.VerPedidoEnviado;
import br.com.appinbanker.inbanker.adapters.ListaHistoricoAdapter;
import br.com.appinbanker.inbanker.adapters.ListaTransacaoAdapter;
import br.com.appinbanker.inbanker.adapters.TransacaoHistoricoAdapter;
import br.com.appinbanker.inbanker.adapters.TransacaoPendenteAdapter;
import br.com.appinbanker.inbanker.entidades.Transacao;
import br.com.appinbanker.inbanker.entidades.Usuario;
import br.com.appinbanker.inbanker.interfaces.RecyclerViewOnClickListenerHack;
import br.com.appinbanker.inbanker.sqlite.BancoControllerUsuario;
import br.com.appinbanker.inbanker.sqlite.CriandoBanco;
import br.com.appinbanker.inbanker.webservice.BuscaUsuarioCPF;
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

    public void retornoBuscaUsuario(Usuario usu){

        progress_lista_historico.setVisibility(View.GONE);
        if(usu != null){

            //iremos adicionar a uma nova lista apenas as trasacoes de status 2 (historico), para posteriormente adicionarmos no adapter
            ArrayList<Transacao> list = new ArrayList<>();

            if(usu.getTransacoes_enviadas() != null) {

                for(int i = 0; i < usu.getTransacoes_enviadas().size(); i++){
                    int status = Integer.parseInt(usu.getTransacoes_enviadas().get(i).getStatus_transacao());
                    if(status == 2 || status >= 6){
                        list.add(usu.getTransacoes_enviadas().get(i));
                    }
                }

            }

            if(usu.getTransacoes_recebidas() != null) {
                for(int i = 0; i < usu.getTransacoes_recebidas().size(); i++){
                    //Log.i("webservice", "lista list = " + i+" - "+mList.get(i).getStatus_transacao());
                    int status = Integer.parseInt(usu.getTransacoes_recebidas().get(i).getStatus_transacao());
                    if(status == 2 || status >= 6){
                        list.add(usu.getTransacoes_recebidas().get(i));
                    }
                }

            }

            if(list.size() > 0) {
                expListView.setVisibility(View.VISIBLE);

                setValue(list);

                listAdapter = new TransacaoHistoricoAdapter(getActivity(),listDataHeader, listDataChild);
                // setting list adapter
                expListView.setAdapter(listAdapter);
            }else{
                msg_lista_historico.setVisibility(View.VISIBLE);
            }
        }else{
            mensagem();
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
