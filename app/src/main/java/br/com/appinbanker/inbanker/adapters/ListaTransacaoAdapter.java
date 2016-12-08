package br.com.appinbanker.inbanker.adapters;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import br.com.appinbanker.inbanker.R;
import br.com.appinbanker.inbanker.entidades.Transacao;
import br.com.appinbanker.inbanker.interfaces.RecyclerViewOnClickListenerHack;

/**
 * Created by jonatassilva on 24/10/16.
 */

public class ListaTransacaoAdapter extends RecyclerView.Adapter<ListaTransacaoAdapter.MyViewHolder> {

    private List<Transacao> mList;
    private LayoutInflater mLayoutInflater;
    private RecyclerViewOnClickListenerHack mRecyclerViewOnClickListenerHack;
    private Context c;

    public ListaTransacaoAdapter(Context c, List<Transacao> l){
        c = c;
        mList = l;
        mLayoutInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setRecyclerViewOnClickListenerHack(RecyclerViewOnClickListenerHack r){
        mRecyclerViewOnClickListenerHack = r;

    }

    @Override
    public ListaTransacaoAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Log.i("Script", "Inicio amigos adapter onCreateViewHolder ");
        View v = mLayoutInflater.inflate(R.layout.adapter_item_transacao, viewGroup,false);
        ListaTransacaoAdapter.MyViewHolder mvh = new ListaTransacaoAdapter.MyViewHolder(v);

        return mvh;
    }

    @Override
    public void onBindViewHolder(ListaTransacaoAdapter.MyViewHolder holder, int position) {

        Log.i("Script", "Inicio amigos adapter onBindViewHolder ");

        Context context = holder.imagem.getContext();
        Uri uri;

        Locale ptBr = new Locale("pt", "BR");
        NumberFormat nf = NumberFormat.getCurrencyInstance(ptBr);
        String valor_formatado = nf.format (Double.parseDouble(mList.get(position).getValor()));

        //concatenamos a string atual com a nova que capturamos
        holder.tv_data_pedido.setText(holder.tv_data_pedido.getText().toString()+mList.get(position).getDataPedido());
        holder.tv_valor_pedido.setText(holder.tv_valor_pedido.getText().toString()+valor_formatado);

        holder.tv_nome_usuario.setText(mList.get(position).getNome_usu2());
        uri = Uri.parse(mList.get(position).getUrl_img_usu2());
        //Picasso.with(context).load(uri).into(holder.imagem);

        Transformation transformation = new RoundedTransformationBuilder()
                .borderColor(Color.GRAY)
                .borderWidthDp(3)
                .cornerRadiusDp(70)
                .oval(false)
                .build();

        Picasso.with(context)
                .load(uri)
                .transform(transformation)
                .into(holder.imagem);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ImageView imagem;
        public TextView tv_nome_usuario;
        public TextView tv_data_pedido;
        public TextView tv_valor_pedido;

        public MyViewHolder(View itemView){
            super(itemView);

            imagem = (ImageView) itemView.findViewById(R.id.icon);
            tv_nome_usuario = (TextView) itemView.findViewById(R.id.tv_nome_usuario);
            tv_data_pedido = (TextView) itemView.findViewById(R.id.tv_data_pedido);
            tv_valor_pedido = (TextView) itemView.findViewById(R.id.tv_valor_pedido);

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
