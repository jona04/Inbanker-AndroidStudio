package br.com.appinbanker.inbanker.fragments_navigation;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import br.com.appinbanker.inbanker.NavigationDrawerActivity;
import br.com.appinbanker.inbanker.R;
import br.com.appinbanker.inbanker.entidades.Transacao;
import br.com.appinbanker.inbanker.entidades.Usuario;
import br.com.appinbanker.inbanker.sqlite.BancoControllerUsuario;
import br.com.appinbanker.inbanker.sqlite.CriandoBanco;
import br.com.appinbanker.inbanker.webservice.BuscaUsuarioCPF;

public class InicioFragment extends Fragment {

    TextView badge_notification_ped_rec,badge_notification_pag_pen,badge_notification_ped_env;

    Button btn_pedir_emprestimo, btn_pedidos_recebidos, btn_pedidos_enviados, btn_pagamentos_pendentes;

    ProgressBar progress_bar_inicio;

    BancoControllerUsuario crud;
    Cursor cursor;

    public InicioFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_inicio, container, false);

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
            String cpf = cursor.getString(cursor.getColumnIndexOrThrow(CriandoBanco.CPF));
            new BuscaUsuarioCPF(cpf, null, null, null, null, InicioFragment.this).execute();
        }catch (Exception e){

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

    public void retornoBuscaUsuario(Usuario usu) {

        progress_bar_inicio.setVisibility(View.GONE);

        if(usu != null){

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



        }

    }

}
