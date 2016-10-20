package br.com.appinbanker.inbanker.entidades;

/**
 * Created by Jonatas on 18/10/2016.
 */

public class Usuario {

    private String nome;
    private String email;
    private String cpf;
    private String senha;

    private String id_face;
    private String email_face;
    private String nome_face;
    private String url_img_face;

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
    public void setIdFace(String id_face){
        this.id_face = id_face;
    }
    public void setEmailFace(String email_face){
        this.email_face = email_face;
    }
    public void setNomeFace(String nome_face){
        this.nome_face = nome_face;
    }
    public void setUrlImgFace(String url_img_face){
        this.url_img_face = url_img_face;
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
        return this.id_face;
    }
    public String getEmailFace(){return this.email_face;}
    public String getNomeFace(){
        return this.nome_face;
    }
    public String getUrlImgFace(){
        return this.url_img_face;
    }

}
