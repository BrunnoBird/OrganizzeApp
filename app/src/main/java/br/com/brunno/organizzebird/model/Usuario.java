package br.com.brunno.organizzebird.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import br.com.brunno.organizzebird.config.ConfiguracaoFirebase;

public class Usuario {
    private String idUsuario;
    private String nome;
    private String email;
    private String senha;
    private Double receitaTotal = 0.00;
    private Double despesaTotal = 0.00;

    //Não preciso alimentar o contrutor pois já consigo alimentar os atributos diretamente pelo setter
    public Usuario() {
    }



    //Methods
    public void salvar(){
        DatabaseReference firebase = ConfiguracaoFirebase.getFirebaseDatabase();
        firebase.child("usuarios")//Acessando o nó
                .child(this.idUsuario) //acessando o ID do usuario
                .setValue( this);//salvando o objeto usuario que já setamos os valures na classe.
    }

    //Getters e Setters
        //@Exclude faz com que este dado seja desconsiderado para salvar no firebase
    @Exclude
    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    //@Exclude faz com que este dado seja desconsiderado para salvar no firebase
    @Exclude
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Double getReceitaTotal() {
        return receitaTotal;
    }

    public void setReceitaTotal(Double receitaTotal) {
        this.receitaTotal = receitaTotal;
    }

    public Double getDespesaTotal() {
        return despesaTotal;
    }

    public void setDespesaTotal(Double despesaTotal) {
        this.despesaTotal = despesaTotal;
    }

}
