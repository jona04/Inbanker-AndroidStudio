package br.com.appinbanker.inbanker.webservice;

import android.os.AsyncTask;
import android.util.Log;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import br.com.appinbanker.inbanker.VerPagamentoPendente;
import br.com.appinbanker.inbanker.VerPedidoEnviado;
import br.com.appinbanker.inbanker.VerPedidoRecebido;
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

    private VerPedidoRecebido vpr;
    private VerPedidoEnviado vpe;
    private VerPagamentoPendente vpp;

    private String cpf;

    public BuscaUsuarioCPF(String cpf, PedidosEnviadosFragment pef, PedidosRecebidosFragment prf, HistoricoFragment hf, PagamentosPendentesFragment pf, InicioFragment inif){

        this.prf = prf;
        this.pef = pef;
        this.cpf = cpf;
        this.hf = hf;
        this.pf = pf;
        this.inif = inif;

    }

    public BuscaUsuarioCPF(String cpf, VerPedidoRecebido vpr, VerPedidoEnviado vpe, VerPagamentoPendente vpp){
        this.cpf = cpf;
        this.vpr = vpr;
        this.vpe = vpe;
        this.vpp = vpp;

    }

    @Override
    protected Usuario doInBackground(String... params) {
        Usuario usu = null;
        String url = "";
        String host = Host.host;
        try {
            //final String url = "http://45.55.217.160:8081/appinbanker/rest/usuario/findEmail/"+email;
            //verificamos de onde esta sendo chamado a api, para utilizamos a url especifico
            if(pef != null || pf != null)
                url = host+"appinbanker/rest/usuario/findCpfTransEnv/"+cpf;
            else if (prf != null)
                url = host+"appinbanker/rest/usuario/findCpfTransRec/"+cpf;
            else if (hf != null)
                url = host+"appinbanker/rest/usuario/findCpfTransHistorico/"+cpf;
            else if (inif != null)
                url = host+"appinbanker/rest/usuario/findUsuarioCpf/"+cpf;
            else if (vpr != null)
                url = host+"appinbanker/rest/usuario/findUsuarioCpf/"+cpf;
            else if (vpe != null)
                url = host+"appinbanker/rest/usuario/findUsuarioCpf/"+cpf;
            else if (vpp != null)
                url = host+"appinbanker/rest/usuario/findUsuarioCpf/"+cpf;

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
        else if(vpr != null)
            vpr.retornoBuscaTokenUsuario(result);
        else if(vpe != null)
            vpe.retornoBuscaTokenUsuario(result);
        else if(vpp != null)
            vpp.retornoBuscaTokenUsuario(result);
    }

}
