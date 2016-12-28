package br.com.appinbanker.inbanker.fragments_navigation;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import br.com.appinbanker.inbanker.NavigationDrawerActivity;
import br.com.appinbanker.inbanker.R;
import br.com.appinbanker.inbanker.entidades.Usuario;
import br.com.appinbanker.inbanker.sqlite.BancoControllerUsuario;
import br.com.appinbanker.inbanker.sqlite.CriandoBanco;

public class InicioFragment extends Fragment {

    TextView badge_notification_ped_rec,badge_notification_pag_pen,badge_notification_ped_env;

    Button btn_pedir_emprestimo, btn_pedidos_recebidos, btn_pedidos_enviados, btn_pagamentos_pendentes;

    ProgressBar progress_bar_inicio;

    BancoControllerUsuario crud;
    Cursor cursor;

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference usuarioReferencia = databaseReference.child("usuarios");
    private FirebaseAuth firebaseAuth;

    public InicioFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_inicio, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        progress_bar_inicio = (ProgressBar) view.findViewById(R.id.progress_bar_inicio);
        badge_notification_ped_rec = (TextView) view.findViewById(R.id.badge_notification_ped_rec);
        badge_notification_pag_pen = (TextView) view.findViewById(R.id.badge_notification_pag_pen);
        badge_notification_ped_env = (TextView) view.findViewById(R.id.badge_notification_ped_env);

        btn_pedir_emprestimo = (Button) view.findViewById(R.id.btn_pedir_emprestimo);
        btn_pedidos_enviados = (Button) view.findViewById(R.id.btn_pedidos_enviados);
        btn_pedidos_recebidos = (Button) view.findViewById(R.id.btn_pedidos_recebidos);
        btn_pagamentos_pendentes = (Button) view.findViewById(R.id.btn_pagamentos_pedentes);

        //fazemos uma busca do usuario logando no banco para mostrarmos corretamente as notificações interna nos butons da tela incio
        crud = new BancoControllerUsuario(getActivity());
        cursor = crud.carregaDados();
        try {
            String id_firebase = cursor.getString(cursor.getColumnIndexOrThrow(CriandoBanco.ID_FIREBASE));
            if(!id_firebase.equals("")) {

                //new BuscaUsuarioCPF(cpf,getActivity(),this).execute();

                obterDadosUsuarioFireBase(id_firebase);

            }else{
                if(firebaseAuth.getCurrentUser() != null)
                    obterDadosUsuarioFireBase(firebaseAuth.getCurrentUser().getUid().toString());
                else
                    progress_bar_inicio.setVisibility(View.INVISIBLE);
            }
        }catch (Exception e){
            Log.i("Excetion inicio","Execpetion cpf sqlite = "+e);
        }
        btn_pedir_emprestimo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(getActivity(),NavigationDrawerActivity.class);
                Bundle b = new Bundle();
                b.putInt("menu_item", NavigationDrawerActivity.MENU_PEDIR_EMPRESTIMO);
                it.putExtras(b);
                startActivity(it);

                getActivity().finish();

            }
        });
        btn_pedidos_enviados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent it = new Intent(getActivity(),NavigationDrawerActivity.class);
                Bundle b = new Bundle();
                b.putInt("menu_item", NavigationDrawerActivity.MENU_PEDIDOS_ENVIADOS);
                it.putExtras(b);
                startActivity(it);

                getActivity().finish();
            }
        });
        btn_pedidos_recebidos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent it = new Intent(getActivity(),NavigationDrawerActivity.class);
                Bundle b = new Bundle();
                b.putInt("menu_item", NavigationDrawerActivity.MENU_PEDIDOS_RECEBIDOS);
                it.putExtras(b);
                startActivity(it);

                getActivity().finish();
            }
        });
        btn_pagamentos_pendentes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent it = new Intent(getActivity(),NavigationDrawerActivity.class);
                Bundle b = new Bundle();
                b.putInt("menu_item", NavigationDrawerActivity.MENU_PAGAMENTOS_ABERTO);
                it.putExtras(b);
                startActivity(it);

                getActivity().finish();
            }
        });

        return view;
    }

    public void obterDadosUsuarioFireBase(String id_firebase) {

        DatabaseReference trans_enviadas = usuarioReferencia.child(id_firebase);
        trans_enviadas.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Usuario usu = dataSnapshot.getValue(Usuario.class);


                Log.i("Teste firebase","Usuario = "+usu.getNome());

                atualizaBadges(usu);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i("Teste firebase","cancelado firebase procurar por usuario");
            }
        });

    }

    public void atualizaBadges(Usuario usu){
        int count_trans_env = 0;
        int count_trans_rec = 0;
        int count_pag_pen = 0;

        if(usu.getTransacoes_enviadas() != null) {

            for(int i = 0; i < usu.getTransacoes_enviadas().size(); i ++){
                int status = Integer.parseInt(usu.getTransacoes_enviadas().get(i).getStatus_transacao());
                if(status <=1 )
                    count_trans_env++;
                if(status >= 3 && status <= 5)
                    count_pag_pen++;
            }

        }

        if(usu.getTransacoes_recebidas() != null) {

            for(int i = 0; i < usu.getTransacoes_recebidas().size(); i ++){
                int status = Integer.parseInt(usu.getTransacoes_recebidas().get(i).getStatus_transacao());

                if(status != 2 && status < 6)
                    count_trans_rec++;
            }

        }

        if(count_pag_pen >0){
            badge_notification_pag_pen.setVisibility(View.VISIBLE);
            badge_notification_pag_pen.setText(String.valueOf(count_pag_pen));
        }
        if(count_trans_env >0){
            badge_notification_ped_env.setVisibility(View.VISIBLE);
            badge_notification_ped_env.setText(String.valueOf(count_trans_env));
        }
        if(count_trans_rec >0){
            badge_notification_ped_rec.setVisibility(View.VISIBLE);
            badge_notification_ped_rec.setText(String.valueOf(count_trans_rec));
        }

        progress_bar_inicio.setVisibility(View.INVISIBLE);
    }

    public void mensagem(String titulo,String corpo,String botao)
    {
        AlertDialog.Builder mensagem = new AlertDialog.Builder(getActivity());
        mensagem.setTitle(titulo);
        mensagem.setMessage(corpo);
        mensagem.setNeutralButton(botao,null);
        mensagem.show();
    }
}
