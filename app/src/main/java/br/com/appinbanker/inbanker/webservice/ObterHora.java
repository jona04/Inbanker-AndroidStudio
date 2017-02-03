package br.com.appinbanker.inbanker.webservice;

import android.os.AsyncTask;
import android.util.Log;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import br.com.appinbanker.inbanker.interfaces.WebServiceReturnStringHora;

/**
 * Created by jonatasilva on 31/01/17.
 */

public class ObterHora extends AsyncTask<String, String, String> {

    WebServiceReturnStringHora ws;

    public ObterHora(WebServiceReturnStringHora ws){
        this.ws = ws;

    }

    @Override
    protected String doInBackground(String... params) {
        String hora = null;

        String host = Host.host;
        try {
            final String url = host+"appinbanker/rest/usuario/obterHora";

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            hora = restTemplate.getForObject(url, String.class);

        } catch (Exception e) {
            Log.e("MainActivity", e.getMessage(), e);
        }
        return hora;
    }
    @Override
    protected void onPostExecute(String result) {

        Log.i("Script",""+result);
        ws.retornoObterHora(result);
    }
}
