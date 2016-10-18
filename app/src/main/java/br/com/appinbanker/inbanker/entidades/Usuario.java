package br.com.appinbanker.inbanker.entidades;

/**
 * Created by Jonatas on 18/10/2016.
 */

public class Usuario {

    private String nome;
    private String email;
    private String cpf;
    private String senha;

    public void setNome(String nome){
        this.nome = nome;
    }
    public void setEmail(String email){
        this.email = email;
    }
    public void setCpf(String cpf){
        this.cpf = cpf;
    }
    public void setSenha(String senha){
        this.senha = senha;
    }

    public String getNome(){
        return this.nome;
    }
    public String getEmail(){
        return this.email;
    }
    public String getCpf(){
        return this.cpf;
    }
    public String getSenha(){
        return this.senha;
    }

}
