package br.com.appinbanker.inbanker.webservice;

import android.os.AsyncTask;
import android.util.Log;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import br.com.appinbanker.inbanker.interfaces.WebServiceReturnCadastroString;
import br.com.appinbanker.inbanker.interfaces.WebServiceReturnString;

/**
 * Created by jonatasilva on 11/02/17.
 */

public class VerificaCEP extends AsyncTask<String, String, String> {

    WebServiceReturnCadastroString ws;
    String cep;

    public VerificaCEP(WebServiceReturnCadastroString ws, String cep){
        this.ws = ws;
        this.cep = cep;

    }
    @Override
    protected String doInBackground(String... strings) {
        String nome = null;
        String host = "https://viacep.com.br/";
        try {
            final String url = host+"ws/"+ cep +"/json/";

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            nome = restTemplate.getForObject(url, String.class);


        } catch (Exception e) {
            Log.e("MainActivity", e.getMessage(), e);
        }
        return nome;
    }

    @Override
    protected void onPostExecute(String result) {

        Log.i("Script","Nome restorno web serveice endereco = "+result);
        ws.retornoStringEndereco(result);
    }
}
