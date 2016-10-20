package br.com.appinbanker.inbanker.webservice;

import android.os.AsyncTask;
import android.util.Log;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import br.com.appinbanker.inbanker.entidades.Usuario;

/**
 * Created by Jonatas on 18/10/2016.
 */

public class teste extends AsyncTask<String, String, Usuario> {
    @Override
    protected Usuario doInBackground(String... params) {

        try {
            final String url = "http://45.55.217.160:8081/appinbanker/rest/usuario/find/teste@teste";
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            Usuario usu = restTemplate.getForObject(url, Usuario.class);
            return usu;
        } catch (Exception e) {
            Log.e("MainActivity", e.getMessage(), e);
        }

        return null;
    }
    @Override
    protected void onPostExecute(Usuario result) {
        Log.i("Script",result.getNome());
    }
}
