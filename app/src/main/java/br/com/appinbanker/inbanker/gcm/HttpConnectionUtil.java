package br.com.appinbanker.inbanker.gcm;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import cz.msebera.android.httpclient.util.EntityUtils;

/**
 * Created by Jonatas on 31/10/2016.
 */

public class HttpConnectionUtil {
    public static String sendRegistrationIdToBackend(String regId,String deviceId,Context context){
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost("http://www.axei.net.br/WebService/gcm_ids.php");
        //HttpPost httpPost = new HttpPost("http://127.0.0.1/axeithe/WebService/TrabalhoRPC.php");
        String answer = "";

        try{
            ArrayList<NameValuePair> valores = new ArrayList<NameValuePair>();


            valores.add(new BasicNameValuePair("gcm-registro", "save-gcm-registration-id"));
            valores.add(new BasicNameValuePair("regid", regId));
            valores.add(new BasicNameValuePair("deviceId", deviceId));

            httpPost.setEntity(new UrlEncodedFormEntity(valores));
            HttpResponse resposta = httpClient.execute(httpPost);
            answer = EntityUtils.toString(resposta.getEntity());
            Log.i("Script", "Resposta registro gcm = " + answer);

            if(answer.equals("ok")){
                Log.i("Script", "acao de true para preferencia aqui!!");

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                preferences.edit().putBoolean("gcm_status_verify",true).apply();

            }

        }
        catch(NumberFormatException e){ e.printStackTrace(); }
        catch(NullPointerException e){ e.printStackTrace(); }
        catch(ClientProtocolException e){ e.printStackTrace(); }
        catch(IOException e){ e.printStackTrace(); }
        return(answer);
    }

}
