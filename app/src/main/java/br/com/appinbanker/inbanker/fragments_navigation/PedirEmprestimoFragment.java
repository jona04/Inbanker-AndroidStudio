package br.com.appinbanker.inbanker.fragments_navigation;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import br.com.appinbanker.inbanker.R;
import br.com.appinbanker.inbanker.SimuladorPedido;
import br.com.appinbanker.inbanker.adapters.ListaAmigosAdapter;
import br.com.appinbanker.inbanker.entidades.Amigos;
import br.com.appinbanker.inbanker.entidades.Usuario;
import br.com.appinbanker.inbanker.interfaces.RecyclerViewOnClickListenerHack;
import br.com.appinbanker.inbanker.sqlite.BancoControllerUsuario;
import br.com.appinbanker.inbanker.sqlite.CriandoBanco;
import br.com.appinbanker.inbanker.webservice.AtualizaUsuario;

public class PedirEmprestimoFragment extends Fragment implements RecyclerViewOnClickListenerHack {

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private ArrayList<Amigos> mList;
    private LinearLayout pb,msg_lista_amigos;

    private LoginButton loginButton;
    private CallbackManager callbackManager;

    private boolean usuario_logado = false;

    //atualizamos os dados do usuario que esta no sqlite com os dados dele que acabaram de ser logados no facebook
    BancoControllerUsuario crud;

    public PedirEmprestimoFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
        }

        crud = new BancoControllerUsuario(getActivity());

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
                    Log.i("Facebook","logando accestoken = "+AccessToken.getCurrentAccessToken());

                    //utilizamos para deixar a lista no modo hide
                    usuario_logado = true;

                    graphFacebook(AccessToken.getCurrentAccessToken());
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


        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //Log.i("Script", "onScrollStateChanged");
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //Log.i("Script", "onScrolled");
            }
        });
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
                mensagem();

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

                                mensagem();
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

                            String id = object.getString("id");
                            //String email = object.getString("email");
                            String name = object.getString("name");

                            JSONObject pic = object.getJSONObject("picture");
                            pic = pic.getJSONObject("data");
                            String url_picture = pic.getString("url");

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

                            listaAmigos(id,url_picture,name);
                        }
                        catch(Exception e){
                            Log.i("Facebook","exception = "+e);

                            mensagem();
                            mRecyclerView.setVisibility(View.GONE);
                            pb.setVisibility(View.GONE);
                        }
                    }
                }
        ).executeAsync();

    }

    public void listaAmigos(String id, String url, String name){

        Log.i("Facebook","metodo Lista amigos");

        pb.setVisibility(View.GONE);

        if(mList.isEmpty() || mList == null){
            msg_lista_amigos.setVisibility(View.VISIBLE);
        }else {
            ListaAmigosAdapter adapter = new ListaAmigosAdapter(getActivity(), mList);
            adapter.setRecyclerViewOnClickListenerHack(this);
            mRecyclerView.setAdapter(adapter);
        }
        //atualizamos os dados do usuario logado caso seja o primeiro login dele no face
        Cursor cursor = crud.carregaDados();
        String id_face = cursor.getString(cursor.getColumnIndexOrThrow(CriandoBanco.ID_FACE));
        Log.i("Facebook","id_face = "+id_face);
        if(id_face == null || id_face.equals(""))
            atualizaDadosUsuario(id,url,name);

    }

    public void atualizaDadosUsuario(String id, String url_picture,String name){

        //atualizamos os dados do usuario que esta no sqlite com os dados dele que acabaram de ser logados no facebook
        BancoControllerUsuario crud = new BancoControllerUsuario(getActivity());
        Cursor cursor = crud.carregaDados();
        String cpf = cursor.getString(cursor.getColumnIndexOrThrow(CriandoBanco.CPF));
        crud.alteraRegistroFace(cpf,id,name,url_picture);

        Usuario usu = new Usuario();

        //para nao da problema
        usu.setNome("");
        usu.setEmail("");
        usu.setSenha("");

        usu.setCpf(cpf);
        usu.setUrlImgFace(url_picture);
        usu.setNomeFace(name);
        usu.setIdFace(id);

        //fazemos a chamada a classe responsavel por realizar a tarefa de webservice em doinbackground
        new AtualizaUsuario(usu,PedirEmprestimoFragment.this).execute();

    }

    public void retornoAtualizaUsuario(String result){
        Log.i("Webservice","retorno = "+result);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("Facebook", "onActivitResult");
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onClickListener(View view, int position) {

        Log.i("Script", "Click tste inicio =" + mList.get(position).getName());

        Intent it = new Intent(getActivity(), SimuladorPedido.class);
        Bundle b = new Bundle();
        b.putString("id",mList.get(position).getId());
        b.putString("nome",mList.get(position).getName());
        b.putString("url_img",mList.get(position).getPicture().getData().getUrl());
        it.putExtras(b);
        startActivity(it);

    }

    public void mensagem()
    {
        AlertDialog.Builder mensagem = new AlertDialog.Builder(getActivity());
        mensagem.setTitle("Houve um erro!");
        mensagem.setMessage("Olá, parece que houve um problema de conexao. Favor tente novamente!");
        mensagem.setNeutralButton("OK",null);
        mensagem.show();
    }

}
