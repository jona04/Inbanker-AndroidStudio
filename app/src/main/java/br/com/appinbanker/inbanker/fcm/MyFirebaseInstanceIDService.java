package br.com.appinbanker.inbanker.fcm;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import br.com.appinbanker.inbanker.util.AllSharedPreferences;

/**
 * Created by jonatassilva on 08/12/16.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService{

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("FCM", "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String token) {

        AllSharedPreferences.putPreferences(AllSharedPreferences.TOKEN_GCM,token,MyFirebaseInstanceIDService.this);

        String deviceId = getDeviceId(this);

        Log.i("registrationId","device id ="+deviceId);

        AllSharedPreferences.putPreferences(AllSharedPreferences.DEVICE_ID,deviceId,MyFirebaseInstanceIDService.this);

    }

    //serve para pegar o id unico do aparelho e armazenar no banco, para podermos bloquear algum aparelho caso queiramos
    public static String getDeviceId(Context context) {
        final String deviceId = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        if (deviceId != null) {
            return deviceId;
        } else {
            return android.os.Build.SERIAL;
        }
    }
}
