package br.com.appinbanker.inbanker.webservice;

import android.os.AsyncTask;
import android.util.Log;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import br.com.appinbanker.inbanker.interfaces.WebServiceReturnStringEmail;
import br.com.appinbanker.inbanker.interfaces.WebServiceReturnStringIdFace;

/**
 * Created by jonatasilva on 20/02/17.
 */

public class VerificaIdFace extends AsyncTask<String,String,String> {

    private String id_face;
    private WebServiceReturnStringIdFace ws;

    public VerificaIdFace(String id_face, WebServiceReturnStringIdFace ws){
        this.id_face = id_face;
        this.ws = ws;
    }


    @Override
    protected String doInBackground(String... params) {
        String usu = null;

        String host = Host.host;
        try {
            final String url = host+"appinbanker/rest/usuario/verificaIdFace/"+id_face;

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
        ws.retornoStringWebServiceIdFace(result);


    }


}
