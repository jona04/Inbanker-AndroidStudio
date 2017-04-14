package br.com.appinbanker.inbanker.webservice;

import android.os.AsyncTask;
import android.util.Log;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import br.com.appinbanker.inbanker.interfaces.WebServiceReturnCadastroString;
import br.com.appinbanker.inbanker.interfaces.WebServiceReturnString;

/**
 * Created by jonatasilva on 11/02/17.
 */

public class VerificaCpfReceita extends AsyncTask<String, String, String> {

    WebServiceReturnCadastroString ws;
    String cpf;

    public VerificaCpfReceita(WebServiceReturnCadastroString ws,String cpf){
        this.ws = ws;
        this.cpf = cpf;

    }
    @Override
    protected String doInBackground(String... strings) {
        String nome = null;
        String host = Host.host;
        //String host = "http://10.0.2.2:8000/";
        try {
            //final String url = host+"appinbanker/cpf/consulta/"+cpf;
            //5ae973d7a997af13f0aaf2bf60e65803 teste
            //f7e4b915509f9d6c7fc924e524d24880 producao
            final String url = "https://api.cpfcnpj.com.br/f7e4b915509f9d6c7fc924e524d24880/1/json/"+cpf;

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

        Log.i("Script","Nome restorno web serveice receita = "+result);
        ws.retornoStringNomeCpf(result);
    }
}
