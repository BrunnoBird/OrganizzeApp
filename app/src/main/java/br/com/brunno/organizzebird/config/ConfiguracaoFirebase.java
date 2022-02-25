package br.com.brunno.organizzebird.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import br.com.brunno.organizzebird.helper.Base64Custom;

//Usando sempre a mesma instancia de firebase para não criar sempre uma nova instancia
public class ConfiguracaoFirebase {

    private static FirebaseAuth autenticacao;
    private static DatabaseReference firebaseDatabase;

    //retorna a instancia do FirebaseAuth
    public static FirebaseAuth getFirebaseAutenticacao() {
        //Se nós tivermos uma instancia de autenticação ele vai retornar ela diretamente, e não criará uma nova
        if ( autenticacao == null ) {
            autenticacao = FirebaseAuth.getInstance();
        }
        return autenticacao;
    }

    //Retorna a instancia do FirebaseDatabase
    public static DatabaseReference getFirebaseDatabase() {
        if(firebaseDatabase == null) {
            firebaseDatabase = FirebaseDatabase.getInstance().getReference();
        }
        return firebaseDatabase;
    }

    public static DatabaseReference getReferenceUser() {
        String idUsuario = getIDUsuario();
        DatabaseReference firebaseRef = getFirebaseDatabase();
        DatabaseReference usuarioRef = firebaseRef.child("usuarios").child(idUsuario);
        return usuarioRef;
    }

    public static String getIDUsuario() {
        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64(emailUsuario);
        return idUsuario;
    }
}
