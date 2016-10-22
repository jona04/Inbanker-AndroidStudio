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

import org.w3c.dom.Text;

import java.util.List;

import br.com.appinbanker.inbanker.R;
import br.com.appinbanker.inbanker.entidades.Amigos;
import br.com.appinbanker.inbanker.interfaces.RecyclerViewOnClickListenerHack;

/**
 * Created by jonatassilva on 21/10/16.
 */

public class ListaAmigosAdapter extends RecyclerView.Adapter<ListaAmigosAdapter.MyViewHolder> {

    private List<Amigos> mList;
    private LayoutInflater mLayoutInflater;
    private RecyclerViewOnClickListenerHack mRecyclerViewOnClickListenerHack;
    private Context c;

    public ListaAmigosAdapter(Context c, List<Amigos> l){
        c = c;
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
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Log.i("Script", "Inicio amigos adapter onCreateViewHolder ");
        View v = mLayoutInflater.inflate(R.layout.adapter_item_amigos, viewGroup,false);
        MyViewHolder mvh = new MyViewHolder(v);

        return mvh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Log.i("Script", "Inicio amigos adapter onBindViewHolder ");
        holder.tv_nome_usuario.setText(mList.get(position).getName());

        //String url = mList.get(position).getPicture().getData().getUrl();
        //Uri uri = Uri.parse(mList.get(position).getPicture().getData().getUrl());
        //Picasso.with((c)).load(url).into(imagem);
        //holder.imagem.setImageURI(uri);
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

            imagem = (ImageView) itemView.findViewById(R.id.image);
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
