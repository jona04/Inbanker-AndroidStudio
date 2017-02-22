package br.com.appinbanker.inbanker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;

import br.com.appinbanker.inbanker.sqlite.BancoControllerUsuario;

public class SplashScreen extends Activity {

    private Thread mSplashThread;
    private boolean mblnClicou = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_splash_screen);

        //thread que dura 2 segundo para mostrar a tela de Splash
        mSplashThread = new Thread() {
            @Override
            public void run() {
                try {
                    synchronized(this){
                        //Espera por 5 segundos or sai quando
                        //o usuário tocar na tela
                        wait(2000);

                        Log.i("Splash","2 segundo");

                        mblnClicou = true;
                    }
                }
                catch(InterruptedException ex){
                }

                if (mblnClicou){

                    Log.i("Splash","vai inicio");

                    vaiInicio();

                }
            }
        };


        //verificamos se usuario ja esta logado antes de mostrar a tela de login
        BancoControllerUsuario crud = new BancoControllerUsuario(getBaseContext());
        Cursor cursor = crud.carregaDados();
        if (cursor.getCount() > 0) {

            Intent it = new Intent(SplashScreen.this, NavigationDrawerActivity.class);
            startActivity(it);

            //encerra splash e evitar voltar
            finish();

        }else{
            Log.i("Splash","chama splash");
            //chama função que dura 2 segundo antes de ir para inicio(menu principal)
            //mSplashThread.start();
            vaiInicio();
        }

    }

    public void vaiInicio(){

        Log.i("Splash","vai inicio 2");

        Intent it = new Intent(SplashScreen.this, SlideInicial.class);
        startActivity(it);

        //encerra splash e evitar voltar
        finish();
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

    @Override
    public void onResume() {
        super.onResume();
        Log.i("Script", "onresume splash");

        //captura as visualizacoes decorrentes desse activity, e manda pro analitcs com o nome
        //MyAppAnalytics.getInstance().trackScreenView("Tela Splash Scren");

    }
}
