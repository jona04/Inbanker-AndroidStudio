package br.com.appinbanker.inbanker.gcm;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.gcm.GcmListenerService;

import br.com.appinbanker.inbanker.NavigationDrawerActivity;
import br.com.appinbanker.inbanker.R;
import br.com.appinbanker.inbanker.VerHistorico;
import br.com.appinbanker.inbanker.VerPagamentoPendente;
import br.com.appinbanker.inbanker.VerPedidoEnviado;
import br.com.appinbanker.inbanker.VerPedidoRecebido;
import br.com.appinbanker.inbanker.entidades.Transacao;
import br.com.appinbanker.inbanker.util.MyApplicationTaskOnTop;

/**
 * Created by Jonatas on 31/10/2016.
 */

public class MyGcmListenerService extends GcmListenerService {

    public void onMessageReceived(String from,Bundle data ){

        //verifica se tem alguma outra aplicaçao no topo da activity, se tiver nao abre notificaçao
        //if(!MyApplicationTaskOnTop.isMyApplicationTaskOnTop(this)){
        //    if(tipo.equals("global")){
        //        sendNotificationAppGlobal(data);
        //    }
       //}

        sendNotificationAppGlobal(data);
    }

    private void sendNotificationAppGlobal(final Bundle data){

        //MyAppAnalytics.getInstance().trackEvent("Notificacao", "gcm-"+id, "GCM Enviados");
        Log.i("Script", "num notificacao= "+data);
        Log.i("Script", "notificacao= "+data.getBundle("notification"));
        Log.i("Script", "notificacao= "+data.getBundle("notification").getString("body"));

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setTicker(data.getBundle("notification").getString("title"))
                .setSmallIcon(R.mipmap.icon)
                .setContentTitle(data.getBundle("notification").getString("title"))
                .setContentText(data.getBundle("notification").getString("body"))
                .setAutoCancel(true);

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText(data.getBundle("notification").getString("body"));
        builder.setStyle(bigText);

        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        //tempo vibrando, dormindo, vibrando, dormindo
        builder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        if(alarmSound == null){
            alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            if(alarmSound == null){
                alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            }
        }
        //Log.i("notification","sound = "+alarmSound);
        builder.setSound(alarmSound);
        //builder.setDefaults(Notification.DEFAULT_SOUND);

        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = data.getBundle("notification").getString("transacao");
        Transacao trans = new Transacao();
        try {
            //JSON from String to Object
            trans = mapper.readValue(jsonInString, Transacao.class);
        }catch(Exception e){
            Log.d("Exception", ""+e);
        }

        int status_transacao = Integer.parseInt(trans.getStatus_transacao());

        Log.i("Notificacao", "Status notificacao = "+ status_transacao);

        Class classe;

        switch (status_transacao){
            case Transacao.AGUARDANDO_RESPOSTA:
                classe = VerPedidoRecebido.class;
                break;
            case Transacao.PEDIDO_ACEITO:
                classe = VerPedidoEnviado.class;
                break;
            case Transacao.PEDIDO_RECUSADO:
                classe = VerHistorico.class;
                break;
            case Transacao.CONFIRMADO_RECEBIMENTO:
                classe = VerPedidoRecebido.class;
                break;
            case Transacao.QUITACAO_SOLICITADA:
                classe = VerPedidoRecebido.class;
                break;
            case Transacao.RESP_QUITACAO_SOLICITADA_RECUSADA:
                classe = VerPagamentoPendente.class;
                break;
            case Transacao.RESP_QUITACAO_SOLICITADA_CONFIRMADA:
                classe = VerHistorico.class;
                break;
            default:
                classe = NavigationDrawerActivity.class;
                break;
        }


        Intent it = new Intent(this,classe);
        Bundle b = new Bundle();
        b.putString("id",trans.getId_trans());
        b.putString("nome2",trans.getNome_usu2());
        b.putString("cpf1",trans.getUsu1());
        b.putString("cpf2",trans.getUsu2());
        b.putString("data_pedido",trans.getDataPedido());
        b.putString("nome1", trans.getNome_usu1());
        b.putString("valor",trans.getValor());
        b.putString("vencimento", trans.getVencimento());
        b.putString("img1", trans.getUrl_img_usu1());
        b.putString("img2", trans.getUrl_img_usu2());
        b.putString("status_transacao", trans.getStatus_transacao());

        if(trans.getData_recusada()!=null)
            b.putString("data_cancelamento", trans.getData_recusada());
        if(trans.getData_pagamento()!=null)
            b.putString("data_pagamento", trans.getData_pagamento());

        it.putExtras(b);
        PendingIntent pi = PendingIntent.getActivity(this,0,it,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pi);

        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(1, builder.build());
    }


}
