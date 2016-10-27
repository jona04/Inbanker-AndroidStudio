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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import br.com.appinbanker.inbanker.R;
import br.com.appinbanker.inbanker.VerPedidoEnviado;
import br.com.appinbanker.inbanker.VerPedidoRecebido;
import br.com.appinbanker.inbanker.adapters.ListaTransacaoAdapter;
import br.com.appinbanker.inbanker.adapters.ListaTransacaoRecAdapter;
import br.com.appinbanker.inbanker.entidades.Transacao;
import br.com.appinbanker.inbanker.entidades.Usuario;
import br.com.appinbanker.inbanker.interfaces.RecyclerViewOnClickListenerHack;
import br.com.appinbanker.inbanker.sqlite.BancoControllerUsuario;
import br.com.appinbanker.inbanker.sqlite.CriandoBanco;
import br.com.appinbanker.inbanker.webservice.BuscaUsuarioCPF;

public class PedidosRecebidosFragment extends Fragment implements RecyclerViewOnClickListenerHack {

    private PedidosRecebidosFragment.OnFragmentInteractionListener mListener;

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private List<Transacao> mList;

    private BancoControllerUsuario crud;
    private Cursor cursor;
    private String cpf;

    private LinearLayout progress_lista_pedidos_recebidos;

    private RelativeLayout msg_lista_pedidos;


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

        progress_lista_pedidos_recebidos = (LinearLayout) view.findViewById(R.id.progress_lista_pedidos_recebidos);

        msg_lista_pedidos = (RelativeLayout) view.findViewById(R.id.msg_lista_pedidos);

        crud = new BancoControllerUsuario(getActivity());
        cursor = crud.carregaDados();
        cpf = cursor.getString(cursor.getColumnIndexOrThrow(CriandoBanco.CPF));

        //busca pedidos enviados
        new BuscaUsuarioCPF(cpf,null,PedidosRecebidosFragment.this,null,null,null).execute();

        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_list_pedidos_rec);

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

        msg_lista_pedidos.setVisibility(View.GONE);
        progress_lista_pedidos_recebidos.setVisibility(View.GONE);

        if(usu != null){

            //iremos adicionar a uma nova lista apenas as trasacoes de status diferente de 2, para posteriormente adicionarmos no adapter
            ArrayList<Transacao> list = new ArrayList<Transacao>();

            if(usu.getTransacoes_recebidas() != null) {
                mList = usu.getTransacoes_recebidas();


                for(int i = 0; i < mList.size(); i++){
                    int status = Integer.parseInt(mList.get(i).getStatus_transacao());
                    if(status != 2 && status < 6){
                        list.add(mList.get(i));
                    }
                }

            }
            if(list.size() > 0) {
                mList = list;
                mRecyclerView.setVisibility(View.VISIBLE);
                ListaTransacaoRecAdapter adapter = new ListaTransacaoRecAdapter(getActivity(), list);
                adapter.setRecyclerViewOnClickListenerHack(this);
                mRecyclerView.setAdapter(adapter);
            }else{
                msg_lista_pedidos.setVisibility(View.VISIBLE);
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
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClickListener(View view, int position) {

        //Log.i("Script", "Click tste inicio =" + mList.get(position));

        Intent it = new Intent(getActivity(), VerPedidoRecebido.class);
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
