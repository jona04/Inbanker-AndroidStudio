package br.com.appinbanker.inbanker.entidades;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by jonatasilva on 15/03/17.
 */

public class NotificacaoContrato {

    @JsonProperty("id_trans")
    private String id_trans;
    @JsonProperty("cpf_user")
    private String cpf_user;
    @JsonProperty("date")
    private String date;
    @JsonProperty("titulo")
    private String titulo;
    @JsonProperty("mensagem")
    private String mensagem;
    @JsonProperty("dias")
    private String dias;

    public String getDias() {
        return dias;
    }

    public void setDias(String dias) {
        this.dias = dias;
    }

    public String getId_trans() {
        return id_trans;
    }
    public void setId_trans(String id_trans) {
        this.id_trans = id_trans;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getMensagem() {
        return mensagem;
    }
    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
    public String getTitulo() {
        return titulo;
    }
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    public String getCpf_user() {
        return cpf_user;
    }
    public void setCpf_user(String cpf_user) {
        this.cpf_user = cpf_user;
    }
}
