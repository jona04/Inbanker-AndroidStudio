package br.com.appinbanker.inbanker.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import br.com.appinbanker.inbanker.NavigationDrawerActivity;
import br.com.appinbanker.inbanker.R;
import br.com.appinbanker.inbanker.entidades.Transacao;
import br.com.appinbanker.inbanker.sqlite.BancoControllerUsuario;
import br.com.appinbanker.inbanker.sqlite.CriandoBanco;

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


            try {
                ObjectMapper mapper = new ObjectMapper();
                String jsonInString = remoteMessage.getData().get("transacao");
                Transacao trans = new Transacao();

                //JSON from String to Object
                trans = mapper.readValue(jsonInString, Transacao.class);

                //faz a verificacao para saber se existe cpf - sabendo disso saberemos se o usuario esta online ou nao, para receber ou nao a notificacao
                BancoControllerUsuario crud = new BancoControllerUsuario(this);
                Cursor cursor = crud.carregaDados();
                String cpf = cursor.getString(cursor.getColumnIndexOrThrow(CriandoBanco.CPF));
                if (cpf != null) {
                    if (cpf != "")
                        if(remoteMessage.getData().get("tipo").equals("notificacao")) {
                            sendNotification(trans, remoteMessage.getData().get("title"), remoteMessage.getData().get("msg"));
                        }else{
                            Log.i("Notificatio","Notificacao de divida");
                        }

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

        Bitmap rawBitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.logo);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.logo)
                .setLargeIcon(rawBitmap)
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

        int menu_item;

        switch (status_transacao){
            case Transacao.AGUARDANDO_RESPOSTA:
                menu_item = NavigationDrawerActivity.MENU_PEDIDOS_RECEBIDOS;
                break;
            case Transacao.PEDIDO_ACEITO:
                menu_item = NavigationDrawerActivity.MENU_INICIO;
                break;
            case Transacao.PEDIDO_RECUSADO:
                menu_item = NavigationDrawerActivity.MENU_HISTORICO;
                break;
            case Transacao.CONFIRMADO_RECEBIMENTO:
                menu_item = NavigationDrawerActivity.MENU_PAGAMENTOS_ABERTO;
                break;
            case Transacao.QUITACAO_SOLICITADA:
                menu_item = NavigationDrawerActivity.MENU_INICIO;
                break;
            case Transacao.RESP_QUITACAO_SOLICITADA_RECUSADA:
                menu_item = NavigationDrawerActivity.MENU_PAGAMENTOS_ABERTO;
                break;
            case Transacao.RESP_QUITACAO_SOLICITADA_CONFIRMADA:
                menu_item = NavigationDrawerActivity.MENU_HISTORICO;
                break;
            default:
                menu_item = NavigationDrawerActivity.MENU_INICIO;
                break;
        }

        Log.i("Notification Menu","Menu item = "+menu_item);

        Intent it = new Intent(this,NavigationDrawerActivity.class);
        it.putExtra("menu_item",menu_item);

        it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, it,
                PendingIntent.FLAG_ONE_SHOT);

        notificationBuilder.setContentIntent(pendingIntent);

        notificationManager.notify(0, notificationBuilder.build());


    }
}
