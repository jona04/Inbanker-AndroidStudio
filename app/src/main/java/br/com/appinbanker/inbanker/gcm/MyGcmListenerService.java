package br.com.appinbanker.inbanker.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import br.com.appinbanker.inbanker.NavigationDrawerActivity;
import br.com.appinbanker.inbanker.R;
import br.com.appinbanker.inbanker.util.MyApplicationTaskOnTop;

/**
 * Created by Jonatas on 31/10/2016.
 */

public class MyGcmListenerService extends GcmListenerService {

    public void onMessageReceived(String from,Bundle data ){
        String tipo = data.getString("tipo");

        Log.i("Script","tipo gcm = "+tipo);

        //verifica se tem alguma outra aplicaçao no topo da activity, se tiver nao abre notificaçao
        if(!MyApplicationTaskOnTop.isMyApplicationTaskOnTop(this)){
            if(tipo.equals("global")){
                sendNotificationAppGlobal(data);
            }
        }
    }

    private void sendNotificationAppGlobal(final Bundle data){

        //id da notificacao, cada notificacao sera um id diferente
        //usamos o id para contar no analitcs quantas das msg enviadas foram abertas
        int id = Integer.parseInt(data.getString("gcm_num"));

        //MyAppAnalytics.getInstance().trackEvent("Notificacao", "gcm-"+id, "GCM Enviados");
        Log.i("Script", "num notifica = " + id);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setTicker(data.getString("title"))
                .setSmallIcon(R.mipmap.icon)
                .setContentTitle(data.getString("title"))
                .setContentText(data.getString("msg"))
                .setAutoCancel(true);

        Intent it = new Intent(this, NavigationDrawerActivity.class);
        //Bundle b = new Bundle();
        //b.putString("notification","1");
        //b.putInt("num_gcm", id);
        //it.putExtras(b);
        PendingIntent pi = PendingIntent.getActivity(this,0,it,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pi);

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText(data.getString("msg"));
        builder.setStyle(bigText);

        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        //tempo vibrando, dormindo, vibrando, dormindo
        builder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});

        Uri uri = RingtoneManager.getDefaultUri( RingtoneManager.TYPE_NOTIFICATION );
        builder.setSound(uri);

        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(id, builder.build());
    }


}
