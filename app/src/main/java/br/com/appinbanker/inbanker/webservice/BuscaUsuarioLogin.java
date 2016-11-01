package br.com.appinbanker.inbanker.webservice;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import br.com.appinbanker.inbanker.TelaLogin;
import br.com.appinbanker.inbanker.entidades.Usuario;

/**
 * Created by jonatassilva on 18/10/16.
 */

public class BuscaUsuarioLogin extends AsyncTask<String,String,Usuario> {

    private Context context;
    private TelaLogin tl;
    private String email;

    public BuscaUsuarioLogin(String email, Context context, TelaLogin tl){
        this.context = context;
        this.tl = tl;
        this.email = email;

    }
    @Override
    protected Usuario doInBackground(String... params) {
        Usuario usu = null;
        String host = Host.host;
        try {
            //final String url = "http://45.55.217.160:8081/appinbanker/rest/usuario/findEmail/"+email;
            final String url = host+"appinbanker/rest/usuario/findEmail/"+email;

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

        if(result!= null)
            Log.i("Script",result.getNome());

        tl.retornoTask(result);

    }
}
