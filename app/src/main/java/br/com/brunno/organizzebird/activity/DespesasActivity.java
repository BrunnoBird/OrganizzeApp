package br.com.brunno.organizzebird.activity;

import android.os.Bundle;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import br.com.brunno.organizzebird.R;
import br.com.brunno.organizzebird.helper.DateCustom;

public class DespesasActivity extends AppCompatActivity {
    private TextInputEditText campoData, campoCategoria, campoDescricao;
    private EditText campoValor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_despesas);

        campoValor = findViewById(R.id.editValor);
        campoData = findViewById(R.id.editData);
        campoDescricao = findViewById(R.id.editDescricao);
        campoCategoria = findViewById(R.id.editCategoria);

        //Preenche o campo data com uma data padrão
        campoData.setText(DateCustom.dataAtual());
    }
}