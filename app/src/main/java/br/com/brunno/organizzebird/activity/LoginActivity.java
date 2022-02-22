package br.com.brunno.organizzebird.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import br.com.brunno.organizzebird.R;
import br.com.brunno.organizzebird.config.ConfiguracaoFirebase;
import br.com.brunno.organizzebird.model.Usuario;

public class LoginActivity extends AppCompatActivity {

    private EditText campoEmail, campoSenha;
    private Button botaoEntrar;
    private Usuario usuario;
    private FirebaseAuth autenticacao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        campoEmail = findViewById(R.id.editEmailLogin);
        campoSenha = findViewById(R.id.editSenhaLogin);
        botaoEntrar = findViewById(R.id.buttonEntrarLogin);

        botaoEntrar.setOnClickListener(v -> {

            String textoEmail = campoEmail.getText().toString();
            String textoSenha = campoSenha.getText().toString();

            if ( !textoEmail.isEmpty() ) {
                if ( !textoSenha.isEmpty() ){

                    usuario = new Usuario();
                    usuario.setEmail( textoEmail );
                    usuario.setSenha( textoSenha );
                    validarLogin();

                } else {
                    Toast.makeText(LoginActivity.this, "Preencha a sua senha!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(LoginActivity.this, "Preencha o seu login!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void validarLogin() {
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.signInWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()
        ).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                abrirTelaPrincipal();
            } else {

                String excecao = "";
                try {
                    throw task.getException();
                } catch ( FirebaseAuthInvalidUserException e) {
                    excecao = "Usuário não está cadastrado";
                } catch ( FirebaseAuthInvalidCredentialsException e) {
                    excecao = "E-mail e senha não correspondem a um usuário cadastrado!";
                } catch ( Exception e ) {
                    excecao = "Erro ao efetuar login!";
                    e.printStackTrace();
                }

                Toast.makeText(LoginActivity.this, excecao, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void abrirTelaPrincipal() {
        startActivity(new Intent(this, PrincipalActivity.class));
        finish();
    }
}