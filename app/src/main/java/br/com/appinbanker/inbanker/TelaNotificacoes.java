package br.com.appinbanker.inbanker;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import br.com.appinbanker.inbanker.adapters.ListaAmigosAdapter;
import br.com.appinbanker.inbanker.adapters.ListaNotificacaoAdapter;
import br.com.appinbanker.inbanker.adapters.TransacaoEnvAdapter;
import br.com.appinbanker.inbanker.entidades.NotificacaoContrato;
import br.com.appinbanker.inbanker.entidades.Transacao;
import br.com.appinbanker.inbanker.entidades.Usuario;
import br.com.appinbanker.inbanker.interfaces.RecyclerViewOnClickListenerHack;
import br.com.appinbanker.inbanker.interfaces.WebServiceReturnUsuario;
import br.com.appinbanker.inbanker.sqlite.BancoControllerUsuario;
import br.com.appinbanker.inbanker.sqlite.CriandoBanco;
import br.com.appinbanker.inbanker.util.AllSharedPreferences;
import br.com.appinbanker.inbanker.webservice.BuscaUsuarioCPF;
import br.com.appinbanker.inbanker.webservice.ObterHora;

public class TelaNotificacoes extends AppCompatActivity implements WebServiceReturnUsuario,RecyclerViewOnClickListenerHack {

    LinearLayout progress_lista_notificacoes;
    RelativeLayout msg_lista_notificacoes;

    private BancoControllerUsuario crud;
    private Cursor cursor;

    //private ListNotificaAdapter listAdapter;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_tela_notificacoes);

        AllSharedPreferences.putPreferences(AllSharedPreferences.COUNT_NOTIFY_CARTA,String.valueOf(0),this);
        AllSharedPreferences.putPreferences(AllSharedPreferences.VERIFY_NOTIFY_CARTA, "verificado", this);

        progress_lista_notificacoes = (LinearLayout) findViewById(R.id.progress_lista_notificacoes);

        msg_lista_notificacoes = (RelativeLayout) findViewById(R.id.msg_lista_notificacoes);

        mRecyclerView = (RecyclerView) findViewById(R.id.notificacaoList);
        crud = new BancoControllerUsuario(this);
        cursor = crud.carregaDados();
        try {
            String cpf = cursor.getString(cursor.getColumnIndexOrThrow(CriandoBanco.CPF));
            String id_face = cursor.getString(cursor.getColumnIndexOrThrow(CriandoBanco.ID_FACE));
            //Log.i("Sqlite","valor cpf = "+cpf+" valor id_face = "+id_face);
            if(!cpf.equals(""))
                new BuscaUsuarioCPF(cpf,this,this).execute();
            else{
                progress_lista_notificacoes.setVisibility(View.GONE);
                msg_lista_notificacoes.setVisibility(View.VISIBLE);
            }
        }catch (Exception e){
            Log.i("Exception","Excessao Pedido enviado cpf = "+e);
        }

        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

    }

    @Override
    public void retornoUsuarioWebService(Usuario usu) {
        progress_lista_notificacoes.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);

        if(usu != null){
            if(usu.getNotificacaoContrato() != null) {

                ListaNotificacaoAdapter adapter = new ListaNotificacaoAdapter(this, usu.getNotificacaoContrato());
                adapter.setRecyclerViewOnClickListenerHack(this);
                mRecyclerView.setAdapter(adapter);

            }else{
                msg_lista_notificacoes.setVisibility(View.VISIBLE);
            }


        }else{
            //mensagem();
        }
    }

    @Override
    public void retornoUsuarioWebServiceAux(Usuario usu) {

    }

    @Override
    public void onClickListener(View view, int position) {
        Log.i("Script","Click lista notificacoes");
    }
}
