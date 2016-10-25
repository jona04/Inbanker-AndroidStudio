package br.com.appinbanker.inbanker.webservice;

import android.os.AsyncTask;
import android.util.Log;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import br.com.appinbanker.inbanker.SimuladorResultado;
import br.com.appinbanker.inbanker.entidades.Usuario;
import br.com.appinbanker.inbanker.fragments_navigation.PedidosEnviadosFragment;

/**
 * Created by jonatassilva on 24/10/16.
 */

public class BuscaUsuarioCPF extends AsyncTask<String,String,Usuario> {

    private PedidosEnviadosFragment pef;
    private String cpf;

    public BuscaUsuarioCPF(String cpf, PedidosEnviadosFragment pef){

        this.pef = pef;
        this.cpf = cpf;

    }

    @Override
    protected Usuario doInBackground(String... params) {
        Usuario usu = null;
        try {
            //final String url = "http://45.55.217.160:8081/appinbanker/rest/usuario/findEmail/"+email;
            final String url = "http://10.0.3.2:8080/appinbanker/rest/usuario/findCpf/"+cpf;

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

        Log.w("webservice", "resesult busca pedido env = "+result);

        pef.retornoBuscaUsuario(result);

    }

}
