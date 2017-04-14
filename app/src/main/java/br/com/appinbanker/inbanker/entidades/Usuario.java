package br.com.appinbanker.inbanker.entidades;

import android.util.Log;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

/**
 * Created by Jonatas on 18/10/2016.
 */
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
    private String id_face;
    @JsonProperty("url_face")
    private String url_face;
    @JsonProperty("token_gcm")
    private String token_gcm;
    @JsonProperty("device_id")
    private String device_id;

    @JsonProperty("idade")
    private String idade;
    @JsonProperty("sexo")
    private String sexo;

    @JsonProperty("adicionado_em")
    private String adicionado_em;

    @JsonProperty("cartaoPagamento")
    private List<CartaoPagamento> cartao_pagamento;

    @JsonProperty("endereco")
    private Endereco endereco;

    @JsonProperty("transacoes_enviadas")
    private List<Transacao> transacoes_enviadas;

    @JsonProperty("transacoes_recebidas")
    private List<Transacao> transacoes_recebidas;

    @JsonProperty("notificacao_contrato")
    private List<NotificacaoContrato> notificacao_contrato;

    public List<NotificacaoContrato> getNotificacaoContrato() {
        return notificacao_contrato;
    }

    public void setNotificacaoContrato(List<NotificacaoContrato> notificacao_contrato) {
        this.notificacao_contrato = notificacao_contrato;
    }

    public List<CartaoPagamento> getCartaoPagamento() {
        return cartao_pagamento;
    }

    public void setCartaoPagamento(List<CartaoPagamento> cartaoPagamento) {
        this.cartao_pagamento = cartaoPagamento;
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

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference usuarioReferencia = databaseReference.child("usuarios");


    public void salvar(){
        Log.i("Cadastro","cpf = "+getCpf());
        if(getCpf().length() > 3) {
            usuarioReferencia.child(getCpf()).setValue(this);
            Log.i("Cadastro","Pelo cpf");
        }
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

    public void setEndereco(Endereco end){
        this.endereco = end;
    }
    public void setSexo(String sexo) {
        this.sexo = sexo;
    }
    public void setIdade(String idade) {
        this.idade = idade;
    }
    public void setId_face(String id_face) {
        this.id_face = id_face;
    }


    public void setUrl_face(String url_ace) {
        this.url_face = url_ace;
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

    public Endereco getEndereco(){
        return this.endereco;
    }

    public String getSexo() {
        return sexo;
    }
    public String getIdade() {
        return idade;
    }

    public String getId_face() {
        return id_face;
    }

    public String getUrl_face() {
        return url_face;
    }

    public String getAdicionado_em() {
        return adicionado_em;
    }

    public void setAdicionado_em(String adicionado_em) {
        this.adicionado_em = adicionado_em;
    }

}
