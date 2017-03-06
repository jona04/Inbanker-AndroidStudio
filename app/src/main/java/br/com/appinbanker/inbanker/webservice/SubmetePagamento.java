package br.com.appinbanker.inbanker.webservice;

import android.os.AsyncTask;
import android.util.Log;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;


import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

import br.com.appinbanker.inbanker.TelaPagamento;
import br.com.appinbanker.inbanker.entidades.CriarPagamento;
import br.com.appinbanker.inbanker.entidades.RetornoPagamento;
import br.com.appinbanker.inbanker.entidades.Usuario;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;

/**
 * Created by jonatassilva on 22/10/16.
 */

public class SubmetePagamento extends AsyncTask<String, String, String> {

    private TelaPagamento tp;
    private CriarPagamento pagamento;

    public SubmetePagamento(TelaPagamento tp,CriarPagamento pagamento){
        this.tp = tp;
        this.pagamento = pagamento;
    }

    @Override
    protected String doInBackground(String... params) {


        String url = "http://45.55.217.160:81/systemRecorrency_1_01/requisicao1/requisicao1.php";


        try {
            // Set the Content-Type header
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.setContentType(new MediaType("application", "json"));
            HttpEntity<CriarPagamento> requestEntity = new HttpEntity<CriarPagamento>(pagamento, requestHeaders);

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
        Log.i("Script","pagamento normal = "+result);
        tp.retornoStringPagamento(result);
    }
}