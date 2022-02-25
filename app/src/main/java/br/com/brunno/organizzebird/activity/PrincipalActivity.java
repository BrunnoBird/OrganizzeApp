package br.com.brunno.organizzebird.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
//import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.text.DecimalFormat;

import br.com.brunno.organizzebird.R;
import br.com.brunno.organizzebird.config.ConfiguracaoFirebase;
import br.com.brunno.organizzebird.model.Usuario;

public class PrincipalActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    private DatabaseReference usuarioRef;
    private ValueEventListener valueEventListenerUsuario;
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

        configuraCalendarView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Anexar o Evento
        recuperaResumo();
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

        calendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {

            }
        });
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
    }
}