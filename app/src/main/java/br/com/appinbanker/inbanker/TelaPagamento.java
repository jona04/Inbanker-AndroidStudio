package br.com.appinbanker.inbanker;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.github.pinball83.maskededittext.MaskedEditText;

import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import br.com.appinbanker.inbanker.entidades.CartaoPagamento;
import br.com.appinbanker.inbanker.entidades.CriarPagamento;
import br.com.appinbanker.inbanker.entidades.CriarPagamentoToken;
import br.com.appinbanker.inbanker.entidades.KeyAccountPagamento;
import br.com.appinbanker.inbanker.entidades.RetornoPagamento;
import br.com.appinbanker.inbanker.entidades.Transacao;
import br.com.appinbanker.inbanker.entidades.Usuario;
import br.com.appinbanker.inbanker.interfaces.WebServiceReturnString;
import br.com.appinbanker.inbanker.interfaces.WebServiceReturnUsuario;
import br.com.appinbanker.inbanker.sqlite.BancoControllerUsuario;
import br.com.appinbanker.inbanker.sqlite.CriandoBanco;
import br.com.appinbanker.inbanker.util.AllSharedPreferences;
import br.com.appinbanker.inbanker.util.Mask;
import br.com.appinbanker.inbanker.util.Validador;
import br.com.appinbanker.inbanker.webservice.AddCartaoUsuario;
import br.com.appinbanker.inbanker.webservice.AddTransacao;
import br.com.appinbanker.inbanker.webservice.BuscaUsuarioCPF;
import br.com.appinbanker.inbanker.webservice.EnviaEmailEnvioPedido;
import br.com.appinbanker.inbanker.webservice.EnviaNotificacao;
import br.com.appinbanker.inbanker.webservice.SubmetePagamento;
import br.com.appinbanker.inbanker.webservice.SubmetePagamentoToken;
import cz.msebera.android.httpclient.util.EntityUtils;

/**
 * Created by jonatasilva on 04/03/17.
 */

public class TelaPagamento extends AppCompatActivity implements WebServiceReturnUsuario{

    ProgressDialog progress;

    ProgressBar progress_bar_tela_pagamento;

    MaskedEditText et_numero_cartao;
    RadioButton radio_mesmo_cartao;
    EditText et_cod_seguranca,et_validade;
    EditText et_nome_cartao;
    TextView et_valor_pagamento;
    Button btn_pagamento;
    String valor_spinner_bandeira;
    //String mes_validade;
    //String ano_validade;
    Transacao trans;
    String token_user2;
    String email_user2;

    boolean mesmo_cartao;

    RetornoPagamento retornoPagamento;

    LinearLayout add_cartao_pagamento;
    RelativeLayout rl_cartao;

    String token_pagamento;
    String num_cartao_token_pagamento;

