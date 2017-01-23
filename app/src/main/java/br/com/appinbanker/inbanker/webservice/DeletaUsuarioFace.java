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
import br.com.appinbanker.inbanker.interfaces.WebServiceReturnStringFace;
import br.com.appinbanker.inbanker.interfaces.WebServiceReturnUsuarioFace;

/**
 * Created by jonatassilva on 23/10/16.
 */

public class DeletaUsuarioFace extends AsyncTask<String,String,String> {

    private Usuario usu;
    private Context context;

    private WebServiceReturnStringFace ru;


    public DeletaUsuarioFace(Usuario usu, Context context, WebServiceReturnStringFace ru){

        this.context = context;
        this.ru = ru;
        this.usu = usu;

    }

    @Override
    protected String doInBackground(String... params) {
        String host = Host.host;
        String result = null;
        try {

            final String url = host+"appinbanker/rest/usuario/deletaUsuFace/"+usu.getId_face();

            // Set the Content-Type header
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.setContentType(new MediaType("application", "json"));
            HttpEntity<Usuario> requestEntity = new HttpEntity<Usuario>(usu, requestHeaders);

            // Create a new RestTemplate instance
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());

            // Make the HTTP POST request, marshaling the request to JSON, and the response to a String
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
            result = responseEntity.getBody();

            requestHeaders.set("Connection", "Close");


        } catch (Exception e) {
            Log.e("MainActivity", e.getMessage(), e);
        }
        return result;
    }

    @Override
    protected void onPostExecute(String result) {

        ru.retornoStringWebServiceFace(result);

    }

}
