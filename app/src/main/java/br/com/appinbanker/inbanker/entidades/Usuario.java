package br.com.appinbanker.inbanker.entidades;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

/**
 * Created by Jonatas on 18/10/2016.
 */
@JsonIgnoreProperties({"id","senha"})
public class Usuario {

    private String id;
    @JsonProperty("nome")
    private String nome;
    @JsonProperty("email")
    private String email;
    @JsonProperty("cpf")
    private String cpf;
    @JsonProperty("senha")
    private String senha;

    @JsonProperty("id_face")
    private String id_face;

    @JsonProperty("nome_face")
    private String nome_face;

    @JsonProperty("url_face")
    private String url_face;

    @JsonProperty("token_gcm")
    private String token_gcm;

    @JsonProperty("device_id")
    private String device_id;

    @JsonProperty("transacoes_enviadas")
    private List<Transacao> transacoes_enviadas;

    @JsonProperty("transacoes_recebidas")
    private List<Transacao> transacoes_recebidas;

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference usuarioReferencia = databaseReference.child("usuarios");

    public void salvar(){
        usuarioReferencia.child(getId()).setValue(this);
    }

    public List<Transacao> getTransacoes_recebidas() {
        return transacoes_recebidas;
    }
    public void setTransacoes_recebidas(List<Transacao> transacoes_recebidas) {
        this.transacoes_recebidas = transacoes_recebidas;
    }

    public List<Transacao> getTransacoes_enviadas() {
        return transacoes_enviadas;
    }
    public void setTransacoes_enviadas(List<Transacao> transacoes_enviadas) {
        this.transacoes_enviadas = transacoes_enviadas;
    }

    public void setDevice_id(String device_id){this.device_id = device_id;}
    public void setToken_gcm(String token_gcm){this.token_gcm = token_gcm;}
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
        this.id_face = idFace;
    }
    public void setNomeFace(String nome_face){
        this.nome_face = nome_face;
    }
    public void setUrlImgFace(String url_img){
        this.url_face = url_img;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDevice_id(){return this.device_id;}
    public String getToken_gcm(){return this.token_gcm;}
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
        return this.id_face;
    }
    public String getNomeFace(){
        return this.nome_face;
    }
    public String getUrlImgFace(){
        return this.url_face;
    }

}
