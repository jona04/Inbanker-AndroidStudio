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

import br.com.appinbanker.inbanker.entidades.Usuario;
import br.com.appinbanker.inbanker.interfaces.WebServiceReturnString;
import br.com.appinbanker.inbanker.interfaces.WebServiceReturnStringFace;

/**
 * Created by jonatassilva on 18/10/16.
 */

public class AddUsuario extends AsyncTask<String, String, String> {

    private Usuario usuario;
    private Context context;
    private WebServiceReturnString rs;

    public AddUsuario(Usuario usu, Context context, WebServiceReturnString rs) {
        this.usuario = usu;
        this.rs = rs;
        this.context = context;
    }

    @Override
    protected String doInBackground(String... params) {

        try {

            final String url = Host.host+"appinbanker/rest/usuario/add";

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

        rs.retornoStringWebService(result);
    }


}
