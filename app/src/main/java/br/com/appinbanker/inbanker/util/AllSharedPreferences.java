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

    public static final String VERIFY_TUTORIAL_INICIO = "verify_tutorial_inicio";
    public static final String VERIFY_TUTORIAL_MENSAGEM = "verify_tutorial_mensagem";
    public static final String VERIFY_TUTORIAL_NOTIFICACOES = "verify_tutorial_notificacoes";
    public static final String VERIFY_TUTORIAL_PEDIR_LOGAR_FACE = "verify_tutorial_pedir_logar_face";
    public static final String VERIFY_TUTORIAL_PEDIR_LISTA_AMIGOS = "verify_tutorial_pedir_lista_amigos";
    public static final String VERIFY_TUTORIAL_PAGAMENTO = "verify_tutorial_pagamento";
    public static final String VERIFY_TUTORIAL_HISTORICO = "verify_tutorial_historico";
    public static final String VERIFY_TUTORIAL_SIMULADOR = "verify_tutorial_simulador";

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

    public static void putPreferencesBooleanTrue(String name, Context classe){

        try {
            SharedPreferences pref_local = classe.getSharedPreferences(PREFS_LOCAL, Context.MODE_PRIVATE);
            pref_local.edit().putBoolean(name, true).apply();
        }catch (Exception e){
            Log.i("Excpetion","Erro putPreferences ="+e);
        }
    }

    public static void putPreferencesBooleanFalse(String name, Context classe){

        try {
            SharedPreferences pref_local = classe.getSharedPreferences(PREFS_LOCAL, Context.MODE_PRIVATE);
            pref_local.edit().putBoolean(name, false).apply();
        }catch (Exception e){
            Log.i("Excpetion","Erro putPreferences ="+e);
        }
    }

    public static boolean getPreferencesBoolean(String name, Context classe){

        try {
            SharedPreferences pref_local = classe.getSharedPreferences(PREFS_LOCAL, Context.MODE_PRIVATE);
            return pref_local.getBoolean(name,false);
        }catch (Exception e){
            Log.i("Excpetion","Erro putPreferences ="+e);
            return false;
        }
    }


}
