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
import br.com.appinbanker.inbanker.entidades.Usuario;
import br.com.appinbanker.inbanker.interfaces.WebServiceReturnString;

/**
 * Created by jonatassilva on 25/10/16.
 */

public class AddCartaoUsuario extends AsyncTask<String, String, String> {

    private Usuario usuario;
    //private WebServiceReturnString ws;

    public AddCartaoUsuario(Usuario usuario){
        this.usuario = usuario;
        //this.ws = ws;
    }

    @Override
    protected String doInBackground(String... params) {

        String host = Host.host;

        try {

            //final String url = "http://45.55.217.160:8081/appinbanker/rest/usuario/edit/"+ usuario.getCpf();
            final String url = host+"appinbanker/rest/usuario/addCartaoUsuario/" + usuario.getCpf();

            // Set the Content-Type header
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.setContentType(new MediaType("application", "json"));
            HttpEntity<Usuario> requestEntity = new HttpEntity<Usuario>(usuario, requestHeaders);

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
        Log.i("Script","onPostExecute result add cartao usuario ="+result);

        //ws.retornoStringWebService(result);
    }
}
