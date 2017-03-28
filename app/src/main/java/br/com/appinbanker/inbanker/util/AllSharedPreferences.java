package br.com.appinbanker.inbanker.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by Jonatas on 01/11/2016.
 */

public class AllSharedPreferences extends Activity {


    public static final String PREFS_LOCAL = "prefs_local";
    public static final String DEVICE_ID = "device_id";
    public static final String TOKEN_GCM = "token_gcm";
    public static final String COUNT_NOTIFY_CARTA = "count_notify_carta";
    public static final String COUNT_NOTIFY_CARTA_AUX = "count_notify_carta_aux";
    public static final String VERIFY_NOTIFY_CARTA = "verify_notify_carta";
    //public static final String ID_FACE = "id_face";
    //public static final String CPF = "cpf";

    public static void putPreferences(String name, String item, Context classe){

        try {
            SharedPreferences pref_local = classe.getSharedPreferences(PREFS_LOCAL, Context.MODE_PRIVATE);
            pref_local.edit().putString(name, item).apply();
        }catch (Exception e){
            Log.i("Excpetion","Erro putPreferences ="+e);
        }
    }

    public static String getPreferences(String name,Context classe){

        try {
            SharedPreferences pref_local = classe.getSharedPreferences(PREFS_LOCAL, Context.MODE_PRIVATE);
            return pref_local.getString(name,"");
        }catch (Exception e){
            Log.i("Excpetion","Erro putPreferences ="+e);
            return "";
        }
    }


}
