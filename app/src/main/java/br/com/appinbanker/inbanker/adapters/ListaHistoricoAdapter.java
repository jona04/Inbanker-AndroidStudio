package br.com.appinbanker.inbanker.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import br.com.appinbanker.inbanker.R;
import br.com.appinbanker.inbanker.entidades.Amigos;
import br.com.appinbanker.inbanker.entidades.Transacao;
import br.com.appinbanker.inbanker.interfaces.RecyclerViewOnClickListenerHack;

/**
 * Created by Jonatas on 26/10/2016.
 */

public class ListaHistoricoAdapter extends RecyclerView.Adapter<ListaHistoricoAdapter.MyViewHolder>{

    private List<Transacao> mList;
    private LayoutInflater mLayoutInflater;
    private RecyclerViewOnClickListenerHack mRecyclerViewOnClickListenerHack;
    private Context c;

    //utilizado especialmente no historico para sabermos qual usuario mostrar na listagem
    private String meu_cpf;

    public ListaHistoricoAdapter(Context c, List<Transacao> l,String meu_cpf){
        this.c = c;
        this.meu_cpf = meu_cpf;
        this.mList = l;
        this.mLayoutInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setRecyclerViewOnClickListenerHack(RecyclerViewOnClickListenerHack r){
        this.mRecyclerViewOnClickListenerHack = r;

    }

    //remove item da lista
    /*public void removeListItem(int position){
        mList.remove(position);
        notifyItemRemoved(position);
    }*/

    @Override
    public ListaHistoricoAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Log.i("Script", "Inicio historico adapter onCreateViewHolder ");
        View v = mLayoutInflater.inflate(R.layout.adapter_item_historico, viewGroup,false);
        ListaHistoricoAdapter.MyViewHolder mvh = new ListaHistoricoAdapter.MyViewHolder(v);

        return mvh;
    }

    @Override
    public void onBindViewHolder(ListaHistoricoAdapter.MyViewHolder holder, int position) {
        Log.i("Script", "Inicio historico adapter onBindViewHolder ");

        //Context context = holder.imagem.getContext();
        //Uri uri;

        Locale ptBr = new Locale("pt", "BR");
        NumberFormat nf = NumberFormat.getCurrencyInstance(ptBr);
        String valor_formatado = nf.format (Double.parseDouble(mList.get(position).getValor()));

        //concatenamos a string atual com a nova que capturamos
        holder.tv_data_pedido.setText(holder.tv_data_pedido.getText().toString()+mList.get(position).getDataPedido());

        if(meu_cpf.equals(mList.get(position).getUsu1().toString())) {
            holder.tv_valor_pedido.setText("- "+valor_formatado);
            holder.tv_nome_usuario.setText(mList.get(position).getNome_usu2());
            //uri = Uri.parse(mList.get(position).getUrl_img_usu2());
            //Picasso.with(context).load(uri).into(holder.imagem);
        }else{
            holder.tv_valor_pedido.setText(valor_formatado);
            holder.tv_nome_usuario.setText(mList.get(position).getNome_usu1());
            //uri = Uri.parse(mList.get(position).getUrl_img_usu1());
            //Picasso.with(context).load(uri).into(holder.imagem);
        }

        if(mList.get(position).getData_pagamento().length()>5) {
            String data_enc = mList.get(position).getData_pagamento().substring(0, mList.get(position).getData_pagamento().length()-5);
            holder.tv_data_finalizado.setText(data_enc);
            //holder.tv_status_pedido.setText("Pedido completado");
            //holder.tv_status_pedido.setTextColor(c.getResources().getColor(R.color.colorGreen));
        }else {
            String data_enc = mList.get(position).getData_recusada().substring(0, mList.get(position).getData_recusada().length()-5);
            holder.tv_data_finalizado.setText(data_enc);
            //holder.tv_status_pedido.setText("Pedido cancelado");
            //holder.tv_status_pedido.setTextColor(c.getResources().getColor(R.color.colorRed));
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        //public ImageView imagem;
        public TextView tv_nome_usuario;
        public TextView tv_data_pedido;
        public TextView tv_valor_pedido;
        public TextView tv_data_finalizado;

        public MyViewHolder(View itemView){
            super(itemView);

            //imagem = (ImageView) itemView.findViewById(R.id.icon);
            tv_nome_usuario = (TextView) itemView.findViewById(R.id.tv_nome_usuario);
            tv_data_pedido = (TextView) itemView.findViewById(R.id.tv_data_pedido);
            tv_valor_pedido = (TextView) itemView.findViewById(R.id.tv_valor_pedido);

            tv_data_finalizado = (TextView) itemView.findViewById(R.id.tv_data_finalizado);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(mRecyclerViewOnClickListenerHack != null){
                mRecyclerViewOnClickListenerHack.onClickListener(v, getPosition());
            }
        }
    }

}
