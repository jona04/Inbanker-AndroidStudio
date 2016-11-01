package br.com.appinbanker.inbanker.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Jonatas on 01/11/2016.
 */

public class AllSharedPreferences extends Activity {

    public static final String PREFS_LOCAL = "prefs_local";
    public static final String DEVICE_ID = "device_id";
    public static final String TOKEN_GCM = "token_gcm";

    public static void putPreferences(String name, String item, Context classe){

        SharedPreferences pref_local = classe.getSharedPreferences(PREFS_LOCAL, 0);
        pref_local.edit().putString(name, item).apply();

    }

    public static String getPreferences(String name,Context classe){

        SharedPreferences pref_local = classe.getSharedPreferences(PREFS_LOCAL, 0);
        return pref_local.getString(name,"");

    }

}
