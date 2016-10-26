package br.com.appinbanker.inbanker;

import android.app.FragmentManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.squareup.picasso.Picasso;

import br.com.appinbanker.inbanker.entidades.Usuario;
import br.com.appinbanker.inbanker.sqlite.BancoControllerUsuario;
import br.com.appinbanker.inbanker.fragments_navigation.HistoricoFragment;
import br.com.appinbanker.inbanker.fragments_navigation.InicioFragment;
import br.com.appinbanker.inbanker.fragments_navigation.PagamentosFragment;
import br.com.appinbanker.inbanker.fragments_navigation.PedidosEnviadosFragment;
import br.com.appinbanker.inbanker.fragments_navigation.PedidosRecebidosFragment;
import br.com.appinbanker.inbanker.fragments_navigation.PedirEmprestimoFragment;
import br.com.appinbanker.inbanker.sqlite.CriandoBanco;

public class NavigationDrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final int MENU_INICIO = 0;
    public static final int MENU_PEDIR_EMPRESTIMO = 1;
    public static final int MENU_PEDIDOS_ENVIADOS = 2;
    public static final int MENU_PAGAMENTOS_ABERTO = 3;
    public static final int MENU_PEDIDOS_RECEBIDOS = 4;

    BancoControllerUsuario crud;
    Cursor cursor;
    Usuario usu;

    int menu = MENU_INICIO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //utilizamos para habilitar o logout quando clicar em sair no navigation drawer
        FacebookSdk.sdkInitialize(getApplicationContext());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_navigation_drawer); //app_bar_navigation_drawer.xml
        setSupportActionBar(toolbar);

        crud = new BancoControllerUsuario(getBaseContext());
        cursor = crud.carregaDados();
        usu = new Usuario();

        Intent it = getIntent();
        Bundle parametro = it.getExtras();
        if(parametro!=null){
            menu = parametro.getInt("menu_item");
        }

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab); //app_bar_navigation_drawer.xml
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout); //activity_navigation_drawer.xml
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //usamos para obter a view do header dentro do navigationdrawer e assim poder editar a foto e nome do perfil do usuario
        View header = navigationView.getHeaderView(0);

        String url = cursor.getString(cursor.getColumnIndexOrThrow(CriandoBanco.URL_IMG_FACE));
        String nome_usu_logado = cursor.getString(cursor.getColumnIndexOrThrow(CriandoBanco.NOME));

        TextView tv_nome_usu = (TextView) header.findViewById(R.id.tv_nome_usu_logado);
        tv_nome_usu.setText(nome_usu_logado);

        ImageView img_usu_logado = (ImageView) header.findViewById(R.id.img_usu_logado);
        Log.i("Facebook","url="+url);
        if(url != null)
            if(url.equals("")){}else{
                Picasso.with(getBaseContext()).load(url).into(img_usu_logado);}

       //para iniciar com o primeiro item do menu navigation drawer (Inicio)
        //se tiver tiver algum parametro o menu é alterado
        onNavigationItemSelected(navigationView.getMenu().getItem(menu).setChecked(true));

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation_drawer, menu);
        return true;
    }*/

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        Class fragmentClass;

        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch(id) {
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
                fragmentClass = PagamentosFragment.class;
                break;
            case R.id.nav_pedidos_recebidos:
                fragmentClass = PedidosRecebidosFragment.class;
                break;
            case R.id.nav_historico:
                fragmentClass = HistoricoFragment.class;
                break;
            /*case R.id.nav_configuracoes:
                //fragmentClass = ConfiguracoesFragment.class;
                break;
            case R.id.nav_ajuda:
                //fragmentClass = AjudaFragment.class;
                break;*/
            case R.id.nav_sair:

                //aparece aqui para nao dar erro no try
                fragmentClass = InicioFragment.class;

                //faz o logout do usuario logado
                LoginManager.getInstance().logOut();

                String cpf = cursor.getString(cursor.getColumnIndexOrThrow("cpf"));
                crud.deletaRegistro(cpf);

                Intent it = new Intent(NavigationDrawerActivity.this, Inicio.class);
                startActivity(it);

                //para encerrar a activity atual e todos os parent
                finishAffinity();

                break;
            default:
                fragmentClass = InicioFragment.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        // Highlight the selected item has been done by NavigationView
        item.setChecked(true);
        // Set action bar title
        setTitle(item.getTitle());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
