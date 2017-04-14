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

import br.com.appinbanker.inbanker.entidades.Usuario;
import br.com.appinbanker.inbanker.interfaces.WebServiceReturnStringAlteraEndereco;
import br.com.appinbanker.inbanker.interfaces.WebServiceReturnStringAlteraSenha;

/**
 * Created by jonatassilva on 22/10/16.
 */

public class EditaEndereco extends AsyncTask<String, String, String> {

    private Usuario usuario;
    private WebServiceReturnStringAlteraEndereco usu_return;

    public EditaEndereco(Usuario usuario, WebServiceReturnStringAlteraEndereco usu){
        this.usuario = usuario;
        this.usu_return = usu;

    }

    @Override
    protected String doInBackground(String... params) {

        try {

            String url = Host.host+"appinbanker/rest/usuario/editEnderecoByCPF/"+ usuario.getCpf();

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
        Log.i("Script","onPostExecute result edita endereco ="+result);

        usu_return.retornoStringWebServiceAlteraEndereco(result);
    }
}
