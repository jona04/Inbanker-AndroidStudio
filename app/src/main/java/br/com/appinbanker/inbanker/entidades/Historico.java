package br.com.appinbanker.inbanker.entidades;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by jonatasilva on 03/03/17.
 */

public class Historico {

    @JsonProperty("status_transacao")
    private String status_transacao;
    @JsonProperty("data")
    private String data;

    public String getStatus_transacao() {
        return status_transacao;
    }

    public void setStatus_transacao(String status_transacao) {
        this.status_transacao = status_transacao;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
