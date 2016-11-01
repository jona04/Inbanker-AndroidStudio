package br.com.appinbanker.inbanker.webservice;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import br.com.appinbanker.inbanker.SimuladorResultado;
import br.com.appinbanker.inbanker.TelaLogin;
import br.com.appinbanker.inbanker.entidades.Usuario;

/**
 * Created by jonatassilva on 23/10/16.
 */

public class BuscaUsuarioFace extends AsyncTask<String,String,Usuario> {

    private SimuladorResultado sr;
    private String id_face;

    public BuscaUsuarioFace(String id_face, SimuladorResultado sr){

        this.sr = sr;
        this.id_face = id_face;

    }

    @Override
    protected Usuario doInBackground(String... params) {
        Usuario usu = null;
        String host = Host.host;
        try {
            //final String url = "http://45.55.217.160:8081/appinbanker/rest/usuario/findEmail/"+email;
            final String url = host+"appinbanker/rest/usuario/findFace/"+id_face;

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            usu = restTemplate.getForObject(url, Usuario.class);

        } catch (Exception e) {
            Log.e("MainActivity", e.getMessage(), e);
        }
        return usu;
    }
    @Override
    protected void onPostExecute(Usuario result) {

        if(result!= null)
            Log.i("Script",result.getNome());

        sr.retornoBuscaUsuario(result);

    }

}
