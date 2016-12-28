package br.com.appinbanker.inbanker.fragments_navigation;

import android.content.Intent;
import android.database.Cursor;
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
import br.com.appinbanker.inbanker.adapters.ListaTransacaoAdapter;
import br.com.appinbanker.inbanker.entidades.Transacao;
import br.com.appinbanker.inbanker.entidades.Usuario;
import br.com.appinbanker.inbanker.interfaces.RecyclerViewOnClickListenerHack;
import br.com.appinbanker.inbanker.interfaces.WebServiceReturnUsuario;
import br.com.appinbanker.inbanker.sqlite.BancoControllerUsuario;
import br.com.appinbanker.inbanker.sqlite.CriandoBanco;
import br.com.appinbanker.inbanker.webservice.BuscaUsuarioCPF;
import br.com.appinbanker.inbanker.webservice.BuscaUsuarioFace;


public class PedidosEnviadosFragment extends Fragment implements RecyclerViewOnClickListenerHack,WebServiceReturnUsuario {

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private List<Transacao> mList;

    private BancoControllerUsuario crud;
    private Cursor cursor;
    private String cpf;

    private LinearLayout progress_lista_pedidos_enviados;

    private RelativeLayout msg_lista_pedidos;

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

        msg_lista_pedidos = (RelativeLayout) view.findViewById(R.id.msg_lista_pedidos);

        crud = new BancoControllerUsuario(getActivity());
        cursor = crud.carregaDados();
        try {
            String cpf = cursor.getString(cursor.getColumnIndexOrThrow(CriandoBanco.CPF));
            String id_face = cursor.getString(cursor.getColumnIndexOrThrow(CriandoBanco.ID_FACE));
            //Log.i("Sqlite","valor cpf = "+cpf+" valor id_face = "+id_face);
            if(!cpf.equals(""))
                new BuscaUsuarioCPF(cpf,getActivity(),this).execute();
        }catch (Exception e){
            Log.i("Exception","Excessao Pedido enviado cpf = "+e);
        }

        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_list_pedidos_env);

        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        return view;

    }

    public void retornoUsuarioWebService(Usuario usu){

        progress_lista_pedidos_enviados.setVisibility(View.GONE);

        if(usu != null){

            //iremos adicionar a uma nova lista apenas as trasacoes de status menor igual a 1, para posteriormente adicionarmos no adapter
            ArrayList<Transacao> list = new ArrayList<Transacao>();

            /*if(usu.getTransacoes_enviadas() != null) {
                mList = usu.getTransacoes_enviadas();


                for(int i = 0; i < mList.size(); i++){

                    int status = Integer.parseInt(mList.get(i).getStatus_transacao());
                    if(status <= 1){
                        list.add(mList.get(i));
                    }
                }



            }*/

            if(list.size() > 0) {
                mList = list;
                mRecyclerView.setVisibility(View.VISIBLE);
                ListaTransacaoAdapter adapter = new ListaTransacaoAdapter(getActivity(), list);
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
    public void onClickListener(View view, int position) {

        //Log.i("Script", "Click tste inicio =" + mList.get(position));

        Intent it = new Intent(getActivity(), VerPedidoEnviado.class);
        it.putExtra("transacao",mList.get(position));
        startActivity(it);


    }

}
