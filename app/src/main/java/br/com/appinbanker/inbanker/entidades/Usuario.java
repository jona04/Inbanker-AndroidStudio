package br.com.appinbanker.inbanker.entidades;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

/**
 * Created by Jonatas on 18/10/2016.
 */
@JsonPropertyOrder({
        "nome",
        "email",
        "cpf",
        "senha",
        "id_face",
        "nome_face",
        "url_face",
        "transacoes"
})
public class Usuario {

    @JsonProperty("nome")
    private String nome;
    @JsonProperty("email")
    private String email;
    @JsonProperty("cpf")
    private String cpf;
    @JsonProperty("senha")
    private String senha;

    @JsonProperty("id_face")
    private String idFace;

    @JsonProperty("nome_face")
    private String nomeFace;

    @JsonProperty("url_face")
    private String urlImgFace;

    @JsonProperty("transacoes")
    private List<Transacao> transacao;

    @JsonProperty("transacaoEnv")
    private List<Transacao> transacaoEnv;

    public void setTransacaoEnv(List<Transacao> transacoes_enviadas){this.transacaoEnv = transacoes_enviadas;}
    public List<Transacao> getTransacaoEnv(){return this.transacaoEnv;}

    public void setTransacao(List<Transacao> trans){this.transacao = trans;}
    public List<Transacao> getTransacao(){return this.transacao;}

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
    public void setIdFace(String idFace){
        this.idFace = idFace;
    }
    public void setNomeFace(String nome_face){
        this.nomeFace = nome_face;
    }
    public void setUrlImgFace(String url_img){
        this.urlImgFace = url_img;
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
    public String getIdFace(){
        return this.idFace;
    }
    public String getNomeFace(){
        return this.nomeFace;
    }
    public String getUrlImgFace(){
        return this.urlImgFace;
    }

}