    private TextWatcher validadeMask;
    private TextWatcher codMask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_tela_pagamento);

        Intent it = getIntent();
        trans = (Transacao) it.getExtras().getSerializable("transacao");
        token_user2 = it.getStringExtra("token_user2");
        email_user2 = it.getStringExtra("email_user2");



        Locale ptBr = new Locale("pt", "BR");
        NumberFormat nf = NumberFormat.getCurrencyInstance(ptBr);
        String taxa_fixa_string = nf.format (Double.parseDouble(trans.getValor_servico()));

        Log.i("Script","valor trans = "+trans.getValor_servico());

        radio_mesmo_cartao = (RadioButton) findViewById(R.id.radio_mesmo_cartao);
        //radio_mesmo_cartao.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic, 0);

        progress_bar_tela_pagamento = (ProgressBar) findViewById(R.id.progress_bar_tela_pagamento);
        rl_cartao = (RelativeLayout) findViewById(R.id.rl_cartao);
        add_cartao_pagamento = (LinearLayout) findViewById(R.id.add_cartao_pagamento);

        et_validade = (EditText) findViewById(R.id.et_validade);
        et_cod_seguranca = (EditText) findViewById(R.id.et_cod_seguranca);
        et_numero_cartao = (MaskedEditText) findViewById(R.id.et_numero_cartao);
        et_nome_cartao = (EditText) findViewById(R.id.et_nome_cartao);
        et_valor_pagamento = (TextView) findViewById(R.id.et_valor_pagamento);
        btn_pagamento = (Button) findViewById(R.id.btn_pagamento);

        et_valor_pagamento.setText(taxa_fixa_string);

        validadeMask = Mask.insert("##/####", et_validade);
        et_validade.addTextChangedListener(validadeMask);

        codMask = Mask.insert("###", et_cod_seguranca);
        et_cod_seguranca.addTextChangedListener(codMask);

        //verifica se usuario tem cartao cadastrado
        verificaMetodoPagamento();


        Spinner bandeira_pagamento = (Spinner) findViewById(R.id.bandeira_pagamento);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.bandeira_pagamento, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        bandeira_pagamento.setAdapter(adapter);
        bandeira_pagamento.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                //Log.i("Script","item spinner = "+adapterView.getItemAtPosition(pos));

                valor_spinner_bandeira = adapterView.getItemAtPosition(pos).toString();
                if(valor_spinner_bandeira.equals("MasterCard"))
                    valor_spinner_bandeira = "Master";
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //Log.i("Script","spinner onNothingSelected");
            }
        });


        /*Spinner mes_validade_pagamento = (Spinner) findViewById(R.id.mes_validade_pagamento);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter_mes = ArrayAdapter.createFromResource(this,
                R.array.mes_validade_pagamento, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter_mes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        mes_validade_pagamento.setAdapter(adapter_mes);
        mes_validade_pagamento.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {

                mes_validade = adapterView.getItemAtPosition(pos).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //Log.i("Script","spinner onNothingSelected");
            }
        });

        Spinner ano_validade_pagamento = (Spinner) findViewById(R.id.ano_validade_pagamento);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter_ano = ArrayAdapter.createFromResource(this,
                R.array.ano_validade_pagamento, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter_ano.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        ano_validade_pagamento.setAdapter(adapter_ano);
        ano_validade_pagamento.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {

                ano_validade = adapterView.getItemAtPosition(pos).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //Log.i("Script","spinner onNothingSelected");
            }
        });*/


        if(AllSharedPreferences.getPreferencesBoolean(AllSharedPreferences.VERIFY_TUTORIAL_PAGAMENTO,this)==false) {
            new ShowcaseView.Builder(this)
                    .setStyle(R.style.CustomShowcaseTheme)
                    .withMaterialShowcase()
                    .setTarget(new ViewTarget(et_valor_pagamento))
                    .setContentTitle("Pagamento")
                    .setContentText("Aqui você fará o pagamento da taxa de serviço por meio de cartão de crédito.\n\nLembrente 1: Ao confirmar o pedido, seu amigo deve aceita-lo e entregar pessoalmente o valor solicitado à você.\nLembrete 2: Ao receber o valor de seu amigo, o próximo passo é confirmar o recebimento através do aplicativo InBanker.")
                    .build();

            AllSharedPreferences.putPreferencesBooleanTrue(AllSharedPreferences.VERIFY_TUTORIAL_PAGAMENTO,this);

        }


    }

    public void verificaMetodoPagamento(){

        BancoControllerUsuario crud = new BancoControllerUsuario(getBaseContext());
        Cursor cursor = crud.carregaDados();
        String cpf = cursor.getString(cursor.getColumnIndexOrThrow(CriandoBanco.CPF));

        new BuscaUsuarioCPF(cpf,this,this).execute();

    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_mesmo_cartao:
                if (checked)
                    mesmo_cartao = true;
                    add_cartao_pagamento.setVisibility(View.GONE);
                    break;
            case R.id.radio_outro_cartao:
                if (checked)
                    mesmo_cartao = false;
                    add_cartao_pagamento.setVisibility(View.VISIBLE);
                    break;
        }
    }

    public void desabilitaBtn(){
        btn_pagamento.setEnabled(false);
    }
    public void habilitaBtn(){
        btn_pagamento.setEnabled(true);
    }
    public void realizarPagamento(View view){
        Log.i("Script","btn ralizzar pagamento");

        desabilitaBtn();

        progress = ProgressDialog.show(TelaPagamento.this, "Verificando Dados",
                "Olá, esse processo pode demorar alguns segundos...", true);

        if(mesmo_cartao){

            // Usually this can be a field rather than a method variable
            Random rand = new Random();

            // nextInt is normally exclusive of the top value,
            // so add 1 to make it inclusive
            int randomNum = rand.nextInt(9999990 - 100000);

            CriarPagamentoToken pagamentoToken = new CriarPagamentoToken();

            pagamentoToken.setMerchantOrderId(String.valueOf(randomNum));
            pagamentoToken.setIdRequest("4");
            pagamentoToken.setClientAcount(KeyAccountPagamento.CLIENT_ACCOUNT);
            pagamentoToken.setClientKey(KeyAccountPagamento.CLIENT_KEY);
            pagamentoToken.setPaymentsAmount(trans.getValor_servico());
            pagamentoToken.setPaymentsInstallments("1");
            pagamentoToken.setPaymentsSoftDescriptor("INBANKER");
            pagamentoToken.setPaymentsCapture("FALSE");
            pagamentoToken.setToken(token_pagamento);

            new SubmetePagamentoToken(this, pagamentoToken).execute();

        }else {

            if(checkCamposCartao()) {

                Log.i("String",trans.getValor_servico());

                // Usually this can be a field rather than a method variable
                Random rand = new Random();

                // nextInt is normally exclusive of the top value,
                // so add 1 to make it inclusive
                int randomNum = rand.nextInt(9999990 - 100000);

                CriarPagamento pagamento = new CriarPagamento();

                pagamento.setMerchantOrderId(String.valueOf(randomNum));
                pagamento.setIdRequest("4");
                pagamento.setClientAcount(KeyAccountPagamento.CLIENT_ACCOUNT);
                pagamento.setClientKey(KeyAccountPagamento.CLIENT_KEY);
                pagamento.setCustomerName(et_nome_cartao.getText().toString());
                pagamento.setPaymentsAmount(trans.getValor_servico());
                pagamento.setPaymentsInstallments("1");
                pagamento.setPaymentsSoftDescriptor("INBANKER");
                pagamento.setPaymentsType("Creditcard");
                pagamento.setPaymentsCapture("FALSE");
                pagamento.setCreditCardBrand(valor_spinner_bandeira);
                pagamento.setCreditCardCardNumber(et_numero_cartao.getUnmaskedText());
                pagamento.setCreditCardSecurityCode(et_cod_seguranca.getText().toString());
                pagamento.setCreditCardExpirationDate(et_validade.getText().toString());
                pagamento.setCreditCardHolder(et_nome_cartao.getText().toString());

                new SubmetePagamento(this, pagamento).execute();
            }
        }
    }

    public void retornoStringPagamento(String rp){

        if(rp!=null) {

            try {

                JSONObject jsonObj = new JSONObject(rp);
                if(jsonObj.isNull("error")) {

                    ObjectMapper mapper = new ObjectMapper();
                    //JSON from String to Object
                    retornoPagamento = mapper.readValue(rp, RetornoPagamento.class);

                    /*Log.i("Script", "resultado = pagamento = " + retornoPagamento.getAmount_first());
                    Log.i("Script", "resultado = pagamento = " + retornoPagamento.getReturn_CardNumber());
                    Log.i("Script", "resultado = pagamento = " + retornoPagamento.getReturn_Status());
                    Log.i("Script", "resultado = pagamento = " + retornoPagamento.getTid_first());
                    Log.i("Script", "resultado = pagamento = " + retornoPagamento.getReturn_message_first());
                    Log.i("Script", "resultado = pagamento = " + retornoPagamento.getToken());*/

                    if (retornoPagamento.getReturn_Status().equals("3")) {
                        progress.dismiss();
                        mensagem("Autorização negada", "Olá, seu cartão foi recusado, por favor revise seus dados.", "Ok");
                    } else if (retornoPagamento.getReturn_Status().equals("1")) {
                        //mensagem("Transação autorizada", "Pedido enviado, aguarde a resposta de seu amigo(a) " + trans.getNome_usu2(), "Ok");

                        //adicionamos o retorno em transacoes, no banco mongodb
                        trans.setPagamento(retornoPagamento);

                        new AddTransacao(trans, TelaPagamento.this).execute();

                    } else {
                        progress.dismiss();
                        mensagem("Erro crítico!", "Parece que houve um erro de pagamento, por favor tente novamente.", "Ok");
                        //mensagem("Autorização negada", jsonObj.getString("error"), "Ok");
                        progress.dismiss();
                        habilitaBtn();
                    }
                }else{
                    mensagem("Autorização negada", jsonObj.getString("error"), "Ok");
                    progress.dismiss();
                    habilitaBtn();
                }

            } catch (Exception e) {
                e.printStackTrace();
                progress.dismiss();
                habilitaBtn();
                mensagem("Erro crítico!", "Verifique se o nome foi digitado corretamente ou se existe conexão. Se o erro persistir por favor entre em contato conosco.", "Ok");
            }

        }else{
            habilitaBtn();
            progress.dismiss();
            mensagem("Erro crítico!", "Parece que houve um erro de conexão, por favor tente novamente.", "Ok");

        }
    }

    public void retornoAddTransacao(String result){

        progress.dismiss();

        BancoControllerUsuario crud = new BancoControllerUsuario(getBaseContext());
        Cursor cursor = crud.carregaDados();
        String email = cursor.getString(cursor.getColumnIndexOrThrow(CriandoBanco.EMAIL));

        if(result!=null) {
            if (result.equals("sucesso_edit")) {
                if(!token_user2.equals("")) {

                    CartaoPagamento cp = new CartaoPagamento();
                    cp.setToken_cartao(retornoPagamento.getToken());
                    cp.setNumero_cartao(retornoPagamento.getReturn_CardNumber());

                    List<CartaoPagamento> list_cp = new ArrayList<CartaoPagamento>();
                    list_cp.add(cp);

                    Usuario usu = new Usuario();
                    usu.setCpf(trans.getUsu1());
                    usu.setCartaoPagamento(list_cp);

                    //add token ao usuario
                    new AddCartaoUsuario(usu).execute();

                    //envia notificação
                    new EnviaNotificacao(trans, token_user2).execute();

                    //envia email para usuarios
                    //new EnviaEmailEnvioPedido(trans,email,email_user2).execute();

                    //aletar e redirecionamento para tela inicial
                    mensagemIntent("InBanker", "Pedido enviado, aguarde a resposta de seu amigo(a) " + trans.getNome_usu2(), "Ok");
                }else{
                    mensagemIntent("InBanker","Pedido enviado! Porém recomendamos entrar em contato pessoalmente com seu amigo(a) "+trans.getNome_usu2()+". Pois ele não receberá notifição de aviso por não estar logado.", "Ok");
                }
            } else {
                mensagem("Houve um erro!", "Parece que houve um erro de conexão, por favor tente novamente.", "Ok");
            }
        }else{
            mensagem("Houve um erro!", "Parece que houve um erro de conexão, por favor tente novamente.", "Ok");
        }


    }

    public boolean checkCamposCartao(){

        boolean campos_ok = true;

        boolean cod_seguranca = Validador.validateNotNull(et_cod_seguranca.getText().toString());
        if(!cod_seguranca){
            et_cod_seguranca.setError("Campo Vazio");
            et_cod_seguranca.setFocusable(true);
            et_cod_seguranca.requestFocus();

            campos_ok = false;
            progress.dismiss();
        }

        boolean numero_cartao = Validador.validateNotNull(et_numero_cartao.getText().toString());
        if(!numero_cartao){
            et_numero_cartao.setError("Campo Vazio");
            et_numero_cartao.setFocusable(true);
            et_numero_cartao.requestFocus();

            campos_ok = false;
            progress.dismiss();
        }

        boolean nome_cartao = Validador.validateNotNull(et_nome_cartao.getText().toString());
        if(!nome_cartao){
            et_nome_cartao.setError("Campo Vazio");
            et_nome_cartao.setFocusable(true);
            et_nome_cartao.requestFocus();

            campos_ok = false;
            progress.dismiss();
        }
        return campos_ok;
    }

    public void mensagemIntent(String titulo,String corpo,String botao)
    {
        AlertDialog.Builder mensagem = new AlertDialog.Builder(this);
        mensagem.setTitle(titulo);
        mensagem.setMessage(corpo);
        mensagem.setPositiveButton(botao,new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent it = new Intent(TelaPagamento.this,NavigationDrawerActivity.class);
                startActivity(it);
                //para encerrar a activity atual e todos os parent
                finishAffinity();
            }
        });
        mensagem.show();
    }

    public void mensagem(String titulo,String corpo,String botao)
    {
        AlertDialog.Builder mensagem = new AlertDialog.Builder(this);
        mensagem.setTitle(titulo);
        mensagem.setMessage(corpo);
        mensagem.setNeutralButton(botao,null);
        mensagem.show();
    }

    @Override
    public void retornoUsuarioWebService(Usuario usu) {
        Log.i("Script","retornoUsuarioWebService");
        progress_bar_tela_pagamento.setVisibility(View.GONE);
        btn_pagamento.setVisibility(View.VISIBLE);
        if(usu!=null){
            if(usu.getCartaoPagamento()!=null){

                //usuario possui cartao cadastrado, entao habilitamos por padrao
                mesmo_cartao = true;

                List<CartaoPagamento> cp = usu.getCartaoPagamento();
                num_cartao_token_pagamento = cp.get(0).getNumero_cartao();

                token_pagamento = cp.get(0).getToken_cartao();

                radio_mesmo_cartao.setText(num_cartao_token_pagamento);

                rl_cartao.setVisibility(View.VISIBLE);
            }else{
                add_cartao_pagamento.setVisibility(View.VISIBLE);

                //usuario nao possui cartao cadastrado, entao desabilitamos o mesmo cartao
                mesmo_cartao = false;
            }
        }else{
            mensagem("Houve um erro!", "Parece que houve um erro de conexão, por favor tente novamente.", "Ok");
        }

    }

    @Override
    public void retornoUsuarioWebServiceAux(Usuario usu) {

    }
}
