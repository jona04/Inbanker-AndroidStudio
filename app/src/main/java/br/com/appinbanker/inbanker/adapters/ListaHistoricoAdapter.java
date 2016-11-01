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

import java.util.List;

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
        c = c;
        meu_cpf = meu_cpf;
        mList = l;
        mLayoutInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setRecyclerViewOnClickListenerHack(RecyclerViewOnClickListenerHack r){
        mRecyclerViewOnClickListenerHack = r;

    }

    //remove item da lista
    /*public void removeListItem(int position){
        mList.remove(position);
        notifyItemRemoved(position);
    }*/

    @Override
    public ListaHistoricoAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Log.i("Script", "Inicio historico adapter onCreateViewHolder ");
        View v = mLayoutInflater.inflate(R.layout.adapter_item_amigos, viewGroup,false);
        ListaHistoricoAdapter.MyViewHolder mvh = new ListaHistoricoAdapter.MyViewHolder(v);

        return mvh;
    }

    @Override
    public void onBindViewHolder(ListaHistoricoAdapter.MyViewHolder holder, int position) {
        Log.i("Script", "Inicio historico adapter onBindViewHolder ");

        Context context = holder.imagem.getContext();
        Uri uri;

        if(meu_cpf == mList.get(position).getUsu1()) {
            holder.tv_nome_usuario.setText(mList.get(position).getNome_usu2());
            uri = Uri.parse(mList.get(position).getUrl_img_usu2());
            Picasso.with(context).load(uri).into(holder.imagem);
        }else{
            holder.tv_nome_usuario.setText(mList.get(position).getNome_usu2());
            uri = Uri.parse(mList.get(position).getUrl_img_usu2());
            Picasso.with(context).load(uri).into(holder.imagem);
        }

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ImageView imagem;
        public TextView tv_nome_usuario;

        public MyViewHolder(View itemView){
            super(itemView);

            imagem = (ImageView) itemView.findViewById(R.id.icon);
            tv_nome_usuario = (TextView) itemView.findViewById(R.id.tv_nome_usuario);

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