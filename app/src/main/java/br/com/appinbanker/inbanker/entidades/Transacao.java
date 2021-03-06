package br.com.appinbanker.inbanker.entidades;

import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;
import java.util.Random;

/**
 * Created by jonatassilva on 23/10/16.
 */

public class Transacao implements Serializable {

    //status da transacao
    public static final int AGUARDANDO_RESPOSTA = 0;
    public static final int PEDIDO_ACEITO = 1;
    public static final int PEDIDO_RECUSADO = 2;
    public static final int CONFIRMADO_RECEBIMENTO = 3;
    public static final int QUITACAO_SOLICITADA = 4;
    public static final int RESP_QUITACAO_SOLICITADA_RECUSADA = 5;
    public static final int RESP_QUITACAO_SOLICITADA_CONFIRMADA = 6;
    public static final int ENVIO_CANCELADO_ANTES_RESPOSTA = 7;
    public static final int ENVIO_CANCELADO_ANTES_RECEBIMENTO = 8;


    @JsonProperty("id_trans")
    private String id_trans;
    @JsonProperty("cpf_usu1")
    private String cpf_usu1;
    @JsonProperty("cpf_usu2")
    private String cpf_usu2;
    @JsonProperty("nome_usu2")
    private String nome_usu2;
    @JsonProperty("url_img_usu2")
    private String url_img_usu2;
    @JsonProperty("valor")
    private String valor;
    @JsonProperty("valor_servico")
    private String valor_servico;
    @JsonProperty("valor_juros_mensal")
    private String valor_juros_mensal;
    @JsonProperty("valor_juros_mora")
    private String valor_juros_mora;
    @JsonProperty("valor_multa")
    private String valor_multa;
    @JsonProperty("vencimento")
    private String vencimento;
    @JsonProperty("data_pedido")
    private String data_pedido;
    @JsonProperty("nome_usu1")
    private String nome_usu1;
    @JsonProperty("url_img_usu1")
    private String url_img_usu1;
    @JsonProperty("status_transacao")
    private String status_transacao;
    @JsonProperty("data_recusada")
    private String data_recusada;
    @JsonProperty("data_pagamento")
    private String data_pagamento;
    @JsonProperty("id_recibo")
    private String id_recibo;
    @JsonProperty("id_contrato")
    private String id_contrato;

    public String getId_recibo() {
        return id_recibo;
    }

    public void setId_recibo(String id_recibo) {
        this.id_recibo = id_recibo;
    }

    public String getId_contrato() {
        return id_contrato;
    }

    public void setId_contrato(String id_contrato) {
        this.id_contrato = id_contrato;
    }

    @JsonProperty("historico")
    private List<Historico> historico;

    @JsonProperty("pagamento")
    private RetornoPagamento pagamento;


    public RetornoPagamento getPagamento() {
        return pagamento;
    }

    public void setPagamento(RetornoPagamento pagamento) {
        this.pagamento = pagamento;
    }

    public List<Historico> getHistorico() {
        return historico;
    }

    public void setHistorico(List<Historico> historico) {
        this.historico = historico;
    }

    public String getId_trans() {
        return id_trans;
    }
    public void setId_trans(String id) {
        id_trans = (id == null) ? String.valueOf(randomInteger(1000000,9999999)) : id;
    }

    public String getValor_multa() {
        return valor_multa;
    }

    public void setValor_multa(String valor_multa) {
        this.valor_multa = valor_multa;
    }

    public String getValor_juros_mora() {
        return valor_juros_mora;
    }

    public void setValor_juros_mora(String valor_juros_mora) {
        this.valor_juros_mora = valor_juros_mora;
    }

    public String getValor_juros_mensal() {
        return valor_juros_mensal;
    }

    public void setValor_juros_mensal(String valor_juros_mensal) {
        this.valor_juros_mensal = valor_juros_mensal;
    }

    public String getValor_servico() {
        return valor_servico;
    }

    public void setValor_servico(String valor_servico) {
        this.valor_servico = valor_servico;
    }

    public void setData_pagamento(String data_pagamento) {
        this.data_pagamento = data_pagamento;
    }
    public void setData_recusada(String data_recusada) {
        this.data_recusada = data_recusada;
    }
    public void setNome_usu1(String nome_usu1) {
        this.nome_usu1 = nome_usu1;
    }
    public void setUrl_img_usu1(String url_img_usu1) {
        this.url_img_usu1 = url_img_usu1;
    }
    public void setUsu1(String usu1){
        this.cpf_usu1 = usu1;
    }
    public void setUsu2(String usu2){
        this.cpf_usu2 = usu2;
    }
    public void setValor(String valor){
        this.valor = valor;
    }
    public void setVencimento(String vencimento){
        this.vencimento = vencimento;
    }
    public void setDataPedido(String data_pedido){
        this.data_pedido = data_pedido;
    }
    public void setNome_usu2(String nome_usu2){
        this.nome_usu2 = nome_usu2;
    }
    public void setUrl_img_usu2(String url_img_usu2){
        this.url_img_usu2 = url_img_usu2;
    }

    public void setStatus_transacao(String status_transacao) {
        this.status_transacao = status_transacao;
    }
    public String getData_pagamento() {
        return data_pagamento;
    }
    public String getStatus_transacao() {
        return status_transacao;
    }
    public String getData_recusada() {
        return data_recusada;
    }
    public String getUrl_img_usu1() {
        return url_img_usu1;
    }
    public String getNome_usu1() {
        return nome_usu1;
    }
    public String getUrl_img_usu2(){
        return this.url_img_usu2;
    }
    public String getNome_usu2(){
        return this.nome_usu2;
    }
    public String getUsu1(){return this.cpf_usu1;}
    public String getUsu2(){return this.cpf_usu2;}
    public String getValor(){
        return this.valor;
    }
    public String getVencimento(){
        return this.vencimento;
    }
    public String getDataPedido(){
        return this.data_pedido;
    }

    public int randomInteger(int min, int max) {

        Random rand = new Random();

        // nextInt excludes the top value so we have to add 1 to include the top value
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }

}
