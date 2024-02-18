package br.com.arlei.handsonspringbatch;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class Pessoa {

    private String nome;

    private String endereco;

    private String bairro;

    private String cidade;

    private String estado;

    private LocalDateTime create_date_time;

    public LocalDateTime getCreate_date_time() {
        return create_date_time;
    }

    public void setCreate_date_time(LocalDateTime create_date_time) {
        this.create_date_time = create_date_time;
    }

    public Pessoa() {
    }

    public Pessoa(String nome, String endereco, String bairro, String cidade, String estado) {
        this.nome = nome;
        this.endereco = endereco;
        this.bairro = bairro;
        this.cidade = cidade;
        this.estado = estado;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
