package br.com.appinbanker.inbanker.webservice;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import br.com.appinbanker.inbanker.entidades.Usuario;
import br.com.appinbanker.inbanker.interfaces.WebServiceReturnUsuario;

/**
 * Created by jonatassilva on 24/10/16.
 */

public class BuscaUsuarioCPF extends AsyncTask<String,String,Usuario> {


    private Context context;
    private WebServiceReturnUsuario ti;

    private String cpf;

    public BuscaUsuarioCPF(String cpf, Context context, WebServiceReturnUsuario ti){
        this.cpf = cpf;
        this.context = context;
        this.ti = ti;
    }

    @Override
    protected Usuario doInBackground(String... params) {
        Usuario usu = null;

        String host = Host.host;
        try {

            String url = host+"appinbanker/rest/usuario/findUsuarioCpf/"+cpf;

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            usu = restTemplate.getForObject(url, Usuario.class);

        } catch (Exception e) {
            Log.e("MainActivity", e.getMessage(), e);
        }
        return usu;
    }
    @Override
    protected void onPostExecute(Usuario result) {

        Log.w("webservice", "resesult busca usuario cpf = "+result);

        ti.retornoUsuarioWebService(result);
    }

}
