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
import br.com.appinbanker.inbanker.VerPagamentoPendente;
import br.com.appinbanker.inbanker.adapters.ListaTransacaoAdapter;
import br.com.appinbanker.inbanker.adapters.TransacaoEnvAdapter;
import br.com.appinbanker.inbanker.adapters.TransacaoPendenteAdapter;
import br.com.appinbanker.inbanker.entidades.Transacao;
import br.com.appinbanker.inbanker.entidades.Usuario;
import br.com.appinbanker.inbanker.interfaces.RecyclerViewOnClickListenerHack;
import br.com.appinbanker.inbanker.interfaces.WebServiceReturnUsuario;
import br.com.appinbanker.inbanker.interfaces.WebServiceReturnUsuarioFace;
import br.com.appinbanker.inbanker.sqlite.BancoControllerUsuario;
import br.com.appinbanker.inbanker.sqlite.CriandoBanco;
import br.com.appinbanker.inbanker.webservice.BuscaUsuarioCPF;
import br.com.appinbanker.inbanker.webservice.BuscaUsuarioFace;

public class PagamentosPendentesFragment extends Fragment implements WebServiceReturnUsuario {

    //private List<Transacao> mList;

    private int lastExpandedPosition = -1;

    private TransacaoPendenteAdapter listAdapter;
    private ExpandableListView expListView;
    ArrayList<Transacao> listDataHeader;
    HashMap<String,Transacao> listDataChild;

    private BancoControllerUsuario crud;
    private Cursor cursor;
    private String cpf;

    private LinearLayout progress_lista_pagamentos;

    private RelativeLayout msg_lista_pagamentos;

    public PagamentosPendentesFragment() {
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
        View view = inflater.inflate(R.layout.fragment_pagamentos, container, false);

        progress_lista_pagamentos = (LinearLayout) view.findViewById(R.id.progress_lista_pagamentos);

        msg_lista_pagamentos = (RelativeLayout) view.findViewById(R.id.msg_lista_pagamentos);

        crud = new BancoControllerUsuario(getActivity());
        cursor = crud.carregaDados();
        try {
            String cpf = cursor.getString(cursor.getColumnIndexOrThrow(CriandoBanco.CPF));
            String id_face = cursor.getString(cursor.getColumnIndexOrThrow(CriandoBanco.ID_FACE));
            if(!cpf.equals(""))
                new BuscaUsuarioCPF(cpf,getActivity(),this).execute();
        }catch (Exception e){
            Log.i("Exception","Excessao Pedido pagamento pendente cpf = "+e);
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

    public void retornoUsuarioWebService(Usuario usu){

        progress_lista_pagamentos.setVisibility(View.GONE);

        if(usu != null){

            //iremos adicionar a uma nova lista apenas as trasacoes de status maior igual a 3 e menor igual a 5, para posteriormente adicionarmos no adapter
            ArrayList<Transacao> list = new ArrayList<Transacao>();

            if(usu.getTransacoes_enviadas() != null) {

                for(int i = 0; i < usu.getTransacoes_enviadas().size(); i++){
                    int status = Integer.parseInt(usu.getTransacoes_enviadas().get(i).getStatus_transacao());
                    if(status >= 3 && status <= 5 ){
                        list.add(usu.getTransacoes_enviadas().get(i));
                    }
                }
            }

            if(usu.getTransacoes_recebidas() != null) {

                for(int i = 0; i < usu.getTransacoes_recebidas().size(); i ++){
                    int status = Integer.parseInt(usu.getTransacoes_recebidas().get(i).getStatus_transacao());

                    if(status == 3 || status == 5)
                        list.add(usu.getTransacoes_recebidas().get(i));
                }

            }

            if(list.size() > 0) {

                expListView.setVisibility(View.VISIBLE);

                setValue(list);

                listAdapter = new TransacaoPendenteAdapter(getActivity(),listDataHeader, listDataChild);
                // setting list adapter
                expListView.setAdapter(listAdapter);

            }else{
                msg_lista_pagamentos.setVisibility(View.VISIBLE);
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

    /*@Override
    public void onClickListener(View view, int position) {

        //Log.i("Script", "Click tste inicio =" + mList.get(position));

        Intent it = new Intent(getActivity(), VerPagamentoPendente.class);
        it.putExtra("transacao",mList.get(position));
        startActivity(it);


    }*/

    private void setValue(List<Transacao> forums) {

        //List generalList = new ArrayList();
        Transacao f = new Transacao();

        listDataHeader = new ArrayList<Transacao>();
        listDataChild = new HashMap<String,Transacao>();

        String previous_header = null;
        for (int i = 0; i < forums.size(); i++) {
            //String header = forums.get(i).getNome_usu1();
            //if (!header.equals(previous_header)) {
                listDataHeader.add(forums.get(i));
            //}
            //if (header.equals("General")) {
            //    generalList.add(forums.get(i));
            //} else

            listDataChild.put(listDataHeader.get(i).getId_trans(), forums.get(i));
            //previous_header = header;
        }

        //listDataChild.put(listDataHeader.get(0), generalList);

    }


    @Override
    public void retornoUsuarioWebServiceAuxInicioToken(Usuario usu){}

}
