package br.com.appinbanker.inbanker.webservice;

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
 * Created by jonatasilva on 09/03/17.
 */

public class EnviaEmailEnvioPedido extends AsyncTask<String,String,String> {

    Transacao transacao;
    String email_user1;
    String email_user2;

    public EnviaEmailEnvioPedido(Transacao transacao, String email_user1, String email_user2){
        this.transacao = transacao;
        this.email_user1 = email_user1;
        this.email_user2 = email_user2;

    }

    @Override
    protected String doInBackground(String... params) {
        String retorno = null;
        String host = Host.host;
        try {
            final String url = host+"appinbanker/email/enviaEmail/envioPedido/"+email_user1+"/"+email_user2;

            // Set the Content-Type header
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.setContentType(new MediaType("application","json"));
            HttpEntity<Transacao> requestEntity = new HttpEntity<Transacao>(transacao, requestHeaders);

            // Create a new RestTemplate instance
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            //evita problema de io
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());

            // Make the HTTP POST request, marshaling the request to JSON, and the response to a String
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
            String result = responseEntity.getBody();

            //evita problema de io
            requestHeaders.set("Connection", "Close");

        } catch (Exception e) {
            Log.e("MainActivity", e.getMessage(), e);
        }
        return retorno;
    }
    @Override
    protected void onPostExecute(String result) {

        Log.i("Script","retorno email trans = "+result);
    }
}
