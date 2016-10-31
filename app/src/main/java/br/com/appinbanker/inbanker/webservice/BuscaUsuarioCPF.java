package br.com.appinbanker.inbanker.webservice;

import android.os.AsyncTask;
import android.util.Log;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import br.com.appinbanker.inbanker.entidades.Usuario;
import br.com.appinbanker.inbanker.fragments_navigation.HistoricoFragment;
import br.com.appinbanker.inbanker.fragments_navigation.InicioFragment;
import br.com.appinbanker.inbanker.fragments_navigation.PagamentosPendentesFragment;
import br.com.appinbanker.inbanker.fragments_navigation.PedidosEnviadosFragment;
import br.com.appinbanker.inbanker.fragments_navigation.PedidosRecebidosFragment;

/**
 * Created by jonatassilva on 24/10/16.
 */

public class BuscaUsuarioCPF extends AsyncTask<String,String,Usuario> {

    private PedidosEnviadosFragment pef;
    private PedidosRecebidosFragment prf;
    private HistoricoFragment hf;
    private PagamentosPendentesFragment pf;
    private InicioFragment inif;

    private String cpf;

    public BuscaUsuarioCPF(String cpf, PedidosEnviadosFragment pef, PedidosRecebidosFragment prf, HistoricoFragment hf, PagamentosPendentesFragment pf, InicioFragment inif){

        this.prf = prf;
        this.pef = pef;
        this.cpf = cpf;
        this.hf = hf;
        this.pf = pf;
        this.inif = inif;

    }

    @Override
    protected Usuario doInBackground(String... params) {
        Usuario usu = null;
        String url = "";
        //String host = "http://10.0.3.2:8080";
        String host = "http://45.55.217.160:8081";
        try {
            //final String url = "http://45.55.217.160:8081/appinbanker/rest/usuario/findEmail/"+email;
            //verificamos de onde esta sendo chamado a api, para utilizamos a url especifico
            if(pef != null || pf != null)
                url = host+"/appinbanker/rest/usuario/findCpfTransEnv/"+cpf;
            else if (prf != null)
                url = host+"/appinbanker/rest/usuario/findCpfTransRec/"+cpf;
            else if (hf != null)
                url = host+"/appinbanker/rest/usuario/findCpfTransHistorico/"+cpf;
            else if (inif != null)
                url = host+"/appinbanker/rest/usuario/findUsuarioCpf/"+cpf;

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

        //verificamos de onde esta sendo chamado a api, para utilizamos o retorno especifico
        if(pef != null)
            pef.retornoBuscaUsuario(result);
        else if(prf != null)
            prf.retornoBuscaUsuario(result);
        else if (hf != null)
            hf.retornoBuscaUsuario(result);
        else if (pf != null)
            pf.retornoBuscaUsuario(result);
        else if (inif != null)
            inif.retornoBuscaUsuario(result);
    }

}
