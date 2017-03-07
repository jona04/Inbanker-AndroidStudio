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

import br.com.appinbanker.inbanker.entidades.AlteraPagamento;
import br.com.appinbanker.inbanker.interfaces.WebServiceReturnStringPagamento;

/**
 * Created by jonatassilva on 22/10/16.
 */

public class AlteraPagamentoService extends AsyncTask<String, String, String> {

    private WebServiceReturnStringPagamento tp;
    private AlteraPagamento pagamento;

    public AlteraPagamentoService(WebServiceReturnStringPagamento tp, AlteraPagamento pagamento){
        this.tp = tp;
        this.pagamento = pagamento;
    }

    @Override
    protected String doInBackground(String... params) {


        String url = "http://45.55.217.160:81/systemRecorrency_1_01/control_modificacao_recorrencia/control_modificacao_recorrencia.php?api_externo=1";

        try {
            // Set the Content-Type header
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.setContentType(new MediaType("application", "json"));
            HttpEntity<AlteraPagamento> requestEntity = new HttpEntity<AlteraPagamento>(pagamento, requestHeaders);

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
        Log.i("Script","altera pagamento = "+result);
        tp.retornoStringWebServicePagamento(result);
    }
}