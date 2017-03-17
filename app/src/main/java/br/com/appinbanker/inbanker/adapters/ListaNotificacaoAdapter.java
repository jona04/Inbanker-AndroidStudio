package br.com.appinbanker.inbanker.adapters;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;
import java.util.Locale;

import br.com.appinbanker.inbanker.R;
import br.com.appinbanker.inbanker.entidades.Amigos;
import br.com.appinbanker.inbanker.entidades.NotificacaoContrato;
import br.com.appinbanker.inbanker.interfaces.RecyclerViewOnClickListenerHack;

/**
 * Created by jonatassilva on 21/10/16.
 */

public class ListaNotificacaoAdapter extends RecyclerView.Adapter<ListaNotificacaoAdapter.MyViewHolder> {

    private List<NotificacaoContrato> mList;
    private LayoutInflater mLayoutInflater;
    private RecyclerViewOnClickListenerHack mRecyclerViewOnClickListenerHack;
    private Context c;

    public ListaNotificacaoAdapter(Context c, List<NotificacaoContrato> l){
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
        //Log.i("Script", "Inicio amigos adapter onCreateViewHolder ");
        View v = mLayoutInflater.inflate(R.layout.adapter_item_notificacao, viewGroup,false);
        MyViewHolder mvh = new MyViewHolder(v);

        return mvh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        Locale BRAZIL = new Locale("pt","BR");
        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        DateTimeFormatter dtfOut = DateTimeFormat.forPattern("EEE, d MMM yyyy");

        DateTime data_parse_utc = fmt.parseDateTime(mList.get(position).getDate());
        String data_parse_string = dtfOut.print(data_parse_utc);

        holder.tv_data_mensagem.setText(data_parse_string);
        holder.tv_mensagem.setText(mList.get(position).getMensagem());

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView tv_mensagem;
        public TextView tv_data_mensagem;

        public MyViewHolder(View itemView){
            super(itemView);

            tv_mensagem = (TextView) itemView.findViewById(R.id.tv_mensagem);
            tv_data_mensagem = (TextView) itemView.findViewById(R.id.tv_data_mensagem);

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
