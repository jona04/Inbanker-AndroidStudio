package br.com.appinbanker.inbanker.entidades;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Random;

/**
 * Created by jonatassilva on 23/10/16.
 */

public class Transacao {

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

    public String getId_trans() {
        return id_trans;
    }
    public void setId_trans(String id) {
        id_trans = (id == null) ? String.valueOf(randomInteger(1000000,9999999)) : id;
    }
    public String getStatus_transacao() {
        return status_transacao;
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
