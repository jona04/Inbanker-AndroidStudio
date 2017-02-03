package br.com.appinbanker.inbanker;

import android.app.FragmentManager;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.io.IOException;

import br.com.appinbanker.inbanker.entidades.Usuario;
import br.com.appinbanker.inbanker.sqlite.BancoControllerUsuario;
import br.com.appinbanker.inbanker.fragments_navigation.HistoricoFragment;
import br.com.appinbanker.inbanker.fragments_navigation.InicioFragment;
import br.com.appinbanker.inbanker.fragments_navigation.PagamentosPendentesFragment;
import br.com.appinbanker.inbanker.fragments_navigation.PedidosEnviadosFragment;
import br.com.appinbanker.inbanker.fragments_navigation.PedidosRecebidosFragment;
import br.com.appinbanker.inbanker.fragments_navigation.PedirEmprestimoFragment;
import br.com.appinbanker.inbanker.sqlite.CriandoBanco;
import br.com.appinbanker.inbanker.util.AllSharedPreferences;
import br.com.appinbanker.inbanker.webservice.AtualizaTokenGcm;

public class NavigationDrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final int MENU_INICIO = 0;
    public static final int MENU_PEDIR_EMPRESTIMO = 2;
    public static final int MENU_PEDIDOS_ENVIADOS = 3;
    public static final int MENU_PAGAMENTOS_ABERTO = 4;
    public static final int MENU_PEDIDOS_RECEBIDOS = 5;
    public static final int MENU_HISTORICO = 6;

    int menu = MENU_INICIO;

    // Create a new fragment and specify the fragment to show based on nav item clicked
    Fragment fragment = null;
    Class fragmentClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //utilizamos para habilitar o logout quando clicar em sair no navigation drawer
        FacebookSdk.sdkInitialize(getApplicationContext());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_navigation_drawer); //app_bar_navigation_drawer.xml
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout); //activity_navigation_drawer.xml
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //usamos para obter a view do header dentro do navigationdrawer e assim poder editar a foto e nome do perfil do usuario
        View header = navigationView.getHeaderView(0);

        Intent it = getIntent();
        Bundle parametro = it.getExtras();
        Log.i("ParametroNavigation","Param 1 ="+parametro);
        if(parametro!=null){
            Log.i("ParametroNavigation","Param 2 ="+parametro);
            menu = parametro.getInt("menu_item");
        }

        BancoControllerUsuario crud = new BancoControllerUsuario(getBaseContext());
        Cursor cursor = crud.carregaDados();

        if(cursor.getCount() > 0) {
            String url = cursor.getString(cursor.getColumnIndexOrThrow(CriandoBanco.URL_IMG_FACE));
            String nome_usu_logado = cursor.getString(cursor.getColumnIndexOrThrow(CriandoBanco.NOME));

            TextView tv_nome_usu = (TextView) header.findViewById(R.id.tv_nome_usu_logado);
            tv_nome_usu.setText(nome_usu_logado);

            ImageView img_usu_logado = (ImageView) header.findViewById(R.id.img_usu_logado);
            //Log.i("Facebook","url="+url);

            Log.i("ParametroNavigation","url 1 ="+url);

            if (url != null)
                if (url.equals("")) {
                } else {
                    Transformation transformation = new RoundedTransformationBuilder()
                            .borderColor(Color.GRAY)
                            .borderWidthDp(3)
                            .cornerRadiusDp(70)
                            .oval(false)
                            .build();

                    Picasso.with(getBaseContext())
                            .load(url)
                            .transform(transformation)
                            .into(img_usu_logado);
                }
        }
       //para iniciar com o primeiro item do menu navigation drawer (Inicio)
        //se tiver tiver algum parametro o menu é alterado
        onNavigationItemSelected(navigationView.getMenu().getItem(menu).setChecked(true));

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            Log.i("NavigationDrawer","teste1");
            drawer.closeDrawer(GravityCompat.START);

        //se nao estiver na tela inicial, dentro do navigationdrawer, damos um popstack no fragment e redirecionamos para a tela inicial
        }else {
            Log.i("NavigationDrawer","teste4");
            if(fragmentClass == InicioFragment.class){
                Log.i("NavigationDrawer","teste5");
                super.onBackPressed();
            }else{

                try {
                    fragmentClass = InicioFragment.class;
                    fragment = (Fragment) fragmentClass.newInstance();
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
                }catch (Exception e){
                    Log.e("Excpetion","Botao voltar navgation excetopn = "+e);
                }
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (id == R.id.menu_ajuda) {

            Intent it = new Intent(this,Ajuda.class);
            startActivity(it);

            return true;
        }else if (id == R.id.menu_minha_conta) {

            Intent it = new Intent(this,MinhaConta.class);
            startActivity(it);

            return true;
        }else if (id == R.id.menu_sair) {

            Log.i("Script","menu sair");

            //faz o logout do usuario logado facebook
            LoginManager.getInstance().logOut();

            BancoControllerUsuario crud = new BancoControllerUsuario(getBaseContext());
            Cursor cursor = crud.carregaDados();

            //deleta registro do usuario no sqlite
            String cpf = cursor.getString(cursor.getColumnIndexOrThrow("cpf"));
            crud.deletaRegistro(cpf);

            //deleta o token do usuario do banco de dados
            String device_id = AllSharedPreferences.getPreferences(AllSharedPreferences.DEVICE_ID, NavigationDrawerActivity.this);
            //String token = AllSharedPreferences.getPreferences(AllSharedPreferences.TOKEN_GCM,NavigationDrawerActivity.this);
            Usuario usu = new Usuario();
            usu.setDevice_id(device_id);
            usu.setToken_gcm("");
            usu.setCpf(cpf);

            new AtualizaTokenGcm(usu).execute();


            Intent it = new Intent(NavigationDrawerActivity.this, Inicio.class);
            startActivity(it);

            //para encerrar a activity atual e todos os parent
            finishAffinity();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_inicio:
                fragmentClass = InicioFragment.class;
                break;
            case R.id.nav_pedir_emprestimo:
                fragmentClass = PedirEmprestimoFragment.class;
                break;
            case R.id.nav_pedidos_enviados:
                fragmentClass = PedidosEnviadosFragment.class;
                break;
            case R.id.nav_pagamento:
                fragmentClass = PagamentosPendentesFragment.class;
                break;
            case R.id.nav_pedidos_recebidos:
                fragmentClass = PedidosRecebidosFragment.class;
                break;
            case R.id.nav_historico:
                fragmentClass = HistoricoFragment.class;
                break;
            case R.id.nav_minha_conta:
                Intent intent = new Intent(this,MinhaConta.class);
                startActivity(intent);
                break;
            case R.id.nav_ajuda:
                Intent intent2 = new Intent(this,Ajuda.class);
                startActivity(intent2);
                break;
            case R.id.nav_sair:

                //fragment qualquer para nao dar erro do try
                fragmentClass = InicioFragment.class;

                //faz o logout do usuario logado facebook
                LoginManager.getInstance().logOut();

                BancoControllerUsuario crud = new BancoControllerUsuario(getBaseContext());
                Cursor cursor = crud.carregaDados();

                //deleta registro do usuario no sqlite
                String cpf = cursor.getString(cursor.getColumnIndexOrThrow("cpf"));
                crud.deletaRegistro(cpf);

                //deleta o token do usuario do banco de dados
                String device_id = AllSharedPreferences.getPreferences(AllSharedPreferences.DEVICE_ID, NavigationDrawerActivity.this);
                //String token = AllSharedPreferences.getPreferences(AllSharedPreferences.TOKEN_GCM,NavigationDrawerActivity.this);
                Usuario usu = new Usuario();
                usu.setDevice_id(device_id);
                usu.setToken_gcm("");
                usu.setCpf(cpf);

                new AtualizaTokenGcm(usu).execute();


                Intent it = new Intent(NavigationDrawerActivity.this, Inicio.class);
                startActivity(it);

                //para encerrar a activity atual e todos os parent
                finishAffinity();

                break;
            default:
                //colocamos o historico para nao dar erro no inicio quando apertamos em sair
                fragmentClass = InicioFragment.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();


        //os fragments do navigationdrawer, com execessao do inicio, sao adicionados ao backstack
        /*if (inicio) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        } else{
            // Insert the fragment by replacing any existing fragment
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).addToBackStack(item.getTitle().toString()).commit();
        }*/
        // Highlight the selected item has been done by NavigationView
        item.setChecked(true);
        // Set action bar title
        setTitle(item.getTitle());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
