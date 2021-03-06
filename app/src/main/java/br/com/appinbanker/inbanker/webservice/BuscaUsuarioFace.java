package br.com.appinbanker.inbanker.webservice;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import br.com.appinbanker.inbanker.entidades.Usuario;
import br.com.appinbanker.inbanker.interfaces.WebServiceReturnUsuarioFace;

/**
 * Created by jonatassilva on 23/10/16.
 */

public class BuscaUsuarioFace extends AsyncTask<String,String,Usuario> {

    private String id_face;
    private Context context;

    private WebServiceReturnUsuarioFace ru;


    public BuscaUsuarioFace(String id_face, Context context, WebServiceReturnUsuarioFace ru){

        this.context = context;
        this.ru = ru;
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

        //verificamos de onde esta sendo chamado a api, para utilizamos o retorno especifico
        ru.retornoUsuarioWebServiceFace(result);
    }

}
