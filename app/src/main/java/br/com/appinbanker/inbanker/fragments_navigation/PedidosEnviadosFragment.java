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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.com.appinbanker.inbanker.R;
import br.com.appinbanker.inbanker.SimuladorPedido;
import br.com.appinbanker.inbanker.adapters.ListaAmigosAdapter;
import br.com.appinbanker.inbanker.adapters.ListaTransacaoAdapter;
import br.com.appinbanker.inbanker.entidades.Amigos;
import br.com.appinbanker.inbanker.entidades.Transacao;
import br.com.appinbanker.inbanker.entidades.Usuario;
import br.com.appinbanker.inbanker.interfaces.RecyclerViewOnClickListenerHack;
import br.com.appinbanker.inbanker.sqlite.BancoControllerUsuario;
import br.com.appinbanker.inbanker.sqlite.CriandoBanco;
import br.com.appinbanker.inbanker.webservice.BuscaUsuarioCPF;


public class PedidosEnviadosFragment extends Fragment implements RecyclerViewOnClickListenerHack {


    private OnFragmentInteractionListener mListener;


    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private List<Transacao> mList;

    private BancoControllerUsuario crud;
    private Cursor cursor;
    private String cpf;

    private LinearLayout progress_lista_pedidos_enviados;

    private RelativeLayout msg_lista_amigos;

    public PedidosEnviadosFragment() {
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
        View view = inflater.inflate(R.layout.fragment_pedidos_enviados, container, false);

        progress_lista_pedidos_enviados = (LinearLayout) view.findViewById(R.id.progress_lista_pedidos_enviados);

        msg_lista_amigos = (RelativeLayout) view.findViewById(R.id.msg_lista_amigos);

        crud = new BancoControllerUsuario(getActivity());
        cursor = crud.carregaDados();
        cpf = cursor.getString(cursor.getColumnIndexOrThrow(CriandoBanco.CPF));

        //busca pedidos enviados
        new BuscaUsuarioCPF(cpf,PedidosEnviadosFragment.this).execute();

        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_list_pedidos_env);

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

        msg_lista_amigos.setVisibility(View.GONE);
        progress_lista_pedidos_enviados.setVisibility(View.GONE);

        if(usu != null){

            if(usu.getTransacaoEnv() != null) {
                mList = usu.getTransacaoEnv();

                Log.i("webservice", "lista trans = " + mList);
                Log.i("webservice", "lista trans = " + usu.getTransacaoEnv().get(0).getUsu1());

                mRecyclerView.setVisibility(View.VISIBLE);
                ListaTransacaoAdapter adapter = new ListaTransacaoAdapter(getActivity(), mList);
                adapter.setRecyclerViewOnClickListenerHack(this);
                mRecyclerView.setAdapter(adapter);


            }else{
                msg_lista_amigos.setVisibility(View.VISIBLE);
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
    public void onClickListener(View view, int position) {

        Log.i("Script", "Click tste inicio =" + mList.get(position));


    }
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
