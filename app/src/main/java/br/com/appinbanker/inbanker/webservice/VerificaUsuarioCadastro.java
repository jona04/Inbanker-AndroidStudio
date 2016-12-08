package br.com.appinbanker.inbanker.webservice;

import android.os.AsyncTask;
import android.util.Log;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import br.com.appinbanker.inbanker.Inicio;
import br.com.appinbanker.inbanker.SimuladorResultado;

/**
 * Created by Jonatas on 27/10/2016.
 */

public class VerificaUsuarioCadastro extends AsyncTask<String,String,String> {

    private Inicio tl;
    private String email;
    private String cpf;
    private SimuladorResultado sr;

    public VerificaUsuarioCadastro(String email,String cpf, Inicio tl){
        this.tl = tl;
        this.email = email;
        this.cpf = cpf;

    }

    public VerificaUsuarioCadastro(String email,String cpf, SimuladorResultado sr){
        this.sr = sr;
        this.email = email;
        this.cpf = cpf;

    }


    @Override
    protected String doInBackground(String... params) {
        String usu = null;

        String host = Host.host;
        try {
            final String url = host+"appinbanker/rest/usuario/verificaUsuarioCadastro/"+cpf+"/"+email;

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
        if (tl != null)
            tl.retornoTaskVerificaCadastro(result);
        else if (sr != null)
            sr.retornoTaskVerificaCadastro(result);
    }

}
