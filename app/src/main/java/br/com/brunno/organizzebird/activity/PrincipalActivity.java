package br.com.brunno.organizzebird.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import br.com.brunno.organizzebird.R;
import br.com.brunno.organizzebird.adapter.AdapterMovimentacao;
import br.com.brunno.organizzebird.config.ConfiguracaoFirebase;
import br.com.brunno.organizzebird.model.Movimentacao;
import br.com.brunno.organizzebird.model.Usuario;

public class PrincipalActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    private DatabaseReference usuarioRef;
    private ValueEventListener valueEventListenerUsuario;
    private ValueEventListener valueEventListenerMovimentacoes;

    private RecyclerView recyclerView;
    private AdapterMovimentacao adapterMovimentacao;
    //Criando uma lista de movimentações com um array vazio
    private List<Movimentacao> movimentacoes = new ArrayList<>();
    private DatabaseReference firebaseDatabase = ConfiguracaoFirebase.getFirebaseDatabase();
    private DatabaseReference movimentacaoRef;
    private String mesAnoSelecionado;

    private MaterialCalendarView calendarView;
    private TextView textoSaudacao, textoSaldo;
    private Toolbar toolbar;
    private Double despesaTotal = 0.0;
    private Double receitaTotal = 0.0;
    private Double resumoUsuario = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        toolbar = findViewById(R.id.toolbarPrincipal);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Organizze");

        textoSaldo = findViewById(R.id.textSaldo);
        textoSaudacao = findViewById(R.id.textSaudacao);
        calendarView = findViewById(R.id.calendarView);

        recyclerView = findViewById(R.id.recyclerMovimentos);

        configuraCalendarView();
        configuraRecyclerViewMovimentacoes();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Anexar o Evento, Para que quando limparmos no STOP ao retornar, as funções serem chamadas novamente. pois do onStop é retornado para o onStart Novamente!
        recuperaResumo();
        recuperarMovimentacoes();
    }

    public void recuperarMovimentacoes() {
        String idUsuario = ConfiguracaoFirebase.getIDUsuario();
        movimentacaoRef = firebaseDatabase.child("movimentacao")
                .child( idUsuario )
                .child( mesAnoSelecionado );
        //Adicionando um listener para sempre buscar as informacoes da movimentação no banco de dados
        valueEventListenerMovimentacoes = movimentacaoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Comecamos limpando nossa lista
                movimentacoes .clear();

                //Percorrendo todas as movimentacoes pertencentes a data
                    //getValue pega o objeto inteiro;
                    //getChildren pega todos os filhos de dataSnapshot -> que no caso é todas as minhas movimentacoes;
                for ( DataSnapshot dados: snapshot.getChildren() ) {
                    Movimentacao movimentacao = dados.getValue( Movimentacao.class );

                    //Adicionando no Array as movimentações buscadas
                    movimentacoes.add( movimentacao );
                }

                //Notificando para avisar que os dados foram avisados para o adapter, onde fazemos uso dele via parametro das nossas listas de movimentacoes
                adapterMovimentacao.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void recuperaResumo(){
        //Criando a referencia para o usuario
        usuarioRef = ConfiguracaoFirebase.getReferenceUser();
        //Listener para escutar os alterações das alterações
        valueEventListenerUsuario = usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Recuperar os dados do usuario
                Usuario usuario = snapshot.getValue( Usuario.class );

                despesaTotal = usuario.getDespesaTotal();
                receitaTotal = usuario.getReceitaTotal();
                resumoUsuario = receitaTotal - despesaTotal;

                //Formatando o valor do R$
                DecimalFormat decimalFormat = new DecimalFormat("0.##");
                String resultadoFormatado = decimalFormat.format( resumoUsuario );

                textoSaudacao.setText( "Olá, " + usuario.getNome() );
                textoSaldo.setText( "R$ " + resultadoFormatado );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //GetMenuInflater -> Converte meu XLM em uma view para o usar
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuSair:
                handleSignOut();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void handleSignOut() {
        autenticacao.signOut();

        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void configuraCalendarView() {
        CharSequence[] meses = {"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};
        calendarView.setTitleMonths(meses);

        //Pegando a data Atual selecionada pelo usuario
        CalendarDay dataAtual = calendarView.getCurrentDate();
        //% -> indica que é uma formacao
        //0 -> ele irá adicionar 0
        //2 -> diz que só tem 2 casas sempre para essa formacao
        //d -> diz que é um digito
        String mesSelecionado = String.format("%02d", dataAtual.getMonth());
        mesAnoSelecionado = String.valueOf( mesSelecionado + "" + dataAtual.getYear());

        calendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                //Método chamado apenas quando modificarmos o mês
                String mesSelecionado = String.format("%02d", date.getMonth());
                mesAnoSelecionado = String.valueOf(mesSelecionado + "" + date.getYear());

                //Limpamos o evento que já estava adicionado do firebase
                movimentacaoRef.removeEventListener( valueEventListenerMovimentacoes );
                //Recuperando novamente as movimentações
                recuperarMovimentacoes();
            }
        });
    }

    private void configuraRecyclerViewMovimentacoes() {
        //Configurar um adapter
        adapterMovimentacao = new AdapterMovimentacao(movimentacoes, this);
        //Configurar RecyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapterMovimentacao);
    }

    public void adicionarReceita(View view) {
        startActivity(new Intent(this, ReceitasActivity.class));
    }

    public void adicionarDespesa(View view) {
        startActivity(new Intent(this, DespesasActivity.class));
    }

    //Chamado quando meu APP não estiver sendo utilizado
    @Override
    protected void onStop() {
        super.onStop();
        //Fechando o listener do firebase para evitar memory leak e afins
        usuarioRef.removeEventListener(valueEventListenerUsuario);
        movimentacaoRef.removeEventListener(valueEventListenerMovimentacoes);
    }
}