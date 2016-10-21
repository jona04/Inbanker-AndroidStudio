package br.com.appinbanker.inbanker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;

import br.com.appinbanker.inbanker.entidades.BancoControllerUsuario;

public class SplashScreen extends Activity {

    private Thread mSplashThread;
    private boolean mblnClicou = false;

    private Cursor cursor;
    private boolean usu_logado = false;

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

                        //verificamos se usuario ja esta logado antes de mostrar a tela de login
                        BancoControllerUsuario crud = new BancoControllerUsuario(getBaseContext());
                        cursor = crud.carregaDados();
                        if (cursor.getCount() > 0) {
                            usu_logado = true;
                        }
                        Log.i("BancoSQLite","resultado banco = "+ cursor.getCount());

                        mblnClicou = true;
                    }
                }
                catch(InterruptedException ex){
                }

                if (mblnClicou){

                    vaiInicio();

                }
            }
        };

        //chama função que dura 2 segundo antes de ir para inicio(menu principal)
        mSplashThread.start();

    }

    public void vaiInicio(){

        //verificamos se usuario ja esta logado antes de mostrar a tela de login
        if (usu_logado == true) {
            Intent it = new Intent(SplashScreen.this, NavigationDrawerActivity.class);
            startActivity(it);

            //encerra splash e evitar voltar
            finish();
        }else {

            //Carrega a Activity Principal
            Intent i = new Intent();
            i.setClass(SplashScreen.this, Inicio.class);
            startActivity(i);
            //encerra splash e evitar voltar
            finish();
        }
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
