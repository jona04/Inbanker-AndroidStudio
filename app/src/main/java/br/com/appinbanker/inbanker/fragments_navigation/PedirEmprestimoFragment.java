package br.com.appinbanker.inbanker.fragments_navigation;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.com.appinbanker.inbanker.R;
import br.com.appinbanker.inbanker.SimuladorResultado;
import br.com.appinbanker.inbanker.adapters.ListaAmigosAdapter;
import br.com.appinbanker.inbanker.entidades.Amigos;
import br.com.appinbanker.inbanker.entidades.Usuario;
import br.com.appinbanker.inbanker.interfaces.RecyclerViewOnClickListenerHack;
import br.com.appinbanker.inbanker.interfaces.WebServiceReturnString;
import br.com.appinbanker.inbanker.interfaces.WebServiceReturnStringIdFace;
import br.com.appinbanker.inbanker.interfaces.WebServiceReturnUsuarioFace;
import br.com.appinbanker.inbanker.sqlite.BancoControllerUsuario;
import br.com.appinbanker.inbanker.sqlite.CriandoBanco;
import br.com.appinbanker.inbanker.util.AllSharedPreferences;
import br.com.appinbanker.inbanker.util.MaskMoney;
import br.com.appinbanker.inbanker.util.Validador;
import br.com.appinbanker.inbanker.webservice.AtualizaUsuario;
import br.com.appinbanker.inbanker.webservice.BuscaUsuarioFace;
import br.com.appinbanker.inbanker.webservice.VerificaIdFace;

public class PedirEmprestimoFragment extends Fragment implements RecyclerViewOnClickListenerHack,WebServiceReturnString,WebServiceReturnUsuarioFace {

    private EditText et_calendario,et_valor;

    // Variable for storing current date and time
    private int mYear, mMonth, mDay,dias_pagamento;

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private ArrayList<Amigos> mList;
    private LinearLayout pb,msg_lista_amigos;

    private LoginButton loginButton;
    private CallbackManager callbackManager;

    private boolean usuario_logado = false;

    String id_face,nome_face,url_face;

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference usuarioReferencia = databaseReference.child("usuarios");

