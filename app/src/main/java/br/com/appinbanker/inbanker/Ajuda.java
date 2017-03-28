package br.com.appinbanker.inbanker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ExpandableListView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.com.appinbanker.inbanker.adapters.AjudaAdapter;
import br.com.appinbanker.inbanker.entidades.Transacao;
import br.com.appinbanker.inbanker.entidades.Usuario;

public class Ajuda extends AppCompatActivity {

    private AjudaAdapter listAdapter;
    ArrayList<br.com.appinbanker.inbanker.entidades.Ajuda> listDataHeader;
    HashMap<String, br.com.appinbanker.inbanker.entidades.Ajuda> listDataChild;

    List<br.com.appinbanker.inbanker.entidades.Ajuda> mList;

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference ajudaReferencia = databaseReference.child("ajuda");
    //private DatabaseReference usuarioReferencia = databaseReference.child("usuarios");

    private ExpandableListView exp_ajuda;
    private int lastExpandedPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_ajuda);

        mList = new ArrayList<>();

        exp_ajuda = (ExpandableListView) findViewById(R.id.expanded_ajuda);

        br.com.appinbanker.inbanker.entidades.Ajuda ajuda = new br.com.appinbanker.inbanker.entidades.Ajuda();
        ajuda.setPergunta("O que é Contrato de Mútuo?");
        ajuda.setResposta("É um contrato que trata da transferência de bens fungíveis, móveis, que podem ser substituídos por outros de mesma espécie, qualidade e quantidade." +
                " No caso de empréstimo de dinheiro, aquele que o toma emprestado pagará àquele que lhe emprestou juros em forma de uma compensação por estar utilizando o dinheiro que é de propriedade do outro.");

        br.com.appinbanker.inbanker.entidades.Ajuda ajuda2 = new br.com.appinbanker.inbanker.entidades.Ajuda();
        ajuda2.setPergunta("Isso é \"agiotagem\"?");
        ajuda2.setResposta("Agiotagem é o empréstimo de dinheiro a juros SUPERIORES à taxa legal estabelecida em Lei.\n" +
                " Os Contratos de Mútuo estabelecidos e administrados pelo InBanker, seguem rigorosamente todos requisitos da legislação brasileira. As Taxas de Juros, Multas e Juros de Mora, serão cobrados de forma legal, respeitando todos os limites estabelecidas em Lei.");

        br.com.appinbanker.inbanker.entidades.Ajuda ajuda3 = new br.com.appinbanker.inbanker.entidades.Ajuda();
        ajuda3.setPergunta("Quais taxas de Juros serão cobradas?");
        ajuda3.setResposta("Valores permitidos: R$20,00 - R$1.000,00 \n " +
                "Juros renuneratórios: 1,99% A.M. \n " +
                "Multa por atraso: 2% \n" +
                "Juros moratórios: 1%");

        br.com.appinbanker.inbanker.entidades.Ajuda ajuda4 = new br.com.appinbanker.inbanker.entidades.Ajuda();
        ajuda4.setPergunta("Serão cobrados Juros Compostos?");
        ajuda4.setResposta("Não, os juros remuneratórios cobrados sobre o valor dos empréstimos será o chamado Juros Simples, o que oferece ao mutuário uma cobrança de juros justa, sem o perigo de haver um descontrole financeiro em suas contas pessoais, aumentando a garantia de retorno do valor ao investidor, tendo em vista que os Juros Compostos cobrados pelos Bancos e Cartões de Crédito, são os grandes responsáveis pelo endividamento de grande parte dos brasileiros, pois cobram Juros sobre Juros que somados em alguns casos, chegam a 300% a.a.");

        br.com.appinbanker.inbanker.entidades.Ajuda ajuda5 = new br.com.appinbanker.inbanker.entidades.Ajuda();
        ajuda5.setPergunta("Porque o vencimento é de apenas 60 dias?");
        ajuda5.setResposta("Inicialmente iremos trabalhar com empréstimos de pequenos valores e com vencimentos de curto prazo, garantindo segurança para quem quer investir, tendo em vista que, todos nós estamos sujeitos a passar por eventualidades, o que pode levar os mutuários a não conseguir cumprir com as obrigações contratuais, quando se trata de empréstimos à longos prazos. \n" +
                " Entretanto, nossa equipe está trabalhando para buscar meios mais seguros e menos burocráticos para suprir todas as demandas de nossos usuários.");

        br.com.appinbanker.inbanker.entidades.Ajuda ajuda6 = new br.com.appinbanker.inbanker.entidades.Ajuda();
        ajuda6.setPergunta("Como vocês são remunerados?");
        ajuda6.setResposta("O Download do Aplicativo InBanker, bem como o cadastro são feitos de forma gratuita. Nós cobramos apenas uma taxa de administração única de 0,99% sobre o valor de cada contrato que será administrado. Os valores auferidos pela cobrança da taxa de administração, são em sua grande parte reinvestidos na empresa, para que seja possível dar cada vez mais segurança e transparência aos nossos usuários.");

        br.com.appinbanker.inbanker.entidades.Ajuda ajuda7 = new br.com.appinbanker.inbanker.entidades.Ajuda();
        ajuda7.setPergunta("Como será paga a Taxa de Administração?");
        ajuda7.setResposta("No ato da confirmação do pedido de empréstimo, o valor da taxa de serviço será debitada no cartão de crédito cadastrado pelo usuário.\n" +
                " Caso não seja possível debitar a totalidade do valor devido, seja por motivo de expiração, fundos insuficientes ou outro, o usuário continuará responsável pela totalidade de valores a pagar não cobrados, assim nós o notificaremos e apresentaremos outras formas de pagamento.");



        mList.add(ajuda);
        mList.add(ajuda2);
        mList.add(ajuda3);
        mList.add(ajuda4);
        mList.add(ajuda5);
        mList.add(ajuda6);
        mList.add(ajuda7);


        setValue(mList);


        listAdapter = new AjudaAdapter(this,listDataHeader, listDataChild);
        // setting list adapter
        exp_ajuda.setAdapter(listAdapter);

        exp_ajuda.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                if (lastExpandedPosition != -1
                        && groupPosition != lastExpandedPosition) {
                    exp_ajuda.collapseGroup(lastExpandedPosition);
                }
                lastExpandedPosition = groupPosition;
            }
        });
    }

    private void setValue(List<br.com.appinbanker.inbanker.entidades.Ajuda> forums) {

        //List generalList = new ArrayList();
        Transacao f = new Transacao();

        listDataHeader = new ArrayList<br.com.appinbanker.inbanker.entidades.Ajuda>();
        listDataChild = new HashMap<String, br.com.appinbanker.inbanker.entidades.Ajuda>();

        for (int i = 0; i < forums.size(); i++) {

            listDataHeader.add(forums.get(i));

            listDataChild.put(listDataHeader.get(i).getPergunta(), forums.get(i));
        }

    }

}
