package br.com.brunno.organizzebird.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import br.com.brunno.organizzebird.R;
import br.com.brunno.organizzebird.config.ConfiguracaoFirebase;
import br.com.brunno.organizzebird.helper.Base64Custom;
import br.com.brunno.organizzebird.helper.DateCustom;
import br.com.brunno.organizzebird.model.Movimentacao;
import br.com.brunno.organizzebird.model.Usuario;

public class ReceitasActivity extends AppCompatActivity {
    private TextInputEditText campoData, campoCategoria, campoDescricao;
    private EditText campoValor;
    private DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private Double receitaTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receitas);

        campoValor = findViewById(R.id.editValorReceita);
        campoData = findViewById(R.id.editDataReceita);
        campoDescricao = findViewById(R.id.editDescricaoReceita);
        campoCategoria = findViewById(R.id.editCategoriaReceita);
        FloatingActionButton fab = findViewById(R.id.fabSalvarReceita);

        campoData.setText(DateCustom.dataAtual());

        recuperaReceitaTotal();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salvarReceita(v);
            }
        });
    }

    private Boolean validarCamposReceita(){
        String textoValor = campoValor.getText().toString();
        String textoData = campoData.getText().toString();
        String textoCategoria = campoCategoria.getText().toString();
        String textoDescricao = campoDescricao.getText().toString();

        if (textoValor.length() > 0) {
            if (!textoData.isEmpty()) {
                if (!textoCategoria.isEmpty()) {
                    if (!textoDescricao.isEmpty()) {
                        return true;
                    } else {
                        Toast.makeText(ReceitasActivity.this, "Descricao não foi preenchida!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ReceitasActivity.this, "Categoria não foi preenchida!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(ReceitasActivity.this, "Data não foi preenchida!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(ReceitasActivity.this, "Valor não foi preenchido!", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public void atualizarReceita(Double receita) {
        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64(emailUsuario);
        DatabaseReference usuarioRef = firebaseRef.child("usuarios").child(idUsuario);

        usuarioRef.child("receitaTotal").setValue(receita);
    }

    public void salvarReceita(View view) {
        if( validarCamposReceita() ){
            String data = campoData.getText().toString();
            Double valorRecuperado = Double.parseDouble(campoValor.getText().toString());

            Movimentacao movimentacao = new Movimentacao();
            movimentacao.setValor( valorRecuperado );
            movimentacao.setCategoria( campoCategoria.getText().toString() );
            movimentacao.setDescricao( campoDescricao.getText().toString() );
            movimentacao.setData( data );
            movimentacao.setTipo( "r" );

            Double receitaAtualizada = receitaTotal + valorRecuperado;
            atualizarReceita( receitaAtualizada );

            movimentacao.salvar(data);
            finish();
        }
    }

    public void recuperaReceitaTotal() {
        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64(emailUsuario);
        DatabaseReference usuarioRef = firebaseRef.child("usuarios").child(idUsuario);

        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario usuario = snapshot.getValue( Usuario.class );
                receitaTotal = usuario.getReceitaTotal();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}