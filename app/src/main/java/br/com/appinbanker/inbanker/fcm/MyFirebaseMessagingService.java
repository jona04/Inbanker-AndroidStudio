package br.com.appinbanker.inbanker.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import br.com.appinbanker.inbanker.NavigationDrawerActivity;
import br.com.appinbanker.inbanker.R;
import br.com.appinbanker.inbanker.VerHistorico;
import br.com.appinbanker.inbanker.VerPagamentoPendente;
import br.com.appinbanker.inbanker.VerPedidoEnviado;
import br.com.appinbanker.inbanker.VerPedidoRecebido;
import br.com.appinbanker.inbanker.entidades.Transacao;
import br.com.appinbanker.inbanker.util.AllSharedPreferences;

/**
 * Created by jonatassilva on 08/12/16.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            ObjectMapper mapper = new ObjectMapper();
            String jsonInString = remoteMessage.getData().get("transacao");
            Transacao trans = new Transacao();
            try {
                //JSON from String to Object
                trans = mapper.readValue(jsonInString, Transacao.class);

                String id_face = AllSharedPreferences.getPreferences(AllSharedPreferences.ID_FACE, getApplication());
                String cpf = AllSharedPreferences.getPreferences(AllSharedPreferences.CPF, getApplication());
                if (id_face != null || cpf != null) {
                    if (id_face != "" || cpf != "")
                        sendNotification(trans, remoteMessage.getData().get("title"), remoteMessage.getData().get("msg"));

                }
            }catch (Exception e){
                Log.i("Notificatio","Excepition = "+e);
            }
        }
        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        //Calling method to generate notification
        //sendNotification(remoteMessage.getNotification().getBody());
    }

    //This method is only generating push notification
    //It is same as we did in earlier posts
    private void sendNotification(Transacao trans,String title,String msg) {

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.icon)
                .setContentTitle(title)
                .setContentText(msg)
                .setAutoCancel(true)
                .setSound(defaultSoundUri);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText(msg);
        notificationBuilder.setStyle(bigText);

        notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        notificationBuilder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        //tempo vibrando, dormindo, vibrando, dormindo
        notificationBuilder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});

        int status_transacao = Integer.parseInt(trans.getStatus_transacao());

        Class classe;

        switch (status_transacao){
            case Transacao.AGUARDANDO_RESPOSTA:
                Log.i("Script","Notifca aguardando esposta");
                classe = VerPedidoRecebido.class;
                break;
            case Transacao.PEDIDO_ACEITO:
                Log.i("Script","Notifca aguardando esposta");
                classe = VerPedidoEnviado.class;
                break;
            case Transacao.PEDIDO_RECUSADO:
                Log.i("Script","Notifca PEDIDO_RECUSADO");
                classe = VerHistorico.class;
                break;
            case Transacao.CONFIRMADO_RECEBIMENTO:
                Log.i("Script","Notifca CONFIRMADO_RECEBIMENTO");
                classe = VerPedidoRecebido.class;
                break;
            case Transacao.QUITACAO_SOLICITADA:
                Log.i("Script","Notifca QUITACAO_SOLICITADA");
                classe = VerPedidoRecebido.class;
                break;
            case Transacao.RESP_QUITACAO_SOLICITADA_RECUSADA:
                Log.i("Script","Notifca RESP_QUITACAO_SOLICITADA_RECUSADA");
                classe = VerPagamentoPendente.class;
                break;
            case Transacao.RESP_QUITACAO_SOLICITADA_CONFIRMADA:
                Log.i("Script","Notifca RESP_QUITACAO_SOLICITADA_CONFIRMADA");
                classe = VerHistorico.class;
                break;
            default:
                Log.i("Notificacao", "default notificacao");
                classe = NavigationDrawerActivity.class;
                break;
        }

        Intent it = new Intent(this,classe);
        it.putExtra("transacao",trans);

        it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, it,
                PendingIntent.FLAG_ONE_SHOT);

        notificationBuilder.setContentIntent(pendingIntent);

        notificationManager.notify(0, notificationBuilder.build());


    }
}
