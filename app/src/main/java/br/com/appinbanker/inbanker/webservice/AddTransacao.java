package br.com.appinbanker.inbanker.webservice;

import android.os.AsyncTask;
import android.util.Log;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import br.com.appinbanker.inbanker.SimuladorResultado;
import br.com.appinbanker.inbanker.entidades.Transacao;
import br.com.appinbanker.inbanker.entidades.Usuario;
import br.com.appinbanker.inbanker.fragments_navigation.PedirEmprestimoFragment;

/**
 * Created by jonatassilva on 23/10/16.
 */

public class AddTransacao extends AsyncTask<String, String, String> {

    private Transacao trans;
    private SimuladorResultado sr;

    public AddTransacao(Transacao trans,SimuladorResultado sr){
        this.trans = trans;
        this.sr = sr;

    }

    @Override
    protected String doInBackground(String... params) {

        try {

            //final String url = "http://10.0.3.2:8080/appinbanker/rest/usuario/addTransacao/" + trans.getUsu1()+"/"+trans.getUsu2();
            final String url = "http://45.55.217.160:8081/appinbanker/rest/usuario/addTransacao/" + trans.getUsu1()+"/"+trans.getUsu2();

            // Set the Content-Type header
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.setContentType(new MediaType("application", "json"));
            HttpEntity<Transacao> requestEntity = new HttpEntity<Transacao>(trans, requestHeaders);

            // Create a new RestTemplate instance
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());

            // Make the HTTP POST request, marshaling the request to JSON, and the response to a String
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
            String result = responseEntity.getBody();

            requestHeaders.set("Connection", "Close");

            return result;
        } catch (Exception e) {
            Log.e("WebService", e.getMessage(), e);
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        Log.i("Script","onPostExecute result add trans ="+result);

        sr.retornoAddTransacao(result);

    }

}
