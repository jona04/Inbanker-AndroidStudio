package br.com.appinbanker.inbanker.gcm;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

import br.com.appinbanker.inbanker.util.AllSharedPreferences;

/**
 * Created by Jonatas on 31/10/2016.
 */

public class RegistrationIntentService extends IntentService {

    public static final String LOG = "LOG";
    private static final String SENDER_ID = "198802367921";

    public RegistrationIntentService(){
        super(LOG);
    }

    @Override
    protected void onHandleIntent( Intent intent) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean status = preferences.getBoolean("gcm_status_verify", false);



        synchronized (LOG){
            InstanceID instanceID = InstanceID.getInstance( this );
            try {
                if(!status) {
                    String token = instanceID.getToken(SENDER_ID,
                            GoogleCloudMessaging.INSTANCE_ID_SCOPE,
                            null);
                    Log.i("Script", "Token:" + token);


                   //preferences.edit().putBoolean("gcm_status_verify",token != null && token.trim().length() > 0).apply();

                    sendRegistrationId(token);
                }else{
                    Log.i("Script", "Token ja resgistrado");
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendRegistrationId( String token){

        String deviceId = getDeviceId(this);

        Log.i("registrationId","device id ="+deviceId);

        AllSharedPreferences.putPreferences(AllSharedPreferences.DEVICE_ID,deviceId,RegistrationIntentService.this);
        AllSharedPreferences.putPreferences(AllSharedPreferences.TOKEN_GCM,token,RegistrationIntentService.this);

        //HttpConnectionUtil.sendRegistrationIdToBackend(token,deviceId,this);
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
