package br.com.appinbanker.inbanker.webservice;

import android.os.AsyncTask;
import android.util.Log;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import br.com.appinbanker.inbanker.entidades.Usuario;
import br.com.appinbanker.inbanker.fragments_navigation.HistoricoFragment;

/**
 * Created by jonatassilva on 24/10/16.
 */

public class BuscaUsuarioHistoricoCPF extends AsyncTask<String,String,Usuario> {

    private HistoricoFragment hf;
    private String cpf;

   public BuscaUsuarioHistoricoCPF(String cpf, HistoricoFragment hf){

       this.cpf = cpf;
       this.hf = hf;

   }


    @Override
    protected Usuario doInBackground(String... params) {
        Usuario usu = null;
        String url = "";
        String host = Host.host;
        try {
            url = host+"appinbanker/rest/usuario/findCpfTransHistorico/"+cpf;

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

        Log.w("webservice", "resesult busca usuario cpf = "+result);

        hf.retornoBuscaUsuario(result);

    }

}
