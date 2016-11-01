package br.com.appinbanker.inbanker.util;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

/**
 * Created by Jonatas on 31/10/2016.
 */

public class CheckPlayServices extends Activity {

   //utilizado na funcao de saber se o google play service esta instalado
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    //para saber se tem o google play service instalado
    public static boolean checkPlayServices(Context classe) {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(classe);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, (Activity) classe,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i("LOG", "This device is not supported.");
                //finish();
            }
            return false;
        }
        return true;
    }
}
