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

import br.com.appinbanker.inbanker.CadastroUsuario;
import br.com.appinbanker.inbanker.entidades.Usuario;

/**
 * Created by jonatassilva on 18/10/16.
 */

public class AddUsuario extends AsyncTask<String, String, String> {

    private Usuario usuario;
    private CadastroUsuario cu;

    public AddUsuario(Usuario usu, CadastroUsuario cu) {
        this.usuario = usu;
        this.cu = cu;
    }

    @Override
    protected String doInBackground(String... params) {

        try {

            //final String url = "http://45.55.217.160:8081/appinbanker/rest/usuario/add";
            final String url = "http://10.0.3.2:8080/appinbanker/rest/usuario/add";

            // Set the Content-Type header
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.setContentType(new MediaType("application","json"));
            HttpEntity<Usuario> requestEntity = new HttpEntity<Usuario>(usuario, requestHeaders);

            // Create a new RestTemplate instance
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            //evita problema de io
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());

            // Make the HTTP POST request, marshaling the request to JSON, and the response to a String
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.PUT, requestEntity, String.class);
            String result = responseEntity.getBody();

            //evita problema de io
            requestHeaders.set("Connection", "Close");

            return result;
        } catch (Exception e) {
            Log.e("WebService", e.getMessage(), e);
        }

        return null ;
    }
    @Override
    protected void onPostExecute(String result) {
        Log.i("Script","resultado = "+result);

        cu.retornoTask(result);

    }
}