    public PedirEmprestimoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext(), new FacebookSdk.InitializeCallback() {
            @Override
            public void onInitialized() {
                if(AccessToken.getCurrentAccessToken() == null){
                    Log.i("Facebook","nao logado");

                    //utilizamos para deixar a lista no modo hide
                    usuario_logado = false;
                } else {
                    Log.i("Facebook","logado accestoken = "+AccessToken.getCurrentAccessToken());

                    BancoControllerUsuario crud = new BancoControllerUsuario(getActivity());
                    Cursor cursor = crud.carregaDados();
                    String id_face = cursor.getString(cursor.getColumnIndexOrThrow(CriandoBanco.ID_FACE));
                    if(id_face.equals("")){

                        //faz o logout do usuario logado facebook
                        LoginManager.getInstance().logOut();
                        //mRecyclerView.setVisibility(View.GONE);
                        //pb.setVisibility(View.GONE);

                    }else {
                        //utilizamos para deixar a lista no modo hide
                        usuario_logado = true;
                        graphFacebook(AccessToken.getCurrentAccessToken());
                    }
                }
            }
        });
        callbackManager = CallbackManager.Factory.create();

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pedir_emprestimo, container, false);

        msg_lista_amigos = (LinearLayout) view.findViewById(R.id.msg_lista_amigos);
        pb = (LinearLayout) view.findViewById(R.id.progress_lista_amigos);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_list_amigos);

        //se usuario nao estiver logado, escondemos a lista de amigos
        if(!usuario_logado) {
            mRecyclerView.setVisibility(View.GONE);
            pb.setVisibility(View.GONE);
        }else{
            //mostramos novamente a barra de carregar e a lista de amigos
            mRecyclerView.setVisibility(View.VISIBLE);
            pb.setVisibility(View.VISIBLE);
        }

        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        loginButton = (LoginButton) view.findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");
        loginButton.setReadPermissions("user_friends");

        // If using in a fragment
        loginButton.setFragment(this);
        // Other app specific specialization

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.i("Facebook", "onSuceess - loingResult= "+loginResult);

                //mostramos novamente a barra de carregar e a lista de amigos
                mRecyclerView.setVisibility(View.VISIBLE);
                pb.setVisibility(View.VISIBLE);

                //chamamos o metedo graphFacebook para obter os dados do usuario logado
                //passando como parametro o accessToken gerado no login
                graphFacebook(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.i("Facebook", "onCancel");
            }

            @Override
            public void onError(FacebookException exception) {
                Log.i("Facebook", "onError - exception = "+exception);

                pb.setVisibility(View.GONE);
                mensagem("Houve um erro!","Olá, parece que houve um problema de conexao. Favor tente novamente!","Ok");
            }

        });
        return view;
    }

    public void graphFacebook(final AccessToken accessToken){
        try{

            // App code
            GraphRequest request = GraphRequest.newMeRequest(
                    accessToken,
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            //Log.v("Facebook", response.toString());
                            //Log.v("Facebook", object.toString());


                            try {
                                // Application code
                                String id = object.getString("id");
                                obterDados(accessToken,id);

                                //String birthday = object.getString("birthday"); // 01/31/1980 format
                            }catch (Exception e){
                                Log.i("Facebook","Exception JSON graph facebook = "+e);

                                mensagem("Houve um erro!","Olá, parece que houve um problema de conexao. Favor tente novamente!","Ok");
                                mRecyclerView.setVisibility(View.GONE);
                                pb.setVisibility(View.GONE);

                            }
                        }
                    });

            request.executeAsync();
        }
        catch (Exception e){
            Log.i("Facebook","Exception graph facebook = "+e);
        }
    }

    public void obterDados(AccessToken accessToken, String id){

        Log.i("Script","obterDados");

        /* make the API call */
        new GraphRequest(
                accessToken,
                "/"+id+"?fields=id,name,email,picture.type(large),friends{id,name,picture.type(large){url}}",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        //Log.i("Facebook","dados gerais = "+response);

                        try {

                            JSONObject object = response.getJSONObject();

                            id_face = object.getString("id");
                            //String email = object.getString("email");
                            nome_face = removerAcentos(object.getString("name"));

                            JSONObject pic = object.getJSONObject("picture");
                            pic = pic.getJSONObject("data");
                            url_face= pic.getString("url");

                            object = object.getJSONObject("friends");
                            JSONArray friends_list = object.getJSONArray("data");

                            //Log.i("Facebook","id, email, name ="+ id +"- "+name + " - "+url_picture);
                            //Log.i("Facebook","friends = "+friends_list);

                            ObjectMapper mapper = new ObjectMapper();
                            mList = mapper.readValue(friends_list.toString(),
                                    TypeFactory.defaultInstance().constructCollectionType(List.class,
                                            Amigos.class));

                            //Log.i("Facebook","amigos = "+list.get(0).getPicture().getData().getUrl());

                            //for (Amigos a: mList) {
                            //    Log.i("Facebook","amigo listado = "+a.getName());
                            //}


                            //atualizamos info no faribase
                            /*amigosRef = database.getReference("usuarios");

                            Map<String, Object> infos = new HashMap<>();
                            infos.put("id_face",id);
                            infos.put("nome_face",name);
                            infos.put("img_url",url_picture);

                            amigosRef.child(id).updateChildren(infos);*/

                            if (usuario_logado)
                                listaAmigos();
                            else
                                verifica_usuario_existe();
                        }
                        catch(Exception e){
                            Log.i("Facebook","exception = "+e);

                            mensagem("Houve um erro!","Olá, parece que houve um problema de conexao. Favor tente novamente!","Ok");
                            mRecyclerView.setVisibility(View.GONE);
                            pb.setVisibility(View.GONE);
                        }
                    }
                }
        ).executeAsync();

    }

    public void verifica_usuario_existe(){

        Log.i("Script","verifica_usuario_existe");

        //checa se id face recem logado já existe
        new BuscaUsuarioFace(id_face,getActivity(),this).execute();
    }

    @Override
    public void retornoUsuarioWebServiceFace(Usuario usu) {

        BancoControllerUsuario crud = new BancoControllerUsuario(getActivity());
        Cursor cursor = crud.carregaDados();
        String id_face_logado = cursor.getString(cursor.getColumnIndexOrThrow(CriandoBanco.ID_FACE));

        if(usu!=null) {

            if (!usu.getId_face().equals(id_face_logado) && !id_face_logado.equals("")){
                //erro
                mensagem("Houve um erro!","Olá, parece que o usuário que você esta tentando logar, já esta vinculado a outra conta. " +
                        "Tente fazer o login diretamente pelo Facebook na tela inicial do aplicativo.","Ok");

                //faz o logout do usuario logado facebook
                LoginManager.getInstance().logOut();

                mRecyclerView.setVisibility(View.GONE);
                pb.setVisibility(View.GONE);

            }else{
                //continua
                confirmarUsuarioLogadoFace();
            }
        }else{
            //continua
            confirmarUsuarioLogadoFace();
        }

    }

    public void confirmarUsuarioLogadoFace(){

        Log.i("PedirEmprestimo","Confirmar usuario logado face no me = "+nome_face);

        //antes de fazer a verificacao do usuario recem logado no nosso banco
        //pedimos para o osuaurio verificar se esse usuario é ele mesmo
        final Dialog dialog = new Dialog(getActivity(),R.style.AppThemeDialog);
        dialog.setContentView(R.layout.dialog_confirma_usuario_logado_face);
        dialog.setTitle("Confirmar Login");

        Button btn_confirmar_login = (Button) dialog.findViewById(R.id.btn_confirmar_login);
        Button btn_cancelar_login = (Button) dialog.findViewById(R.id.btn_cancelar_login);

        TextView tv_msg = (TextView) dialog.findViewById(R.id.tv_msg_confirma_usuario_logado_face);
        tv_msg.setText("Olá, você esta tentando logar como "+ nome_face +". Esse usuário é você mesmo?");

        ImageView img = (ImageView) dialog.findViewById(R.id.img_usuario);
        Transformation transformation = new RoundedTransformationBuilder()
                .borderColor(Color.GRAY)
                .borderWidthDp(3)
                .cornerRadiusDp(70)
                .oval(false)
                .build();
        Picasso.with(getActivity())
                .load(url_face)
                .transform(transformation)
                .into(img);

        TextView tv = (TextView) dialog.findViewById(R.id.nome_usuario);
        tv.setText(nome_face);

        btn_cancelar_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //faz o logout do usuario logado facebook
                LoginManager.getInstance().logOut();

                mRecyclerView.setVisibility(View.GONE);
                pb.setVisibility(View.GONE);

                dialog.dismiss();
            }
        });

        btn_confirmar_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BancoControllerUsuario crud = new BancoControllerUsuario(getActivity());
                Cursor cursor = crud.carregaDados();
                String id_face_logado = cursor.getString(cursor.getColumnIndexOrThrow(CriandoBanco.ID_FACE));

                if(id_face_logado.equals("")){
                    atualizaDadosUsuario(id_face,url_face);
                }else{
                    listaAmigos();
                }


                dialog.dismiss();
            }
        });

        dialog.show();

    }

    public void listaAmigos(){

        Log.i("Script","listaAmigos");

        pb.setVisibility(View.GONE);

        if(mList.isEmpty() || mList == null){
            msg_lista_amigos.setVisibility(View.VISIBLE);
        }else {
            ListaAmigosAdapter adapter = new ListaAmigosAdapter(getActivity(), mList);
            adapter.setRecyclerViewOnClickListenerHack(this);
            mRecyclerView.setAdapter(adapter);
        }
    }

    public void atualizaDadosUsuario(String id, String url_picture){

        Log.i("PedirEmprestimo","Esta atualizando dados");

        //atualizamos os dados do usuario que esta no sqlite com os dados dele que acabaram de ser cadastrador - cpf e senha
        BancoControllerUsuario crud = new BancoControllerUsuario(getActivity());
        Cursor cursor = crud.carregaDados();
        String cpf = cursor.getString(cursor.getColumnIndexOrThrow(CriandoBanco.CPF));
        String nome = cursor.getString(cursor.getColumnIndexOrThrow(CriandoBanco.NOME));
        String senha = cursor.getString(cursor.getColumnIndexOrThrow(CriandoBanco.SENHA));
        String email = cursor.getString(cursor.getColumnIndexOrThrow(CriandoBanco.EMAIL));
        //String token_gcm = AllSharedPreferences.getPreferences(AllSharedPreferences.TOKEN_GCM,getActivity());


        Log.i("PedirEmprestimo","Esta atualizando dados cpf = "+cpf);

        Usuario usu = new Usuario();

        //para nao da problema
        usu.setNome(nome);
        usu.setEmail(email);
        usu.setSenha(senha);

        usu.setCpf(cpf);
        usu.setUrl_face(url_picture);
        usu.setId_face(id);

        //atualiza dados do face no banco sqlite
        crud.alteraRegistroFace(cpf,id,nome,url_picture,email,senha);

        //usuarioReferencia.child(cpf).child("url_face").setValue(url_picture);
        //usuarioReferencia.child(cpf).child("id_face").setValue(id);
        //usuarioReferencia.child(cpf).child("token_gcm").setValue(token_gcm);

        //fazemos a chamada a classe responsavel por realizar a tarefa de webservice em doinbackground
        new AtualizaUsuario(usu,this,"cpf").execute();

    }

    @Override
    public void retornoStringWebService(String result) {
        Log.i("PedirEMprestimo","Atualizado com sucesso, primeiro login face");

        if(result.equals("sucesso_edit")){
            listaAmigos();
        }else{
            mensagem("Houve um erro!","Olá, parece que houve um problema de conexao. Favor tente novamente!","Ok");
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("Facebook", "onActivitResult");
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClickListener(View view, final int position) {

        final Dialog dialog = new Dialog(getActivity(),R.style.AppThemeDialog);
        dialog.setContentView(R.layout.dialog_simulador_pedido);
        dialog.setTitle("Simulador Pedido");

        et_calendario = (EditText) dialog.findViewById(R.id.et_calendario);
        et_valor = (EditText) dialog.findViewById(R.id.et_valor);
        et_valor.addTextChangedListener(MaskMoney.insert(et_valor));

        et_valor.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ( (actionId == EditorInfo.IME_ACTION_DONE) || ((event.getKeyCode() == KeyEvent.KEYCODE_ENTER) && (event.getAction() == KeyEvent.ACTION_DOWN ))){
                    //Log.i("Script","apertou eba");
                    esconderTeclado();
                    mostraCalendario();
                    return true;
                }
                else{
                    return false;
                }
            }
        });

        et_calendario.setEnabled(false);

        Button btn_verificar = (Button) dialog.findViewById(R.id.btn_verificar);
        Button btn_voltar = (Button) dialog.findViewById(R.id.btn_voltar_simulador);

        ImageView img = (ImageView) dialog.findViewById(R.id.img_amigo);

        Transformation transformation = new RoundedTransformationBuilder()
                .borderColor(Color.GRAY)
                .borderWidthDp(3)
                .cornerRadiusDp(70)
                .oval(false)
                .build();

        Picasso.with(getActivity())
                .load(mList.get(position).getPicture().getData().getUrl())
                .transform(transformation)
                .into(img);

        TextView tv = (TextView) dialog.findViewById(R.id.nome_amigo);
        tv.setText(mList.get(position).getName());

        Button btnCalendar = (Button) dialog.findViewById(R.id.btnCalendar);
        btnCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                esconderTeclado();

                mostraCalendario();

            }
        });

        btn_voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btn_verificar.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {

                 String valor_normal = MaskMoney.removeMask(et_valor.getText().toString());

                 boolean campos = true;

                 boolean campo_valor = Validador.validateNotNull(et_valor.getText().toString());
                 if (!campo_valor) {
                     et_valor.setError("Campo vazio");
                     et_valor.setFocusable(true);
                     et_valor.requestFocus();

                     campos = false;
                 }
                 boolean campo_calendario = Validador.validateNotNull(et_calendario.getText().toString());
                 if (!campo_calendario) {
                     et_calendario.setError("Campo vazio");
                     et_calendario.setFocusable(true);
                     et_calendario.requestFocus();

                     campos = false;
                 }

                 if (campos) {

                     String valor_normal_ = valor_normal.substring(0, valor_normal.length() - 2);
                     if (Double.parseDouble(valor_normal_) > 10){
                         if (Double.parseDouble(valor_normal_) < 1001) {

                             DateTimeFormatter fmt = DateTimeFormat.forPattern("dd/MM/YYYY");
                             DateTime hoje = new DateTime();
                             DateTime vencimento = fmt.parseDateTime(et_calendario.getText().toString());

                             Days d = Days.daysBetween(hoje, vencimento);
                             dias_pagamento = d.getDays();

                             Intent it = new Intent(getActivity(), SimuladorResultado.class);
                             Bundle b = new Bundle();
                             b.putString("id", mList.get(position).getId());
                             b.putString("nome", removerAcentos(mList.get(position).getName()));
                             b.putString("valor", valor_normal);
                             b.putString("url_img", mList.get(position).getPicture().getData().getUrl());
                             b.putString("vencimento", et_calendario.getText().toString());
                             b.putInt("dias", dias_pagamento);
                             it.putExtras(b);
                             startActivity(it);

                             dialog.dismiss();
                         } else {
                             //Log.i("Scrip", "valor normal = " + valor_normal_);
                             mensagem("InBanker", "Olá, no momento só é permitido valores menores que R$ 1.000,00. Por favor insira um valor menor.", "Ok");
                         }
                    }else{
                         mensagem("InBanker", "Olá, no momento só é permitido valores maiores que R$ 10,00. Por favor insira um valor maior.", "Ok");

                     }
                 }
             }


         });
        dialog.show();

    }

    public void mostraCalendario(){

        //esconde o teclado para nao dar erro no calendario
        InputMethodManager inputMethodManager =(InputMethodManager)getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);

        // Process to get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        // Launch Date Picker Dialog
        DatePickerDialog dpd = new DatePickerDialog(getActivity(),
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        String data = checkDigit(dayOfMonth) + "/" + checkDigit((monthOfYear + 1)) + "/" + year;

                        // Display Selected date in textbox
                        et_calendario.setText(data);

                    }
                }, mYear, mMonth, mDay);

        Date today = new Date();
        Calendar c2 = Calendar.getInstance();
        c2.setTime(today);
        c2.add( Calendar.MONTH, 2 ); // add 2 months
        Calendar c3 = Calendar.getInstance();
        c3.setTime(today);
        c3.add( Calendar.DAY_OF_MONTH, 1 ); // add 1 day
        long minDate = c3.getTime().getTime(); // Twice!
        long maxDate = c2.getTime().getTime(); // Twice!

        dpd.getDatePicker().setMinDate(minDate);
        dpd.getDatePicker().setMaxDate(maxDate);
        dpd.show();
    }

    public void mensagem(String title,String content,String button)
    {
        AlertDialog.Builder mensagem = new AlertDialog.Builder(getActivity());
        mensagem.setTitle(title);
        mensagem.setMessage(content);
        mensagem.setNeutralButton(button,null);
        mensagem.show();
    }

    public String checkDigit(int number)
    {
        return number<=9?"0"+number:String.valueOf(number);
    }

    public void esconderTeclado() {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        //inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    public static String removerAcentos(String str) {
        return Normalizer.normalize(str, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    }
}
