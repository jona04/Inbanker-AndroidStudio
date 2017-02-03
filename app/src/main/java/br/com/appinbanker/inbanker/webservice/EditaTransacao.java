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

import br.com.appinbanker.inbanker.entidades.Transacao;
import br.com.appinbanker.inbanker.interfaces.WebServiceReturnString;

/**
 * Created by jonatassilva on 25/10/16.
 */

public class EditaTransacao extends AsyncTask<String, String, String> {

    private Transacao trans;
    private WebServiceReturnString ws;
    private String cpf_user2,cpf_user1;

    public EditaTransacao(Transacao trans, String cpf_user1, String cpf_user2,WebServiceReturnString ws){
        this.cpf_user2 = cpf_user2;
        this.cpf_user1 = cpf_user1;
        this.trans = trans;
        this.ws = ws;

    }

    @Override
    protected String doInBackground(String... params) {

        String host = Host.host;

        try {

            //final String url = "http://45.55.217.160:8081/appinbanker/rest/usuario/edit/"+ usuario.getCpf();
            final String url = host+"appinbanker/rest/usuario/editTransacao/" + cpf_user1+"/"+cpf_user2;

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
        Log.i("Script","onPostExecute result edita trans ="+result);

        ws.retornoStringWebService(result);
    }
}
