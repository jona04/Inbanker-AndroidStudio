package br.com.appinbanker.inbanker.util;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Jonatas on 01/11/2016.
 */

public class CheckConection extends Activity {

    // Se precisar desse método pra mais de uma classe, mude ele pra ser estático.
    public static boolean temConexao(Context classe) {
        //Pego a conectividade do contexto passado como argumento
        ConnectivityManager gerenciador = (ConnectivityManager) classe.getSystemService(Context.CONNECTIVITY_SERVICE);
        //Crio a variável informacao que recebe as informações da Rede
        NetworkInfo informacao = gerenciador.getActiveNetworkInfo();
        //Se o objeto for nulo ou nao tem conectividade retorna false
        if ((informacao != null) && (informacao.isConnectedOrConnecting()) && (informacao.isAvailable())) {
            return true;
        }
        return false;
    }



}
