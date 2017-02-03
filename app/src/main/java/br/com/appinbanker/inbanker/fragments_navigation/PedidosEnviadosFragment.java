package br.com.appinbanker.inbanker.fragments_navigation;

import android.database.Cursor;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.AlertDialog;
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
import br.com.appinbanker.inbanker.adapters.TransacaoEnvAdapter;
import br.com.appinbanker.inbanker.adapters.TransacaoPendenteAdapter;
import br.com.appinbanker.inbanker.entidades.Transacao;
import br.com.appinbanker.inbanker.entidades.Usuario;
import br.com.appinbanker.inbanker.interfaces.WebServiceReturnStringHora;
import br.com.appinbanker.inbanker.interfaces.WebServiceReturnUsuario;
import br.com.appinbanker.inbanker.sqlite.BancoControllerUsuario;
import br.com.appinbanker.inbanker.sqlite.CriandoBanco;
import br.com.appinbanker.inbanker.webservice.BuscaUsuarioCPF;
import br.com.appinbanker.inbanker.webservice.ObterHora;


public class PedidosEnviadosFragment extends Fragment implements WebServiceReturnStringHora,WebServiceReturnUsuario{

    private List<Transacao> mList;

    private int lastExpandedPosition = -1;

    private TransacaoEnvAdapter listAdapter;
    private ExpandableListView expListView;
    ArrayList<Transacao> listDataHeader;
    HashMap<String,Transacao> listDataChild;

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
            else{
                progress_lista_pedidos_enviados.setVisibility(View.GONE);
                msg_lista_pedidos.setVisibility(View.VISIBLE);
            }
        }catch (Exception e){
            Log.i("Exception","Excessao Pedido enviado cpf = "+e);
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

        progress_lista_pedidos_enviados.setVisibility(View.GONE);
        expListView.setVisibility(View.VISIBLE);

        if(usu != null){

            //iremos adicionar a uma nova lista apenas as trasacoes de status menor igual a 1, para posteriormente adicionarmos no adapter
            ArrayList<Transacao> list = new ArrayList<Transacao>();

            if(usu.getTransacoes_enviadas() != null) {
                mList = usu.getTransacoes_enviadas();


                for(int i = 0; i < mList.size(); i++){

                    int status = Integer.parseInt(mList.get(i).getStatus_transacao());
                    if(status <= 1){
                        list.add(mList.get(i));
                    }
                }



            }

            if(list.size() > 0) {
                mList = list;

                expListView.setVisibility(View.VISIBLE);

                setValue(mList);

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
        listAdapter = new TransacaoEnvAdapter(getActivity(),listDataHeader, listDataChild,hoje);
        // setting list adapter
        expListView.setAdapter(listAdapter);
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

    @Override
    public void retornoUsuarioWebServiceAux(Usuario usu){}

}
