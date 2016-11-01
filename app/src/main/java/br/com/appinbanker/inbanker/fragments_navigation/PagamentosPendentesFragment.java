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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import br.com.appinbanker.inbanker.R;
import br.com.appinbanker.inbanker.VerPagamentoPendente;
import br.com.appinbanker.inbanker.adapters.ListaTransacaoAdapter;
import br.com.appinbanker.inbanker.entidades.Transacao;
import br.com.appinbanker.inbanker.entidades.Usuario;
import br.com.appinbanker.inbanker.interfaces.RecyclerViewOnClickListenerHack;
import br.com.appinbanker.inbanker.sqlite.BancoControllerUsuario;
import br.com.appinbanker.inbanker.sqlite.CriandoBanco;
import br.com.appinbanker.inbanker.webservice.BuscaUsuarioCPF;

public class PagamentosPendentesFragment extends Fragment implements RecyclerViewOnClickListenerHack {

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private List<Transacao> mList;

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
        cpf = cursor.getString(cursor.getColumnIndexOrThrow(CriandoBanco.CPF));

        //busca pedidos enviados
        new BuscaUsuarioCPF(cpf,null,null,null,PagamentosPendentesFragment.this,null).execute();

        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_list_pagamentos);

        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //Log.i("Script", "onScrollStateChanged");
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //Log.i("Script", "onScrolled");
            }
        });
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        return view;
    }

    public void retornoBuscaUsuario(Usuario usu){

        progress_lista_pagamentos.setVisibility(View.GONE);

        if(usu != null){

            //iremos adicionar a uma nova lista apenas as trasacoes de status maior igual a 3 e menor igual a 5, para posteriormente adicionarmos no adapter
            ArrayList<Transacao> list = new ArrayList<Transacao>();

            if(usu.getTransacoes_enviadas() != null) {
                mList = usu.getTransacoes_enviadas();

                for(int i = 0; i < mList.size(); i++){
                    int status = Integer.parseInt(mList.get(i).getStatus_transacao());
                    if(status >= 3 && status <= 5 ){
                        list.add(mList.get(i));
                    }
                }
            }

            if(list.size() > 0) {
                mList = list;
                mRecyclerView.setVisibility(View.VISIBLE);
                ListaTransacaoAdapter adapter = new ListaTransacaoAdapter(getActivity(), list);
                adapter.setRecyclerViewOnClickListenerHack(this);
                mRecyclerView.setAdapter(adapter);
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

    @Override
    public void onClickListener(View view, int position) {

        //Log.i("Script", "Click tste inicio =" + mList.get(position));

        Intent it = new Intent(getActivity(), VerPagamentoPendente.class);
        Bundle b = new Bundle();
        b.putString("id",mList.get(position).getId_trans());
        b.putString("nome2",mList.get(position).getNome_usu2());
        b.putString("cpf1",mList.get(position).getUsu1());
        b.putString("cpf2",mList.get(position).getUsu2());
        b.putString("data_pedido",mList.get(position).getDataPedido());
        b.putString("nome1", mList.get(position).getNome_usu1());
        b.putString("valor",mList.get(position).getValor());
        b.putString("vencimento", mList.get(position).getVencimento());
        b.putString("img1", mList.get(position).getUrl_img_usu1());
        b.putString("img2", mList.get(position).getUrl_img_usu2());
        b.putString("status_transacao", mList.get(position).getStatus_transacao());
        it.putExtras(b);
        startActivity(it);


    }

}