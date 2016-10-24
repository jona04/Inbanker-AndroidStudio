package br.com.appinbanker.inbanker.webservice;

import android.content.Context;
import android.net.http.HttpResponseCache;
import android.os.AsyncTask;
import android.util.Log;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

import br.com.appinbanker.inbanker.entidades.Usuario;
import br.com.appinbanker.inbanker.fragments_navigation.PedirEmprestimoFragment;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;

/**
 * Created by jonatassilva on 22/10/16.
 */

public class AtualizaUsuario extends AsyncTask<String, String, String> {

    private Usuario usuario;
    private PedirEmprestimoFragment pef;

    public AtualizaUsuario(Usuario usu,PedirEmprestimoFragment pef){
        this.usuario = usu;
        this.pef = pef;

    }

    @Override
    protected String doInBackground(String... params) {

        try {

            //final String url = "http://45.55.217.160:8081/appinbanker/rest/usuario/edit/"+ usuario.getCpf();
            final String url = "http://10.0.3.2:8080/appinbanker/rest/usuario/edit/" + usuario.getCpf();

            Log.i("WebService", "url atualiza = " + url);
            Log.i("webservice", "id name e senha" + usuario.getIdFace() + " - " + usuario.getNomeFace() + " - " + usuario.getUrlImgFace());

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
        /*try {

            final String url = "http://10.0.3.2:8080/appinbanker/rest/usuario/edit/"+ usuario.getCpf();

            ObjectMapper mapper = new ObjectMapper();


            // Convert object to JSON string
            String jsonInString = mapper.writeValueAsString(usuario);
            Log.w("Webservie", "jsonInString = "+jsonInString);
            // Convert object to JSON string and pretty print
            jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(usuario);
            Log.w("Webservice","jsonInString = "+ jsonInString);


            HttpResponse response;

            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(new StringEntity(jsonInString, "UTF-8"));
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("Accept-Encoding", "application/json");
            httpPost.setHeader("Accept-Language", "pt-BR");
            response = httpClient.execute(httpPost);
            String sresponse = response.getEntity().toString();
            Log.w("QueingSystem 1 =", sresponse);
            Log.w("QueingSystem 2 =", EntityUtils.toString(response.getEntity()));
        }
         catch (JsonGenerationException e) {
            Log.d("JsonGenerationException", ""+e);
        } catch (JsonMappingException e) {
            Log.d("JsonMappingException", ""+e);
        } catch (IOException e) {
            Log.d("IOException", ""+e);
        }catch(Exception e){
            Log.d("Exception", ""+e);
        }

            return null;
        }*/

    @Override
    protected void onPostExecute(String result) {
        Log.i("Script","onPostExecute result atualiza usuario ="+result);

        pef.retornoAtualizaUsuario(result);

    }
}
