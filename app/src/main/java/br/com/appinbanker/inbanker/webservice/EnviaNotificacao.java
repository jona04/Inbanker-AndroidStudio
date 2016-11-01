package br.com.appinbanker.inbanker.webservice;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import br.com.appinbanker.inbanker.entidades.Transacao;


/**
 * Created by Jonatas on 01/11/2016.
 */

public class EnviaNotificacao extends AsyncTask<String,String,String> {

    private String token;
    private Transacao trans;

    public EnviaNotificacao(Transacao trans, String token){
        this.token = token;
        this.trans = trans;

    }
    @Override
    protected String doInBackground(String... params) {
        String host = Host.host;
        String result = null;
        try {
            //final String url = "http://45.55.217.160:8081/appinbanker/rest/usuario/findEmail/"+email;
            final String url = host+"appinbanker/gcm/notification/sendNotification/"+token;

            // Set the Content-Type header
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.setContentType(new MediaType("application", "json"));
            HttpEntity<Transacao> requestEntity = new HttpEntity<Transacao>(trans, requestHeaders);

            // Create a new RestTemplate instance
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());

            // Make the HTTP POST request, marshaling the request to JSON, and the response to a String
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
            result = responseEntity.getBody();

            requestHeaders.set("Connection", "Close");

        } catch (Exception e) {
            Log.e("MainActivity", e.getMessage(), e);
        }
        return result;
    }
    @Override
    protected void onPostExecute(String result) {

        Log.i("Script","result notification = "+result);

        //tl.retornoTask(result);

    }

}
