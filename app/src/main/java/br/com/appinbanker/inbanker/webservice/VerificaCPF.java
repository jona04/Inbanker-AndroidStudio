package br.com.appinbanker.inbanker.webservice;

import android.os.AsyncTask;
import android.util.Log;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import br.com.appinbanker.inbanker.interfaces.WebServiceReturnStringCPF;
import br.com.appinbanker.inbanker.interfaces.WebServiceReturnStringEmail;

/**
 * Created by jonatasilva on 20/02/17.
 */

public class VerificaCPF extends AsyncTask<String,String,String> {

    private String cpf;
    private WebServiceReturnStringCPF ws;

    public VerificaCPF(String cpf, WebServiceReturnStringCPF ws){
        this.cpf = cpf;
        this.ws = ws;
    }


    @Override
    protected String doInBackground(String... params) {
        String usu = null;

        String host = Host.host;
        try {
            final String url = host+"appinbanker/rest/usuario/verificaCPFCadastro/"+cpf;

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            usu = restTemplate.getForObject(url, String.class);

        } catch (Exception e) {
            Log.e("MainActivity", e.getMessage(), e);
        }
        return usu;
    }
    @Override
    protected void onPostExecute(String result) {

        Log.i("Script",""+result);
        ws.retornoStringWebServiceCPF(result);


    }


}
