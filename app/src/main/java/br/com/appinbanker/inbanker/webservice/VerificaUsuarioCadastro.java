package br.com.appinbanker.inbanker.webservice;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import br.com.appinbanker.inbanker.CadastroUsuario;
import br.com.appinbanker.inbanker.TelaLogin;
import br.com.appinbanker.inbanker.entidades.Usuario;

/**
 * Created by Jonatas on 27/10/2016.
 */

public class VerificaUsuarioCadastro extends AsyncTask<String,String,String> {

    private Context context;
    private CadastroUsuario tl;
    private String email;
    private String cpf;

    public VerificaUsuarioCadastro(String email,String cpf, Context context, CadastroUsuario tl){
        this.context = context;
        this.tl = tl;
        this.email = email;
        this.cpf = cpf;

    }
    @Override
    protected String doInBackground(String... params) {
        String usu = null;
        try {
            //final String url = "http://45.55.217.160:8081/appinbanker/rest/usuario/findEmail/"+email;
            final String url = "http://10.0.3.2:8080/appinbanker/rest/usuario/verificaUsuarioCadastro/"+cpf+"/"+email;

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

        tl.retornoTaskVerifica(result);

    }

}
